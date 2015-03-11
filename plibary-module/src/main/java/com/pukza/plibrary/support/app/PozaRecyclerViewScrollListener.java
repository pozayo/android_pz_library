package com.pukza.plibrary.support.app;

import android.content.Context;
import android.graphics.Point;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.view.Display;
import android.view.WindowManager;

public abstract class PozaRecyclerViewScrollListener extends RecyclerView.OnScrollListener {
    public static final int VIEW_VISIBLE_STATE_NONE = 0;
    public static final int VIEW_VISIBLE_STATE_COMPLETE_VISIBLE = 1;

    private static int WindowWidth = -1;
    private static int WindowHeight = -1;

	private int previousTotal = 0;
	private boolean loading = true;
	private int visibleThreshold = 5;
	int firstVisibleItem, visibleItemCount, totalItemCount;

    private int mLastCompletePosition = -1;
	private int current_page = 1;

	private LayoutManager mLinearLayoutManager;

	public PozaRecyclerViewScrollListener(LayoutManager linearLayoutManager) {
		this.mLinearLayoutManager = linearLayoutManager;
	}
	
	private long mIdleTime;
    private boolean IsOnVisibleState;
	/** 
	 * @desc 
	 * @see android.support.v7.widget.RecyclerView.OnScrollListener#onScrollStateChanged(android.support.v7.widget.RecyclerView, int)
	 */
	@Override
	public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
		super.onScrollStateChanged(recyclerView, newState);
	
		if(newState == RecyclerView.SCROLL_STATE_IDLE)
		{
			mIdleTime = System.currentTimeMillis();
		}

	}

    /**
     * 패쳐의 뷰가 보이는 상태를 가져올지 여부
     */
    public void SetIsEnableOnVisibleState(boolean isOnVisibleState)
    {
        IsOnVisibleState = isOnVisibleState;
    }
	public boolean isEnableOnClick() {
		return System.currentTimeMillis() - mIdleTime > 500;
	}
	
	public void resetLoadMore() {
		loading = true;
		previousTotal = 0;//visibleThreshold;
		totalItemCount = 0;
		current_page = 1;
	}

	@Override
	public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
		super.onScrolled(recyclerView, dx, dy);

		if(recyclerView == null || mLinearLayoutManager == null)
			return;

        onScrolledY(dy);

        int lastCompletePosition = -1;

		visibleItemCount = recyclerView.getChildCount();
		totalItemCount = mLinearLayoutManager.getItemCount();
		if (mLinearLayoutManager instanceof LinearLayoutManager) {
			firstVisibleItem = ((LinearLayoutManager)mLinearLayoutManager).findFirstVisibleItemPosition();

            lastCompletePosition = ((LinearLayoutManager)mLinearLayoutManager).findLastCompletelyVisibleItemPosition();
		} else if(mLinearLayoutManager instanceof GridLayoutManager) {
			firstVisibleItem = ((GridLayoutManager)mLinearLayoutManager).findFirstCompletelyVisibleItemPosition();
            lastCompletePosition = ((GridLayoutManager)mLinearLayoutManager).findLastCompletelyVisibleItemPosition();
        }


        /**
         * OnViewState를 위한 처리
         */
        if(IsOnVisibleState) {

            if (lastCompletePosition != -1 && mLastCompletePosition != lastCompletePosition) {
                RecyclerView.ViewHolder holder = recyclerView.findViewHolderForAdapterPosition(lastCompletePosition);
                int[] loc = new int[2];
                holder.itemView.getLocationOnScreen(loc);
                int windowWidth = getWindowWidth(holder.itemView.getContext());
                int windowHeight = getWindowHeight(holder.itemView.getContext());
                int viewWidth = holder.itemView.getWidth();
                int viewHeight = holder.itemView.getHeight();

                if (viewWidth > windowWidth) viewWidth = windowWidth;
                if (viewHeight > windowHeight) viewHeight = windowHeight;

//                LocalLog.d("cvrt", "position : " + lastCompletePosition + " - " +loc[0] +":" +loc[1] + " , "+ viewWidth + ":" +viewHeight);

                if (loc[0] + viewWidth <= windowWidth && loc[1] + viewHeight <= windowHeight) {
                    if (mLastCompletePosition != -1)
                        onVisibleState(mLastCompletePosition, VIEW_VISIBLE_STATE_NONE);

                    mLastCompletePosition = lastCompletePosition;
                    onVisibleState(mLastCompletePosition, VIEW_VISIBLE_STATE_COMPLETE_VISIBLE);
                }

            }
        }


        /**
         * OnMoreLoad를 위한 처리
         */
		if (loading) {
			if (totalItemCount > previousTotal) {
				loading = false;
				previousTotal = totalItemCount;
			}
		}
		if (!loading && (visibleItemCount + firstVisibleItem) >= totalItemCount) {
			current_page++;
			onLoadMore(current_page);
			loading = true;
		}
	}


    public static interface OnMoreLoad {
        public void onMoreLoad(int position, PozaRecyclerViewItem lastItem);
    }

	public abstract void onLoadMore(int current_page);
    public abstract void onVisibleState(int position, int state);


    public void onScrolledY(int dy)
    {

    }


    public static int getWindowHeight(Context context) {

        if(WindowHeight > 0) return WindowHeight;

        Display display = getWindowManager(context).getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        WindowHeight = size.y;

        return WindowHeight;
    }

    public static int getWindowWidth(Context context) {
        if(WindowWidth > 0) return WindowWidth;

        Display display = getWindowManager(context).getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        WindowWidth = size.x;
        return WindowWidth;
    }

    public static WindowManager getWindowManager(Context context) {
        return (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    }

}
