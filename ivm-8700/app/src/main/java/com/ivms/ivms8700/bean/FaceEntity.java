package com.ivms.ivms8700.bean;

import java.io.Serializable;

public class FaceEntity  implements Serializable {

    private String count;
    private String name;
    private String employeeNumber;
    private String date;
    private String faceCapture;
    private String afternoondate;
    private String afternoonfaceCapture;
    private String sex;
    private String officeName;
    private String department;
    private String position;
    private String phone;

    private String lineCode;
    private String lineName;
    private String stationCode;
    private String stationName;
    private String  modelPhoto;


    private String clock3Time;
    private String clock3ImagePath;

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmployeeNumber() {
        return employeeNumber;
    }

    public void setEmployeeNumber(String employeeNumber) {
        this.employeeNumber = employeeNumber;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getFaceCapture() {
        return faceCapture;
    }

    public void setFaceCapture(String faceCapture) {
        this.faceCapture = faceCapture;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getOfficeName() {
        return officeName;
    }

    public void setOfficeName(String officeName) {
        this.officeName = officeName;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

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

    public String getModelPhoto() {
        return modelPhoto;
    }

    public void setModelPhoto(String modelPhoto) {
        this.modelPhoto = modelPhoto;
    }

    public String getAfternoondate() {
        return afternoondate;
    }

    public void setAfternoondate(String afternoondate) {
        this.afternoondate = afternoondate;
    }

    public String getAfternoonfaceCapture() {
        return afternoonfaceCapture;
    }

    public void setAfternoonfaceCapture(String afternoonfaceCapture) {
        this.afternoonfaceCapture = afternoonfaceCapture;
    }

    public String getClock3Time() {
        return clock3Time;
    }

    public void setClock3Time(String clock3Time) {
        this.clock3Time = clock3Time;
    }

    public String getClock3ImagePath() {
        return clock3ImagePath;
    }

    public void setClock3ImagePath(String clock3ImagePath) {
        this.clock3ImagePath = clock3ImagePath;
    }
}
