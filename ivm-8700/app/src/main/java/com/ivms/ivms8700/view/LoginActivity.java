package com.ivms.ivms8700.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ivms.ivms8700.R;
import com.ivms.ivms8700.presenter.LoginPresenter;
import com.ivms.ivms8700.utils.LocalDbUtil;
import com.ivms.ivms8700.utils.UIUtil;
import com.ivms.ivms8700.utils.okmanager.OkHttpClientManager;
import com.ivms.ivms8700.view.iview.ILoginView;

import org.json.JSONObject;


public class LoginActivity extends Activity implements ILoginView, View.OnClickListener ,OkHttpClientManager.JsonStringCallback{
    private final String TAG ="Alan";
    private Button login_btn;
    private LoginPresenter presenter;
    private TextView set_btn;
    private EditText username;
    private EditText pwd;
    private LocalDbUtil localDbUtil=null;
    private OkHttpClientManager okHttpClientManager=null;

    private static final int MY_PERMISSION_REQUEST_CODE = 10000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        // 一次请求多个权限, 如果其他有权限是已经授予的将会自动忽略掉
        ActivityCompat.requestPermissions( this, new String[] {
                Manifest.permission.WRITE_EXTERNAL_STORAGE}
                , MY_PERMISSION_REQUEST_CODE );
        okHttpClientManager=OkHttpClientManager.getInstance();
    }
    //初始化控件
    private void initView() {
        localDbUtil=new LocalDbUtil(this);
        presenter=new LoginPresenter(this);
        login_btn=(Button)findViewById(R.id.login_btn);
        login_btn.setOnClickListener(this);
        set_btn=(TextView)findViewById(R.id.set_btn);
        set_btn.setOnClickListener(this);

        username=(EditText)findViewById(R.id.username);
        pwd=(EditText)findViewById(R.id.pwd);
    }
    @Override
    public void onClick(View v) {
       switch (v.getId()){
           case R.id.login_btn:
//               okHttpClientManager.asyncJsonStringByURL("http://222.66.82.4:80/shm/login?userName=mobile&passWord=123456&videoIP=222\n" +
//                       ".66.82.2&token=4CE19CA8FCD150A4 ",this);
//               String url =localDbUtil.getString("local_url");
//               String userName=username.getText().toString().trim();
//               String password=pwd.getText().toString().trim();
//               if(checkLoginData(url,userName,password)){
                   String url = "https://222.66.82.2:443";
                   String userName="admin";
                   String password = "Admin13761101256";
                   String macAddress = getMacAddress();
                   String passwordLevel="2";
                   presenter.login(url, userName, password, macAddress, passwordLevel);
//               }
               break;
           case R.id.set_btn:
               Intent intent = new Intent(this, SetingActivity.class);
               startActivity(intent);
               break;
       }
    }
    //判断参数是否合法
//    private boolean checkLoginData(String url, String userName, String password) {
//
//
//    }

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

    //网络请求返回
    @Override
    public void onResponse(String result) {
        Log.i(TAG,"网络请求成功了！"+result);
    }
}
