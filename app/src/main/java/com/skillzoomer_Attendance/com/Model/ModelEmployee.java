package com.skillzoomer_Attendance.com.Model;

public class ModelEmployee {
    String userId,name,dept,userType,mobile,doj,dob,email,workEmail,salary,registrationStatus,hrUid,tlUid,siteName,companyName,industryName,
            dateOfRegister,timestamp,designation,lastActivity,uid,profile,inTimeStamp,inTime,outTime,inDate,gender,totalWorkingHours
            ,breakStartTime,breakStartTimestamp,breakEndTime,breakendTimestamp,totalBreakhours,aadhar;
    Boolean permanentEmployee,probationEmployee,singleAttendance,multiAttendance,allAttendance,Sunday,Monday,Tuesday,Wednesday,Thursday,Friday,Saturday,
            punchIn,punchOut,breakCond;
    long designationPosition,deptPosition,siteId,industryPosition,latitude,
            grade ,longitude,radius,inLatitude,workingHours,inLongitude,outLatitude,outLongitude,breakHours;


    public ModelEmployee() {

    }

    public ModelEmployee(String userId , String name , String uid ) {
        this.userId = userId;
        this.name = name;
        this.uid = uid;
    }

    public ModelEmployee(String name, String designation, long grade,String userId ,String uid) {
        this.name = name;
        this.designation = designation;
        this.grade = grade;
        this.userId = userId;
        this.uid = uid;
    }

    public ModelEmployee(String userId,
                         String name,
                         String dept,
                         String userType,
                         String mobile,
                         String doj,
                         String email,
                         String workEmail,
                         String salary,
                         String registrationStatus,
                         String hrUid,
                         String siteName,
                         String companyName,
                         String industryName,
                         String dateOfRegister,
                         String timestamp,
                         String designation,
                         String lastActivity,
                         Boolean permanentEmployee,
                         Boolean probationEmployee,
                         Boolean singleAttendance,
                         Boolean multiAttendance,
                         Boolean allAttendance,
                         Boolean sunday,
                         Boolean monday,
                         Boolean tuesday,
                         Boolean wednesday,
                         Boolean thursday,
                         Boolean friday,
                         Boolean saturday,
                         long designationPosition,
                         long deptPosition,
                         long siteId,
                         long industryPosition,
                         long latitude,
                         long longitude,
                         long radius) {
        this.userId = userId;
        this.name = name;
        this.dept = dept;
        this.userType = userType;
        this.mobile = mobile;
        this.doj = doj;
        this.email = email;
        this.workEmail = workEmail;
        this.salary = salary;
        this.registrationStatus = registrationStatus;
        this.hrUid = hrUid;
        this.siteName = siteName;
        this.companyName = companyName;
        this.industryName = industryName;
        this.dateOfRegister = dateOfRegister;
        this.timestamp = timestamp;
        this.designation = designation;
        this.lastActivity = lastActivity;
        this.permanentEmployee = permanentEmployee;
        this.probationEmployee = probationEmployee;
        this.singleAttendance = singleAttendance;
        this.multiAttendance = multiAttendance;
        this.allAttendance = allAttendance;
        Sunday = sunday;
        Monday = monday;
        Tuesday = tuesday;
        Wednesday = wednesday;
        Thursday = thursday;
        Friday = friday;
        Saturday = saturday;
        this.designationPosition = designationPosition;
        this.deptPosition = deptPosition;
        this.siteId = siteId;
        this.industryPosition = industryPosition;
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;
    }

