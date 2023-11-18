package com.skillzoomer_Attendance.com.Model;

public class ModelEditLabour {
    String labourId,labourName,status;

    public ModelEditLabour() {
    }

    public ModelEditLabour(String labourId, String labourName, String status) {
        this.labourId = labourId;
        this.labourName = labourName;
        this.status = status;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
