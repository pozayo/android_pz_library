/**
 * 
 */
package com.pukza.plibrary.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings;

import java.net.HttpCookie;
import java.util.List;
import java.util.Map;

/**
 * @desc 
 * 
 *
 * @author hwaseopchoi
 * @date 2015. 5. 18.
 *
 */
public class PozaNetworkUtil {
    public static final int NETWORK_STATUS_NONE = 0;
    public static final int NETWORK_STATUS_CONNECTED = 1;
    public static final int NETWORK_STATUS_NOT_CONNECTED = 2;
    public static final int NETWORK_STATUS_AIR_PLAIN_MODE = 3;
    public static final int NETWORK_STATUS_BLOCK_NETWORK = 4;

    public static String SP_KEY_COOKIE = "cookies";


    /**
     * 네트워크 상태 체크
     */
    public static int checkNetworkStatus(Context context) {
        if(context == null)
            return NETWORK_STATUS_NONE;

        ConnectivityManager connectivityManager = (ConnectivityManager) context
            .getSystemService(Context.CONNECTIVITY_SERVICE);
        
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null) {
            
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                return NETWORK_STATUS_CONNECTED;
            }else 
            {

            }

            if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                return NETWORK_STATUS_CONNECTED;
            }
        }


        try {
            if (isAirplaneModeOn(context)) {
                return NETWORK_STATUS_AIR_PLAIN_MODE;
            }
        } catch (Exception e) {

        }


        return NETWORK_STATUS_BLOCK_NETWORK;
    }
    
    

    public static void saveCookie(Context context, String _key, String _value) {
        SharedPreferences pref = context.getSharedPreferences(SP_KEY_COOKIE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(_key, _value);
        editor.commit();
    }
    
    @SuppressWarnings("deprecation")
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static boolean isAirplaneModeOn(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return Settings.System.getInt(context.getContentResolver(),
                    Settings.System.AIRPLANE_MODE_ON, 0) != 0;
        } else {
            return Settings.Global.getInt(context.getContentResolver(),
                    Settings.Global.AIRPLANE_MODE_ON, 0) != 0;
        }       
    }

    
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
	public static void saveCookies(Context context, List<HttpCookie> list) {
        if (list == null)
            return;

        SharedPreferences pref = context.getSharedPreferences(SP_KEY_COOKIE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        for (HttpCookie cookie : list) {
            editor.putString(cookie.getName(), cookie.getValue());
        }

        editor.commit();
        
    }

    public static void clearCookie(Context context)
    {
        SharedPreferences pref = context.getSharedPreferences(SP_KEY_COOKIE, Context.MODE_PRIVATE);
        pref.edit().clear().commit();
    }
    
    public static String getCookie(Context context, String key) {
        SharedPreferences pref = context.getSharedPreferences(SP_KEY_COOKIE, Context.MODE_PRIVATE);
        return pref.getString(key, null);
    }

    public static Map<String, ?> getCookieKeys(Context _context) {
        SharedPreferences pref = _context.getSharedPreferences(SP_KEY_COOKIE, Context.MODE_PRIVATE);
        return pref.getAll();
    }
}
