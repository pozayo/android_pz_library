package com.pukza.plibrary.component;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pukza.plibrary.network.BaseRequest.OnRequestCallback;
import com.pukza.plibrary.network.model.BaseInterface;

import static com.pukza.plibrary.util.LocalLog.LOGI;
import static com.pukza.plibrary.util.LocalLog.makeLogTag;

/**
 * Created by choihwaseop on 2016. 2. 2..
 */
public class PozaFragment extends Fragment {
	protected static String TAG;
	private boolean isCreated;
    public interface FragmentListener {
        public void onFragmentViewCreated(PozaFragment fragment);
        public void onFragmentAttached(PozaFragment fragment);
        public void onFragmentDetached(PozaFragment fragment);
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setExceptionHander();
    }

    /** 
     * @desc 
     * @see Fragment#onResume()
     */
    @Override
    public void onResume() {
    	super.onResume();
        LOGI(TAG, "onResume");
        setExceptionHander();
    }
    
	public boolean isCreated()
	{
		return isCreated;
	}

    @Override
    public void onStart() {
        super.onStart();
        setExceptionHander();
    }

    private void setExceptionHander()
    {
        Activity parent = getActivity();
        if(parent != null && parent instanceof PozaActivity)
        {
        	PozaActivity.TrakerUncaughtExceptionHandler handler = ((PozaActivity)parent).getTrakerExceptionHandler();
            if(handler != null)
            {
                handler.setTrakerClass(((Object) this).getClass());
            }
        }
    }
    
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
		TAG = makeLogTag(this.getClass());

        if (getActivity() instanceof FragmentListener) {
            ((FragmentListener) getActivity()).onFragmentViewCreated(this);
        }
        
        if((getActivity() instanceof PozaActivity) == false)
        {
        	try {
				throw new Exception("PojaFragment, you must set the PojaActivity is a parent Activity.");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }		
        isCreated = true;
    }
    
    /** 
     * @desc 
     * @see Fragment#onCreateView(LayoutInflater, ViewGroup, Bundle)
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	// TODO Auto-generated method stub
		LOGI(TAG, "onCreateView");

    	return super.onCreateView(inflater, container, savedInstanceState);
    }
 

    /** 
     * @desc 
     * @see Fragment#onPause()
     */
    @Override
    public void onPause() {
		LOGI(TAG, "onPause");
    	super.onPause();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (getActivity() instanceof FragmentListener) {
            ((FragmentListener) getActivity()).onFragmentAttached(this);
        }
        
        mActivity = activity;

    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (getActivity() instanceof FragmentListener) {
            ((FragmentListener) getActivity()).onFragmentDetached(this);
        }
    }
    
    /** 
     * @desc 
     * @see Fragment#onDestroy()
     */
    @Override
    public void onDestroy() {
    	isCreated = false;
    	super.onDestroy();
    	
    }

    
	public void requestApi(BaseInterface api, OnRequestCallback cb)
	{
		try {
            getPojaActivity().requestApi(api, cb);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void requestApi(BaseInterface api, OnRequestCallback cb, boolean isDecoding)
	{
		try {
            getPojaActivity().requestApi(api, cb, isDecoding);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

    public boolean onBackPressed()
    {
        return false;
    }

	private Activity mActivity;

	
	public PozaActivity getPojaActivity()
	{
		Activity activity = getActivity();
		if(activity == null)
		{
			activity  = mActivity;
		}
        if (activity instanceof PozaActivity) {
        	return (PozaActivity)activity;
        }
        
        return null;
       
   	}
    
}
