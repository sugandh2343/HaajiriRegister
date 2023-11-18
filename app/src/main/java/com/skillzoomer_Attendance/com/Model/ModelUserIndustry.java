package com.skillzoomer_Attendance.com.Model;

public class ModelUserIndustry {
    private String industryName,industryNameHindi,companyName,companyEmail,designationName,designationNameHindi;
    private long designationPosition,industryPosition;

    public ModelUserIndustry() {

    }

    public ModelUserIndustry(String industryName, String industryNameHindi,
                             String companyName, String companyEmail, String designationName, String designationNameHindi, long designationPosition, long industryPosition) {
        this.industryName = industryName;
        this.industryNameHindi = industryNameHindi;
        this.companyName = companyName;
        this.companyEmail = companyEmail;
        this.designationName = designationName;
        this.designationNameHindi = designationNameHindi;
        this.designationPosition = designationPosition;
        this.industryPosition = industryPosition;
    }

    public ModelUserIndustry(String industryName, long industryPosition) {
        this.industryName = industryName;
        this.industryPosition = industryPosition;
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

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyEmail() {
        return companyEmail;
    }

    public void setCompanyEmail(String companyEmail) {
        this.companyEmail = companyEmail;
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

    public long getIndustryPosition() {
        return industryPosition;
    }

    public void setIndustryPosition(long industryPosition) {
        this.industryPosition = industryPosition;
    }
}