package com.ivms.ivms8700.service;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/****
 *
 * Android监听应用进程被系统清理后向服务器发送退出请求
 *
 * Alan
 * */
public class CheckService extends Service {
    private static final String PackageName = "com.ivms.ivms8700";
    private final Timer timerMail = new Timer();
    private ActivityManager activityManager = null;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.e("Alan","   开启检查服务");
        timerMail.schedule(new TimerTask() {
            @Override
            public void run() {
//                Log.e("Alan","   发送消息");
                Message message = new Message();
                message.what = 1;
                mHandler.sendMessage(message);
            }
        }, 1000, 1000);
        super.onCreate();
    }
    @SuppressLint("WrongConstant")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        flags = START_STICKY;
        return super.onStartCommand(intent, flags, startId);
    }
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            try {
                if (!isBackgroundRunning()) {
                    Log.e("Alan","   退出操作");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }
    });

    private boolean isBackgroundRunning() {
        activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        if (activityManager == null) {
            return false;
        }
        List<ActivityManager.RunningTaskInfo> processList = activityManager.getRunningTasks(100);
        for (ActivityManager.RunningTaskInfo info : processList) {

            if (info.baseActivity.getPackageName().startsWith(PackageName)) {
                return true;
            }
        }
        return false;
    }
    /* (non-Javadoc)
     * @see android.app.Service#onDestroy()
     */

    @Override
    public void onDestroy() {
        Log.e("Alan","   销毁服务");
        if (timerMail != null) {
            timerMail.cancel();
        }
        //Handler中提供了去清除Message和Runnable的方法，不再占用多余的队列空间
        mHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

}

