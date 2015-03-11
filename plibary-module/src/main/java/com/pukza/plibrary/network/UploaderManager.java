package com.pukza.plibrary.network;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import com.pukza.plibrary.network.UploaderService.OnUploadStateCallback;
import com.pukza.plibrary.util.LocalLog;

/**
 * 업로드를 관리하는 매니저 클래스 
 * 업로드 서비스 시작, 업로드 상황 인터페이스 등을 포함한다.
 * @author LOEN
 *
 */
public class UploaderManager {
	private UploaderService mUploaderService;
	private boolean isSerivce;
	private OnUploadStateCallback mUploadStateCallback;
	private Context mContext;
	private Handler.Callback mCallback;
		
	private static UploaderManager mInstance;
	private Queue<UploaderParam> mServiceBeforeParams;

	/***
	 * 하나의 업로더 및 서비스 관리를 위해 싱글톤으로 작성
	 * @return
	 */
	public static UploaderManager getInstance(Context context)
	{
		if(mInstance == null)
		{
			mInstance = new UploaderManager(context);
		}
		
		return mInstance;
	}
	
	private UploaderManager(Context context)
	{
		mContext = context;
		mServiceBeforeParams = new LinkedBlockingQueue<UploaderParam>();

	}
	
	private ServiceConnection mUploadConnection = new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			LocalLog.d("cvrt" ,"[UploaderManager] onServiceDisconnected");
			mUploaderService.stopSelf();
			mUploaderService = null;	
			isSerivce = false;
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			LocalLog.d("cvrt", "[UploaderManager] onServiceConnected");
			if(mServiceBeforeParams == null) return;
			mUploaderService = ((UploaderService.UploaderServiceBinder)service).getService();
			mUploaderService.setCallback(mServiceCallback);
			UploaderParam param;
			while((param = mServiceBeforeParams.poll() )!= null)
			{
				mUploaderService.upload(param);
			}
			isSerivce = true;

		}
		
		
	};
	
	public void setOnUploadStateCallback(OnUploadStateCallback uploadCallback)
	{
		mUploadStateCallback = uploadCallback;
	}
	
	
	public void onUpload(UploaderParam uploadParams)
	{ 
        Intent uploadService = new Intent(mContext, UploaderService.class);

		if(mUploaderService == null)
		{
			mServiceBeforeParams.add(uploadParams);
			mContext.bindService(uploadService, mUploadConnection, Context.BIND_AUTO_CREATE);
		}else
		{
            mUploaderService.upload(uploadParams);
		}
		
	}
	
	public void abortUpload(){
		if( !isSerivce || mUploaderService == null) return;
		
		mUploaderService.abort();
		onComplete();
	}
	
	private OnUploadStateCallback mServiceCallback = new OnUploadStateCallback() {
		
		@Override
		public void onUploadState(final UploaderParam option, final int state, final String args) {
			if(state == STATE_ERROR)
			{
				abortUpload();
			}
			
			
			if(mContext instanceof Activity)
			{
				((Activity)mContext).runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						if(mUploadStateCallback != null) mUploadStateCallback.onUploadState(option,state, args);
					}
				});
			}
			
		}
	};
	
	public void onComplete()
	{
		mUploaderService.stopSelf();
	}

	public static class UploaderParam{
        public String url;
        public Map<String, String> header;
        public Map<String, String> param;
        public String[] filePaths;
        public String fileParamKey;
        
		public UploaderParam(String url,Map<String, String> header, Map<String, String> param, String[] filePaths, String fileParamKey) {
			super();
			this.url = url;
			this.param = param;
			this.filePaths = filePaths;
			this.fileParamKey = fileParamKey;
			this.header = header;
		}
        
        
	}

}
