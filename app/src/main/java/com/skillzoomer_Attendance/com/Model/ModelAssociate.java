package com.skillzoomer_Attendance.com.Model;

public class ModelAssociate {
    private String name,mobile,memberUid;
    private Boolean attendanceManagement,cashManagement,financeManagement;

    public ModelAssociate() {
    }

    public ModelAssociate(String name, String mobile, String memberUid, Boolean attendanceManagement, Boolean cashManagement, Boolean financeManagement) {
        this.name = name;
        this.mobile = mobile;
        this.memberUid = memberUid;
        this.attendanceManagement = attendanceManagement;
        this.cashManagement = cashManagement;
        this.financeManagement = financeManagement;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getAttendanceManagement() {
        return attendanceManagement;
    }

    public void setAttendanceManagement(Boolean attendanceManagement) {
        this.attendanceManagement = attendanceManagement;
    }

    public Boolean getCashManagement() {
        return cashManagement;
    }

    public void setCashManagement(Boolean cashManagement) {
        this.cashManagement = cashManagement;
    }

    public Boolean getFinanceManagement() {
        return financeManagement;
    }

    public void setFinanceManagement(Boolean financeManagement) {
        this.financeManagement = financeManagement;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getMemberUid() {
        return memberUid;
    }

    public void setMemberUid(String memberUid) {
        this.memberUid = memberUid;
    }
}
