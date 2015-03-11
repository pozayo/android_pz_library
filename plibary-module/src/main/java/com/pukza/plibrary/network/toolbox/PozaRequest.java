/**
 * 
 */
package com.pukza.plibrary.network.toolbox;

import android.content.Context;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import com.pukza.plibrary.component.PozaActivity;
import com.pukza.plibrary.network.BaseRequest;
import com.pukza.plibrary.network.NetworkError;
import com.pukza.plibrary.network.RequestError;
import com.pukza.plibrary.network.model.BaseInterface;
import com.pukza.plibrary.support.Config;
import com.pukza.plibrary.util.LocalLog;
import com.pukza.plibrary.util.PozaNetworkUtil;

import static com.pukza.plibrary.util.LocalLog.LOGD;
import static com.pukza.plibrary.util.LocalLog.LOGE;
import static com.pukza.plibrary.util.LocalLog.LOGI;
import static com.pukza.plibrary.util.LocalLog.makeSupportLogTag;

/**
 * Created by choihwaseop on 2016. 2. 2..
 */
public  class PozaRequest extends VolleyRequest {
	private static final String ERROR_RELEASE_MESSAGE = "서버 오류가 발생 하였습니다.";

	private boolean mIsDecoding = false;
	
	private static final String TAG = makeSupportLogTag(PozaRequest.class);
	
	
	public PozaRequest(Class<? extends BaseInterface> apiClass) {
		super(apiClass);
	}

	public PozaRequest(BaseInterface api) {
		super(api);
		// TODO Auto-generated constructor stub
	}

	public PozaRequest(String url, Class jsonTargetCls, Map<String, Object> params, Map<String, String> header) {
		super(url, jsonTargetCls, params, header);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void request(Context context, BaseRequest.OnRequestCallback cb) {
		int networkEnable = PozaNetworkUtil.checkNetworkStatus(context);
		if(networkEnable != PozaNetworkUtil.NETWORK_STATUS_NONE && networkEnable != PozaNetworkUtil.NETWORK_STATUS_CONNECTED)
		{
			mCallback = cb;
			NetworkError error = new NetworkError(NetworkError.ERROR_NETWORK_CONNECT, networkEnable, "데이터망 연결 확인");
			error.setRequestContext(context);
			sendError(error);
			return;
		}
		
		
		super.request(context, cb);

		StringBuilder logBuilder = new StringBuilder();
		logBuilder.append( "====================Poja Request====================")
		.append( "\n==url : " + mRequestUrl)
		.append( "\n==header : " + api.getHeader())
		.append( "\n==param : " + api.getParam())
		.append( "\n======================================================");
		LOGD(TAG, logBuilder.toString());

	}
	
	public void request(Context context, BaseRequest.OnRequestCallback cb, boolean isDecoding){
		mIsDecoding = isDecoding;
		request(context, cb);
	}


	/** VolleyRequest Override Method **/
	protected Response.Listener<String> onSuccessListener() {
		return new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				parseDataForResult(response);
			}
		};
	}

	/** VolleyRequest Override Method **/
	protected Response.ErrorListener onErrorListener() {
		return new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {

				NetworkError nerror = new NetworkError(error);
				sendError(nerror);

			}
		};
	}
	
	/**
	 * AqueryRequest를 상속 받았을 경우 처리 
	 */
