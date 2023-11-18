package com.skillzoomer_Attendance.com.Model;

public class ModelCategory {
    private long id;
    private String image,name,hindiName;

    public ModelCategory() {
    }

    public ModelCategory(long id, String image, String name, String hindiName) {
        this.id = id;
        this.image = image;
        this.name = name;
        this.hindiName = hindiName;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHindiName() {
        return hindiName;
    }

    public void setHindiName(String hindiName) {
        this.hindiName = hindiName;
    }
}