    public ModelEmployee(String userId ,
                         String name ,
                         String dept ,
                         String userType ,
                         String mobile ,
                         String doj ,
                         String dob ,
                         String email ,
                         String workEmail ,
                         String salary ,
                         String registrationStatus ,
                         String hrUid ,
                         String siteName ,
                         String companyName ,
                         String industryName ,
                         String dateOfRegister ,
                         String timestamp ,
                         String designation ,
                         String lastActivity ,
                         String uid ,
                         String profile ,
                         String inTimeStamp ,
                         String inTime ,
                         String outTime ,
                         String inDate ,
                         String gender ,
                         String totalWorkingHours ,
                         String breakStartTime ,
                         String breakStartTimestamp ,
                         String breakEndTime ,
                         String breakendTimestamp ,
                         String totalBreakhours ,
                         String aadhar ,
                         Boolean permanentEmployee ,
                         Boolean probationEmployee ,
                         Boolean singleAttendance ,
                         Boolean multiAttendance ,
                         Boolean allAttendance ,
                         Boolean sunday ,
                         Boolean monday ,
                         Boolean tuesday ,
                         Boolean wednesday ,
                         Boolean thursday ,
                         Boolean friday ,
                         Boolean saturday ,
                         Boolean punchIn ,
                         Boolean punchOut ,
                         Boolean breakCond ,
                         long designationPosition ,
                         long deptPosition ,
                         long siteId ,
                         long industryPosition ,
                         long latitude ,
                         long grade ,
                         long longitude ,
                         long radius ,
                         long inLatitude ,
                         long workingHours ,
                         long inLongitude ,
                         long outLatitude ,
                         long outLongitude ,
                         long breakHours) {
        this.userId = userId;
        this.name = name;
        this.dept = dept;
        this.userType = userType;
        this.mobile = mobile;
        this.doj = doj;
        this.dob = dob;
        this.email = email;
        this.workEmail = workEmail;
        this.salary = salary;
        this.registrationStatus = registrationStatus;
        this.hrUid = hrUid;
        this.siteName = siteName;
        this.companyName = companyName;
        this.industryName = industryName;
        this.dateOfRegister = dateOfRegister;
        this.timestamp = timestamp;
        this.designation = designation;
        this.lastActivity = lastActivity;
        this.uid = uid;
        this.profile = profile;
        this.inTimeStamp = inTimeStamp;
        this.inTime = inTime;
        this.outTime = outTime;
        this.inDate = inDate;
        this.gender = gender;
        this.totalWorkingHours = totalWorkingHours;
        this.breakStartTime = breakStartTime;
        this.breakStartTimestamp = breakStartTimestamp;
        this.breakEndTime = breakEndTime;
        this.breakendTimestamp = breakendTimestamp;
        this.totalBreakhours = totalBreakhours;
        this.aadhar = aadhar;
        this.permanentEmployee = permanentEmployee;
        this.probationEmployee = probationEmployee;
        this.singleAttendance = singleAttendance;
        this.multiAttendance = multiAttendance;
        this.allAttendance = allAttendance;
        Sunday = sunday;
        Monday = monday;
        Tuesday = tuesday;
        Wednesday = wednesday;
        Thursday = thursday;
        Friday = friday;
        Saturday = saturday;
        this.punchIn = punchIn;
        this.punchOut = punchOut;
        this.breakCond = breakCond;
        this.designationPosition = designationPosition;
        this.deptPosition = deptPosition;
        this.siteId = siteId;
        this.industryPosition = industryPosition;
        this.latitude = latitude;
        this.grade = grade;
        this.longitude = longitude;
        this.radius = radius;
        this.inLatitude = inLatitude;
        this.workingHours = workingHours;
        this.inLongitude = inLongitude;
        this.outLatitude = outLatitude;
        this.outLongitude = outLongitude;
        this.breakHours = breakHours;
    }

