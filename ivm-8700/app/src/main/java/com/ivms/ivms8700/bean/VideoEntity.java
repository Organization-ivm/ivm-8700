package com.ivms.ivms8700.bean;

import com.hikvision.sdk.net.bean.CameraInfo;
import com.hikvision.sdk.net.bean.DeviceInfo;
import com.ivms.ivms8700.live.LiveControl;
import com.ivms.ivms8700.playback.PlayBackControl;

public class VideoEntity {

    int rowCout=1;
    private LiveControl mLiveControl = null;
    private PlayBackControl mPlayBackControl= null;
    private boolean isSelect= false;

    /**
     * 监控点详细信息
     */
    private CameraInfo cameraInfo =null;

    /**
     * 监控点关联的监控设备信息
     */
    private DeviceInfo deviceInfo = null;


    public CameraInfo getCameraInfo() {
        return cameraInfo;
    }

    public void setCameraInfo(CameraInfo cameraInfo) {
        this.cameraInfo = cameraInfo;
    }

    public DeviceInfo getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(DeviceInfo deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    public int getRowCout() {
        return rowCout;
    }

    public void setRowCout(int rowCout) {
        this.rowCout = rowCout;
    }

    public LiveControl getmLiveControl() {
        return mLiveControl;
    }

    public void setmLiveControl(LiveControl mLiveControl) {
        this.mLiveControl = mLiveControl;
    }

    public PlayBackControl getmPlayBackControl() {
        return mPlayBackControl;
    }

    public void setmPlayBackControl(PlayBackControl mPlayBackControl) {
        this.mPlayBackControl = mPlayBackControl;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }
}
