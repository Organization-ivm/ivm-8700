package com.ivms.ivms8700.presenter;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;

import com.ivms.ivms8700.control.Constants;
import com.ivms.ivms8700.model.LoginModel;
import com.ivms.ivms8700.view.iview.ILoginView;

public class LoginPresenter implements ILoginPresenter{

     private static ILoginView  iLoginView;
    @Override
    public void login(String loginAddress, String username, String password, String macAddress) {
        iLoginView.showLoginProgress();
        LoginModel.login(loginAddress, username, password, macAddress);

    }
    public LoginPresenter(ILoginView iLoginView) {
        this.iLoginView = iLoginView;
    }

    @SuppressLint("HandlerLeak")
    public static final class ViewHandler extends Handler {

        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Constants.Login.LOGIN_SUCCESS:
                    // 登录成功
                    iLoginView.cancelProgress();
                    iLoginView.onLoginSuccess();
                    break;
                case Constants.Login.LOGIN_FAILED:
                    // 登录失败
                    iLoginView.cancelProgress();
                    iLoginView.onLoginFailed();
                    break;

                default:
                    break;
            }

        }
    }
}
