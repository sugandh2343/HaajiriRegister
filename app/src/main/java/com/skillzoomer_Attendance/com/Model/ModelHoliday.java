package com.skillzoomer_Attendance.com.Model;

public class ModelHoliday {
    String id,date,title,addedBy;

    public ModelHoliday() {
    }

    public ModelHoliday(String id, String date, String title, String addedBy) {
        this.id = id;
        this.date = date;
        this.title = title;
        this.addedBy = addedBy;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAddedBy() {
        return addedBy;
    }

    public void setAddedBy(String addedBy) {
        this.addedBy = addedBy;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
