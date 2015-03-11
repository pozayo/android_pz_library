/**
 * 
 */
package com.pukza.plibrary.support.app;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * @desc
 * 
 *
 * @author hwaseopchoi
 * @date 2015. 7. 7.
 *
 */
public abstract class PozaViewPagerSimpleAdapter<T extends PozaRecyclerViewItem> extends PozaViewPagerAdapter {
	public PozaViewPagerSimpleAdapter(ViewPager viewPager) {
		this(viewPager, null);
	}

	public PozaViewPagerSimpleAdapter(ViewPager viewPager, ArrayList<? extends PozaRecyclerViewItem> itemList) {
		super(viewPager, itemList);
		setItemFethcer((Class) PozaViewPagerSimpleFetcher.class);

	}


	public int getViewType() {
		return 0;
	}

	abstract public int getLayoutResId();

	public void initCreateLayout(View v) {

	}

	abstract public void setDataForView(PozaRecyclerViewFetcher.PojaViewHolder holder, T data, int position);

	public class PozaViewPagerSimpleFetcher extends PozaRecyclerViewFetcher<T> {

		public PozaViewPagerSimpleFetcher(T data) {
			super(data);
		}

		@Override
		public int getViewType() {
			return PozaViewPagerSimpleAdapter.this.getViewType();
		}

		@Override
		public View createLayout(Context context, ViewGroup viewGroup) {
			View view = LayoutInflater.from(context).inflate(getLayoutResId(), viewGroup, false);
			initCreateLayout(view);
			return view;
		}

		@Override
		public void setDataForView(PozaRecyclerViewFetcher.PojaViewHolder holder, T data, int position) {
			PozaViewPagerSimpleAdapter.this.setDataForView(holder, data, position);
		}
	}
}