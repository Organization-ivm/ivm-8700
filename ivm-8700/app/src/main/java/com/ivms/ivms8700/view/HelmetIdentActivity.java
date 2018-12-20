package com.ivms.ivms8700.view;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ivms.ivms8700.R;
import com.ivms.ivms8700.bean.DiscernEntity;
import com.ivms.ivms8700.bean.FaceEntity;
import com.ivms.ivms8700.utils.okmanager.OkHttpClientManager;
import com.ivms.ivms8700.view.adapter.DiscernAdapter;
import com.ivms.ivms8700.view.adapter.FaceAdapter;
import com.polites.android.GestureImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/****
 * 安全帽识别
 *  by Alan
 * **/
public class HelmetIdentActivity extends Activity implements View.OnClickListener, OkHttpClientManager.JsonObjectCallback {

    private ImageView back_btn;
    private TextView save_btn;
    private TextView title_txt;
    private Button sure_btn;
    private RecyclerView sb_list_view;
    private List<DiscernEntity> mDiscernList = new ArrayList<DiscernEntity>();
    private DiscernAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_helmet_ident);
        initView();
    }

    private void initView() {
        back_btn = (ImageView) findViewById(R.id.back_btn);
        back_btn.setOnClickListener(this);
        save_btn = (TextView) findViewById(R.id.right_btn);
        save_btn.setVisibility(View.INVISIBLE);
        save_btn.setOnClickListener(this);
        title_txt = (TextView) findViewById(R.id.title_txt);
        title_txt.setText(getString(R.string.helmet_identification));
        sure_btn = (Button) findViewById(R.id.sure_btn);
        sure_btn.setOnClickListener(this);
        //初始化RecyclerView
        sb_list_view = (RecyclerView) findViewById(R.id.sb_list_view);
        //创建LinearLayoutManager 对象 这里使用 LinearLayoutManager 是线性布局的意思
        LinearLayoutManager layoutmanager = new LinearLayoutManager(this);
        //设置RecyclerView 布局
        layoutmanager.setOrientation(LinearLayoutManager.VERTICAL);
        sb_list_view.setLayoutManager(layoutmanager);
        //设置Adapter
        adapter = new DiscernAdapter(mDiscernList, this);
        sb_list_view.setAdapter(adapter);
    }

    //刷新数据
    private void refreshData() {
        OkHttpClientManager.getInstance().asyncJsonObjectByUrl("http://222.66.82.4/shm/safeCapRecognize?recognizeTime=2018-11-07&lineCode=310000L14&stationCode=310000L14S01&userName=mobile&token=4CE19CA8FCD150A4", this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_btn:
                finish();
                break;
            case R.id.sure_btn:
                refreshData();
                break;
        }
    }

    @Override
    public void onResponse(JSONObject jsonObject) {
        try {
            mDiscernList.clear();
            JSONObject data = jsonObject.getJSONObject("data");
            JSONArray list = data.getJSONArray("list");
            for (int i = 0; i < list.length(); i++) {
                JSONObject obj = list.getJSONObject(i);
                String lineCode = obj.getString("lineCode");
                String lineName = obj.getString("lineName");
                JSONArray stations = obj.getJSONArray("stations");
                for (int j = 0; j < stations.length(); j++) {
                    JSONObject obj1 = stations.getJSONObject(j);
                    String stationCode = obj1.getString("stationCode");
                    String stationName = obj1.getString("stationName");
                    JSONArray photos = obj1.getJSONArray("photos");
                    for (int k = 0; k < photos.length(); k++) {
                        JSONObject obj2 = photos.getJSONObject(k);
                        String captureTime = obj2.getString("captureTime");
                        String safeCapCapture = obj2.getString("safeCapCapture");

                        DiscernEntity discernEntity = new DiscernEntity();
                        discernEntity.setLineCode(lineCode);
                        discernEntity.setLineName(lineName);
                        discernEntity.setStationCode(stationCode);
                        discernEntity.setStationName(stationName);
                        discernEntity.setCaptureTime(captureTime);
                        discernEntity.setSafeCapCapture(safeCapCapture);
                        mDiscernList.add(discernEntity);
                    }
                }
            }

            adapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



}
