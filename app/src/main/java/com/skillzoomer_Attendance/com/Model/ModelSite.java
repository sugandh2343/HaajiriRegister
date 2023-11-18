package com.skillzoomer_Attendance.com.Model;

public class ModelSite {
    String siteName,siteCity,siteCountry,timestamp,hrUid,time,siteCreatedDate,startTime,endTime,
            cashAmt,cashReqID,SkilledTime,UnskilledTime,date,picId,picTime,picDate,picLink,picRemark,siteAddress,memberStatus,skipTime,onlineTimestamp,
            siteStatus,picUid,picType,picMsg,associateRequestType,toPayAmount,labourIdPaying,associateRequestStatus,associateRequestReason,reqStatus,reqAmt,uid;
    long siteId,skilled,unskilled,cashInHand,paymentSum,upToDatePAyment,memberCount,payableAmount;
    Boolean online,forceLogout,cashReq,reqPendency,locationVerifyConfirm,skilledSeen,unSkilledSeen,picActivity,forceOpt,workOpt,locationSkip,
            attendanceEditSkilled,attendanceEditUnSkilled,locationVerify,associateRequest,isSelected;
    double memberLatitude,memberLongitude,siteLatitude, siteLongitude,picLatitude,picLongitude;

    public ModelSite() {
    }

    public ModelSite(String siteName, String siteCity, String siteCountry, String timestamp, String hrUid, String time, String siteCreatedDate, String startTime, String endTime, String cashAmt, String cashReqID, String skilledTime, String unskilledTime, String date, String picId, String picTime, String picDate, String picLink, String picRemark, String siteAddress, String memberStatus, String skipTime, String onlineTimestamp, String siteStatus, String picUid, String picType, String picMsg, String associateRequestType, String toPayAmount, String labourIdPaying, String associateRequestStatus, String associateRequestReason, String reqStatus, String reqAmt, String uid, long siteId, long skilled, long unskilled, long cashInHand, long paymentSum, long upToDatePAyment, long memberCount, long payableAmount, Boolean online, Boolean forceLogout, Boolean cashReq, Boolean reqPendency, Boolean locationVerifyConfirm, Boolean skilledSeen, Boolean unSkilledSeen, Boolean picActivity, Boolean forceOpt, Boolean workOpt, Boolean locationSkip, Boolean attendanceEditSkilled, Boolean attendanceEditUnSkilled, Boolean locationVerify, Boolean associateRequest,
                     Boolean isSelected, double memberLatitude, double memberLongitude, double siteLatitude, double siteLongitude, double picLatitude, double picLongitude) {
        this.siteName = siteName;
        this.siteCity = siteCity;
        this.siteCountry = siteCountry;
        this.timestamp = timestamp;
        this.hrUid = hrUid;
        this.time = time;
        this.siteCreatedDate = siteCreatedDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.cashAmt = cashAmt;
        this.cashReqID = cashReqID;
        SkilledTime = skilledTime;
        UnskilledTime = unskilledTime;
        this.date = date;
        this.picId = picId;
        this.picTime = picTime;
        this.picDate = picDate;
        this.picLink = picLink;
        this.picRemark = picRemark;
        this.siteAddress = siteAddress;
        this.memberStatus = memberStatus;
        this.skipTime = skipTime;
        this.onlineTimestamp = onlineTimestamp;
        this.siteStatus = siteStatus;
        this.picUid = picUid;
        this.picType = picType;
        this.picMsg = picMsg;
        this.associateRequestType = associateRequestType;
        this.toPayAmount = toPayAmount;
        this.labourIdPaying = labourIdPaying;
        this.associateRequestStatus = associateRequestStatus;
        this.associateRequestReason = associateRequestReason;
        this.reqStatus = reqStatus;
        this.reqAmt = reqAmt;
        this.uid = uid;
        this.siteId = siteId;
        this.skilled = skilled;
        this.unskilled = unskilled;
        this.cashInHand = cashInHand;
        this.paymentSum = paymentSum;
        this.upToDatePAyment = upToDatePAyment;
        this.memberCount = memberCount;
        this.payableAmount = payableAmount;
        this.online = online;
        this.forceLogout = forceLogout;
        this.cashReq = cashReq;
        this.reqPendency = reqPendency;
        this.locationVerifyConfirm = locationVerifyConfirm;
        this.skilledSeen = skilledSeen;
        this.unSkilledSeen = unSkilledSeen;
        this.picActivity = picActivity;
        this.forceOpt = forceOpt;
        this.workOpt = workOpt;
        this.locationSkip = locationSkip;
        this.attendanceEditSkilled = attendanceEditSkilled;
        this.attendanceEditUnSkilled = attendanceEditUnSkilled;
        this.locationVerify = locationVerify;
        this.associateRequest = associateRequest;
        this.isSelected = isSelected;
        this.memberLatitude = memberLatitude;
        this.memberLongitude = memberLongitude;
        this.siteLatitude = siteLatitude;
        this.siteLongitude = siteLongitude;
        this.picLatitude = picLatitude;
        this.picLongitude = picLongitude;
    }

