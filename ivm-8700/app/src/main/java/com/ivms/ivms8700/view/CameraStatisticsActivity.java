package com.ivms.ivms8700.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.ivms.ivms8700.R;
import com.ivms.ivms8700.control.Constants;
import com.ivms.ivms8700.control.MyApplication;
import com.ivms.ivms8700.utils.LocalDbUtil;
import com.ivms.ivms8700.utils.UIUtil;
import com.ivms.ivms8700.utils.okmanager.OkHttpClientManager;
import com.ivms.ivms8700.view.chart.ChartItem;
import com.ivms.ivms8700.view.chart.LineChartItem;
import com.ivms.ivms8700.view.customui.MyLineChartView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
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
    private TextView time_btn;

    private Calendar calendar;// 用来装日期的
    private DatePickerDialog dialog;
    private TextView xl_btn;
    private TextView zd_btn;//站点
    private Button sure_btn;
    private String[] lineNameList = null;
    private String[] lineCodeList = null;
    private String[] stationCodeList = null;
    private String[] stationNameList = null;
    private String[] typeArray = {"日", "月", "年"};
    private String[] type_tagArray = {"day", "month", "year"};

    private JSONArray loginJsonArray;
    private LocalDbUtil localDbUtil;
    private String local_url;
    private String userName;
    private TextView type_btn;
    private ChartDataAdapter cda;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_statistics);

        initView();
        initLineData();
        initData();
