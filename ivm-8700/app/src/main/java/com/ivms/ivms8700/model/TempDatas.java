package com.ivms.ivms8700.model;

import com.hikvision.sdk.net.bean.Camera;
import com.hikvision.sdk.net.bean.LoginData;

public class TempDatas {

    private static TempDatas ins = new TempDatas();

    /**
     * 登录返回的数据
     */
    private LoginData loginData;


    /**
     * 登陆地址
     */
    private String loginAddr;

    /**
     * 监控点信息，用作临时传递数据用
     */
    private Camera camera;

    public static TempDatas getIns() {
        return ins;
    }

    /**
     * 设置登录成功返回的信息
     * @param loginData
     * @since V1.0
     */
    public void setLoginData(LoginData loginData) {
        this.loginData = loginData;
    }

    /**
     * 获取登录成功返回的信息
     * @return
     * @since V1.0
     */
    public LoginData getLoginData() {
        return loginData;
    }

    public String getLoginAddr() {
        return loginAddr;
    }

    public void setLoginAddr(String loginAddr) {
        this.loginAddr = loginAddr;
    }

    /**
     * 保存监控点信息
     * @param camera
     * @since V1.0
     */
    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    /**
     * 获取监控点信息
     * @return
     * @since V1.0
     */
    public Camera getCamera() {
        return camera;
    }
}
