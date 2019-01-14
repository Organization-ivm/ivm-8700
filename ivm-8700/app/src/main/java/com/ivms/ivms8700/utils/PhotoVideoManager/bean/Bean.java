package com.ivms.ivms8700.utils.PhotoVideoManager.bean;

import android.os.Parcel;
import android.os.Parcelable;


/**
 * Created by lenovo on 2016/11/27.
 */

public class Bean implements Parcelable {
    private String filePath;
    private boolean isChecked;
    private boolean isShow;
    private int id;


    public Bean() {
    }

    protected Bean(Parcel in) {
        filePath = in.readString();
        isChecked = in.readByte() != 0;
        isShow = in.readByte() != 0;
        id = in.readInt();
    }

    public static final Creator<Bean> CREATOR = new Creator<Bean>() {
        @Override
        public Bean createFromParcel(Parcel in) {
            return new Bean(in);
        }

        @Override
        public Bean[] newArray(int size) {
            return new Bean[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(filePath);
        dest.writeByte((byte) (isChecked ? 1 : 0));
        dest.writeByte((byte) (isShow ? 1 : 0));
        dest.writeInt(id);
    }
}
