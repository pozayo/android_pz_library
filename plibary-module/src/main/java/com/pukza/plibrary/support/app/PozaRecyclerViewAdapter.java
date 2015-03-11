package com.pukza.plibrary.support.app;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.GridLayoutManager.SpanSizeLookup;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.SparseArray;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.HashMap;

import com.pukza.plibrary.component.PozaActivity;
import com.pukza.plibrary.support.app.PozaRecyclerViewFetcher.PojaViewHolder;
import com.pukza.plibrary.support.app.PozaRecyclerViewFetcher.SpanType;
import com.pukza.plibrary.support.app.PozaRecyclerViewSimpleAdapter.PozaRecyclerViewSimpleFetcher;
import com.pukza.plibrary.util.LocalLog;

import static com.pukza.plibrary.util.LocalLog.LOGI;
import static com.pukza.plibrary.util.LocalLog.makeLogTag;

/**
 * @desc
 * 
 *
 * @author hwaseopchoi
 *
 */
public class PozaRecyclerViewAdapter extends RecyclerView.Adapter<PozaRecyclerViewAdapter.PojaRecyclerViewHolder> {
	public static final int ID_PREFIX = 100000;
	public static String TAG = makeLogTag(PozaRecyclerViewAdapter.class);

	private ArrayList<? extends PozaRecyclerViewItem> mItemList;
	private SparseArray<PozaRecyclerViewFetcher> mViewTypeFetcher;
    private SparseArray<PozaRecyclerViewFetcher> mViewBindedFetcher;

    private Context mContext;
	private RecyclerView mRecyclerView;

	private PozaRecyclerViewItem mCurrentVisiableItem;

	private SparseArray<OnItemClickListener> mItemClickListeners;
	private SparseArray<OnItemClickListener> mItemFetcherClickListeners;

	private OnItemClickListener mItemClickListener;
	private PozaRecyclerViewScrollListener.OnMoreLoad mItemMoreLoad;
    private boolean IsOnVisibleState;

	private PozaRecyclerViewScrollListener mScrollerListener;
    private PozaActivity.OnActivityState mOnActivityState;

    public void release()
    {
        mItemList = null;
        mViewTypeFetcher = null;
        mViewBindedFetcher = null;
        mCurrentVisiableItem = null;
        mItemClickListener = null;
        mItemFetcherClickListeners = null;
        mRecyclerView.removeOnScrollListener(mScrollerListener);
    }
	/**
	 * 재활용 하지 않는 뷰는 첫번째 생성후 onBindViewHolder를 타지 않는다.
	 */
	private HashMap<Integer, Integer> mNotRecyclerCreatedList;
	/**
	 * 어댑터 기준의 패쳐 하나의 패쳐만 이용할때 해당 패쳐를 셋팅해주면 모든 아이템의 해당 패쳐로 설정된다.
	 */
	private Class<? extends PozaRecyclerViewFetcher<? extends PozaRecyclerViewItem>> mSetFetcherClass;

