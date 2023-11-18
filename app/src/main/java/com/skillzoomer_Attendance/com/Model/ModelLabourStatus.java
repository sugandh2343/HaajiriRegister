package com.skillzoomer_Attendance.com.Model;

public class ModelLabourStatus {
    private String date,labourId,status,type,reason;

    public ModelLabourStatus() {
    }

    public ModelLabourStatus(String date, String labourId, String status, String type) {
        this.date = date;
        this.labourId = labourId;
        this.status = status;
        this.type = type;
    }

    public ModelLabourStatus(String date, String labourId, String status, String type, String reason) {
        this.date = date;
        this.labourId = labourId;
        this.status = status;
        this.type = type;
        this.reason = reason;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
