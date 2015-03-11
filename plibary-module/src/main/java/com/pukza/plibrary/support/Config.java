/**
 * 
 */
package com.pukza.plibrary.support;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.facebook.drawee.backends.pipeline.Fresco;

import java.net.HttpURLConnection;

import com.pukza.plibrary.util.LocalLog;


/**
 * @desc 
 *  아지톡 라이브러리의 모든 모듈을 설정. 
 *
 * @author hwaseopchoi
 * @date 2015. 5. 12.
 *
 */
public class Config {
	public static boolean isDebugable;
	/************************************************
	 * 
	 * 라이브러리 기본 설정 
	 * 
	 ************************************************/
	public static void setConfig(boolean isDebug)
	{
		isDebugable = isDebug;
		LocalLog.SetLogConfiguration(isDebug ? LocalLog.LOG_LEVEL_DEBUG : LocalLog.LOG_LEVEL_RELEASE, LocalLog.LOG_DISPLAY_CONSOLE, null);
	}
	
	
	/************************************************
	 * 
	 * 네트워크 설정 
	 * 
	 ************************************************/
	public static final int REQUEST_TYPE_VOLLEY = 0;
	public static final int REQUEST_TYPE_AQUERY = 1;
//	public static final int REQUEST_TYPE_ASYNCTASK_HTTP= 2;
//	public static final int REQUEST_TYPE_MELON = 3;
//
//	
	public static int DEFAULT_REQUEST_TYPE = REQUEST_TYPE_VOLLEY;
	
	public static final int NETWORK_TIMEOUT_MS = 10 * 1000;
	
	public static final String RESPONSE_RESULT_CODE_KEY = "resultCode";		// ex {resultCode:0, resultMessage:"suc", data:{}}
	public static final String RESPONSE_RESULT_MESSAGE_KEY = "resultMessage";		// ex {resultCode:0, resultMessage:"suc", data:{}}
	public static final String RESPONSE_ERROR_MESSAGE_KEY = "errorMesg"; // ex {resultCode:-1, errorMesg:"suc", data:{}}
	public static final String RESPONSE_BODY_DATA_KEY = "data";		// ex {resultCode:0, resultMessage:"suc", data:{}}
	
	public static void setNetworkConfig()
	{
		HttpURLConnection.setFollowRedirects(true);
	}
	
	
	/*************************************************
	 * 
	 * 이미지 로더 설정 
	 * 
	 *************************************************/
	public static Drawable IMAGE_DEFAULT_PLACEHOLDER_ERROR_IMAGE_DRAWABLE = null;
	public static int IMAGE_DEFAULT_PLACEHOLDER_ERROR_IMAGE = 0;

	
	public static void setImageConfig(Context context, int defaultPlaceHolderRes)
	{

        Fresco.initialize(context);
        
//		GlideImageLoader.setGlideOption(context);
		IMAGE_DEFAULT_PLACEHOLDER_ERROR_IMAGE = defaultPlaceHolderRes;
	}
	
	

	
}
