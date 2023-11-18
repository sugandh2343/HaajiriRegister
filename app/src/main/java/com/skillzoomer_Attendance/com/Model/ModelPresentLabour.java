package com.skillzoomer_Attendance.com.Model;

public class ModelPresentLabour {
    private String labourId,labourName,date,time;

    public ModelPresentLabour() {
    }

    public ModelPresentLabour(String labourId , String labourName , String date , String time) {
        this.labourId = labourId;
        this.labourName = labourName;
        this.date = date;
        this.time = time;
    }

    public String getLabourId() {
        return labourId;
    }

    public void setLabourId(String labourId) {
        this.labourId = labourId;
    }

    public String getLabourName() {
        return labourName;
    }

    public void setLabourName(String labourName) {
        this.labourName = labourName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
