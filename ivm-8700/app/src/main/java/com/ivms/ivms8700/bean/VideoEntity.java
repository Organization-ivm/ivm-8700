package com.ivms.ivms8700.bean;


import com.hikvision.sdk.net.bean.SubResourceNodeBean;

public class VideoEntity {

    int rowCout=1;
    private boolean isSelect= false;
    private SubResourceNodeBean camera; //资源对象



    public int getRowCout() {
        return rowCout;
    }

    public void setRowCout(int rowCout) {
        this.rowCout = rowCout;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public SubResourceNodeBean getCamera() {
        return camera;
    }
    public void setCamera(SubResourceNodeBean camera) {
        this.camera = camera;
    }
}
