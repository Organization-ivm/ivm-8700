package com.ivms.ivms8700.mysdk;

import android.app.Application;

import com.hikvision.sdk.businesssdk.ILoginNetSDK;
import com.hikvision.sdk.businesssdk.IResourceNetSDK;
import com.hikvision.sdk.net.asynchttp.AsyncHttpExecute;
import com.hikvision.sdk.net.bean.Camera;
import com.hikvision.sdk.net.bean.CameraInfo;
import com.hikvision.sdk.net.bean.SubResourceNodeBean;
import com.hikvision.sdk.net.business.LiveOrPalyBackBusiness;
import com.hikvision.sdk.net.business.LoginBusiness;
import com.hikvision.sdk.net.business.OnVMSNetSDKBusiness;
import com.hikvision.sdk.net.business.PTZBusiness;
import com.hikvision.sdk.net.business.ResourceBusiness;
import com.hikvision.sdk.utils.SDKUtil;

import org.apache.http.conn.ssl.SSLSocketFactory;

import java.util.Calendar;

public class MyVMSNetSDK implements ILoginNetSDK, IResourceNetSDK {
    public static final String TAG = MyVMSNetSDK.class.getSimpleName();
    public static Application APPLICATION;
    private String build = "20150517";
    private int mLastError = 0;
    private String mLastErrorDescribe = "no error";
    private static volatile MyVMSNetSDK mVMSNetSDK = null;
    private OnVMSNetSDKBusiness mOnVMSNetSDKBusiness;

    private MyVMSNetSDK() {
    }

    public static MyVMSNetSDK getInstance() {
        if (mVMSNetSDK == null) {
            Class var0 = MyVMSNetSDK.class;
            synchronized(MyVMSNetSDK.class) {
                if (mVMSNetSDK == null) {
                    mVMSNetSDK = new MyVMSNetSDK();
                }
            }
        }

        return mVMSNetSDK;
    }

    public static void init(Application application) {
        APPLICATION = application;
    }

    public static Application getApplication() {
        return APPLICATION;
    }

    public int getLastErrorCode() {
        return this.mLastError;
    }

    public String getLastErrorDesc() {
        return this.mLastErrorDescribe;
    }

    public void setSSLSocketFactory(SSLSocketFactory sf) {
        AsyncHttpExecute.getIns().setSSLSocketFactory(sf);
    }

    public boolean login(String loginAddress, String username, String password, String macAddress) {
        boolean result = LoginBusiness.getInstance().login(loginAddress, username, password, macAddress, this.mOnVMSNetSDKBusiness);
        return result;
    }

    public boolean logout() {
        boolean result = LoginBusiness.getInstance().logout(this.mOnVMSNetSDKBusiness);
        return result;
    }

    public Camera initCameraInfo(SubResourceNodeBean nodeBean) {
        if (nodeBean == null) {
            return null;
        } else {
            Camera camera = new Camera();
            camera.setID(String.valueOf(nodeBean.getId()));
            camera.setIsOnline(nodeBean.getIsOnline());
            camera.setName(nodeBean.getName());
            camera.setSysCode(nodeBean.getSysCode());
            camera.setUserCapability(nodeBean.getUserCapability());
            return camera;
        }
    }

    public boolean queryRecordSegment(CameraInfo cameraInfo, Calendar startTime, Calendar endTime, int storageType, String guid) {
        boolean result = LiveOrPalyBackBusiness.getInstance().queryRecoredInfo(cameraInfo, startTime, endTime, storageType, guid, this.mOnVMSNetSDKBusiness);
        return result;
    }

    public boolean sendPTZCtrlCmd(CameraInfo cameraInfo, String action, int command) {
        if (cameraInfo == null) {
            return false;
        } else {
            boolean result = MyPTZBusiness.getInstance().ptzCtrl(cameraInfo, action, command);
            return result;
        }
    }

    public boolean ptzPresetCtrl(CameraInfo cameraInfo, String action, int command, int presetIndex) {
        if (cameraInfo == null) {
            return false;
        } else if (presetIndex > 255) {
            return false;
        } else {
            boolean result = PTZBusiness.getInstance().ptzPresetCtrl(cameraInfo, action, command, presetIndex);
            return result;
        }
    }

