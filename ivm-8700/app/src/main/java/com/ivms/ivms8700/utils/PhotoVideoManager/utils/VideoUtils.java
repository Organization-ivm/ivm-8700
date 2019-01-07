package com.ivms.ivms8700.utils.PhotoVideoManager.utils;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;


import com.ivms.ivms8700.R;
import com.ivms.ivms8700.control.MyApplication;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class VideoUtils {

    private static DisplayMetrics metrics;

    public static List<EntityVideo> getList(Context context) {
        List<EntityVideo> sysVideoList = new ArrayList<>();
        // MediaStore.Video.Thumbnails.DATA:视频缩略图的文件路径
        String[] thumbColumns = {MediaStore.Video.Thumbnails.DATA,
                MediaStore.Video.Thumbnails.VIDEO_ID};
        // 视频其他信息的查询条件
        String[] mediaColumns = {MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DATA, MediaStore.Video.Media.DURATION};

        Cursor cursor = context.getContentResolver().query(MediaStore.Video.Media
                        .EXTERNAL_CONTENT_URI,
                mediaColumns, null, null, null);

        if (cursor == null) {
            return sysVideoList;
        }
        if (cursor.moveToFirst()) {
            do {
                EntityVideo info = new EntityVideo();
                int id = cursor.getInt(cursor
                        .getColumnIndex(MediaStore.Video.Media._ID));
                Cursor thumbCursor = context.getContentResolver().query(
                        MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI,
                        thumbColumns, MediaStore.Video.Thumbnails.VIDEO_ID
                                + "=" + id, null, null);
                if (thumbCursor.moveToFirst()) {
                    info.setThumbPath(thumbCursor.getString(thumbCursor
                            .getColumnIndex(MediaStore.Video.Thumbnails.DATA)));
                }
                info.setPath(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media
                        .DATA)));
                info.setDuration(cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video
                        .Media.DURATION)));
                sysVideoList.add(info);
            } while (cursor.moveToNext());
        }
        return sysVideoList;
    }


    //根据文件路径获取缩略图
    public static Bitmap getBitmapFromFile(String path) {
        /**
         * android系统中为我们提供了ThumbnailUtils工具类来获取缩略图的处理。
         * ThumbnailUtils.createVideoThumbnail(filePath, kind)
         *          创建视频缩略图，filePath:文件路径，kind：MINI_KIND or MICRO_KIND
         * ThumbnailUtils.extractThumbnail(bitmap, width, height)
         *          将bitmap裁剪为指定的大小
         * ThumbnailUtils.extractThumbnail(bitmap, width, height, options)
         *          将bitmap裁剪为指定的大小，可以有参数BitmapFactory.Options参数
         *
         */
        metrics = MyApplication.getIns().getResources().getDisplayMetrics();
        Bitmap bitmap = null;
//        if(infor.type == 0){//若果是图片，即拍照
//            //直接通过路径利用BitmapFactory来形成bitmap
//            bitmap = BitmapFactory.decodeFile(path);
//        }else if(infor.type == 1){//如果是视频，即拍摄视频
//            //利用ThumnailUtils
        bitmap = ThumbnailUtils.createVideoThumbnail(path, MediaStore.Images.Thumbnails.MINI_KIND);
//        }

        //获取图片后，我们队图片进行压缩，获取指定大小
        //裁剪大小
        if (bitmap != null)
            bitmap = ThumbnailUtils.extractThumbnail(bitmap, (int) (100 * metrics.density), (int) (100 * metrics.density));
        else {//如果为空，采用我们的默认图片
            bitmap = BitmapFactory.decodeResource(MyApplication.getIns().getResources(), R.mipmap.ic_launcher);
        }
        return bitmap;
    }


    //获取视频总时长
    public static int getVideoDuration(String path) {
//        MediaMetadataRetriever media = new MediaMetadataRetriever();
//        media.setDataSource(path);
//        String duration = media.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION); //
//        return Integer.parseInt(duration);
        MediaPlayer mediaPlayer = new MediaPlayer();
        int i = 0;
        try {
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();
          i =   mediaPlayer.getDuration();
            Log.i("VideoUtils",i+"");
        } catch (IOException e) {
            Log.i("VideoUtils","cash");
            e.printStackTrace();
        }
        Log.i("VideoUtils",i+"");
        return i;

    }



}
