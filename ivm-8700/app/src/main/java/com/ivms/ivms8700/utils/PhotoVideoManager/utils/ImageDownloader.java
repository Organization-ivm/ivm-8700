package com.ivms.ivms8700.utils.PhotoVideoManager.utils;

/**
 * Created by cxy on 2017/2/17.
 */

 import java.io.ByteArrayOutputStream;
 import java.io.File;
 import java.io.InputStream;
 import java.net.HttpURLConnection;
 import java.net.URL;
 import java.util.concurrent.ExecutorService;
 import java.util.concurrent.Executors;

 import android.content.Context;
 import android.graphics.Bitmap;
 import android.graphics.BitmapFactory;
 import android.os.Handler;
 import android.os.Message;
 import android.util.Log;

/**
 * @author Rowand jj
 *下载图片的工具类
 *    通过downloadImage方法下载图片，并将图片保存到缓存中（使用线程池）。对下载得到的图片交由一个回调接口OnImageDownloadListener处理
 *    通过showCacheImage方法获取缓存中的图片
 */
public class ImageDownloader
{
    /**
     * 下载image的线程池
     */
    private ExecutorService mImageThreadPool = null;

    /**
     * 文件缓存的工具类
     */
    private FileCacheUtils fileCacheUtils = null;

    /**
     * 线程池中线程的数量
     */
    private static final int THREAD_NUM = 2;

    /**
     * 缩略图的宽
     */
    private static final int REQ_WIDTH = 90;
    /**
     * 缩略图的高
     */
    private static final int REQ_HEIGHT = 90;

    protected static final int DOWNLOAD = 1;

    private Context context;

    /**
     * 构造器
     * @param context
     */
    public ImageDownloader(Context context)
    {
        this.context = context;
        fileCacheUtils = new FileCacheUtils(context);
    }

    /**
     * 下载一张图片，先从内存缓存中找，如果没有则去文件缓存中找，如果还没有就从网络中下载
     * @param url
     * @param listener
     * @return
     */
    public Bitmap downloadImage(final String url,final OnImageDownloadListener listener)
    {
        final String subUrl = url.replaceAll("[^\\w]", "");
        Bitmap bitmap = showCacheBitmap(subUrl);
        if(bitmap!=null)//缓存中找到
        {
            return bitmap;
        }else//缓存中未找到，则开启线程下载
        {
//            new AsyncTask<string, bitmap="">()
//            {
//                @Override
//                protected Bitmap doInBackground(String... params)
//                {
//                    Bitmap bitmap = getImageFromUrl(url);//从网络上下载图片
//                    fileCacheUtils.addBitmapToFile(subUrl,bitmap);//加到文件缓存
//                    BitmapLruCacheHelper.getInstance().addBitmapToMemCache(subUrl, bitmap);//加到内存缓存
//                    return bitmap;
//                }
//                protected void onPostExecute(Bitmap result)
//                {
//                    listener.onImageDownload(url, result);
//                }
//            }.execute(url);

            final Handler handler = new Handler()
            {
                @Override
                public void handleMessage(Message msg)
                {
                    if(msg.what == DOWNLOAD)
                    {
                        listener.onImageDownload(url,(Bitmap)msg.obj);//对下载后的图片的操作交由listener实现类处理
                    }
                }
            };
            getThreadPool().execute(new Runnable()//从线程池中获取一个线程执行下载操作并将下载后的图片加到文件缓存和内存缓存
            {
                @Override
                public void run()
                {
                    Bitmap bitmap = getImageFromFile(url);//从网络上下载图片
                    Message msg = Message.obtain(handler, DOWNLOAD, bitmap);
                    msg.sendToTarget();//发送消息

                    //加到缓存中
                    fileCacheUtils.addBitmapToFile(subUrl,bitmap);
                    BitmapLruCacheHelper.getInstance().addBitmapToMemCache(subUrl, bitmap);
                }
            });

        }
        return null;
    }

    /**
     * 显示缓存中的图片
     * @param url
     * @return
     */
    public Bitmap showCacheBitmap(String url)
    {
        Bitmap bitmap = BitmapLruCacheHelper.getInstance().getBitmapFromMemCache(url);
        if(bitmap!=null)//首先从内存缓存中找
        {
            return bitmap;
        }else
        {
            bitmap = fileCacheUtils.getBitmapFromFile(url);
            if(bitmap!=null)//在文件缓存中找到
            {
                BitmapLruCacheHelper.getInstance().addBitmapToMemCache(url, bitmap);//加入内存缓存
                return bitmap;
            }
        }
        return null;
    }
    /**
     * 获取线程池实例
     */
    public ExecutorService getThreadPool()
    {
        if (mImageThreadPool == null)
        {
            synchronized (ExecutorService.class)
            {
                if (mImageThreadPool == null)
                {
                    mImageThreadPool = Executors.newFixedThreadPool(THREAD_NUM);
                }
            }
        }
        return mImageThreadPool;
    }

    /**
     * 从url中获取bitmap
     * @param url
     * @return
     */
    public Bitmap getImageFromUrl(String url)
    {
        HttpURLConnection conn = null;
        try
        {
            URL target = new URL(url);
            conn = (HttpURLConnection) target.openConnection();
            conn.setReadTimeout(3000);
            conn.setConnectTimeout(10 * 1000);
            conn.setDoInput(true);

            if (conn.getResponseCode() == 200)
            {
                InputStream is = conn.getInputStream();
                ByteArrayOutputStream bout = new ByteArrayOutputStream();
                int len = 0;
                byte[] buf = new byte[1024];
                while((len = is.read(buf))!=-1)
                {
                    bout.write(buf, 0, len);
                }
                is.close();
                byte[] data = bout.toByteArray();
                return BitmapUtils.decodeSampledBitmapFromByteArray(data,REQ_WIDTH, REQ_HEIGHT);//返回的是压缩后的缩略图
            }

        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }




    public Bitmap getImageFromFile(String path)
    {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.outHeight = 200;
        opts.outWidth = 200;
        File file = new File(path);
        if (!file.exists()){
            Log.e("tag","===file is not exists===");
        }
        Bitmap bitmap = BitmapFactory.decodeFile(file.toString(), opts);
        Bitmap bitmaps = Bitmap.createScaledBitmap(bitmap, 150, 150, false);
        return bitmaps;//返回的是压缩后的缩略图

    }

    /**
     * 取消当前的任务
     */
    public synchronized void cancellTask()
    {
        if(mImageThreadPool != null)
        {
            mImageThreadPool.shutdownNow();
            mImageThreadPool = null;
        }
    }
    /**
     *操作下载后的图片的回调接口
     */
    public interface OnImageDownloadListener
    {
        void onImageDownload(String url, Bitmap bitmap);
    }
}
