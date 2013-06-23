package com.hugo.image.fetcher.plugin;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * 从网络上取得图片
 * 
 * @author hugo
 * 
 */
public class NetworkFetcher implements Fetchable {

	/**
	 * 链接超时时间
	 */
	private final static int TIMEOUT = 5 * 1000;

	@Override
	public Bitmap fetch(String photoUrl) throws Exception {
		Bitmap result = null;
		// 从网络下载图片
		URL url = new URL(photoUrl);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(TIMEOUT);
		conn.setRequestMethod("GET");
		InputStream is = conn.getInputStream();

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] b = new byte[1024];
		int len = 0;

		while ((len = is.read(b, 0, 1024)) != -1) {
			baos.write(b, 0, len);
			baos.flush();
		}
		byte[] bytes = baos.toByteArray();

		result = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
		return result;
	}

	@Override
	public void cache(String key, Bitmap bitmap) throws Exception {
		// doNothing
	}
}
