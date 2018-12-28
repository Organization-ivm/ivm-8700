package com.ivms.ivms8700.control;

import android.app.Application;
import android.util.Log;

import com.hik.mcrsdk.MCRSDK;
import com.hik.mcrsdk.rtsp.RtspClient;
import com.hikvision.sdk.VMSNetSDK;
import com.hikvision.sdk.net.bean.LoginData;
import com.ivms.ivms8700.mysdk.MyVMSNetSDK;

import org.json.JSONArray;

public class MyApplication extends Application implements AppForegroundStateManager.OnAppForegroundStateChangeListener {
    private static MyApplication ins;
    private LoginData loginData;
    private JSONArray videoList=null;
    @Override
    public void onCreate() {
        super.onCreate();
        ins = this;
        MCRSDK.init();
        RtspClient.initLib();
        MCRSDK.setPrint(1, null);
        MyVMSNetSDK.init(this);
        VMSNetSDK.init(this);
        AppForegroundStateManager.getInstance().addListener(this);
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
}