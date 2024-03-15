package com.vistara.traveler.ServerFile;

/**
 * Created by Sharad on 22-06-2017.
 */

public class UrlConfig {


    // common api

    //public static String ipaddress= "https://www.tsaldxuat-1484241539.ap-southeast-1.elb.airvistara.com/vtravel/traveller/";
   // public static String hostVerifyName= "tsaldxuat-1484241539.ap-southeast-1.elb.airvistara.com";

// public static String ipaddress= "https://custolife.com/traveller/";
// public static String hostVerifyName= "custolife.com";

  // public static String ipaddress= "https://vtravel.airvistara.com/";//Production URL


    public static String ipaddress= "http://10.29.3.228/"; //UAT URL

    public static String hostVerifyName= "vtravel.airvistara.com";

    public static String otpValidation=ipaddress+"webapi/do_mob_validation.php";
    public static String signUp=ipaddress+"webapi/signup.php";
    public static String appFeedback=ipaddress+"webapi/app_feedback.php";

    // driver api
    public static String driverAppData=ipaddress+"webapi/d_getmyappdata.php";
    public static String updateCarParams=ipaddress+"webapi/d_updatecarparams.php";
    public static String tripCompleted=ipaddress+"webapi/d_trip_completed.php";
    public static String d_SOS=ipaddress+"webapi/d_sos.php";
    //public static String updateTravellerStatus=ipaddress+"traveller/webapi/d_update_traveller_status.php";
    public static String updateTravellerStatus=ipaddress+"webapi/d_traveller_status_update.php";

    // traveller api
    public static String travellerAppData=ipaddress+"webapi/t_getmyappdata.php";
    public static String t_SOS=ipaddress+"webapi/t_sos.php";
    public static String getCarUpdate=ipaddress+"webapi/t_getcarupdate.php";
    public static String userFeedback=ipaddress+"webapi/t_feedback.php";
    public static String t_reached_getready=ipaddress+"webapi/t_reached_getready.php";

  public static String Past_Trips_API = ipaddress+"webapi/GetTripHistory.php";


}
