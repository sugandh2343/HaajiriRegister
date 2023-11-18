package com.skillzoomer_Attendance.com.Model;

public class ModelDesignation {
    private long id;
    private String image,name,nameHindi;

    public ModelDesignation(long id, String image, String name, String nameHindi) {
        this.id = id;
        this.image = image;
        this.name = name;
        this.nameHindi = nameHindi;
    }

    public ModelDesignation() {
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

    public String getNameHindi() {
        return nameHindi;
    }

    public void setNameHindi(String nameHindi) {
        this.nameHindi = nameHindi;
    }
}

