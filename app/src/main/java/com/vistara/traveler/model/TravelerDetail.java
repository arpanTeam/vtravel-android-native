package com.vistara.traveler.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sharad on 06-07-2017.
 */

public class TravelerDetail {

    public String traveltype;
    public String  pickuptime;
    public String pickupaddress;
    public String  pickuplat;
    public String  pickuplon;
    public String  driver;
    public String car;
    public String  carlat;
    public String carlon;
    public String tripid;
    public String targettime;
    public String  destination;
    public String destlat;
    public String  destlon;
    public String otp;
    public String carid;
    public String duration;

    public String sosnumber;
    public String askupdateevery;
    public List<TravelerDetail> tripList = new ArrayList<>();

    public String empId;
    public String empName;
    public String empMobileNo;
    public String empAddress;
    public String travellerPickUpStatus;

    public List<TravelerDetail> travellerFellowList = new ArrayList<>();


    public TravelerDetail(String sosnumber,String askupdateevery, List<TravelerDetail> tripList,List<TravelerDetail> travellerFellowList) {

        this.sosnumber              = sosnumber;
        this.askupdateevery         = askupdateevery;
        this.travellerFellowList    = travellerFellowList ;

        for(int i=0; i<tripList.size(); i++)
        {
            this.tripList.add(tripList.get(i));
        }

    }

    public  TravelerDetail(String empId,String empName,String empMobileNo,String empAddress,String travellerPickUpStatus )
    {
        this.empId       = empId ;
        this.empName     = empName ;
        this.empMobileNo = empMobileNo ;
        this.empAddress  = empAddress ;
        this.travellerPickUpStatus = travellerPickUpStatus ;
    }




    public TravelerDetail(String traveltype,String  pickuptime,String pickupaddress,String  pickuplat,String  pickuplon,
                          String  driver, String car,String  carlat,String carlon,String tripid,String targettime,
                          String  destination,String destlat,String  destlon,String otp, String carid) {
        this.traveltype=traveltype;
        this.pickuptime=pickuptime;
        this.pickupaddress=pickupaddress;
        this.pickuplat=pickuplat;
        this.pickuplon=pickuplon;
        this.driver=driver;
        this.car=car;
        this.carlat=carlat;
        this.carlon=carlon;
        this.tripid=tripid;
        this.targettime=targettime;
        this.destination=destination;
        this.destlat=destlat;
        this.destlon=destlon;
        this.otp=otp;
        this.carid=carid;
    }

}
