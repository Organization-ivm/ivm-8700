package com.ivms.ivms8700.control;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import com.hik.mcrsdk.MCRSDK;
import com.hik.mcrsdk.rtsp.RtspClient;
import com.hikvision.sdk.VMSNetSDK;
import com.hikvision.sdk.net.bean.LoginData;
import com.ivms.ivms8700.mysdk.MyVMSNetSDK;
import com.ivms.ivms8700.service.CheckExitService;

import org.json.JSONArray;
import org.json.JSONObject;

public class MyApplication extends Application implements AppForegroundStateManager.OnAppForegroundStateChangeListener {
    private static MyApplication ins;
    private LoginData loginData;
    private JSONArray videoList=null;
    private JSONObject msgJSONObject=null;
    @Override
    public void onCreate() {
        super.onCreate();
        ins = this;
        startCheckExiteService();
        MCRSDK.init();
        RtspClient.initLib();
        MCRSDK.setPrint(1, null);
        MyVMSNetSDK.init(this);
        VMSNetSDK.init(this);
        AppForegroundStateManager.getInstance().addListener(this);
    }

    public void startCheckExiteService(){
        Intent intent=new Intent(this,CheckExitService.class);
        this.startService(intent);
    }

    public static MyApplication getIns() {
        return ins;
    }

    private LoginData getLoginData() {
        return loginData;
    }

    public void setLoginData(LoginData loginData) {
        this.loginData = loginData;
    }

    @Override
    public void onAppForegroundStateChange(AppForegroundStateManager.AppForegroundState newState) {
        if (AppForegroundStateManager.AppForegroundState.IN_FOREGROUND == newState) {
            Log.i("Alan","App just entered the foreground. Do something here!");
        } else {
            Log.i("Alan","App just entered the background. Do something here!");
        }
    }

    public JSONArray getVideoList() {
        return videoList;
    }

    public void setVideoList(JSONArray videoList) {
        this.videoList = videoList;
    }

    public JSONObject getMsgJSONObject() {
        return msgJSONObject;
    }

    public void setMsgJSONObject(JSONObject msgJSONObject) {
        this.msgJSONObject = msgJSONObject;
    }
}