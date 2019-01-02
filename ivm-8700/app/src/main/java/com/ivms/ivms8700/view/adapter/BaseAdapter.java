package com.ivms.ivms8700.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;

import com.ivms.ivms8700.utils.NoDoubleClickListener;

import java.util.ArrayList;
import java.util.List;


/**
 * Adapter积累.处理了点击和长按事件.
 * Created by y on 17/10/18.
 */
public abstract class BaseAdapter<T, V extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<V> {

    public interface OnItemClickListener<T> {
        /**
         * 被点击
         *
         * @param adapter  adapter
         * @param data     数据
         * @param position 位置
         */
        void onItemClicked(RecyclerView.Adapter adapter, T data, int position);
    }

    interface OnItemLongClickListener<T> {
        /**
         * 被长按
         *
         * @param adapter  adapter
         * @param data     数据
         * @param position 位置
         * @return 是否向下传递
         */
        boolean onItemLongClicked(RecyclerView.Adapter adapter, T data, int position);
    }

    protected List<T> mData = new ArrayList<>();
    private OnItemClickListener mClickListener;
    private OnItemLongClickListener mLongClickListener;

    protected Context mCtx;
    public BaseAdapter(Context ctx) {
        this.mCtx = ctx;
//        baseActivity= (BaseActivity) mCtx;
    }

    /**
     * 增加数据
     *
     * @param data data
     */
    public void add(T data) {
        this.mData.add(data);
        notifyDataSetChanged();
    }

    /**
     * 制定位置添加数据
     *
     * @param position position
     * @param data     data
     */
    public void add(int position, T data) {
        this.mData.add(position, data);
    }

    /**
     * 增加全部数据
     *
     * @param //datas datas
     */
    public void addAll(List<T> datas) {
        if (datas == null)
            return;
        this.mData.addAll(datas);
    }

    /**
     * 清空，重新设置
     *
     * @param datas
     */
    public void setData(List<T> datas) {
//        if (mData != null)
//            mData.clear();
            mData = datas;
            notifyDataSetChanged();
    }

    /**
     * 清空数据
     */
    public void clear() {
        this.mData.clear();
    }

    /**
     * 设置点击监听器
     *
     * @param listener 监听器
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mClickListener = listener;
    }

    /**
     * 设置长按监听器
     *
     * @param listener 监听器
     */
    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.mLongClickListener = listener;
    }

    /**
     * 绑定数据
     *
     * @param holder   StickerItemViewHolder
     * @param position 位置
     */
    @SuppressWarnings("unchecked")
    @Override
    public void onBindViewHolder(final V holder, int position) {
        if (mData==null||mData.size()<=0){
            return;
        }
        final T data = mData.get(position);
//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//                                               @Override
//                                               public void onClick(View view) {
//
//                                               }
//                                           }
//        );
        holder.itemView.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                if (mClickListener != null) { mClickListener.onItemClicked(BaseAdapter.this, data, holder.getAdapterPosition()); }
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                return mLongClickListener != null && mLongClickListener.onItemLongClicked(BaseAdapter.this, data, holder.getAdapterPosition());
            }
        });
//        holder.itemView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                if (baseActivity!=null){
//                    return baseActivity.onTouchEvent(motionEvent);
//                }else{
//                    return false;
//                }
//            }
//        });
        bindViewHolderData(holder, data, position);
    }

    @Override
    public int getItemCount() {
        if (mData!=null){
         return mData.size();
        }
        return 0;
    }

    /**
     * 绑定viewholder与data的数据.
     *
     * @param viewHolder viewholder
     * @param data       数据
     */
    protected abstract void bindViewHolderData(V viewHolder, T data, int position);


    /**
     * 获取指定位置的item
     *
     * @param position 要获取的位置
     * @return 返回的item
     */
    public T getItemAt(int position) {
        if (position < mData.size()) {
            return mData.get(position);
        }
        return null;
    }

    public int getItemHeigh(){

        return 1;
    }
    /**
     * 获得绑定数据
     *
     * @return
     */
    public List<T> getmData() {
        return this.mData;
    }
}
