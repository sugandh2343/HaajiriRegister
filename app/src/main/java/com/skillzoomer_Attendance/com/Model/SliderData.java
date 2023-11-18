package com.skillzoomer_Attendance.com.Model;

public class SliderData {
    private String url,msg,msgHindi,urlHindi;

    public SliderData() {
    }

    public SliderData(String url, String msg, String msgHindi, String urlHindi) {
        this.url = url;
        this.msg = msg;
        this.msgHindi = msgHindi;
        this.urlHindi = urlHindi;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getMsgHindi() {
        return msgHindi;
    }

    public void setMsgHindi(String msgHindi) {
        this.msgHindi = msgHindi;
    }

    public String getUrlHindi() {
        return urlHindi;
    }

    public void setUrlHindi(String urlHindi) {
        this.urlHindi = urlHindi;
    }
}
