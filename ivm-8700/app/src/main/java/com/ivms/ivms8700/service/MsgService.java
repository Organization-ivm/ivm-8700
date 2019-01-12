package com.ivms.ivms8700.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import com.ivms.ivms8700.R;
import com.ivms.ivms8700.bean.EventEntity;
import com.ivms.ivms8700.control.Constants;
import com.ivms.ivms8700.control.MyApplication;
import com.ivms.ivms8700.utils.LocalDbUtil;
import com.ivms.ivms8700.utils.UIUtil;
import com.ivms.ivms8700.utils.okmanager.OkHttpClientManager;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

public class MsgService extends Service implements OkHttpClientManager.JsonObjectCallback {
    private static final String TAG = "Alan" ;
    private LocalDbUtil localDbUtil;
    private String local_url;
    private String deviceId;
    private String userName;

    // 定位相关
    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public void onCreate() {
        Log.v(TAG, "MsgService onCreate");
        localDbUtil = new LocalDbUtil(this);
        local_url = localDbUtil.getString("local_url");
        deviceId = localDbUtil.getString("local_deviceId");
        userName = localDbUtil.getString("userName");
        super.onCreate();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        getMsg();
        /** 下面是定时器 */
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int anHour = 300000; //定时器时间 5分钟
        long triggerAtTime = SystemClock.elapsedRealtime() + anHour;
        Intent i = new Intent(this, AlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP ,triggerAtTime,pi);
        return super.onStartCommand(intent, flags, startId);
    }
    private void getMsg() {
        String url = "";
        url += local_url + "/shm/msgPush?";
        url += "&token=" + Constants.APP_TOKEN;
        url += "&deviceID=" + deviceId ;
        url += "&userName=" + userName;

        Log.i("Alan", "MsgService消息url=-=" + url);
        OkHttpClientManager.getInstance().asyncJsonObjectByUrl(url, this);
    }
    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        Log.v(TAG, "MsgService onDestroy");
        System.out.println("=-==onDestroy======服务挂掉了，，，，");
        Intent localIntent = new Intent();
        localIntent.setClass(MsgService.this, MsgService.class); //销毁时重新启动Service
        MsgService.this.startService(localIntent);
        super.onDestroy();
    }

    @Override
    public void onResponse(JSONObject jsonObject) {
        String result = "";
        try {
            result = jsonObject.getString("result");
            if (result.equals("success")) {
                EventBus.getDefault().post(new EventEntity(Constants.Event.getMsg,jsonObject));
            }else{
                MyApplication.getIns().setMsgJSONObject(null);//将全局消息设置为null
                Log.i(TAG+"_MsgService",jsonObject.getString("msg"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
