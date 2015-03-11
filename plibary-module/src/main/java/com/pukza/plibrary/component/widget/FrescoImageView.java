package com.pukza.plibrary.component.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import com.facebook.common.references.CloseableReference;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.DraweeHolder;
import com.facebook.imagepipeline.bitmaps.PlatformBitmapFactory;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.BasePostprocessor;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.facebook.imagepipeline.request.Postprocessor;

import com.pukza.plibrary.component.image.ImageRequestListener;
import com.pukza.plibrary.util.LocalLog;

/**
 * Created by choihwaseop on 2016. 2. 2..
 */
public class FrescoImageView extends NetworkImageView{
    DraweeHolder<GenericDraweeHierarchy> mDraweeHolder;
    private static int colorPoistion = 0;
    protected int mStrokeColor = Color.TRANSPARENT;

    private boolean isGif;
    private boolean isCircle;
    private boolean isCrop = true;
    private boolean isLoadingColor = true;
    private boolean isBlur = false;
    private boolean isTransform = false;
    private boolean isCacheHigh = false;    //고품질로 캐시한다.
    private boolean isResize = false;
    private ScalingUtils.ScaleType mScaleType;

    private int mBlurRadius = 25;
    private ImageRequestListener mListener = null;
    private ImageInfo mCompleteInfo;
    // 단말기에서 그릴수 있는 최대 텍스쳐 크기
    private int mMaxTextureSize;

    // 비트맵이 최대 텍스쳐 크기보다 클때 크롭할지 여부
    // true : 최대 텍스쳐 크기에 맞춰 crop
    // false : 최대 텍스쳐 크기에 맞춰 원본비율 유지하면서 리사이즈
    private boolean mMaxTextureSizeCrop = true;
    private DraweeController mController;
    private GenericDraweeHierarchy mHierarchy;
    public FrescoImageView(Context context) {
        super(context);
        init(context);
    }

