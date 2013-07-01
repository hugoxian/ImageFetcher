package com.hugo.image.fetcher;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.hugo.image.fetcher.plugin.ImageFetcher;

/**
 * 首个Activity
 * 
 * @author hugo
 * 
 */
public class MainActivity extends Activity {

	private static final String CACHDIR = "ImageFetcher/temp";

	private ListView mListView;
	private BaseAdapter mAdapter;
	private ImageFetcher mImageFetcher;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mListView = (ListView) this.findViewById(R.id.listView);
		mAdapter = new ListViewAdapter(this.getItems(), this);
		mListView.setAdapter(mAdapter);

		// 取得程序可用内存大小
		int memorySize = ((ActivityManager) this
				.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
		// 硬引用缓存容量，这里的参数根据应用本身环境重新设定，这里设定为为1/8
		System.out.println("memorySize" + memorySize);
		int cacheSize = 1024 * 1024 * memorySize / 2;
		mImageFetcher = ImageFetcher.init(CACHDIR, cacheSize);

		/**
		 * 当list滚动是锁住，停止是解锁，在用户快速滚动是省去没必要的图片加载
		 */
		mListView.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				switch (scrollState) {
				case OnScrollListener.SCROLL_STATE_FLING: {
					mImageFetcher.lock();
					break;
				}
				case OnScrollListener.SCROLL_STATE_IDLE: {
					mImageFetcher.unlock();
					break;
				}
				case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL: {
					mImageFetcher.lock();
					break;
				}
				default: {
					break;
				}
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {

			}

		});
	}

	/**
	 * 
	 * @return
	 */
	private List<ListItemVo> getItems() {

		String[] urls = new String[] {
				"http://e.hiphotos.baidu.com/album/w%3D230/sign=ec156b4e9e3df8dca63d8892fd1072bf/c995d143ad4bd113ef2ad28b5bafa40f4afb05a7.jpg",
				"http://a.hiphotos.baidu.com/album/w%3D230/sign=33a0e6cc42a98226b8c12c24ba83b97a/562c11dfa9ec8a13bb433359f603918fa0ecc02a.jpg",
				"http://a.hiphotos.baidu.com/album/w%3D230/sign=26301abfadaf2eddd4f14eeabd110102/ca1349540923dd540677c3d1d009b3de9c824806.jpg",
				"http://f.hiphotos.baidu.com/album/w%3D230/sign=9690e152500fd9f9a017526a152dd42b/adaf2edda3cc7cd9f078faf73801213fb80e9143.jpg",
				"http://e.hiphotos.baidu.com/album/w%3D230/sign=9d962293b151f819f1250449eab54a76/58ee3d6d55fbb2fbe792fcaa4e4a20a44723dcc9.jpg",
				"http://g.hiphotos.baidu.com/album/w%3D230/sign=bccaea7c0bd162d985ee651f21dea950/500fd9f9d72a60599bef45a22934349b023bba8f.jpg",
				"http://a.hiphotos.baidu.com/album/w%3D310/sign=ff92e2098b82b9013dadc532438da97e/8b13632762d0f703a1e15cf209fa513d2697c549.jpg",
				"http://t12.baidu.com/it/u=3465439355,3905271308&fm=59",
				"http://t11.baidu.com/it/u=3255259780,3122740905&fm=59",
				"http://t11.baidu.com/it/u=623520238,1599092840&fm=59",
				"http://t10.baidu.com/it/u=2763433320,3047781300&fm=59",
				"http://t11.baidu.com/it/u=417395623,493879816&fm=59",
				"http://t11.baidu.com/it/u=2268787663,466819174&fm=59",
				"http://t12.baidu.com/it/u=2247765677,1967115923&fm=59",
				"http://t10.baidu.com/it/u=3382898826,3889258074&fm=59",
				"http://b.hiphotos.baidu.com/album/w%3D310/sign=e29254b02fdda3cc0be4be2131e83905/ca1349540923dd54ffea6a47d009b3de9c824800.jpg",
				"http://t1.baidu.com/it/u=2976002976,3625039180&fm=23&gp=0.jpg",
				"http://t3.baidu.com/it/u=435600663,811823342&fm=21&gp=0.jpg",
				"http://t3.baidu.com/it/u=2597338825,3060554915&fm=21&gp=0.jpg",
				"http://t2.baidu.com/it/u=1441910787,4169500706&fm=21&gp=0.jpg",
				"http://t1.baidu.com/it/u=3406736270,1163497769&fm=23&gp=0.jpg",
				"http://t2.baidu.com/it/u=1144071234,2227445662&fm=21&gp=0.jpg",
				"http://t3.baidu.com/it/u=1135115870,371233137&fm=21&gp=0.jpg",
				"http://t1.baidu.com/it/u=3417496042,1937162607&fm=21&gp=0.jpg",
				"http://t1.baidu.com/it/u=2198072405,2396178938&fm=21&gp=0.jpg",
				"http://t2.baidu.com/it/u=2037646477,984993186&fm=21&gp=0.jpg",
				"http://t3.baidu.com/it/u=2754639423,2522281398&fm=21&gp=0.jpg",
				"http://t3.baidu.com/it/u=255816207,181242671&fm=23&gp=0.jpg" };

		List<ListItemVo> items = new ArrayList<ListItemVo>();

		for (int i = 0; i < 1000; i++) {
			int yushu = i % urls.length;
			ListItemVo item = new ListItemVo();
			item.setTitle("这里是标题" + (i + 1));
			item.setContent("这里是内容");
			if(i==25){
				System.out.println(urls[yushu]);
			}
			item.setPhotoUrl(urls[yushu]);
			items.add(item);
		}

		return items;
	}

	class ListViewAdapter extends BaseAdapter {
		/**
		 * list元素列表
		 */
		private List<ListItemVo> items;
		private Context context;
		private LayoutInflater mInflater;

		private ListViewAdapter(List<ListItemVo> items, Context context) {
			this.items = items;
			this.context = context;
			this.mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			if (this.items == null) {
				return 0;
			}
			return this.items.size();
		}

		@Override
		public Object getItem(int position) {
			return this.items.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.list_item, null);
				convertView.setTag(holder);
				holder.imageView = (ImageView) convertView
						.findViewById(R.id.imageView);
				holder.titleView = (TextView) convertView
						.findViewById(R.id.titleView);
				holder.contentView = (TextView) convertView
						.findViewById(R.id.contentView);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			ListItemVo item = this.items.get(position);
			holder.titleView.setText(item.getTitle());
			holder.contentView.setText(item.getContent());
			holder.imageView.setImageResource(R.drawable.loading);
			// 如果有photoUrl值，则由ImageFetcher去加载
			if (item.getPhotoUrl() != null) {
				mImageFetcher.addTask(holder.imageView, item.getPhotoUrl());
				holder.imageView.setTag(item.getPhotoUrl());
			}
			return convertView;
		}

		/**
		 * 每一个ListItem的子View Holder
		 * 
		 * @author hugo
		 * 
		 */
		class ViewHolder {
			ImageView imageView;
			TextView titleView;
			TextView contentView;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		this.getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

}