//	/** AqueryRequest Override Method **/
//	public synchronized void onResult(final String url, final String jsonString, final AjaxStatus status) {
//		super.onResult(url, jsonString, status);
//		if (status.getCode() != 200) {
//			sendError(new NetworkError(NetworkError.ERROR_NETWORK, status.getCode(), status.getMessage()));
//			return;
//		}
//		
//		parseDataForResult(jsonString);
//	}

	private void parseDataForResult(String response)
	{
//		LOGI(LocalLog.LOG_LEVEL_DEBUG , TAG , "Response : " + response);
//        LocalLog.d("cvrt", "Response : " +response);
		Gson gson = new Gson();

		int resultCode = -1;
		String data = "";
		String resultMessage = "";
		String errorMesg = "";
		try {
			JSONObject responseObject = new JSONObject(response);

			errorMesg = responseObject.has(Config.RESPONSE_ERROR_MESSAGE_KEY) ? responseObject.getString(Config.RESPONSE_ERROR_MESSAGE_KEY) : "";
			resultCode = responseObject.getInt(Config.RESPONSE_RESULT_CODE_KEY);
			resultMessage = responseObject.getString(Config.RESPONSE_RESULT_MESSAGE_KEY);
			data = responseObject.getString(Config.RESPONSE_BODY_DATA_KEY);
		} catch (JSONException e) {
			if(resultCode == -1 && resultMessage.length() < 1)
			{
//				NetworkError error = new NetworkError(new RequestError(resultCode, "아지톡 API의 응답구조가 아닙니다."));
//				sendError(error);
			}
			data = response;
			
			// return;
		}

		if (mRequestUrl == null)
			mRequestUrl = api.getUrl();

		com.pukza.plibrary.network.model.Response newResponse = new com.pukza.plibrary.network.model.Response(mRequestUrl, resultCode, resultMessage, response.toString());
		newResponse.setCookies(mSetCookie);

		Object responseBody = null;
		String errorMessage = "";

			try {

				if (api.getResponseBody() != null)
					responseBody = gson.fromJson(data, api.getResponseBody());
				else
				{
					errorMessage = "API에 getResponseBody가 선언 되어있지 않습니다. ";
					if(!Config.isDebugable)
						errorMessage = ERROR_RELEASE_MESSAGE;
					NetworkError error = new NetworkError(new RequestError(0, errorMessage));
					sendError(error);
					return;
				}
			} catch (Exception e) {
				LocalLog.d("sung", data + " => data parsing exception " + e.getMessage());
				errorMessage = ", " + e.getMessage();
			}
//		}

		
		if(responseBody == null)
		{
			errorMessage = "Response Data를 파싱 할 수 없습니다." + errorMessage;
			if(!Config.isDebugable)
				errorMessage = ERROR_RELEASE_MESSAGE;
			NetworkError error = new NetworkError(new RequestError(0, errorMessage));


			sendError(error);
			return;
		}



        StringBuilder logBuilder = new StringBuilder();
		logBuilder.append( "====================Poja Response====================")
		.append( "\n==api url : " + api.getUrl())
		.append( "\n==result code : " + resultCode)
		.append( "\n==result msg : " + resultMessage)
		.append( "\n==response : " + response)
		.append( "\n======================================================");
		LOGI(TAG, logBuilder.toString());
		
//		if(newResponse.getStatus() != 0 && newResponse.getStatus() != RequestError.SERVER_REQUEST_CODE_BIG_DATA_ERROR)		//에러 & 빅데이터
//		{
//			if (!TextUtils.isEmpty(errorMesg)) {
//				newResponse.setMessage(errorMesg);
//			}
//			sendError(new NetworkError(NetworkError.ERROR_DATA_REQUEST, newResponse));
//			return;
//		}
		
		if (mCallback != null) {
			if(mContext instanceof PozaActivity)
			{
				if(((PozaActivity)mContext).isCreated() == false)
					return;
			}
			try{
				mCallback.onResult(newResponse, responseBody);
			}catch(Exception e)
			{
				e.printStackTrace();
				sendError(new NetworkError(NetworkError.ERROR_UNKNOWN, -1, "Callback Error Msg : " +e.getMessage()));
			}
		}
	}

	public void sendError(NetworkError error) {

		StringBuilder logBuilder = new StringBuilder();
		logBuilder.append("=================Poja Response Error=================")
		.append( "\n==api url : " + api.getUrl())
		.append( "\n==error type : " + error.getErrorType())
		.append( "\n==error code : " + error.getStatusCode())
		.append( "\n==error msg : " + error.getMessage())
		.append( "\n======================================================");
		LOGE(TAG, logBuilder.toString());
		if(mContext != null)
			error.setRequestContext(mContext);
		
		if (mCallback != null) {
			if(mContext instanceof PozaActivity)
			{
				if(((PozaActivity)mContext).isCreated() == false)
					return;
			}
			
			error.setRequestInterface(api);
			mCallback.onError(error);
		}
	}
}
