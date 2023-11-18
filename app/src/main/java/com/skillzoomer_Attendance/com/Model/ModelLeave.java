package com.skillzoomer_Attendance.com.Model;

public class ModelLeave {
    String leaveType,leaveStartDate,leaveEndDate,leaveDays,leaveStatus,
            leaveId,leaveApplyDate,leaveApplyTime,leaveCurrentDate,
            leaveappliedByUid,leaveappliedByName,status,reason;

    public ModelLeave() {
    }

    public ModelLeave(String leaveType ,
                      String leaveStartDate ,
                      String leaveEndDate ,
                      String leaveDays ,
                      String leaveStatus ,
                      String leaveId ,
                      String leaveApplyDate ,
                      String leaveApplyTime ,
                      String leaveCurrentDate ,
                      String leaveappliedByUid ,
                      String leaveappliedByName ,
                      String status) {
        this.leaveType = leaveType;
        this.leaveStartDate = leaveStartDate;
        this.leaveEndDate = leaveEndDate;
        this.leaveDays = leaveDays;
        this.leaveStatus = leaveStatus;
        this.leaveId = leaveId;
        this.leaveApplyDate = leaveApplyDate;
        this.leaveApplyTime = leaveApplyTime;
        this.leaveCurrentDate = leaveCurrentDate;
        this.leaveappliedByUid = leaveappliedByUid;
        this.leaveappliedByName = leaveappliedByName;
        this.status = status;
    }

    public ModelLeave(String leaveType ,
                      String leaveStartDate ,
                      String leaveEndDate ,
                      String leaveDays ,
                      String leaveStatus ,
                      String leaveId ,
                      String leaveApplyDate ,
                      String leaveApplyTime ,
                      String leaveCurrentDate ,
                      String leaveappliedByUid ,
                      String leaveappliedByName ,
                      String status ,
                      String reason) {
        this.leaveType = leaveType;
        this.leaveStartDate = leaveStartDate;
        this.leaveEndDate = leaveEndDate;
        this.leaveDays = leaveDays;
        this.leaveStatus = leaveStatus;
        this.leaveId = leaveId;
        this.leaveApplyDate = leaveApplyDate;
        this.leaveApplyTime = leaveApplyTime;
        this.leaveCurrentDate = leaveCurrentDate;
        this.leaveappliedByUid = leaveappliedByUid;
        this.leaveappliedByName = leaveappliedByName;
        this.status = status;
        this.reason = reason;
    }

    public ModelLeave(ModelLeave modelLeave) {
    }

    public String getLeaveType() {
        return leaveType;
    }

    public void setLeaveType(String leaveType) {
        this.leaveType = leaveType;
    }

    public String getLeaveStartDate() {
        return leaveStartDate;
    }

    public void setLeaveStartDate(String leaveStartDate) {
        this.leaveStartDate = leaveStartDate;
    }

    public String getLeaveEndDate() {
        return leaveEndDate;
    }

    public void setLeaveEndDate(String leaveEndDate) {
        this.leaveEndDate = leaveEndDate;
    }

    public String getLeaveDays() {
        return leaveDays;
    }

    public void setLeaveDays(String leaveDays) {
        this.leaveDays = leaveDays;
    }

    public String getLeaveStatus() {
        return leaveStatus;
    }

    public void setLeaveStatus(String leaveStatus) {
        this.leaveStatus = leaveStatus;
    }

    public String getLeaveId() {
        return leaveId;
    }

    public void setLeaveId(String leaveId) {
        this.leaveId = leaveId;
    }

    public String getLeaveApplyDate() {
        return leaveApplyDate;
    }

    public void setLeaveApplyDate(String leaveApplyDate) {
        this.leaveApplyDate = leaveApplyDate;
    }

    public String getLeaveApplyTime() {
        return leaveApplyTime;
    }

    public void setLeaveApplyTime(String leaveApplyTime) {
        this.leaveApplyTime = leaveApplyTime;
    }

    public String getLeaveCurrentDate() {
        return leaveCurrentDate;
    }

    public void setLeaveCurrentDate(String leaveCurrentDate) {
        this.leaveCurrentDate = leaveCurrentDate;
    }

    public String getLeaveappliedByUid() {
        return leaveappliedByUid;
    }

    public void setLeaveappliedByUid(String leaveappliedByUid) {
        this.leaveappliedByUid = leaveappliedByUid;
    }

    public String getLeaveappliedByName() {
        return leaveappliedByName;
    }

    public void setLeaveappliedByName(String leaveappliedByName) {
        this.leaveappliedByName = leaveappliedByName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
