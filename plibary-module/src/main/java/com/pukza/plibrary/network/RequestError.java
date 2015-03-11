/**
 * 
 */
package com.pukza.plibrary.network;

/**
 * @desc 
 * 
 *
 * @author hwaseopchoi
 * @date 2015. 5. 13.
 *
 */
public class RequestError  extends Exception {

	private static final long serialVersionUID = -3499450422769197788L;
	
    public final static int SERVER_REQUEST_CODE_SECCESE = 0;
    public final static int SERVER_REQUEST_CODE_FAIL = 1;
    public final static int SERVER_REQUEST_CODE_DELETE_CONTENTS = 409;
    public final static int SERVER_REQUEST_CODE_CONTENTS_NOT_FOUND = 411;
    public final static int SERVER_REQUEST_CODE_BIG_DATA_ERROR = 413;
    public final static int SERVER_REQUEST_CODE_EXPIRE_TOKEN = 414;
    public final static int SERVER_REQUEST_CODE_HASHTAG_ERROR = 415;
    
    private int resultCode;
    private String resultMessage;
    
    public RequestError(int code, String message)
    {
    	resultCode = code;
    	resultMessage = message;
    }
    
    public RequestError(Exception e)
    {
    	super(e);
    }

	public int getResultCode() {
		return resultCode;
	}

	public void setResultCode(int resultCode) {
		this.resultCode = resultCode;
	}

	public String getResultMessage() {
		return resultMessage;
	}

	public void setResultMessage(String resultMessage) {
		this.resultMessage = resultMessage;
	}
    
    
    

}
