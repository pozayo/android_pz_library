/**
 * 
 */
package com.pukza.plibrary.component.widget;

import android.content.Context;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;
import android.view.KeyEvent;

/**
 * @desc 
 * 
 *
 * @author hwaseopchoi
 *
 */
public class PozaEditText extends AppCompatEditText{
	public PozaEditText(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	public PozaEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	
	public PozaEditText(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub
	}
	
	
	public boolean isEmojiCheck()
	{

		return isHighSurrogateCheck();
	}
	
	private boolean isHighSurrogateCheck()
	{
		//emoji 체크 
		String strContent = getText().toString();
		for(int i = 0 ; i < length(); i++)
		{    		
			if(Character.isHighSurrogate(strContent.charAt(i)))
			{
				return true;
			}
		}
		
		return false;




	}

    public static boolean isEmojiCheck(String text)
    {
//        for (int i = 0; i < text.length(); i++) {
//            if (Character.isHighSurrogate(text.charAt(i))) {
//                return true;
//            }
//        }

        return false;
    }

	
	
	/**
	 * 
	 * @desc 
	 * 백키 리스너
	 *
	 * @author hwaseopchoi
	 *
	 */
    public interface SoftKeyboardBackListener {
        void onBackKeyAction();
    }

    private SoftKeyboardBackListener mSoftKeyboardBackListener;

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            // 키보드 백키 눌러서 사라지는 경우 이벤트 처리
            if (mSoftKeyboardBackListener != null)
                mSoftKeyboardBackListener.onBackKeyAction();
        }

        return super.onKeyPreIme(keyCode, event);
    }

    public void setOnSoftKeyboardBackListener(SoftKeyboardBackListener listener) {
        mSoftKeyboardBackListener = listener;
    }
}
