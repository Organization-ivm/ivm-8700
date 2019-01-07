package com.ivms.ivms8700.service;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/****
 *
 * Android监听应用进程被系统清理后向服务器发送退出请求
 *
 * Alan
 * */
public class CheckExitService extends Service {

    private String packageName = "com.ivms.ivms8700";


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Log.i("packageName","退出了");
        Toast.makeText(CheckExitService.this, "App要退出了", Toast.LENGTH_SHORT).show();
    }

    //service异常停止的回调
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        ActivityManager activtyManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = activtyManager.getRunningAppProcesses();
        for (int i = 0; i < runningAppProcesses.size(); i++) {
            if (packageName.equals(runningAppProcesses.get(i).processName)) {
                Log.i("packageName","app还在运行中");

                Toast.makeText(this, "app还在运行中", Toast.LENGTH_LONG).show();
            }
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("packageName","App检测服务开启了");

        Toast.makeText(CheckExitService.this, "App检测服务开启了", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}



