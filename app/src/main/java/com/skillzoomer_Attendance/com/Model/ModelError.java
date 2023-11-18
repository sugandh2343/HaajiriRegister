package com.skillzoomer_Attendance.com.Model;

public class ModelError {
    private String date,time,remark,amount,cashInHand,name,uid,timestamp;

    public ModelError() {
    }

    public ModelError(String date, String time, String remark, String amount, String cashInHand, String name, String uid, String timestamp) {
        this.date = date;
        this.time = time;
        this.remark = remark;
        this.amount = amount;
        this.cashInHand = cashInHand;
        this.name = name;
        this.uid = uid;
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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCashInHand() {
        return cashInHand;
    }

    public void setCashInHand(String cashInHand) {
        this.cashInHand = cashInHand;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
