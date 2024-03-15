package com.vistara.traveler;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.vistara.traveler.LocationService.DataParser;
import com.vistara.traveler.LocationService.GPSTracker;
import com.vistara.traveler.LocationService.GpsUtils;
import com.vistara.traveler.ServerFile.ServerInterface;
import com.vistara.traveler.ServerFile.UrlConfig;
import com.vistara.traveler.adapter.ViewPagerAdapter;
import com.vistara.traveler.internal.TinyDB;
import com.vistara.traveler.model.TravelerDetail;
import com.vistara.traveler.singlton.UserDetail;
import com.vistara.traveler.utils.ApiResponseParser;
import com.vistara.traveler.utils.CompleteTrip;
import com.vistara.traveler.utils.CustomPopup;
import com.vistara.traveler.utils.TelephonyManagerInfo;
import com.vistara.traveler.utils.VersionChecker;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Sharad on 21-06-2017.
 */

public class TravelerHomePage extends DrawerBaseActvity implements ApiResponseParser , CompleteTrip,
        NavigationView.OnNavigationItemSelectedListener{


    public static PolylineOptions lineOptions = null;
    public static Boolean routeDraw = false;


    public static Activity activity;
    int apiSataus = 1;    // 1 for traveller data, 2 for  sos, 4 for car update, 5 for user feedback;
    public static String sosNum;
    public static String tripId = "";
    public static String carId = "";
    public static Context context;
    public static int isFirstEntry = 0;
    public static int updateTime = 1;
    public static int firstTimeInOtpValidated = 0;

    static  GoogleApiClient mGoogleApiClient;
    static  Location mLastLocation;
    static  Marker mCurrLocationMarker, cabMarker, travelerMarker, destinationMarker;
    static  LocationRequest mLocationRequest;


    FrameLayout frameLayout;
    TextView driverName, pickUp, gaadiNum, dateTv,  timeTv,otpTv, textView;
    public static RelativeLayout topRl,feedbackRl, callDriverRl;
    TextView address;
    public static  String tripFound = "";
    TextView userName,mobileNum,versionNumber, travellerName, empCode, travallerAddress;
    TextView travellersCountId;
    public static RelativeLayout travellerView;
    ImageView sosIcon;
    public static ArrayList<Marker> markerList=new ArrayList();
    public static ArrayList<TravelerDetail> arrayListTraveller;
    public static TravelerDetail travelTime;
    int checkMrkerAdded = 0;Marker finalMarker;
    Bitmap bitmap = null;

//    public String pickUpLat, pickUpLon, dropLat, dropLon;

    double latSos = 0.0;
    double lonSos = 0.0;
    TinyDB tDb;
    String latestVersion = "";
    String userAppVersion = "";
    static CompleteTrip cTripObj;
    RelativeLayout callDeskRl;
    Handler versionhandler;// = new Handler();

    static int safeReach = 0;
    static int isTripFound = 0;
    static int noOfFellowTravelers = 0;
    static RelativeLayout readyToBoard,  sosRl, travelersRl;
    static RelativeLayout readyToBoardRl;

    LatLng pickUpLatLng ;
    LatLng dropLatLng ;
    LatLng carLatLng;
    LatLng lastCarLatLng;

    static String otpValidated = "0";

    String driver_mobile;

    boolean isGPS = false;

    ViewPagerAdapter viewPagerAdapter;

    String callDeskNum ;

    private final Lock lockBlock = new ReentrantLock();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = TravelerHomePage.this;
        activity = TravelerHomePage.this;
        tDb = new TinyDB(TravelerHomePage.this);
        cTripObj = TravelerHomePage.this;
        versionhandler = new Handler();

        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.home_fragment); //Remember this is the FrameLayout area within your activity_main.xml
        getLayoutInflater().inflate(R.layout.home_page, contentFrameLayout);
        getSupportActionBar().setTitle("Home");




        ImageView iv = (ImageView) toolbar.findViewById(R.id.notifiClik);
        ImageView imageViewRefreshId = (ImageView) toolbar.findViewById(R.id.imageViewRefreshId);
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TravelerHomePage.this, NotificationActivity.class);
                startActivity(intent);
            }
        });

        imageViewRefreshId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                try
                {
                    TravelerHomePage.sendMessageToActivity(1);
//                    getTravellerDatas();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

            }
        });


        try {
            GPSTracker gps = new GPSTracker(TravelerHomePage.this);
            latSos = gps.getLatitude();
            lonSos = gps.getLongitude();
            Log.e("","");
        }catch(Exception e) {
            latSos = 0.0;
            lonSos = 0.0;
            e.printStackTrace();
        }

        driverName          = (TextView)  findViewById(R.id.driverName);
        pickUp              = (TextView)  findViewById(R.id.pickUp);
        gaadiNum            = (TextView)  findViewById(R.id.gaadiNum);
        timeTv              = (TextView)  findViewById(R.id.timeTv);
        dateTv              = (TextView)  findViewById(R.id.dateTv);
        otpTv               = (TextView)  findViewById(R.id.otpTv);
        topRl               = (RelativeLayout)  findViewById(R.id.topRl);
        feedbackRl          = (RelativeLayout)  findViewById(R.id.feedbackRl);
        address             = (TextView) findViewById(R.id.address);
        sosIcon             = (ImageView)  findViewById(R.id.sosIcon);
        travellerView       = (RelativeLayout) findViewById(R.id.travellerView);
        travallerAddress    = (TextView) findViewById(R.id.travallerAddress);
        travellerName       = (TextView) findViewById(R.id.travellerName);
        empCode             = (TextView) findViewById(R.id.empCode);
        callDeskRl          = (RelativeLayout)  findViewById(R.id.callDeskRl);
        callDriverRl        = (RelativeLayout)  findViewById(R.id.callToDriverRl);

        travellersCountId   = (TextView)  findViewById(R.id.travellersCountId);

//         safeReachRl        = (RelativeLayout) findViewById(R.id.safeReach);
         readyToBoard       = (RelativeLayout) findViewById(R.id.readyToBoard);
         readyToBoardRl     = (RelativeLayout) findViewById(R.id.readyToBoardRl);
         sosRl              = (RelativeLayout) findViewById(R.id.sosRl);
        travelersRl         = (RelativeLayout) findViewById(R.id.travelersRl);

        textView            = (TextView) findViewById(R.id.text_view);
        frameLayout         = (FrameLayout) findViewById(R.id.framelayout);



                feedbackRl.setEnabled(false);
        ViewPager viewPager     = (ViewPager) findViewById(R.id.viewPager);
        viewPagerAdapter        = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);
        TabLayout  tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);


        NavigationView navigationView = (NavigationView) findViewById(R.id.nvView);
        navigationView.setNavigationItemSelectedListener(TravelerHomePage.this);
        View header = navigationView.getHeaderView(0);
        userName        = (TextView) header.findViewById(R.id.userName);
        mobileNum       = (TextView) header.findViewById(R.id.mobileNum);
        versionNumber   = (TextView) header.findViewById(R.id.versionNumber);

        String name = AppController.tDb.getString(Constant.userNmae);
        String id   = UserDetail.getInstance().getUserId();
        userName.setText(name+" ("+ id + ")");
        travellerName.setText(name);

        try
        {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            versionNumber.setText(version);
        }
        catch (PackageManager.NameNotFoundException e)
        {
            e.printStackTrace();
        }


        mobileNum.setText(AppController.tDb.getString(Constant.varify_mobile));
        empCode.setText(AppController.tDb.getString(Constant.tEmpOtp));
        travallerAddress.setText(AppController.tDb.getString(Constant.taddress));
        if(!TelephonyManagerInfo.gpsCheck(activity)) {
            displayPromptForEnablingGPS();
        }

        travelersRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TravelerHomePage.this, AddressListActivity.class);
                intent.putExtra("navigateFrom","2");
                startActivity(intent);
            }
        });


        locationEnabledHandler();

        try {
            if (TelephonyManagerInfo.isConnectingToInternet(activity)) {
                try {
                    userAppVersion = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }

                VersionChecker versionChecker = new VersionChecker();
                try {
                    latestVersion = versionChecker.execute().get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }


                versionhandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (!userAppVersion.equals(latestVersion)) {
                                if(latestVersion != null && !latestVersion.equals("null") && !latestVersion.equals(""))
                                    versionCheck();
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }, 5000);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        callDeskRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              try
              {

                  callDeskNum = AppController.tDb.getString(Constant.callDeskNum);
                  Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + callDeskNum));
                  startActivity(dialIntent);


              }
              catch (SecurityException e)
              {
                  e.printStackTrace();
              }
              catch (Exception e)
              {
                  e.printStackTrace();
              }
            }
        });

        callDriverRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try
                {
                    Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + driver_mobile));
                    startActivity(dialIntent);
                }
                catch (SecurityException e)
                {
                    e.printStackTrace();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });








    }


