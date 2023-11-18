package com.skillzoomer_Attendance.com.Model;

public class ModelPicActivity {
    private String picId,picRemark,uploadedbyUid,picLink,dateOfUpload,timeofUpload,uploadedBytype,uploadedByName,picType,picMsg,replyMsg,replyTime,replyDate,replyType,replyLink;
    private long siteId;
    private double picLatitude,picLongitude;
    private Boolean reply;

    public ModelPicActivity() {
    }

    public ModelPicActivity(String picId, String picRemark, String uploadedbyUid, String picLink, String dateOfUpload, String timeofUpload, String uploadedBytype, String uploadedByName, String picType,
                            String picMsg, String replyMsg, String replyTime, String replyDate, String replyType, String replyLink, long siteId, double picLatitude, double picLongitude, Boolean reply) {
        this.picId = picId;
        this.picRemark = picRemark;
        this.uploadedbyUid = uploadedbyUid;
        this.picLink = picLink;
        this.dateOfUpload = dateOfUpload;
        this.timeofUpload = timeofUpload;
        this.uploadedBytype = uploadedBytype;
        this.uploadedByName = uploadedByName;
        this.picType = picType;
        this.picMsg = picMsg;
        this.replyMsg = replyMsg;
        this.replyTime = replyTime;
        this.replyDate = replyDate;
        this.replyType = replyType;
        this.replyLink = replyLink;
        this.siteId = siteId;
        this.picLatitude = picLatitude;
        this.picLongitude = picLongitude;
        this.reply = reply;
    }

    public String getPicId() {
        return picId;
    }

    public void setPicId(String picId) {
        this.picId = picId;
    }

    public String getPicRemark() {
        return picRemark;
    }

    public void setPicRemark(String picRemark) {
        this.picRemark = picRemark;
    }

    public String getUploadedbyUid() {
        return uploadedbyUid;
    }

    public void setUploadedbyUid(String uploadedbyUid) {
        this.uploadedbyUid = uploadedbyUid;
    }

    public String getPicLink() {
        return picLink;
    }

    public void setPicLink(String picLink) {
        this.picLink = picLink;
    }

    public String getDateOfUpload() {
        return dateOfUpload;
    }

    public void setDateOfUpload(String dateOfUpload) {
        this.dateOfUpload = dateOfUpload;
    }

    public String getTimeofUpload() {
        return timeofUpload;
    }

    public void setTimeofUpload(String timeofUpload) {
        this.timeofUpload = timeofUpload;
    }

    public long getSiteId() {
        return siteId;
    }

    public void setSiteId(long siteId) {
        this.siteId = siteId;
    }

    public double getPicLatitude() {
        return picLatitude;
    }

    public void setPicLatitude(double picLatitude) {
        this.picLatitude = picLatitude;
    }

    public double getPicLongitude() {
        return picLongitude;
    }

    public void setPicLongitude(double picLongitude) {
        this.picLongitude = picLongitude;
    }

    public String getUploadedBytype() {
        return uploadedBytype;
    }

    public void setUploadedBytype(String uploadedBytype) {
        this.uploadedBytype = uploadedBytype;
    }

    public String getUploadedByName() {
        return uploadedByName;
    }

    public void setUploadedByName(String uploadedByName) {
        this.uploadedByName = uploadedByName;
    }

    public String getPicType() {
        return picType;
    }

    public void setPicType(String picType) {
        this.picType = picType;
    }

    public String getPicMsg() {
        return picMsg;
    }

    public void setPicMsg(String picMsg) {
        this.picMsg = picMsg;
    }

    public String getReplyMsg() {
        return replyMsg;
    }

    public void setReplyMsg(String replyMsg) {
        this.replyMsg = replyMsg;
    }

    public String getReplyTime() {
        return replyTime;
    }

    public void setReplyTime(String replyTime) {
        this.replyTime = replyTime;
    }

    public String getReplyDate() {
        return replyDate;
    }

    public void setReplyDate(String replyDate) {
        this.replyDate = replyDate;
    }

    public String getReplyType() {
        return replyType;
    }

    public void setReplyType(String replyType) {
        this.replyType = replyType;
    }

    public String getReplyLink() {
        return replyLink;
    }

    public void setReplyLink(String replyLink) {
        this.replyLink = replyLink;
    }

    public Boolean getReply() {
        return reply;
    }

    public void setReply(Boolean reply) {
        this.reply = reply;
    }
}
