//package com.ivms.ivms8700.view.adapter;
//
//import android.content.Context;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//import com.ivms.ivms8700.R;
//import com.ivms.ivms8700.entity.ClassA;
//import com.ivms.ivms8700.entity.ClassB;
//import com.ivms.ivms8700.entity.ClassC;
//import com.ivms.ivms8700.entity.ClassD;
//import com.lijianxun.multilevellist.adapter.MultiLevelAdapter;
//import com.lijianxun.multilevellist.model.MultiLevelModel;
//
//
//public class MonitoryAdapter extends MultiLevelAdapter {
//
//    public MonitoryAdapter(Context context, boolean isExpandable, boolean isExpandAll
//            , int expandLevel) {
//        super(context, isExpandable, isExpandAll, expandLevel);
//    }
//
//    @Override
//    public View onCreateView(int position, View convertView, ViewGroup parent) {
//        Holder v = null;
//        if (convertView == null) {
//            convertView = mInflater.inflate(R.layout.item_layout, null);
//            v = new Holder(convertView);
//            convertView.setTag(v);
//        } else {
//            v = (Holder) convertView.getTag();
//        }
//        MultiLevelModel model = (MultiLevelModel) getItem(position);
//        switch (model.getLevel()) {
//            case 0:
//                ClassA a = (ClassA) model;
//                v.tv.setText( a.getName());
//                break;
//            case 1:
//                ClassB b = (ClassB) model;
//                v.tv.setText( b.getLabel());
//                break;
//            case 2:
//                ClassC c = (ClassC) model;
//                v.tv.setText(c.getName());
//                break;
//            case 3:
//                ClassD d = (ClassD) model;
//                v.tv.setText(d.getName());
//                break;
//        }
//        convertView.setPadding(10 + model.getLevel() * 50, 10, 10, 10);
//        return convertView;
//    }
//
//    class Holder {
//        TextView tv;
//
//        public Holder(View view) {
//            tv = (TextView) view.findViewById(R.id.tv);
//        }
//    }
//}