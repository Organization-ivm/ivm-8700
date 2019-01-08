package com.ivms.ivms8700.view.adapter;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ivms.ivms8700.R;
import com.ivms.ivms8700.bean.DiscernEntity;
import com.ivms.ivms8700.bean.WeatherEntity;
import com.ivms.ivms8700.utils.LocalDbUtil;
import com.ivms.ivms8700.utils.UIUtil;
import com.polites.android.GestureImageView;

import java.util.List;

/***
 *
 * 气象适配器 by Alan
 *
 */

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.ViewHolder> {

    private List<WeatherEntity> mWeatherList;
    private Context mContext;

    public WeatherAdapter(List<WeatherEntity> mWeatherList, Context mContext) {
        this.mWeatherList = mWeatherList;
        this.mContext=mContext;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.weather_item_layout,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final WeatherEntity mWeatherEntity = mWeatherList.get(position);
        holder.count.setText((position+1)+"、");
        holder.staion.setText(mWeatherEntity.getName());
        holder.temperature.setText(mWeatherEntity.getTemperature()+"℃");
        holder.date_txt.setText(mWeatherEntity.getReceiveTime());

        holder.windAngle.setText(mWeatherEntity.getWindAngle()+"度");
        holder.windSpeed.setText(mWeatherEntity.getWindSpeed()+"m/s");
        holder.humidity.setText(mWeatherEntity.getHumidity());
        holder.dayRainFall.setText(mWeatherEntity.getDayRainFall()+"mm");

    }

    @Override
    public int getItemCount() {
        return mWeatherList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout item_view;
        TextView count;
        TextView staion;
        TextView temperature;
        TextView date_txt;

        TextView windAngle;
        TextView windSpeed;
        TextView humidity;
        TextView dayRainFall;
        public ViewHolder(View view) {
            super(view);
            item_view=(LinearLayout) view.findViewById(R.id.item_view);
            count = (TextView) view.findViewById(R.id.count);
            staion = (TextView) view.findViewById(R.id.staion);
            temperature = (TextView) view.findViewById(R.id.temperature);
            date_txt=(TextView)view.findViewById(R.id.date_txt);

            windAngle = (TextView) view.findViewById(R.id.windAngle);
            windSpeed = (TextView) view.findViewById(R.id.windSpeed);
            humidity = (TextView) view.findViewById(R.id.humidity);
            dayRainFall=(TextView)view.findViewById(R.id.dayRainFall);
        }
    }

}
