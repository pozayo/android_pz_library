package com.pukza.plibrary.support.app;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import com.pukza.plibrary.support.app.PozaRecyclerViewFetcher.PojaViewHolder;
import com.pukza.plibrary.support.app.PozaViewPagerSimpleAdapter.PozaViewPagerSimpleFetcher;

/**
 * @desc
 * 
 *
 * @author hwaseopchoi
 * @date 2015. 7. 7.
 *
 */
public class PozaViewPagerAdapter extends PagerAdapter {
	private ViewPager viewPager;
	private ArrayList<? extends PozaRecyclerViewItem> mItemList;
	private Context mContext;

	private Class<? extends PozaRecyclerViewFetcher<? extends PozaRecyclerViewItem>> mSetFetcherClass;
	
	public PozaViewPagerAdapter(ViewPager viewPager, ArrayList<? extends PozaRecyclerViewItem> itemList) {
		this.viewPager = viewPager;
		mItemList = itemList;
	}

	
	public void setItemFethcer(Class<? extends PozaRecyclerViewFetcher<? extends PozaRecyclerViewItem>> fetcherClass) {
		mSetFetcherClass = fetcherClass;
	}


	/**
	 * @desc
	 * @see PagerAdapter#getCount()
	 */
	@Override
	public int getCount() {
		if (mItemList == null)
			return 0;
		return mItemList.size();
	}
	
	public void setItemList(ArrayList<? extends PozaRecyclerViewItem> itemList) {
		mItemList = itemList;
	}

	/**
	 * @desc
	 * @see PagerAdapter#instantiateItem(ViewGroup,
	 *      int)
	 */
	@Override
	public Object instantiateItem(ViewGroup container,final int position) {
		PozaRecyclerViewItem item = mItemList.get(position);
		PozaRecyclerViewFetcher fetcher = item.getViewFetcher();
		if(mSetFetcherClass != null)
		{
			try {
				if(mSetFetcherClass.equals(PozaViewPagerSimpleFetcher.class))
				{
					Class[] constructorArgs = new Class[] { PozaViewPagerSimpleAdapter.class, PozaRecyclerViewItem.class };
					fetcher = (PozaRecyclerViewFetcher)mSetFetcherClass.getConstructor(constructorArgs).newInstance(this, item);
				}else
				{
					Class[] constructorArgs = new Class[] { item.getClass() };
					fetcher = (PozaRecyclerViewFetcher)mSetFetcherClass.getConstructor(constructorArgs).newInstance(item);
				}
			} catch (Exception e) {
				e.printStackTrace();

			}
		}
		

		final View v = fetcher.createLayout(container.getContext(), container);
		
		PojaAdapterViewHolder holder = new PojaAdapterViewHolder(v, position);
		final PojaViewHolder loenholder = new PojaViewHolder(holder);

		fetcher.setDataForView(loenholder, item, position);
		
		container.addView(v, 0);
		
		PozaRecyclerViewAdapter.setOnAttachedView(fetcher, loenholder, position);

		return v;
	}

	/**
	 * @desc
	 * @see PagerAdapter#destroyItem(ViewGroup,
	 *      int, Object)
	 */
	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		// TODO Auto-generated method stub
		container.removeView((View) object);
	}

	/**
	 * @desc
	 * @see PagerAdapter#isViewFromObject(View,
	 *      Object)
	 */
	@Override
	public boolean isViewFromObject(View pager, Object obj) {
		return pager == obj;
	}
	
	
	public static class PojaAdapterViewHolder extends RecyclerView.ViewHolder {
		private View mItemView;
		public int type;
		private int position;

		public PojaAdapterViewHolder(View itemView, int position) {
			super(itemView);
			mItemView = itemView;
			// TODO Auto-generated constructor stub
		}
		
		public int getItemPostion(){
			return position;
		}
		

		public <T extends View> T get(int id) {
			SparseArray<View> viewHolder = null;
			if (mItemView.getTag(android.R.id.list) == null) {
				viewHolder = new SparseArray<View>();
				mItemView.setTag(android.R.id.list, viewHolder);
			}else if(mItemView.getTag(android.R.id.list) instanceof SparseArray)
			{
				viewHolder = (SparseArray<View>) mItemView.getTag(android.R.id.list);

			}
			
			if(viewHolder == null) return null;
			
			View childView = viewHolder.get(id);
			if (childView == null) {
				childView = mItemView.findViewById(id);
				viewHolder.put(id, childView);
			}
			return (T) childView;
		}
		

	}

}
