package com.hugo.image.fetcher.plugin;

import java.lang.ref.SoftReference;
import java.util.LinkedHashMap;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.util.Log;

/**
 * 内存获取图片器
 * 
 * @author hugo
 * 
 */
public class MemoryCahceFetcher implements Fetchable {

	private final static String TAG = "MemoryCahceFetcher";

	/**
	 * 图片软引用缓存
	 */
	private static LinkedHashMap<String, SoftReference<Bitmap>> mSoftCache;
	/**
	 * 软引用缓存容量
	 */
	private static final int SOFT_CACHE_SIZE = 15;
	/**
	 * 硬引用缓存
	 */
	private static LruCache<String, Bitmap> mLruCache;

	/**
	 * 
	 * @param cacheSize
	 *            硬引用内存块的大小
	 */
	public MemoryCahceFetcher(int cacheSize) {

		// 实例化软引用
		mSoftCache = new LinkedHashMap<String, SoftReference<Bitmap>>(
				SOFT_CACHE_SIZE, 0.75f, true) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 2744397547318185618L;

			@Override
			protected boolean removeEldestEntry(
					Entry<String, SoftReference<Bitmap>> eldest) {
				if (size() > SOFT_CACHE_SIZE) {
					return true;
				}
				return false;
			}
		};

		// 实例化硬引用缓存
		mLruCache = new LruCache<String, Bitmap>(cacheSize) {
			@Override
			protected int sizeOf(String key, Bitmap value) {
				if (value != null) {
					return value.getRowBytes() * value.getHeight();
				} else {
					return 0;
				}
			}

			/**
			 * 硬引用缓存满了，自动将缓存转到软引用，必要时给回收
			 */
			@Override
			protected void entryRemoved(boolean evicted, String key,
					Bitmap oldValue, Bitmap newValue) {
				if (oldValue != null) {
					mSoftCache.put(key, new SoftReference<Bitmap>(oldValue));
				}
			}
		};

	}

	@Override
	public Bitmap fetch(String photoUrl) {
		Bitmap bitmap = null;
		// 加锁，先从硬引用中取图片
		synchronized (LruCache.class) {
			bitmap = mLruCache.get(photoUrl);
			if (bitmap != null) {
				mLruCache.remove(photoUrl);
				mLruCache.put(photoUrl, bitmap);
				return bitmap;
			}
		}

		// 接着到软引用缓存中取
		synchronized (SoftReference.class) {
			SoftReference<Bitmap> bitmapReference = mSoftCache.get(photoUrl);
			if (bitmapReference != null) {
				bitmap = bitmapReference.get();
				if (bitmap != null) {
					// 取得新的图片，则放到硬引用中
					mLruCache.put(photoUrl, bitmap);
					mSoftCache.remove(photoUrl);
					return bitmap;
				} else {
					mSoftCache.remove(photoUrl);
				}
			}
		}

		return bitmap;
	}

	@Override
	public void cache(String key, Bitmap bitmap) throws Exception {
		Log.d(TAG, "MemoryCahceFetcher cache");
		if (bitmap != null) {
			synchronized (LruCache.class) {
				mLruCache.put(key, bitmap);
			}
		}
	}

}
