package com.pukza.plibrary.component.widget;

/**
 * Created by choihwaseop on 2016. 2. 2..
 */
import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

public class NetworkImageView extends ImageView {
	protected String mUrl;
	protected int mDefaultImageId;

	protected boolean isFadein;
	protected String mCatchDir;
	protected long mCatchSize;
	
	public NetworkImageView(Context context) {
		super(context);
		initalize();
		// TODO Auto-generated constructor stub
	}
	
	public NetworkImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initalize();
		// TODO Auto-generated constructor stub
	}
	
	public NetworkImageView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initalize();
		// TODO Auto-generated constructor stub
	}
	
	private void initalize()
	{
		
	}
	
	
	public void setImageUrl(String url)
	{
		mUrl = url;
	}

    public String getImageUrl(){return mUrl;};
}
