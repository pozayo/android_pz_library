/**
 * 
 */
package com.pukza.plibrary.support.app;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import com.pukza.plibrary.component.widget.PozaViewPager;
import com.pukza.plibrary.support.app.PozaRecyclerViewFetcher.PojaViewHolder;
import com.pukza.plibrary.support.app.PozaViewPagerAdapter.PojaAdapterViewHolder;
import com.pukza.plibrary.support.app.PozaViewPagerSimpleAdapter.PozaViewPagerSimpleFetcher;

/**
 * @desc
 * 
 *
 * @author hwaseopchoi
 * @date 2015. 7. 7.
 *
 */
public class PozaViewPagerFragmentAdapter extends FragmentStatePagerAdapter {
	private ViewPager mViewPager;
	private SparseArray<Fragment> mFragmentList;					//별도 추가한 프레그먼트 리스트
	private ArrayList<? extends PozaRecyclerViewItem> mItemList;	//아이템 리스트
	private ArrayList<Object> mList;	//위아래 합친 전체 리스트
	private Context mContext;
	
	private boolean mPause;

	private Class<? extends PozaRecyclerViewFetcher<? extends PozaRecyclerViewItem>> mSetFetcherClass;

	public PozaViewPagerFragmentAdapter(FragmentManager fm, ViewPager viewPager, ArrayList<? extends PozaRecyclerViewItem> itemList) {
		super(fm);
		mViewPager = viewPager;
		mItemList = itemList;
		mList = new ArrayList<>();
		mList.addAll(mItemList);
		mFragmentList = new SparseArray<>();
		
		if (viewPager instanceof PozaViewPager) {
			viewPager.addOnPageChangeListener(new OnPageChangeListener() {

				@Override
				public void onPageSelected(int arg0) {
					mPause = false;
					Fragment fragment = mFragmentList.get(mViewPager.getCurrentItem());
					if (fragment != null && fragment instanceof PojaViewPagerFragment) {
						((PojaViewPagerFragment) fragment).mFetcher.onCreate(fragment.getContext());
					}
				}

				@Override
				public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
					int oldPosition = mViewPager.getCurrentItem();

					if ((position >= oldPosition && positionOffset >= 0.25f)
							|| (position < oldPosition && positionOffset <= 0.75f)) {
						Fragment fragment = mFragmentList.get(mViewPager.getCurrentItem());
						if (fragment != null && fragment instanceof PojaViewPagerFragment && !mPause) {
							((PojaViewPagerFragment) fragment).mFetcher.onDestroy(fragment.getContext());
							mPause = true;
						}
					}
				}

				@Override
				public void onPageScrollStateChanged(int arg0) {
					mPause = false;
				}
			});
		}
	}

	public void setItemFethcer(Class<? extends PozaRecyclerViewFetcher<? extends PozaRecyclerViewItem>> fetcherClass) {
		mSetFetcherClass = fetcherClass;
	}
	
	public void addFramgent(int index, Fragment fragment) {
		mFragmentList.put(index, fragment);
		mList.add(index, fragment);
	}

	@Override
	public int getCount() {
		return mList.size();
	}
	
	@Override
	public int getItemPosition(Object object) {
		if (object instanceof PojaViewPagerFragment) {
			return PagerAdapter.POSITION_NONE;
		}
		return super.getItemPosition(object);
	}

	public void clearAll() {
		mList.clear();
		mItemList.clear();
		mFragmentList.clear();
	}
	
	public void setItemList(ArrayList<? extends PozaRecyclerViewItem> itemList) {
		mItemList = itemList;
		mList.clear();
		mList.addAll(mItemList);
		
//		int fragmentSize = mFragmentList.size();
//		for (int i = 0; i < fragmentSize; i++) {
//			int key = mFragmentList.keyAt(i);
//			mList.add(key, mFragmentList.get(key));
//		}
		
//		for(Entry<Integer, Fragment> addFragment : mFragmentList.entrySet()) {
//			mList.add(addFragment.getKey(), addFragment.getValue());
//		}
	}
	
	public <T extends PozaRecyclerViewItem> T getItemAt(int position) {
		return (mItemList != null) ? (T) mItemList.get(position) : null;
	}
	
	public Fragment getFragmentItemAt(int position) {
		return mFragmentList.get(position);
	}
	
	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		super.destroyItem(container, position, object);
		mFragmentList.remove(position);
	}

	@Override
	public Fragment getItem(int position) {
		Object item = mList.get(position);
		if(item instanceof Fragment) {
			return (Fragment)item;
		}
		PojaViewPagerFragment fragment = new PojaViewPagerFragment(position);
		mFragmentList.put(position, fragment);
		
		return fragment;
	}
	

	public class PojaViewPagerFragment extends Fragment {
		private PozaRecyclerViewItem mItem;
		private int position;
		private PozaRecyclerViewFetcher mFetcher;

		public PojaViewPagerFragment(){
			position = 0;
			if(mItemList.size() > 0)
			{
				mItem = mItemList.get(position);
				this.position = position;
			}
		}
		
		public PojaViewPagerFragment(int position) {
			position = (position < mItemList.size()) ? position : mItemList.size() - 1;
			mItem = mItemList.get(position);
			this.position = position;

		}

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			mFetcher = mItem.getViewFetcher();
			if (mFetcher != null) {
				mFetcher.onCreate(getContext());
			}
			
		}
		
		public boolean dispatchOnBackPressed() {
			return (mFetcher != null && mFetcher.dispatchOnBackPressed());
		}

		public PozaRecyclerViewFetcher getFetcher() {
			return mFetcher;
		}

		public void setOnFetcherSelected(int position) {
			if (mFetcher != null) {
				mFetcher.onFetcherSelected(position);
			}
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			if (mSetFetcherClass != null) {
				try {
					if (mSetFetcherClass.equals(PozaViewPagerSimpleFetcher.class)) {
						Class[] constructorArgs = new Class[] { PozaViewPagerSimpleAdapter.class, PozaRecyclerViewItem.class };
						mFetcher = (PozaRecyclerViewFetcher)mSetFetcherClass.getConstructor(constructorArgs).newInstance(this, mItem);
					} else {
						Class[] constructorArgs = new Class[] { mItem.getClass() };
						mFetcher = (PozaRecyclerViewFetcher)mSetFetcherClass.getConstructor(constructorArgs).newInstance(mItem);
					}
				} catch (Exception e) {
					e.printStackTrace();

				}
			}
			
			if (mFetcher == null) {
				return super.onCreateView(inflater, container, savedInstanceState);
			}

			final View v = mFetcher.createLayout(container.getContext(), container);

			PojaAdapterViewHolder holder = new PojaAdapterViewHolder(v, position);
			final PojaViewHolder loenHolder = new PojaViewHolder(holder);

			mFetcher.setDataForView(loenHolder, mItem, position);

			PozaRecyclerViewAdapter.setOnAttachedView(mFetcher, loenHolder, position);

			return v;
		}

        @Override
        public void onResume() {
            super.onResume();
            if (mFetcher != null) {
                mFetcher.onResume(getContext());
            }
        }

        @Override
		public void onPause() {
			super.onPause();
			if (mFetcher != null) {
				mFetcher.onPause(getContext());
			}
		}
		
		@Override
		public void onDestroyView() {
			super.onDestroyView();
			if (mFetcher != null) {
				mFetcher.onDestroy(getContext());
			}
		}
	}
}