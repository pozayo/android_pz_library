/**
 * 
 */
package com.pukza.plibrary.component.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import com.pukza.plibrary.component.image.ImageRequestListener;

/**
 * Created by choihwaseop on 2016. 2. 2..
 */
public class PozaImageView extends FrescoImageView {
	private static int colorPoistion = 0;
	
	public PozaImageView(Context context) {
		super(context);
		colorPoistion++;
	}
	
	public PozaImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		colorPoistion++;
	}
	
	public PozaImageView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		colorPoistion++;
	}
	
	public static void setLoadingColorPosition(int position) {
		colorPoistion = position;
	}

	/** 
	 * @desc 
	 */
	public void setImageUrl(String url) {
        this.setImageUrl(url, 0, (ImageRequestListener) null);
    }
	
	public void setImageUrl(String url, ImageRequestListener listener) {
        this.setImageUrl(url, 0, listener);
    }

	public void setImageUrl(String url, int error) {

		this.setImageUrl(url, error, (ImageRequestListener)null);
	}

	public void setImageUrl(String url, Drawable placeHolder, int error) {
		this.setImageUrl(url, placeHolder, error, null);
	}

	public void setImageUrl(String url, Drawable placeHolder, int error, ImageRequestListener listener) {
		super.setImageUrl(url, error, placeHolder, listener);
	}
	
	public void setImageUrl(String url, int error, ImageRequestListener listener) {
		if(url !=null) {
			url = url.replace("http://timgs.wecandeo.com", "http://aztvimg.melon.co.kr");
			url = url.replace("http://graph.facebook.com", "https://graph.facebook.com");
		}
		
		ColorDrawable color = new ColorDrawable(getDefaultLoadingColor());

		super.setImageUrl(url, error, color, listener);
//        super.setImageUrl(url, error, color, new RequestListener<String, Bitmap>() {
//            @Override
//            public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource) {
//                return false;
//            }
//
//            @Override
//            public boolean onResourceReady(Bitmap resource, String model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
//                return false;
//            }
//        }); //Glide


    }

    public void setImageUrlGif(String url, int error){
        setImageUrlGif(url,error, null);
	}
	
	public void setImageUrlGif(String url, int error, ImageRequestListener listener){
//		setImageResource(error);
		
		if(url !=null) {
			url = url.replace("http://timgs.wecandeo.com", "http://aztvimg.melon.co.kr");
		}
		super.setImageGifUrl(url, error , new ColorDrawable(getDefaultLoadingColor()), listener);
//        super.setImageGifUrl(url, error, new ColorDrawable(getDefaultLoadingColor()), new RequestListener<String, GifDrawable>() {
//            @Override
//            public boolean onException(Exception e, String model, Target<GifDrawable> target, boolean isFirstResource) {
//                return false;
//            }
//
//            @Override
//            public boolean onResourceReady(GifDrawable resource, String model, Target<GifDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
//                return false;
//            }
//        });// Glide

    }
	
	private static TypedArray defaultLoadingColor;
	private int getDefaultLoadingColor() {
//		if(defaultLoadingColor == null)
//			defaultLoadingColor = getContext().getResources().obtainTypedArray(R.array.imageview_loading_background_colors);
//		int color = defaultLoadingColor.getColor(colorPoistion%defaultLoadingColor.length() , 0);
//		ta.recycle();
		int color = Color.WHITE;
		return color;
	}
}
