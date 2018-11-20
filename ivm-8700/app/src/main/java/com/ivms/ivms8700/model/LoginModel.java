package com.ivms.ivms8700.model;

import android.os.Handler;
import com.hikvision.sdk.VMSNetSDK;
import com.hikvision.sdk.net.bean.LoginData;
import com.hikvision.sdk.net.business.OnVMSNetSDKBusiness;
import com.ivms.ivms8700.control.Constants;
import com.ivms.ivms8700.presenter.LoginPresenter;

public class LoginModel {
    /**
     * 发送消息的对象
     */
    private static Handler mHandler = new LoginPresenter.ViewHandler();


    public static void login(final String loginAddress, String username, String password, String macAddress, String passwordLevel){

        VMSNetSDK.getInstance().setOnVMSNetSDKBusiness(new OnVMSNetSDKBusiness() {

            @Override
            public void onFailure() {
                mHandler.sendEmptyMessage(Constants.Login.LOGIN_FAILED);
            }

            @Override
            public void loading() {
                mHandler.sendEmptyMessage(Constants.Login.SHOW_LOGIN_PROGRESS);

            }

            @Override
            public void onSuccess(Object data) {
                if (data instanceof LoginData) {
                    TempDatas.getIns().setLoginData((LoginData) data);
                    TempDatas.getIns().setLoginAddr(loginAddress);
                    mHandler.sendEmptyMessage(Constants.Login.LOGIN_SUCCESS);
                    // you can do something by data
                    System.out.println("-------data userID---------"+((LoginData) data).getUserID());
                }
            }

        });
        //android 少了一个参数，所以拼接
       String pamams=macAddress +"&passwordLevel="+passwordLevel;

        // 登录请求
        VMSNetSDK.getInstance().login(loginAddress, username, password, pamams);
    }


}
