package com.ivms.ivms8700.utils.PhotoVideoManager.utils;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Environment;
import android.webkit.MimeTypeMap;


import com.ivms.ivms8700.control.MyApplication;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class PhotoUtils {
    /**
     * 保存图片到SD卡
     * * @author YOLANDA
     * * @param isInsertGallery 是否保存到图库
     * * @return
     */
    public static String saveImg(Bitmap bmp) {
        File myappDir = new File(Environment.getExternalStorageDirectory(), "test");
        if (myappDir.exists() && myappDir.isFile()) {
            myappDir.delete();
        }
        if (!myappDir.exists()) {
            myappDir.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".png";
        File file = new File(myappDir, fileName);
        if (file.exists()) {
            file.delete();
        }

        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 50, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return file.getAbsolutePath();
    }
    public static String  savePhoto(Bitmap bmp){

        String path =  saveImg(bmp);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            MediaScanner mediaScanner = new MediaScanner(MyApplication.getIns());
            String[] filePaths = new String[]{path};
            String[] mimeTypes = new String[]{MimeTypeMap.getSingleton().getMimeTypeFromExtension("png")};
            mediaScanner.scanFiles(filePaths, mimeTypes);
        } else {
        }
        return path;
    }
}
