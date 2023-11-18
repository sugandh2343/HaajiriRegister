package com.skillzoomer_Attendance.com.Model;

public class ModelEventCategory {
    long id;
    String name;

    public ModelEventCategory() {
    }

    public ModelEventCategory(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
