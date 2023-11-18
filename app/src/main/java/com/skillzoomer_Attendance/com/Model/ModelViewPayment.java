package com.skillzoomer_Attendance.com.Model;

public class ModelViewPayment {
    private String srNo,labourId,labourName,labourType,date,time,amt,Id,reason;

    public ModelViewPayment() {
    }

    public ModelViewPayment(String srNo, String labourId, String labourName, String labourType, String date, String time, String amt, String id, String reason) {
        this.srNo = srNo;
        this.labourId = labourId;
        this.labourName = labourName;
        this.labourType = labourType;
        this.date = date;
        this.time = time;
        this.amt = amt;
        this.Id = id;
        this.reason = reason;
    }

    public ModelViewPayment(String srNo, String labourId, String labourName, String labourType, String date, String time, String amt, String id) {
        this.srNo = srNo;
        this.labourId = labourId;
        this.labourName = labourName;
        this.labourType = labourType;
        this.date = date;
        this.time = time;
        this.amt = amt;
        Id = id;
    }

    public String getSrNo() {
        return srNo;
    }

    public void setSrNo(String srNo) {
        this.srNo = srNo;
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

    public String getAmt() {
        return amt;
    }

    public void setAmt(String amt) {
        this.amt = amt;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
