package com.hugo.image.fetcher.plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

/**
 * 图片获取者，单例
 * 
 * @author hugo
 * 
 */
public class ImageFetcher {

	private final static String TAG = "ImageFetcher";
	/**
	 * 
	 */
	private static ImageFetcher instance;
	/**
	 * 执行取图片任务的线程池
	 */
	private ExecutorService executorService;

	private List<Fetchable> fetchers;

	/**
	 * 是否锁住，如果锁住，则不允许加载图片
	 */
	private boolean isLock;

	private Map<String, ImageView> tasks;

	/**
	 * 
	 * @param sdPath
	 *            SDCache缓存的路径
	 * @param cacheSize
	 *            硬引用缓存的大小
	 */
	private ImageFetcher(String sdPath, int cacheSize) {
		// 取得CPU的核数
		int cpuCount = Runtime.getRuntime().availableProcessors();
		// 也可以根据网络状况，譬如wifi，gprs等决定初始化线程池数
		// 根据CPU的核数初始化线程池
		// this.executorService = Executors.newFixedThreadPool(cpuCount + 1);
		// 单线程池，主要是为了观察效果，测试用
		this.executorService = Executors.newSingleThreadExecutor();
		this.fetchers = new ArrayList<Fetchable>();
		// 加载三个图片加载器，注意顺序，优先从内存缓存中取，其次SD卡缓存中存，最后从网络中获取
		this.fetchers.add(new MemoryCahceFetcher(cacheSize));
		this.fetchers.add(new SDFileFetcher(sdPath));
		this.fetchers.add(new NetworkFetcher());
		// 可能出现几个view同时请求图片，故用ConcurrentHashMap
		tasks = new ConcurrentHashMap<String, ImageView>();
	};

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if (msg.obj != null) {
				ImageInfoHolder holder = (ImageInfoHolder) msg.obj;
				if (holder.bitmap != null) {
					holder.imageView.setImageBitmap(holder.bitmap);
				}
			}
		}
	};

	/**
	 * 带参数取得实例，加上锁，以免多线程同时调用，造成多个实例的存在
	 * 
	 * @param context
	 * @param sdPath
	 */
	public static ImageFetcher init(String sdPath, int cacheSize) {
		synchronized (ImageFetcher.class) {
			if (instance == null) {
				instance = new ImageFetcher(sdPath, cacheSize);
			}
		}
		return instance;
	}

	/**
	 * 取得ImageFetcher的实例对象，可能返回null
	 * 
	 * @param context
	 * @return
	 */
	public static ImageFetcher getInstance() {
		if (instance == null) {
			Log.w(TAG, "Please init the ImageFetcher before use it!");
		}
		return instance;
	}

	/**
	 * ImageFetcher销毁
	 */
	public static void destory() {
		if (instance != null) {
			instance.clear();
			instance = null;
		}
	}

	/**
	 * 清理工作
	 */
	private void clear() {
		this.executorService.shutdown();
		this.fetchers.clear();
	}

	/**
	 * 锁住
	 */
	public void lock() {
		this.isLock = true;
	}

	/**
	 * 解锁
	 */
	public void unlock() {
		this.isLock = false;
		doTasks();
	}

	/**
	 * 加载一个取图片任务
	 * 
	 * @param imageView
	 * @param photoUrl
	 */
	public void addTask(ImageView imageView, String photoUrl) {
		// 传入数据不合法，为null则立即返回
		if (imageView == null || photoUrl == null) {
			return;
		}

		imageView.setTag(photoUrl);
		tasks.put(Integer.toString(imageView.hashCode()), imageView);

		// 如果不是锁住的话，立即加载图片
		if (!this.isLock) {
			doTasks();
		}
	}

	/**
	 * 执行加载图片任务
	 */
	public synchronized void doTasks() {
		for (Map.Entry<String, ImageView> entry : tasks.entrySet()) {
			ImageInfoHolder holder = new ImageInfoHolder();
			holder.imageView = entry.getValue();
			holder.photoUrl = (String) entry.getValue().getTag();
			this.executorService.execute(new FetchRunnable(holder));
		}
	}

	/**
	 * 在取图片整个任务中传输的对象
	 * 
	 * @author hugo
	 * 
	 */
	class ImageInfoHolder {
		ImageView imageView;
		String photoUrl;
		Bitmap bitmap;
	}

	/**
	 * 取图片Runnable
	 * 
	 * @author hugo
	 * 
	 */
	class FetchRunnable implements Runnable {
		private ImageInfoHolder holder;

		FetchRunnable(ImageInfoHolder holder) {
			this.holder = holder;
		}

		@Override
		public void run() {
			Bitmap bitmap = null;
			// 记录取得图片的Fetch的下标
			int fetchIndex = 0;
			for (int i = 0; i < fetchers.size(); i++) {
				Fetchable fetcher = fetchers.get(i);
				try {
					bitmap = fetcher.fetch(this.holder.photoUrl);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (bitmap != null) {
					fetchIndex = i;
					break;
				}
			}

			if (bitmap == null) {
				return;
			}

			// 在下标fetchIndex之前的Fetcher都得执行存缓存动作
			for (int i = 0; i < fetchers.size(); i++) {
				if (i == fetchIndex) {
					break;
				}
				Fetchable fetcher = fetchers.get(i);
				try {
					fetcher.cache(this.holder.photoUrl, bitmap);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			if (bitmap != null) {
				this.holder.bitmap = bitmap;
				Message msg = mHandler.obtainMessage();
				msg.obj = this.holder;
				mHandler.sendMessage(msg);
			}

		}
	}

}
