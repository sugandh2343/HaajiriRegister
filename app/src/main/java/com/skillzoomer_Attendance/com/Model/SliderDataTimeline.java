package com.skillzoomer_Attendance.com.Model;

public class SliderDataTimeline {
    private String url,urlHindi;

    public SliderDataTimeline() {
    }

    public SliderDataTimeline(String url, String urlHindi) {
        this.url = url;
        this.urlHindi = urlHindi;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrlHindi() {
        return urlHindi;
    }

    public void setUrlHindi(String urlHindi) {
        this.urlHindi = urlHindi;
    }
}
