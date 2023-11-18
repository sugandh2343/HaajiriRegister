package com.skillzoomer_Attendance.com.Model;

import java.util.ArrayList;

public class ModelAttendance {
   String labourId,labourName ,dateOfUpload ,timeOfupload, uploadedByName ,uploadedBYUid;

   public ModelAttendance() {
   }

   public ModelAttendance(String labourId, String labourName, String dateOfUpload, String timeOfupload, String uploadedByName, String uploadedBYUid) {
      this.labourId = labourId;
      this.labourName = labourName;
      this.dateOfUpload = dateOfUpload;
      this.timeOfupload = timeOfupload;
      this.uploadedByName = uploadedByName;
      this.uploadedBYUid = uploadedBYUid;
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

   public String getDateOfUpload() {
      return dateOfUpload;
   }

   public void setDateOfUpload(String dateOfUpload) {
      this.dateOfUpload = dateOfUpload;
   }

   public String getTimeOfupload() {
      return timeOfupload;
   }

   public void setTimeOfupload(String timeOfupload) {
      this.timeOfupload = timeOfupload;
   }

   public String getUploadedByName() {
      return uploadedByName;
   }

   public void setUploadedByName(String uploadedByName) {
      this.uploadedByName = uploadedByName;
   }

   public String getUploadedBYUid() {
      return uploadedBYUid;
   }

   public void setUploadedBYUid(String uploadedBYUid) {
      this.uploadedBYUid = uploadedBYUid;
   }
}
