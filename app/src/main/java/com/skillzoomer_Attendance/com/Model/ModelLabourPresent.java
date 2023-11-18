package com.skillzoomer_Attendance.com.Model;

public class ModelLabourPresent {
    private String dateOfUpload,labourId,labourName,labourType,timeOfupload,uploadedBYUid,uploadedByName;

    public ModelLabourPresent() {
    }

    public ModelLabourPresent(String dateOfUpload, String labourId, String labourName, String labourType, String timeOfupload, String uploadedBYUid, String uploadedByName) {
        this.dateOfUpload = dateOfUpload;
        this.labourId = labourId;
        this.labourName = labourName;
        this.labourType = labourType;
        this.timeOfupload = timeOfupload;
        this.uploadedBYUid = uploadedBYUid;
        this.uploadedByName = uploadedByName;
    }

    public String getDateOfUpload() {
        return dateOfUpload;
    }

    public void setDateOfUpload(String dateOfUpload) {
        this.dateOfUpload = dateOfUpload;
    }

    public String getLabourId() {
        return labourId;
    }

    public void setLabourId(String labourId) {
        this.labourId = labourId;
    }

    public String getLabourName() {
        return labourName;
    }

    public void setLabourName(String labourName) {
        this.labourName = labourName;
    }

    public String getLabourType() {
        return labourType;
    }

    public void setLabourType(String labourType) {
        this.labourType = labourType;
    }

    public String getTimeOfupload() {
        return timeOfupload;
    }

    public void setTimeOfupload(String timeOfupload) {
        this.timeOfupload = timeOfupload;
    }

    public String getUploadedBYUid() {
        return uploadedBYUid;
    }

    public void setUploadedBYUid(String uploadedBYUid) {
        this.uploadedBYUid = uploadedBYUid;
    }

    public String getUploadedByName() {
        return uploadedByName;
    }

    public void setUploadedByName(String uploadedByName) {
        this.uploadedByName = uploadedByName;
    }
}
