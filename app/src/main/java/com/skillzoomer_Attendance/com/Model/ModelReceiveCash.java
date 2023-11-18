package com.skillzoomer_Attendance.com.Model;

public class ModelReceiveCash {
    private String recCashId,recType,amount,reqTime,reqDate,recFrom,entryByUid,entryByName,entryByType;

    public ModelReceiveCash() {
    }

    public ModelReceiveCash(String recCashId, String recType, String amount, String reqTime, String reqDate, String recFrom, String entryByUid, String entryByName, String entryByType) {
        this.recCashId = recCashId;
        this.recType = recType;
        this.amount = amount;
        this.reqTime = reqTime;
        this.reqDate = reqDate;
        this.recFrom = recFrom;
        this.entryByUid = entryByUid;
        this.entryByName = entryByName;
        this.entryByType = entryByType;
    }

    public String getRecCashId() {
        return recCashId;
    }

    public void setRecCashId(String recCashId) {
        this.recCashId = recCashId;
    }

    public String getRecType() {
        return recType;
    }

    public void setRecType(String recType) {
        this.recType = recType;
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

    public String getRecFrom() {
        return recFrom;
    }

    public void setRecFrom(String recFrom) {
        this.recFrom = recFrom;
    }

    public String getEntryByUid() {
        return entryByUid;
    }

    public void setEntryByUid(String entryByUid) {
        this.entryByUid = entryByUid;
    }

    public String getEntryByName() {
        return entryByName;
    }

    public void setEntryByName(String entryByName) {
        this.entryByName = entryByName;
    }

    public String getEntryByType() {
        return entryByType;
    }

    public void setEntryByType(String entryByType) {
        this.entryByType = entryByType;
    }
}
