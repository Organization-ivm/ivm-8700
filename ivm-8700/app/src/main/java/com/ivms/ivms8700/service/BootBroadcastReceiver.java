package com.ivms.ivms8700.service;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
public class BootBroadcastReceiver extends BroadcastReceiver {
    //重写onReceive方法
    @Override
    public void onReceive(Context context, Intent intent) {
        //后边的XXX.class就是要启动的服务
        Log.v("Alan", "开机自动服务自动启动.....");
        Intent service = new Intent(context,com.ivms.ivms8700.service.CheckService.class);
        context.startService(service);

        //启动应用，参数为需要自动启动的应用的包名
//        Intent intent = getPackageManager().getLaunchIntentForPackage(packageName);
//        context.startActivity(intent );
    }
}
