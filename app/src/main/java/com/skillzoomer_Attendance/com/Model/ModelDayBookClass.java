package com.skillzoomer_Attendance.com.Model;

public class ModelDayBookClass {
    private String date;
    private String recAmt;
    private String rec_from;
    private long count;
    private String expAmt;;
    private String expRemark;
    private String expDoneBy;


    public ModelDayBookClass() {
    }

    public ModelDayBookClass(String date, String recAmt, String rec_from, long count, String expAmt, String expRemark) {
        this.date = date;
        this.recAmt = recAmt;
        this.rec_from = rec_from;
        this.count = count;
        this.expAmt = expAmt;
        this.expRemark = expRemark;
    }

    public ModelDayBookClass(String date, String recAmt, String rec_from, long count, String expAmt, String expRemark, String expDoneBy) {
        this.date = date;
        this.recAmt = recAmt;
        this.rec_from = rec_from;
        this.count = count;
        this.expAmt = expAmt;
        this.expRemark = expRemark;
        this.expDoneBy = expDoneBy;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getRecAmt() {
        return recAmt;
    }

    public void setRecAmt(String recAmt) {
        this.recAmt = recAmt;
    }

    public String getRec_from() {
        return rec_from;
    }

    public void setRec_from(String rec_from) {
        this.rec_from = rec_from;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public String getExpAmt() {
        return expAmt;
    }

    public void setExpAmt(String expAmt) {
        this.expAmt = expAmt;
    }

    public String getExpRemark() {
        return expRemark;
    }

    public void setExpRemark(String expRemark) {
        this.expRemark = expRemark;
    }

    public String getExpDoneBy() {
        return expDoneBy;
    }

    public void setExpDoneBy(String expDoneBy) {
        this.expDoneBy = expDoneBy;
    }
}
