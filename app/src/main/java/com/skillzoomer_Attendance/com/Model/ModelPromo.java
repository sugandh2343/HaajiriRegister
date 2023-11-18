package com.skillzoomer_Attendance.com.Model;

public class ModelPromo {
    String details,id,name,title,url;
    long daysValid,discount;

    public ModelPromo() {
    }

    public ModelPromo(String details, String id, String name, String title, String url, long daysValid, long discount) {
        this.details = details;
        this.id = id;
        this.name = name;
        this.title = title;
        this.url = url;
        this.daysValid = daysValid;
        this.discount = discount;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getDaysValid() {
        return daysValid;
    }

    public void setDaysValid(long daysValid) {
        this.daysValid = daysValid;
    }

    public long getDiscount() {
        return discount;
    }

    public void setDiscount(long discount) {
        this.discount = discount;
    }
}
