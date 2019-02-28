package com.ivms.ivms8700.control;

import com.hikvision.sdk.net.bean.LoginData;

/**
 * <p>临时数据实体类</p>
 *
 * @author zhangwei59 2017/3/10 14:29
 * @version V1.0.0
 */
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

    public static TempDatas getIns() {
        return ins;
    }

    public void setLoginData(LoginData loginData) {
        this.loginData = loginData;
    }

    public LoginData getLoginData() {
        return loginData;
    }

    public String getLoginAddr() {
        return loginAddr;
    }

    public void setLoginAddr(String loginAddr) {
        this.loginAddr = loginAddr;
    }

}
