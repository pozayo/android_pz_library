/**
 * 
 */
package com.pukza.plibrary.support.app;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

/**
 * @desc
 *
 *
 * @author hwaseopchoi
 *
 */
public abstract class PozaRecyclerViewFetcher<T> {
	private boolean mIsAttachEanable = true;
	private T mData;
	public PozaRecyclerViewFetcher(T data)
	{
		this.mData = data;
	}
	/**
	 * @desc 
	 * 리사이클러 뷰에 표시될 뷰의 타입 
	 * ex) 타입1, 타입2, 타입1, 타입1 , 타입2 ....
	 */
	public abstract int getViewType();
	
	public int createViewType(String typeString)
	{
		int sum = 0;
		
		for(char c: typeString.toCharArray())
			sum += Character.getNumericValue(c);
		
		return sum;
	}
	
	/**
	 * @desc 해당 함수를 오버라이드 해서 True를 반환할 경우 해당 패쳐는 재활용 하지 않는다. 
	 *
	 */
	public boolean isNotRecycler()
	{
		return false;
	}
	
	public final void setEnableAttachView(boolean isEnable)
	{
		mIsAttachEanable = isEnable;
	}
	public final boolean isEnableAttachView()
	{
		return mIsAttachEanable;
	}
	
	/**
	 * @desc 
	 * 재사용될 뷰를 정의 한다. 
	 * 데이터는 없는 빈 레이아웃을 생성한다. 
	 * 
	 */
	public abstract View createLayout(Context context, ViewGroup viewGroup);
	public abstract void setDataForView(PojaViewHolder holder, T data, int position);
	public boolean dispatchOnBackPressed() {
		return false;
	}
	
	public SpanType getSpanType(){
		return SpanType.FullSpan;
	}


    /**
	 * 
	 * @desc 
	 * 
	 *
	 * @author hwaseopchoi
	 * @date 2015. 6. 16.
	 *
	 */
	public static class PojaViewHolder{
        //2016.01.14 test
//		public OnPreDrawListener DrawListener;
		public Context context;
		public View itemView;
		public int type;
		PozaRecyclerViewAdapter.PojaRecyclerViewHolder mRecyclerHolder;
		PozaViewPagerAdapter.PojaAdapterViewHolder mViewPagerHolder;
		
		public PojaViewHolder(PozaRecyclerViewAdapter.PojaRecyclerViewHolder holder)
		{
			mRecyclerHolder = holder;
			itemView = holder.itemView;
			type = holder.type;
			context = itemView.getContext();

		}
		
		public PojaViewHolder(PozaViewPagerAdapter.PojaAdapterViewHolder holder)
		{
			mViewPagerHolder = holder;
			itemView = holder.itemView;
			type = holder.type;
			context = itemView.getContext();	
			
		}

		
		public <T extends View> T get(int id) {
			T view = null;
			if(mRecyclerHolder != null)
			{
				
				view = mRecyclerHolder.get(id);
			}else if(mViewPagerHolder != null)
			{
				view = mViewPagerHolder.get(id);
			}
//			else
//			{
//				view = mRecyclerPagerHolder.get(id);
//			}
			return view;
		}
		
		public int getPosition()
		{
			if(mRecyclerHolder != null)
			{
				return mRecyclerHolder.getLayoutPosition();
			}else if(mViewPagerHolder != null)
			{
				return mViewPagerHolder.getItemPostion();
			}
			else
			{
//				return mRecyclerPagerHolder.getPosition();
				return -1;
			}
		}
		
		public long getItemId()
		{
			if(mRecyclerHolder != null)
			{
				return mRecyclerHolder.getItemId();
			}else if(mViewPagerHolder != null)
			{
				return mViewPagerHolder.getItemId();
			}
			else
			{
//				return mRecyclerPagerHolder.getItemId();
				return -1;
			}
		}
		
		public int getItemViewType()
		{
			if(mRecyclerHolder != null)
			{
				return mRecyclerHolder.getItemViewType();
			}else if(mViewPagerHolder != null)
			{
				return mViewPagerHolder.getItemViewType();
			}
			else
			{
//				return mRecyclerPagerHolder.getItemViewType();
				return -1;
			}
			
		}
		private PozaRecyclerViewAdapter.OnItemClickListener mItemClickListener;
		
		public void setOnItemClickListener(PozaRecyclerViewAdapter.OnItemClickListener listener)
		{
			mItemClickListener = listener;
		}
		
		public PozaRecyclerViewAdapter.OnItemClickListener getOnItemClickListener()
		{
			return mItemClickListener;
		}
		
	}
	
	
	private PozaRecyclerViewAdapter.OnItemClickListener mFetcherItemClickListener;
	public void setOnFetcherItemClickListener(PozaRecyclerViewAdapter.OnItemClickListener listener)
	{
		mFetcherItemClickListener = listener;
	}
	public PozaRecyclerViewAdapter.OnItemClickListener getOnFetcherItemClickListener()
	{
		return mFetcherItemClickListener;
	}
	
	/**
	 * 
	 * @desc 
	 *
	 */
	public T getBindData()
	{
		return mData;
	}
	
	
	/**
	 * 
	 * @desc 
	 * 
	 *
	 * @author hwaseopchoi
	 * @date 2015. 6. 16.
	 *
	 */
	public static class SpanType{
		public static SpanType Default = new SpanType(0);
		public static SpanType FullSpan = new SpanType(1);
		
		public int span;
		public SpanType(int span)
		{
			this.span = span;
		}
	}


    public void onAttachedView(PojaViewHolder holder, T data, int position) {

    }

    public void onFetcherSelected(int position) {

    }

    /**
     * 뷰가 완전히 보이면 true, 아닐경우 false
     * 해당 메소드를 오버라이드하면 호출
     */
    public void onViewVisibleState( T data, int position, boolean visibleComplete)
    {

    }


    public void onCreate(Context context) {

    }

    public void onPause(Context context) {

    }

    public void onDestroy(Context context) {
    }

    public void onResume(Context context) {

    }


}
