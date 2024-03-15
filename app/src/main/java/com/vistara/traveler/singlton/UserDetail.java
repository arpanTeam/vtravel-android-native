package com.vistara.traveler.singlton;

/**
 * Created by Sharad on 29-06-2017.
 */

public class UserDetail {

    double lat;
    double lon;
    String userId;
    String mobileNumber;
    String userType;                           // 1 for driver and 2 for treveller
    String userData;                            // traveller or driver data
    String sendupdateevery;                     // call driver update api
    String speedlimit;
    String userName;
    String gpsStaus="1";
    String batteryStatus="1";

    private UserDetail()
    {}

//    String lastTrip;
//
//    public String getLastTrip() {
//        return lastTrip;
//    }
//
//    public void setLastTrip(String lastTrip) {
//        this.lastTrip = lastTrip;
//    }


    public void setGpsStaus(String gpsStaus) {
        this.gpsStaus = gpsStaus;
    }

    public void setBatteryStatus(String batteryStatus) {
        this.batteryStatus = batteryStatus;
    }


    public String getGpsStaus() {
        return gpsStaus;
    }

    public String getBatteryStatus() {
        return batteryStatus;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    private static UserDetail latLon = new UserDetail();


    public static UserDetail getInstance()
    {
        return  latLon;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public double getLat() {
        return lat;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }
    public String getUserData() {
        return userData;
    }

    public void setUserData(String userData) {
        this.userData = userData;
    }

    public String getSendupdateevery() {
        return sendupdateevery;
    }

    public void setSendupdateevery(String sendupdateevery) {
        this.sendupdateevery = sendupdateevery;
    }

    public String getSpeedlimit() {
        return speedlimit;
    }

    public void setSpeedlimit(String speedlimit) {
        this.speedlimit = speedlimit;
    }
}
