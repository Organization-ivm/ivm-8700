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
    public void login(String loginAddress, String username, String password, String macAddress, String passwordLevel) {
        iLoginView.showLoginProgress();
        LoginModel.login(loginAddress, username, password, macAddress,passwordLevel);

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
                case Constants.Login.SHOW_LOGIN_PROGRESS:
                    iLoginView.showLoginProgress();
                    break;
                case Constants.Login.CANCEL_PROGRESS:
                    iLoginView.cancelProgress();
                    break;
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

                case Constants.Logout.SHOW_LOGOUT_PROGRESS:
                    iLoginView.showLogoutProgress();
                    break;
                case Constants.Logout.CANCEL_PROGRESS:
                    iLoginView.cancelProgress();
                    break;
                case Constants.Logout.LOGOUT_SUCCESS:
                    // 退出成功
                    iLoginView.cancelProgress();
                    iLoginView.onLogoutSuccess();
                    break;
                case Constants.Logout.LOGOUT_FAILED:
                    // 登录失败
                    iLoginView.cancelProgress();
                    iLoginView.onLogoutFailed();
                    break;
                default:
                    break;
            }

        }
    }
}
