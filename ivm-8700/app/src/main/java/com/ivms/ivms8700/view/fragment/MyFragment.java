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
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.ivms.ivms8700.R;
import com.ivms.ivms8700.control.Constants;
import com.ivms.ivms8700.utils.LocalDbUtil;
import com.ivms.ivms8700.utils.okmanager.OkHttpClientManager;
import com.ivms.ivms8700.view.CameraStatisticsActivity;
import com.ivms.ivms8700.view.FaceClockActivity;
import com.ivms.ivms8700.view.HelmetIdentActivity;
import com.ivms.ivms8700.view.OnlineSummaryActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class MyFragment extends Fragment implements View.OnClickListener, OkHttpClientManager.JsonStringCallback, OkHttpClientManager.JsonObjectCallback {

    private View view;
    private ImageView message_btn;
    private boolean isMessage = true;
    private RelativeLayout out_app_btn;
    private OkHttpClientManager okHttpClientManager = null;
    private RelativeLayout face_clock_btn;
    private RelativeLayout helmet_identification_btn;
    private RelativeLayout camera_statisic_lay;
    private RelativeLayout online_summary_lay;
    private LocalDbUtil localDbUtil;
    private String local_url;
    private String userName;
    private TextView username;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.my_layout, container, false);
            localDbUtil=new LocalDbUtil(getActivity());
            local_url=localDbUtil.getString("local_url");
            userName=localDbUtil.getString("userName");
            username=(TextView)view.findViewById(R.id.username);
            username.setText(userName);
            message_btn = (ImageView) view.findViewById(R.id.message_btn);
            message_btn.setOnClickListener(this);
            out_app_btn = (RelativeLayout) view.findViewById(R.id.out_app_btn);
            out_app_btn.setOnClickListener(this);
            face_clock_btn = (RelativeLayout) view.findViewById(R.id.face_clock_btn);
            face_clock_btn.setOnClickListener(this);
            helmet_identification_btn = (RelativeLayout) view.findViewById(R.id.helmet_identification_btn);
            helmet_identification_btn.setOnClickListener(this);
            camera_statisic_lay = (RelativeLayout) view.findViewById(R.id.camera_statisic_lay);
            camera_statisic_lay.setOnClickListener(this);
            online_summary_lay = (RelativeLayout) view.findViewById(R.id.online_summary_lay);
            online_summary_lay.setOnClickListener(this);
        }
        okHttpClientManager = OkHttpClientManager.getInstance();
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.message_btn:
                if (isMessage) {
                    isMessage = false;
                    message_btn.setBackgroundResource(R.drawable.anniu_1);
                } else {
                    isMessage = true;
                    message_btn.setBackgroundResource(R.drawable.anniu_2);
                }
                break;

            case R.id.out_app_btn:
                Exit(getActivity());
                break;
            case R.id.face_clock_btn://人脸识别
                Intent face_intent = new Intent(getActivity(), FaceClockActivity.class);
                getActivity().startActivity(face_intent);
                break;
            case R.id.helmet_identification_btn://安全帽识别
                Intent hi_intent = new Intent(getActivity(), HelmetIdentActivity.class);
                getActivity().startActivity(hi_intent);
                break;
            case R.id.camera_statisic_lay://摄像机在线统计
                Intent camera_intent = new Intent(getActivity(), CameraStatisticsActivity.class);
                getActivity().startActivity(camera_intent);
                break;
            case R.id.online_summary_lay://摄像机在线汇总
                String url=local_url+"/shm/cameraSummary?userName="+userName+"&token="+ Constants.APP_TOKEN;
                OkHttpClientManager.getInstance().asyncJsonObjectByUrl(url,this);
                break;

        }
    }

    /**
     * 退出程序
     *
     * @param cont
     */

    public void Exit(final Context cont) {
        AlertDialog.Builder builder = new AlertDialog.Builder(cont, AlertDialog.THEME_HOLO_LIGHT);
        builder.setTitle("确定退出系统吗？");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                okHttpClientManager.asyncJsonStringByURL(local_url+"/shm/loginout?userName=mobile&token=4CE19CA8FCD150A4", MyFragment.this);
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

    @Override
    public void onResponse(String result) {
        Log.i("Alan", "登出成功");
        exitAPP();
    }

    @Override
    public void onResponse(JSONObject jsonObject) {
        String mContent="";
        try {
            JSONObject obj=jsonObject.getJSONObject("data");
            mContent+="在线数量："+obj.getString("onLineCount");
            mContent+="\n离线数量："+obj.getString("offLineCount");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        showDialog(mContent);

    }

    private void showDialog(String mContent) {
        AlertDialog dialog = new AlertDialog.Builder(getActivity(),AlertDialog.THEME_HOLO_LIGHT)
                .setTitle("摄像机实时在线汇总")//设置对话框的标题
                .setMessage(mContent)//设置对话框的内容 //设置对话框的按钮
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();
        dialog.show();
    }
}
