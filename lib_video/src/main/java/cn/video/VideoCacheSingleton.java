package cn.video;

import android.content.Context;
import android.util.Log;

import com.danikula.videocache.HttpProxyCacheServer;
import com.danikula.videocache.file.TotalCountLruDiskUsage;

public class VideoCacheSingleton {

    private volatile static VideoCacheSingleton singleton;  //1:volatile修饰

    private VideoCacheSingleton (){}

    public static VideoCacheSingleton getSingleton() {
        if (singleton == null) {  //2:减少不要同步，优化性能
            synchronized (VideoCacheSingleton.class) {  // 3：同步，线程安全
                if (singleton == null) {
                    singleton = new VideoCacheSingleton();  //4：创建singleton 对象
                }
            }
        }
        return singleton;
    }


    private HttpProxyCacheServer proxy;

    public HttpProxyCacheServer getProxy(Context context) {
        Log.i("HttpProxyCacheServer","HttpProxyCacheServer getProxy");
        return proxy == null ? (proxy = newProxy(context)) : proxy;
    }

    private HttpProxyCacheServer newProxy(Context context) {
        Log.i("HttpProxyCacheServer","HttpProxyCacheServer newProxy");
        return new HttpProxyCacheServer.Builder(context)
                .maxCacheFilesCount(10)
                .maxCacheSize(1024 * 1024 * 1024)// 1 Gb for cache
                .fileNameGenerator(new MyFileNameGenerator())
                .diskUsage(new TotalCountLruDiskUsage(10))
                .build();
    }

    public void release(){
        if (proxy != null) {
            proxy.shutdown();
        }
    }


}
