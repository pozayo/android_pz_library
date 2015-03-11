package com.pukza.plibrary.network;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.pukza.plibrary.network.UploaderManager.UploaderParam;
import com.pukza.plibrary.support.Config;

/**
 * 업로드 수행 서비스
 * 업로드 시에만 서비스가 활성 되도록 한다.
 * @author LOEN
 *
 */
public class UploaderService extends Service {
	private final IBinder mBinder = new UploaderServiceBinder();
	private OnUploadStateCallback mCallback;
    private boolean isUploading;
    private ThreadPoolExecutor mThreadPool;
    
	public class UploaderServiceBinder extends Binder {
		UploaderService getService()
		{
			return UploaderService.this;
		}
	}
	
	public interface OnUploadStateCallback{
		public static final int STATE_PREPARE	= 0;
		public static final int STATE_START 	= 1;
		public static final int STATE_PROGRESS	= 2;
		public static final int STATE_ERROR		= 3;
		public static final int STATE_COMPLETE	= 4;
		public void onUploadState(UploaderParam uploadParam, int state, String args);
	}
	
	public void setCallback(OnUploadStateCallback callback)
	{
		mCallback = callback;
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return mBinder;
	}

    @Override
    public void onCreate() {
        super.onCreate();
        isUploading = false;
        mThreadPool= new ThreadPoolExecutor( 5, 10, Config.NETWORK_TIMEOUT_MS, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>()  );
    }
    public boolean isUploading()
    {
        return isUploading;
    }

    public void upload(final UploaderManager.UploaderParam option)
	{

		if(mCallback != null) 
		{
            mCallback.onUploadState(option, OnUploadStateCallback.STATE_PREPARE, "");
        }
		
//        MultiPartRequestHttp requestRunnable  = new MultiPartRequestHttp(option, new ErrorListener() {
//
//
//			@Override
//			public void onErrorResponse(VolleyError error) {
//				if(mCallback != null)
//				{
//                    error.printStackTrace();
//                    mCallback.onUploadState(option,OnUploadStateCallback.STATE_ERROR, error.getMessage());
//                }
//
//			}
//		}, new RequestListener() {
//
//			@Override
//			public void onResponse(UploaderParam param, String response) {
//                isUploading = false;
//
//				if(mCallback != null)
//				{
//                    mCallback.onUploadState(param,OnUploadStateCallback.STATE_COMPLETE, response);
//				}
//
//			}
//		}
//		, null, new MultipartProgressListener() {
//
//			@Override
//			public void transferred(long transfered, int progress) {
//				if(mCallback != null) {
////                    mCallback.onUploadState(UploadStateCallback.STATE_PROGRESS, new Gson().toJson(option.data));
//                    mCallback.onUploadState(option,OnUploadStateCallback.STATE_PROGRESS, progress+"");
//                }
//
//			}
//		});
//
//        for(String key: option.param.keySet())
//        {
//        	requestRunnable.addStringUpload(key, option.param.get(key));
//
//        }


//        ArrayList<File> fileList = new ArrayList<File>();
//        long fileLength = 0L;
//        for(int i = 0 ; i < option.filePaths.length ; i++)
//		{
//			File file = new File(option.filePaths[i]);
//            fileLength += file.length();
//            if(file != null && file.exists())
//                fileList.add(file);
//		}
//
//        if(fileList.size() > 0)
//        	requestRunnable.addFileUpladArray(option.fileParamKey, fileList,fileLength);
//
//        if(mThreadPool != null)
//        	mThreadPool.execute(requestRunnable);
//        else{
//            mThreadPool= new ThreadPoolExecutor( 5, 10, Config.NETWORK_TIMEOUT_MS, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>()  );
//        	mThreadPool.execute(requestRunnable);
//        }

        isUploading = true;
		
		if(mCallback != null) 
			mCallback.onUploadState(option, OnUploadStateCallback.STATE_START, "");
	}
	
	public void abort()
	{
		if(mThreadPool != null)
		{
			mThreadPool.shutdown();
		}
		
		mThreadPool = null;
		
		stopSelf();
	}
	

}