	public PozaRecyclerViewAdapter(RecyclerView recyclerView) {
		this(recyclerView, null);
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	@SuppressWarnings("deprecation")
	public PozaRecyclerViewAdapter(RecyclerView recyclerView, ArrayList<? extends PozaRecyclerViewItem> itemList) {
		mRecyclerView = recyclerView;
		mContext = mRecyclerView.getContext();
		mItemList = itemList;
		initalizeAdapter();
	}

	
	private void initalizeAdapter()
	{
        IsOnVisibleState = true;
		mViewTypeFetcher = new SparseArray<>();
        mViewBindedFetcher = new SparseArray<>();
		mItemClickListeners = new SparseArray<>();
		mItemFetcherClickListeners = new SparseArray<>();
		mNotRecyclerCreatedList = new HashMap<Integer, Integer>();
		mPreDrawMap = null;

		LayoutManager manager = mRecyclerView.getLayoutManager();
		if (manager instanceof GridLayoutManager) {
			final GridLayoutManager gridManager = ((GridLayoutManager) manager);
			gridManager.setSpanSizeLookup(new SpanSizeLookup() {

				@Override
				public int getSpanSize(int position) {
					if(mItemList.size() <= position) return gridManager.getSpanCount();
					
					PozaRecyclerViewItem item = mItemList.get(position);
					PozaRecyclerViewFetcher currentfetcher = item.getViewFetcher();
					if (currentfetcher == null)
						return 1;
					return currentfetcher.getSpanType() == SpanType.FullSpan ? gridManager.getSpanCount() : 1;
				}
			});
		}
		

        if(mScrollerListener == null) {
            mScrollerListener = new PozaRecyclerViewScrollListener(manager) {
                @Override
                public void onLoadMore(int current_page) {
                    int itemCount = getItemCount();
                    if (mItemMoreLoad != null && itemCount > 0) {
                        int index = (itemCount <= mItemList.size()) ? itemCount - 1 : mItemList.size() - 1;

                        if (index < 0 || index > mItemList.size() - 1) return;

                        PozaRecyclerViewItem lastItem = mItemList.get(index);
                        mItemMoreLoad.onMoreLoad(current_page, lastItem);
                    }
                }

                @Override
                public void onVisibleState(int position, int state) {
                    if(mItemList == null || (mItemList.size() <= position) || position < 0) return;

                    PozaRecyclerViewItem item = mItemList.get(position);
                    if (item != null) {
                        PozaRecyclerViewFetcher currentfetcher = item.getViewFetcher();
                        if (currentfetcher != null)
                            currentfetcher.onViewVisibleState(currentfetcher.getBindData(),position, state == PozaRecyclerViewScrollListener.VIEW_VISIBLE_STATE_COMPLETE_VISIBLE);
                    }
                }
            };
            mScrollerListener.SetIsEnableOnVisibleState(IsOnVisibleState);
            mRecyclerView.addOnScrollListener(mScrollerListener);
        }

//        mRecyclerView.removeOnScrollListener(mScrollerListener);

        if(mContext instanceof PozaActivity && mOnActivityState == null)
        {
            mOnActivityState = new PozaActivity.OnActivityState() {
                @Override
                public void onCreate(Context context) {

                }

                @Override
                public void onResume(Context context) {
                    LayoutManager layoutManager = mRecyclerView.getLayoutManager();
                    if (layoutManager instanceof LinearLayoutManager) {

                        int firstVisibleItem = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
                        int lastCompletePosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();

                        int first = firstVisibleItem < lastCompletePosition ? firstVisibleItem : lastCompletePosition;
                        int last = firstVisibleItem < lastCompletePosition ? lastCompletePosition : firstVisibleItem;

                        for(; first < last ; first++)
                        {
                            if(mItemList == null || (mItemList.size() <= first) || first < 0) continue;
                            PozaRecyclerViewItem item = mItemList.get(first);
                            if (item != null) {
                                PozaRecyclerViewFetcher currentfetcher = item.getViewFetcher();
                                if (currentfetcher != null)
                                    currentfetcher.onResume(context);
                            }
                        }


                    }
                }

                @Override
                public void onPause(Context context) {
                    LayoutManager layoutManager = mRecyclerView.getLayoutManager();
                    if (layoutManager instanceof LinearLayoutManager) {

                        int firstVisibleItem = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
                        int lastCompletePosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();

                        int first = firstVisibleItem < lastCompletePosition ? firstVisibleItem : lastCompletePosition;
                        int last = firstVisibleItem < lastCompletePosition ? lastCompletePosition : firstVisibleItem;

                        for(; first < last ; first++)
                        {
                            if(mItemList == null || (mItemList.size() <= first) || first < 0) continue;
                            PozaRecyclerViewItem item = mItemList.get(first);
                            if (item != null) {
                                PozaRecyclerViewFetcher currentfetcher = item.getViewFetcher();
                                if (currentfetcher != null)
                                    currentfetcher.onPause(context);
                            }
                        }


                    }
                }

                @Override
                public void onDestroy(Context context) {

                }
            };

            ((PozaActivity)mContext).addOnActivityState(mOnActivityState);
        }

	}


	public void setItemFethcer(Class<? extends PozaRecyclerViewFetcher<? extends PozaRecyclerViewItem>> fetcherClass) {
		mSetFetcherClass = fetcherClass;
	}

	public void addOnItemClickListener(OnItemClickListener listener) {
		if (listener != null) {
			mItemClickListener = listener;
		}
	}

    /**
     * 패쳐의 뷰가 보이는 상태를 가져올지 여부
     */
    public void setOnVisibleStateEnable(boolean isOnVisibleState)
    {
        IsOnVisibleState = isOnVisibleState;
        if(mScrollerListener != null)
            mScrollerListener.SetIsEnableOnVisibleState(isOnVisibleState);
    }

	public void setOnItemMoreLoadListener(PozaRecyclerViewScrollListener.OnMoreLoad listener) {
		mItemMoreLoad = listener;
	}

	
	public void resetLoadMore() {
		if (mScrollerListener != null) {
			mScrollerListener.resetLoadMore();
		}
	}

	/**
	 * @desc
	 * @see android.support.v7.widget.RecyclerView.Adapter#getItemCount()
	 */
	@Override
	public int getItemCount() {
		if (mItemList == null)
			return 0;
		return mItemList.size();
	}

	public void setItemList(ArrayList<? extends PozaRecyclerViewItem> itemList) {
		mItemList = itemList;
		initalizeAdapter();
	}

	public int getCurrentIndex() {
		if (mCurrentVisiableItem == null || mItemList == null)
			return -1;
		return mItemList.indexOf(mCurrentVisiableItem);
	}

	public PozaRecyclerViewItem getCurrentItem() {
		return mCurrentVisiableItem;
	}

	private PozaRecyclerViewFetcher createSetFetcherInstance(PozaRecyclerViewItem item) {

		try {
			if (mSetFetcherClass.equals(PozaRecyclerViewSimpleFetcher.class)) {
				Class[] constructorArgs = new Class[] { PozaRecyclerViewSimpleAdapter.class, PozaRecyclerViewItem.class };
				return (PozaRecyclerViewFetcher)mSetFetcherClass.getConstructor(constructorArgs).newInstance(this, item);
			} else {
				Class[] constructorArgs = new Class[] { item.getClass() };
				return (PozaRecyclerViewFetcher)mSetFetcherClass.getConstructor(constructorArgs).newInstance(item);
			}
		} catch (Exception e) {
			e.printStackTrace();
			LocalLog.LOGE(TAG, "setItemFethcer의 패쳐 데이터 타입과 어댑터 리스트의 데이터 타입이 다릅니다. : " + e.getMessage());

		}

		return null;
	}

	@Override
	public int getItemViewType(int position) {
		PozaRecyclerViewFetcher fetcher = null;

		if (mSetFetcherClass != null) {
			PozaRecyclerViewItem item = mItemList.get(position);
			fetcher = createSetFetcherInstance(item);
		} else {
			fetcher = mItemList.get(position).getViewFetcher();
		}

		if (fetcher == null)
			return 0;

		int viewType = fetcher.getViewType();

		if(fetcher.isNotRecycler())
		{
			String type =  fetcher.getViewType()+"" + position;
			viewType = Integer.parseInt(type);
		}

		if (mViewTypeFetcher.get(viewType) == null)
			mViewTypeFetcher.put(viewType, fetcher);

//		LOGI(LocalLog.LOG_LEVEL_TEST, TAG, "getItemViewType viewType - [" + viewType + "]");

		return viewType;

	}

	/**
	 * @desc
	 * @see android.support.v7.widget.RecyclerView.Adapter#onCreateViewHolder(ViewGroup,
	 *      int)
	 */
	@Override
	public PojaRecyclerViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
		/***
		 * 타입에 따라 홀더를 만들어준다.
		 */
		PozaRecyclerViewFetcher fetcher = mViewTypeFetcher.get(viewType);
		if (fetcher == null)
			return new PojaRecyclerViewHolder(new View(viewGroup.getContext()));

		final View view = fetcher.createLayout(mContext, viewGroup);

		view.setOnClickListener(itemClickListener);

		PojaRecyclerViewHolder holder = new PojaRecyclerViewHolder(view);
		holder.type = viewType;

		LOGI(LocalLog.LOG_LEVEL_TEST, TAG, "onCreateViewHolder viewType - [" + viewType + "]");

		return holder;
	}


