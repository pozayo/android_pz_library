/**
 * 
 */
package com.pukza.plibrary.support.app;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * @desc 
 *  심플 리사이클러 뷰 어댑터 
 *  getLayoutResId - 재사용할 뷰의 리소스 레이아웃 
 *  setDataForView - 재사용된 뷰의 데이터를 그려준다. 
 * @author hwaseopchoi
 * @date 2015. 7. 7.
 *
 */
public abstract class PozaRecyclerViewSimpleAdapter<T extends PozaRecyclerViewItem> extends PozaRecyclerViewAdapter {
	public PozaRecyclerViewSimpleAdapter(RecyclerView recyclerView) {
		this(recyclerView, null);
	}
	
	public PozaRecyclerViewSimpleAdapter(RecyclerView recyclerView, ArrayList<T> itemList) {
		super(recyclerView, itemList);
		
		setItemFethcer((Class) PozaRecyclerViewSimpleFetcher.class);
	}
	

	public int getViewType()
	{
		return 0;
	}
	
	public void initCreateLayout(View v) {
		
	}
	
	abstract public int getLayoutResId();
	abstract public void setDataForView(PozaRecyclerViewFetcher.PojaViewHolder holder, T data, int position);

	public class PozaRecyclerViewSimpleFetcher extends PozaRecyclerViewFetcher<T> {
		
		public PozaRecyclerViewSimpleFetcher(T data) {
			super(data);
			// TODO Auto-generated constructor stub
		}

		/** 
		 * @desc 
		 */
		@Override
		public int getViewType() {
			// TODO Auto-generated method stub
			return PozaRecyclerViewSimpleAdapter.this.getViewType();
		}

		/** 
		 * @desc 
		 */
		@Override
		public View createLayout(Context context, ViewGroup viewGroup) {
			View view = LayoutInflater.from(context).inflate(getLayoutResId(), viewGroup, false);
			initCreateLayout(view);
			return view;
		}

		/** 
		 * @desc 
		 */
		@Override
		public void setDataForView(PozaRecyclerViewFetcher.PojaViewHolder holder, T data, int position) {
			PozaRecyclerViewSimpleAdapter.this.setDataForView(holder,data,position);
			
		}
		
	}


}
