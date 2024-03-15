package com.vistara.traveler.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sharad on 27-06-2017.
 */

public class DDriverDetail {
    public String msg_before_trip;
    public String sendupdateevery;
    public String speedlimit;
    public String sosnumber;
    public String location_update_driver_app;


    public String carId;
    public String traveltype;
    public String car;
    public String vendorname;
    public String driverName;
    public String tripid;
    public String targettime;
    public String destination;
    public String destlat;
    public String destlon;


    public String id;
    public String name;
    public String address;
    public String lat;
    public String lon;
    public String pickuptime;
    public String loggedin;
    public String empcode;
    public String otp;
    public String mobileNo;


    public List<DDriverDetail> traverDeatil = new ArrayList<>();
    public List<DDriverDetail> tripDetail = new ArrayList<>();

    public DDriverDetail(String sendupdateevery, String msg_before_trip, String sosnumber, String speedlimit,
                         String location_update_driver_app, List<DDriverDetail> tripDetail) {
        this.sendupdateevery=sendupdateevery;
        this.msg_before_trip=msg_before_trip;
        this.sosnumber=sosnumber;
        this.speedlimit=speedlimit;
        this.location_update_driver_app=location_update_driver_app;

        for(int i=0; i<tripDetail.size(); i++)
        {
            this.tripDetail.add(tripDetail.get(i));
        }
    }

    public DDriverDetail(String carId, String traveltype, String car, String vendorname, String tripid, String targettime,
                         String destination, String destlat, String destlon, String driverName, List<DDriverDetail> traverDeatil) {
        this.tripid=tripid;
        this.carId=carId;
        this.traveltype=traveltype;
        this.vendorname=vendorname;
        this.car=car;
        this.targettime=targettime;
        this.destination=destination;
        this.destlat=destlat;
        this.destlon=destlon;
        this.driverName=driverName;

        for(int i=0; i<traverDeatil.size(); i++)
        {
            this.traverDeatil.add(traverDeatil.get(i));
        }
    }


    public DDriverDetail(String id, String name, String address, String lat,String lon,String pickuptime,
                         String loggedin,String empcode, String otp,String mobileNo) {
        this.id=id;
        this.name=name;
        this.address=address;
        this.lat=lat;
        this.lon=lon;
        this.pickuptime=pickuptime;
        this.loggedin=loggedin;
        this.empcode=empcode;
        this.empcode=empcode;
        this.otp=otp;
        this.mobileNo  =mobileNo ;
    }
}
