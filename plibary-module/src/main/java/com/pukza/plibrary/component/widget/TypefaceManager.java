/**
 * 
 */
package com.pukza.plibrary.component.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.util.SparseArray;

/**
 * @desc 
 * 
 *
 * @author hwaseopchoi
 * @date 2015. 5. 21.
 *
 */
public class TypefaceManager {

    public final static int DROID_SANS = 0;
    public final static int ROBOTO_THIN_ITALIC = 1;
    public final static int ROBOTO_LIGHT = 2;
 
    private final static SparseArray<Typeface> mTypefaces = new SparseArray<Typeface>(20);

    public static Typeface obtaintTypeface(Context context, int typefaceValue) throws IllegalArgumentException {
        Typeface typeface = mTypefaces.get(typefaceValue);
        if (typeface == null) {
            typeface = createTypeface(context, typefaceValue);
            mTypefaces.put(typefaceValue, typeface);
        }
        return typeface;
    }

    private static Typeface createTypeface(Context context, int typefaceValue) throws IllegalArgumentException {
        Typeface typeface;
        switch (typefaceValue) {
            case DROID_SANS:
                typeface = Typeface.createFromAsset(context.getAssets(), "fonts/DroidSansFallback.ttf");
                break;
            case ROBOTO_THIN_ITALIC:
                typeface = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Black.ttf");
                break;
            case ROBOTO_LIGHT:
                typeface = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Light.ttf");
                break;
            default:
                throw new IllegalArgumentException("Unknown `typeface` attribute value " + typefaceValue);
        }
         
        typeface = Typeface.DEFAULT;
        
        
        return typeface;
    }

}