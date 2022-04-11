package com.fyp.evhelper.reminder;

// vehLicNo    --> vehicle Licence Number
// vehLicDate  --> vehicle licence valid date
// vehClass    --> vehicle class
public class VehLicData {
    String vehLicNo, vehLicDate, vehClass;

    public VehLicData() {
    }

    public VehLicData(String vehLicNo, String vehLicDate, String vehClass) {
        this.vehLicNo = vehLicNo;
        this.vehLicDate = vehLicDate;
        this.vehClass = vehClass;
    }

    public String getVehLicNo() {
        return vehLicNo;
    }

    public void setVehLicNo(String vehLicNo) {
        this.vehLicNo = vehLicNo;
    }

    public String getVehLicDate() {
        return vehLicDate;
    }

    public void setVehLicDate(String vehLicDate) {
        this.vehLicDate = vehLicDate;
    }

    public String getVehClass() {
        return vehClass;
    }

    public void setVehClass(String vehClass) {
        this.vehClass = vehClass;
    }

}
