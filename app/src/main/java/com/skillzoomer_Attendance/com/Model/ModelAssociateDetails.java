package com.skillzoomer_Attendance.com.Model;

public class ModelAssociateDetails {
    private double memberLatitude,memberLongitude,siteLatitude,siteLongitude;
    private String time,timeStamp,profile,status,date,uid,name;
    private Boolean online;

    public ModelAssociateDetails() {
    }


    public ModelAssociateDetails(double memberLatitude, double memberLongitude, String time, String timeStamp, String profile, String status, String date, String uid, Boolean online) {
        this.memberLatitude = memberLatitude;
        this.memberLongitude = memberLongitude;
        this.time = time;
        this.timeStamp = timeStamp;
        this.profile = profile;
        this.status = status;
        this.date = date;
        this.uid = uid;
        this.online = online;
    }

    public ModelAssociateDetails(double memberLatitude,
                                 double memberLongitude, String time, String timeStamp, String profile, String status, String date, String uid, String name, Boolean online) {
        this.memberLatitude = memberLatitude;
        this.memberLongitude = memberLongitude;
        this.time = time;
        this.timeStamp = timeStamp;
        this.profile = profile;
        this.status = status;
        this.date = date;
        this.uid = uid;
        this.name = name;
        this.online = online;
    }

    public double getMemberLatitude() {
        return memberLatitude;
    }

    public void setMemberLatitude(double memberLatitude) {
        this.memberLatitude = memberLatitude;
    }

    public double getMemberLongitude() {
        return memberLongitude;
    }

    public void setMemberLongitude(double memberLongitude) {
        this.memberLongitude = memberLongitude;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getOnline() {
        return online;
    }

    public void setOnline(Boolean online) {
        this.online = online;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getSiteLatitude() {
        return siteLatitude;
    }

    public void setSiteLatitude(double siteLatitude) {
        this.siteLatitude = siteLatitude;
    }

    public double getSiteLongitude() {
        return siteLongitude;
    }

    public void setSiteLongitude(double siteLongitude) {
        this.siteLongitude = siteLongitude;
    }
}
