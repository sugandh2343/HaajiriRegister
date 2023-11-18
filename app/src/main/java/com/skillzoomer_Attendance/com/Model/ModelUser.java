package com.skillzoomer_Attendance.com.Model;

public class ModelUser {
    private String uid,userId,password,name,mobile,userType,timestamp,siteName,companyName,companyAddress,companyEmail,
            companyWebsite,memberBlock,hrUid,tlUid,dateOfRegister,designationName,designationNameHindi,industryName,industryNameHindi,hrDesignation,hrDesignationHindi
            ;
    long siteId,designationPosition,industryPosition,industryCount;
    long workOpt;
    private Boolean attendanceManagement,cashManagement,financeManagement,workActivity,forceOpt;

    public ModelUser() {
    }

    public ModelUser(String uid, String userId, String password, String name, String mobile, String userType, String timestamp, String siteName, String companyName, String companyAddress, String companyEmail, String companyWebsite, String memberBlock, String hrUid, String dateOfRegister, String designationName, String designationNameHindi, String industryName, String industryNameHindi, long siteId, long designationPosition, long industryPosition,
                     long industryCount, long workOpt, Boolean attendanceManagement, Boolean cashManagement, Boolean financeManagement, Boolean workActivity, Boolean forceOpt) {
        this.uid = uid;
        this.userId = userId;
        this.password = password;
        this.name = name;
        this.mobile = mobile;
        this.userType = userType;
        this.timestamp = timestamp;
        this.siteName = siteName;
        this.companyName = companyName;
        this.companyAddress = companyAddress;
        this.companyEmail = companyEmail;
        this.companyWebsite = companyWebsite;
        this.memberBlock = memberBlock;
        this.hrUid = hrUid;
        this.dateOfRegister = dateOfRegister;
        this.designationName = designationName;
        this.designationNameHindi = designationNameHindi;
        this.industryName = industryName;
        this.industryNameHindi = industryNameHindi;
        this.siteId = siteId;
        this.designationPosition = designationPosition;
        this.industryPosition = industryPosition;
        this.industryCount = industryCount;
        this.workOpt = workOpt;
        this.attendanceManagement = attendanceManagement;
        this.cashManagement = cashManagement;
        this.financeManagement = financeManagement;
        this.workActivity = workActivity;
        this.forceOpt = forceOpt;
    }

    public ModelUser(String uid, String userId, String password, String name, String mobile, String userType, String timestamp, String siteName, String companyName, String companyAddress, String companyEmail, String companyWebsite,
                     String memberBlock, String hrUid, String dateOfRegister, String designationName, String designationNameHindi, String industryName, String industryNameHindi, String hrDesignation, String hrDesignationHindi, long siteId, long designationPosition, long industryPosition, long industryCount, long workOpt,
                     Boolean attendanceManagement, Boolean cashManagement, Boolean financeManagement, Boolean workActivity, Boolean forceOpt) {
        this.uid = uid;
        this.userId = userId;
        this.password = password;
        this.name = name;
        this.mobile = mobile;
        this.userType = userType;
        this.timestamp = timestamp;
        this.siteName = siteName;
        this.companyName = companyName;
        this.companyAddress = companyAddress;
        this.companyEmail = companyEmail;
        this.companyWebsite = companyWebsite;
        this.memberBlock = memberBlock;
        this.hrUid = hrUid;
        this.dateOfRegister = dateOfRegister;
        this.designationName = designationName;
        this.designationNameHindi = designationNameHindi;
        this.industryName = industryName;
        this.industryNameHindi = industryNameHindi;
        this.hrDesignation = hrDesignation;
        this.hrDesignationHindi = hrDesignationHindi;
        this.siteId = siteId;
        this.designationPosition = designationPosition;
        this.industryPosition = industryPosition;
        this.industryCount = industryCount;
        this.workOpt = workOpt;
        this.attendanceManagement = attendanceManagement;
        this.cashManagement = cashManagement;
        this.financeManagement = financeManagement;
        this.workActivity = workActivity;
        this.forceOpt = forceOpt;
    }

    public ModelUser(String uid, String name, String siteName, long siteId) {
        this.uid = uid;
        this.name = name;
        this.siteName = siteName;
        this.siteId = siteId;
    }

    public ModelUser(String uid, String userId, String password, String name, String mobile, String userType, String timestamp, String siteName, String companyName, String companyAddress, String companyEmail, String companyWebsite, String memberBlock, String hrUid, String dateOfRegister,
                     long siteId, long workOpt, Boolean attendanceManagement, Boolean cashManagement, Boolean financeManagement, Boolean workActivity, Boolean forceOpt) {
        this.uid = uid;
        this.userId = userId;
        this.password = password;
        this.name = name;
        this.mobile = mobile;
        this.userType = userType;
        this.timestamp = timestamp;
        this.siteName = siteName;
        this.companyName = companyName;
        this.companyAddress = companyAddress;
        this.companyEmail = companyEmail;
        this.companyWebsite = companyWebsite;
        this.memberBlock = memberBlock;
        this.hrUid = hrUid;
        this.dateOfRegister = dateOfRegister;
        this.siteId = siteId;
        this.workOpt = workOpt;
        this.attendanceManagement = attendanceManagement;
        this.cashManagement = cashManagement;
        this.financeManagement = financeManagement;
        this.workActivity = workActivity;
        this.forceOpt = forceOpt;
    }

