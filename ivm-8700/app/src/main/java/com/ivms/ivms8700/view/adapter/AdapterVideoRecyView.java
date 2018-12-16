package com.ivms.ivms8700.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.ivms.ivms8700.R;
import com.ivms.ivms8700.view.customui.CustomSurfaceView;

import java.util.List;

public class AdapterVideoRecyView extends RecyclerView.Adapter<AdapterVideoRecyView.ViewHolder> {
    Context context;
    List<Integer> list;
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

    public AdapterVideoRecyView(Context context, List<Integer> list) {
        this.context = context;
        this.list = list;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //返回ViewHolder对象，通过构造方法传入加载布局文件得到的view对象
        View view = View.inflate(context, R.layout.video_item_layout, null);

        ViewHolder holder = new ViewHolder(view);

        return holder;
    }
    //删除数据
    public void removeItem(int pos) {
        list.remove(pos);
        notifyItemRemoved(pos);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        //通过Resources获取屏幕高度
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        window_heigth = dm.heightPixels;
        window_width = dm.widthPixels;
        int rowCount =list.get(0);
        LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(
                window_width/rowCount,
                window_width/rowCount
        );
        holder.itemView.setLayoutParams(linearParams);
        //判断是否设置了监听器
        if (mOnItemClickListener != null) { //为ItemView设置监听器
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = holder.getLayoutPosition();
                    mOnItemClickListener.onItemClick(holder.itemView, position);
                }
            });
        }
        if (mOnImgClickListener != null) { //为ItemImg设置监听器
            holder.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = holder.getLayoutPosition();
                    mOnImgClickListener.onItemImgClick(holder.itemView, position);
                }
            });
        }

        if (mOnItemLongClickListener != null) {
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int position = holder.getLayoutPosition();
                    mOnItemLongClickListener.onItemLongClick(holder.itemView, position);
                    //返回true 表示消耗了事件 事件不会继续传递
                    return true;
                }
            });
        }
    }

    @Override//返回数据源大小
    public int getItemCount() {
        return list.size();
    } //自定义MyViewHolder类用于复用

    public class ViewHolder extends RecyclerView.ViewHolder { //声明imageview对象
        private final ImageView image;
        private final CustomSurfaceView videoView;
        //构造方法中初始化imageview对象
        public ViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.add_monitory);
            videoView=(CustomSurfaceView)itemView.findViewById(R.id.surfaceView);

        }
    }
}
