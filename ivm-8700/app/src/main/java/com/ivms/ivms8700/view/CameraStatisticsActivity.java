package com.ivms.ivms8700.view;

import android.app.Activity;
import android.os.Bundle;

import com.ivms.ivms8700.R;
import com.ivms.ivms8700.view.customui.MyLineChartView;

import java.util.ArrayList;
import java.util.List;

/****
 * 摄像机统计
 * by Alan
 * **/
public class CameraStatisticsActivity extends Activity {
   private MyLineChartView chartView;
    List<String> xValues;   //x轴数据集合
    List<Integer> yValues;  //y轴数据集合

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_statistics);

        initView();
    }

    private void initView() {
        chartView = (MyLineChartView) findViewById(R.id.linechartview);
        xValues = new ArrayList<>();
        yValues = new ArrayList<>();
        for(int i=0;i<51;i++){
            xValues.add("14号线");
            yValues.add(i);
        }
        // xy轴集合自己添加数据
        chartView.setXValues(xValues);
        chartView.setYValues(yValues);
    }
}
