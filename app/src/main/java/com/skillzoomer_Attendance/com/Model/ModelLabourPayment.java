package com.skillzoomer_Attendance.com.Model;

public class ModelLabourPayment {
    String date,time,amount,labourId,labourName,uploadedByUid,timestamp
            ;

    public ModelLabourPayment() {
    }

    public ModelLabourPayment(String date, String time, String amount, String labourId, String labourName, String uploadedByUid) {
        this.date = date;
        this.time = time;
        this.amount = amount;
        this.labourId = labourId;
        this.labourName = labourName;
        this.uploadedByUid = uploadedByUid;
    }

    public ModelLabourPayment(String date, String time, String amount, String labourId, String labourName, String uploadedByUid, String timestamp) {
        this.date = date;
        this.time = time;
        this.amount = amount;
        this.labourId = labourId;
        this.labourName = labourName;
        this.uploadedByUid = uploadedByUid;
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

    public String getUploadedByUid() {
        return uploadedByUid;
    }

    public void setUploadedByUid(String uploadedByUid) {
        this.uploadedByUid = uploadedByUid;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
