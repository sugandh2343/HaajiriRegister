package com.skillzoomer_Attendance.com.Model;

public class ModelPayment {
    private String date,time,amount,labourId,labourName,labourType,uploadedByUid,uploadedByType,uploadedByName,timestamp;

    public ModelPayment() {
    }

    public ModelPayment(String date, String time, String amount, String labourId, String labourName, String labourType, String uploadedByUid, String uploadedByType, String uploadedByName, String timestamp) {
        this.date = date;
        this.time = time;
        this.amount = amount;
        this.labourId = labourId;
        this.labourName = labourName;
        this.labourType = labourType;
        this.uploadedByUid = uploadedByUid;
        this.uploadedByType = uploadedByType;
        this.uploadedByName = uploadedByName;
        this.timestamp = timestamp;
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

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
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

    public String getUploadedByUid() {
        return uploadedByUid;
    }

    public void setUploadedByUid(String uploadedByUid) {
        this.uploadedByUid = uploadedByUid;
    }

    public String getUploadedByType() {
        return uploadedByType;
    }

    public void setUploadedByType(String uploadedByType) {
        this.uploadedByType = uploadedByType;
    }

    public String getUploadedByName() {
        return uploadedByName;
    }

    public void setUploadedByName(String uploadedByName) {
        this.uploadedByName = uploadedByName;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
