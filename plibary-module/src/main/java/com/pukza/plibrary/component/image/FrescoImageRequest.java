package com.pukza.plibrary.component.image;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author created by hwaseopchoi
 * @date on 2016. 1. 8.
 */

public class FrescoImageRequest {
    	public static Bitmap getBitmapFromURL(String src) {
		HttpURLConnection connection = null;
		try {
			URL url = new URL(src);

			connection = (HttpURLConnection) url.openConnection();
			connection.setDoInput(true);
			connection.connect();

			InputStream input = connection.getInputStream();

			Bitmap myBitmap = BitmapFactory.decodeStream(input);

			return myBitmap;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} finally {
			if (connection != null)
				connection.disconnect();
		}
	}
}
