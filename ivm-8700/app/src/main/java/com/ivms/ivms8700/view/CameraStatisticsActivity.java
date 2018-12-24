package com.ivms.ivms8700.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.ivms.ivms8700.R;
import com.ivms.ivms8700.utils.okmanager.OkHttpClientManager;
import com.ivms.ivms8700.view.chart.ChartItem;
import com.ivms.ivms8700.view.chart.LineChartItem;
import com.ivms.ivms8700.view.customui.MyLineChartView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/****
 * 摄像机统计
 * by Alan
 * **/
public class CameraStatisticsActivity extends Activity implements OkHttpClientManager.JsonObjectCallback, View.OnClickListener {
    private MyLineChartView chartView;
    List<String> xValues;   //x轴数据集合
    List<Integer> yValues;  //y轴数据集合
    private ListView lv;
    private LineData cd;
    List<JSONObject> valuesList = new ArrayList<>();  //数据集合
    private ImageView back_btn;
    private TextView save_btn;
    private TextView title_txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_statistics);

        initView();
//          initData();
        OkHttpClientManager.getInstance().asyncJsonObjectByUrl("http://222.66.82.4/shm/cameraOnlineRate?type=day&lineCode=310000L14&stationCode=310000L14S01&queryTime=2018-11-08&userName=mobile&token=4CE19CA8FCD150A4", this);
    }

    private void initData() {
        try {

            ArrayList<String> damaXList = new ArrayList<String>();
            ArrayList<LineDataSet> sets = new ArrayList<LineDataSet>();
            ArrayList<ChartItem> list = new ArrayList<ChartItem>();
            ArrayList<Entry> e = new ArrayList<Entry>();
            for (int i = 0; i < valuesList.size(); i++) {
                JSONObject obj = valuesList.get(i);
                String num =obj.getString("rate");
                num=num.split("%")[0];
                e.add(new Entry(Float.valueOf(num), i, obj.getString("cameraName")));

                damaXList.add(obj.getString("cameraName"));
            }
            LineDataSet d1 = new LineDataSet(e, "按月份");//所选择的病害类型
//                if (AppApplication.statisticsFlag) {//是否第一次进入
            d1.setLineWidth(8.5f);
            d1.setCircleSize(10.5f);
//                } else {
//                    d1.setLineWidth(2.5f);
//                    d1.setCircleSize(4.5f);
//                }
            d1.setHighLightColor(Color.rgb(255, 2, 4));//红色
            d1.setColor(Color.parseColor("#0080FF"));
            d1.setCircleColor(Color.parseColor("#0080FF"));
            d1.setDrawValues(false);
            sets.add(d1);
            cd = new LineData(damaXList, sets);
            list.add(new LineChartItem(cd, getApplicationContext()));

            ChartDataAdapter cda = new ChartDataAdapter(getApplicationContext(), list);
            lv.setAdapter(cda);
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
    }

    private void initView() {
        back_btn = (ImageView) findViewById(R.id.back_btn);
        back_btn.setOnClickListener(this);
        save_btn = (TextView) findViewById(R.id.right_btn);
        save_btn.setVisibility(View.INVISIBLE);
        save_btn.setOnClickListener(this);
        title_txt= (TextView) findViewById(R.id.title_txt);
        title_txt.setText(getString(R.string.shexiangji_tongji));
        lv = (ListView) findViewById(R.id.listView);
    }

    @Override
    public void onResponse(JSONObject jsonObject) {
        try {
            JSONObject data = jsonObject.getJSONObject("data");
            JSONArray list = data.getJSONArray("list");
            for (int i = 0; i < list.length(); i++) {
                JSONObject object = list.getJSONObject(i);
                String lineCode = object.getString("lineCode");
                String lineName = object.getString("lineName");
                JSONArray stations = object.getJSONArray("stations");
                for (int j = 0; j < stations.length(); j++) {
                    JSONObject stations_object = stations.getJSONObject(j);
                    String stationCode = stations_object.getString("stationCode");
                    String stationName = stations_object.getString("stationName");
                    JSONArray cameras = stations_object.getJSONArray("cameras");
                    for (int k = 0; k < cameras.length(); k++) {
                        JSONObject camera_object = cameras.getJSONObject(k);
                        valuesList.add(camera_object);

                    }
                }

            }
            initData();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back_btn:
                finish();
                break;
        }
    }


    private class ChartDataAdapter extends ArrayAdapter<ChartItem> {
        private List<ChartItem> objects;

        public ChartDataAdapter(Context context, final List<ChartItem> objects) {
            super(context, 0, objects);
            this.objects = objects;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getItem(position).getView(position, convertView, getContext());
        }

        @Override
        public int getItemViewType(int position) {
            return getItem(position).getItemType();
        }

        @Override
        public int getViewTypeCount() {
            return 3; // we have 3 different item-types
        }
    }

//    private void initView() {
//        chartView = (MyLineChartView) findViewById(R.id.linechartview);
//        xValues = new ArrayList<>();
//        yValues = new ArrayList<>();
//        for(int i=0;i<51;i++){
//            xValues.add("14号线");
//            yValues.add(i);
//        }
//        // xy轴集合自己添加数据
//        chartView.setXValues(xValues);
//        chartView.setYValues(yValues);
//    }
}
