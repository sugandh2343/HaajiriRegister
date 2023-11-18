package com.skillzoomer_Attendance.com.Model;

public class ModelWorkPlace {

    Boolean Sunday,Monday,Tuesday,Wednesday,Thursday,Friday,Saturday,online,punchIn,punchOut;
    long siteId,siteLatitude,siteLongitude,industryPosition,circleRadius,circleCenterLat,circleCenterLon,workingHours,workingMins;
    String startTime,endTime,siteCreatedDate,memberStatus,hrUid,timestamp,siteAddress,siteCity,siteName;

    public ModelWorkPlace() {
    }

    public ModelWorkPlace(Boolean sunday,
                          Boolean monday,
                          Boolean tuesday,
                          Boolean wednesday,
                          Boolean thursday,
                          Boolean friday,
                          Boolean saturday,
                          Boolean online,
                          Boolean punchIn,
                          Boolean punchOut,
                          long siteId,
                          long siteLatitude,
                          long siteLongitude,
                          long industryPosition,
                          long circleRadius,
                          long circleCenterLat,
                          long circleCenterLon,
                          long workingHours,
                          long workingMins,
                          String startTime,
                          String endTime,
                          String siteCreatedDate,
                          String memberStatus,
                          String hrUid,
                          String timestamp,
                          String siteAddress,
                          String siteCity,
                          String siteName) {
        Sunday = sunday;
        Monday = monday;
        Tuesday = tuesday;
        Wednesday = wednesday;
        Thursday = thursday;
        Friday = friday;
        Saturday = saturday;
        this.online = online;
        this.punchIn = punchIn;
        this.punchOut = punchOut;
        this.siteId = siteId;
        this.siteLatitude = siteLatitude;
        this.siteLongitude = siteLongitude;
        this.industryPosition = industryPosition;
        this.circleRadius = circleRadius;
        this.circleCenterLat = circleCenterLat;
        this.circleCenterLon = circleCenterLon;
        this.workingHours = workingHours;
        this.workingMins = workingMins;
        this.startTime = startTime;
        this.endTime = endTime;
        this.siteCreatedDate = siteCreatedDate;
        this.memberStatus = memberStatus;
        this.hrUid = hrUid;
        this.timestamp = timestamp;
        this.siteAddress = siteAddress;
        this.siteCity = siteCity;
        this.siteName = siteName;
    }

    public ModelWorkPlace(String siteCity) {
        this.siteCity = siteCity;
    }

    public Boolean getSunday() {
        return Sunday;
    }

    public void setSunday(Boolean sunday) {
        Sunday = sunday;
    }

    public Boolean getMonday() {
        return Monday;
    }

    public void setMonday(Boolean monday) {
        Monday = monday;
    }

    public Boolean getTuesday() {
        return Tuesday;
    }

    public void setTuesday(Boolean tuesday) {
        Tuesday = tuesday;
    }

    public Boolean getWednesday() {
        return Wednesday;
    }

    public void setWednesday(Boolean wednesday) {
        Wednesday = wednesday;
    }

    public Boolean getThursday() {
        return Thursday;
    }

    public void setThursday(Boolean thursday) {
        Thursday = thursday;
    }

    public Boolean getFriday() {
        return Friday;
    }

    public void setFriday(Boolean friday) {
        Friday = friday;
    }

    public Boolean getSaturday() {
        return Saturday;
    }

    public void setSaturday(Boolean saturday) {
        Saturday = saturday;
    }

    public Boolean getOnline() {
        return online;
    }

    public void setOnline(Boolean online) {
        this.online = online;
    }

    public long getSiteId() {
        return siteId;
    }

    public void setSiteId(long siteId) {
        this.siteId = siteId;
    }

    public long getSiteLatitude() {
        return siteLatitude;
    }

    public void setSiteLatitude(long siteLatitude) {
        this.siteLatitude = siteLatitude;
    }

    public long getSiteLongitude() {
        return siteLongitude;
    }

    public void setSiteLongitude(long siteLongitude) {
        this.siteLongitude = siteLongitude;
    }

    public long getIndustryPosition() {
        return industryPosition;
    }

    public void setIndustryPosition(long industryPosition) {
        this.industryPosition = industryPosition;
    }

    public long getCircleRadius() {
        return circleRadius;
    }

    public void setCircleRadius(long circleRadius) {
        this.circleRadius = circleRadius;
    }

    public long getCircleCenterLat() {
        return circleCenterLat;
    }

    public void setCircleCenterLat(long circleCenterLat) {
        this.circleCenterLat = circleCenterLat;
    }

    public long getCircleCenterLon() {
        return circleCenterLon;
    }

    public void setCircleCenterLon(long circleCenterLon) {
        this.circleCenterLon = circleCenterLon;
    }

    public long getWorkingHours() {
        return workingHours;
    }

    public void setWorkingHours(long workingHours) {
        this.workingHours = workingHours;
    }

    public long getWorkingMins() {
        return workingMins;
    }

    public void setWorkingMins(long workingMins) {
        this.workingMins = workingMins;
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

    public String getSiteCreatedDate() {
        return siteCreatedDate;
    }

    public void setSiteCreatedDate(String siteCreatedDate) {
        this.siteCreatedDate = siteCreatedDate;
    }

    public String getMemberStatus() {
        return memberStatus;
    }

    public void setMemberStatus(String memberStatus) {
        this.memberStatus = memberStatus;
    }

    public String getHrUid() {
        return hrUid;
    }

    public void setHrUid(String hrUid) {
        this.hrUid = hrUid;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getSiteAddress() {
        return siteAddress;
    }

    public void setSiteAddress(String siteAddress) {
        this.siteAddress = siteAddress;
    }

    public String getSiteCity() {
        return siteCity;
    }

    public void setSiteCity(String siteCity) {
        this.siteCity = siteCity;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public Boolean getPunchIn() {
        return punchIn;
    }

    public void setPunchIn(Boolean punchIn) {
        this.punchIn = punchIn;
    }

    public Boolean getPunchOut() {
        return punchOut;
    }

    public void setPunchOut(Boolean punchOut) {
        this.punchOut = punchOut;
    }
}
