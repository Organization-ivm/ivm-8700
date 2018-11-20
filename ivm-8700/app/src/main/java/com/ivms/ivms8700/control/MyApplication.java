package com.ivms.ivms8700.control;

import android.app.Application;

import com.hik.mcrsdk.MCRSDK;
import com.hik.mcrsdk.rtsp.RtspClient;
import com.hikvision.sdk.VMSNetSDK;
import com.hikvision.sdk.net.bean.LoginData;

public class MyApplication extends Application {
    private static MyApplication ins;
    private LoginData loginData;
    @Override
    public void onCreate() {
        super.onCreate();
        ins = this;
        MCRSDK.init();
        RtspClient.initLib();
        MCRSDK.setPrint(1, null);
        VMSNetSDK.init(this);
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
}