    public boolean isHasLivePermission(Camera camera) {
        if (camera == null) {
            return false;
        } else {
            boolean hasLivePermission = false;
            String userCapability = camera.getUserCapability();
            if (!SDKUtil.isEmpty(new String[]{userCapability})) {
                int start = userCapability.indexOf(",");
                if (start == -1) {
                    if (userCapability.equals("1")) {
                        hasLivePermission = true;
                    }
                } else {
                    String[] strings = userCapability.split(",");
                    String[] var9 = strings;
                    int var8 = strings.length;

                    for(int var7 = 0; var7 < var8; ++var7) {
                        String string = var9[var7];
                        if (string.equals("1")) {
                            hasLivePermission = true;
                        }
                    }
                }
            }

            return hasLivePermission;
        }
    }

    public boolean isHasPlayBackPermission(Camera camera) {
        if (camera == null) {
            return false;
        } else {
            boolean hasPlayBackPermission = false;
            String userCapability = camera.getUserCapability();
            if (!SDKUtil.isEmpty(new String[]{userCapability})) {
                int start = userCapability.indexOf(",");
                if (start == -1) {
                    if (userCapability.equals("2")) {
                        hasPlayBackPermission = true;
                    }
                } else {
                    String[] strings = userCapability.split(",");
                    String[] var9 = strings;
                    int var8 = strings.length;

                    for(int var7 = 0; var7 < var8; ++var7) {
                        String string = var9[var7];
                        if (string.equals("2")) {
                            hasPlayBackPermission = true;
                        }
                    }
                }
            }

            return hasPlayBackPermission;
        }
    }

    public void setOnVMSNetSDKBusiness(OnVMSNetSDKBusiness nBusiness) {
        this.mOnVMSNetSDKBusiness = nBusiness;
    }

    public boolean getCameraInfo(String cameraId, String sysCode) {
        return ResourceBusiness.getInstance().getCameraInfo(cameraId, sysCode, this.mOnVMSNetSDKBusiness);
    }

    public boolean getCameraInfoById(String cameraId) {
        return this.getCameraInfo(cameraId, "");
    }

    public boolean getCameraInfoByCode(String sysCode) {
        return this.getCameraInfo("", sysCode);
    }

    public boolean getCameraInfo(Camera camera) {
        if (camera == null) {
            this.mLastError = 1100;
            this.mLastErrorDescribe = "VMSNetSDK::camera is null, plese reselect";
            return false;
        } else {
            return this.getCameraInfo(camera.getID(), camera.getSysCode());
        }
    }

    public boolean getDeviceInfo(String deviceId) {
        boolean result = ResourceBusiness.getInstance().getDeviceInfo(deviceId, this.mOnVMSNetSDKBusiness);
        return result;
    }

    public boolean getRootCtrlCenterInfo(int curPage, int sysType, int numPerPage) {
        boolean success = ResourceBusiness.getInstance().getRootCtrlCenterInfo(curPage, sysType, numPerPage, this.mOnVMSNetSDKBusiness);
        return success;
    }

    public boolean getSubResourceList(int curPage, int numPerPage, int sysType, int parentNodeType, String pId) {
        boolean success = ResourceBusiness.getInstance().getSubResourceList(curPage, numPerPage, sysType, parentNodeType, pId, this.mOnVMSNetSDKBusiness);
        return success;
    }

    public String getPlayBackRtspUrl(CameraInfo cameraInfo, String url, Calendar start, Calendar stop) {
        if (cameraInfo == null) {
            this.mLastError = 1100;
            this.mLastErrorDescribe = "VMSNetSDK::cameraInfo is null, plese reselect";
        }

        if (url == null) {
            this.mLastError = 10001;
            this.mLastErrorDescribe = "There is not has playback";
        }

        String rtspUri = LiveOrPalyBackBusiness.getInstance().getPlayBackRtspUrl(cameraInfo, url, start, stop);
        if (rtspUri == null || rtspUri.length() <= 0) {
            this.mLastError = LiveOrPalyBackBusiness.getInstance().getLastError();
            this.mLastErrorDescribe = LiveOrPalyBackBusiness.getInstance().getLastErrorDescribe();
        }

        return rtspUri;
    }

    public String getPlayUrl(CameraInfo cameraInfo, int streamType) {
        if (cameraInfo == null) {
            this.mLastError = 1100;
            this.mLastErrorDescribe = "VMSNetSDK::cameraInfo is null, plese reselect";
        }

        String rtspUri = LiveOrPalyBackBusiness.getInstance().getPlayUrl(cameraInfo, streamType);
        if (rtspUri == null || rtspUri.length() <= 0) {
            this.mLastError = LiveOrPalyBackBusiness.getInstance().getLastError();
            this.mLastErrorDescribe = LiveOrPalyBackBusiness.getInstance().getLastErrorDescribe();
        }

        return rtspUri;
    }

    public String getBuild() {
        return this.build;
    }
}
