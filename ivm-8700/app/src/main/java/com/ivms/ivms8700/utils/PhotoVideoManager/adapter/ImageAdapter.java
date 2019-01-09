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


import com.bumptech.glide.Glide;
import com.ivms.ivms8700.R;
import com.ivms.ivms8700.control.MyApplication;
import com.ivms.ivms8700.utils.PhotoVideoManager.bean.Bean;
import com.ivms.ivms8700.utils.PhotoVideoManager.utils.ImageDownloader;

import java.util.List;

public class ImageAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private List<Bean> items;
    private Bean bean;

    public void setItems(List<Bean> items) {
        this.items = items;
    }
//

    public ImageAdapter(Context context,List<Bean> list) {
        this.items = list;
        inflater = LayoutInflater.from(context);
        Log.i("tag", "===adapter===" + list.size());
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
            holder.img = convertView.findViewById(R.id.imageView1);
            holder.cb = convertView.findViewById(R.id.checkBox);
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
        String imageUrl = "" + bean.getFilePath();
        Glide.with(MyApplication.getIns()).load(imageUrl).into(holder.img);
        holder.cb.setChecked(bean.isChecked());
        return convertView;
    }


    static class ViewHolder {
        ImageView img;
        CheckBox cb;
    }
}
