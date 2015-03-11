package com.pukza.plibrary.component;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;

import com.pukza.plibrary.component.PozaFragment.FragmentListener;
import com.pukza.plibrary.network.BaseRequest.OnRequestCallback;
import com.pukza.plibrary.network.model.BaseInterface;
import com.pukza.plibrary.network.toolbox.PozaRequest;

import static com.pukza.plibrary.util.LocalLog.LOGE;
import static com.pukza.plibrary.util.LocalLog.LOGI;
import static com.pukza.plibrary.util.LocalLog.makeLogTag;

/**
 * Created by choihwaseop on 2016. 2. 2..
 */
public class PozaActivity extends AppCompatActivity implements FragmentListener {
	protected static String TAG = "";
	private boolean isCreated;
	
    private Thread.UncaughtExceptionHandler mUncaughtExceptionHandler;
    private static TrakerUncaughtExceptionHandler mExceptionHandler;
	private Fragment mFragment;

    private ArrayList<OnActivityState> onStateList;


    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TAG = makeLogTag(this.getClass());
		LOGI(TAG, "onCreate");
		isCreated = true;		//위치 조정 필요, 온크레이트 된 이후 시점 
        onStateList = new ArrayList<>();
		
		mUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();

        if(mExceptionHandler == null)
        {
            mExceptionHandler = new TrakerUncaughtExceptionHandler(((Object)this).getClass());
            Thread.setDefaultUncaughtExceptionHandler(mExceptionHandler);
        }

        for(OnActivityState onState : onStateList)
            if(onState != null)
                onState.onCreate(this);
	}
	
    public class TrakerUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
        private Class<?> mCls;

        public void setTrakerClass(Class<?> cls) {
            mCls = cls;
        }

        public TrakerUncaughtExceptionHandler(Class<?> cls) {
            mCls = cls;
        }

        @Override
        public void uncaughtException(Thread thread, Throwable ex) {
        	/**
        	 * 에러 관련 처리 
        	 */
        	LOGE(TAG, mCls.getSimpleName() + " - " + ex.getMessage());
            mUncaughtExceptionHandler.uncaughtException(thread, ex);
        }

    }
    
    public TrakerUncaughtExceptionHandler getTrakerExceptionHandler()
    {
        return mExceptionHandler;
    }

    public void addOnActivityState(OnActivityState state)
    {
        if(onStateList == null)
            onStateList = new ArrayList<>();
        onStateList.add(state);
    }
    public interface OnActivityState{
        public void onCreate(Context context);
        public void onResume(Context context);
        public void onPause(Context context);
        public void onDestroy(Context context);
    }

    @Override
    public void onBackPressed() {
        if(mFragment != null && mFragment instanceof PozaFragment)
        {
            if(((PozaFragment)mFragment).onBackPressed())
                return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mExceptionHandler != null)
            mExceptionHandler.setTrakerClass(((Object)this).getClass());
    }

	
	@Override
	protected void onResume() {
		super.onResume();
		LOGI(TAG, "onResume");
		
        if(mExceptionHandler != null)
            mExceptionHandler.setTrakerClass(((Object) this).getClass());

        for(OnActivityState onState : onStateList)
            if (onState != null)
                onState.onResume(this);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		LOGI(TAG, "onPause");
        for(OnActivityState onState : onStateList)
            if(onState != null)
                onState.onPause(this);
	}

	public boolean isCreated()
	{
		return isCreated;
	}
	
	@Override
	protected void onDestroy() {

        for(OnActivityState onState : onStateList)
            if(onState != null)
                onState.onDestroy(this);

		isCreated = false;
		super.onDestroy();
		LOGI(TAG, "onDestroy");
	}
	
	@Override
	public void onFragmentViewCreated(PozaFragment fragment) {
		mFragment = fragment;
	}
	
	@Override
	public void onFragmentAttached(PozaFragment fragment) {
		mFragment = fragment;
	}
	
	@Override
	public void onFragmentDetached(PozaFragment fragment) {
		mFragment = null;
	}
	
    
    public Fragment getCurrentFragment()
    {
    	return mFragment;
    }
	
	public void requestApi(BaseInterface api, OnRequestCallback cb)
	{
		if(!isCreated)
		{
			try {
				throw new Exception("PojaRequest must then created activity.");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		PozaRequest request = new PozaRequest(api);
		request.request(this, cb);
	}

	public void requestApi(BaseInterface api, OnRequestCallback cb, boolean isDecoding)
	{
		if(!isCreated)
		{
			try {
				throw new Exception("PojaRequest must then created activity.");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		PozaRequest request = new PozaRequest(api);
		request.request(this, cb, isDecoding);
	}

	public void requestApi(Class<? extends BaseInterface> cls, OnRequestCallback cb)
	{
		if(!isCreated)
		{
			try {
				throw new Exception("PojaRequest must then created activity.");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		PozaRequest request = new PozaRequest(cls);
		request.request(this, cb);
	}
	
	
	
	
}
