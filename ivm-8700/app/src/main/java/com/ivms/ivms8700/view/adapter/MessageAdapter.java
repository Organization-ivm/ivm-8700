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


    public MessageAdapter(Context mContext) {
        super(mContext);

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
        holder.tvName.setText("站点："+data.getStationName());
        holder.tvName.setTag(data.getStationCode());
        holder.tvTime.setText("日期："+data.getRecognizeTime());
        holder.count.setText((position+1)+"、");

    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        TextView count;
        TextView tvTime;

        public ViewHolder(View view) {
            super(view);
            count = (TextView) view.findViewById(R.id.count);
            tvName = (TextView) view.findViewById(R.id.tvName);
            tvTime=(TextView)view.findViewById(R.id.tvTime);
        }
    }

}
