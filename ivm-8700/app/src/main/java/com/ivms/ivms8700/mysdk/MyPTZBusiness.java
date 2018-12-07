package com.ivms.ivms8700.mysdk;

import android.content.Context;

import com.hikvision.sdk.data.TempData;
import com.hikvision.sdk.net.asynchttp.AsyncHttpExecute;
import com.hikvision.sdk.net.bean.CameraInfo;
import com.hikvision.sdk.net.bean.LoginData;
import com.hikvision.sdk.net.bean.MAGServer;
import com.hikvision.sdk.utils.CNetSDKLog;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.SyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;

public class MyPTZBusiness  extends MyBaseBusiness {
    private static final String TAG = MyPTZBusiness.class.getSimpleName();
    private static volatile MyPTZBusiness mIns;

    private MyPTZBusiness() {
    }

    public static MyPTZBusiness getInstance() {
        if (mIns == null) {
            Class var0 = MyPTZBusiness.class;
            synchronized(MyPTZBusiness.class) {
                if (mIns == null) {
                    mIns = new MyPTZBusiness();
                }
            }
        }

        return mIns;
    }

    public boolean ptzCtrl(CameraInfo cameraInfo, final String action, int command) {
        LoginData data = TempData.getIns().getLoginData();
        if (data != null && cameraInfo != null) {
            MAGServer mag = data.getMAGServer();
            if (mag == null) {
                return false;
            } else {
                boolean isNcgDevice = 1 == cameraInfo.getCascadeFlag();
                StringBuffer stringBuffer = new StringBuffer();
                if (!isNcgDevice) {
                    stringBuffer.append("<PTZControl>");
                    stringBuffer.append("<SessionId>").append(data.getSessionID()).append("</SessionId>");
                    stringBuffer.append("<SzCamIndexCode>").append(cameraInfo.getSysCode()).append("</SzCamIndexCode>");
                    stringBuffer.append("<IPtzCommand>").append(command).append("</IPtzCommand>");
                    if (action.equals("START")) {
                        stringBuffer.append("<IAction>").append(0).append("</IAction>");
                    } else {
                        stringBuffer.append("<IAction>").append(1).append("</IAction>");
                    }

                    if (command != 9 && command != 39 && command != 38) {
                        stringBuffer.append("<IIndex>").append(5).append("</IIndex>");
                    }

                    stringBuffer.append("<ISpeed>").append(5).append("</ISpeed>");
                    stringBuffer.append("<IPriority>").append(100).append("</IPriority>");
                    stringBuffer.append("<IUserId>").append("").append("</IUserId>");
                    stringBuffer.append("<IMatrixCameraId>").append("").append("</IMatrixCameraId>");
                    stringBuffer.append("<IMonitorId>").append("").append("</IMonitorId>");
                    stringBuffer.append("<ILockTime>").append("").append("</ILockTime>");
                    stringBuffer.append("<IPtzCruisePoint>").append("").append("</IPtzCruisePoint>");
                    stringBuffer.append("<IPtzCruiseInput>").append("").append("</IPtzCruiseInput>");
                    stringBuffer.append("<Param1>").append(5).append("</Param1>");
                    stringBuffer.append("<Param2>").append(2).append("</Param2>");
                    stringBuffer.append("<Param3>").append(2).append("</Param3>");
                    stringBuffer.append("<Param4>").append(2).append("</Param4>");
                    stringBuffer.append("</PTZControl>");
                } else {
                    stringBuffer.append("<PTZControl>");
                    stringBuffer.append("<SzCamIndexCode>").append(cameraInfo.getGbSysCode()).append("</SzCamIndexCode>");
                    stringBuffer.append("<IPtzCommand>").append(command).append("</IPtzCommand>");
                    if (action.equals("START")) {
                        stringBuffer.append("<IAction>").append(0).append("</IAction>");
                    } else {
                        stringBuffer.append("<IAction>").append(1).append("</IAction>");
                    }

                    if (command != 9 && command != 39 && command != 38) {
                        stringBuffer.append("<IPresetIndex>").append(0).append("</IPresetIndex>");
                    }

                    stringBuffer.append("<ISpeed>").append(4).append("</ISpeed>");
                    stringBuffer.append("<IPriority>").append(TempData.getIns().getLoginData().getUserAuthority()).append("</IPriority>");
                    stringBuffer.append("<IMatrixCameraId></IMatrixCameraId>");
                    stringBuffer.append("<IMonitorId></IMonitorId>");
                    stringBuffer.append("<Param1>").append(17).append("</Param1>");
                    stringBuffer.append("<Param2>").append(17).append("</Param2>");
                    stringBuffer.append("<Param3>").append(17).append("</Param3>");
                    stringBuffer.append("<Param4>").append(17).append("</Param4>");
                    stringBuffer.append("</PTZControl>");
                }

                String url = "";
                if (isNcgDevice) {
                    url = String.format("http://%s:%d/mag/ncgPtz", mag.getMAGAddr(), mag.getMAGHttpSerPort());
                } else {
                    url = String.format("http://%s:%d/mag/ptz", mag.getMAGAddr(), mag.getMAGHttpSerPort());
                }

                int commandFlag = isNcgDevice ? 17 : 1;
                Context context =MyVMSNetSDK.getApplication().getApplicationContext();
                MyAsyncHttpExecute.getIns().execute(context, url, commandFlag, stringBuffer.toString(), new TextHttpResponseHandler() {
                    public void onSuccess(int arg0, Header[] arg1, String arg2) {
                        CNetSDKLog.info("ptzCtrlEZVIZ: onSuccess" + action);
                        MyPTZBusiness.this.isNetSuccess = true;
                        CNetSDKLog.info("onSuccess response--->" + arg2);
                    }

                    public void onFailure(int arg0, Header[] arg1, String arg2, Throwable arg3) {
                        CNetSDKLog.info("ptzCtrlEZVIZ:onFailure" + action);
                        MyPTZBusiness.this.isNetSuccess = false;
                        CNetSDKLog.info("onFailure response--->" + arg2);
                    }
                });
                return this.isNetSuccess;
            }
        } else {
            return false;
        }
    }