//            refreshData();
    }

    private void initView() {
        localDbUtil = new LocalDbUtil(this);
        local_url = localDbUtil.getString("local_url");
        userName = localDbUtil.getString("userName");
        back_btn = (ImageView) findViewById(R.id.back_btn);
        back_btn.setOnClickListener(this);
        save_btn = (TextView) findViewById(R.id.right_btn);
        save_btn.setVisibility(View.INVISIBLE);
        save_btn.setOnClickListener(this);
        title_txt = (TextView) findViewById(R.id.title_txt);
        title_txt.setText(getString(R.string.shexiangji_tongji));
        lv = (ListView) findViewById(R.id.listView);
        xl_btn = (TextView) findViewById(R.id.xl_btn);
        xl_btn.setOnClickListener(this);
        zd_btn = (TextView) findViewById(R.id.zd_btn);
        zd_btn.setOnClickListener(this);
        sure_btn = (Button) findViewById(R.id.sure_btn);
        sure_btn.setOnClickListener(this);
        time_btn = (TextView) findViewById(R.id.time_btn);
        time_btn.setOnClickListener(this);
        type_btn = (TextView) findViewById(R.id.type_btn);
        type_btn.setOnClickListener(this);
    }

    private void initData() {
        try {
            ArrayList<String> damaXList = new ArrayList<String>();
            ArrayList<LineDataSet> sets = new ArrayList<LineDataSet>();
            ArrayList<ChartItem> list = new ArrayList<ChartItem>();
            ArrayList<Entry> e = new ArrayList<Entry>();
            for (int i = 0; i < valuesList.size(); i++) {
                JSONObject obj = valuesList.get(i);
                String num = obj.getString("rate");
                num = num.split("%")[0];
                e.add(new Entry(Float.valueOf(num), i, obj.getString("cameraName")));

                damaXList.add(obj.getString("cameraName"));
            }
            LineDataSet d1 = new LineDataSet(e, "");//所选择的病害类型
            d1.setLineWidth(2.5f);
            d1.setCircleSize(4.5f);
            d1.setHighLightColor(Color.rgb(255, 2, 4));
            d1.setColor(Color.parseColor("#0080FF"));
            d1.setCircleColor(Color.parseColor("#0080FF"));
            d1.setDrawValues(false);
            sets.add(d1);
            cd = new LineData(damaXList, sets);
            list.add(new LineChartItem(cd, getApplicationContext()));

            cda = new ChartDataAdapter(getApplicationContext(), list);
            lv.setAdapter(cda);
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
    }

    //获取线路列表
    private void initLineData() {
        if (null != MyApplication.getIns().getVideoList()) {
            loginJsonArray = MyApplication.getIns().getVideoList();
            lineNameList = new String[loginJsonArray.length()];
            lineCodeList = new String[loginJsonArray.length()];
            for (int i = 0; i < loginJsonArray.length(); i++) {
                try {
                    JSONObject lineObj = loginJsonArray.getJSONObject(i);
                    lineNameList[i] = lineObj.getString("lineName");
                    lineCodeList[i] = lineObj.getString("lineCode");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //根据线路获取站点列表
    private void getStationData(String lineCode) {
        for (int i = 0; i < loginJsonArray.length(); i++) {
            try {
                JSONObject lineObj = loginJsonArray.getJSONObject(i);
                if (lineCode.equals(lineObj.getString("lineCode"))) {
                    JSONArray stationsArray = lineObj.getJSONArray("stations");
                    stationNameList = new String[stationsArray.length()];
                    stationCodeList = new String[stationsArray.length()];
                    for (int j = 0; j < stationsArray.length(); j++) {
                        JSONObject stationObj = stationsArray.getJSONObject(j);
                        stationNameList[j] = stationObj.getString("stationName");
                        stationCodeList[j] = stationObj.getString("stationCode");
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        selectStation();
    }

    //选择站点
    private void selectStation() {
        new AlertDialog.Builder(CameraStatisticsActivity.this, AlertDialog.THEME_HOLO_LIGHT).setTitle("选择站点").setItems(stationNameList, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(CameraStatisticsActivity.this, getString(R.string.your_select) + lineNameList[which], Toast.LENGTH_LONG).show();
                zd_btn.setText(stationNameList[which]);
                zd_btn.setTag(stationCodeList[which]);
                dialog.dismiss();
            }
        }).show();

    }

    @Override
    public void onResponse(JSONObject jsonObject) {
        UIUtil.cancelProgressDialog();
        try {
            String result = jsonObject.getString("result");
            valuesList.clear();
            if (result.equals("success")) {
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
            } else {
                UIUtil.showToast(this, jsonObject.getString("msg"));
            }
            initData();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_btn:
                finish();
                break;
            case R.id.xl_btn:
                selectLine();
                zd_btn.setText("");
                zd_btn.setTag("");
                break;
            case R.id.zd_btn:
                String lineCode = xl_btn.getTag().toString();
                if (!lineCode.isEmpty()) {
                    getStationData(lineCode);
                } else {
                    UIUtil.showToast(this, getString(R.string.plase_xl));
                }
                break;
            case R.id.sure_btn:
                if (xl_btn.getTag().toString().isEmpty()) {
                    UIUtil.showToast(this, getString(R.string.xl_toast));
                    return;
                }
                if (time_btn.getText().toString().isEmpty()) {
                    UIUtil.showToast(this, getString(R.string.time_toast));
                    return;
                }
                if (type_btn.getTag().toString().isEmpty()) {
                    UIUtil.showToast(this, getString(R.string.type_toast));
                    return;
                }
                refreshData();
                break;
            case R.id.type_btn:
                selectType();
                break;
            case R.id.time_btn:
                calendar = Calendar.getInstance();
                dialog = new DatePickerDialog(CameraStatisticsActivity.this, AlertDialog.THEME_HOLO_LIGHT,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                String month = (monthOfYear + 1) + "";
                                if (monthOfYear + 1 < 10) {
                                    month = "0" + month;
                                }
                                String day = dayOfMonth + "";
                                if (dayOfMonth < 10) {
                                    day = "0" + day;
                                }
                                time_btn.setText(year + "-" + month + "-"
                                        + day);
                            }
                        }, calendar.get(Calendar.YEAR), calendar
                        .get(Calendar.MONTH), calendar
                        .get(Calendar.DAY_OF_MONTH));
                dialog.show();
                break;
        }
    }

    private void refreshData() {
        UIUtil.showProgressDialog(this, R.string.loading_process_tip);
        String url = "";
        url += local_url + "/shm/cameraOnlineRate?";
        url += "type=" + type_btn.getTag().toString();
        url += "&lineCode=" + xl_btn.getTag().toString().trim();
        if (!zd_btn.getTag().toString().isEmpty()) {
            url += "&stationCode=" + zd_btn.getTag().toString().trim();
        }
        url += "&queryTime=" + time_btn.getText().toString().trim();
        url += "&userName=" + userName;
        url += "&token=" + Constants.APP_TOKEN;

        Log.i("Alan", "摄像机在线率url=-=" + url);

        OkHttpClientManager.getInstance().asyncJsonObjectByUrl(url, this);
    }

    //选择线路
    private void selectLine() {
        new AlertDialog.Builder(CameraStatisticsActivity.this, AlertDialog.THEME_HOLO_LIGHT).setTitle("选择线路").setItems(lineNameList, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(CameraStatisticsActivity.this, getString(R.string.your_select) + lineNameList[which], Toast.LENGTH_LONG).show();
                xl_btn.setText(lineNameList[which]);
                xl_btn.setTag(lineCodeList[which]);
                zd_btn.setTag("");
                zd_btn.setText("");
                dialog.dismiss();
            }
        }).show();
    }

    //选择类型
    private void selectType() {
        new AlertDialog.Builder(CameraStatisticsActivity.this, AlertDialog.THEME_HOLO_LIGHT).setTitle("选择类型").setItems(typeArray, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(CameraStatisticsActivity.this, getString(R.string.your_select) + typeArray[which], Toast.LENGTH_LONG).show();
                type_btn.setText(typeArray[which]);
                type_btn.setTag(type_tagArray[which]);
                dialog.dismiss();
            }
        }).show();
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

}
