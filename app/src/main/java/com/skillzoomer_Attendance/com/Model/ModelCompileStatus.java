package com.skillzoomer_Attendance.com.Model;

public class ModelCompileStatus {
    private String date,labourId,status,type,amount;
    private int siteId;

    public ModelCompileStatus() {
    }

    public ModelCompileStatus(String date, String labourId, String status, String type, String amount) {
        this.date = date;
        this.labourId = labourId;
        this.status = status;
        this.type = type;
        this.amount = amount;
    }

    public ModelCompileStatus(String date, String labourId, String status, String amount, int siteId) {
        this.date = date;
        this.labourId = labourId;
        this.status = status;
        this.amount = amount;
        this.siteId = siteId;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