//    public void callPhoneNumber()
//    {
//        try
//        {
//            if(Build.VERSION.SDK_INT > 22)
//            {
//                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
//                    // TODO: Consider calling
//
//                    ActivityCompat.requestPermissions(TravelerHomePage.this, new String[]{android.Manifest.permission.CALL_PHONE}, 101);
//
//                    return;
//                }
//
//                Intent callIntent = new Intent(Intent.ACTION_CALL);
//                callIntent.setData(Uri.parse("tel:" + callDeskNum));
//                startActivity(callIntent);
//
//            }
//            else {
//                Intent callIntent = new Intent(Intent.ACTION_CALL);
//                callIntent.setData(Uri.parse("tel:" + callDeskNum));
//                startActivity(callIntent);
//            }
//        }
//        catch (Exception ex)
//        {
//            ex.printStackTrace();
//        }
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                                           int[] grantResults)
//    {
//        if(requestCode == 101)
//        {
//            if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
//            {
//                callPhoneNumber();
//            }
//            else
//            {
//
//            }
//        }
//    }

    public static void sendMessageToActivity(int msg) {
        Message m = new Message();
        m.what = msg;
        ((TravelerHomePage)activity).updateDriverHandler1.sendMessage(m);
    }

    @SuppressLint("HandlerLeak")
    public Handler updateDriverHandler1 = new Handler()
    {
        // @Override
        public void handleMessage(Message msg) {

            int event = msg.what;
            switch(event)
            {
                case 1:
//                    getTravellerDatas(AppController.tDb.getString(Constant.userId));
                    if(isFirstEntry != 0)
                        getTravellerDatas();
                    break;

                case 3:
                    String deviceid="";
                    try
                    {
                        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                        deviceid = telephonyManager.getDeviceId();
                    }
                    catch (SecurityException e)
                    {
                        e.printStackTrace();
                    }
                    catch (Exception e)
                    {
                        Log.e("","");
                    }
                    JSONObject root = new JSONObject();
                    String userId = UserDetail.getInstance().getUserId();
                    try
                    {
                        //  root.put("userid", userId);
                        root.put("carid", TravelerHomePage.carId);
                        root.put("tripid",TravelerHomePage.tripId);
                        //  root.put("deviceid", deviceid);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    apiSataus = 4;
                    if(ServerInterface.travelerLatLon)
                        ServerInterface.getInstance(TravelerHomePage.activity).TraveRequest(userId, deviceid, UrlConfig.getCarUpdate, root.toString(),TravelerHomePage.this, false);

                    break;

            }
        }
    };


//    public void getTravellerDatas(String userId)
    public void getTravellerDatas()
    {
        try
        {
            CabFragment.googleMap.clear();
        }
        catch (Exception e)
        {

        }
        apiSataus = 1;
        String deviceid="";
        try {
            deviceid = TelephonyManagerInfo.getIMEI(activity);
        }catch (Exception e)
        {}
        String userId1 = AppController.tDb.getString(Constant.userId);
        JSONObject root = new JSONObject();
        try {
//            root.put("userid", userId);
//            root.put("deviceid", deviceid);
            String gcm = AppController.tDb.getString(Constant.gcm_id);
            root.put("gcm", gcm);
        }catch (Exception e)
        {}

        try
        {
            checkMrkerAdded=0;
            tripFound = "";
            topRl.setVisibility(View.VISIBLE);
            travellerView.setVisibility(View.GONE);
            feedbackRl.setBackgroundColor(getResources().getColor(R.color.disable_button_color));
            feedbackRl.setEnabled(false);
            feedbackRl.setFocusable(false);

            callDriverRl.setBackgroundColor(getResources().getColor(R.color.disable_button_color));
            callDriverRl.setEnabled(false);
            callDriverRl.setFocusable(false);
            sosRl.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            sosRl.setEnabled(true);
            sosRl.setFocusable(true);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        ServerInterface.getInstance(activity).makeServiceCall(userId1, deviceid, UrlConfig.travellerAppData, root.toString(),TravelerHomePage.this,true, "Please wait...");

    }

    double serverLat    = 0.0;
    double serverLon    = 0.0;
    double lastLat      = 0.0;
    double lastLon      = 0.0;

    public void parseResponse(String response)
    {
        if (response != null && !response.equals("") && !response.equals("Not Allowed") && !response.equals("server_error")) {
            if (apiSataus == 4) {
                if(isFirstEntry == 0)
                {
                    isFirstEntry++;
                    updateTime = 60;
                }
                final JSONObject jObj;
                try {

                    jObj = new JSONObject(response);
                    String status = jObj.getString("status");
                    String tripStatus = jObj.getString("trip_status");
                    if (tripStatus.equals("2") || tripStatus.equals("3")) {      // 2 for complete, 3 for cancelled
                        try {
                            tripFound = "no trip";
                            topRl.setVisibility(View.GONE);
                            travellerView.setVisibility(View.VISIBLE);
                            UserDetail.getInstance().setUserData("");
                            CustomPopup cp = new CustomPopup(TravelerHomePage.this);

                            if(tripStatus.equals("2"))
                            {
//                                cp.commonPopup("Trip Completed");
                                callDriverRl.setVisibility(View.GONE);
                                Intent intent = new Intent(TravelerHomePage.this, FeedBackActivity.class);
                                intent.putExtra("isTripComplete", true);
                                mHandler.removeCallbacks(mRunnable);
                                startActivity(intent);

                            }

                            else if( tripStatus.equals("3"))
                              cp.commonPopup("Trip Cancelled");



                            safeReach = 0;
                            CabFragment.googleMap.clear();
                            mHandler.removeCallbacks(mRunnable);

                            sosRl.setBackgroundColor(getResources().getColor(R.color.disable_button_color));
                            sosRl.setEnabled(false);
                            sosRl.setFocusable(false);

//                            TravelerHomePage.safeReachRl.setVisibility(View.GONE);
                            TravelerHomePage.readyToBoard.setVisibility(View.VISIBLE);
                            TravelerHomePage.readyToBoard.setClickable(false);
                            TravelerHomePage.readyToBoard.setEnabled(false);
                            TravelerHomePage.readyToBoardRl.setBackgroundColor(getResources().getColor(R.color.disable_button_color));

                            TravelerHomePage.travelersRl.setVisibility(View.GONE);

                            return;
                        } catch (Exception e) {
                        }
                    }

                    if (status.equals("0"))
                        return;

                } catch (Exception e) {
                    return;
                }


                try {
                    TravelerHomePage.activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if (markerList == null) {
                                markerList = new ArrayList<>();
                            }
                            try {

                                for (int i = 0; i < markerList.size(); i++) {
                                    Marker mm = markerList.get(i);
                                    mm.remove();
                                }

                                JSONObject jObj1        = jObj.getJSONObject("info");
                                String carLat           = jObj1.getString("lat");
                                String carLon           = jObj1.getString("lon");
//                                carLatLng               = new LatLng(carLat, carLon);

                                Double carLat12     = 0.0;
                                Double carLng12     = 0.0;

                                LatLng carLatLng    = new LatLng(carLat12, carLng12);
                                if(!carLat.equals("") && !carLat.equals("null") && carLat != null && !carLon.equals("") && !carLon.equals("null") && carLon != null)
                                {
                                    carLat12        = Double.parseDouble(carLat);
                                    carLng12        = Double.parseDouble(carLon);
                                    carLatLng       = new LatLng(carLat12, carLng12);
                                }

                                String otpValidatedTemp = jObj.getString("otp_validated");

                                    otpValidated = otpValidatedTemp;
                                    if(otpValidated.equals("3"))
                                    {
                                        CabFragment.googleMap.clear();

                                        MarkerOptions markerOptions1 = new MarkerOptions();
                                        MarkerOptions markerOptions2 = new MarkerOptions();
                                        if(carLatLng != null && dropLatLng != null )
                                        {
                                            Double lat   = carLatLng.latitude;
                                            Double lon   = carLatLng.longitude;
                                            Double d_lat = dropLatLng.longitude;
                                            Double d_lon = dropLatLng.longitude;
                                            if(lat != 0.0 && lon != 0.0 && d_lat != 0.0 && d_lon != 0.0)
                                            {
                                                lockBlock.lock();
                                                try
                                                {
                                                    String pathUrl = getPathUrl(carLatLng, dropLatLng);

                                                    FetchUrlForRoute FetchUrl = new FetchUrlForRoute();
                                                    FetchUrl.execute(pathUrl);
                                                }
                                                catch (Exception e)
                                                {
                                                    e.printStackTrace();
                                                }
                                                finally
                                                {
                                                    lockBlock.unlock();
                                                };

                                                if(firstTimeInOtpValidated == 0)
                                                {
                                                    firstTimeInOtpValidated++;
                                                    mHandler.removeCallbacks(mRunnable);
                                                    mHandler.postDelayed(mRunnable, 1 * 500);
                                                }

                                                markerOptions1.position(carLatLng);

                                                if(!AppController.mp.travelTime.equals("") && AppController.mp.travelTime != null)
                                                {
                                                    textView.setText(AppController.mp.travelTime);
                                                    Bitmap bitmap = frameLayoutToBitmap(frameLayout);
                                                    markerOptions1.icon(BitmapDescriptorFactory.fromBitmap(bitmap));
                                                }
                                                else
                                                {
                                                    markerOptions1.icon(BitmapDescriptorFactory.fromResource(R.drawable.car_icon1));
                                                }

                                                markerOptions2.position(dropLatLng);
                                                markerOptions2.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

                                                CabFragment.googleMap.addMarker(markerOptions1);
                                                CabFragment.googleMap.addMarker(markerOptions2);
                                            }
                                            else if(lat == 0.0 && lon == 0.0 && d_lat != 0.0 && d_lon != 0.0)
                                            {
                                                markerOptions2.position(dropLatLng);
                                                markerOptions2.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                                                CabFragment.googleMap.addMarker(markerOptions2);
                                            }
                                            else if(lat != 0.0 && lon != 0.0 && d_lat == 0.0 && d_lon == 0.0)
                                            {
                                                markerOptions1.position(carLatLng);
                                                markerOptions1.icon(BitmapDescriptorFactory.fromResource(R.drawable.car_icon1));
                                                CabFragment.googleMap.addMarker(markerOptions1);
                                            }
                                        }
                                        else if(carLatLng != null && dropLatLng == null)
                                        {
                                            Double lat = carLatLng.longitude;
                                            Double lon = carLatLng.longitude;
                                            if(lat != 0.0 && lon != 0.0)
                                            {
                                                markerOptions1.position(carLatLng);
                                                markerOptions1.icon(BitmapDescriptorFactory.fromResource(R.drawable.car_icon1));
                                                CabFragment.googleMap.addMarker(markerOptions1);
                                            }
                                        }
                                        else if(dropLatLng != null)
                                        {
                                            Double lat = dropLatLng.longitude;
                                            Double lon = dropLatLng.longitude;
                                            if(lat != 0.0 && lon != 0.0)
                                            {
                                                markerOptions2.position(carLatLng);
                                                markerOptions2.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                                                CabFragment.googleMap.addMarker(markerOptions2);
                                            }
                                        }

                                        feedbackRl.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                                        feedbackRl.setEnabled(true);
                                        feedbackRl.setFocusable(true);
                                        callDriverRl.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                                        callDriverRl.setEnabled(true);
                                        callDriverRl.setFocusable(true);

//                                        CabFragment.googleMap.clear();
//
//                                        MarkerOptions markerOptions2 = new MarkerOptions();
//                                        MarkerOptions markerOptions3 = new MarkerOptions();
//
//                                        if(carLatLng != null && dropLatLng != null)
//                                        {
//                                            Double lat = dropLatLng.latitude;
//                                            Double lon = dropLatLng.longitude;
//                                            if(lat != 0.0 && lon != 0.0)
//                                            {
//                                                String pathUrl = getPathUrl(carLatLng, dropLatLng);
//                                                FetchUrlForRoute FetchUrl = new FetchUrlForRoute();
//                                                FetchUrl.execute(pathUrl);
//
//                                                markerOptions2.position(dropLatLng);
//                                                markerOptions2.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
//
//                                                markerOptions3.position(carLatLng);
//                                                textView.setText(AppController.mp.travelTime);
//                                                bitmap = frameLayoutToBitmap(frameLayout);
//                                                markerOptions3.icon(BitmapDescriptorFactory.fromBitmap(bitmap));
//
//                                                CabFragment.googleMap.addMarker(markerOptions2);
//                                                CabFragment.googleMap.addMarker(markerOptions3);
//                                            }
//                                            else
//                                            {
//                                                markerOptions3.icon(BitmapDescriptorFactory.fromResource(R.drawable.car_icon1));
//                                            }
//
//                                        }
//                                        else if(carLatLng != null && dropLatLng == null)
//                                        {
//                                            markerOptions3.icon(BitmapDescriptorFactory.fromResource(R.drawable.car_icon1));
//                                        }
//
//                                        feedbackRl.setBackgroundColor(getResources().getColor(R.color.colorAccent));
//                                        feedbackRl.setEnabled(true);
//                                        feedbackRl.setFocusable(true);
//                                        callDriverRl.setBackgroundColor(getResources().getColor(R.color.colorAccent));
//                                        callDriverRl.setEnabled(true);
//                                        callDriverRl.setFocusable(true);
                                    }

                                    else
                                    {

                                        CabFragment.googleMap.clear();

                                        MarkerOptions markerOptions1 = new MarkerOptions();
                                        MarkerOptions markerOptions2 = new MarkerOptions();
                                        MarkerOptions markerOptions3 = new MarkerOptions();

                                        if(pickUpLatLng != null && carLatLng != null )
                                        {
                                            Double lat = carLatLng.latitude;
                                            Double lon = carLatLng.longitude;
                                            Double p_lat = pickUpLatLng.longitude;
                                            Double p_lon = pickUpLatLng.longitude;
                                            if(lat != 0.0 && lon != 0.0 && p_lat != 0.0 && p_lon != 0.0)
                                            {
                                                lockBlock.lock();
                                                try
                                                {
                                                    String pathUrl = getPathUrl(pickUpLatLng, carLatLng);

                                                    FetchUrlForRoute FetchUrl = new FetchUrlForRoute();
                                                    FetchUrl.execute(pathUrl);
                                                }
                                                catch (Exception e)
                                                {
                                                    e.printStackTrace();
                                                }
                                                finally
                                                {
                                                    lockBlock.unlock();
                                                }

                                                markerOptions1.position(carLatLng);

                                                if(!AppController.mp.travelTime.equals("") && AppController.mp.travelTime != null)
                                                {
                                                    textView.setText(AppController.mp.travelTime);
                                                    Bitmap bitmap = frameLayoutToBitmap(frameLayout);
                                                    markerOptions1.icon(BitmapDescriptorFactory.fromBitmap(bitmap));
                                                }
                                                else
                                                {
                                                    markerOptions1.icon(BitmapDescriptorFactory.fromResource(R.drawable.car_icon1));
                                                }

                                                markerOptions2.position(pickUpLatLng);
                                                markerOptions2.icon(BitmapDescriptorFactory.fromResource(R.drawable.man_green1));

                                                CabFragment.googleMap.addMarker(markerOptions1);
                                                CabFragment.googleMap.addMarker(markerOptions2);
                                            }
                                            else if(lat == 0.0 && lon == 0.0 && p_lat != 0.0 && p_lon != 0.0)
                                            {
                                                markerOptions2.position(pickUpLatLng);
                                                markerOptions2.icon(BitmapDescriptorFactory.fromResource(R.drawable.man_green1));
                                                CabFragment.googleMap.addMarker(markerOptions2);
                                            }
                                            else if(lat != 0.0 && lon != 0.0 && p_lat == 0.0 && p_lon == 0.0)
                                            {
                                                markerOptions1.position(carLatLng);
                                                markerOptions1.icon(BitmapDescriptorFactory.fromResource(R.drawable.car_icon1));
                                                CabFragment.googleMap.addMarker(markerOptions1);
                                            }
                                        }
                                        else if(carLatLng != null && pickUpLatLng == null)
                                        {
                                            Double lat = carLatLng.longitude;
                                            Double lon = carLatLng.longitude;
                                            if(lat != 0.0 && lon != 0.0)
                                            {
                                                markerOptions1.position(carLatLng);
                                                markerOptions1.icon(BitmapDescriptorFactory.fromResource(R.drawable.car_icon1));
                                                CabFragment.googleMap.addMarker(markerOptions1);
                                            }
                                        }
                                        else if(carLatLng == null && pickUpLatLng != null)
                                        {
                                            Double lat = carLatLng.longitude;
                                            Double lon = carLatLng.longitude;
                                            if(lat != 0.0 && lon != 0.0)
                                            {
                                                markerOptions1.position(carLatLng);
                                                markerOptions1.icon(BitmapDescriptorFactory.fromResource(R.drawable.man_green1));
                                                CabFragment.googleMap.addMarker(markerOptions1);
                                            }
                                        }
                                        if(dropLatLng != null)
                                        {
                                            Double lat = dropLatLng.longitude;
                                            Double lon = dropLatLng.longitude;
                                            if(lat != 0.0 && lon != 0.0)
                                            {
                                                markerOptions3.position(dropLatLng);
                                                markerOptions3.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                                                CabFragment.googleMap.addMarker(markerOptions3);
                                            }
                                        }

                                    }
//                                }
//                                else
//                                {
//                                    CabFragment.googleMap.clear();
//
//                                    MarkerOptions markerOptions1 = new MarkerOptions();
//                                    MarkerOptions markerOptions2 = new MarkerOptions();
//                                    MarkerOptions markerOptions3 = new MarkerOptions();
//
//                                    if(pickUpLatLng != null)
//                                    {
//                                        markerOptions1.position(pickUpLatLng);
//
//                                        Bitmap bitmap = frameLayoutToBitmap(frameLayout);
//                                        markerOptions1.icon(BitmapDescriptorFactory.fromBitmap(bitmap));
////                                            markerOptions1.icon(BitmapDescriptorFactory.fromResource(R.drawable.man_green));
//                                        CabFragment.googleMap.addMarker(markerOptions1);
//                                    }
//                                    if(dropLatLng != null)
//                                    {
//                                        markerOptions2.position(dropLatLng);
//                                        markerOptions2.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
//                                        CabFragment.googleMap.addMarker(markerOptions2);
//                                    }
//
//                                    if(carLatLng != null)
//                                    {
//                                        markerOptions3.position(carLatLng);
//                                        markerOptions3.icon(BitmapDescriptorFactory.fromResource(R.drawable.car_icon1));
//                                        CabFragment.googleMap.addMarker(markerOptions3);
//                                    }
//
//                                    feedbackRl.setBackgroundColor(getResources().getColor(R.color.colorAccent));
//                                    feedbackRl.setEnabled(true);
//                                    feedbackRl.setFocusable(true);
//                                    callDriverRl.setBackgroundColor(getResources().getColor(R.color.colorAccent));
//                                    callDriverRl.setEnabled(true);
//                                    callDriverRl.setFocusable(true);
//
//                                    if(carLatLng != null && pickUpLatLng != null)
//                                    {
//                                        String pathUrl = getPathUrl(carLatLng, pickUpLatLng);
//                                        FetchUrlForRoute FetchUrl = new FetchUrlForRoute();
//                                        FetchUrl.execute(pathUrl);
//                                    }
//
//                                }

//                                serverLat = Double.parseDouble(lat);
//                                serverLon = Double.parseDouble(lon);


//                                LatLng currentPosition = new LatLng(serverLat, serverLon);

                                if (checkMrkerAdded == 0) {
                                    MarkerOptions markerOptions = new MarkerOptions();
//                                    markerOptions.position(currentPosition);
                                    markerOptions.position(carLatLng);

                                    if(!otpValidated.equals("3"))
                                    {
                                        if(pickUpLatLng != null )
                                        {
                                            if(!AppController.mp.travelTime.equals("") && AppController.mp.travelTime != null)
                                            {
                                                textView.setText(AppController.mp.travelTime);
                                                Bitmap bitmap = frameLayoutToBitmap(frameLayout);
                                                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(bitmap));
                                            }
                                            else
                                            {
                                                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.car_icon1));
                                            }
                                        }
                                        else
                                        {
                                            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.car_icon1));
                                        }
                                    }
                                    else
                                    {
                                        if(dropLatLng != null )
                                        {
                                            if(!AppController.mp.travelTime.equals("") && AppController.mp.travelTime != null)
                                            {
                                                textView.setText(AppController.mp.travelTime);
                                                Bitmap bitmap = frameLayoutToBitmap(frameLayout);
                                                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(bitmap));
                                            }
                                            else
                                            {
                                                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.car_icon1));
                                            }
                                        }
                                        else
                                        {
                                            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.car_icon1));
                                        }
                                    }
