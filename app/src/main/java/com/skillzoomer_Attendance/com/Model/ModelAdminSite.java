package com.skillzoomer_Attendance.com.Model;

public class ModelAdminSite {
    long siteId,tm,labourCount,attendanceCount;
    String siteName;

    public ModelAdminSite() {
    }

    public ModelAdminSite(long siteId, long tm, long labourCount, long attendanceCount, String siteName) {
        this.siteId = siteId;
        this.tm = tm;
        this.labourCount = labourCount;
        this.attendanceCount = attendanceCount;
        this.siteName = siteName;
    }

    public long getSiteId() {
        return siteId;
    }

    public void setSiteId(long siteId) {
        this.siteId = siteId;
    }

    public long getTm() {
        return tm;
    }

    public void setTm(long tm) {
        this.tm = tm;
    }

    public long getLabourCount() {
        return labourCount;
    }

    public void setLabourCount(long labourCount) {
        this.labourCount = labourCount;
    }

    public long getAttendanceCount() {
        return attendanceCount;
    }

    public void setAttendanceCount(long attendanceCount) {
        this.attendanceCount = attendanceCount;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }
}
