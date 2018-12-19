package com.ivms.ivms8700.bean;

import java.io.Serializable;

public class DiscernEntity implements Serializable {

    private String lineCode;
    private String lineName;
    private String stationCode;
    private String stationName;
    private String safeCapCapture;
    private String captureTime;



    public String getLineCode() {
        return lineCode;
    }

    public void setLineCode(String lineCode) {
        this.lineCode = lineCode;
    }

    public String getLineName() {
        return lineName;
    }

    public void setLineName(String lineName) {
        this.lineName = lineName;
    }

    public String getStationCode() {
        return stationCode;
    }

    public void setStationCode(String stationCode) {
        this.stationCode = stationCode;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public String getSafeCapCapture() {
        return safeCapCapture;
    }

    public void setSafeCapCapture(String safeCapCapture) {
        this.safeCapCapture = safeCapCapture;
    }

    public String getCaptureTime() {
        return captureTime;
    }

    public void setCaptureTime(String captureTime) {
        this.captureTime = captureTime;
    }
}