    public ModelUser(String uid ,
                     String userId ,
                     String password ,
                     String name ,
                     String mobile ,
                     String userType ,
                     String timestamp ,
                     String siteName ,
                     String companyName ,
                     String companyAddress ,
                     String companyEmail ,
                     String companyWebsite ,
                     String memberBlock ,
                     String hrUid ,
                     String tlUid ,
                     String dateOfRegister ,
                     String designationName ,
                     String designationNameHindi ,
                     String industryName ,
                     String industryNameHindi ,
                     String hrDesignation ,
                     String hrDesignationHindi ,
                     long siteId ,
                     long designationPosition ,
                     long industryPosition ,
                     long industryCount ,
                     long workOpt ,
                     Boolean attendanceManagement ,
                     Boolean cashManagement ,
                     Boolean financeManagement ,
                     Boolean workActivity ,
                     Boolean forceOpt) {
        this.uid = uid;
        this.userId = userId;
        this.password = password;
        this.name = name;
        this.mobile = mobile;
        this.userType = userType;
        this.timestamp = timestamp;
        this.siteName = siteName;
        this.companyName = companyName;
        this.companyAddress = companyAddress;
        this.companyEmail = companyEmail;
        this.companyWebsite = companyWebsite;
        this.memberBlock = memberBlock;
        this.hrUid = hrUid;
        this.tlUid = tlUid;
        this.dateOfRegister = dateOfRegister;
        this.designationName = designationName;
        this.designationNameHindi = designationNameHindi;
        this.industryName = industryName;
        this.industryNameHindi = industryNameHindi;
        this.hrDesignation = hrDesignation;
        this.hrDesignationHindi = hrDesignationHindi;
        this.siteId = siteId;
        this.designationPosition = designationPosition;
        this.industryPosition = industryPosition;
        this.industryCount = industryCount;
        this.workOpt = workOpt;
        this.attendanceManagement = attendanceManagement;
        this.cashManagement = cashManagement;
        this.financeManagement = financeManagement;
        this.workActivity = workActivity;
        this.forceOpt = forceOpt;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public long getSiteId() {
        return siteId;
    }

    public void setSiteId(long siteId) {
        this.siteId = siteId;
    }

    public String getCompanyAddress() {
        return companyAddress;
    }

    public void setCompanyAddress(String companyAddress) {
        this.companyAddress = companyAddress;
    }

    public String getCompanyEmail() {
        return companyEmail;
    }

    public void setCompanyEmail(String companyEmail) {
        this.companyEmail = companyEmail;
    }

    public String getCompanyWebsite() {
        return companyWebsite;
    }

    public void setCompanyWebsite(String companyWebsite) {
        this.companyWebsite = companyWebsite;
    }

    public long getWorkOpt() {
        return workOpt;
    }

    public void setWorkOpt(long workOpt) {
        this.workOpt = workOpt;
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

    public Boolean getWorkActivity() {
        return workActivity;
    }

    public void setWorkActivity(Boolean workActivity) {
        this.workActivity = workActivity;
    }



    public String getMemberBlock() {
        return memberBlock;
    }

    public void setMemberBlock(String memberBlock) {
        this.memberBlock = memberBlock;
    }

    public String getHrUid() {
        return hrUid;
    }

    public void setHrUid(String hrUid) {
        this.hrUid = hrUid;
    }

    public String getDateOfRegister() {
        return dateOfRegister;
    }

    public void setDateOfRegister(String dateOfRegister) {
        this.dateOfRegister = dateOfRegister;
    }

    public Boolean getForceOpt() {
        return forceOpt;
    }

    public void setForceOpt(Boolean forceOpt) {
        this.forceOpt = forceOpt;
    }

    public String getDesignationName() {
        return designationName;
    }

    public void setDesignationName(String designationName) {
        this.designationName = designationName;
    }

    public String getDesignationNameHindi() {
        return designationNameHindi;
    }

    public void setDesignationNameHindi(String designationNameHindi) {
        this.designationNameHindi = designationNameHindi;
    }

    public long getDesignationPosition() {
        return designationPosition;
    }

    public void setDesignationPosition(long designationPosition) {
        this.designationPosition = designationPosition;
    }

    public String getIndustryName() {
        return industryName;
    }

    public void setIndustryName(String industryName) {
        this.industryName = industryName;
    }

    public String getIndustryNameHindi() {
        return industryNameHindi;
    }

    public void setIndustryNameHindi(String industryNameHindi) {
        this.industryNameHindi = industryNameHindi;
    }

    public long getIndustryPosition() {
        return industryPosition;
    }

    public void setIndustryPosition(long industryPosition) {
        this.industryPosition = industryPosition;
    }

    public long getIndustryCount() {
        return industryCount;
    }

    public void setIndustryCount(long industryCount) {
        this.industryCount = industryCount;
    }

    public String getHrDesignation() {
        return hrDesignation;
    }

    public void setHrDesignation(String hrDesignation) {
        this.hrDesignation = hrDesignation;
    }

    public String getHrDesignationHindi() {
        return hrDesignationHindi;
    }

    public void setHrDesignationHindi(String hrDesignationHindi) {
        this.hrDesignationHindi = hrDesignationHindi;
    }

    public String getTlUid() {
        return tlUid;
    }

    public void setTlUid(String tlUid) {
        this.tlUid = tlUid;
    }
}
