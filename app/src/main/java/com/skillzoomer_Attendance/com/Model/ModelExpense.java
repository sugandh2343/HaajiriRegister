package com.skillzoomer_Attendance.com.Model;

public class ModelExpense {
    String expId,expTime,amount,expType,expDate,expRemark,entryByUid,entryByName,entryByType;

    public ModelExpense() {
    }

    public ModelExpense(String expId, String expTime, String amount, String expType, String expDate, String expRemark, String entryByUid, String entryByName, String entryByType) {
        this.expId = expId;
        this.expTime = expTime;
        this.amount = amount;
        this.expType = expType;
        this.expDate = expDate;
        this.expRemark = expRemark;
        this.entryByUid = entryByUid;
        this.entryByName = entryByName;
        this.entryByType = entryByType;
    }

    public String getExpId() {
        return expId;
    }

    public void setExpId(String expId) {
        this.expId = expId;
    }

    public String getExpTime() {
        return expTime;
    }

    public void setExpTime(String expTime) {
        this.expTime = expTime;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getExpType() {
        return expType;
    }

    public void setExpType(String expType) {
        this.expType = expType;
    }

    public String getExpDate() {
        return expDate;
    }

    public void setExpDate(String expDate) {
        this.expDate = expDate;
    }

    public String getExpRemark() {
        return expRemark;
    }

    public void setExpRemark(String expRemark) {
        this.expRemark = expRemark;
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
