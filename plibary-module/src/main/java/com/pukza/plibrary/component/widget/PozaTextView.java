/**
 * 
 */
package com.pukza.plibrary.component.widget;

import android.content.Context;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatTextView;
import android.text.Html;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;

import java.text.DecimalFormat;

/**
 * @desc
 * 
 *
 * @author hwaseopchoi
 * @date 2015. 5. 12.
 *
 */
public class PozaTextView extends AppCompatTextView {
	
	
	// Auto Resize 관련 변수들
	private static final int MIN_TEXT_SIZE = 10; // sp 단위
	private static final int NO_LINE_LIMIT = -1;
	
	private final RectF mAvailableSpaceRect = new RectF();
	private AutoResizeInterface mAutoResizer;
	private float mMinTextSize;
	private float mMaxTextSize;
	private int mMaxLines;
	private float mSpacingMult = 1.0f;
	private float mSpacingAdd = 0.0f;
	private int mWidthLimit;
	private TextPaint mPaint;
	
	private boolean mInitialize;
	private boolean mApplyAutoResize; // auto resize 적용할지 여부
	
	private interface AutoResizeInterface {
		public int onResize(int suggestedSize, RectF availableSpace);
	}
	
	public PozaTextView(Context context) {
		super(context);
		init();
	}

	public PozaTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public PozaTextView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}
