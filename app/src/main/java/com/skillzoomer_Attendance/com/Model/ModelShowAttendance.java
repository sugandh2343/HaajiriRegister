package com.skillzoomer_Attendance.com.Model;

import java.util.ArrayList;

public class ModelShowAttendance {
    String LabourId,labourName;
    ArrayList<DateModel> dateModelArrayList;

    public ModelShowAttendance(String labourId , String labourName , ArrayList<DateModel> dateModelArrayList) {
        LabourId = labourId;
        this.labourName = labourName;
        this.dateModelArrayList = dateModelArrayList;
    }

    public ModelShowAttendance() {
    }

    public String getLabourId() {
        return LabourId;
    }

    public void setLabourId(String labourId) {
        LabourId = labourId;
    }

    public String getLabourName() {
        return labourName;
    }

    public void setLabourName(String labourName) {
        this.labourName = labourName;
    }

    public ArrayList<DateModel> getDateModelArrayList() {
        return dateModelArrayList;
    }

    public void setDateModelArrayList(ArrayList<DateModel> dateModelArrayList) {
        this.dateModelArrayList = dateModelArrayList;
    }
}
