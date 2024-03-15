package com.vistara.traveler.model;

/**
 * Created by Sharad on 15-07-2017.
 */

public class DTravellerStatus {
    public String travellerId;
    public String travellerStatus;

    public DTravellerStatus(String travellerId,String travellerStatus)
    {
        this.travellerId=travellerId;
        this.travellerStatus=travellerStatus;
    }

    public String getTravellerId() {
        return travellerId;
    }

    public String getTravellerStatus() {
        return travellerStatus;
    }

    public void setTravellerStatus(String travellerStatus) {
        this.travellerStatus = travellerStatus;
    }
}
