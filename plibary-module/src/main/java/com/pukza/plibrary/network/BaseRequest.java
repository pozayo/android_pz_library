/**
 * 
 */
package com.pukza.plibrary.network;

import android.content.Context;

import java.net.HttpCookie;
import java.util.List;
import java.util.Map;

import com.pukza.plibrary.network.model.BaseInterface;
import com.pukza.plibrary.network.model.Response;
import com.pukza.plibrary.network.model.ResponseBody;

/**
 * @desc 
 * 각종 네트워크 요청 라이브러리의 기본 클래스 
 * 각 리퀘스트는 요청,취소 등의 매니저 기능을 포함하고 각 네트워크 라이브러리에 있는 기능들을 구현한다. 
 * @author hwaseopchoi
 *
 */
public abstract class BaseRequest {
	protected String mRequestUrl; // 리퀘스트에 대한 콜백이 비동기임으로, 리퀘스트당 하나의 API만 처리하도록 구성해야 한다.
	protected Map<String, String> mRequestHeader;
	protected List<HttpCookie> mSetCookie; // 응답의 대한 쿠키 값
	protected OnRequestCallback mCallback;
	protected Context mContext;
	protected BaseInterface api;
	protected int mMethod = RequestMethod.GET;
	
	public interface OnRequestCallback<T>{
		public void onResult(Response response, T responseBody);
		public void onError(NetworkError error);
	}
	
	public BaseRequest(BaseInterface api){
		this.api = api;
	}
	
	
	public BaseRequest(Class<? extends BaseInterface> apiClass){
		try {
			this.api = (BaseInterface) apiClass.getConstructors()[0].newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	public BaseRequest(final String url, final Class jsonTargetCls, final Map<String, Object> params){
		this(url, jsonTargetCls, params, null);
	}
	
	public BaseRequest(final String url, final Class jsonTargetCls, final Map<String, Object> params, final Map<String, String> header){
		api = new BaseInterface() {
			
			@Override
			public Map<String, Object> makeParam() {
				return params;
			}
			
			@Override
			public String getUrl() {
				return url;
			}
			
			@Override
			public Map<String, String> makeHeader() {
				return header;
			}

			@Override
			public Class<? extends ResponseBody> getResponseBody() {
				return jsonTargetCls;
			}

			@Override
			public int getMethod() {
				return mMethod;
			}
		};
	}
	
	public BaseInterface getApi() {
		return api;
	}
	
	public String getRequestUrl() {
		return mRequestUrl;
	}

	public Map<String, String> getRequestHeader() {
		return mRequestHeader;
	}

	public List<HttpCookie> getSetCookie() {
		return mSetCookie;
	}

	public OnRequestCallback getCallback() {
		return mCallback;
	}

	public void setCallback(OnRequestCallback mCallback) {
		this.mCallback = mCallback;
	}


	public void setMethod(int method) {
		if (method < RequestMethod.GET || method > RequestMethod.POST) {
			return;
		}
		mMethod = method;
	}

	/**
	 * 
	 * @desc 
	 * 요청 리퀘스트 함수 
	 * 컨텍스트는 요청, 취소 기준임으로 생성자에 받지 않는다. 
	 *
	 */
	public void request(Context context, OnRequestCallback cb) {
		if (api == null)
			return;

		mRequestHeader = api.getHeader();
		mCallback = cb;
		mRequestUrl =  api.getUrl();
		mContext = context;
		request(context, cb, api.getMethod(), api.getUrl(), api.getParam(), api.getHeader());
	}
	abstract protected void request(Context context, OnRequestCallback cb, int method, final String Url, final Map<String, Object> params ,final Map<String, String> header);

	abstract public void cancel(Context context);
	
	public class RequestMethod {
		  public static final int GET = 0;
		  public static final int POST = 1;
	}
}