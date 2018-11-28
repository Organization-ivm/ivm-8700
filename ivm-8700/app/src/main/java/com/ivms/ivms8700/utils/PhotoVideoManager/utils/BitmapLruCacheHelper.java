package com.ivms.ivms8700.utils.PhotoVideoManager.utils;

/**
 * Created by cxy on 2017/2/17.
 */

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.util.Log;

/**
 * @author Rowand jj
 *
 *使用lrucache缓存图片到内存，做成了单例模式
 */
public class BitmapLruCacheHelper
{
    private static final String TAG = null;
    private static BitmapLruCacheHelper instance = new BitmapLruCacheHelper();
    private LruCache<String,Bitmap> cache = null;
    private BitmapLruCacheHelper()
    {
        int maxSize = (int) (Runtime.getRuntime().maxMemory()/8);
        cache = new LruCache<String, Bitmap>(maxSize){
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getRowBytes()*value.getHeight();
            }
        };
    }

    /**
     *加入缓存
     * @param key
     * @param value
     */
    public void addBitmapToMemCache(String key,Bitmap value)
    {
        if(key == null || value == null)
        {
            return;
        }
        if(cache!=null && getBitmapFromMemCache(key)==null)
        {
            cache.put(key, value);
            Log.i(TAG,"put to lrucache success");
        }
    }

    /**
     * 从缓存中获取图片
     * @param key
     * @return
     */
    public Bitmap getBitmapFromMemCache(String key)
    {
        if(key == null)
        {
            return null;
        }
        Bitmap bitmap = cache.get(key);
        Log.i(TAG,"from lrucache,bitmap="+bitmap);
        return bitmap;
    }

    /**
     * 获取实例
     * @return
     */
    public static BitmapLruCacheHelper getInstance()
    {
        return instance;
    }
}