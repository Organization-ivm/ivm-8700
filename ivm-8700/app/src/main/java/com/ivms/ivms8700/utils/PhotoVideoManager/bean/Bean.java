package com.ivms.ivms8700.utils.PhotoVideoManager.bean;

import java.io.File;

/**
 * Created by lenovo on 2016/11/27.
 */

public class Bean {
    private String filePath;
    private boolean isChecked;
    private boolean isShow;
    private int id;
    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public boolean isShow() {
        return isShow;
    }

    public void setShow(boolean show) {
        isShow = show;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
