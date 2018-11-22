package com.ivms.ivms8700.multilevellist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;


import com.ivms.ivms8700.R;

import java.util.HashMap;
import java.util.List;


/**
 * Created by xulc on 2018/7/27.
 */

public class TreeAdapter extends BaseAdapter {
    private Context mcontext;
    private List<TreePoint> pointList;

    public List<TreePoint> getPointList() {
        return pointList;
    }

    public void setPointList(List<TreePoint> pointList) {
        this.pointList = pointList;
    }

    private HashMap<String, TreePoint> pointMap = new HashMap<>();


    public TreeAdapter(final Context mcontext, List<TreePoint> pointList, HashMap<String, TreePoint> pointMap) {
        this.mcontext = mcontext;
        this.pointList = pointList;
        this.pointMap = pointMap;
    }

    public HashMap<String, TreePoint> getPointMap() {
        return pointMap;
    }

    public void setPointMap(HashMap<String, TreePoint> pointMap) {
        this.pointMap = pointMap;
    }


    //第一要准确计算数量
    @Override
    public int getCount() {
        return pointList.size();
    }

    @Override
    public Object getItem(int position) {
        return pointList.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mcontext).inflate(R.layout.adapter_treeview, null);
            holder = new ViewHolder();
            holder.text = (TextView) convertView.findViewById(R.id.text);
            holder.icon = (ImageView) convertView.findViewById(R.id.icon);
//            holder.ib_select = (ImageButton) convertView.findViewById(R.id.ib_select);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final TreePoint tempPoint = (TreePoint) getItem(position);
        int level = TreeUtils.getLevel(tempPoint, pointMap);
        if (level == 0) {
            holder.icon.setImageResource(R.drawable.manu_1);
        } else {
            holder.icon.setImageResource(R.drawable.manu_2);
        }
        if (null != pointList.get(position).getSubResourceNodeBean()) {
            if (pointList.get(position).getSubResourceNodeBean().isHasPermissionLive()) {
                holder.text.setTextColor(mcontext.getResources().getColor(R.color.text_noselect_color));
            } else {
                holder.text.setTextColor(mcontext.getResources().getColor(R.color.main_text_57576B));
            }


        }


        holder.icon.setPadding(25 * level, holder.icon.getPaddingTop(), 0, holder.icon.getPaddingBottom());
        holder.text.setCompoundDrawablePadding(DensityUtil.dip2px(mcontext, 10));
        holder.text.setText(tempPoint.getNNAME());
        return convertView;
    }

    class ViewHolder {
        TextView text;
        ImageView icon;
    }

}
