package com.vistara.traveler;

import android.content.Context;

import com.vistara.traveler.model.DDriverDetail;
import com.vistara.traveler.model.TravelerDetail;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sharad on 27-06-2017.
 */

public class MainApp {
    public List<DDriverDetail> driverDetail;
    public List<TravelerDetail> travelerDetail;
    public String travelTime;

    public MainApp(Context context) {
        driverDetail    = new ArrayList<>();
        travelerDetail  = new ArrayList<>();
        travelTime      = "";
    }
}
