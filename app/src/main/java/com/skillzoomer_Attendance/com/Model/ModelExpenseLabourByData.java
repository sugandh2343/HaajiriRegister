package com.skillzoomer_Attendance.com.Model;

public class ModelExpenseLabourByData {
    private String amount,date,labourId,labourName,labourType,time,uploadedByUid;

    public ModelExpenseLabourByData() {
    }

    public ModelExpenseLabourByData(String amount, String date, String labourId, String labourName, String labourType, String time, String uploadedByUid) {
        this.amount = amount;
        this.date = date;
        this.labourId = labourId;
        this.labourName = labourName;
        this.labourType = labourType;
        this.time = time;
        this.uploadedByUid = uploadedByUid;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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

    public String getLabourType() {
        return labourType;
    }

    public void setLabourType(String labourType) {
        this.labourType = labourType;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUploadedByUid() {
        return uploadedByUid;
    }

    public void setUploadedByUid(String uploadedByUid) {
        this.uploadedByUid = uploadedByUid;
    }
}
