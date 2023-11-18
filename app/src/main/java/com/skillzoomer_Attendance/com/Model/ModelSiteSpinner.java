package com.skillzoomer_Attendance.com.Model;

public class ModelSiteSpinner {
    long siteId;
    String siteName;

    public ModelSiteSpinner(long siteId, String siteName) {
        this.siteId = siteId;
        this.siteName = siteName;
    }

    public long getSiteId() {
        return siteId;
    }

    public void setSiteId(long siteId) {
        this.siteId = siteId;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    @Override
    public String toString() {
        return this.siteName;
    }
}
