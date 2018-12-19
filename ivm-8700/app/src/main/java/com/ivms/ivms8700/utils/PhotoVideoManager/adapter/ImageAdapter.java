package com.ivms.ivms8700.utils.PhotoVideoManager.adapter;

/**
 * Created by cxy on 2017/2/17.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ImageView;


import com.ivms.ivms8700.R;
import com.ivms.ivms8700.utils.PhotoVideoManager.bean.Bean;
import com.ivms.ivms8700.utils.PhotoVideoManager.utils.ImageDownloader;

import java.util.ArrayList;
import java.util.List;

public class ImageAdapter extends BaseAdapter implements OnScrollListener {
    private GridView gridView;
    private Context context;
    private List<String> imageThumUrls = new ArrayList<>();
    private ImageDownloader mImageDownloader;
    private boolean isFirstEnter = true;
    private int mFirstVisibleItem;
    private int mVisibleItemCount;

    private LayoutInflater inflater;
    private List<Bean> items;
    private Bean bean;
    private OnShowItemClickListener onShowItemClickListener;

    public void setItems(List<Bean> items) {
        this.items = items;
    }

    public List<String> getImageThumUrls() {
        return imageThumUrls;
    }

    public void setImageThumUrls(List<String> imageThumUrls) {
        this.imageThumUrls = imageThumUrls;
    }

    public ImageAdapter(Context context, List<String> paths, GridView gridView, List<Bean> list) {
        this.items = list;
        Log.i("tag", "===adapter===" + list.size());
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.gridView = gridView;
        this.imageThumUrls = paths;
        this.mImageDownloader = new ImageDownloader(context);
        gridView.setOnScrollListener(this);
    }


    @Override
    public int getCount() {
        return items.size();
    }


    @Override
    public Bean getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_photo, null);
            holder.img = (ImageView) convertView.findViewById(R.id.imageView1);
            holder.cb = (CheckBox) convertView.findViewById(R.id.checkBox);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.cb.setClickable(false);
        holder.cb.setEnabled(false);
        bean = items.get(position);
        if (bean.isShow()) {
            holder.cb.setVisibility(View.VISIBLE);
        } else {
            holder.cb.setVisibility(View.GONE);
        }
        try {
            String imageUrl = "" + imageThumUrls.get(position);
            //只显示缓存图片，如果缓存中没有则设置一张默认的图片
            Bitmap bitmap = mImageDownloader.showCacheBitmap(imageUrl.replaceAll("[^\\w]", ""));
            if (bitmap != null) {
//        Glide.with(context).load(imageUrl).into(holder.img);
                holder.img.setImageBitmap(bitmap);
            } else {
                holder.img.setImageBitmap(bitmap);
//            holder.img.setImageResource(R.mipmap.ic_launcher);
            }
        }catch (Exception e){

            Log.i("ImageAdapter","crach------");
        }
//        holder.cb.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (holder.cb.isChecked()) {
//                    bean.setChecked(true);
//                } else {
//                    bean.setChecked(false);
//                }
//                onShowItemClickListener.onShowItemClick(position,bean, holder.cb.isChecked());
//            }
//        });

//        holder.cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
//
//
//            }
//        });
        holder.cb.setChecked(bean.isChecked());
        return convertView;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState == OnScrollListener.SCROLL_STATE_IDLE)//滑动停止时启动下载图片
        {
            showImage(mFirstVisibleItem, mVisibleItemCount);
        } else {
            cancellTask();
        }
    }

    /**
     * 滚动时执行此方法
     * 第一次进入会调用showImage显示图片
     */
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        mFirstVisibleItem = firstVisibleItem;
        mVisibleItemCount = visibleItemCount;

        if (isFirstEnter && visibleItemCount > 0) {
            showImage(firstVisibleItem, visibleItemCount);
            isFirstEnter = false;
        }
    }

    /**
     * 显示图片，先从缓存中找，如果没找到就开启线程下载
     *
     * @param firstVisibleItem 第一个可见项的id
     * @param visibleItemCount 可见项的总数
     */
    private void showImage(int firstVisibleItem, int visibleItemCount) {
        for (int i = firstVisibleItem; i < firstVisibleItem + visibleItemCount; i++) {
            String mImageUrl = "" + imageThumUrls.get(i);
            final ImageView mImageView = (ImageView) gridView.findViewWithTag(mImageUrl);
            mImageDownloader.downloadImage(mImageUrl, new ImageDownloader.OnImageDownloadListener() {
                @Override
                public void onImageDownload(String url, Bitmap bitmap) {
                    if (mImageView != null && bitmap != null) {
                        mImageView.setImageBitmap(bitmap);//下载后直接设置到view对象上
                    }
                }
            });
        }
    }

    public void clearData() {
        imageThumUrls.clear();
    }

    /**
     * 取消下载任务
     */
    public void cancellTask() {
        mImageDownloader.cancellTask();
    }

    public interface OnShowItemClickListener {
        public void onShowItemClick(int position, Bean bean, boolean checked);
    }

    public void setOnShowItemClickListener(OnShowItemClickListener onShowItemClickListener) {
        this.onShowItemClickListener = onShowItemClickListener;
    }

    static class ViewHolder {
        ImageView img;
        CheckBox cb;
    }
}
