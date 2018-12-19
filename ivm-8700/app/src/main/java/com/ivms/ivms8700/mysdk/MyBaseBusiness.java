package com.ivms.ivms8700.mysdk;

import com.hikvision.sdk.consts.Constant;
import com.hikvision.sdk.utils.CNetSDKLog;
import com.hikvision.sdk.utils.SDKUtil;

public class MyBaseBusiness {
    protected int mLastError;
    protected String mLastErrorDescribe;
    protected String mLoginAddress;
    protected String mSessionId;
    protected boolean isNetSuccess;

    public MyBaseBusiness() {
    }

    public int getLastError() {
        return this.mLastError;
    }

    public String getLastErrorDescribe() {
        return this.mLastErrorDescribe;
    }

    protected boolean invalidSessionID() {
        this.mLoginAddress = Constant.LOGINADDRESS;
        this.mSessionId = Constant.SEESIONID;
        if (SDKUtil.isEmpty(new String[]{this.mLoginAddress, this.mSessionId})) {
            CNetSDKLog.error("requestCameraInfo ==>> request params invalid");
            this.mLastError = 128;
            this.mLastErrorDescribe = "VMSNetSDK::net request session is err.";
            return true;
        } else {
            return false;
        }
    }
}
