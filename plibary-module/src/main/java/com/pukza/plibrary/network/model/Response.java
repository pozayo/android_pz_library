/**
 * 
 */
package com.pukza.plibrary.network.model;

import java.net.HttpCookie;
import java.util.List;

/**
 * @desc 
 * 
 *
 * @author hwaseopchoi
 * @date 2015. 5. 12.
 *
 */
public class Response{

	  private final String mUrl;
	  private final int mStatus;
	  private final String mBody;
	  private List<HttpCookie> mCookie;
	  private String mMessage;

	  public Response(String url, int status, String message, String body) {
//	    if (url == null) {
//	      throw new IllegalArgumentException("url == null");
//	    }
//	    if (status < 200) {
//	      throw new IllegalArgumentException("Invalid status code: " + status);
//	    }
//	    if (message == null) {
//	      throw new IllegalArgumentException("reason == null");
//	    }

	    mUrl = url;
	    mStatus = status;
	    mMessage = message;
	    mBody = body;
	  }

	  public String getUrl() {
	    return mUrl;
	  }

	  /**
	   * 
	   * @desc 
	   * resultCode
	   */
	  public int getStatus() {
	    return mStatus;
	  }

	  /**
	   * 
	   * @desc 
	   * resultMessage
	   */
	  public String getMessage() {
	    return mMessage;
	  }
	  
	  public void setMessage(String msg) {
		  mMessage = msg;
	  }

	  /** Response body. May be {@code null}. */
	  public String getBody() {
	    return mBody;
	  }

	public List<HttpCookie> getCookies() {
		return mCookie;
	}

	public void setCookies(List<HttpCookie> cookie) {
		this.mCookie = cookie;
	}
	
	  
//	private T mBody;
//	
//	public void setBody(ResponseBody body)
//	{
//		mBody = (T)body;
//	}
//	
//	public <T> T getBody()
//	{
//		return (T) mBody;
//	}
//	
//	public int getResultCode()
//	{
//		if(mBody == null) return -1;
//		
//		return mBody.resultCode;
//	}
//	
//	public String getResultMessage()
//	{
//		if(mBody == null) return null;
//		
//		return mBody.resultString;
//
//	}
}
