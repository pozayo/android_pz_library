package com.pukza.plibrary.component.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

import java.util.ArrayList;

public class PozaViewPager extends ViewPager {
	private boolean canSwiping;
	private ArrayList<OnPageChangeListener> mPageChangeListeners;
	
	public PozaViewPager(Context context) {
		super(context);
		init();
	}
	
	public PozaViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	private void init() {
		canSwiping = true;
		mPageChangeListeners = new ArrayList<>();
		setOnPageChangeListener(mPageChangeListener);
	}
	
	public void addOnPageChangeListener(OnPageChangeListener listener) {
		mPageChangeListeners.add(listener);
	}
	
	public void setSwipingEnabled(boolean canSwiping) {
		this.canSwiping = canSwiping;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent arg0) {
		if (!canSwiping) {
			return false;
		}
		return super.onTouchEvent(arg0);
	}
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if (!canSwiping) {
			return false;
		}
		return super.onInterceptTouchEvent(ev);
	}
	
	private OnPageChangeListener mPageChangeListener = new OnPageChangeListener() {
		
		@Override
		public void onPageSelected(int arg0) {
			for (OnPageChangeListener listener : mPageChangeListeners) {
				listener.onPageSelected(arg0);
			}
		}
		
		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			for (OnPageChangeListener listener : mPageChangeListeners) {
				listener.onPageScrolled(arg0, arg1, arg2);
			}
		}
		
		@Override
		public void onPageScrollStateChanged(int arg0) {
			for (OnPageChangeListener listener : mPageChangeListeners) {
				listener.onPageScrollStateChanged(arg0);
			}
		}
	};
}
