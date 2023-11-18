package com.skillzoomer_Attendance.com.Model;

public class ModelFundRequest {
    private String reqId,reqType,amount,reqTime,reqDate,reqStatus;

    public ModelFundRequest() {
    }

    public ModelFundRequest(String reqId, String reqType, String amount, String reqTime, String reqDate, String reqStatus) {
        this.reqId = reqId;
        this.reqType = reqType;
        this.amount = amount;
        this.reqTime = reqTime;
        this.reqDate = reqDate;
        this.reqStatus = reqStatus;
    }

    public String getReqId() {
        return reqId;
    }

    public void setReqId(String reqId) {
        this.reqId = reqId;
    }

    public String getReqType() {
        return reqType;
    }

    public void setReqType(String reqType) {
        this.reqType = reqType;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getReqTime() {
        return reqTime;
    }

    public void setReqTime(String reqTime) {
        this.reqTime = reqTime;
    }

    public String getReqDate() {
        return reqDate;
    }

    public void setReqDate(String reqDate) {
        this.reqDate = reqDate;
    }

    public String getReqStatus() {
        return reqStatus;
    }

    public void setReqStatus(String reqStatus) {
        this.reqStatus = reqStatus;
    }
}