    public ModelSite(String siteCity, long siteId, Boolean isSelected) {
        this.siteCity = siteCity;
        this.siteId = siteId;
        this.isSelected=isSelected;
    }

    public ModelSite(String siteCity, long siteId) {
        this.siteCity = siteCity;
        this.siteId = siteId;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getSiteCity() {
        return siteCity;
    }

    public void setSiteCity(String siteCity) {
        this.siteCity = siteCity;
    }

    public String getSiteCountry() {
        return siteCountry;
    }

    public void setSiteCountry(String siteCountry) {
        this.siteCountry = siteCountry;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getHrUid() {
        return hrUid;
    }

    public void setHrUid(String hrUid) {
        this.hrUid = hrUid;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSiteCreatedDate() {
        return siteCreatedDate;
    }

    public void setSiteCreatedDate(String siteCreatedDate) {
        this.siteCreatedDate = siteCreatedDate;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getCashAmt() {
        return cashAmt;
    }

    public void setCashAmt(String cashAmt) {
        this.cashAmt = cashAmt;
    }

    public String getCashReqID() {
        return cashReqID;
    }

    public void setCashReqID(String cashReqID) {
        this.cashReqID = cashReqID;
    }

    public String getSkilledTime() {
        return SkilledTime;
    }

    public void setSkilledTime(String skilledTime) {
        SkilledTime = skilledTime;
    }

    public String getUnskilledTime() {
        return UnskilledTime;
    }

    public void setUnskilledTime(String unskilledTime) {
        UnskilledTime = unskilledTime;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPicId() {
        return picId;
    }

    public void setPicId(String picId) {
        this.picId = picId;
    }

    public String getPicTime() {
        return picTime;
    }

    public void setPicTime(String picTime) {
        this.picTime = picTime;
    }

    public String getPicDate() {
        return picDate;
    }

    public void setPicDate(String picDate) {
        this.picDate = picDate;
    }

    public String getPicLink() {
        return picLink;
    }

    public void setPicLink(String picLink) {
        this.picLink = picLink;
    }

    public long getSiteId() {
        return siteId;
    }

    public void setSiteId(long siteId) {
        this.siteId = siteId;
    }

    public long getSkilled() {
        return skilled;
    }

    public void setSkilled(long skilled) {
        this.skilled = skilled;
    }

    public long getUnskilled() {
        return unskilled;
    }

    public void setUnskilled(long unskilled) {
        this.unskilled = unskilled;
    }

    public long getCashInHand() {
        return cashInHand;
    }

    public void setCashInHand(long cashInHand) {
        this.cashInHand = cashInHand;
    }

    public Boolean getOnline() {
        return online;
    }

    public void setOnline(Boolean online) {
        this.online = online;
    }

    public Boolean getForceLogout() {
        return forceLogout;
    }

    public void setForceLogout(Boolean forceLogout) {
        this.forceLogout = forceLogout;
    }

    public Boolean getCashReq() {
        return cashReq;
    }

    public void setCashReq(Boolean cashReq) {
        this.cashReq = cashReq;
    }

    public Boolean getReqPendency() {
        return reqPendency;
    }

    public void setReqPendency(Boolean reqPendency) {
        this.reqPendency = reqPendency;
    }

    public Boolean getLocationVerifyConfirm() {
        return locationVerifyConfirm;
    }

    public void setLocationVerifyConfirm(Boolean locationVerifyConfirm) {
        this.locationVerifyConfirm = locationVerifyConfirm;
    }

    public Boolean getSkilledSeen() {
        return skilledSeen;
    }

    public void setSkilledSeen(Boolean skilledSeen) {
        this.skilledSeen = skilledSeen;
    }

    public Boolean getUnSkilledSeen() {
        return unSkilledSeen;
    }

    public void setUnSkilledSeen(Boolean unSkilledSeen) {
        this.unSkilledSeen = unSkilledSeen;
    }

    public Boolean getPicActivity() {
        return picActivity;
    }

    public void setPicActivity(Boolean picActivity) {
        this.picActivity = picActivity;
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

    public String getPicRemark() {
        return picRemark;
    }

    public void setPicRemark(String picRemark) {
        this.picRemark = picRemark;
    }

    public String getSiteAddress() {
        return siteAddress;
    }

    public void setSiteAddress(String siteAddress) {
        this.siteAddress = siteAddress;
    }

    public String getMemberStatus() {
        return memberStatus;
    }

    public void setMemberStatus(String memberStatus) {
        this.memberStatus = memberStatus;
    }

    public long getPaymentSum() {
        return paymentSum;
    }

    public void setPaymentSum(long paymentSum) {
        this.paymentSum = paymentSum;
    }

    public long getUpToDatePAyment() {
        return upToDatePAyment;
    }

    public void setUpToDatePAyment(long upToDatePAyment) {
        this.upToDatePAyment = upToDatePAyment;
    }

    public Boolean getForceOpt() {
        return forceOpt;
    }

    public void setForceOpt(Boolean forceOpt) {
        this.forceOpt = forceOpt;
    }

    public Boolean getWorkOpt() {
        return workOpt;
    }

    public void setWorkOpt(Boolean workOpt) {
        this.workOpt = workOpt;
    }

    public String getSkipTime() {
        return skipTime;
    }

    public void setSkipTime(String skipTime) {
        this.skipTime = skipTime;
    }

    public Boolean getLocationSkip() {
        return locationSkip;
    }

    public void setLocationSkip(Boolean locationSkip) {
        this.locationSkip = locationSkip;
    }

    public String getOnlineTimestamp() {
        return onlineTimestamp;
    }

    public void setOnlineTimestamp(String onlineTimestamp) {
        this.onlineTimestamp = onlineTimestamp;
    }



    public long getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(long memberCount) {
        this.memberCount = memberCount;
    }

    public String getSiteStatus() {
        return siteStatus;
    }

    public void setSiteStatus(String siteStatus) {
        this.siteStatus = siteStatus;
    }

    public Boolean getAttendanceEditSkilled() {
        return attendanceEditSkilled;
    }

    public void setAttendanceEditSkilled(Boolean attendanceEditSkilled) {
        this.attendanceEditSkilled = attendanceEditSkilled;
    }

    public Boolean getAttendanceEditUnSkilled() {
        return attendanceEditUnSkilled;

    }

    public void setAttendanceEditUnSkilled(Boolean attendanceEditUnSkilled) {
        this.attendanceEditUnSkilled = attendanceEditUnSkilled;
    }

    public String getPicUid() {
        return picUid;
    }

    public void setPicUid(String picUid) {
        this.picUid = picUid;
    }

    public Boolean getLocationVerify() {
        return locationVerify;
    }

    public void setLocationVerify(Boolean locationVerify) {
        this.locationVerify = locationVerify;
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

    public String getAssociateRequestType() {
        return associateRequestType;
    }

    public void setAssociateRequestType(String associateRequestType) {
        this.associateRequestType = associateRequestType;
    }

    public String getToPayAmount() {
        return toPayAmount;
    }

    public void setToPayAmount(String toPayAmount) {
        this.toPayAmount = toPayAmount;
    }

    public String getLabourIdPaying() {
        return labourIdPaying;
    }

    public void setLabourIdPaying(String labourIdPaying) {
        this.labourIdPaying = labourIdPaying;
    }

    public long getPayableAmount() {
        return payableAmount;
    }

    public void setPayableAmount(long payableAmount) {
        this.payableAmount = payableAmount;
    }

    public Boolean getAssociateRequest() {
        return associateRequest;
    }

    public void setAssociateRequest(Boolean associateRequest) {
        this.associateRequest = associateRequest;
    }

    public String getAssociateRequestStatus() {
        return associateRequestStatus;
    }

    public void setAssociateRequestStatus(String associateRequestStatus) {
        this.associateRequestStatus = associateRequestStatus;
    }

    public String getAssociateRequestReason() {
        return associateRequestReason;
    }

    public void setAssociateRequestReason(String associateRequestReason) {
        this.associateRequestReason = associateRequestReason;
    }

    public String getReqStatus() {
        return reqStatus;
    }

    public void setReqStatus(String reqStatus) {
        this.reqStatus = reqStatus;
    }

    public String getReqAmt() {
        return reqAmt;
    }

    public void setReqAmt(String reqAmt) {
        this.reqAmt = reqAmt;
    }

    public Boolean getSelected() {
        return isSelected;
    }

    public void setSelected(Boolean selected) {
        isSelected = selected;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
