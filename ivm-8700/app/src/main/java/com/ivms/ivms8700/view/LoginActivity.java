package com.ivms.ivms8700.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.ivms.ivms8700.R;
import com.ivms.ivms8700.control.AppForegroundStateManager;
import com.ivms.ivms8700.control.Constants;
import com.ivms.ivms8700.control.MyApplication;
import com.ivms.ivms8700.presenter.LoginPresenter;
import com.ivms.ivms8700.service.CheckService;
import com.ivms.ivms8700.utils.LocalDbUtil;
import com.ivms.ivms8700.utils.UIUtil;
import com.ivms.ivms8700.utils.okmanager.OkHttpClientManager;
import com.ivms.ivms8700.view.iview.ILoginView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class LoginActivity extends Activity implements ILoginView, View.OnClickListener, OkHttpClientManager.JsonObjectCallback {
    private final String TAG = "Alan";
    private Button login_btn;
    private LoginPresenter presenter;
    private TextView set_btn;
    private EditText username;
    private EditText pwd;
    private LocalDbUtil localDbUtil = null;
    private OkHttpClientManager okHttpClientManager = null;

    private static final int MY_PERMISSION_REQUEST_CODE = 10000;
    private String videoUser = "";//video二次登录后台返回用户名
    private String videoPassword = "";//video二次登录后台返回密码
    private String local_url = "";
    private String local_port = "";
    private String local_video_url = "";
    private String local_video_ip = "";
    private String userName = "";
    private String local_video_port = "";
    private String password = "";
    private static final int REQUEST_READ_PHONE_STATE = 1001;
    private static final int REQUEST_WRITE_EXTERNAL_STORAGE = 1002;

    //初始化控件
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        registerPression();
        // 一次请求多个权限, 如果其他有权限是已经授予的将会自动忽略掉
//        ActivityCompat.requestPermissions(this, new String[]{
//                        Manifest.permission.WRITE_EXTERNAL_STORAGE}
//                , MY_PERMISSION_REQUEST_CODE);
        okHttpClientManager = OkHttpClientManager.getInstance();
        //初始化
        if (localDbUtil.getString("local_url").isEmpty()) {
            localDbUtil.setString("local_ip", getString(R.string.url_et));
            localDbUtil.setString("local_url", getString(R.string.http_et) + getString(R.string.url_et));
            localDbUtil.setString("local_port", getString(R.string.port_et));
            localDbUtil.setString("local_video_url", getString(R.string.https_et) + getString(R.string.video_url_et));
            localDbUtil.setString("local_video_ip", getString(R.string.video_url_et));
            localDbUtil.setString("local_video_port", getString(R.string.video_port_et));
        }
        localDbUtil.setString("local_deviceId", UIUtil.getDeviceId(MyApplication.getIns()));
    }

    private void initView() {
        localDbUtil = new LocalDbUtil(this);
        presenter = new LoginPresenter(this);
        login_btn = (Button) findViewById(R.id.login_btn);
        login_btn.setOnClickListener(this);
        set_btn = (TextView) findViewById(R.id.set_btn);
        set_btn.setOnClickListener(this);
        username = (EditText) findViewById(R.id.username);
        String dbUser= localDbUtil.getString("userName");
        if(!dbUser.isEmpty()){
            username.setText(dbUser);
        }
        pwd = (EditText) findViewById(R.id.pwd);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_btn:
                local_url = localDbUtil.getString("local_url");
                local_port = localDbUtil.getString("local_port");
                local_video_url = localDbUtil.getString("local_video_url");
//                userName = "mobile";
                local_video_ip = localDbUtil.getString("local_video_ip");
                local_video_port = localDbUtil.getString("local_video_port");
                userName = username.getText().toString().trim();
                password = pwd.getText().toString().trim();
                if (checkLoginData(local_url, local_port, local_video_ip, local_video_port, userName, password)) {
                    String getUrl = local_url + "/shm/login?userName=" + userName + "&passWord=" + password + "&videoIP=" + local_video_ip + "&token=" + Constants.APP_TOKEN;
                    Log.i("Alan", "getUrl=-=" + getUrl);
                    okHttpClientManager.asyncJsonObjectByUrl(getUrl, this);
//                  okHttpClientManager.asyncJsonObjectByUrl("http://222.66.82.4:80/shm/login?userName=mobile&passWord=123456&videoIP=222.66.82.2&token=" + Constants.APP_TOKEN, this);
                }
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
        localDbUtil.setString("userName", userName);
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

    @Override
    protected void onStart() {
        super.onStart();
        AppForegroundStateManager.getInstance().onActivityVisible(this);
    }

    @Override
    protected void onStop() {
        AppForegroundStateManager.getInstance().onActivityNotVisible(this);
        super.onStop();
    }

    //网络请求返回
    @Override
    public void onResponse(JSONObject jsonObject) {

        try {
            if (jsonObject.getString("data") != null) {
                String data = jsonObject.getString("data");
                String msg = jsonObject.getString("msg");
                String result = jsonObject.getString("result");
                if (result.equals("success")) {//解析登录返回数据
                    JSONObject obj = new JSONObject(data);
                    videoUser = obj.getString("videoUser");
                    videoPassword = obj.getString("videoPassword");
                    JSONArray videoList = obj.getJSONArray("list");//当前用户能查看的监控点列表
                    Log.i(TAG, "videoList=-=" + videoList);
                    MyApplication.getIns().setVideoList(videoList);
                    Log.i("Alan", "后台登录成功，开始登录ivms后台..");
                    String macAddress = getMacAddress();
                    String passwordLevel = "2";
                    presenter.login(local_video_url, videoUser, videoPassword, macAddress, passwordLevel);
                } else {
                    UIUtil.showToast(this, msg);
                }

            } else {
                UIUtil.cancelProgressDialog();
                UIUtil.showToast(this, R.string.login_failed);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void registerPression() {
        int permissionWriteFile = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED ||  permissionWriteFile != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.READ_PHONE_STATE,Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, REQUEST_READ_PHONE_STATE);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_READ_PHONE_STATE:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {

                    localDbUtil.setString("local_deviceId", UIUtil.getDeviceId(MyApplication.getIns()));
                }
                if((grantResults.length > 1)  && (grantResults[1] == PackageManager.PERMISSION_GRANTED)){

                }
                break;
            default:
                break;
        }
    }


    //判断参数是否合法
    private boolean checkLoginData(String url, String port, String video_url, String video_port, String userName, String password) {
        if (url.isEmpty()) {
            UIUtil.showToast(this, "业务地址不能为空");
            return false;
        }
        if (port.isEmpty()) {
            UIUtil.showToast(this, "业务端口不能为空");
            return false;
        }
        if (video_url.isEmpty()) {
            UIUtil.showToast(this, "视频地址不能为空");
            return false;
        }
        if (video_port.isEmpty()) {
            UIUtil.showToast(this, "视频端口不能为空");
            return false;
        }
        if (userName.isEmpty()) {
            UIUtil.showToast(this, "请输入用户名");
            return false;
        }
        if (password.isEmpty()) {
            UIUtil.showToast(this, "请输入密码");
            return false;
        }
        return true;
    }
}
