package com.skillzoomer_Attendance.com.Model;

public class ModelMember {
    String MemberType,MemberName,MemberMobile,MemberStatus,Hruid,siteName;
    Long siteId;

    public ModelMember() {
    }

    public ModelMember(String memberType , String memberName , String memberMobile , String memberStatus , String hruid , String siteName , Long siteId) {
        MemberType = memberType;
        MemberName = memberName;
        MemberMobile = memberMobile;
        MemberStatus = memberStatus;
        Hruid = hruid;
        this.siteName = siteName;
        this.siteId = siteId;
    }

    public String getMemberType() {
        return MemberType;
    }

    public void setMemberType(String memberType) {
        MemberType = memberType;
    }

    public String getMemberName() {
        return MemberName;
    }

    public void setMemberName(String memberName) {
        MemberName = memberName;
    }

    public String getMemberMobile() {
        return MemberMobile;
    }

    public void setMemberMobile(String memberMobile) {
        MemberMobile = memberMobile;
    }

    public String getMemberStatus() {
        return MemberStatus;
    }

    public void setMemberStatus(String memberStatus) {
        MemberStatus = memberStatus;
    }

    public String getHruid() {
        return Hruid;
    }

    public void setHruid(String hruid) {
        Hruid = hruid;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }
}
