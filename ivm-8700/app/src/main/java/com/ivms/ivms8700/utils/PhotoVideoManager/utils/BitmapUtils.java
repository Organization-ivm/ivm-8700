package com.ivms.ivms8700.utils.PhotoVideoManager.utils;

/**
 * Created by cxy on 2017/2/17.
 */
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author Rowand jj
 * 压缩图片
 */
public class BitmapUtils
{
    /**
     * 根据资源id获取到图片，并进行压缩
     * @param res
     * @param resId
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static Bitmap decodeSampledBitmapFromResource(Resources res,
                                                         int resId, int reqWidth, int reqHeight)
    {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, opts);
        int inSampleSize = cacluateInSampleSize(opts, reqWidth, reqHeight);
        opts.inSampleSize = inSampleSize;
        opts.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeResource(res, resId, opts);
        return bitmap;
    }

    /**
     * 从byte数组中获取图片并压缩
     * @param data
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static Bitmap decodeSampledBitmapFromByteArray(byte[] data,
                                                          int reqWidth, int reqHeight)
    {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(data, 0, data.length, opts);
        int inSampleSize = cacluateInSampleSize(opts, reqWidth, reqHeight);
        opts.inJustDecodeBounds = false;
        opts.inSampleSize = inSampleSize;
        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length,
                opts);
        return bitmap;
    }

    private static int cacluateInSampleSize(BitmapFactory.Options opts,
                                            int reqWidth, int reqHeight)
    {
        if (opts == null)
            return 1;

        int inSampleSize = 1;
        int realWidth = opts.outWidth;
        int realHeight = opts.outHeight;

        if (realHeight > reqHeight || realWidth > reqWidth)
        {
            int heightRatio = realHeight / reqHeight;
            int widthRatio = realWidth / reqWidth;

            inSampleSize = (heightRatio > widthRatio) ? widthRatio
                    : heightRatio;
        }
        return inSampleSize;
    }

    public static void saveToSystemGallery(Context context, Bitmap bmp) {
        // 首先保存图片
        File appDir = new File(Environment.getExternalStorageDirectory(), "test");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 其次把文件插入到系统图库
        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(),
                    file.getAbsolutePath(), fileName, null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // 最后通知图库更新
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse(file.getAbsolutePath())));
    }

}