package com.ivms.ivms8700.multilevellist;

import com.hikvision.sdk.net.bean.RootCtrlCenter;
import com.hikvision.sdk.net.bean.SubResourceNodeBean;

/**
 * Created by xulc on 2018/7/27.
 */

public class TreePoint {
    private int ID;        // 7241,          //账号id
    private String NNAME; // "用户原因",    //原因名称
    private int PARENTID;   // 0,           //父id     0表示父节点
    private int DISPLAY_ORDER; // 1       //同一个级别的显示顺序
    private boolean isExpand = false;  //是否展开了
    private int layer = 1;//层级
    private boolean hasSubDatas = false;
    private SubResourceNodeBean subResourceNodeBean;

    public RootCtrlCenter getRootCtrlCenter() {
        return rootCtrlCenter;
    }

    public void setRootCtrlCenter(RootCtrlCenter rootCtrlCenter) {
        this.rootCtrlCenter = rootCtrlCenter;
    }

    private RootCtrlCenter rootCtrlCenter;

    public SubResourceNodeBean getSubResourceNodeBean() {
        return subResourceNodeBean;
    }

    public void setSubResourceNodeBean(SubResourceNodeBean subResourceNodeBean) {
        this.subResourceNodeBean = subResourceNodeBean;
    }

    public TreePoint(int ID, String NNAME, int PARENTID, int DISPLAY_ORDER, boolean isExpand, int layer, boolean hasSubDatas, SubResourceNodeBean subResourceNodeBean, RootCtrlCenter rootCtrlCenter) {
        this.ID = ID;
        this.NNAME = NNAME;
        this.PARENTID = PARENTID;
        this.DISPLAY_ORDER = DISPLAY_ORDER;
        this.isExpand = isExpand;
        this.layer = layer;
        this.hasSubDatas = hasSubDatas;
        this.subResourceNodeBean = subResourceNodeBean;
        this.rootCtrlCenter = rootCtrlCenter;
    }

//    public TreePoint(int ID, String NNAME, int PARENTID, int DISPLAY_ORDER, boolean isExpand, int layer, boolean hasSubDatas) {
//        this.ID = ID;
//        this.NNAME = NNAME;
//        this.PARENTID = PARENTID;
//        this.DISPLAY_ORDER = DISPLAY_ORDER;
//        this.isExpand = isExpand;
//        this.layer = layer;
//        this.hasSubDatas = hasSubDatas;
//
//    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getNNAME() {
        return NNAME;
    }

    public void setNNAME(String NNAME) {
        this.NNAME = NNAME;
    }

    public int getPARENTID() {
        return PARENTID;
    }

    public void setPARENTID(int PARENTID) {
        this.PARENTID = PARENTID;
    }

    public int getDISPLAY_ORDER() {
        return DISPLAY_ORDER;
    }

    public void setDISPLAY_ORDER(int DISPLAY_ORDER) {
        this.DISPLAY_ORDER = DISPLAY_ORDER;
    }

    public boolean isExpand() {
        return isExpand;
    }

    public void setExpand(boolean expand) {
        isExpand = expand;
    }

    public int getLayer() {
        return layer;
    }

    public void setLayer(int layer) {
        this.layer = layer;
    }

    public boolean isHasSubDatas() {
        return hasSubDatas;
    }

    public void setHasSubDatas(boolean hasSubDatas) {
        this.hasSubDatas = hasSubDatas;
    }
}