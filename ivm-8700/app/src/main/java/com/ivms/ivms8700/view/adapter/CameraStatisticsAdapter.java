package com.ivms.ivms8700.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.JsonObject;
import com.ivms.ivms8700.R;
import com.ivms.ivms8700.bean.FaceEntity;
import com.ivms.ivms8700.view.FaceDetailActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class CameraStatisticsAdapter extends RecyclerView.Adapter<CameraStatisticsAdapter.ViewHolder> {

    private List<JSONObject> cameraList;
    private Context mContext;

    public CameraStatisticsAdapter(List<JSONObject> cameraList, Context mContext) {
        this.cameraList = cameraList;
        this.mContext = mContext;
    }

    @Override
    public CameraStatisticsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.camera_item_layout, parent, false);
        CameraStatisticsAdapter.ViewHolder holder = new CameraStatisticsAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(CameraStatisticsAdapter.ViewHolder holder, int position) {
        try {
            JSONObject obj = cameraList.get(position);
            holder.count.setText((position + 1) + "、");
            holder.staion_txt.setText(obj.getString("cameraName").toString());
            holder.num_txt.setText(obj.getString("rate").toString());
            holder.item_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return cameraList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout item_view;
        TextView count;
        TextView staion_txt;
        TextView num_txt;

        public ViewHolder(View view) {
            super(view);
            item_view = (LinearLayout) view.findViewById(R.id.item_view);
            count = (TextView) view.findViewById(R.id.count);
            staion_txt = (TextView) view.findViewById(R.id.staion_txt);
            num_txt = (TextView) view.findViewById(R.id.num_txt);

        }
    }
}