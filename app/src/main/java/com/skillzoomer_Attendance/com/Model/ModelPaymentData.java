package com.skillzoomer_Attendance.com.Model;

public class ModelPaymentData {
    private String labourId,labourName,type;
    private int count,position;
    private long payableAmt;

    public ModelPaymentData() {
    }

    public ModelPaymentData(String labourId, String labourName, String type, int count, int position, long payableAmt) {
        this.labourId = labourId;
        this.labourName = labourName;
        this.type = type;
        this.count = count;
        this.position = position;
        this.payableAmt = payableAmt;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public long getPayableAmt() {
        return payableAmt;
    }

    public void setPayableAmt(long payableAmt) {
        this.payableAmt = payableAmt;
    }
}
