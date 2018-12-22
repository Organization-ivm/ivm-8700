package com.ivms.ivms8700.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.ivms.ivms8700.R;
import com.ivms.ivms8700.bean.FaceEntity;
import com.ivms.ivms8700.control.MyApplication;
import com.ivms.ivms8700.utils.UIUtil;
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
    private TextView xl_btn;
    private TextView zd_btn;//站点
    private String[] lineNameList = null;
    private String[] lineCodeList = null;
    private String[] stationCodeList = null;
    private String[] stationNameList = null;

    private JSONArray loginJsonArray;


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

        xl_btn=(TextView)findViewById(R.id.xl_btn);
        xl_btn.setOnClickListener(this);
        zd_btn=(TextView)findViewById(R.id.zd_btn);
        zd_btn.setOnClickListener(this);
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
        initLineData();

    }
    //获取线路列表
    private void initLineData() {
        if(null!=MyApplication.getIns().getVideoList()) {
            loginJsonArray = MyApplication.getIns().getVideoList();
            lineNameList=new String[loginJsonArray.length()];
            lineCodeList=new String[loginJsonArray.length()];
            for (int i=0;i<loginJsonArray.length();i++){
                try {
                    JSONObject lineObj=loginJsonArray.getJSONObject(i);
                    lineNameList[i]=lineObj.getString("lineName");
                    lineCodeList[i]=lineObj.getString("lineCode");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
   //根据线路获取站点列表
   private void getStationData(String lineCode) {
       for (int i=0;i<loginJsonArray.length();i++){
           try {
               JSONObject lineObj=loginJsonArray.getJSONObject(i);
              if(lineCode.equals(lineObj.getString("lineCode"))){
                 JSONArray stationsArray=lineObj.getJSONArray("stations");
                  stationNameList=new String[stationsArray.length()];
                  stationCodeList=new String[stationsArray.length()];
                 for (int j=0;j<stationsArray.length();j++){
                     JSONObject stationObj=stationsArray.getJSONObject(j);
                     stationNameList[j]=stationObj.getString("stationName");
                     stationCodeList[j]=stationObj.getString("stationCode");
                 }
              }
           } catch (JSONException e) {
               e.printStackTrace();
           }
       }
       selectStation();
   }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back_btn:
                 finish();
                break;
            case R.id.xl_btn:

                selectLine();
                break;
            case R.id.zd_btn:
                String  lineCode = xl_btn.getTag().toString();
                if(!lineCode.isEmpty()){
                    getStationData(lineCode);
                }else{
                    UIUtil.showToast(this,getString(R.string.plase_xl));
                }
                break;
            case R.id.sure_btn://确认查询
                refreshData();
                break;
        }
    }
    //选择线路
    private void selectLine() {
        new AlertDialog.Builder(FaceClockActivity.this,AlertDialog.THEME_HOLO_LIGHT).setTitle("选择线路").setItems(lineNameList,new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int which){
                Toast.makeText(FaceClockActivity.this, getString(R.string.your_select) + lineNameList[which],Toast.LENGTH_LONG).show();
                xl_btn.setText(lineNameList[which]);
                xl_btn.setTag(lineCodeList[which]);
                zd_btn.setTag("");
                zd_btn.setText("");
                dialog.dismiss();
            }
        }).show();

    }
    //选择站点
    private void selectStation() {
        new AlertDialog.Builder(FaceClockActivity.this,AlertDialog.THEME_HOLO_LIGHT).setTitle("选择站点").setItems(stationNameList,new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int which){
                Toast.makeText(FaceClockActivity.this, getString(R.string.your_select) + lineNameList[which],Toast.LENGTH_LONG).show();
                zd_btn.setText(stationNameList[which]);
                zd_btn.setTag(stationCodeList[which]);
                dialog.dismiss();
            }
        }).show();

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
                     String modelPhoto=obj2.getString("modelPhoto");

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
                         faceEntity.setModelPhoto(modelPhoto);
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