//	private static Typeface typeFace;
	private void init() {
//		if(typeFace ==null)
//			typeFace=Typeface.createFromAsset(getContext().getAssets(),"fonts/DroidSansFallback.ttf");
//		setTypeface(typeFace);
		
		setIncludeFontPadding(false);
		mMinTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, MIN_TEXT_SIZE, getResources().getDisplayMetrics());
		mMaxTextSize = getTextSize();
		if (mMaxLines == 0) {
			mMaxLines = NO_LINE_LIMIT;
		}
		
		mAutoResizer = new AutoResizeInterface() {
			final RectF textRect = new RectF();
			@Override
			public int onResize(int suggestedSize, RectF availableSpace) {
				mPaint.setTextSize(suggestedSize);
				final String text = getText().toString();
				
				if (mMaxLines == 1) {
					textRect.bottom = mPaint.getFontSpacing();
					textRect.right = mPaint.measureText(text);
				} else {
					final StaticLayout layout = new StaticLayout(text, mPaint, mWidthLimit, Alignment.ALIGN_NORMAL, mSpacingMult, mSpacingAdd, true);
                    if(mMaxLines != NO_LINE_LIMIT && layout.getLineCount() > mMaxLines) {
                        return 1;
                    }
                    textRect.bottom = layout.getHeight();
                    int maxWidth = -1;
                    for(int i = 0; i < layout.getLineCount(); i++) {
                        if(maxWidth < layout.getLineWidth(i)) {
                            maxWidth = (int) layout.getLineWidth(i);
                        }
                    }
                    textRect.right = maxWidth;
				}
				
				textRect.offsetTo(0,0);
                if(availableSpace.contains(textRect)) {
                    return -1;
                }
                return 1;
			}
		};
		mInitialize = true;
	}

	public void setTextNonHtml(String html) {
		if(html != null){
//			html = html.replaceAll("<(.*?)\\>", "");
//			html = html.replaceAll("<(.*?)\\\n", "");
//			html = html.replaceFirst("(.*?)\\>", "");
//			html = html.replaceAll("&nbsp;", "");
//			html = html.replaceAll("&amp;", "");
			try{
                setText(Html.fromHtml(html).toString());
            }catch (Exception e)

            {
                setText(html);
            }


		}
	}

	public void setTextHtml(String text) {
		if(text != null){
            try{
                setText(Html.fromHtml(text));
            }catch (Exception e)

            {
                setText(text);
            }

		}
		
//		setMovementMethod(LinkMovementMethod.getInstance());
//		Linkify.addLinks(this, Linkify.WEB_URLS);
	}
	
	public void setTextNumber(String text) {
		try{
			int textInt = Integer.parseInt(text);
		    DecimalFormat formatter = new DecimalFormat("###,###,###");
		    setText( formatter.format(textInt));
		} catch(Exception e) {
			setText(text);
		}
	}
	
	@Override
	public void setTypeface(Typeface tf) {
		if (mPaint == null) {
			mPaint = new TextPaint(getPaint());
		}
		mPaint.setTypeface(tf);
		super.setTypeface(tf);
	}
	
	@Override
	public void setTextSize(float size) {
		mMaxTextSize = size;
		if (mApplyAutoResize) {
			adjustTextSize();
			return;
		}
		super.setTextSize(size);
	}
	
	public void setMinTextSize(float minSize) {
		mMinTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, minSize, getResources().getDisplayMetrics());
	}
	
	public void setMinTextSize(int type, float minSize) {
		mMinTextSize = TypedValue.applyDimension(type, minSize, getResources().getDisplayMetrics());
	}
	
	@Override
	public void setMaxLines(int maxlines) {
		super.setMaxLines(maxlines);
		mMaxLines = maxlines;
		reAdjust();
	}
	
	@Override
	public int getMaxLines() {
		return mMaxLines;
	}
	
	@Override
	public void setSingleLine() {
		super.setSingleLine();
		mMaxLines = 1;
		reAdjust();
	}
	
	@Override
	public void setSingleLine(boolean singleLine) {
		super.setSingleLine(singleLine);
		mMaxLines = (singleLine) ? 1 : NO_LINE_LIMIT;
		reAdjust();
	}
	
	private void reAdjust() {
		if (mApplyAutoResize) {
			adjustTextSize();
		}
	}
	
	public void setIsAutoResize(boolean isAutoResize) {
		mApplyAutoResize = isAutoResize;
	}
	
	private void adjustTextSize() {
		if(!mInitialize) {
            return;
        }
        final int startSize = (int) mMinTextSize;
        final int heightLimit = getMeasuredHeight() - getCompoundPaddingBottom() - getCompoundPaddingTop();
        mWidthLimit = getMeasuredWidth() - getCompoundPaddingLeft() - getCompoundPaddingRight();
        if(mWidthLimit <= 0) {
            return;
        }
        mAvailableSpaceRect.right = mWidthLimit;
        mAvailableSpaceRect.bottom = heightLimit;
        super.setTextSize(TypedValue.COMPLEX_UNIT_PX, efficientTextSizeSearch(startSize, (int) mMaxTextSize, mAutoResizer, mAvailableSpaceRect));
	}
	
	private int efficientTextSizeSearch(final int start, final int end, final AutoResizeInterface sizeTester, final RectF availableSpace) {
		return binarySearch(start, end, sizeTester, availableSpace);
    }
	
	private int binarySearch(final int start, final int end, final AutoResizeInterface sizeTester, final RectF availableSpace) {
        int lastBest = start;
        int lo = start;
        int hi = end-1;
        int mid = 0;
        while(lo <= hi) {
            mid=lo+hi>>>1;
            final int midValCmp = sizeTester.onResize(mid, availableSpace);
            if(midValCmp < 0) {
                lastBest = lo;
                lo = mid + 1;
            } else if(midValCmp>0) {
                hi = mid - 1;
                lastBest = hi;
            } else {
                return mid;
            }
        }
        return lastBest;
    }

	
	@Override
	protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
		super.onTextChanged(text, start, lengthBefore, lengthAfter);
		reAdjust();
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		if(w != oldw || h != oldh) {
            reAdjust();
        }
	}
	
	/** 
	 * @desc 
	 * @see android.view.View#onFinishInflate()
	 */
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
//		LocalLog.d("cvrt", "textSize : "+ getTextSize() + " , change:" + getTextSize()*1.01f);
		if (!mApplyAutoResize) {
			setTextSize(TypedValue.COMPLEX_UNIT_PX, getTextSize() * 1.2f);
		}
	}
	
	public static interface LoenTextViewSizeUp{
		
	}
}
