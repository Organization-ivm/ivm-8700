package com.ivms.ivms8700.bean;

import com.ivms.ivms8700.live.LiveControl;
import com.ivms.ivms8700.playback.PlayBackControl;

public class VideoEntity {

    int rowCout=1;
    private LiveControl mLiveControl = null;
    private PlayBackControl mPlayBackControl= null;

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
}
