package com.ivms.ivms8700.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ivms.ivms8700.R;
import com.ivms.ivms8700.presenter.LoginPresenter;
import com.ivms.ivms8700.utils.UIUtil;
import com.ivms.ivms8700.view.iview.ILoginView;


public class LoginActivity extends Activity implements ILoginView, View.OnClickListener {

    private Button login_btn;
    private LoginPresenter presenter;
    private TextView set_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
    }
    //初始化控件
    private void initView() {
        presenter=new LoginPresenter(this);
        login_btn=(Button)findViewById(R.id.login_btn);
        login_btn.setOnClickListener(this);
        set_btn=(TextView)findViewById(R.id.set_btn);
        set_btn.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
       switch (v.getId()){
           case R.id.login_btn:
               String url = "https://222.66.82.2:443";
               String userName="admin";
               String password = "Admin13761101256";
               String macAddress = getMacAddress();
               String passwordLevel="2";
               presenter.login(url,userName,password,macAddress,passwordLevel);
               break;
           case R.id.set_btn:
               Intent intent = new Intent(this, SetingActivity.class);
               startActivity(intent);

               break;


       }
    }

    @Override
    public void showLoginProgress() {
        UIUtil.showProgressDialog(this, R.string.login_process_tip);
    }

    @Override
    public void showLogoutProgress() {
        UIUtil.showProgressDialog(this, R.string.logout_process_tip);
    }

    @Override
    public void cancelProgress() {
        UIUtil.cancelProgressDialog();
    }

    @Override
    public void onLoginFailed() {
        UIUtil.showToast(this, R.string.login_failed);
    }

    @Override
    public void onLoginSuccess() {
        UIUtil.showToast(this, R.string.login_success);
        Intent intent = new Intent(this, MainActivity.class);
//        intent.putExtra(Constants.IntentKey.GET_ROOT_NODE, true);
        startActivity(intent);
    }

    @Override
    public void onLogoutSuccess() {
            UIUtil.showToast(this, R.string.logout_success);
    }

    @Override
    public void onLogoutFailed() {
        UIUtil.showToast(this, R.string.logout_failed);
    }

    /**
     * 获取登录设备mac地址
     *
     * @return
     */
    public String getMacAddress() {
        @SuppressLint("WifiManagerLeak")
        WifiManager wm = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        String mac = wm.getConnectionInfo().getMacAddress();
        return mac == null ? "" : mac;
    }


}