    public ModelEmployee(String userId ,
                         String name ,
                         String dept ,
                         String userType ,
                         String mobile ,
                         String doj ,
                         String dob ,
                         String email ,
                         String workEmail ,
                         String salary ,
                         String registrationStatus ,
                         String hrUid ,
                         String tlUid ,
                         String siteName ,
                         String companyName ,
                         String industryName ,
                         String dateOfRegister ,
                         String timestamp ,
                         String designation ,
                         String lastActivity ,
                         String uid ,
                         String profile ,
                         String inTimeStamp ,
                         String inTime ,
                         String outTime ,
                         String inDate ,
                         String gender ,
                         String totalWorkingHours ,
                         String breakStartTime ,
                         String breakStartTimestamp ,
                         String breakEndTime ,
                         String breakendTimestamp ,
                         String totalBreakhours ,
                         String aadhar ,
                         Boolean permanentEmployee ,
                         Boolean probationEmployee ,
                         Boolean singleAttendance ,
                         Boolean multiAttendance ,
                         Boolean allAttendance ,
                         Boolean sunday ,
                         Boolean monday ,
                         Boolean tuesday ,
                         Boolean wednesday ,
                         Boolean thursday ,
                         Boolean friday ,
                         Boolean saturday ,
                         Boolean punchIn ,
                         Boolean punchOut ,
                         Boolean breakCond ,
                         long designationPosition ,
                         long deptPosition ,
                         long siteId ,
                         long industryPosition ,
                         long latitude ,
                         long grade ,
                         long longitude ,
                         long radius ,
                         long inLatitude ,
                         long workingHours ,
                         long inLongitude ,
                         long outLatitude ,
                         long outLongitude ,
                         long breakHours) {
        this.userId = userId;
        this.name = name;
        this.dept = dept;
        this.userType = userType;
        this.mobile = mobile;
        this.doj = doj;
        this.dob = dob;
        this.email = email;
        this.workEmail = workEmail;
        this.salary = salary;
        this.registrationStatus = registrationStatus;
        this.hrUid = hrUid;
        this.tlUid = tlUid;
        this.siteName = siteName;
        this.companyName = companyName;
        this.industryName = industryName;
        this.dateOfRegister = dateOfRegister;
        this.timestamp = timestamp;
        this.designation = designation;
        this.lastActivity = lastActivity;
        this.uid = uid;
        this.profile = profile;
        this.inTimeStamp = inTimeStamp;
        this.inTime = inTime;
        this.outTime = outTime;
        this.inDate = inDate;
        this.gender = gender;
        this.totalWorkingHours = totalWorkingHours;
        this.breakStartTime = breakStartTime;
        this.breakStartTimestamp = breakStartTimestamp;
        this.breakEndTime = breakEndTime;
        this.breakendTimestamp = breakendTimestamp;
        this.totalBreakhours = totalBreakhours;
        this.aadhar = aadhar;
        this.permanentEmployee = permanentEmployee;
        this.probationEmployee = probationEmployee;
        this.singleAttendance = singleAttendance;
        this.multiAttendance = multiAttendance;
        this.allAttendance = allAttendance;
        Sunday = sunday;
        Monday = monday;
        Tuesday = tuesday;
        Wednesday = wednesday;
        Thursday = thursday;
        Friday = friday;
        Saturday = saturday;
        this.punchIn = punchIn;
        this.punchOut = punchOut;
        this.breakCond = breakCond;
        this.designationPosition = designationPosition;
        this.deptPosition = deptPosition;
        this.siteId = siteId;
        this.industryPosition = industryPosition;
        this.latitude = latitude;
        this.grade = grade;
        this.longitude = longitude;
        this.radius = radius;
        this.inLatitude = inLatitude;
        this.workingHours = workingHours;
        this.inLongitude = inLongitude;
        this.outLatitude = outLatitude;
        this.outLongitude = outLongitude;
        this.breakHours = breakHours;
    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDept() {
        return dept;
    }

    public void setDept(String dept) {
        this.dept = dept;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getDoj() {
        return doj;
    }

    public void setDoj(String doj) {
        this.doj = doj;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWorkEmail() {
        return workEmail;
    }

    public void setWorkEmail(String workEmail) {
        this.workEmail = workEmail;
    }

    public String getSalary() {
        return salary;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }

    public String getRegistrationStatus() {
        return registrationStatus;
    }

    public void setRegistrationStatus(String registrationStatus) {
        this.registrationStatus = registrationStatus;
    }

    public String getHrUid() {
        return hrUid;
    }

    public void setHrUid(String hrUid) {
        this.hrUid = hrUid;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getIndustryName() {
        return industryName;
    }

    public void setIndustryName(String industryName) {
        this.industryName = industryName;
    }

    public String getDateOfRegister() {
        return dateOfRegister;
    }

    public void setDateOfRegister(String dateOfRegister) {
        this.dateOfRegister = dateOfRegister;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public Boolean getPermanentEmployee() {
        return permanentEmployee;
    }

    public void setPermanentEmployee(Boolean permanentEmployee) {
        this.permanentEmployee = permanentEmployee;
    }

    public Boolean getProbationEmployee() {
        return probationEmployee;
    }

    public void setProbationEmployee(Boolean probationEmployee) {
        this.probationEmployee = probationEmployee;
    }

    public Boolean getSingleAttendance() {
        return singleAttendance;
    }

    public void setSingleAttendance(Boolean singleAttendance) {
        this.singleAttendance = singleAttendance;
    }

    public Boolean getMultiAttendance() {
        return multiAttendance;
    }

    public void setMultiAttendance(Boolean multiAttendance) {
        this.multiAttendance = multiAttendance;
    }

    public Boolean getAllAttendance() {
        return allAttendance;
    }

    public void setAllAttendance(Boolean allAttendance) {
        this.allAttendance = allAttendance;
    }

    public Boolean getSunday() {
        return Sunday;
    }

    public void setSunday(Boolean sunday) {
        Sunday = sunday;
    }

    public Boolean getMonday() {
        return Monday;
    }

    public void setMonday(Boolean monday) {
        Monday = monday;
    }

    public Boolean getTuesday() {
        return Tuesday;
    }

    public void setTuesday(Boolean tuesday) {
        Tuesday = tuesday;
    }

    public Boolean getWednesday() {
        return Wednesday;
    }

    public void setWednesday(Boolean wednesday) {
        Wednesday = wednesday;
    }

    public Boolean getThursday() {
        return Thursday;
    }

    public void setThursday(Boolean thursday) {
        Thursday = thursday;
    }

    public Boolean getFriday() {
        return Friday;
    }

    public void setFriday(Boolean friday) {
        Friday = friday;
    }

    public Boolean getSaturday() {
        return Saturday;
    }

    public void setSaturday(Boolean saturday) {
        Saturday = saturday;
    }

    public long getDesignationPosition() {
        return designationPosition;
    }

    public void setDesignationPosition(long designationPosition) {
        this.designationPosition = designationPosition;
    }

    public long getDeptPosition() {
        return deptPosition;
    }

    public void setDeptPosition(long deptPosition) {
        this.deptPosition = deptPosition;
    }

    public long getSiteId() {
        return siteId;
    }

    public void setSiteId(long siteId) {
        this.siteId = siteId;
    }

    public long getIndustryPosition() {
        return industryPosition;
    }

    public void setIndustryPosition(long industryPosition) {
        this.industryPosition = industryPosition;
    }

    public long getLatitude() {
        return latitude;
    }

    public void setLatitude(long latitude) {
        this.latitude = latitude;
    }

    public long getLongitude() {
        return longitude;
    }

    public void setLongitude(long longitude) {
        this.longitude = longitude;
    }

    public long getRadius() {
        return radius;
    }

    public void setRadius(long radius) {
        this.radius = radius;
    }

    public String getLastActivity() {
        return lastActivity;
    }

    public void setLastActivity(String lastActivity) {
        this.lastActivity = lastActivity;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getInTimeStamp() {
        return inTimeStamp;
    }

    public void setInTimeStamp(String inTimeStamp) {
        this.inTimeStamp = inTimeStamp;
    }

    public String getInTime() {
        return inTime;
    }

    public void setInTime(String inTime) {
        this.inTime = inTime;
    }

    public String getOutTime() {
        return outTime;
    }

    public void setOutTime(String outTime) {
        this.outTime = outTime;
    }

    public String getInDate() {
        return inDate;
    }

    public void setInDate(String inDate) {
        this.inDate = inDate;
    }



    public Boolean getPunchIn() {
        return punchIn;
    }

    public void setPunchIn(Boolean punchIn) {
        this.punchIn = punchIn;
    }

    public Boolean getPunchOut() {
        return punchOut;
    }

    public void setPunchOut(Boolean punchOut) {
        this.punchOut = punchOut;
    }

    public long getInLatitude() {
        return inLatitude;
    }

    public void setInLatitude(long inLatitude) {
        this.inLatitude = inLatitude;
    }

    public long getInLongitude() {
        return inLongitude;
    }

    public void setInLongitude(long inLongitude) {
        this.inLongitude = inLongitude;
    }

    public long getOutLatitude() {
        return outLatitude;
    }

    public void setOutLatitude(long outLatitude) {
        this.outLatitude = outLatitude;
    }

    public long getOutLongitude() {
        return outLongitude;
    }

    public void setOutLongitude(long outLongitude) {
        this.outLongitude = outLongitude;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public long getWorkingHours() {
        return workingHours;
    }

    public void setWorkingHours(long workingHours) {
        this.workingHours = workingHours;
    }

    public String getTotalWorkingHours() {
        return totalWorkingHours;
    }

    public void setTotalWorkingHours(String totalWorkingHours) {
        this.totalWorkingHours = totalWorkingHours;
    }

    public String getBreakStartTime() {
        return breakStartTime;
    }

    public void setBreakStartTime(String breakStartTime) {
        this.breakStartTime = breakStartTime;
    }

    public String getBreakStartTimestamp() {
        return breakStartTimestamp;
    }

    public void setBreakStartTimestamp(String breakStartTimestamp) {
        this.breakStartTimestamp = breakStartTimestamp;
    }

    public String getBreakEndTime() {
        return breakEndTime;
    }

    public void setBreakEndTime(String breakEndTime) {
        this.breakEndTime = breakEndTime;
    }

    public String getBreakendTimestamp() {
        return breakendTimestamp;
    }

    public void setBreakendTimestamp(String breakendTimestamp) {
        this.breakendTimestamp = breakendTimestamp;
    }

    public String getTotalBreakhours() {
        return totalBreakhours;
    }

    public void setTotalBreakhours(String totalBreakhours) {
        this.totalBreakhours = totalBreakhours;
    }

    public Boolean getBreakCond() {
        return breakCond;
    }

    public void setBreakCond(Boolean breakCond) {
        this.breakCond = breakCond;
    }

    public long getBreakHours() {
        return breakHours;
    }

    public void setBreakHours(long breakHours) {
        this.breakHours = breakHours;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getAadhar() {
        return aadhar;
    }

    public void setAadhar(String aadhar) {
        this.aadhar = aadhar;
    }

    public long getGrade() {
        return grade;
    }

    public void setGrade(long grade) {
        this.grade = grade;
    }

    public String getTlUid() {
        return tlUid;
    }

    public void setTlUid(String tlUid) {
        this.tlUid = tlUid;
    }
}
