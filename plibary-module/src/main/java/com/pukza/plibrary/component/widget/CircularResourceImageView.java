package com.pukza.plibrary.component.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ImageView;

import com.pukza.plibrary.component.image.ImageRequestListener;


public class CircularResourceImageView
        extends PozaImageView {

    private boolean isCircle = true;

    public CircularResourceImageView(Context context) {
        super(context);
        setEnableCircle(true);

    }

    public CircularResourceImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setEnableCircle(true);

    }

    public CircularResourceImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setEnableCircle(true);
    }


    @Override
    public void setImageResource(int resId) {
        setImageCircleResource(resId);
    }

    @Override
    public void setImageUrl(String url, int error, ImageRequestListener listener) {
        super.setImageUrl(url, error, listener);
    }

    @Override
    public void setImageUrl(String url, int error, Drawable placeHolder, ImageRequestListener listener) {
        super.setImageUrl(url, error, placeHolder, listener);
    }



    @Override
    public void setImageGifUrl(String url, int error, Drawable placeHolder, ImageRequestListener listener) {
        super.setImageGifUrl(url, error, placeHolder, listener);
    }

    public void setImageCircleResource(int resId) {
        setEnableFresco(false);
        setScaleType(ImageView.ScaleType.CENTER_CROP);
        setFadeDuration(0);
        super.setImageResource(resId);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	protected void onDraw(Canvas canvas) {
        if(isEnableFresco){
            super.onDraw(canvas);
            return;
        }

		long drawbefore = System.currentTimeMillis();
        Drawable drawable = getDrawable();

        if (isCircle) {
			if (drawable == null) {
				return;
			}

			if (getWidth() == 0 || getHeight() == 0) {
				return;
			}

			if (drawable instanceof BitmapDrawable) {
				Bitmap b = ((BitmapDrawable) drawable).getBitmap();
				if (b == null || b.isRecycled()) {
					return;
				}
				Bitmap bitmap = b.copy(Bitmap.Config.ARGB_8888, true);

				Bitmap roundBitmap = getCroppedBitmap(bitmap, getWidth());
				canvas.drawBitmap(roundBitmap, 0, 0, null);
			} else if (drawable instanceof ColorDrawable) {
				ColorDrawable draw = (ColorDrawable) drawable;
				Paint paint = new Paint();
				paint.setAntiAlias(true);
				paint.setFilterBitmap(true);
				paint.setDither(true);
				paint.setColor(draw.getColor());

				canvas.drawCircle(getWidth() / 2 + 0.1f, getHeight() / 2 + 0.1f, getWidth() / 2 + 0.1f, paint);
			} else if (drawable instanceof NinePatchDrawable) {

				Bitmap b = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
				Canvas c = new Canvas(b);
				drawable.draw(c);

				if (b == null) {
					return;
				}
				Bitmap bitmap = b.copy(Bitmap.Config.ARGB_8888, true);
				Bitmap roundBitmap = getCroppedBitmap(bitmap, getWidth());
				canvas.drawBitmap(roundBitmap, 0, 0, null);
			}

            if(mStrokeColor != Color.TRANSPARENT)
            {
                float dp1 = dpToPixel(1);
                Paint strokePaint = new Paint();
                strokePaint.setColor(mStrokeColor);
                strokePaint.setStyle(Paint.Style.STROKE);
                strokePaint.setStrokeWidth(dp1);
                strokePaint.setAntiAlias(true);
                canvas.drawCircle(getWidth() / 2, getHeight() / 2, (getWidth() / 2) - (dp1/2) , strokePaint);
            }

		} else {
            if (drawable instanceof BitmapDrawable) {
                Bitmap b = ((BitmapDrawable) drawable).getBitmap();
                if (b == null || b.isRecycled()) {
                    return;
                }
            }
			super.onDraw(canvas);
		}

	}

    private float dpToPixel(float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getContext().getResources().getDisplayMetrics());
    }

	public Bitmap getCroppedBitmap(Bitmap bmp, int radius) {
		Bitmap sbmp;
		if (bmp.getWidth() != radius || bmp.getHeight() != radius)
			sbmp = Bitmap.createScaledBitmap(bmp, radius, radius, false);
		else
			sbmp = bmp;

		Bitmap output = Bitmap.createBitmap(sbmp.getWidth(), sbmp.getHeight(), Bitmap.Config.ARGB_8888);
		final Rect rect = new Rect(0, 0, sbmp.getWidth(), sbmp.getHeight());

		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setFilterBitmap(true);
		paint.setDither(true);
		paint.setColor(Color.parseColor("#BAB399"));

		Canvas c = new Canvas(output);
		c.drawARGB(0, 0, 0, 0);
		c.drawCircle(sbmp.getWidth() / 2 , sbmp.getHeight() / 2 , sbmp.getWidth() / 2, paint);
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		c.drawBitmap(sbmp, rect, rect, paint);

		return output;
	}

}
