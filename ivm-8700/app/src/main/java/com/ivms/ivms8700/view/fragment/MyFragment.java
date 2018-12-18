package com.ivms.ivms8700.view.fragment;


import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.ivms.ivms8700.R;
import com.ivms.ivms8700.utils.okmanager.OkHttpClientManager;
import com.ivms.ivms8700.view.FaceClockActivity;
import com.ivms.ivms8700.view.HelmetIdentActivity;

import org.json.JSONObject;

import java.util.List;

public class MyFragment extends Fragment implements View.OnClickListener{

    private View view;
    private ImageView message_btn;
    private boolean isMessage=true;
    private RelativeLayout out_app_btn;
    private OkHttpClientManager okHttpClientManager=null;
    private RelativeLayout face_clock_btn;
    private RelativeLayout helmet_identification_btn;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(view==null){
            view=inflater.inflate(R.layout.my_layout,container,false);
            message_btn=(ImageView)view.findViewById(R.id.message_btn);
            message_btn.setOnClickListener(this);
            out_app_btn=(RelativeLayout)view.findViewById(R.id.out_app_btn);
            out_app_btn.setOnClickListener(this);
            face_clock_btn=(RelativeLayout)view.findViewById(R.id.face_clock_btn);
            face_clock_btn.setOnClickListener(this);
            helmet_identification_btn=(RelativeLayout)view.findViewById(R.id.helmet_identification_btn);
            helmet_identification_btn.setOnClickListener(this);
        }
        okHttpClientManager=OkHttpClientManager.getInstance();
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.message_btn:
                  if(isMessage){
                      isMessage=false;
                      message_btn.setBackgroundResource(R.drawable.anniu_1);
                  }else{
                      isMessage=true;
                      message_btn.setBackgroundResource(R.drawable.anniu_2);
                  }
                break;

            case R.id.out_app_btn:
                Exit(getActivity());
                break;
            case R.id.face_clock_btn://人脸识别
                Intent face_intent =new Intent(getActivity(), FaceClockActivity.class);
                getActivity().startActivity(face_intent);
                break;
            case R.id.helmet_identification_btn://安全帽识别
                Intent hi_intent =new Intent(getActivity(), HelmetIdentActivity.class);
                getActivity().startActivity(hi_intent);
                break;


        }
    }
    /**
     * 退出程序
     * @param cont
     */

    public void Exit(final Context cont)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(cont,AlertDialog.THEME_HOLO_LIGHT);
        builder.setTitle("确定退出系统吗？");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                okHttpClientManager.synaGetByUrl("http://222.66.82.4/shm/loginout?userName=mobile&token=4CE19CA8FCD150A4");
                exitAPP();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void exitAPP() {
        ActivityManager activityManager = (ActivityManager) getActivity().getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.AppTask> appTaskList = activityManager.getAppTasks();
        for (ActivityManager.AppTask appTask : appTaskList) {
            appTask.finishAndRemoveTask();
        }
        // appTaskList.get(0).finishAndRemoveTask();
        System.exit(0);
    }
}
