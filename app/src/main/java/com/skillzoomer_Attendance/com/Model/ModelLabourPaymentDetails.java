package com.skillzoomer_Attendance.com.Model;

public class ModelLabourPaymentDetails {
    String date,time,amount,labourId,labourName,labourType;

    public ModelLabourPaymentDetails() {
    }

    public ModelLabourPaymentDetails(String date, String time, String amount, String labourId, String labourName, String labourType) {
        this.date = date;
        this.time = time;
        this.amount = amount;
        this.labourId = labourId;
        this.labourName = labourName;
        this.labourType = labourType;
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
}
