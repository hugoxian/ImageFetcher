ImageFetcher
============

功能描述：

Android的取图片组件，优化了取图片的速度，加入了二级内存，和SD卡缓存块

用法:

将imagefetcher.jar，加入到程序中，在Application或者需要用到加载图片的Activity中调用

ImageFetcher.init(CACHDIR, cacheSize); 其中CACHEDIR是SD卡的缓存路径，cacheSize是内存中硬引用的大小，应根据具体应用做调整

如果还需要在ListView或者GridView中用到图片加载，还应在用户滚动的时候，ImageFetcher应停止加载没必要的图片，进一步提高滑动效果

给ListView/GridView添加一个监听器，在滚动时调用：ImageFetcher.getInstance().lock(); 停止后调用：ImageFetcher.getInstance.unlock();

效果图：

注意点：
在使用ImageFetcher.getInstance()前应先调用ImageFetcher.init(CACHDIR,cacheSize)初始化，直接取ImageFetcher.getInstance()将返回null！




