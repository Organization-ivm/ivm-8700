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
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import com.ivms.ivms8700.R;
import com.ivms.ivms8700.utils.PhotoVideoManager.VideoInfo;
import java.util.List;

public class VideoAdapter extends BaseAdapter{
    private LayoutInflater inflater;
    private List<VideoInfo> items;
    private VideoInfo bean;

    public void setItems(List<VideoInfo> items) {
        this.items = items;
    }

    public VideoAdapter(Context context, List<VideoInfo> list)
    {
        this.items = list;
        Log.i("tag","===adapter==="+list.size());
        inflater = LayoutInflater.from(context);
    }


    @Override
    public int getCount()
    {
        return items.size();
    }


    @Override
    public VideoInfo getItem(int position)
    {
        return items.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder holder;
        if (convertView==null){
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_video,null);
            holder.img = convertView.findViewById(R.id.imageView1);
            holder.cb = convertView.findViewById(R.id.checkBox);
            holder.tvTime = convertView.findViewById(R.id.tvtime);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder)convertView.getTag();
        }
        holder.cb.setEnabled(false);
        holder.cb.setClickable(false);
        bean = items.get(position);
        if (bean.isShow()){
            holder.cb.setVisibility(View.VISIBLE);
        }else{
            holder.cb.setVisibility(View.GONE);
        }


        Bitmap bitmap = bean.getBitmap();
        //只显示缓存图片，如果缓存中没有则设置一张默认的图片
        if(bitmap != null)
        {
            holder.img.setImageBitmap(bitmap);
        }else
        {
            holder.img.setImageResource(R.mipmap.ic_launcher);
        }
        holder.tvTime.setText(bean.getLastModifed());
        holder.cb.setChecked(bean.isChecked());
        return convertView;
    }

    static class ViewHolder
    {
        ImageView img;
        CheckBox cb;
        TextView tvTime;
    }
}