	public PozaRecyclerViewFetcher getViewTypeFetcher(int viewType){
		return mViewTypeFetcher.get(viewType);
	}


    public PozaRecyclerViewFetcher getBindedViewFetcher(int position)
    {
        return mViewBindedFetcher.get(position);
    }

	/**
	 * @desc
	 * @see android.support.v7.widget.RecyclerView.Adapter#onBindViewHolder(android.support.v7.widget.RecyclerView.ViewHolder,
	 *      int)
	 */
	@Override
	public void onBindViewHolder(PojaRecyclerViewHolder holder, final int position) {

        long before = System.currentTimeMillis();
		mCurrentVisiableItem = mItemList.get(position);
		PozaRecyclerViewFetcher currentfetcher = null;

		if (mSetFetcherClass != null) {
			PozaRecyclerViewItem item = mItemList.get(position);
			currentfetcher = createSetFetcherInstance(item);
		} else {
			currentfetcher = mCurrentVisiableItem.getViewFetcher();
		}

		if (currentfetcher == null)
			return;

		if(currentfetcher.isNotRecycler())
		{
			Integer isCreated = mNotRecyclerCreatedList.get(position);
			if(isCreated != null)
			{
				return;
			}else
			{
				mNotRecyclerCreatedList.put(position, position);
			}
		}

		// PojaRecyclerViewFetcher fetcher =
		// mViewTypeFetcher.get(currentfetcher.getViewType());
		final PojaViewHolder loenholder = new PojaViewHolder(holder);
		currentfetcher.setDataForView(loenholder, currentfetcher.getBindData(), position);

		// 패쳐 클래스에 클릭 리스너를 추가해준다. -마이너스로 리스너에 저장해둬서 홀더 클릭 리스너의 중복을 피한다.
		OnItemClickListener fetcherclickListener = currentfetcher.getOnFetcherItemClickListener();

		// 홀더에 있는 리스너를 추가 해준다.
		OnItemClickListener clickListener = loenholder.getOnItemClickListener();

		if (fetcherclickListener != null) {
			if (mItemFetcherClickListeners.get(position) != null)
				mItemFetcherClickListeners.remove(position);
			mItemFetcherClickListeners.put(position, fetcherclickListener);

		}
		if (clickListener != null) {
			if (mItemClickListeners.get(position) != null)
				mItemClickListeners.remove(position);
			mItemClickListeners.put(position, clickListener);
		}

		final View itemView = holder.itemView;
		ViewGroup.LayoutParams param = itemView.getLayoutParams();
		if (param instanceof StaggeredGridLayoutManager.LayoutParams) {
			((StaggeredGridLayoutManager.LayoutParams) param).setFullSpan(currentfetcher.getSpanType() == SpanType.FullSpan);

			holder.itemView.setLayoutParams(param);

		}

        //2016.01.14 test
		if(currentfetcher.isEnableAttachView())
			setOnAttachedView(currentfetcher, loenholder, position);

//        LocalLog.d("cvrt2", "mViewBindedFetcher : " + position + " , " + mViewBindedFetcher.containsKey(position));
        if(mViewBindedFetcher.get(position) != null)
        {
            mViewBindedFetcher.remove(position);
        }
        mViewBindedFetcher.put(position,currentfetcher);

		holder.itemView.setTag(android.R.id.button1, position);

		LOGI(LocalLog.LOG_LEVEL_TEST, TAG, "onBindViewHolder position - [" + position + "] : " + (System.currentTimeMillis() - before));
	}
	
