package com.ivms.ivms8700.view.adapter;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ivms.ivms8700.R;
import com.ivms.ivms8700.bean.DiscernEntity;
import com.polites.android.GestureImageView;

import java.util.List;

/***
 *
 * 人脸识别适配器 by Alan
 *
 */

public class DiscernAdapter extends RecyclerView.Adapter<DiscernAdapter.ViewHolder> {

    private List<DiscernEntity> discernList;
    private Context mContext;
    private Dialog dia;
    public DiscernAdapter(List<DiscernEntity> mDiscernList, Context mContext) {
        this.discernList = mDiscernList;
        this.mContext=mContext;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.discern_item_layout,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final DiscernEntity discernEntity = discernList.get(position);
        holder.count.setText((position+1)+"、");
        holder.staion.setText(discernEntity.getStationName());
        holder.date_txt.setText("日期："+discernEntity.getCaptureTime());

        holder.item_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showDialog(discernEntity.getSafeCapCapture());
            }
        });

    }

    @Override
    public int getItemCount() {
        return discernList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout item_view;
        TextView count;
        TextView staion;
        TextView date_txt;

        public ViewHolder(View view) {
            super(view);
            item_view=(LinearLayout) view.findViewById(R.id.item_view);
            count = (TextView) view.findViewById(R.id.count);
            staion = (TextView) view.findViewById(R.id.staion);
            date_txt=(TextView)view.findViewById(R.id.date_txt);
        }
    }

    private void showDialog(String url) {
        dia =new Dialog(mContext, R.style.edit_AlertDialog_style);
        dia.setContentView(R.layout.activity_start_dialog);
        GestureImageView imageView =(GestureImageView) dia.findViewById(R.id.start_img);
        Glide.with(mContext).load("http://222.66.82.4:80/shm/"+url).into(imageView);
        dia.show();
        dia.setCanceledOnTouchOutside(true);
        Window w = dia.getWindow();
        WindowManager.LayoutParams lp = w.getAttributes();
        lp.x = 0; lp.y = 40; dia.onWindowAttributesChanged(lp);
        imageView.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dia.dismiss();
            }
        });
    }
}
