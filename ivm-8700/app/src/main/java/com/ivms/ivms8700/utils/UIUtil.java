package com.ivms.ivms8700.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.ivms.ivms8700.view.customui.MyProgressDialog;

import java.text.SimpleDateFormat;
import java.util.Date;


public final class UIUtil {

	private static MyProgressDialog dialog;

	private UIUtil() {
	}

	public static void showToast(Context c, int resId) {
		Toast.makeText(c, resId, Toast.LENGTH_SHORT).show();
	}

	public static void showToast(Context c, String desc) {
		Toast.makeText(c, desc, Toast.LENGTH_SHORT).show();
	}

	public static void showProgressDialog(Context c, String msg) {
		dialog = new MyProgressDialog(c,msg);
		dialog.show();
	}

	public static void showProgressDialog(Context c, int resId) {
		dialog = new MyProgressDialog(c,resId);
		dialog.show();
	}

	public static void cancelProgressDialog() {
		if (dialog != null) {
			dialog.cancel();
			dialog = null;
		}
	}

	public static String timeStamp2Date(String seconds) {
		if (seconds == null || seconds.isEmpty() || seconds.equals("null")) {
			return "";
		}
		String format = "yyyy/MM/dd HH:mm:ss";
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(new Date(Long.valueOf(seconds)));
	}
	public static String getDeviceId(Context context){
		String  deviceId = null;
		if(ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
			TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			deviceId = tm.getDeviceId();
		}
		return deviceId;
	}

}
