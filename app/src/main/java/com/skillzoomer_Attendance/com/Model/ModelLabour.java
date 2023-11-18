package com.skillzoomer_Attendance.com.Model;

import java.util.ArrayList;

public class ModelLabour {
    String fatherName, labourId,name,profile,siteName,timestamp,uniqueId,type,dateOfRegister,status;
    int siteCode,originalPosition;
    long wages,payableAmt;
    Boolean present;


    public ModelLabour() {
    }

    public ModelLabour(String fatherName, String labourId, String name, String profile, String siteName, String timestamp,
                       String uniqueId, String type, String dateOfRegister, String status, int siteCode, long wages, long payableAmt, Boolean present) {
        this.fatherName = fatherName;
        this.labourId = labourId;
        this.name = name;
        this.profile = profile;
        this.siteName = siteName;
        this.timestamp = timestamp;
        this.uniqueId = uniqueId;
        this.type = type;
        this.dateOfRegister = dateOfRegister;
        this.status = status;
        this.siteCode = siteCode;
        this.wages = wages;
        this.payableAmt = payableAmt;
        this.present = present;
    }

    public String getFatherName() {
        return fatherName;
    }

    public void setFatherName(String fatherName) {
        this.fatherName = fatherName;
    }

    public String getLabourId() {
        return labourId;
    }

    public void setLabourId(String labourId) {
        this.labourId = labourId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(int siteCode) {
        this.siteCode = siteCode;
    }

    public long getWages() {
        return wages;
    }

    public String getDateOfRegister() {
        return dateOfRegister;
    }

    public void setDateOfRegister(String dateOfRegister) {
        this.dateOfRegister = dateOfRegister;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }



    public Boolean getPresent() {
        return present;
    }

    public void setPresent(Boolean present) {
        this.present = present;
    }

    public void setWages(long wages) {
        this.wages = wages;
    }

    public long getPayableAmt() {
        return payableAmt;
    }

    public void setPayableAmt(long payableAmt) {
        this.payableAmt = payableAmt;
    }

    public int getOriginalPosition() {
        return originalPosition;
    }

    public void setOriginalPosition(int originalPosition) {
        this.originalPosition = originalPosition;
    }
}
