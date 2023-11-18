package com.skillzoomer_Attendance.com.Model;

public class ModelParentClass {
    private String workerId,workerName,workerType;

    public ModelParentClass() {
    }

    public ModelParentClass(String workerId, String workerName, String workerType) {
        this.workerId = workerId;
        this.workerName = workerName;
        this.workerType = workerType;
    }

    public String getWorkerId() {
        return workerId;
    }

    public void setWorkerId(String workerId) {
        this.workerId = workerId;
    }

    public String getWorkerName() {
        return workerName;
    }

    public void setWorkerName(String workerName) {
        this.workerName = workerName;
    }

    public String getWorkerType() {
        return workerType;
    }

    public void setWorkerType(String workerType) {
        this.workerType = workerType;
    }
}