	private static HashMap<Integer, ViewTreeObserver> mPreDrawMap;

    public static void setOnAttachedView(PozaRecyclerViewFetcher currentfetcher, final PojaViewHolder loenholder, final int position )
	{
		final PozaRecyclerViewFetcher observer = currentfetcher;

		ViewTreeObserver treeObserver = loenholder.itemView.getViewTreeObserver();
		
		loenholder.itemView.setTag(android.R.id.button2, System.currentTimeMillis());
		OnPreDrawListener drawListener = new OnPreDrawListener() {
			
			@Override
			public boolean onPreDraw() {
//				long time = (long) loenholder.itemView.getTag(R.id.action_bar_activity_content);
//				long drawTime =  (System.currentTimeMillis()-time);
				
				int currentapiVersion = Build.VERSION.SDK_INT;
				if (currentapiVersion >= Build.VERSION_CODES.JELLY_BEAN)
				{
					loenholder.itemView.getViewTreeObserver().removeOnPreDrawListener(this);
				}
				
				if(mPreDrawMap != null)
				{
					ViewTreeObserver viewObserver = mPreDrawMap.get(position);
					if(viewObserver != null)
					{
						if(viewObserver.isAlive())
							viewObserver.removeOnPreDrawListener(this);
						mPreDrawMap.remove(position);
					}else
					{
						return true;
					}
				}
				
//				if(drawTime > 1000)
//				{
//					return true;
//				}
				
				observer.onAttachedView(loenholder, observer.getBindData(), position);

				return true;
			}
		};
		
		if(mPreDrawMap == null)
			mPreDrawMap = new HashMap<Integer, ViewTreeObserver>();
		
		if(mPreDrawMap.get(position) != null)
		{
			mPreDrawMap.remove(position);
		}
		
		mPreDrawMap.put(position, treeObserver);

		treeObserver.addOnPreDrawListener(drawListener);
		
		
		
//		loenholder.itemView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
//			
//			@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
//			@Override
//			public void onGlobalLayout() {
//				int currentapiVersion = android.os.Build.VERSION.SDK_INT;
//				if (currentapiVersion >= android.os.Build.VERSION_CODES.JELLY_BEAN)
//					loenholder.itemView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//				observer.onAttachedView(loenholder, observer.getBindData(), position);
//
//			}
//		});
	}

