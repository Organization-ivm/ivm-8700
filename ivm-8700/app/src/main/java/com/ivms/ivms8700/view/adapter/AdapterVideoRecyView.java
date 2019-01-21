package com.ivms.ivms8700.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.ivms.ivms8700.R;
import com.ivms.ivms8700.bean.VideoEntity;
import com.ivms.ivms8700.view.customui.CustomSurfaceView;

import java.util.List;

public class AdapterVideoRecyView extends RecyclerView.Adapter<AdapterVideoRecyView.ViewHolder> {
    Context context;
    List<VideoEntity> list;
    private int window_heigth;
    private int window_width;
    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(View view, int position);
    }
    public interface OnItemImgClickListener {
        void onItemImgClick(View view, int position);
    }
    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener mOnItemLongClickListener;
    private OnItemImgClickListener mOnImgClickListener;
    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener mOnItemLongClickListener) {
        this.mOnItemLongClickListener = mOnItemLongClickListener;
    }
    public void setOnItemImgClickListener(OnItemImgClickListener mOnImgClickListener) {
        this.mOnImgClickListener = mOnImgClickListener;
    }

    public AdapterVideoRecyView(Context context, List<VideoEntity> list) {
        this.context = context;
        this.list = list;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //返回ViewHolder对象，通过构造方法传入加载布局文件得到的view对象
        return new AdapterVideoRecyView.ViewHolder( LayoutInflater.from(parent.getContext()).inflate(R.layout.video_item_layout,parent,false));
    }
    //删除数据
    public void removeItem(int pos) {
        list.remove(pos);
        notifyItemRemoved(pos);
    }

    @Override
    public void onBindViewHolder(final AdapterVideoRecyView.ViewHolder holder, final int position) {
        //通过Resources获取屏幕高度
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        window_heigth = dm.heightPixels;
        window_width = dm.widthPixels;
        int rowCount =list.get(position).getRowCout();
        LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(
                window_width/rowCount,
                window_width/rowCount
        );
        holder.myItemView.setLayoutParams(linearParams);
            //更换背景
            if(list.get(position).isSelect()){
                holder.myItemView.setBackgroundResource(R.drawable.item_select_style);
            }else{
                holder.myItemView.setBackground(null);
            }
        //判断是否设置了监听器
        if (mOnItemClickListener != null) { //为ItemView设置监听器
            holder.myItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = holder.getLayoutPosition();

                    mOnItemClickListener.onItemClick(holder.myItemView, position);
                }
            });
        }
        if (mOnImgClickListener != null) { //为ItemImg设置监听器
            holder.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = holder.getLayoutPosition();
                    mOnImgClickListener.onItemImgClick(holder.myItemView, position);
                }
            });
        }

        if (mOnItemLongClickListener != null) {
            holder.myItemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int position = holder.getLayoutPosition();
                    mOnItemLongClickListener.onItemLongClick(holder.myItemView, position);
                    //返回true 表示消耗了事件 事件不会继续传递
                    return true;
                }
            });
        }
    }
    @Override
    public int getItemViewType(int position) {
        return position;
    }
    @Override//返回数据源大小
    public int getItemCount() {
        return list.size();
    } //自定义MyViewHolder类用于复用

    public class ViewHolder extends RecyclerView.ViewHolder { //声明imageview对象
        private  View myItemView;
        private final ImageView image;
        private final CustomSurfaceView videoView;
        //构造方法中初始化imageview对象
        public ViewHolder(View itemView) {
            super(itemView);
            myItemView=itemView;
            image = (ImageView) itemView.findViewById(R.id.add_monitory);
            videoView=(CustomSurfaceView)itemView.findViewById(R.id.surfaceView);

        }
        public void autoClick(){
            myItemView.performClick();
        }
    }

}
