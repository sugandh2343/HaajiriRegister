package com.skillzoomer_Attendance.com.Model;

public class ModelTransaction  {
    private String dateOfPayment,timeOfPayment,timestamp,paymentId,orderId,signature,paidAmount,status,
    downloadFile,fromDate,toDate,promoTitle,siteName,fileType,totalAmount;
    private Boolean promoApplied;
    private long siteId;

    public ModelTransaction() {
    }

    public ModelTransaction(String dateOfPayment, String timeOfPayment, String timestamp, String paymentId, String orderId, String signature, String paidAmount, String status, String downloadFile,
                            String fromDate, String toDate, String promoTitle, String siteName, String fileType, String totalAmount, Boolean promoApplied, long siteId) {
        this.dateOfPayment = dateOfPayment;
        this.timeOfPayment = timeOfPayment;
        this.timestamp = timestamp;
        this.paymentId = paymentId;
        this.orderId = orderId;
        this.signature = signature;
        this.paidAmount = paidAmount;
        this.status = status;
        this.downloadFile = downloadFile;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.promoTitle = promoTitle;
        this.siteName = siteName;
        this.fileType = fileType;
        this.totalAmount = totalAmount;
        this.promoApplied = promoApplied;
        this.siteId = siteId;
    }

    public String getDateOfPayment() {
        return dateOfPayment;
    }

    public void setDateOfPayment(String dateOfPayment) {
        this.dateOfPayment = dateOfPayment;
    }

    public String getTimeOfPayment() {
        return timeOfPayment;
    }

    public void setTimeOfPayment(String timeOfPayment) {
        this.timeOfPayment = timeOfPayment;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(String paidAmount) {
        this.paidAmount = paidAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDownloadFile() {
        return downloadFile;
    }

    public void setDownloadFile(String downloadFile) {
        this.downloadFile = downloadFile;
    }

    public String getFromDate() {
        return fromDate;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public String getToDate() {
        return toDate;
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
    }

    public String getPromoTitle() {
        return promoTitle;
    }

    public void setPromoTitle(String promoTitle) {
        this.promoTitle = promoTitle;
    }

    public Boolean getPromoApplied() {
        return promoApplied;
    }

    public void setPromoApplied(Boolean promoApplied) {
        this.promoApplied = promoApplied;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public long getSiteId() {
        return siteId;
    }

    public void setSiteId(long siteId) {
        this.siteId = siteId;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }
}
