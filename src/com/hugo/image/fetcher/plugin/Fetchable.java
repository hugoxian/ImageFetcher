package com.hugo.image.fetcher.plugin;

import android.graphics.Bitmap;

/**
 * 可以获取图片的能力接口
 * 
 * @author hugo
 * 
 */
public interface Fetchable {

	/**
	 * 如果非空Bitmap则代表取得成功
	 * 
	 * @param photoUrl
	 * @return
	 */
	public Bitmap fetch(String photoUrl) throws Exception;

	/**
	 * 缓存Bitmap
	 * 
	 * @param key
	 * @param bitmap
	 * @throws Exception
	 */
	public void cache(String key, Bitmap bitmap) throws Exception;

}
