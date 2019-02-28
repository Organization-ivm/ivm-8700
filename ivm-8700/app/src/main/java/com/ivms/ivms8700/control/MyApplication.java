package com.ivms.ivms8700.control;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.hik.mcrsdk.MCRSDK;
import com.hik.mcrsdk.rtsp.RtspClient;
import com.hik.mcrsdk.talk.TalkClientSDK;
import com.hikvision.sdk.VMSNetSDK;
import com.hikvision.sdk.utils.FileUtils;
import com.ivms.ivms8700.service.CheckExitService;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MyApplication extends Application implements AppForegroundStateManager.OnAppForegroundStateChangeListener {
    private static MyApplication ins;
    private JSONArray videoList=null;
    private JSONObject msgJSONObject=null;
    @Override
    public void onCreate() {
        super.onCreate();
        ins = this;
        startCheckExiteService();

        AppForegroundStateManager.getInstance().addListener(this);

        MCRSDK.init();
        // 初始化RTSP
        RtspClient.initLib();
        MCRSDK.setPrint(1, null);
        // 初始化语音对讲
        TalkClientSDK.initLib();
        // SDK初始化
        VMSNetSDK.init(this);

        releaseDemoVideo();
    }

    public void startCheckExiteService(){
        Intent intent=new Intent(this,CheckExitService.class);
        this.startService(intent);
    }

    public static MyApplication getIns() {
        return ins;
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

    /**
     * 获取登录设备mac地址
     *
     * @return Mac地址
     */
    public String getMacAddress() {
        WifiManager wm = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo connectionInfo = wm.getConnectionInfo();
        String mac = connectionInfo.getMacAddress();
        return mac == null ? "02:00:00:00:00:00" : mac;
    }

    /**
     * 释放demo.mp4到手机sd卡
     */
    private void releaseDemoVideo() {
        File demoVideo = new File(FileUtils.getVideoDirPath() + "/demo.mp4");
        if (demoVideo.exists()) return;
        AssetManager assetManager = getAssets();
        try {
            InputStream inputStream = assetManager.open("demo.mp4");
            FileOutputStream outputStream = new FileOutputStream(demoVideo, false);
            byte[] buffer = new byte[1024];
            while (true) {
                int len = inputStream.read(buffer);
                if (len == -1) {
                    break;
                }
                outputStream.write(buffer, 0, len);
            }
            inputStream.close();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}