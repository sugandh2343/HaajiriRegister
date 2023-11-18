package com.skillzoomer_Attendance.com.Model;

import java.util.ArrayList;

public class ModelAttendanceMaster {
    private String date;
    private ArrayList<ModelShowAttendance> showAttendanceArrayList;

    public ModelAttendanceMaster() {
    }

    public ModelAttendanceMaster(String date , ArrayList<ModelShowAttendance> showAttendanceArrayList) {
        this.date = date;
        this.showAttendanceArrayList = showAttendanceArrayList;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public ArrayList<ModelShowAttendance> getShowAttendanceArrayList() {
        return showAttendanceArrayList;
    }

    public void setShowAttendanceArrayList(ArrayList<ModelShowAttendance> showAttendanceArrayList) {
        this.showAttendanceArrayList = showAttendanceArrayList;
    }
}
