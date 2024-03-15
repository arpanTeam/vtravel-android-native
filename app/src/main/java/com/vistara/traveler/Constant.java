package com.vistara.traveler;

import com.vistara.traveler.receivers.deliverReceiver;
import com.vistara.traveler.receivers.sentReceiver;

/**
 * Created by Sharad on 22-06-2017.
 */

public class Constant {
    public static final String varify_mobile = "verify_mobile";
    public static final String userId = "user_id";
    public static final String userNmae = "user_name";
    public static final String officeId = "office_id";
    public static final String userType = "user_type";            // 1 for traveller, 2 for driver
    public static final String tripType = "trip_type";            // 2 for pick up and 1 for drop     // currentlry using only for driver not traveller
    public static final String userData = "user_data";
    public static final String lat = "lat";
    public static final String lon = "lon";
    public static final String gcm_id = "gcm_id";
 //   public static final String pickUpListObj = "pick_up_list";                 // for showing currnt pick uplayout in driver app
    public static final String lastTimeForDriverApiCall = "last_time_for_driverApiCall";
 //   public static final String tripComplete = "trip_complete";
    public static final String lastTripData = "last_trip_data";          // user last trip data for drivers
    public static final String firstCarId = "car_id";
    public static final String firstTripId = "trip_id";
    public static final String pickedTravObj = "pick_up_list";

    public static sentReceiver sentReceiver1;
    public static deliverReceiver deliverReceiver1;

    public static final String dTravellerStatus = "t_status";                  // traveler status who are arrived for driver
  //  public static final String treachSafe = "t_safe_reach";                     // safe reach for traveller
    public static final String tReadyBoard = "t_ready_board";               // ready to board for traveller
    public static final String taddress = "t_address";                      // address for traveller
    public static final String tEmpOtp = "t_emp_otp";                       //emp Otp for employee
    public static final String callDeskNum = "call_desk_num";

    public static final String FEEDBACK_STATUS = "feedback_status";
    public static final String DUPLICATE_TRIP_ID = "duplicate_trip_id";


}
