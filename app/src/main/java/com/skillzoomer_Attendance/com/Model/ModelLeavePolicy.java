package com.skillzoomer_Attendance.com.Model;

public class ModelLeavePolicy {
    long policyId;
    String policyName,latePenalty,shortLeaveTerm,shortLeaveCount,shortLeaveDuration,lateTerm,lateCount,lateDuration,halfdayTerm,halfdayCount,maxhalfday,clTerm,
            clCount,maxCl,elTerm,elCount,maxEL,mlTerm,mlCount,plTerm,plCount,slTerm,slCount,maxSL;
    Boolean shortLeave,late,halfday,cl,el,ml,pl,sl,latePenaltyCl,latePenaltySalary,halfdaycarry,clcarry,elcarry,slcarry;

    public ModelLeavePolicy() {
    }

    public ModelLeavePolicy(long policyId, String policyName) {
        this.policyId = policyId;
        this.policyName = policyName;
    }

    public ModelLeavePolicy(long policyId,
                            String policyName,
                            String latePenalty,
                            String shortLeaveTerm,
                            String shortLeaveCount,
                            String shortLeaveDuration,
                            String lateTerm,
                            String lateCount,
                            String lateDuration,
                            String halfdayTerm,
                            String halfdayCount,
                            String maxhalfday,
                            String clTerm,
                            String clCount,
                            String maxCl,
                            String elTerm,
                            String elCount,
                            String maxEL,
                            String mlTerm,
                            String mlCount,
                            String plTerm,
                            String plCount,
                            String slTerm,
                            String slCount,
                            String maxSL,
                            Boolean shortLeave,
                            Boolean late,
                            Boolean halfday,
                            Boolean cl,
                            Boolean el,
                            Boolean ml,
                            Boolean pl,
                            Boolean sl,
                            Boolean latePenaltyCl,
                            Boolean latePenaltySalary,
                            Boolean halfdaycarry,
                            Boolean clcarry,
                            Boolean elcarry,
                            Boolean slcarry) {
        this.policyId = policyId;
        this.policyName = policyName;
        this.latePenalty = latePenalty;
        this.shortLeaveTerm = shortLeaveTerm;
        this.shortLeaveCount = shortLeaveCount;
        this.shortLeaveDuration = shortLeaveDuration;
        this.lateTerm = lateTerm;
        this.lateCount = lateCount;
        this.lateDuration = lateDuration;
        this.halfdayTerm = halfdayTerm;
        this.halfdayCount = halfdayCount;
        this.maxhalfday = maxhalfday;
        this.clTerm = clTerm;
        this.clCount = clCount;
        this.maxCl = maxCl;
        this.elTerm = elTerm;
        this.elCount = elCount;
        this.maxEL = maxEL;
        this.mlTerm = mlTerm;
        this.mlCount = mlCount;
        this.plTerm = plTerm;
        this.plCount = plCount;
        this.slTerm = slTerm;
        this.slCount = slCount;
        this.maxSL = maxSL;
        this.shortLeave = shortLeave;
        this.late = late;
        this.halfday = halfday;
        this.cl = cl;
        this.el = el;
        this.ml = ml;
        this.pl = pl;
        this.sl = sl;
        this.latePenaltyCl = latePenaltyCl;
        this.latePenaltySalary = latePenaltySalary;
        this.halfdaycarry = halfdaycarry;
        this.clcarry = clcarry;
        this.elcarry = elcarry;
        this.slcarry = slcarry;
    }

    public long getPolicyId() {
        return policyId;
    }

    public void setPolicyId(long policyId) {
        this.policyId = policyId;
    }

    public String getPolicyName() {
        return policyName;
    }

    public void setPolicyName(String policyName) {
        this.policyName = policyName;
    }

    public String getLatePenalty() {
        return latePenalty;
    }

    public void setLatePenalty(String latePenalty) {
        this.latePenalty = latePenalty;
    }

    public String getShortLeaveTerm() {
        return shortLeaveTerm;
    }

    public void setShortLeaveTerm(String shortLeaveTerm) {
        this.shortLeaveTerm = shortLeaveTerm;
    }

    public String getShortLeaveCount() {
        return shortLeaveCount;
    }

    public void setShortLeaveCount(String shortLeaveCount) {
        this.shortLeaveCount = shortLeaveCount;
    }

    public String getShortLeaveDuration() {
        return shortLeaveDuration;
    }

    public void setShortLeaveDuration(String shortLeaveDuration) {
        this.shortLeaveDuration = shortLeaveDuration;
    }

    public String getLateTerm() {
        return lateTerm;
    }

    public void setLateTerm(String lateTerm) {
        this.lateTerm = lateTerm;
    }

    public String getLateCount() {
        return lateCount;
    }

