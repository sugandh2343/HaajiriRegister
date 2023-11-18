package com.skillzoomer_Attendance.com.Model;

public class MopdelOtherExpenses {
    String expId,expType,amount,expTime,expDate,expRemark,entryByUid,entryByName,entryByType;
    Boolean show;

    public MopdelOtherExpenses() {
    }

    public MopdelOtherExpenses(String expId, String expType,
                               String amount, String expTime, String expDate, String expRemark, String entryByUid, String entryByName, String entryByType, Boolean show) {
        this.expId = expId;
        this.expType = expType;
        this.amount = amount;
        this.expTime = expTime;
        this.expDate = expDate;
        this.expRemark = expRemark;
        this.entryByUid = entryByUid;
        this.entryByName = entryByName;
        this.entryByType = entryByType;
        this.show = show;
    }

    public String getExpId() {
        return expId;
    }

    public void setExpId(String expId) {
        this.expId = expId;
    }

    public String getExpType() {
        return expType;
    }

    public void setExpType(String expType) {
        this.expType = expType;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getExpTime() {
        return expTime;
    }

    public void setExpTime(String expTime) {
        this.expTime = expTime;
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

    public Boolean getShow() {
        return show;
    }

    public void setShow(Boolean show) {
        this.show = show;
    }
}
