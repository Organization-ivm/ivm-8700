package com.ivms.ivms8700.model;

import android.os.Handler;

import com.hikvision.sdk.VMSNetSDK;
import com.hikvision.sdk.net.bean.LoginData;
import com.hikvision.sdk.net.business.OnVMSNetSDKBusiness;
import com.hikvision.sdk.utils.SDKUtil;
import com.ivms.ivms8700.control.Constants;
import com.ivms.ivms8700.control.TempDatas;
import com.ivms.ivms8700.presenter.LoginPresenter;

public class LoginModel {

    /**
     * 发送消息的对象
     */
    private static Handler mHandler = new LoginPresenter.ViewHandler();


    public static void login(final String loginAddress, String userName, String password, String macAddress){
        VMSNetSDK.getInstance().Login(loginAddress, userName, password, macAddress, new OnVMSNetSDKBusiness() {
            @Override
            public void onFailure() {
                mHandler.sendEmptyMessage(Constants.Login.LOGIN_FAILED);
            }

            @Override
            public void onSuccess(Object obj) {
                if (obj instanceof LoginData) {
                    mHandler.sendEmptyMessage(Constants.Login.LOGIN_SUCCESS);
                    //存储登录数据
                    TempDatas.getIns().setLoginData((LoginData) obj);
                    TempDatas.getIns().setLoginAddr(loginAddress);
//                    SharedPreferences.Editor editor = sharedPreferences.edit();
//                    editor.putString(Constants.USER_NAME, mUserEdit.getText().toString().trim());
//                    editor.putString(Constants.PASSWORD, mPsdEdit.getText().toString().trim());
//                    editor.putString(Constants.ADDRESS_NET, mUrlEdit.getText().toString().trim());
//                    editor.apply();
                    //解析版本号
                    String appVersion = ((LoginData) obj).getVersion();
                    SDKUtil.analystVersionInfo(appVersion);

                }
            }
        });

    }


}