    public boolean ptzPresetCtrl(CameraInfo cameraInfo, final String action, int command, int presetIndex) {
        LoginData data = TempData.getIns().getLoginData();
        if (data != null && cameraInfo != null) {
            MAGServer mag = data.getMAGServer();
            if (mag == null) {
                return false;
            } else {
                boolean isNcgDevice = 1 == cameraInfo.getCascadeFlag();
                StringBuffer stringBuffer = new StringBuffer();
                if (!isNcgDevice) {
                    stringBuffer.append("<PTZControl>");
                    stringBuffer.append("<SessionId>").append(data.getSessionID()).append("</SessionId>");
                    stringBuffer.append("<SzCamIndexCode>").append(cameraInfo.getSysCode()).append("</SzCamIndexCode>");
                    stringBuffer.append("<IPtzCommand>").append(command).append("</IPtzCommand>");
                    if (action.equals("START")) {
                        stringBuffer.append("<IAction>").append(0).append("</IAction>");
                    } else {
                        stringBuffer.append("<IAction>").append(1).append("</IAction>");
                    }

                    if (command != 9 && command != 8 && command != 39 && command != 38) {
                        stringBuffer.append("<IIndex>").append(0).append("</IIndex>");
                    } else {
                        stringBuffer.append("<IIndex>").append(presetIndex).append("</IIndex>");
                    }

                    stringBuffer.append("<ISpeed>").append(4).append("</ISpeed>");
                    stringBuffer.append("<IPriority>").append(17).append("</IPriority>");
                    stringBuffer.append("<IUserId>").append(17).append("</IUserId>");
                    stringBuffer.append("<IMatrixCameraId>").append(17).append("</IMatrixCameraId>");
                    stringBuffer.append("<IMonitorId>").append(17).append("</IMonitorId>");
                    stringBuffer.append("<ILockTime>").append(17).append("</ILockTime>");
                    stringBuffer.append("<IPtzCruisePoint>").append(17).append("</IPtzCruisePoint>");
                    stringBuffer.append("<IPtzCruiseInput>").append(17).append("</IPtzCruiseInput>");
                    stringBuffer.append("<Param1>").append(17).append("</Param1>");
                    stringBuffer.append("<Param2>").append(17).append("</Param2>");
                    stringBuffer.append("<Param3>").append(17).append("</Param3>");
                    stringBuffer.append("<Param4>").append(17).append("</Param4>");
                    stringBuffer.append("</PTZControl>");
                } else {
                    stringBuffer.append("<PTZControl>");
                    stringBuffer.append("<SzCamIndexCode>").append(cameraInfo.getGbSysCode()).append("</SzCamIndexCode>");
                    stringBuffer.append("<IPtzCommand>").append(command).append("</IPtzCommand>");
                    if (action.equals("START")) {
                        stringBuffer.append("<IAction>").append(0).append("</IAction>");
                    } else {
                        stringBuffer.append("<IAction>").append(1).append("</IAction>");
                    }

                    if (command != 9 && command != 8 && command != 39 && command != 38) {
                        stringBuffer.append("<IPresetIndex>").append(0).append("</IPresetIndex>");
                    } else {
                        stringBuffer.append("<IPresetIndex>").append(presetIndex).append("</IPresetIndex>");
                    }

                    stringBuffer.append("<ISpeed>").append(4).append("</ISpeed>");
                    stringBuffer.append("<IPriority>").append(17).append("</IPriority>");
                    stringBuffer.append("<IMatrixCameraId></IMatrixCameraId>");
                    stringBuffer.append("<IMonitorId></IMonitorId>");
                    stringBuffer.append("<Param1>").append(17).append("</Param1>");
                    stringBuffer.append("<Param2>").append(17).append("</Param2>");
                    stringBuffer.append("<Param3>").append(17).append("</Param3>");
                    stringBuffer.append("<Param4>").append(17).append("</Param4>");
                    stringBuffer.append("</PTZControl>");
                }

                String url = "";
                if (isNcgDevice) {
                    url = String.format("http://%s:%d/mag/ncgPtz", mag.getMAGAddr(), mag.getMAGHttpSerPort());
                } else {
                    url = String.format("http://%s:%d/mag/ptz", mag.getMAGAddr(), mag.getMAGHttpSerPort());
                }

                int commandFlag = isNcgDevice ? 17 : 1;
                AsyncHttpExecute.getIns().execute(MyVMSNetSDK.getApplication().getApplicationContext(), url, commandFlag, stringBuffer.toString(), new AsyncHttpResponseHandler() {
                    public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                        MyPTZBusiness.this.isNetSuccess = true;
                        CNetSDKLog.info("ptzPresetCtrlEZVIZ:onSuccess" + action);

                        try {
                            CNetSDKLog.info("onSuccess response--->" + new String(arg2, "utf-8"));
                         } catch (Exception var5) {
                            var5.printStackTrace();
                        }

                    }

                    public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
                        CNetSDKLog.info("ptzPresetCtrlEZVIZ:onFailure" + action);
                        MyPTZBusiness.this.isNetSuccess = false;

                        try {
                            CNetSDKLog.info("onFailure response--->" + new String(arg2, "utf-8"));
                        } catch (Exception var6) {
                            var6.printStackTrace();
                        }

                    }
                }, new SyncHttpClient());
                return this.isNetSuccess;
            }
        } else {
            return false;
        }
    }
}
