/**
 * 
 */
package com.pukza.plibrary.network.model;

import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Map;

/**
 * @desc 
 * 
 *
 * @author hwaseopchoi
 * @date 2015. 5. 12.
 *
 */
public abstract class BaseInterface {
	public static final String COOKIE_KEY = "Cookie";
    public static final String SET_COOKIE_KEY = "Set-Cookie";

	private SharedPreferences mPreferences;

	  
	protected HashMap<String, Object> mParams;
	protected HashMap<String, String> mHeader;
	protected HashMap<String, String> mCookie;
	
	abstract class Builder{
		
	}
	
	abstract public Map<String, String> makeHeader();
	abstract public Map<String, Object> makeParam();
	abstract public String getUrl();
	abstract public int getMethod();
	abstract public Class<? extends ResponseBody> getResponseBody();
	
	public void addParam(String key, Object value){
		if(mParams == null) mParams = new HashMap<String, Object>();
		
		mParams.put(key, value);
	}
	
	public Map<String, Object> getParam()
	{
		if(mParams == null) mParams = new HashMap<String, Object>();
		
		Map<String, Object> makeParam = makeParam();
		if(makeParam != null)
			mParams.putAll(makeParam);
		
		return mParams;
	}
	
	public void addHeader(String key, String value)
	{
		if(mHeader == null) mHeader = new HashMap<String, String>();
		
		mHeader.put(key, value);
	}
	
	public Map<String,String> getHeader()
	{
		if(mHeader == null) mHeader = new HashMap<String, String>();
		
		Map<String, String> makeHeader = makeHeader();
		if(makeHeader != null)
			mHeader.putAll(makeHeader);

		
		return mHeader;
	}
	
	public Map<String,String> getCookie()
	{
		if(mCookie == null) mCookie = new HashMap<String, String>();

		return mCookie;
		
	}
	
	public void addCookie(String key, String value)
	{
		if(mCookie == null) mCookie = new HashMap<String, String>();
		
		mCookie.put(key, value);
	}
	
}