    public FrescoImageView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }

    public FrescoImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mHierarchy = new GenericDraweeHierarchyBuilder(getResources()).build();
        mDraweeHolder = DraweeHolder.create(mHierarchy, context);
        isAutoRotate = false;
    }

    protected void setImageUrl(String url, final int error, Drawable placeHolder, ImageRequestListener listener) {
        super.setImageUrl(url);
        if (url == null)
            url = "";

        if (url.length() < 1) {
            setEnableFresco(false);
            setImageResource(error);
            return;
        }

        setEnableFresco(true);

        isGif = false;
        mListener = listener;
        mHierarchy = mDraweeHolder.getHierarchy();
		mHierarchy.setPlaceholderImage(placeHolder);
        ImageRequestBuilder requestBuilder = ImageRequestBuilder.newBuilderWithSource(Uri.parse(url));
        requestBuilder.setAutoRotateEnabled(isAutoRotate);
        requestBuilder.setResizeOptions(mResizeOptions);
        if (isLoadingColor == false || isCircle) {
            placeHolder = null;
            if (isCircle)
                requestBuilder.setImageType(ImageRequest.ImageType.SMALL);
        }

        if (isBlur) {
            requestBuilder.setPostprocessor(fastBlurPostProcessor);
        } else if (isResize) {
            requestBuilder.setPostprocessor(resizeIfNeedPostProcessor);
        }

        mHierarchy.setFadeDuration(mFadeDuration);

        if (isTransform) {
            if (isCrop) {
                mHierarchy.setActualImageScaleType(ScalingUtils.ScaleType.CENTER_CROP);
            } else {
                mHierarchy.setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER);
            }
        } else
        {
            mHierarchy.setActualImageScaleType(ScalingUtils.ScaleType.FOCUS_CROP);
            mHierarchy.setActualImageFocusPoint(new PointF(0.5f, 0.0f));
        }

        if(mScaleType != null)
        {
            mHierarchy.setActualImageScaleType(mScaleType);
        }

        ImageRequest request = requestBuilder.build();
        mController = (PipelineDraweeController) Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .setOldController(mDraweeHolder.getController())
                .setControllerListener(mControlListener)
                .build();

		mDraweeHolder.setController(mController);
        mDraweeHolder.getTopLevelDrawable().setCallback(this);

        if(error != 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mHierarchy.setFailureImage(getResources().getDrawable(error, getContext().getTheme()), ScalingUtils.ScaleType.CENTER_CROP);
            } else {
                mHierarchy.setFailureImage(getResources().getDrawable(error), ScalingUtils.ScaleType.CENTER_CROP);
            }
        }
        mDraweeHolder.onAttach();

    }

    @Override
    public Drawable getDrawable() {
        if(isEnableFresco)
        {
            if(mDraweeHolder != null)
            {
//                RoundingParams param = mDraweeHolder.getHierarchy().getRoundingParams();
//                param.setRoundAsCircle(false);
//                param.setBorder(Color.TRANSPARENT, 0);
//                mDraweeHolder.getHierarchy().setRoundingParams(param);
                Drawable drawable = mDraweeHolder.getTopLevelDrawable();
                return drawable;
            }
        }
        return super.getDrawable();

    }

    public boolean isCompleteImage()
    {
        return mCompleteInfo != null;
    }

    public ImageInfo getCompleteImageInfo()
    {
       return mCompleteInfo;
    }
    public Bitmap getImage()
    {
        Bitmap bitmap = null;
        if(mDraweeHolder != null && mCompleteInfo != null)
        {
            Drawable drawable = mDraweeHolder.getTopLevelDrawable();

            if(drawable != null)
            {
                bitmap = Bitmap.createBitmap(mCompleteInfo.getWidth(), mCompleteInfo.getHeight(), Bitmap.Config.ARGB_4444);
                Canvas canvas = new Canvas(bitmap);
                drawable.setBounds(0, 0, getWidth(), getHeight());
                drawable.draw(canvas);
            }

        }

        return bitmap;

    }


    public void setImageGifUrl(String url, final int error, Drawable placeHolder, ImageRequestListener listener) {
        super.setImageUrl(url);

        if(url == null)
            url = "";

        if(url.length() < 1)
        {
            setEnableFresco(false);
            setImageResource(error);
            return;
        }

        isGif = true;
        setEnableFresco(true);

        ImageRequestBuilder requestBuilder =  ImageRequestBuilder.newBuilderWithSource(Uri.parse(url));
        ImageRequest request = requestBuilder.build();

        mController = (PipelineDraweeController) Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .setAutoPlayAnimations(true)
                .setOldController(mDraweeHolder.getController())
                .setControllerListener(mControlListener)
                .build();

        if(error != 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mDraweeHolder.getHierarchy().setFailureImage(getResources().getDrawable(error, getContext().getTheme()));
            } else {
                mDraweeHolder.getHierarchy().setFailureImage(getResources().getDrawable(error));
            }
        }
        mDraweeHolder.setController(mController);
        mDraweeHolder.getHierarchy().setPlaceholderImage(placeHolder);
        mDraweeHolder.getTopLevelDrawable().setCallback(this);

        mDraweeHolder.onAttach();

    }


    private BaseControllerListener<ImageInfo> mControlListener = new BaseControllerListener<ImageInfo>() {
        @Override
        public void onFailure(String id, Throwable throwable) {
            super.onFailure(id, throwable);

            LocalLog.d("cvrt", "gif onFailure : " + id);

            if (mListener != null) {
                mListener.onException(new Exception(throwable.getMessage(), throwable) , true);
            }
        }

        @Override
        public void onRelease(String id) {
            super.onRelease(id);
        }


        @Override
        public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
            super.onFinalImageSet(id, imageInfo, animatable);
            mCompleteInfo = imageInfo;


            if(getDrawable() instanceof BitmapDrawable) {

                if (mListener != null) {
                    BitmapDrawable drawable = (BitmapDrawable) getDrawable();

                    if (drawable != null)
                        mListener.onResourceReady(id, true);
                }
            }else if(mDraweeHolder != null && mDraweeHolder.getHierarchy() != null) {
                Drawable drawable = mDraweeHolder.getHierarchy().getTopLevelDrawable();
                if (drawable != null) {

                    if (mListener != null) {

                        Bitmap drawBitmap = Bitmap.createBitmap(imageInfo.getWidth(), imageInfo.getHeight(), Bitmap.Config.ARGB_4444);
                        Canvas canvas = new Canvas(drawBitmap);

                        drawable.draw(canvas);

                        if (drawable != null)
                            mListener.onResourceReady(id, true);
                    }

                }
            }


        }


    };

    @Override
    protected boolean verifyDrawable(Drawable dr) {
        if(!isEnableFresco && !isGif) return super.verifyDrawable(dr);

        if(mDraweeHolder != null && mDraweeHolder.getHierarchy() != null) {
            if (dr == mDraweeHolder.getHierarchy().getTopLevelDrawable()) {
                return true;
            }
        }
        return super.verifyDrawable(dr);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if(!isEnableFresco && !isGif) {
            super.onDraw(canvas);
            return;
        }

        GenericDraweeHierarchy hierarchy = mDraweeHolder.getHierarchy();
        Drawable drawable = hierarchy.getTopLevelDrawable();
        if (isGif) {
//            super.onDraw(canvas);
            drawable.setBounds(0, 0, getWidth(), getHeight());
            drawable.draw(canvas);
//            super.setImageDrawable(drawable);
        } else {
            if (hierarchy == null) return;
//            setEnableCircle(isCircle);
            drawable.setBounds(0, 0, getWidth(), getHeight());
            drawable.draw(canvas);
        }
    }

    public void setEnableCircle(boolean isCircle) {
        this.isCircle = isCircle;

        GenericDraweeHierarchy hierarchy = mDraweeHolder.getHierarchy();
        if(hierarchy == null) return;

        if(isCircle)
        {
            RoundingParams roundingParams = hierarchy.getRoundingParams();
            if(hierarchy.getRoundingParams() == null)
                roundingParams = RoundingParams.asCircle();

            roundingParams.setRoundAsCircle(true);
            roundingParams.setBorder(mStrokeColor, dpToPixel(1));
            hierarchy.setRoundingParams(roundingParams);
        }
        else
        {
            hierarchy.setRoundingParams(null);
        }
    }
    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mDraweeHolder.onDetach();
    }

    @Override
    public void onStartTemporaryDetach() {
        super.onStartTemporaryDetach();
        mDraweeHolder.onDetach();
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        mDraweeHolder.onAttach();
    }

    @Override
    public void onFinishTemporaryDetach() {
        super.onFinishTemporaryDetach();
        mDraweeHolder.onAttach();
    }

    private Postprocessor resizeIfNeedPostProcessor = new BasePostprocessor() {

        @Override
        public String getName() {
            return "resizeIfNeedPostProcessor";
        }

        @Override
        public CloseableReference<Bitmap> process(Bitmap sourceBitmap, PlatformBitmapFactory bitmapFactory) {
            DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
            int resolution = displayMetrics.widthPixels / 320;


            final int sourceWidth = sourceBitmap.getWidth();
            final int sourceHeight = sourceBitmap.getHeight();

            CloseableReference<Bitmap> bitmapRef = null;

//            bitmapRef = bitmapFactory.createBitmap(sourceWidth,sourceHeight);
            if (mMaxTextureSize > 0 && sourceHeight > mMaxTextureSize) {
                if (mMaxTextureSizeCrop) {
                    bitmapRef = bitmapFactory.createBitmap(sourceWidth, mMaxTextureSize);
                } else {
                    bitmapRef = bitmapFactory.createBitmap(sourceWidth * mMaxTextureSize / sourceHeight, mMaxTextureSize);
                }
            } else if (sourceWidth < 320 && sourceHeight < 320) {
                bitmapRef = bitmapFactory.createBitmap(sourceWidth * resolution, sourceHeight * resolution);
            }else
            {
                bitmapRef = bitmapFactory.createBitmap(sourceWidth,sourceHeight);
            }


            try {
                Bitmap destBitmap = bitmapRef.get();
                Rect srcR = new Rect(0,0,sourceBitmap.getWidth(),sourceBitmap.getHeight());
                RectF dstR = new RectF(0, 0, destBitmap.getWidth(), destBitmap.getHeight());
                Canvas canvas = new Canvas(destBitmap);
                Paint paint = new Paint();
                canvas.drawBitmap(sourceBitmap,srcR,dstR,paint);

//                canvas.setBitmap(destBitmap);
//                Rect srcR = new Rect(0,0,sourceBitmap.getWidth(),sourceBitmap.getHeight());
//                RectF dstR = new RectF(0, 0, destBitmap.getWidth(), destBitmap.getHeight());
//                canvas.drawBitmap(sourceBitmap,srcR,dstR,new Paint());
//                canvas.setBitmap(null);
//                for (int x = 0; x < destBitmap.getWidth(); x += 2) {
//                    for (int y = 0; y < destBitmap.getHeight(); y += 2) {
//                        destBitmap.setPixel(x,y,sourceBitmap.getPixel(x, y));
//                    }
//                }
                return CloseableReference.cloneOrNull(bitmapRef);
            } finally {
                CloseableReference.closeSafely(bitmapRef);
            }
        }
    };
    private Postprocessor fastBlurPostProcessor = new BasePostprocessor() {
        @Override
        public String getName() {
            return "fastBlurPostProcessor";
        }

        @Override
        public void process(Bitmap bitmap) {
            fastblur(bitmap,mBlurRadius);
        }
    };

    /**
     * 이미지를 흐리게 하는 함수
     */
    public static void fastblur(Bitmap bitmap, int radius) {

        if (radius < 1) return;
//		Bitmap bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        int[] pix = new int[w * h];
        bitmap.getPixels(pix, 0, w, 0, 0, w, h);

        int wm = w - 1;
        int hm = h - 1;
        int wh = w * h;
        int div = radius + radius + 1;

        int r[] = new int[wh];
        int g[] = new int[wh];
        int b[] = new int[wh];
        int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
        int vmin[] = new int[Math.max(w, h)];

        int divsum = (div + 1) >> 1;
        divsum *= divsum;
        int dv[] = new int[256 * divsum];
        for (i = 0; i < 256 * divsum; i++) {
            dv[i] = (i / divsum);
        }

        yw = yi = 0;

        int[][] stack = new int[div][3];
        int stackpointer;
        int stackstart;
        int[] sir;
        int rbs;
        int r1 = radius + 1;
        int routsum, goutsum, boutsum;
        int rinsum, ginsum, binsum;

        for (y = 0; y < h; y++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            for (i = -radius; i <= radius; i++) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))];
                sir = stack[i + radius];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);
                rbs = r1 - Math.abs(i);
                rsum += sir[0] * rbs;
                gsum += sir[1] * rbs;
                bsum += sir[2] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
            }
            stackpointer = radius;

            for (x = 0; x < w; x++) {

                r[yi] = dv[rsum];
                g[yi] = dv[gsum];
                b[yi] = dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (y == 0) {
                    vmin[x] = Math.min(x + radius + 1, wm);
                }
                p = pix[yw + vmin[x]];

                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[(stackpointer) % div];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi++;
            }
            yw += w;
        }
        for (x = 0; x < w; x++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            yp = -radius * w;
            for (i = -radius; i <= radius; i++) {
                yi = Math.max(0, yp) + x;

                sir = stack[i + radius];

                sir[0] = r[yi];
                sir[1] = g[yi];
                sir[2] = b[yi];

                rbs = r1 - Math.abs(i);

                rsum += r[yi] * rbs;
                gsum += g[yi] * rbs;
                bsum += b[yi] * rbs;

                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }

                if (i < hm) {
                    yp += w;
                }
            }
            yi = x;
            stackpointer = radius;
            for (y = 0; y < h; y++) {
                // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16) | (dv[gsum] << 8) | dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (x == 0) {
                    vmin[y] = Math.min(y + r1, hm) * w;
                }
                p = x + vmin[y];

                sir[0] = r[p];
                sir[1] = g[p];
                sir[2] = b[p];

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[stackpointer];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi += w;
            }
        }

        bitmap.setPixels(pix, 0, w, 0, 0, w, h);
    }

    public void setIsCrop(boolean isCrop) {
        this.isCrop = isCrop;
    }

    public void setIsBlur(boolean isBlur, int radius) {
        this.isBlur = isBlur;
        this.mBlurRadius = radius;
    }

    public void setMaxTextureSize(int maxTextureSize) {
        setMaxTextureSize(maxTextureSize, true);
    }

    public void setIsCacheHigh(boolean isHigh) {
        isCacheHigh = isHigh;
    }

    public void setResizePipeLine(boolean resize) {
        this.isResize = resize;
    }

    public void setMaxTextureSize(int maxTextureSize, boolean cropIfBigger) {
        mMaxTextureSize = maxTextureSize;
        mMaxTextureSizeCrop = cropIfBigger;
    }

    public void setIsTransform(boolean isEnable) {
        isTransform = isEnable;
    }

    public void setIsLoadingColor(boolean isEnable) {
        isLoadingColor = isEnable;
    }

    public void setFrescoScaleType(ScalingUtils.ScaleType scaleType)
    {
        mScaleType = scaleType;
    }

    private float dpToPixel(float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getContext().getResources().getDisplayMetrics());
    }

    private int mFadeDuration = 300;
    public void setFadeDuration(int fadeduration)
    {
        mFadeDuration = fadeduration;
    }

    private boolean isAutoRotate;
    public void setAutoRotateEnabled(boolean isEnable)
    {
        isAutoRotate = isEnable;
    }

    private ResizeOptions mResizeOptions;
    public void setResizeOption(int width, int height)
    {
        mResizeOptions = new ResizeOptions(width,height);
    }

    protected boolean isEnableFresco;
    public void setEnableFresco(boolean noshow)
    {
        isEnableFresco = noshow;
    }

    @Override
    public void setImageResource(int resId) {
        setEnableFresco(false);
        super.setImageResource(resId);
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        setEnableFresco(false);
        super.setImageBitmap(bm);
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        setEnableFresco(false);
        super.setImageDrawable(drawable);
    }

    public void setStrokeColor(int strokeColor) {
        mStrokeColor = strokeColor;

        GenericDraweeHierarchy hierarchy = mDraweeHolder.getHierarchy();
        if(hierarchy == null) return;

        RoundingParams params =  hierarchy.getRoundingParams();
        if(params != null)
            params.setBorder(mStrokeColor, dpToPixel(1));
    }

    public void setColorFilter(ColorFilter colorFilter)
    {
        if(isEnableFresco)
            mHierarchy.setActualImageColorFilter(colorFilter);
        else
            super.setColorFilter(colorFilter);
    }
}