    public void setLateCount(String lateCount) {
        this.lateCount = lateCount;
    }

    public String getLateDuration() {
        return lateDuration;
    }

    public void setLateDuration(String lateDuration) {
        this.lateDuration = lateDuration;
    }

    public String getHalfdayTerm() {
        return halfdayTerm;
    }

    public void setHalfdayTerm(String halfdayTerm) {
        this.halfdayTerm = halfdayTerm;
    }

    public String getHalfdayCount() {
        return halfdayCount;
    }

    public void setHalfdayCount(String halfdayCount) {
        this.halfdayCount = halfdayCount;
    }

    public String getMaxhalfday() {
        return maxhalfday;
    }

    public void setMaxhalfday(String maxhalfday) {
        this.maxhalfday = maxhalfday;
    }

    public String getClTerm() {
        return clTerm;
    }

    public void setClTerm(String clTerm) {
        this.clTerm = clTerm;
    }

    public String getClCount() {
        return clCount;
    }

    public void setClCount(String clCount) {
        this.clCount = clCount;
    }

    public String getMaxCl() {
        return maxCl;
    }

    public void setMaxCl(String maxCl) {
        this.maxCl = maxCl;
    }

    public String getElTerm() {
        return elTerm;
    }

    public void setElTerm(String elTerm) {
        this.elTerm = elTerm;
    }

    public String getElCount() {
        return elCount;
    }

    public void setElCount(String elCount) {
        this.elCount = elCount;
    }

    public String getMaxEL() {
        return maxEL;
    }

    public void setMaxEL(String maxEL) {
        this.maxEL = maxEL;
    }

    public String getMlTerm() {
        return mlTerm;
    }

    public void setMlTerm(String mlTerm) {
        this.mlTerm = mlTerm;
    }

    public String getMlCount() {
        return mlCount;
    }

    public void setMlCount(String mlCount) {
        this.mlCount = mlCount;
    }

    public String getPlTerm() {
        return plTerm;
    }

    public void setPlTerm(String plTerm) {
        this.plTerm = plTerm;
    }

    public String getPlCount() {
        return plCount;
    }

    public void setPlCount(String plCount) {
        this.plCount = plCount;
    }

    public String getSlTerm() {
        return slTerm;
    }

    public void setSlTerm(String slTerm) {
        this.slTerm = slTerm;
    }

    public String getSlCount() {
        return slCount;
    }

    public void setSlCount(String slCount) {
        this.slCount = slCount;
    }

    public String getMaxSL() {
        return maxSL;
    }

    public void setMaxSL(String maxSL) {
        this.maxSL = maxSL;
    }

    public Boolean getShortLeave() {
        return shortLeave;
    }

    public void setShortLeave(Boolean shortLeave) {
        this.shortLeave = shortLeave;
    }

    public Boolean getLate() {
        return late;
    }

    public void setLate(Boolean late) {
        this.late = late;
    }

    public Boolean getHalfday() {
        return halfday;
    }

    public void setHalfday(Boolean halfday) {
        this.halfday = halfday;
    }

    public Boolean getCl() {
        return cl;
    }

    public void setCl(Boolean cl) {
        this.cl = cl;
    }

    public Boolean getEl() {
        return el;
    }

    public void setEl(Boolean el) {
        this.el = el;
    }

    public Boolean getMl() {
        return ml;
    }

    public void setMl(Boolean ml) {
        this.ml = ml;
    }

    public Boolean getPl() {
        return pl;
    }

    public void setPl(Boolean pl) {
        this.pl = pl;
    }

    public Boolean getSl() {
        return sl;
    }

    public void setSl(Boolean sl) {
        this.sl = sl;
    }

    public Boolean getLatePenaltyCl() {
        return latePenaltyCl;
    }

    public void setLatePenaltyCl(Boolean latePenaltyCl) {
        this.latePenaltyCl = latePenaltyCl;
    }

    public Boolean getLatePenaltySalary() {
        return latePenaltySalary;
    }

    public void setLatePenaltySalary(Boolean latePenaltySalary) {
        this.latePenaltySalary = latePenaltySalary;
    }

    public Boolean getHalfdaycarry() {
        return halfdaycarry;
    }

    public void setHalfdaycarry(Boolean halfdaycarry) {
        this.halfdaycarry = halfdaycarry;
    }

    public Boolean getClcarry() {
        return clcarry;
    }

    public void setClcarry(Boolean clcarry) {
        this.clcarry = clcarry;
    }

    public Boolean getElcarry() {
        return elcarry;
    }

    public void setElcarry(Boolean elcarry) {
        this.elcarry = elcarry;
    }

    public Boolean getSlcarry() {
        return slcarry;
    }

    public void setSlcarry(Boolean slcarry) {
        this.slcarry = slcarry;
    }
}


















