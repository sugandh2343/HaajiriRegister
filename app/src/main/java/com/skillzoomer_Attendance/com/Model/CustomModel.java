package com.skillzoomer_Attendance.com.Model;

import android.net.Uri;

/*This model class is required only if you are going to show the list of selected images*/
public class CustomModel {
    String imageName;
    Uri imageURI;
    String extension;

    public CustomModel() {
    }

    public CustomModel(String imageName, Uri imageURI, String extension) {
        this.imageName = imageName;
        this.imageURI = imageURI;
        this.extension = extension;
    }

    public String getImageName() {
        return imageName;
    }

    public Uri getImageURI() {
        return imageURI;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public void setImageURI(Uri imageURI) {
        this.imageURI = imageURI;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }
}
