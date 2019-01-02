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
import com.ivms.ivms8700.bean.MessageEntity;

import java.util.List;

/***
 *
 * 消息是配器
 *
 */

public class MessageAdapter extends BaseAdapter<MessageEntity.Msg,MessageAdapter.ViewHolder> {

    private List<DiscernEntity> discernList;
    private Context mContext;
    private Dialog dia;
    public MessageAdapter(Context mContext) {
        super(mContext);
//        this.discernList = mDiscernList;
//        this.mContext=mContext;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }


    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    protected void bindViewHolderData(ViewHolder holder, MessageEntity.Msg data, int position) {
//        holder.staion.setText(data.getStationName());
//        holder.date_txt.setText("日期："+data.getCaptureTime());
        holder.tvName.setText(data.getStationCode());
        holder.tvTime.setText(data.getRecognizeTime());
        holder.tvMessage.setText(data.getType());


    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        TextView tvMessage;
        TextView tvTime;

        public ViewHolder(View view) {
            super(view);
            tvName = (TextView) view.findViewById(R.id.tvName);
            tvMessage = (TextView) view.findViewById(R.id.tvMessage);
            tvTime=(TextView)view.findViewById(R.id.tvTime);
        }
    }

//    private void showDialog(String url) {
//        dia =new Dialog(mContext, R.style.edit_AlertDialog_style);
//        dia.setContentView(R.layout.activity_start_dialog);
//        GestureImageView imageView =(GestureImageView) dia.findViewById(R.id.start_img);
//        Glide.with(mContext).load("http://222.66.82.4:80/shm/"+url).into(imageView);
//        dia.show();
//        dia.setCanceledOnTouchOutside(true);
//        Window w = dia.getWindow();
//        WindowManager.LayoutParams lp = w.getAttributes();
//        lp.x = 0; lp.y = 40; dia.onWindowAttributesChanged(lp);
//        imageView.setOnClickListener( new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                dia.dismiss();
//            }
//        });
//    }
}
