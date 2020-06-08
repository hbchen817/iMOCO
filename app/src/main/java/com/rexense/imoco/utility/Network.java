package com.rexense.imoco.utility;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Creator: xieshaobing
 * creat time: 2020-03-31 15:29
 * Description: 网络
 */
public class Network {
	// 获取http位图
	public static Bitmap getHttpBitmap(String url) throws IOException {
		URL myFileUrl = null;
		Bitmap bitmap = null;
		InputStream is = null;
		try {
			myFileUrl = new URL(url);
		} catch (MalformedURLException e) {
			Logger.e("Failed to get product image, the reason is: \r\n" + e.getMessage());
			return null;
		}
		try {
			HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
			conn.setConnectTimeout(0);
			conn.setDoInput(true);
			conn.connect();
			is = conn.getInputStream();
			bitmap = BitmapFactory.decodeStream(is);
		} catch (IOException e) {
			Logger.e("Failed to get product image, the reason is: \r\n" + e.getMessage());
			return null;
		}
		finally {
			if(is != null){
				is.close();
			}
		}
		return bitmap;
	}
}