//
                                    finalMarker = CabFragment.googleMap.addMarker(markerOptions);
//                                    finalMarker.setFlat(true);
                                    checkMrkerAdded = 1;
                                }
                                else {
                                    finalMarker.setPosition(carLatLng);
                                    //  finalMarker.setFlat(true);
                                }
                                if (lastLat != 0 && lastLon != 0)
                                {
                                    drawCarMarker(lastLat, lastLon, serverLat, serverLon);
//                                    LatLng carLatLng = new LatLng(serverLat,serverLon);

                                    lastLat = serverLat;
                                    lastLon = serverLon;
                                }




                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });

                } catch (SecurityException e) {
                    e.printStackTrace();
                }

            } else if (apiSataus == 2) {
                try {
                    JSONObject jsonData = new JSONObject(response);
                    //  String status = jsonData.getString("status");
                    //  String msg = jsonData.getString("msg");
                } catch (Exception e) {
                }

            } else if (apiSataus == 5) {
                try {
                    JSONObject jsonData = new JSONObject(response);
                    String status = jsonData.getString("api_status");
                    if (status.equals("1"))
                        Toast.makeText(activity, "Feedback submitted successfully", Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (apiSataus == 1)
            {
                UserDetail.getInstance().setUserData(response);
//                parseData(response);
                finish();
                overridePendingTransition(0, 0);
                startActivity(getIntent());
                overridePendingTransition(0, 0);
            }

        }else if(response != null && !response.equals("") && response.equals("Not Allowed")){
            final android.support.v7.app.AlertDialog.Builder builder =  new android.support.v7.app.AlertDialog.Builder(activity);
            builder.setCancelable(false);
            final String message = "You are not allowed. Please contact admin.";
            builder.setMessage(message)
                    .setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface d, int id) {
                                    finish();
                                }
                            });
            builder.create().show();
        } else
        {
            final android.support.v7.app.AlertDialog.Builder builder =  new android.support.v7.app.AlertDialog.Builder(activity);
            final String message = "Sorry for inconvenience. Please try again later";
            builder.setMessage(message)
                    .setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface d, int id) {
                                    finish();
                                }
                            });
            builder.create().show();
        }
    }

    public static String tripid1;
    public static String pickupaddress1;
    public static String car1;
    public static String driver1,otp1;
    public static String type="";
    public static String carLat1, carLon1;


    Handler mHandler;

    public void parseData()
