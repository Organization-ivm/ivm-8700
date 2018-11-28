package com.ivms.ivms8700.utils.PhotoVideoManager;

import android.graphics.Bitmap;

public class VideoInfo {

    private Bitmap bitmap;
    private String path;
    private int time;
    private String name;
    private boolean checked;
    private boolean isShow;
    private String lastModifed;

    public String getLastModifed() {
        return lastModifed;
    }

    public void setLastModifed(String lastModifed) {
        this.lastModifed = lastModifed;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public boolean isShow() {
        return isShow;
    }

    public void setShow(boolean show) {
        isShow = show;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }
}
