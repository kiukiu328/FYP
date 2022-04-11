package com.fyp.evhelper.reminder;

// driLicNo     --> Driving Licence Number
// driName      --> Driver Name
// driLicDate   --> Driving Licence Valid Date

public class DriLicData {
    String driLicNo, driName, driLicDate;

    public DriLicData() {
    }

    public DriLicData(String driLicNo, String driName, String driLicDate) {
        this.driLicNo = driLicNo;
        this.driName = driName;
        this.driLicDate = driLicDate;
    }

    public String getDriLicNo() {
        return driLicNo;
    }

    public void setDriLicNo(String driLicNo) {
        this.driLicNo = driLicNo;
    }

    public String getDriName() {
        return driName;
    }

    public void setDriName(String driName) {
        this.driName = driName;
    }

    public String getDriLicDate() {
        return driLicDate;
    }

    public void setDriLicDate(String driLicDate) {
        this.driLicDate = driLicDate;
    }
}