//    public void parseData(String data)
    {
        final String userData = UserDetail.getInstance().getUserData();
        if (userData == null || userData.equals(""))
        {
//            parseData(userData);
            try
            {
                tripFound = "no trip";
                topRl.setVisibility(View.GONE);
                travellerView.setVisibility(View.VISIBLE);
                CustomPopup cp = new CustomPopup(TravelerHomePage.this);
                cp.commonPopup("No Trip Found");
                safeReach = 0;
                isTripFound = 0;

                feedbackRl.setBackgroundColor(getResources().getColor(R.color.disable_button_color));
                feedbackRl.setEnabled(false);
                feedbackRl.setFocusable(false);
                callDriverRl.setBackgroundColor(getResources().getColor(R.color.disable_button_color));
                callDriverRl.setEnabled(false);
                callDriverRl.setFocusable(false);
                TravelerHomePage.sosRl.setBackgroundColor(getResources().getColor(R.color.disable_button_color));
                TravelerHomePage.sosRl.setEnabled(false);
                TravelerHomePage.sosRl.setFocusable(false);

                if(AppController.tDb.getString(Constant.tReadyBoard).equals("1") && TravelerHomePage.isTripFound == 0) {
                    TravelerHomePage.readyToBoard.setVisibility(View.VISIBLE);
                    TravelerHomePage.travelersRl.setVisibility(View.GONE);
                }
                else
                {
                    TravelerHomePage.readyToBoard.setVisibility(View.GONE);
                    TravelerHomePage.travelersRl.setVisibility(View.VISIBLE);
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return;
        }


        JSONObject jObject;
        try
        {
            TravelerHomePage.sosRl.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            TravelerHomePage.sosRl.setEnabled(true);
            TravelerHomePage.sosRl.setFocusable(true);
                try
                {
                    jObject = new JSONObject(userData);
//                    jObject = new JSONObject(data);
                    String sosnumber = jObject.getString("sosnumber");
                    this.sosNum = sosnumber;

                    JSONArray rootArray     = jObject.getJSONArray("trip");
                    String sendupdateevery  = jObject.getString("sendupdateevery");
                    String ready_to_board   = jObject.getString("ready_to_board");

                    AppController.tDb.putString(Constant.tReadyBoard, ready_to_board);

                    List<TravelerDetail> traveList = new ArrayList<>();
                    arrayListTraveller = new ArrayList<TravelerDetail>();

                    if(rootArray.length() == 0)
                    {
                        travelersRl.setVisibility(View.GONE);
                    }

                    for (int i = 0; i < rootArray.length(); i++)
                    {
                        JSONObject dataObj      = rootArray.getJSONObject(i);
                        String status           = dataObj.getString("status");
                        if(status.equals("0"))
                        {
                            AppController.tDb.putString(Constant.userId, "");
                            Intent intent = new Intent(TravelerHomePage.this, LoginActivity.class);
                            startActivity(intent);
                            this.finish();
                        }
                        String traveltype       = dataObj.getString("traveltype");     // 2 for pick up and 1 for drop
                        String tripDate         = dataObj.getString("tripdate");
                        String pickuptime       = dataObj.getString("pickuptime");
                        String pickupaddress    = dataObj.getString("pickupaddress");
                        String pickuplat        = dataObj.getString("pickuplat");
                        String pickuplon        = dataObj.getString("pickuplon");
                        String driver           = dataObj.getString("driver");
                        driver_mobile           = dataObj.getString("driver_mobile");
                        String car              = dataObj.getString("car");
                        String carlat           = dataObj.getString("carlat");
                        String carlon           = dataObj.getString("carlon");
                        String tripid           = dataObj.getString("tripid");
                        String carid            = dataObj.getString("carid");
                        String targettime       = dataObj.getString("targettime");
                        String destination      = dataObj.getString("destination");
                        String destlat          = dataObj.getString("destlat");
                        String destlon          = dataObj.getString("destlon");
                        final String otp        = dataObj.getString("OTP");
                        String address1         = dataObj.getString("address");
                        otpValidated            = dataObj.getString("otp_validated");

                        AppController.tDb.putString(Constant.taddress, address1);


                        Double pickUpLat    = 0.0;
                        Double pickUpLng    = 0.0;
                        Double dropLat      = 0.0;
                        Double dropLng      = 0.0;
                        Double carLat12     = 0.0;
                        Double carLng12     = 0.0;

                        if(!pickuplat.equals("") && !pickuplat.equals("null") && pickuplat != null && !pickuplon.equals("") && !pickuplon.equals("null") && pickuplon != null)
                        {
                            pickUpLat       = Double.parseDouble(pickuplat);
                            pickUpLng       = Double.parseDouble(pickuplon);
                            pickUpLatLng    = new LatLng(pickUpLat, pickUpLng);
                        }

                        if(!destlat.equals("") && destlat != null && !destlat.equals("null") && !destlon.equals("")&& !destlat.equals("null") && destlon != null)
                        {
                            dropLat         = Double.parseDouble(destlat);
                            dropLng         = Double.parseDouble(destlon);
                            dropLatLng      = new LatLng(dropLat, dropLng);
                        }

                        LatLng carLatLng    = new LatLng(carLat12, carLng12);
                        if(!carlat.equals("") && !carlat.equals("null") && carlat != null && !carlon.equals("") && !carlat.equals("null") && carlon != null)
                        {
                            carLat12        = Double.parseDouble(carlat);
                            carLng12        = Double.parseDouble(carlon);
                            carLatLng       = new LatLng(carLat12, carLng12);
                        }



                        String pathUrl = "";
                        if(otpValidated.equals("3"))
                        {
                            CabFragment.googleMap.clear();

                            MarkerOptions markerOptions1 = new MarkerOptions();
                            MarkerOptions markerOptions2 = new MarkerOptions();
                            if(carLatLng != null && dropLatLng != null )
                            {
                                Double lat   = carLatLng.latitude;
                                Double lon   = carLatLng.longitude;
                                Double d_lat = dropLatLng.longitude;
                                Double d_lon = dropLatLng.longitude;
                                if(lat != 0.0 && lon != 0.0 && d_lat != 0.0 && d_lon != 0.0)
                                {
                                    lockBlock.lock();
                                    try
                                    {
                                        pathUrl = getPathUrl(carLatLng, dropLatLng);

                                        FetchUrlForRoute FetchUrl = new FetchUrlForRoute();
                                        FetchUrl.execute(pathUrl);
                                    }
                                    catch (Exception e)
                                    {
                                        e.printStackTrace();
                                    }
                                    finally
                                    {
                                        lockBlock.unlock();
                                    }


                                    markerOptions1.position(carLatLng);

                                    textView.setText(AppController.mp.travelTime);
                                    Bitmap bitmap = frameLayoutToBitmap(frameLayout);
                                    markerOptions1.icon(BitmapDescriptorFactory.fromBitmap(bitmap));

                                    markerOptions2.position(dropLatLng);
                                    markerOptions2.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

                                    CabFragment.googleMap.addMarker(markerOptions1);
                                    CabFragment.googleMap.addMarker(markerOptions2);
                                }
                                else if(lat == 0.0 && lon == 0.0 && d_lat != 0.0 && d_lon != 0.0)
                                {
                                    markerOptions2.position(dropLatLng);
                                    markerOptions2.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                                    CabFragment.googleMap.addMarker(markerOptions2);
                                }
                                else if(lat != 0.0 && lon != 0.0 && d_lat == 0.0 && d_lon == 0.0)
                                {
                                    markerOptions1.position(carLatLng);
                                    markerOptions1.icon(BitmapDescriptorFactory.fromResource(R.drawable.car_icon1));
                                    CabFragment.googleMap.addMarker(markerOptions1);
                                }
                            }
                            else if(carLatLng != null && dropLatLng == null)
                            {
                                Double lat = carLatLng.longitude;
                                Double lon = carLatLng.longitude;
                                if(lat != 0.0 && lon != 0.0)
                                {
                                    markerOptions1.position(carLatLng);
                                    markerOptions1.icon(BitmapDescriptorFactory.fromResource(R.drawable.car_icon1));
                                    CabFragment.googleMap.addMarker(markerOptions1);
                                }
                            }

                            feedbackRl.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                            feedbackRl.setEnabled(true);
                            feedbackRl.setFocusable(true);
                            callDriverRl.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                            callDriverRl.setEnabled(true);
                            callDriverRl.setFocusable(true);
                        }
                        else
                        {
                            CabFragment.googleMap.clear();

                            MarkerOptions markerOptions1 = new MarkerOptions();
                            MarkerOptions markerOptions2 = new MarkerOptions();
                            MarkerOptions markerOptions3 = new MarkerOptions();
                            if(pickUpLatLng != null && carLatLng != null )
                            {
                                Double lat   = carLatLng.latitude;
                                Double lon   = carLatLng.longitude;
                                Double p_lat = pickUpLatLng.longitude;
                                Double p_lon = pickUpLatLng.longitude;

                                if(lat != 0.0 && lon != 0.0 && p_lat != 0.0 && p_lon != 0.0)
                                {
                                    lockBlock.lock();

                                     try
                                    {
                                        pathUrl = getPathUrl(pickUpLatLng, carLatLng);

                                        FetchUrlForRoute FetchUrl = new FetchUrlForRoute();
                                        FetchUrl.execute(pathUrl);
                                    }
                                    catch (Exception e)
                                    {
                                        e.printStackTrace();
                                    }
                                    finally
                                    {
                                        lockBlock.unlock();
                                    }

                                    markerOptions1.position(carLatLng);

                                    if(!AppController.mp.travelTime.equals("") && AppController.mp.travelTime != null)
                                    {
                                        textView.setText(AppController.mp.travelTime);
                                        Bitmap bitmap = frameLayoutToBitmap(frameLayout);
                                        markerOptions1.icon(BitmapDescriptorFactory.fromBitmap(bitmap));
                                    }
                                    else
                                    {
                                        markerOptions1.icon(BitmapDescriptorFactory.fromResource(R.drawable.car_icon1));
                                    }

                                    markerOptions2.position(pickUpLatLng);
                                    markerOptions2.icon(BitmapDescriptorFactory.fromResource(R.drawable.man_green1));

                                    CabFragment.googleMap.addMarker(markerOptions1);
                                    CabFragment.googleMap.addMarker(markerOptions2);
                                }
                                else if(lat == 0.0 && lon == 0.0 && p_lat != 0.0 && p_lon != 0.0)
                                {
                                    markerOptions2.position(pickUpLatLng);
                                    markerOptions2.icon(BitmapDescriptorFactory.fromResource(R.drawable.man_green1));
                                    CabFragment.googleMap.addMarker(markerOptions2);
                                }
                                else if(lat != 0.0 && lon != 0.0 && p_lat == 0.0 && p_lon == 0.0)
                                {
                                    markerOptions1.position(carLatLng);
                                    markerOptions1.icon(BitmapDescriptorFactory.fromResource(R.drawable.car_icon1));
                                    CabFragment.googleMap.addMarker(markerOptions1);
                                }
                            }
                            else if(carLatLng != null && pickUpLatLng == null)
                            {
                                Double lat = carLatLng.longitude;
                                Double lon = carLatLng.longitude;

                                if(lat != 0.0 && lon != 0.0)
                                {
                                    markerOptions1.position(carLatLng);
                                    markerOptions1.icon(BitmapDescriptorFactory.fromResource(R.drawable.car_icon1));
                                    CabFragment.googleMap.addMarker(markerOptions1);
                                }

                            }
                            else if(carLatLng == null && pickUpLatLng != null)
                            {
                                Double lat = carLatLng.longitude;
                                Double lon = carLatLng.longitude;
                                if(lat != 0.0 && lon != 0.0)
                                {
                                    markerOptions1.position(carLatLng);
                                    markerOptions1.icon(BitmapDescriptorFactory.fromResource(R.drawable.man_green1));
                                    CabFragment.googleMap.addMarker(markerOptions1);
                                }
                            }

                            if(dropLatLng != null)
                            {
                                markerOptions3.position(dropLatLng);
                                markerOptions3.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                                CabFragment.googleMap.addMarker(markerOptions3);
                            }
                        }




                        isTripFound = 1;
                        if(ready_to_board.equals("1") && isTripFound == 0) {
                            TravelerHomePage.readyToBoard.setVisibility(View.VISIBLE);
                            TravelerHomePage.travelersRl.setVisibility(View.GONE);
                        }
                        else
                        {
                            TravelerHomePage.readyToBoard.setVisibility(View.GONE);
                            TravelerHomePage.travelersRl.setVisibility(View.VISIBLE);
                        }
                        if(!driver_mobile.equals("") && driver_mobile != null)
                        {
                            callDriverRl.setVisibility(View.VISIBLE);
                        }
                        else
                        {
                            callDriverRl.setVisibility(View.GONE);
                        }
                        if (i == 0) {
                            this.tripId = tripid;
                            this.carId = carid;
                            AppController.tDb.putString(Constant.firstTripId, tripid);
                            AppController.tDb.putString(Constant.firstCarId, carid);

                            tripid1 = dataObj.getString("tripid");
                            pickupaddress1 = dataObj.getString("pickupaddress");
                            car1 = dataObj.getString("car");
                            driver1 = dataObj.getString("driver");
                            otp1 = dataObj.getString("OTP");
                            type = dataObj.getString("traveltype");

                            AppController.tDb.putString(Constant.tEmpOtp, otp1);

                            carLat1 = carlat;
                            carLon1 = carlon;


                            try {
                                driverName.setText(TravelerHomePage.driver1);
                                gaadiNum.setText(TravelerHomePage.car1);

                                otpTv.setText("Trip id - " + TravelerHomePage.tripid1 + ", " + "OTP - " + TravelerHomePage.otp1);
                                empCode.setText("OTP - " + TravelerHomePage.otp1);
                                topRl.setVisibility(View.VISIBLE);
                                travellerView.setVisibility(View.GONE);
                                String dateFormat = tripDate.replace("-", "/");
                                Date date = new SimpleDateFormat("yyyy/MM/dd").parse(dateFormat);
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("d MMM");
                                String formattedDate  = simpleDateFormat.format(date);
                                dateTv.setText(formattedDate);
                                if (TravelerHomePage.type.equals("1"))      // drop
                                {
                                    pickUp.setText("Drop");
                                    timeTv.setText(pickuptime);
                                    address.setText(pickupaddress1);
                                    String reached_safe = dataObj.getString("reached_safe");

                                    if(reached_safe.equals("1"))
                                       safeReach = 1;
                                }
                                else if(TravelerHomePage.type.equals("2"))      // pick up
                                {
                                    pickUp.setText("Pick Up");
                                    timeTv.setText(pickuptime);
                                    address.setText(destination);
                                    safeReach = 0;
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }

                        if (TravelerHomePage.type.equals("1")) {    // drop
                            try {

                                try {

                                    String destLat = AppController.mp.travelerDetail.get(0).tripList.get(0).destlat;
                                    String destLon = AppController.mp.travelerDetail.get(0).tripList.get(0).destlon;
                                    if(destLat != null || !destLat.equals("") || destLon != null || !destLon.equals(""))
                                    {
                                        LatLng currentPosition2 = new LatLng(Double.parseDouble(AppController.mp.travelerDetail.get(0).tripList.get(0).destlat), // drop location
                                                Double.parseDouble(AppController.mp.travelerDetail.get(0).tripList.get(0).destlon));
                                        MarkerOptions markerOptions2 = new MarkerOptions();
                                        markerOptions2.position(currentPosition2);
                                        CabFragment.googleMap.addMarker(markerOptions2);
                                    }

                                }catch (Exception e)
                                {}


//                                try {
//                                    LatLng currentPosition1 = new LatLng(Double.parseDouble(AppController.mp.travelerDetail.get(0).tripList.get(0).pickuplat),      // pick up location
//                                            Double.parseDouble(AppController.mp.travelerDetail.get(0).tripList.get(0).pickuplon));
//                                    MarkerOptions markerOptions1 = new MarkerOptions();
//                                    markerOptions1.position(currentPosition1);
//
//                                    Bitmap bitmap = frameLayoutToBitmap(frameLayout);
//                                    markerOptions1.icon(BitmapDescriptorFactory.fromBitmap(bitmap));
////                                    markerOptions1.icon(BitmapDescriptorFactory.fromResource(R.drawable.man_green));
//                                    CabFragment.googleMap.addMarker(markerOptions1);
//
//                                }catch (Exception e)
//                                {
//
//                                }

                                try {
                                    LatLng currentPosition3 = new LatLng(Double.parseDouble(AppController.mp.travelerDetail.get(0).tripList.get(0).carlat),      // pick up location
                                            Double.parseDouble(AppController.mp.travelerDetail.get(0).tripList.get(0).carlon));
                                    MarkerOptions markerOptions3 = new MarkerOptions();
                                    markerOptions3.position(currentPosition3);

                                    if(otpValidated.equals("3"))
                                    {
                                        String destLat = AppController.mp.travelerDetail.get(0).tripList.get(0).destlat;
                                        String destLon = AppController.mp.travelerDetail.get(0).tripList.get(0).destlon;
                                        if(destLat != null || !destLat.equals("") || destLon != null || !destLon.equals(""))
                                        {
                                            if(!AppController.mp.travelTime.equals("") && AppController.mp.travelTime != null)
                                            {
                                                textView.setText(AppController.mp.travelTime);
                                                Bitmap bitmap = frameLayoutToBitmap(frameLayout);
                                                markerOptions3.icon(BitmapDescriptorFactory.fromBitmap(bitmap));
                                            }
                                            else
                                            {
                                                markerOptions3.icon(BitmapDescriptorFactory.fromResource(R.drawable.car_icon1));
                                            }
                                        }
                                        else
                                        {
                                            markerOptions3.icon(BitmapDescriptorFactory.fromResource(R.drawable.car_icon1));
                                        }

                                    }
                                    else
                                    {
                                        markerOptions3.icon(BitmapDescriptorFactory.fromResource(R.drawable.car_icon1));
                                    }

                                    Marker m = CabFragment.googleMap.addMarker(markerOptions3);
                                    m.setPosition(new LatLng(Double.parseDouble(AppController.mp.travelerDetail.get(0).tripList.get(0).carlat),      // pick up location
                                            Double.parseDouble(AppController.mp.travelerDetail.get(0).tripList.get(0).carlon)));
                                    markerList.add(m);

                                }
                                catch (Exception e)
                                {
                                  e.printStackTrace();
                                }
                            }
                            catch (Exception e)
                            {
                                e.printStackTrace();
                            }
                        }
                        else if(TravelerHomePage.type.equals("2"))             // pick up
                        {
                            try
                            {
                                try
                                {

                                    LatLng currentPosition2 = new LatLng(Double.parseDouble(AppController.mp.travelerDetail.get(0).tripList.get(0).destlat),
                                            Double.parseDouble(AppController.mp.travelerDetail.get(0).tripList.get(0).destlon)); // drop location
                                    MarkerOptions markerOptions2 = new MarkerOptions();
                                    markerOptions2.position(currentPosition2);
                                    CabFragment.googleMap.addMarker(markerOptions2);
                                }
                                catch (Exception e)
                                {
                                    e.printStackTrace();
                                }


                                try
                                {
                                    LatLng currentPosition1 = new LatLng(Double.parseDouble(AppController.mp.travelerDetail.get(0).tripList.get(0).pickuplat),      // pick up location
                                            Double.parseDouble(AppController.mp.travelerDetail.get(0).tripList.get(0).pickuplon));
                                    MarkerOptions markerOptions1 = new MarkerOptions();
                                    markerOptions1.position(currentPosition1);

                                    markerOptions1.icon(BitmapDescriptorFactory.fromResource(R.drawable.man_green));
                                    CabFragment.googleMap.addMarker(markerOptions1);
                                }
                                catch (Exception e)
                                {
                                        e.printStackTrace();
                                }

                                try
                                {
                                    LatLng currentPosition3 = new LatLng(Double.parseDouble(AppController.mp.travelerDetail.get(0).tripList.get(0).carlat),      // pick up location
                                            Double.parseDouble(AppController.mp.travelerDetail.get(0).tripList.get(0).carlon));
                                    MarkerOptions markerOptions3 = new MarkerOptions();
                                    markerOptions3.position(currentPosition3);

//                                    if(otpValidated.equals("3"))
//                                    {
                                    if(!AppController.mp.travelTime.equals("") && AppController.mp.travelTime != null)
                                    {
                                        textView.setText(AppController.mp.travelTime);
                                        Bitmap bitmap = frameLayoutToBitmap(frameLayout);
                                        markerOptions3.icon(BitmapDescriptorFactory.fromBitmap(bitmap));
                                    }
                                    else
                                    {
                                        markerOptions3.icon(BitmapDescriptorFactory.fromResource(R.drawable.car_icon1));
                                    }
//                                    }
//                                    else
//                                    {
//                                        markerOptions3.icon(BitmapDescriptorFactory.fromResource(R.drawable.car_icon1));
//                                    }

                                    Marker m = CabFragment.googleMap.addMarker(markerOptions3);
                                    m.setPosition(new LatLng(Double.parseDouble(AppController.mp.travelerDetail.get(0).tripList.get(0).carlat),      // pick up location
                                            Double.parseDouble(AppController.mp.travelerDetail.get(0).tripList.get(0).carlon)));
                                    markerList.add(m);
                                }
                                catch (Exception e)
                                {
                                    e.printStackTrace();
                                }
                            }
                            catch (Exception e)
                            {
                                e.printStackTrace();
                            }
                        }


                        JSONArray jsonArrayFellowTravellers = dataObj.getJSONArray("RestTravellersInfo");
                        noOfFellowTravelers = jsonArrayFellowTravellers.length();
                        if(jsonArrayFellowTravellers!=null)
                        {
                            for(int x=0;x<noOfFellowTravelers;x++)
                            {
                                String emp_id = jsonArrayFellowTravellers.getJSONObject(x).getString("emp_id");
                                String emp_name = jsonArrayFellowTravellers.getJSONObject(x).getString("emp_name");
                                String mobile_no = jsonArrayFellowTravellers.getJSONObject(x).getString("mobile_no");
                                String address = jsonArrayFellowTravellers.getJSONObject(x).getString("address");
                                String api_status = jsonArrayFellowTravellers.getJSONObject(x).getString("api_status");
                                arrayListTraveller.add(new TravelerDetail(emp_id,emp_name,mobile_no,address,api_status));
                            }
                        }

                        try
                        {
                            travellersCountId.setText("Travelers - "+String.valueOf(arrayListTraveller.size()));
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }

                        // 2 for pick up and 1 for drop



                        traveList.add(new TravelerDetail(traveltype, pickuptime, pickupaddress, pickuplat, pickuplon,
                                driver, car, carlat, carlon, tripid, targettime,
                                destination, destlat, destlon, otp, carid));
                    }

//                    String duration = (String) textView.getText();
                    AppController.mp.travelerDetail.add(new
                            TravelerDetail(sosnumber, sendupdateevery, traveList,arrayListTraveller));
                    if (!TelephonyManagerInfo.isConnectingToInternet(TravelerHomePage.this))
                    {
                        CustomPopup cp = new CustomPopup(TravelerHomePage.this);
                        cp.commonPopup(getResources().getString(R.string.internet_connection));
                    }
                    else
                    {
                        if (TravelerHomePage.tripFound.equals(""))
                        {
                            try
                            {
                                mHandler = new Handler();
                                mHandler.postDelayed(mRunnable, 1 * 1000);
                            }
                            catch (Exception e)
                            {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    try
                    {
                        tripFound = "no trip";
                        topRl.setVisibility(View.GONE);
                        travellerView.setVisibility(View.VISIBLE);
                        CustomPopup cp = new CustomPopup(TravelerHomePage.this);
                        cp.commonPopup("No Trip Found");
                        feedbackRl.setBackgroundColor(context.getResources().getColor(R.color.disable_button_color));
                        feedbackRl.setEnabled(false);
                        feedbackRl.setFocusable(false);

                        callDriverRl.setBackgroundColor(getResources().getColor(R.color.disable_button_color));
                        callDriverRl.setEnabled(false);
                        callDriverRl.setFocusable(false);
                        safeReach = 0;
                        isTripFound = 0;
                        sosRl.setBackgroundColor(context.getResources().getColor(R.color.disable_button_color));
                        sosRl.setEnabled(false);
                        sosRl.setFocusable(false);

//                        TravelerHomePage.safeReachRl.setVisibility(View.GONE);
                        TravelerHomePage.travelersRl.setVisibility(View.GONE);
                        TravelerHomePage.readyToBoard.setClickable(false);
                        TravelerHomePage.readyToBoard.setEnabled(false);
                        TravelerHomePage.readyToBoardRl.setBackgroundColor(getResources().getColor(R.color.disable_button_color));
                        readyToBoard.setVisibility(View.VISIBLE);

                    }
                    catch (Exception e1)
                    {
                        e.printStackTrace();
                    }
                }

            try
            {
//                Toast.makeText(activity,TravelerHomePage.tripId+" - "+ AppController.tDb.getString(Constant.DUPLICATE_TRIP_ID), Toast.LENGTH_LONG).show();
                if(TravelerHomePage.tripId!=null && !TravelerHomePage.tripId.equals(""))
                {
                    if(AppController.tDb.getString(Constant.DUPLICATE_TRIP_ID)!=null)
                    {
                        if(TravelerHomePage.tripId.equals(AppController.tDb.getString(Constant.DUPLICATE_TRIP_ID)))
                        {
                            feedbackRl.setBackgroundColor(context.getResources().getColor(R.color.disable_button_color));
                            feedbackRl.setEnabled(false);
                            feedbackRl.setFocusable(false);
                            callDriverRl.setBackgroundColor(getResources().getColor(R.color.disable_button_color));
                            callDriverRl.setEnabled(false);
                            callDriverRl.setFocusable(false);
                        }
                    }
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            String deviceid="";
            try
            {
                TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

                deviceid = telephonyManager.getDeviceId();

            }
            catch (SecurityException e)
            {
                e.printStackTrace();
            }
            catch (Exception e)
            {
                Log.e("","");
            }
            JSONObject root = new JSONObject();
            String userId = UserDetail.getInstance().getUserId();
            try
            {
              //  root.put("userid", userId);
                root.put("carid", TravelerHomePage.carId);
                root.put("tripid",TravelerHomePage.tripId);
              //  root.put("deviceid", deviceid);
            }
            catch (Exception e)
            {
               e.printStackTrace();
            }
            apiSataus = 4;
            if(ServerInterface.travelerLatLon)
                ServerInterface.getInstance(TravelerHomePage.activity).TraveRequest(userId, deviceid, UrlConfig.getCarUpdate, root.toString(),TravelerHomePage.this, false);

//            mHandler.postDelayed(mRunnable, 50*1000);

            mHandler.postDelayed(mRunnable, updateTime * 500);
        }
    };


    public void locationEnabledHandler() {
//        mHandler = new Handler();
//        mHandler.postDelayed(mRunnable, 2000);
    }

    public void sos(View v) {
        if(!tripFound.equals("")) {
            sosRl.setFocusable(false);
            sosRl.setEnabled(false);

            return;
        }

        apiSataus = 2;
        JSONObject sosObj = new JSONObject();
        String userId = UserDetail.getInstance().getUserId();
        String deviceId="";
        try {
            deviceId = TelephonyManagerInfo.getIMEI(activity);
        }catch (Exception e)
        {}
        try {
            sosObj.put("tripid", tripId);
            sosObj.put("carid", carId);
            sosObj.put("lat", String.valueOf(latSos));
            sosObj.put("lon", String.valueOf(lonSos));

        }catch (Exception e)
        {

        }

        if(TelephonyManagerInfo.isConnectingToInternet(TravelerHomePage.this))
        {
            sosIcon.setVisibility(View.VISIBLE);
            String url = UrlConfig.t_SOS;
            ServerInterface.getInstance(activity).makeServiceCall(userId, deviceId, url, sosObj.toString(), TravelerHomePage.this, true, "Please wait...");
        }

        try
        {
            sosIcon.setVisibility(View.VISIBLE);
            callDeskNum = sosNum ;
            Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + callDeskNum));
            startActivity(dialIntent);

        }
        catch (SecurityException e)
        {
            e.printStackTrace();
        }
        catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "Could not find an activity to place the call.", Toast.LENGTH_SHORT).show();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
       // }
    }


    public void feedback(View v)
    {
        Intent intent = new Intent(this,FeedBackActivity.class);
        intent.putExtra("isTripComplete", false);
        startActivity(intent);
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (resultCode  == RESULT_OK )
        {
            if(requestCode == 111)
            {
                isGPS = true;
            }
        }
        else
        {
            closeAppWarning();
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void closeAppWarning()
    {
        new AlertDialog.Builder(this)
                .setTitle("GPS Alert !")
                .setMessage("Your GPS is disabled.\nTo use this app location service is mandatory.\n\nTo turn on GPS click on OK")
                .setNegativeButton("Close App", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                        System.exit(0);
                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        displayPromptForEnablingGPS();
                    }
                }).create().show();
    }




    public void displayPromptForEnablingGPS()
    {

        new GpsUtils(TravelerHomePage.this).turnGPSOn(new GpsUtils.onGpsListener()
        {
            @Override
            public void gpsStatus(boolean isGPSEnable)
            {
                // turn on GPS
                isGPS = isGPSEnable;
            }
        });
//        final AlertDialog.Builder builder =  new AlertDialog.Builder(activity);
//        final String action = Settings.ACTION_LOCATION_SOURCE_SETTINGS;
//        final String message = "Do you want open GPS setting?";
//
//        builder.setMessage(message)
//                .setPositiveButton("OK",
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface d, int id) {
//                                startActivity(new Intent(action));
//                                d.dismiss();
//                            }
//                        })
//                .setNegativeButton("Cancel",
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface d, int id) {
//                                d.cancel();
//                            }
//                        });
//        builder.create().show();
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.pastTripsId)
        {
            startActivity(new Intent(TravelerHomePage.this,PastTripsActivity.class));
        }

        else if (id == R.id.lay4) {
             sendFeedback();
        } else if (id == R.id.lay5) {
            Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=com.vistara.traveler"); //+ ((HomeActivity)mContext).getPackageName());
            Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
            try {
                startActivity(myAppLinkToMarket);
            } catch (ActivityNotFoundException e) {
            }
        }   if (id == R.id.lay6) {

            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.airvistara.com"));
            startActivity(browserIntent);
        }
        else if (id == R.id.lay9) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.airvistara.com"));
            startActivity(browserIntent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void sendFeedback()
    {
        apiSataus = 5;
        JSONObject dataObj = new JSONObject();
        try {
            String userType = AppController.tDb.getString(Constant.userType);
            String userId = AppController.tDb.getString(Constant.userId);
            dataObj.put("userid", userId);
            dataObj.put("type", userType);

            CustomPopup feedback = new CustomPopup(TravelerHomePage.this);
            feedback.feebackPopup(dataObj.toString(), TravelerHomePage.this);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    public void drawCarMarker(double lastlat, double lastLon, double currentLat, double currentLon)
    {
        LatLng origin = new LatLng(lastlat, lastLon);
        LatLng dest   = new LatLng(currentLat, currentLon);

//        LatLng origin = new LatLng(28.457523, 77.026344);
//        LatLng dest = new LatLng(28.6527809, 77.1921441);
        String url = getDirectionsUrl(origin, dest);
        DownloadTask downloadTask = new DownloadTask();
        downloadTask.execute(url);
    }


    private String getDirectionsUrl(LatLng origin,LatLng dest){

        String str_origin = "origin="+origin.latitude+","+origin.longitude;
        String str_dest = "destination="+dest.latitude+","+dest.longitude;
        String sensor = "sensor=false";


        String waypoints1 = "";
        String waypoints = "waypoints=";
//        for(int i=2;i<markerPoints.size();i++){
//            LatLng point  = (LatLng) markerPoints.get(i);
//            if(i==2)
//                waypoints = "waypoints=";
//            waypoints += point.latitude + "," + point.longitude + "|";
//        }

        String parameters = str_origin+"&"+str_dest+"&"+sensor+"&"+waypoints;
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;

        return url;
    }






    //** A method to download json data from url *//*
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuffer sb  = new StringBuffer();
            String line = "";
            while( ( line = br.readLine())  != null){
                sb.append(line);
            }

            data = sb.toString();
            JSONObject jsonObject = new JSONObject(data);
            String travelTime = jsonObject.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("duration").getString("text");
            AppController.mp.travelTime = travelTime;
            textView.setText(travelTime);
            br.close();

        }catch(Exception e){
//            Log.d("Exception while downloading", e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    // Fetches data from url passed
    private class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {
            String data = "";

            try{
                data = downloadUrl(url[0]);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            ParserTask parserTask = new ParserTask();
            parserTask.execute(result);
        }
    }

    //** A class to parse the Google Places in JSON format *//*
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >{

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try{
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();
                routes = parser.parse(jObject);
            }catch(Exception e){
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {

            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;

            for(int i=0;i<result.size();i++){
                points = new ArrayList<LatLng>();

                List<HashMap<String, String>> path = result.get(i);

                for(int ii=0; ii<path.size(); ii++)
                {
                    HashMap<String, String> point = path.get(ii);
                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);
                    points.add(position);
                }

                animateMarker(CabFragment.googleMap, points);
            }

        }
    }


    LatLng ll = null;
    private void animateMarker(GoogleMap myMap, final List<LatLng> directionPoint) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final long duration = 30000;

        final Interpolator interpolator = new LinearInterpolator();
     //   Toast.makeText(activity, ""+directionPoint.size(), Toast.LENGTH_LONG).show();
        handler.post(new Runnable() {
            int i = 0;
            @Override
            public void run() {
           long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed
                        / duration);
                if (i < directionPoint.size()) {
                    if(ll!=null) {
                        double sss = bearingBetweenLocations(ll, (directionPoint.get(i)));
                        float f = (float)sss;
                        rotateMarker(finalMarker, f);
                    }

                    finalMarker.setPosition(directionPoint.get(i));
                    finalMarker.setFlat(true);
                    ll = directionPoint.get(i);
                }
                i++;
                if(i<directionPoint.size())
                     handler.postDelayed(this, 16);

            }
        });
    }


    public double bearingBetweenLocations(LatLng latLng1,LatLng latLng2) {

        double PI = 3.14159;
        double lat1 = latLng1.latitude * PI / 180;
        double long1 = latLng1.longitude * PI / 180;
        double lat2 = latLng2.latitude * PI / 180;
        double long2 = latLng2.longitude * PI / 180;

        double dLon = (long2 - long1);

        double y = Math.sin(dLon) * Math.cos(lat2);
        double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1)
                * Math.cos(lat2) * Math.cos(dLon);

        double brng = Math.atan2(y, x);

        brng = Math.toDegrees(brng);
        brng = (brng + 360) % 360;

        return brng;
    }


    boolean isMarkerRotating = false;
    private void rotateMarker(final Marker marker, final float toRotation) {
        if(!isMarkerRotating) {
            final Handler handler = new Handler();
            final long start = SystemClock.uptimeMillis();
            final float startRotation = marker.getRotation();
            final long duration = 1000;

            final Interpolator interpolator = new LinearInterpolator();

            handler.post(new Runnable() {
                @Override
                public void run() {
                    isMarkerRotating = true;

                    long elapsed = SystemClock.uptimeMillis() - start;
                    float t = interpolator.getInterpolation((float) elapsed / duration);

                    float rot = t * toRotation + (1 - t) * startRotation;

                    marker.setRotation(-rot > 180 ? rot / 2 : rot);
                    marker.setFlat(true);

                }
            });
        }
    }



    public void versionCheck() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
        builder1.setMessage("New App version is available. Please update your App.");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        final String appPackageName = getPackageName();
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                        } catch (android.content.ActivityNotFoundException anfe) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                        }
                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }


    public void completeTrip(){
        try {
            tripFound = "no trip";
            topRl.setVisibility(View.GONE);
            travellerView.setVisibility(View.VISIBLE);

            feedbackRl.setBackgroundColor(context.getResources().getColor(R.color.disable_button_color));
            feedbackRl.setEnabled(false);
            feedbackRl.setFocusable(false);
            callDriverRl.setBackgroundColor(getResources().getColor(R.color.disable_button_color));
            callDriverRl.setEnabled(false);
            callDriverRl.setFocusable(false);
            safeReach = 0;
            CabFragment.googleMap.clear();
            mHandler.removeCallbacks(mRunnable);
            sosRl.setBackgroundColor(context.getResources().getColor(R.color.disable_button_color));
            sosRl.setEnabled(false);
            sosRl.setFocusable(false);

//            TravelerHomePage.safeReachRl.setVisibility(View.GONE);
            TravelerHomePage.readyToBoard.setVisibility(View.VISIBLE);
            TravelerHomePage.readyToBoard.setClickable(false);
            TravelerHomePage.readyToBoard.setEnabled(false);
            TravelerHomePage.readyToBoardRl.setBackgroundColor(getResources().getColor(R.color.disable_button_color));
            travelersRl.setVisibility(View.GONE);
            return;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch(keyCode){
            case KeyEvent.KEYCODE_BACK:

                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

//                moveTaskToBack(true);
//                android.os.Process.killProcess(android.os.Process.myPid());
//                System.exit(0);

                return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Exit  !")
                .setMessage("Are you sure you want to exit ?")
                .setNegativeButton("NO", null)
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        finish();
                        System.exit(0);
                    }
                }).create().show();
    }





    private String getPathUrl(LatLng origin, LatLng dest)
    {
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        String sensor = "sensor=false";
        String mode = "mode=driving";
        String transitMode = "transit_mode=bus";
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode + "&" +transitMode;
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=AIzaSyA3Jws4Ms5aCM8ubMsuCaROnfw_8W2qkXs";

        return url;

    }

    private class FetchUrlForRoute extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
                Log.d("Background Task data", data.toString());
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTaskForDrawRoute parserTaskForDrawRoute = new ParserTaskForDrawRoute();
            parserTaskForDrawRoute.execute(result);

        }
    }

    private class ParserTaskForDrawRoute extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {
            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try
            {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();
                routes = parser.parse(jObject);
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points;

            try
            {
                for (int i = 0; i < result.size(); i++)
                {
                    points = new ArrayList<>();
                    lineOptions = new PolylineOptions();

                    // Fetching i-th route
                    List<HashMap<String, String>> path = result.get(i);

                    // Fetching all the points in i-th route
                    for (int j = 0; j < path.size(); j++)
                    {
                        HashMap<String, String> point = path.get(j);

                        double lat = Double.parseDouble(point.get("lat"));
                        double lng = Double.parseDouble(point.get("lng"));
                        LatLng position = new LatLng(lat, lng);

                        points.add(position);
                    }

                    // Adding all the points in the route to LineOptions
                    lineOptions.addAll(points);
                    lineOptions.width(8);
                    lineOptions.color(R.color.colorPrimaryDark);
//                    lineOptions.color("#46143c");

                    Log.d("onPostExecute","onPostExecute lineoptions decoded");
                }

                // Drawing polyline in the Google Map for the i-th route
                if(lineOptions != null && CabFragment.googleMap!=null)
                {
                    textView.setText(AppController.mp.travelTime);
                    routeDraw = true ;

                    CabFragment.googleMap.addPolyline(lineOptions);
                }
                else
                {
                    Log.d("onPostExecute","without Polylines drawn");
                    routeDraw = false ;

                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
                routeDraw = false ;

            }


        }
    }




    public Bitmap frameLayoutToBitmap(View view) {
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());

        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Drawable background = view.getBackground();
        if (background != null) {
            background.draw(canvas);
        }
        view.draw(canvas);
        return bitmap;
    }


}
