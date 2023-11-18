package com.skillzoomer_Attendance.com.Model;

public class ModelAttendanceEmployee implements Comparable<ModelAttendanceEmployee> {
    String date,empId,empNmae,status;
    int count;

    public ModelAttendanceEmployee() {
    }

    public ModelAttendanceEmployee(String date, String empId, String empNmae, String status) {
        this.date = date;
        this.empId = empId;
        this.empNmae = empNmae;
        this.status = status;
    }

    public ModelAttendanceEmployee(String date, String empId, String empNmae, String status, int count) {
        this.date = date;
        this.empId = empId;
        this.empNmae = empNmae;
        this.status = status;
        this.count = count;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getEmpId() {
        return empId;
    }

    public void setEmpId(String empId) {
        this.empId = empId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEmpNmae() {
        return empNmae;
    }

    public void setEmpNmae(String empNmae) {
        this.empNmae = empNmae;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }


    @Override
    public int compareTo(ModelAttendanceEmployee modelAttendanceEmployee) {
        int countTemp=modelAttendanceEmployee.getCount();
        return count-countTemp;
    }
    @Override public String toString() {
        return "[ Count=" + count + ", name="
                + empNmae + ", id=" + empId + "Date:"+date+"]";
    }
}
