package com.ivms.ivms8700.service;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.List;

public class AlarmReceiver  extends BroadcastReceiver{

	public AlarmReceiver() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		boolean isrun=  isServiceExisted(context,MsgService.class.getName());
		Log.i("Alan","isrun=-="+isrun);
		if(isrun){
			Intent is = new Intent(context, MsgService.class);
			context.stopService(is);
		}
		Intent i = new Intent(context, MsgService.class);
		context.startService(i);
	}
	public static boolean isServiceExisted(Context context, String className) {
		ActivityManager activityManager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningServiceInfo> serviceList =activityManager.getRunningServices(Integer.MAX_VALUE);
		if(!(serviceList.size() > 0)) {
			return false;
		}
		for(int i = 0; i < serviceList.size(); i++) {
			ActivityManager.RunningServiceInfo serviceInfo = serviceList.get(i);
			ComponentName serviceName = serviceInfo.service;
			if(serviceName.getClassName().equals(className)) {
				return true;
			}
		}
		return false;
	}
}
