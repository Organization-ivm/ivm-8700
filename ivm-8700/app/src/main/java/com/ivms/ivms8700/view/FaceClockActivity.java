package com.ivms.ivms8700.view;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ivms.ivms8700.R;
import com.ivms.ivms8700.bean.FaceEntity;
import com.ivms.ivms8700.utils.okmanager.OkHttpClientManager;
import com.ivms.ivms8700.view.adapter.FaceAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/***
 * 人脸识别考勤 by Alan
 * **/
public class FaceClockActivity extends Activity implements View.OnClickListener,OkHttpClientManager.JsonObjectCallback {

    private ImageView back_btn;
    private TextView save_btn;
    private TextView title_txt;
    private RecyclerView face_clock_view;
    private List<FaceEntity> mFaceList = new ArrayList<FaceEntity>();
    private FaceAdapter adapter=null;
    private Button sure_btn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_clock);
        initView();
//        refreshData();
    }
    //刷新数据
    private void refreshData() {
        OkHttpClientManager.getInstance().asyncJsonObjectByUrl("http://222.66.82.4/shm/faceRecognize?recognizeTime=2018-12-13&lineCode=310000L14&stationCode=310000L14S13&userName=mobile&token=4CE19CA8FCD150A4",this);
    }

    private void initView() {
        back_btn=(ImageView)findViewById(R.id.back_btn);
        back_btn.setOnClickListener(this);
        save_btn=(TextView)findViewById(R.id.right_btn);
        save_btn.setVisibility(View.INVISIBLE);
        save_btn.setOnClickListener(this);
        title_txt=(TextView)findViewById(R.id.title_txt);
        title_txt.setText(getString(R.string.renlianshibie_kaoqin));
        sure_btn=(Button)findViewById(R.id.sure_btn);
        sure_btn.setOnClickListener(this);
        //初始化RecyclerView
        face_clock_view=(RecyclerView)findViewById(R.id.face_clock_list);
        //创建LinearLayoutManager 对象 这里使用 LinearLayoutManager 是线性布局的意思
        LinearLayoutManager layoutmanager = new LinearLayoutManager(this);
        //设置RecyclerView 布局
        layoutmanager.setOrientation(LinearLayoutManager.VERTICAL);
        face_clock_view.setLayoutManager(layoutmanager);
        //设置Adapter
        adapter = new FaceAdapter(mFaceList,this);
        face_clock_view.setAdapter(adapter);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back_btn:
                 finish();
                break;
            case R.id.sure_btn://确认查询
                refreshData();
                break;
        }
    }
    //网络请求回调
    @Override
    public void onResponse(JSONObject jsonObject) {
        try {
            mFaceList.clear();
            JSONObject data=jsonObject.getJSONObject("data");
            JSONArray list=data.getJSONArray("list");
            for (int i=0;i<list.length();i++){
                JSONObject obj=list.getJSONObject(i);
                String lineCode=obj.getString("lineCode");
                String lineName=obj.getString("lineName");
                JSONArray stations=obj.getJSONArray("stations");
                for (int j=0;j<stations.length();j++){
                   JSONObject obj1=stations.getJSONObject(j);
                   String stationCode=obj1.getString("stationCode");
                   String stationName=obj1.getString("stationName");
                   JSONArray photos =obj1.getJSONArray("photos");
                   for (int k=0;k<photos.length();k++){
                     JSONObject obj2=photos.getJSONObject(k);
                     String name=obj2.getString("name");
                     String employeeNumber=obj2.getString("employeeNumber");
                     String captureTime=obj2.getString("captureTime");
                     String faceCapture=obj2.getString("faceCapture");
                     String sex=obj2.getString("sex");
                     String officeName=obj2.getString("officeName");
                     String department=obj2.getString("department");
                     String position=obj2.getString("position");
                     String phone=obj2.getString("phone");

                     FaceEntity faceEntity = new FaceEntity();
                         faceEntity.setName(name);
                         faceEntity.setEmployeeNumber(employeeNumber);
                         faceEntity.setDate(captureTime);
                         faceEntity.setLineCode(lineCode);
                         faceEntity.setLineName(lineName);
                         faceEntity.setStationCode(stationCode);
                         faceEntity.setStationName(stationName);
                         faceEntity.setFaceCapture(faceCapture);
                         faceEntity.setSex(sex);
                         faceEntity.setOfficeName(officeName);
                         faceEntity.setDepartment(department);
                         faceEntity.setPosition(position);
                         faceEntity.setPhone(phone);
                     mFaceList.add(faceEntity);
                   }
                }
            }
            adapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
