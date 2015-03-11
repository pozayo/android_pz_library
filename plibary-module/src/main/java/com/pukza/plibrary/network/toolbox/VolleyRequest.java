/**
 * 
 */
package com.pukza.plibrary.network.toolbox;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.apache.commons.io.Charsets;

import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookieStore;
import java.net.URLEncoder;
import java.util.Map;

import com.pukza.plibrary.network.BaseRequest;
import com.pukza.plibrary.network.HttpsTrustManager;
import com.pukza.plibrary.network.NetworkError;
import com.pukza.plibrary.network.model.BaseInterface;
import com.pukza.plibrary.support.Config;
import com.pukza.plibrary.util.PozaNetworkUtil;

import static com.pukza.plibrary.util.LocalLog.makeSupportLogTag;

/**
 * Created by choihwaseop on 2016. 2. 2..
 */
public class VolleyRequest extends BaseRequest {
	private static final String TAG = makeSupportLogTag(VolleyRequest.class);
	private String mCharset = null;

	protected static RequestQueue mRequestQueue;
	private CookieManager mCookieManager;
	
	public VolleyRequest(Class<? extends BaseInterface> apiClass) {
		super(apiClass);
	}

	public VolleyRequest(BaseInterface api) {
		super(api);
	}

	public VolleyRequest(String url, Class jsonTargetCls, Map<String, Object> params, Map<String, String> header) {
		super(url, jsonTargetCls, params, header);
	}

	@Override
	protected void request(Context context, BaseRequest.OnRequestCallback cb, final int method, String Url, final Map<String, Object> params, Map<String, String> header) {
		mRequestUrl = Url;

		HttpsTrustManager.allowAllSSL();
		
		/*쿠키*/
        StringBuilder builder = new StringBuilder();
		for(String key : api.getCookie().keySet())
		{
	        builder.append(key);
	        builder.append("=");
	        builder.append(api.getCookie().get(key));
	        builder.append(";");
		}
		if(builder.length() > 0)
			header.put(BaseInterface.COOKIE_KEY, builder.toString());
		
		mRequestHeader = header;
		mSetCookie = null;
		// 리퀘스트 큐 가져오기
		synchronized (VolleyRequest.this) {
			if (mRequestQueue == null) {
				mRequestQueue = Volley.newRequestQueue(context);
			}
		}

		
		if(mCookieManager == null)
		{
			mCookieManager = new CookieManager();
			CookieHandler.setDefault(mCookieManager);
		}
		
		

		// 네트워크 접속 상태 체크
		int networkStatus = PozaNetworkUtil.checkNetworkStatus(context);
		if (networkStatus != PozaNetworkUtil.NETWORK_STATUS_CONNECTED) {

			if (mCallback != null) {
				NetworkError error = new NetworkError(NetworkError.ERROR_NETWORK_CONNECT, networkStatus, null);
				error.setRequestInterface(api);
				mCallback.onError(error);
			}

			return;
		}
		

		setCharset("utf-8");

		if (params != null) {
			if (method == RequestMethod.GET) {
				StringBuilder paramStr = new StringBuilder("?");
				for (String key : params.keySet()) {
					if (params.get(key) != null) {
						Object value = params.get(key);
						if(value instanceof String)
						{
							try {
								String strValue = URLEncoder.encode((String) value, Charsets.UTF_8.name());
								paramStr.append(key + "=" + strValue);
							} catch (UnsupportedEncodingException e) {
								e.printStackTrace();
								paramStr.append(key + "=" + params.get(key));
							}

						}else
						{
							paramStr.append(key + "=" + params.get(key));
						}
						paramStr.append("&");
					}
				}
				paramStr.deleteCharAt(paramStr.length() - 1);

				String getParams = paramStr.toString();

				mRequestUrl = mRequestUrl + getParams;
			}

		}


		StringRequest requestQueue = new StringRequest(method, mRequestUrl, onSuccessListener(), onErrorListener()) {

			@Override
			public Map<String, String> getHeaders() throws AuthFailureError {
				if (mRequestHeader == null)
					return super.getHeaders();
	
				return mRequestHeader;
			}

			@Override
			public byte[] getBody() throws AuthFailureError {
				StringBuilder paramStr = new StringBuilder();
				for(Map.Entry<String,Object> param : params.entrySet())
				{
					if (param.getValue() != null) {
						try {
							Object value = param.getValue();
							if(value instanceof String)
							{
								String encodeValue = URLEncoder.encode((String) value, Charsets.UTF_8.name());
								paramStr.append(param.getKey() + "=" + encodeValue);

							}else
							{
								paramStr.append(param.getKey() + "=" + value);

							}
						} catch (UnsupportedEncodingException e) {
							paramStr.append(param.getKey() + "=" + param.getValue());
						}

						paramStr.append("&");
					}
				}
				
				return paramStr.toString().getBytes();
			}

			@TargetApi(Build.VERSION_CODES.GINGERBREAD)
			@Override
			protected Response<String> parseNetworkResponse(NetworkResponse response) {
//				mSetCookie = HttpCookie.parse(response.headers.get(BaseInterface.SET_COOKIE_KEY));
				CookieStore store = mCookieManager.getCookieStore();
				mSetCookie = store.getCookies();
				mCookieManager.getCookieStore().removeAll();
				
				return super.parseNetworkResponse(response);
			}
			
			
		};

		requestQueue.setRetryPolicy(new DefaultRetryPolicy(Config.NETWORK_TIMEOUT_MS, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		mRequestQueue.getCache().clear();
		mRequestQueue.add(requestQueue);


	}

	protected Response.Listener<String> onSuccessListener() {
		return new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				com.pukza.plibrary.network.model.Response newResponse = new com.pukza.plibrary.network.model.Response(mRequestUrl, 200, "success", response);
				newResponse.setCookies(mSetCookie);

				if (mCallback != null) {
					if (api.getResponseBody() != null) {
						try {
							mCallback.onResult(newResponse, new Gson().fromJson(response, api.getResponseBody()));
						} catch (Exception e) {
							mCallback.onResult(newResponse, null);
						}
					} else {
						mCallback.onResult(newResponse, null);
					}
				}
			}
		};
	}

	protected Response.ErrorListener onErrorListener() {
		return new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {

				if (mCallback != null) {
					NetworkError nerror = new NetworkError(error);
					nerror.setRequestInterface(api);
					mCallback.onError(nerror);
				}

			}
		};
	}

	@Override
	public void cancel(Context context) {
		mRequestQueue.cancelAll(context);
	}

	public void setCharset(String charset) {
		mCharset = charset;
	}

}
