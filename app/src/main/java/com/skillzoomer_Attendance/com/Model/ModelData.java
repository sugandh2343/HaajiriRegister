package com.skillzoomer_Attendance.com.Model;

import java.util.ArrayList;

public class ModelData {
    private String Name,Date;
    private int Count,position;
    private ArrayList<Integer> multiple;

    public ModelData() {
    }

    public ModelData(String name, String date, int count, int position, ArrayList<Integer> multiple) {
        Name = name;
        Date = date;
        Count = count;
        this.position = position;
        this.multiple = multiple;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public int getCount() {
        return Count;
    }

    public void setCount(int count) {
        Count = count;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public ArrayList<Integer> getMultiple() {
        return multiple;
    }

    public void setMultiple(ArrayList<Integer> multiple) {
        this.multiple = multiple;
    }
}