	private OnClickListener itemClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			int position = (Integer) v.getTag(android.R.id.button1);
			boolean clickIgnore = false;

			if (mItemClickListener != null) {
				clickIgnore = mItemClickListener.onClick(v, position);
			}

			OnItemClickListener fetcheritemClick = mItemFetcherClickListeners.get(position);
			if (fetcheritemClick != null && !clickIgnore) {
				clickIgnore = fetcheritemClick.onClick(v, position);
			}

			OnItemClickListener itemClick = mItemClickListeners.get(position);
			if (itemClick != null && !clickIgnore) {
				clickIgnore = itemClick.onClick(v, position);
			}

		}
	};

	OnGestureListener mGestureListener = new GestureDetector.SimpleOnGestureListener() {
		public boolean onSingleTapConfirmed(MotionEvent e) {
			if (mScrollerListener.isEnableOnClick() == false)
				return false;

			View childView = mRecyclerView.findChildViewUnder(e.getX(), e.getY());

			int position = mRecyclerView.getChildLayoutPosition(childView);
			if (childView != null) {
				for (View view : childView.getTouchables()) {
					if (view instanceof ImageView || view instanceof ViewPager) {
						continue;
					}
					Rect rect = new Rect();
					view.getHitRect(rect);

					if (rect.contains((int) e.getX(), (int) e.getY())) {

						return false;
					}
				}
			}

			boolean clickIgnore = false;
			if (mItemClickListener != null) {
				clickIgnore = mItemClickListener.onClick(childView, position);
				mRecyclerView.playSoundEffect(SoundEffectConstants.CLICK);
			}
			if (childView != null) {

				OnItemClickListener fetcheritemClick = mItemFetcherClickListeners.get(position);
				if (fetcheritemClick != null && !clickIgnore) {
					clickIgnore = fetcheritemClick.onClick(childView, position);
					mRecyclerView.playSoundEffect(SoundEffectConstants.CLICK);
				}

				OnItemClickListener itemClick = mItemClickListeners.get(position);
				if (itemClick != null && !clickIgnore) {
					clickIgnore = itemClick.onClick(childView, position);
					mRecyclerView.playSoundEffect(SoundEffectConstants.CLICK);
				}
			}

			return super.onSingleTapConfirmed(e);

		};

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			return true;
		}

	};
	
	

	public static class PojaRecyclerViewHolder extends RecyclerView.ViewHolder {
		private View mItemView;
		public int type;

		public PojaRecyclerViewHolder(View itemView) {
			super(itemView);
			mItemView = itemView;
			// TODO Auto-generated constructor stub
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
	

	public static interface OnItemClickListener {
		/**
		 * 
		 * @desc
		 * @return True일 경우 아이템 클릭 이벤트를 하위이벤트로 내리지 않고 여기서만 처리 (우선순위 - 어댑터 온클릭,
		 *         패쳐 온클릭, 홀더 온클릭)
		 */
		public boolean onClick(View view, int position);
	}


}
