package com.vistara.traveler;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.vistara.traveler.LocationService.GPSTracker;
import com.vistara.traveler.LocationService.GpsUtils;
import com.vistara.traveler.LocationService.LoationService;
import com.vistara.traveler.ServerFile.ServerInterface;
import com.vistara.traveler.ServerFile.UrlConfig;
import com.vistara.traveler.model.DDriverDetail;
import com.vistara.traveler.model.DTravellerStatus;
import com.vistara.traveler.receivers.ArrivingButtonReciever;
import com.vistara.traveler.receivers.BatteryRecever;
import com.vistara.traveler.singlton.UserDetail;
import com.vistara.traveler.utils.ApiResponseParser;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static java.lang.Math.acos;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.toRadians;

public class DriverHomePage extends DrawerBaseActvity implements ApiResponseParser ,
        NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback{

    //public MapView mMapView;
    public static GoogleMap googleMap;
    public static Activity activity;
    public static ApiResponseParser apiResActivity;
    public static Context context;
    public static Button waiting;
    Button reported;
    Button pickedUpButton;
    public static Button arrivingButton;
    ImageView gpsImage;
    RelativeLayout cross;
    static TextView pickUp;
    TextView onlineTv, driverName, reportingAdress,reportingTime,gaadiNum,tripId;
    TextView travelerName,travelerDetail,travePickUpTime;
    static RelativeLayout travelerBottomRl, topRl;
    RelativeLayout buttonLay, nextTrip, welcomeRl;
    public static boolean isMyTrackingOn=true;
    public static int apiSataus = 1;                 // 1 for app data, 2 for sos, 3 for complete trip, 4 for witing, 5 for not reported,
                                        // 6 for picked up, 7 for feedback, 8 for check arriving

    public static ImageView batteryImage;
    EditText travellerOtp;
    TextView userName, tripCompleteTv,mobileNum,versionNumber;
    static RelativeLayout tripComp, tripCompRl;
//    LatLng currentPositionForChage;
    public static ArrayList<String>  pickesEmpStatus;
    public static ArrayList<String>  notReportedEmpStatus;


    public static String firstCarId="";
    public static String firstTripId="";
    String empCode="";
    public static String empId="";
    LatLng latLan;
    String otp;
    int travellerCount;
    static RelativeLayout soscall;
    static RelativeLayout callDeskRl;
    ImageView sosIcon;
    static int triCompleteSign = 1;
    boolean nextTripCheck = false;
    double destLat;
    double destLon;
    GradientDrawable bgShape3;
    public static List<DDriverDetail> tripListMarker;                      // trip list for adress list activity
    public static HashMap<String, LatLng> markerPositio;
    String userAppVersion = "";
    String latestVersion = "";

    public static String destinationLat, destinationLon;
    boolean isGPS = false;

    String callNo ;
  //  public static ImageView central_marker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.home_fragment); //Remember this is the FrameLayout area within your activity_main.xml
        getLayoutInflater().inflate(R.layout.driver_home, contentFrameLayout);

        MapsInitializer.initialize(DriverHomePage.this.getApplicationContext());
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(DriverHomePage.this);


        context = DriverHomePage.this;
        tripListMarker = new ArrayList<>();
        markerPositio = new HashMap<>();
        ImageView iv = (ImageView) toolbar.findViewById(R.id.notifiClik);
        ImageView imageViewRefreshId = (ImageView) toolbar.findViewById(R.id.imageViewRefreshId);
        imageViewRefreshId.setVisibility(View.VISIBLE);
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DriverHomePage.this, NotificationActivity.class);
                startActivity(intent);
            }
        });

        imageViewRefreshId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                try
                {
                    String lastTripData = AppController.tDb.getString(Constant.lastTripData);
                    JSONObject jObj = new JSONObject(lastTripData);
                    String tripStatus = jObj.getString("trip_complete");

//                    if(tripStatus.equals("1"))
                        DriverHomePage.sendMessageToActivity(2);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    try
                    {
                        DriverHomePage.sendMessageToActivity(2);
                    }
                    catch (Exception e1)
                    {
                        e1.printStackTrace();
                    }


                }
            }
        });




        tripComp            = (RelativeLayout) findViewById(R.id.tripComp);
        soscall             =  (RelativeLayout) findViewById(R.id.soscall);
        waiting             = (Button)  findViewById(R.id.waitingButton);
        reported            = (Button)  findViewById(R.id.notRepordButton);
        pickedUpButton      = (Button)  findViewById(R.id.pickedUpButton);
        arrivingButton      = (Button)  findViewById(R.id.arrivingButton);
        gpsImage            = (ImageView)  findViewById(R.id.gpsImage);
        batteryImage        = (ImageView)  findViewById(R.id.batteryImage);
        onlineTv            = (TextView)  findViewById(R.id.internetText);
        cross               = (RelativeLayout)  findViewById(R.id.cross);
        travelerBottomRl    = (RelativeLayout) findViewById(R.id.travelerBottomRl);
        driverName          = (TextView)  findViewById(R.id.driverName);
        pickUp              = (TextView)  findViewById(R.id.pickUp);
        reportingAdress     = (TextView)  findViewById(R.id.reportingAdress);
        reportingTime       = (TextView)  findViewById(R.id.reportingTime);
        gaadiNum            = (TextView)  findViewById(R.id.gaadiNum);
        travelerName        = (TextView)  findViewById(R.id.travelerName);
        travelerDetail      = (TextView)  findViewById(R.id.travelerDetail);
        tripId              = (TextView)  findViewById(R.id.tripId);
        buttonLay           = (RelativeLayout)  findViewById(R.id.buttonLay);
        tripCompRl          = (RelativeLayout)  findViewById(R.id.tripCompRl);
        topRl               = (RelativeLayout)  findViewById(R.id.topRl2);
        nextTrip            = (RelativeLayout)  findViewById(R.id.nextTrip);
        welcomeRl           = (RelativeLayout)  findViewById(R.id.welcomeRl);
        tripCompleteTv      = (TextView)  findViewById(R.id.tripCompleteTv);
        travePickUpTime     = (TextView) findViewById(R.id.traveTime);
        sosIcon             = (ImageView) findViewById(R.id.sosIcon);
        callDeskRl          = (RelativeLayout)  findViewById(R.id.callDeskRl);

        getSupportActionBar().setTitle("Home");
        pickesEmpStatus = new ArrayList<>();
        notReportedEmpStatus = new ArrayList<>();
        if(!isMyServiceRunning(LoationService.class)) {
            startService(new Intent(this, LoationService.class));
        }
//        else
//            LoationService.finalMarker = null;

        bgShape3 = (GradientDrawable)arrivingButton.getBackground();
        bgShape3.setColor(getResources().getColor(R.color.yellow));

        GradientDrawable bgShape = (GradientDrawable)waiting.getBackground();
        bgShape.setColor(getResources().getColor(R.color.yellow));

        GradientDrawable bgShape1 = (GradientDrawable)reported.getBackground();
        bgShape1.setColor(getResources().getColor(R.color.light_red));

        GradientDrawable bgShape2 = (GradientDrawable)pickedUpButton.getBackground();
        bgShape2.setColor(getResources().getColor(R.color.green));

        //mMapView.onCreate(savedInstanceState);
        activity = DriverHomePage.this;
        apiResActivity = DriverHomePage.this;
        NavigationView navigationView = (NavigationView) findViewById(R.id.nvView);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        userName = (TextView) header.findViewById(R.id.userName);
        mobileNum = (TextView) header.findViewById(R.id.mobileNum);
        versionNumber = (TextView) header.findViewById(R.id.versionNumber);
        String name = AppController.tDb.getString(Constant.userNmae);
        String id   = UserDetail.getInstance().getUserId();
        userName.setText(name+" ("+id+")");
        mobileNum.setText(AppController.tDb.getString(Constant.varify_mobile));

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

        BatteryRecever batteryRecever = new BatteryRecever();
        IntentFilter iFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        getApplicationContext().registerReceiver(batteryRecever,iFilter);


        final String userData = UserDetail.getInstance().getUserData();
        if (userData != null && !userData.equals(""))
        {
            parseData(userData);   // deep
        }
        else
        {
            tripComp.setVisibility(View.VISIBLE);
            soscall.setFocusable(false);
            soscall.setEnabled(false);
            soscall.setBackgroundColor(getResources().getColor(R.color.disable_button_color));
            tripCompleteTv.setText("NO TRIP ASSIGNED");
            tripCompRl.setClickable(false);
            tripCompRl.setFocusable(false);
            tripCompRl.setBackgroundColor(getResources().getColor(R.color.disable_button_color));
        }

        changeIconRunTime();
        cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                travelerBottomRl.setVisibility(View.GONE);
                googleMap.getUiSettings().setZoomControlsEnabled(true);
            }
        });


        tripId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DriverHomePage.this, AddressListActivity.class);
                intent.putExtra("navigateFrom","1");
                intent.putExtra("lat",destinationLat);
                intent.putExtra("lon",destinationLon);
                startActivity(intent);
            }
        });

        callDeskRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try
                {

                    String callDeskNum = AppController.tDb.getString(Constant.callDeskNum);
                    callNo = callDeskNum ;

                    Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + callNo));
                    startActivity(dialIntent);

                }
                catch (SecurityException e)
                {
                    e.printStackTrace();
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        });



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


                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
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

    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        DriverHomePage.this.googleMap = googleMap;
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        double lat;
        double lon;
        try {
            GPSTracker gps = new GPSTracker(activity);
            lat = gps.getLatitude();
            lon = gps.getLongitude();
            Log.e("","");

        }catch(Exception e) {
            lat = 28.644800;
            lon = 77.216721;
            e.printStackTrace();
        }

        LatLng currentPosition = new LatLng(lat,  lon);
        CameraPosition cameraPosition = null;
        cameraPosition = new CameraPosition.Builder().target(currentPosition).zoom(10).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(currentPosition);
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.car_icon));
        googleMap.setMyLocationEnabled(true);


        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener()
        {
            @Override
            public boolean onMarkerClick(Marker arg0) {

                if(triCompleteSign == 0) {                                 // if trip complete then disable marker click
                    return true;
                }

                latLan = arg0.getPosition();
                try
                {
                    String ids = arg0.getTag().toString();
                    JSONObject jsonObj = new JSONObject(ids);
                    String id = jsonObj.getString("id");
                    String name = jsonObj.getString("name");
                    String empcode = jsonObj.getString("empcode");
                    String address = jsonObj.getString("address");
                    String pickuptime = jsonObj.getString("pickuptime");
                    String loggedin = jsonObj.getString("loggedin");
                    String lat = jsonObj.getString("lat");
                    String lon = jsonObj.getString("lon");
                    otp = jsonObj.getString("otp");
                    empId = id;
                    empCode = empcode;
                    DriverHomePage.this.googleMap.getUiSettings().setZoomControlsEnabled(false);
                    travelerBottomRl.setVisibility(View.VISIBLE);
                    travelerName.setText(name);
                    travelerDetail.setText(address);
                    travePickUpTime.setText(pickuptime);

                    if(AppController.tDb.getString(Constant.tripType).equals("1"))
                        travePickUpTime.setVisibility(View.GONE);
                    else
                        travePickUpTime.setVisibility(View.VISIBLE);

                    if(pickesEmpStatus.size()>0)
                    {
                        for(int i=0; i<pickesEmpStatus.size(); i++)
                        {
                            if(pickesEmpStatus.get(i).equals(empId))
                            {
                                buttonLay.setVisibility(View.GONE);
                                break;
                            }else
                                buttonLay.setVisibility(View.VISIBLE);
                        }
                    }else {
                        if (loggedin.equals("3"))
                            buttonLay.setVisibility(View.GONE);
                        else
                            buttonLay.setVisibility(View.VISIBLE);
                    }


                    ArrayList<DTravellerStatus> statusList = (ArrayList) AppController.tDb.getListObject(Constant.dTravellerStatus, DTravellerStatus.class);
                    if(statusList.size()>0)
                    {
                        for(int i=0; i<statusList.size(); i++)
                        {
                            if(statusList.get(i).getTravellerId().equals(empId))
                            {
                                if(statusList.get(i).getTravellerStatus().equals("1"))
                                {
                                   // arrivingButton.setBackgroundColor(getResources().getColor(R.color.disable_button_color));
                                   // bgShape3.setCornerRadius(5);
                                    bgShape3.setColor(getResources().getColor(R.color.disable_button_color));
                                    bgShape3.setCornerRadius(5);
                                    arrivingButton.setBackground(bgShape3);
                                    arrivingButton.setVisibility(View.VISIBLE);
                                    arrivingButton.setEnabled(false);
                                    arrivingButton.setFocusable(false);
                                    waiting.setVisibility(View.GONE);
                                }else
                                {
                                    waiting.setVisibility(View.VISIBLE);
                                    arrivingButton.setVisibility(View.GONE);
                                }

                                break;
                            }

                            if(statusList.size()==(i+1))
                            {
                                arrivingButton.setEnabled(true);
                                arrivingButton.setFocusable(true);
                                arrivingButton.setVisibility(View.VISIBLE);
                                waiting.setVisibility(View.GONE);
                                arrivingButton.setBackgroundColor(getResources().getColor(R.color.yellow));
                            }

                        }
                    }

                }catch(Exception e)
                {

                }
                return true;
            }

        });

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {


        if(!TelephonyManagerInfo.gpsCheck(activity)) {
            gpsImage.setBackgroundResource(R.drawable.gps);
            gpsImage.setVisibility(View.VISIBLE);
            // displayPromptForEnablingGPS(activity);
            UserDetail.getInstance().setGpsStaus("0");
//            closeAppWarning();
        }
        else {
            gpsImage.setVisibility(View.GONE);
            UserDetail.getInstance().setGpsStaus("1");
        }

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
        new GpsUtils(DriverHomePage.this).turnGPSOn(new GpsUtils.onGpsListener()
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
////        final String action = Settings.ACTION_LOCATION_SOURCE_SETTINGS;
//        final String message = "Your GPS is disabled\n\nClick on OK to go to GPS setting and enable it.";
////        final String message = "Do you want open GPS setting?";
//
//        builder.setMessage(message)
//                .setPositiveButton("OK",
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(final DialogInterface d, int id) {
//                               // startActivity(new Intent(action));
////                                startActivityForResult(new Intent(action), 111);
//
//                                d.dismiss();
//                            }
//                        })
//                .setNegativeButton("Close App",
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface d, int id) {
//                                d.cancel();
//                                System.exit(1);
//                            }
//                        });
//        Dialog dialog = builder.create();
//        dialog.show();
//        dialog.setCanceledOnTouchOutside(false);
    }

    public void changeIconRunTime()
    {
        if(!TelephonyManagerInfo.gpsCheck(activity)) {
            gpsImage.setBackgroundResource(R.drawable.gps);
            gpsImage.setVisibility(View.VISIBLE);
            displayPromptForEnablingGPS();
            UserDetail.getInstance().setGpsStaus("0");
        }
        else {
            gpsImage.setVisibility(View.GONE);
            UserDetail.getInstance().setGpsStaus("1");
        }


        if(TelephonyManagerInfo.isConnectingToInternet(activity)) {
            onlineTv.setVisibility(View.GONE);
        }
        else {
            onlineTv.setVisibility(View.VISIBLE);
            onlineTv.setText("No Internet");
            onlineTv.setTextColor(Color.parseColor("#FF0000"));
        }

    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.pastTripsId)
        {
            startActivity(new Intent(DriverHomePage.this,PastTripsActivity.class));
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

    @Override
    protected void onResume() {
        super.onResume();
    //    mMapView.onResume();
     //   DriverHomePage.googleMap.rem

        try {
            LoationService.finalMarker.remove();
        }catch (Exception e)
        {

        }
        LoationService.finalMarker = null;
    }

    Handler mHandler;
    private PendingIntent pendingIntent;
    private AlarmManager manager;
    public void arriving(View v)
    {
        apiSataus = 8;
        String deviceId="";
        String userId = UserDetail.getInstance().getUserId();
        try {
            deviceId = TelephonyManagerInfo.getIMEI(activity);
        }catch (Exception e)
        {}
        if(TelephonyManagerInfo.isConnectingToInternet(activity)) {
            JSONObject data = new JSONObject();
            try {
            //    data.put("userid", UserDetail.getInstance().getUserId());
                data.put("carid", firstCarId);
                data.put("tripid", firstTripId);
                data.put("emp_id", empCode);
                data.put("empcode", "");
                data.put("status", "4");
            }catch (Exception e)
            {

            }
            ServerInterface.getInstance(activity).makeServiceCall(userId, deviceId, UrlConfig.updateTravellerStatus, data.toString(),
                    DriverHomePage.this, true, "Please wait...");

        }else
        {
            CustomPopup cp = new CustomPopup(this);
            cp.commonPopup(getResources().getString(R.string.internet_connection));
        }
    }

    private Runnable mRunnable = new Runnable() {

        @Override
        public void run() {
                try {
                    try {
                       activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                         //       Toast.makeText(DriverHomePage.this, "start runnable", Toast.LENGTH_LONG).show();
                                waiting.setVisibility(View.VISIBLE);
                                arrivingButton.setVisibility(View.GONE);
                            }
                        });

                    } catch (SecurityException e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
         //   mHandler.postDelayed(mRunnable, 1000*5);
        }
    };


    public void waiting(View v)
    {
        apiSataus = 4;

        String deviceId="";
        try {
            deviceId = TelephonyManagerInfo.getIMEI(activity);
        }catch (Exception e)
        {}
        String userId = UserDetail.getInstance().getUserId();
        JSONObject data = new JSONObject();
        try {
       //     data.put("userid", UserDetail.getInstance().getUserId());
            data.put("carid", firstCarId);
            data.put("tripid", firstTripId);
            data.put("emp_id", empCode);
            data.put("empcode", "");
            data.put("status", "1");
        }catch (Exception e)
        {

        }

        if(TelephonyManagerInfo.isConnectingToInternet(DriverHomePage.this)) {
            ServerInterface.getInstance(activity).makeServiceCall(userId, deviceId, UrlConfig.updateTravellerStatus, data.toString(),
                    DriverHomePage.this, true, "Please wait...");
        }else
        {
            CustomPopup cp = new CustomPopup(DriverHomePage.this);
            cp.commonPopup(getResources().getString(R.string.internet_connection));
        }

    }

    public void notReported(View v)
    {
        apiSataus = 5;
        String deviceId="";
        try {
            deviceId = TelephonyManagerInfo.getIMEI(activity);
        }catch (Exception e)
        {}

        String userId = UserDetail.getInstance().getUserId();
        JSONObject data = new JSONObject();
        try {
         //   data.put("userid", UserDetail.getInstance().getUserId());
            data.put("carid", firstCarId);
            data.put("tripid", firstTripId);
            data.put("emp_id", empCode);
            data.put("empcode", "");
            data.put("status", "2");
        }catch (Exception e)
        {
        }
        ServerInterface.getInstance(activity).makeServiceCall(userId, deviceId, UrlConfig.updateTravellerStatus, data.toString(),
                DriverHomePage.this, true, "Please wait...");
    }

    public void pickUp(View v) {
        final Dialog dialog1 = new Dialog(activity);
        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog1.setContentView(R.layout.common_dialog);
        dialog1.setCanceledOnTouchOutside(true);
        RelativeLayout rl = (RelativeLayout)dialog1. findViewById(R.id.ok);
        TextView tv2 = (TextView)dialog1. findViewById(R.id.tv2);
        tv2.setText("I got cab");
        TextView textView2 = (TextView)dialog1. findViewById(R.id.textView2);
        textView2.setText("OK");
        tv2.setVisibility(View.GONE);
        travellerOtp = (EditText)dialog1.  findViewById(R.id.travellerOtp);
        travellerOtp.setVisibility(View.VISIBLE);
        rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dialog1.dismiss();

                apiSataus = 6;
                String value = travellerOtp.getText().toString();

                String otpLowerCase = otp.toLowerCase();
                String valueLowerCase="";
                if(!value.equals(""))
                   valueLowerCase = value.toLowerCase();

                if(valueLowerCase.equals(""))
                {
                    Toast.makeText(DriverHomePage.this, "Please enter traveller OTP", Toast.LENGTH_LONG).show();
                    return;
                }else if(!valueLowerCase.equals(otpLowerCase))
                {
                    Toast.makeText(DriverHomePage.this, "Please enter correct OTP", Toast.LENGTH_LONG).show();
                    return;
                }

                JSONObject data = new JSONObject();
                String deviceId="";
                try {
                    deviceId = TelephonyManagerInfo.getIMEI(activity);
                }catch (Exception e)
                {}

                String userId = UserDetail.getInstance().getUserId();
                try {
              //      data.put("userid", UserDetail.getInstance().getUserId());
                    data.put("carid", firstCarId);
                    data.put("tripid", firstTripId);
                    data.put("emp_id", empCode);
                    data.put("empcode", value);
                    data.put("status", "3");
                }catch (Exception e)
                {
                }

                if(TelephonyManagerInfo.isConnectingToInternet(DriverHomePage.this)) {
                    ServerInterface.getInstance(activity).makeServiceCall(userId, deviceId, UrlConfig.updateTravellerStatus, data.toString(),
                            DriverHomePage.this, true, "Please wait...");
                }else
                {
                    try {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm");
                        Date dd = new Date(System.currentTimeMillis());
                        String ddStr = dateFormat.format(dd);
                        data.put("date_time", ddStr);
                    }catch (Exception e)
                    {

                    }

                    try {
                        ArrayList<String> pickedTravList = AppController.tDb.getListString(Constant.pickedTravObj);
                        pickedTravList.add(data.toString());
                        AppController.tDb.putListString(Constant.pickedTravObj, pickedTravList);
                        pickesEmpStatus.add(empId);          // for current employee picked status
                        buttonLay.setVisibility(View.GONE);


                        LatLng  currentPositionForChage = markerPositio.get(empId);
                        if(currentPositionForChage !=null && !currentPositionForChage.equals("")) {
                            try {
                                MarkerOptions markerOptions = new MarkerOptions();
                                markerOptions.position(currentPositionForChage);
                                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.man_green));
                                googleMap.addMarker(markerOptions);
                            }catch (Exception e)
                            {}
                        }

                    }catch (Exception e) {}
                }


            }
        });

        RelativeLayout cancel = (RelativeLayout)dialog1. findViewById(R.id.cancel);
        cancel.setVisibility(View.GONE);
        dialog1.show();
    }


    public void sos(View v)
    {
        apiSataus = 2;
        JSONObject sosObj = new JSONObject();
        try
        {
            double lat = AppController.tDb.getDouble(Constant.lat,0.0);
            double lon = AppController.tDb.getDouble(Constant.lon,0.0);

          //  String deviceid = TelephonyManagerInfo.getIMEI(activity);
          //  sosObj.put("userid", UserDetail.getInstance().getUserId());
            sosObj.put("tripid", firstTripId);
            sosObj.put("carid", firstCarId);
        //    sosObj.put("deviceid", deviceid);
            sosObj.put("lat", String.valueOf(lat));
            sosObj.put("lon", String.valueOf(lon));
        }
        catch (Exception e)
        {
           e.printStackTrace();
        }

        if(TelephonyManagerInfo.isConnectingToInternet(DriverHomePage.this))
        {
            String deviceId="";
            try
            {
                deviceId = TelephonyManagerInfo.getIMEI(activity);
            }
            catch (Exception e)
            {

            }
            callNo = sosNum ;
            String userId = UserDetail.getInstance().getUserId();
            sosIcon.setVisibility(View.VISIBLE);
            String url = UrlConfig.d_SOS;
            ServerInterface.getInstance(activity).makeServiceCall(userId, deviceId, url, sosObj.toString(), DriverHomePage.this, true, "Please wait...");
        }
        else
        {
            sosIcon.setVisibility(View.VISIBLE);
//            Intent in = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+sosNum));
            try
            {
                callNo = sosNum ;
                Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + callNo));
                startActivity(dialIntent);
            }
            catch (android.content.ActivityNotFoundException ex)
            {
                Toast.makeText(this, "Could not find an activity to place the call.", Toast.LENGTH_SHORT).show();
            }
        }

    }

//    public void callPhoneNumber()
//    {
//        try
//        {
//            if(Build.VERSION.SDK_INT > 22)
//            {
//                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED)
//                {
//                    // TODO: Consider calling
//                    ActivityCompat.requestPermissions(DriverHomePage.this, new String[]{android.Manifest.permission.CALL_PHONE}, 101);
//                    return;
//                }
//
//                Intent callIntent = new Intent(Intent.ACTION_CALL);
//                callIntent.setData(Uri.parse("tel:" + callNo));
//                startActivity(callIntent);
//            }
//            else
//            {
//                Intent callIntent = new Intent(Intent.ACTION_CALL);
//                callIntent.setData(Uri.parse("tel:" + callNo));
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


    public void tripCompleted(View v)
    {
        apiSataus = 3;

        // Added by Vijay
        boolean isShowPopup = true;
//        for (int i = 0; i < AppController.mp.driverDetail.size(); i++)
//        {
//            List<DDriverDetail> tripListDetail = AppController.mp.driverDetail.get(i).tripDetail;
//            for (int j = 0; j < tripListDetail.size(); j++)
//            {
//                DDriverDetail travelerDetail = tripListDetail.get(j);
//                for (int k = 0; k < travelerDetail.traverDeatil.size(); k++)
//                {
//                    String loggedIn = travelerDetail.traverDeatil.get(k).loggedin.toString();
//                    if (loggedIn.equals("0") || loggedIn.equals("4"))
//                    {
//                        isShowPopup = false;
//                        break;
//                    }
//                }
//                if(!isShowPopup)
//                {
//                    break;
//                }
//            }
//            if(!isShowPopup)
//            {
//                break;
//            }
//        }
//
//        if(isShowPopup)
//        {
            final JSONObject tripCompleteObj = new JSONObject();
            try {

                new AlertDialog.Builder(this)
                        .setTitle("Trip Complete")
                        .setMessage("Are you sure?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    String deviceId="";
                                    try {
                                        deviceId = TelephonyManagerInfo.getIMEI(activity);
                                    }catch (Exception e)
                                    {}

                                    String userId = UserDetail.getInstance().getUserId();

                                    double lat = AppController.tDb.getDouble(Constant.lat,0.0);
                                    double lon = AppController.tDb.getDouble(Constant.lon,0.0);
                                    //   tripCompleteObj.put("userid", UserDetail.getInstance().getUserId());
                                    tripCompleteObj.put("tripid", firstTripId);
                                    tripCompleteObj.put("carid", firstCarId);
                                    //  tripCompleteObj.put("deviceid", deviceid);
                                    tripCompleteObj.put("lat", String.valueOf(lat));
                                    tripCompleteObj.put("lon", String.valueOf(lon));
                                    String url = UrlConfig.tripCompleted;
                                    if(TelephonyManagerInfo.isConnectingToInternet(DriverHomePage.this)) {
                                        ServerInterface.getInstance(activity).makeServiceCall(userId, deviceId, url, tripCompleteObj.toString(),
                                                DriverHomePage.this, true, "Please wait...");
                                    }else {
                                        CustomPopup cp = new CustomPopup(DriverHomePage.this);
                                        cp.commonPopup(getResources().getString(R.string.internet_connection));
                                    }

                                }catch (Exception e)
                                {

                                }
                            }
                        }).setNegativeButton("No", null).show();
            }catch (Exception e)
            {

            }

//        }
//        else
//        {
//            new AlertDialog.Builder(this)
//                        .setTitle("Alert")
//                        .setMessage("To complete trip first go to travelers page and click on Picked Up")
//                        .setNegativeButton("Ok", null).show();
//        }

    }

    public static void sendMessageToActivity(int msg)
    {

        Message m = new Message();
        m.what = msg;

        ((DriverHomePage)activity).updateDriverHandler.sendMessage(m);
    }

    public Handler updateDriverHandler = new Handler(){
        // @Override
        public void handleMessage(Message msg) {

            int event = msg.what;
            switch(event){
                case 2:
                    googleMap.clear();
                    getDriverData(AppController.tDb.getString(Constant.userId));
                    break;
            }
        }
    };

    public void getDriverData(String userId)
    {
        apiSataus = 1;
        double lat = 0.0;
        double lon = 0.0;
        lat = UserDetail.getInstance().getLat();
        lon = UserDetail.getInstance().getLon();
        if(lat==0.0 || lon==0.0)
        {
            lat = AppController.tDb.getDouble(Constant.lat,0);
            lon =  AppController.tDb.getDouble(Constant.lon,0);
        }

        String deviceId="";
        try {
            deviceId = TelephonyManagerInfo.getIMEI(activity);
        }catch (Exception e)
        {}

        JSONObject root = new JSONObject();
        try {
//            root.put("userid", userId);
//            root.put("deviceid", deviceid);
            root.put("lat", String.valueOf(lat));
            root.put("lon", String.valueOf(lon));
//            Toast.makeText(DriverHomePage.this, String.valueOf(lat)+"\n"+String.valueOf(lon), Toast.LENGTH_LONG).show();
            String gcm = AppController.tDb.getString(Constant.gcm_id);
            root.put("gcm", gcm);
        }catch (Exception e)
        {

        }
        ServerInterface.getInstance(activity).makeServiceCall(userId, deviceId, UrlConfig.driverAppData, root.toString(),DriverHomePage.this,true, "Please wait...");
    }

    public void parseResponse(String response)
    {

        if (response != null && !response.equals("") && !response.equals("Not Allowed")) {
            if(apiSataus == 1)
                UserDetail.getInstance().setUserData(response);


            parseData(response);

        }else if(response != null && !response.equals("") && response.equals("Not Allowed"))
        {
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
        }else
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
            return;
        }


    }

    static String sosNum="";
    public void parseData(final String data)
    {
            if(data.equals("server_error"))
            {
                final AlertDialog.Builder builder =  new AlertDialog.Builder(activity);
                final String message = "Sorry for inconvenience. Please try again later";

                builder.setMessage(message)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface d, int id) {
                                        builder.create().dismiss();
                                    }
                                });
                builder.create().show();
                return;
            }

             try {
                    if(apiSataus == 1) {               // 1 for app data
                        try {
                                JSONObject rootJson = new JSONObject(data);
                                if(nextTripCheck) {
                                    AppController.tDb.putString(Constant.userData,data);
                                }
                                String sendupdateevery = rootJson.getString("sendupdateevery");
                                String speedlimit = rootJson.getString("speedlimit");
                                String sosnumber = rootJson.getString("sosnumber");
                                sosNum = sosnumber;
                                String location_update_driver_app = rootJson.getString("location_update_driver_app");
                                String msg_before_trip = rootJson.getString("msg_before_trip");
                                UserDetail.getInstance().setSendupdateevery(sendupdateevery);
                                UserDetail.getInstance().setSpeedlimit(speedlimit);

                                if(!rootJson.has("info"))
                                {
                                    String driverStatus = rootJson.getString("pri_status");
                                    if(driverStatus.equals("0"))
                                    {
                                        this.finish();
                                        startActivity(new Intent(this, LoginActivity.class));
                                    }
                                }
                                JSONArray arrayInfo = rootJson.getJSONArray("info");
                                ArrayList<DDriverDetail> tripList = new ArrayList<>();
                                for (int i = 0; i < arrayInfo.length(); i++) {
                                    JSONObject tripData = arrayInfo.getJSONObject(i);

                                    String car = tripData.getString("car");
                                    String vendorname = tripData.getString("vendorname");
                                    String drivername = tripData.getString("drivername");
                                    String carid = tripData.getString("carid");
                                    String tripid = tripData.getString("tripid");
                                    String traveltype = tripData.getString("traveltype");       // 2 for pick up and 1 for drop
                                    destinationLat      = tripData.getString("destlat");
                                    destinationLon      = tripData.getString("destlon");
                                    AppController.tDb.putString(Constant.tripType, traveltype);
                                    if(i==0)
                                    {
                                        firstCarId = carid;
                                        firstTripId = tripid;

                                        AppController.tDb.putString(Constant.firstCarId, firstCarId);
                                        AppController.tDb.putString(Constant.firstTripId, firstTripId);

                                        tripData.put("sendupdateevery",sendupdateevery);
                                        tripData.put("speedlimit",speedlimit);
                                        tripData.put("sosnumber",sosnumber);
                                        tripData.put("trip_complete","0");


                                        String lastTrip = AppController.tDb.getString(Constant.lastTripData);                      // setting data for last trip
                                        if (lastTrip.equals(""))
                                        {
                                            AppController.tDb.putString(Constant.lastTripData, tripData.toString());
                                        }
                                        else
                                        {
                                            try
                                            {
                                                JSONObject lastTripObj = new JSONObject(lastTrip);

                                                if(lastTripObj.has("trip_complete"))
                                                {
                                                    if(lastTripObj.getString("trip_complete").equals("1"))
                                                    {
                                                        AppController.tDb.putString(Constant.lastTripData, tripData.toString());
                                                    }
                                                    else
                                                    {
                                                        String lastTripId = lastTripObj.getString("tripid");
                                                        if(!lastTripId.equals(tripid))
                                                        {
                                                            AppController.tDb.putString(Constant.lastTripData, tripData.toString());
                                                        }
                                                    }
                                                }
                                                else
                                                {
                                                    String lastTripId = lastTripObj.getString("tripid");
                                                    if(!lastTripId.equals(tripid))
                                                    {
                                                        AppController.tDb.putString(Constant.lastTripData, tripData.toString());
                                                    }
                                                }

                                            }
                                            catch (Exception e)
                                            {
                                                e.printStackTrace();
                                            }
                                        }
                                    }


                                    String destlon;
                                    String destlat;
                                    String targetDate;
                                    String targettime;
                                    String destination;
                                    if (traveltype.equals("1")) {                            //  1 for drop
                                        destlat = tripData.getString("pickuplat");
                                        destlon = tripData.getString("pickuplon");
                                        targetDate = tripData.getString("tripdate");
                                        targettime = tripData.getString("pickuptime");
                                        destination = tripData.getString("pickupaddress");

                                    } else {                                                 // 2 for pick up
                                        destlat = tripData.getString("destlat");
                                        destlon = tripData.getString("destlon");
                                        targetDate = tripData.getString("tripdate");
                                        targettime = tripData.getString("targettime");
                                        destination = tripData.getString("destination");
                                    }

                                    String dateFormat = targetDate.replace("-", "/");
                                    Date date = new SimpleDateFormat("yyyy/MM/dd").parse(dateFormat);
                                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("d MMM");
                                    String formattedDate  = simpleDateFormat.format(date);

                                    targettime = formattedDate + "  " + targettime;

//                                    travelers larray

                                    JSONArray travellerArray = tripData.getJSONArray("travellers");
                                    ArrayList<DDriverDetail> traveList = new ArrayList<>();
                                    for (int j = 0; j < travellerArray.length(); j++)
                                    {
                                        JSONObject travellerObj = travellerArray.getJSONObject(j);
                                        String id = travellerObj.getString("id");
                                        String lon = travellerObj.getString("lon");
                                        String loggedin = travellerObj.getString("loggedin");
                                        String address = travellerObj.getString("address");
                                        String pickuptime = travellerObj.getString("pickuptime");
                                        String empcode = travellerObj.getString("empcode");
                                        String name = travellerObj.getString("name");
                                        String lat = travellerObj.getString("lat");
                                        String OTP = travellerObj.getString("OTP");
                                        String mobileNo = travellerObj.getString("mobile_no");

                                        if(loggedin.equals("3"))
                                        {
                                            pickesEmpStatus.add(id);
                                        }
                                        else if(loggedin.equals("2"))
                                        {
                                            notReportedEmpStatus.add(id);
                                        }

                                        traveList.add(new DDriverDetail(id, name, address, lat, lon, pickuptime, loggedin, empcode, OTP,mobileNo));
                                    }


                                    tripList.add(new DDriverDetail(carid, traveltype, car, vendorname, tripid, targettime,
                                            destination, destlat, destlon, drivername, traveList));
                                }
                                DDriverDetail detail = new DDriverDetail(sendupdateevery, msg_before_trip, sosnumber, speedlimit,
                                        location_update_driver_app, tripList);
                                AppController.mp.driverDetail.add(detail);

                                final Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        showTraveler();
                                    }
                                }, 1000);


                                Log.e("", "");

                            }
                            catch (Exception e)
                            {
                                try
                                {

                                    tripComp.setVisibility(View.VISIBLE);
                                    soscall.setFocusable(false);
                                    soscall.setEnabled(false);
                                    soscall.setBackgroundColor(getResources().getColor(R.color.disable_button_color));
                                    tripCompleteTv.setText("NO TRIP ASSIGNED");
                                    tripCompRl.setClickable(false);
                                    tripCompRl.setFocusable(false);
                                    tripCompRl.setBackgroundColor(getResources().getColor(R.color.disable_button_color));
                                    triCompleteSign=0;
                                    topRl.setVisibility(View.GONE);
                                    pickUp.setVisibility(View.GONE);
                         //           welcomeRl.setVisibility(View.VISIBLE);
                                    if(nextTripCheck)
                                    {
                                        CustomPopup cp = new CustomPopup(activity);
                                        cp.commonPopup("Currently, NO trip assigned to you.");
                                    }
                                    String name = AppController.tDb.getString(Constant.userNmae);
                                    driverName.setText("Welcome "+name);
                                }
                                catch (Exception e1)
                                {
                                  e.printStackTrace();
                                }
                            }

                        }
                        else if(apiSataus == 2)           // 2 for sos
                        {
                            try
                            {
                                JSONObject jsonData = new JSONObject(data);
                                String status = jsonData.getString("api_status");
                                String msg = jsonData.getString("msg");

                            }
                            catch (Exception e)
                            {
                                e.printStackTrace();
                            }

                            try
                            {

                                Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + callNo));
                                startActivity(dialIntent);
                            }
                            catch (SecurityException e)
                            {
                                e.printStackTrace();
                            }
                            catch (android.content.ActivityNotFoundException ex)
                            {
                                Toast.makeText(this, "Could not find an activity to place the call.", Toast.LENGTH_SHORT).show();
                            }
                            catch (Exception e)
                            {
                                e.printStackTrace();
                            }
                        }
                        else if(apiSataus == 3)               // 3 for complete trip
                        {
                            JSONObject jsonData = new JSONObject(data);
                            String status = jsonData.getString("api_status");
                            String msg = jsonData.getString("msg");
                            String tripStatus = jsonData.getString("trip_status");    // 2 case complete, 3 case for complete, blank case nothing

                            if(status.equals("1"))
                                 tripCompleteOrCancelled(tripStatus, msg);


                        }
                        else if(apiSataus == 4)          // 4 for witing
                        {
                            JSONObject jsonData = new JSONObject(data);
                            String status = jsonData.getString("api_status");
                            if(status.equals("1")) {

                                // added by Vijay
                                List<DDriverDetail> dDriverDetail = new ArrayList<DDriverDetail>();

                                for(int i = 0; i < AppController.mp.driverDetail.get(0).tripDetail.size(); i++)
                                {
                                    dDriverDetail.add(AppController.mp.driverDetail.get(0).tripDetail.get(i));
                                }

                                AppController.mp.driverDetail.get(0).tripDetail.clear();
                                for(int i = 0; i < dDriverDetail.size(); i++)
                                {
                                    List<DDriverDetail> travelerList = dDriverDetail.get(i).traverDeatil;
                                    for(int j = 0; j < travelerList.size(); j++)
                                    {
                                        if(travelerList.get(j).id.equals(empId))
                                        {
                                            travelerList.get(j).loggedin = "1";
                                        }
                                    }
                                }

                                for(int i = 0; i < dDriverDetail.size(); i++)
                                {
                                    AppController.mp.driverDetail.get(0).tripDetail.add(dDriverDetail.get(i));
                                }





                                String msg = jsonData.getString("msg");
                                CustomPopup cp = new CustomPopup(this);
                                cp.commonPopup(msg);
                            }else
                            {

                                CustomPopup cp = new CustomPopup(this);
                                cp.commonPopup("Try again later");
                            }

                        }
                        else if(apiSataus == 5)             // 5 for not reported,
                        {
//                            JSONObject jsonData = new JSONObject(data);
//                            String status = jsonData.getString("api_status");
//                           // String msg = jsonData.getString("msg");
//
//                            if(status.equals("1"))
//                            {
//
//                                // added by Vijay
//                                List<DDriverDetail> dDriverDetail = new ArrayList<DDriverDetail>();
//
//                                for(int i = 0; i < AppController.mp.driverDetail.get(0).tripDetail.size(); i++)
//                                {
//                                    dDriverDetail.add(AppController.mp.driverDetail.get(0).tripDetail.get(i));
//                                }
//
//                                AppController.mp.driverDetail.get(0).tripDetail.clear();
//                                for(int i = 0; i < dDriverDetail.size(); i++)
//                                {
//                                    List<DDriverDetail> travelerList = dDriverDetail.get(i).traverDeatil;
//                                    for(int j = 0; j < travelerList.size(); j++)
//                                    {
//                                        if(travelerList.get(j).id.equals(empId))
//                                        {
//                                            travelerList.get(j).loggedin = "2";
//                                        }
//                                    }
//                                }
//
//                                for(int i = 0; i < dDriverDetail.size(); i++)
//                                {
//                                    AppController.mp.driverDetail.get(0).tripDetail.add(dDriverDetail.get(i));
//                                }
//
//
//
//                                if(!DriverHomePage.notReportedEmpStatus.contains(empId))
//                                    notReportedEmpStatus.add(empId);          // for current employee not reported status
//
//                                buttonLay.setVisibility(View.GONE);
//
//                                LatLng  currentPositionForChage = markerPositio.get(empId);
//                                if(currentPositionForChage !=null && !currentPositionForChage.equals("")) {
//                                    try {
//                                        MarkerOptions markerOptions = new MarkerOptions();
//                                        markerOptions.position(currentPositionForChage);
//                                        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.not_reported_men));
//                                        googleMap.addMarker(markerOptions);
//                                    }catch (Exception e)
//                                    {}
//                                }
//
//                            }else {
//                                CustomPopup cp = new CustomPopup(this);
//                                cp.commonPopup("Try again later");
//                            }




                            JSONObject jsonData = new JSONObject(data);
                            String status = jsonData.getString("api_status");
                            String msg = jsonData.getString("msg");

                            try{
                                if(status.equals("1")) {

                                    // added by Vijay
                                    List<DDriverDetail> dDriverDetail = new ArrayList<DDriverDetail>();

                                    for(int i = 0; i < AppController.mp.driverDetail.get(0).tripDetail.size(); i++)
                                    {
                                        dDriverDetail.add(AppController.mp.driverDetail.get(0).tripDetail.get(i));
                                    }

                                    AppController.mp.driverDetail.get(0).tripDetail.clear();
                                    for(int i = 0; i < dDriverDetail.size(); i++)
                                    {
                                        List<DDriverDetail> travelerList = dDriverDetail.get(i).traverDeatil;
                                        for(int j = 0; j < travelerList.size(); j++)
                                        {
                                            if(travelerList.get(j).id.equals(empId))
                                            {
                                                travelerList.get(j).loggedin = "2";
                                            }
                                        }
                                    }

                                    for(int i = 0; i < dDriverDetail.size(); i++)
                                    {
                                        AppController.mp.driverDetail.get(0).tripDetail.add(dDriverDetail.get(i));
                                    }








                                    if(!DriverHomePage.notReportedEmpStatus.contains(empId))
                                        notReportedEmpStatus.add(empId);          // for current employee picked status

                                    buttonLay.setVisibility(View.GONE);
                                    LatLng  currentPositionForChage = markerPositio.get(empId);
                                    if(currentPositionForChage !=null && !currentPositionForChage.equals("")) {
                                        MarkerOptions markerOptions = new MarkerOptions();
                                        markerOptions.position(currentPositionForChage);
                                        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.man_green));
                                        googleMap.addMarker(markerOptions);
                                    }
                                }else {
                                    Toast.makeText(DriverHomePage.this, msg, Toast.LENGTH_LONG).show();
                                }
                            }catch (Exception e)
                            {

                            }









                        }else if(apiSataus == 6)        // 6 for picked up
                        {
                            JSONObject jsonData = new JSONObject(data);
                            String status = jsonData.getString("api_status");
                            String msg = jsonData.getString("msg");

                            try{
                                if(status.equals("1")) {

                                    // added by Vijay
                                    List<DDriverDetail> dDriverDetail = new ArrayList<DDriverDetail>();

                                    for(int i = 0; i < AppController.mp.driverDetail.get(0).tripDetail.size(); i++)
                                    {
                                        dDriverDetail.add(AppController.mp.driverDetail.get(0).tripDetail.get(i));
                                    }

                                    AppController.mp.driverDetail.get(0).tripDetail.clear();
                                    for(int i = 0; i < dDriverDetail.size(); i++)
                                    {
                                        List<DDriverDetail> travelerList = dDriverDetail.get(i).traverDeatil;
                                        for(int j = 0; j < travelerList.size(); j++)
                                        {
                                            if(travelerList.get(j).id.equals(empId))
                                            {
                                                travelerList.get(j).loggedin = "3";
                                            }
                                        }
                                    }

                                    for(int i = 0; i < dDriverDetail.size(); i++)
                                    {
                                        AppController.mp.driverDetail.get(0).tripDetail.add(dDriverDetail.get(i));
                                    }



                                    


                                    if(!DriverHomePage.pickesEmpStatus.contains(empId))
                                         pickesEmpStatus.add(empId);          // for current employee picked status

                                    buttonLay.setVisibility(View.GONE);
                                    LatLng  currentPositionForChage = markerPositio.get(empId);
                                    if(currentPositionForChage !=null && !currentPositionForChage.equals("")) {
                                        MarkerOptions markerOptions = new MarkerOptions();
                                        markerOptions.position(currentPositionForChage);
                                        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.man_green));
                                        googleMap.addMarker(markerOptions);
                                    }
                                }else {
                                    Toast.makeText(DriverHomePage.this, msg, Toast.LENGTH_LONG).show();
                                }
                            }catch (Exception e)
                            {

                            }
                        }else if(apiSataus == 7)           // 7 for feedback
                        {
                            try{
                                JSONObject jsonData = new JSONObject(data);
                                String status = jsonData.getString("api_status");
                                String msg = jsonData.getString("msg");
                                if(status.equals("1"))
                                    Toast.makeText(activity, "Feedback submitted successfully", Toast.LENGTH_LONG).show();
                                else
                                    Toast.makeText(activity, "Try again later", Toast.LENGTH_LONG).show();

                            }catch (Exception e)
                            {
                                e.printStackTrace();
                            }
                        }else if(apiSataus == 8)       // 8 for check arriving
                        {
                            try{
                                JSONObject jsonData = new JSONObject(data);
                                String status = jsonData.getString("api_status");

                                if(status.equals("1")) {


                                    // added by Vijay
                                    List<DDriverDetail> dDriverDetail = new ArrayList<DDriverDetail>();
                                    for(int i = 0; i < AppController.mp.driverDetail.get(0).tripDetail.size(); i++)
                                    {
                                        dDriverDetail.add(AppController.mp.driverDetail.get(0).tripDetail.get(i));
                                    }

                                    AppController.mp.driverDetail.get(0).tripDetail.clear();
                                    for(int i = 0; i < dDriverDetail.size(); i++)
                                    {
                                        List<DDriverDetail> travelerList = dDriverDetail.get(i).traverDeatil;
                                        for(int j = 0; j < travelerList.size(); j++)
                                        {
                                            if(travelerList.get(j).id.equals(empId))
                                            {
                                                travelerList.get(j).loggedin = "4";
                                            }
                                        }
                                    }

                                    for(int i = 0; i < dDriverDetail.size(); i++)
                                    {
                                        AppController.mp.driverDetail.get(0).tripDetail.add(dDriverDetail.get(i));
                                    }










                                    String msg = jsonData.getString("msg");
                                 //   Toast.makeText(activity, msg, Toast.LENGTH_LONG).show();

                                    ArrayList<DTravellerStatus> statusList = (ArrayList) AppController.tDb.getListObject(Constant.dTravellerStatus, DTravellerStatus.class);

                                    if(statusList.size()>0)
                                    {
                                        for(int i=0; i<statusList.size(); i++)
                                        {
                                            if(statusList.get(i).getTravellerId().equals(empId))
                                            {
                                                //arrivingButton.setBackgroundColor(getResources().getColor(R.color.dark_grey));
                                                break;
                                            }else if(statusList.size()==i+1)
                                            {
                                                arrivingButton.setVisibility(View.VISIBLE);
                                                waiting.setVisibility(View.GONE);
//                                                arrivingButton.setBackgroundColor(getResources().getColor(R.color.disable_button_color));
                                                bgShape3.setColor(getResources().getColor(R.color.disable_button_color));
                                                bgShape3.setCornerRadius(5);
                                                arrivingButton.setBackground(bgShape3);
                                                arrivingButton.setEnabled(false);
                                                arrivingButton.setFocusable(false);
                                                statusList.add(new DTravellerStatus(empId,"1"));
                                                AppController.tDb.putListObject(Constant.dTravellerStatus, statusList);
                                            }
                                        }
                                    }else {
                                        arrivingButton.setVisibility(View.VISIBLE);
                                        waiting.setVisibility(View.GONE);
                                       // arrivingButton.setBackgroundColor(getResources().getColor(R.color.disable_button_color));
                                        bgShape3.setColor(getResources().getColor(R.color.disable_button_color));
                                        bgShape3.setCornerRadius(5);
                                        arrivingButton.setBackground(bgShape3);
                                        arrivingButton.setEnabled(false);
                                        arrivingButton.setFocusable(false);
                                        statusList.add(new DTravellerStatus(empId, "1"));
                                        AppController.tDb.putListObject(Constant.dTravellerStatus, statusList);
                                    }


                                    Intent alarmIntent = new Intent(this, ArrivingButtonReciever.class);
                                    alarmIntent.putExtra("id",empId);
                                    final int _id = (int) System.currentTimeMillis();
                                    pendingIntent = PendingIntent.getBroadcast(this, _id, alarmIntent, PendingIntent.FLAG_ONE_SHOT);
                                    manager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                                    int interval = 3*60*1000;

                                    int ALARM_TYPE = AlarmManager.RTC_WAKEUP;
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                                        manager.setExactAndAllowWhileIdle(ALARM_TYPE, System.currentTimeMillis()+interval, pendingIntent);
                                    else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                                        manager.setExact(ALARM_TYPE, System.currentTimeMillis()+interval, pendingIntent);
                                    else
                                        manager.set(ALARM_TYPE, System.currentTimeMillis()+interval, pendingIntent);

                                }else
                                {
                                    Toast.makeText(DriverHomePage.this, "Try again later", Toast.LENGTH_LONG).show();
                                }
                            }catch (Exception e)
                            {
                                e.printStackTrace();
                            }
                        }

                    } catch (Exception obj) {
                        obj.printStackTrace();
                    }

    }

     double lat = 0.0;
     double lon = 0.0;
    public void showTraveler()
    {
        //int size = AppController.mp.driverDetail.size();
        Log.e("","");
        for (int i = 0; i < AppController.mp.driverDetail.size(); i++) {

            tripListMarker = AppController.mp.driverDetail.get(i).tripDetail;

            for (int j = 0; j < tripListMarker.size(); j++) {
                if (j == 0) {

                    try {
                        destLat = Double.parseDouble(tripListMarker.get(j).destlat);
                        destLon = Double.parseDouble(tripListMarker.get(j).destlon);
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                LatLng currentPosition = new LatLng(destLat, destLon);
                                // currentPositionForChage = currentPosition;
                                MarkerOptions markerOptions = new MarkerOptions();
                                markerOptions.position(currentPosition);
                                 googleMap.addMarker(markerOptions);

                            }
                        });
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }

                    List<DDriverDetail> traverDeatil = tripListMarker.get(j).traverDeatil;
                    String tripId1 = tripListMarker.get(j).tripid;

                    travellerCount = traverDeatil.size();
                    driverName.setText(tripListMarker.get(j).driverName);
                    if (tripListMarker.get(j).traveltype.equals("1"))
                        pickUp.setText("Drop");
                    else
                        pickUp.setText("Pick Up");

                    reportingAdress.setText(tripListMarker.get(j).destination);

                    reportingTime.setText( tripListMarker.get(j).targettime);
                    gaadiNum.setText(tripListMarker.get(j).car);
                  //  firstCarId = tripListMarker.get(j).carId;
                  //  firstTripId = tripListMarker.get(j).tripid;
                    userName.setText(AppController.tDb.getString(Constant.userNmae));
                    tripId.setText("Trip Id - " + firstTripId + ", Travelers - " + travellerCount);
//                    AppController.tDb.putString(Constant.firstCarId, firstCarId);
//                    AppController.tDb.putString(Constant.firstTripId, firstTripId);

                    for (int k = 0; k < traverDeatil.size(); k++) {

                        if (traverDeatil.get(k).lat.equals("") || traverDeatil
                                .get(k).lon.equals(""))
                            return;

                        try {
                            lat = Double.parseDouble(traverDeatil.get(k).lat);
                            lon = Double.parseDouble(traverDeatil.get(k).lon);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        final String id = traverDeatil.get(k).id;
                        String name = traverDeatil.get(k).name;
                        String empcode = traverDeatil.get(k).empcode;
                        String address = traverDeatil.get(k).address;
                        String pickuptime = traverDeatil.get(k).pickuptime;
                        final String loggedin = traverDeatil.get(k).loggedin;
                        String otp = traverDeatil.get(k).otp;

                        final JSONObject rootObj = new JSONObject();
                        try {
                            rootObj.put("id", id);
                            rootObj.put("name", name);
                            rootObj.put("empcode", empcode);
                            rootObj.put("address", address);
                            rootObj.put("pickuptime", pickuptime);
                            rootObj.put("loggedin", loggedin);
                            rootObj.put("lat", "" + lat);
                            rootObj.put("lon", "" + lon);
                            rootObj.put("otp", "" + otp);
                        } catch (Exception e) {
                        }


                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                LatLng currentPosition = new LatLng(lat, lon);
                               // currentPositionForChage = currentPosition;
                                MarkerOptions markerOptions = new MarkerOptions();
                                markerOptions.position(currentPosition);
                                if (loggedin.equals("3"))
                                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.man_green));
                                else if(loggedin.equals("2"))
                                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.not_reported_men));
                                else
                                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.man_red));

                                Marker marker = googleMap.addMarker(markerOptions);
                                marker.setTag(rootObj);

                                markerPositio.put(id,currentPosition);

                            }
                        });

                    }

                    checkTripCompleteOrNot(tripId1);

                } // j validation finish
            } // root for loop finish
        }
    }


    public void checkTripCompleteOrNot(String tripId)
    {
        String trip = AppController.tDb.getString(Constant.lastTripData);
        try {
            JSONObject lastTripObj = new JSONObject(trip);
            String lastTripId = lastTripObj.getString("tripid");
            if(tripId.equals(lastTripId))
            {
                String checkTripComp = lastTripObj.getString("trip_complete");
                if(checkTripComp.equals("1"))
                {
                        triCompleteSign=0;
                        apiSataus = 0;
                        tripComp.setVisibility(View.VISIBLE);
                        tripCompRl.setClickable(false);
                        tripCompRl.setFocusable(false);
                        tripCompRl.setBackgroundColor(getResources().getColor(R.color.disable_button_color));
                        topRl.setVisibility(View.VISIBLE);
                        pickUp.setVisibility(View.VISIBLE);
                }else
                {
                    apiSataus = 1;
                    triCompleteSign = 1;
                    tripComp.setVisibility(View.GONE);
                    tripCompRl.setClickable(true);
                    tripCompRl.setFocusable(true);
                    tripCompRl.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                    topRl.setVisibility(View.VISIBLE);
                    pickUp.setVisibility(View.VISIBLE);

                    soscall.setFocusable(true);
                    soscall.setEnabled(true);
                    soscall.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                }

            }
        } catch (Exception e) {

        }
    }


    public void sendFeedback() {
        apiSataus = 7;
        JSONObject dataObj = new JSONObject();
        try {
            String userType = AppController.tDb.getString(Constant.userType);
            String userId = AppController.tDb.getString(Constant.userId);
            String deviceId = TelephonyManagerInfo.getIMEI(activity);
            dataObj.put("userid", userId);
            dataObj.put("type", userType);

            CustomPopup feedback = new CustomPopup(DriverHomePage.this);
            feedback.feebackPopup(dataObj.toString(), DriverHomePage.this);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void nextTrip(View v)
    {
        if(TelephonyManagerInfo.isConnectingToInternet(activity))
        {
            nextTripCheck = true;
            String userId = UserDetail.getInstance().getUserId();
            getDriverData(userId);
        }else
        {
            CustomPopup cp = new CustomPopup(DriverHomePage.this);
            cp.commonPopup(getResources().getString(R.string.internet_connection));
        }

    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }





    /////////////////////////////////////////////////////////



    private String getDirectionsUrl(LatLng origin,LatLng dest){

        // Origin of route
        String str_origin = "origin="+origin.latitude+","+origin.longitude;

        // Destination of route
        String str_dest = "destination="+dest.latitude+","+dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        // Waypoints
        String waypoints = "";
        for(int i=2;i<markerPoints.size();i++){
            LatLng point  = (LatLng) markerPoints.get(i);
            if(i==2)
                waypoints = "waypoints=";
            waypoints += point.latitude + "," + point.longitude + "|";
        }

        // Building the parameters to the web service
        String parameters = str_origin+"&"+str_dest+"&"+sensor+"&"+waypoints;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;


        long distance =   getDistanceMeters(origin.latitude,origin.longitude,dest.latitude,dest.longitude);
        distanceInKms = distance/1000;

        return url;
    }

    static double latitude;
    static double longitude;
    long distanceInKms;
    ArrayList<LatLng> markerPoints;
    public static long getDistanceMeters(double lat1, double lng1, double lat2, double lng2)
    {

        double l1 = toRadians(lat1);
        double l2 = toRadians(lat2);
        double g1 = toRadians(lng1);
        double g2 = toRadians(lng2);

        double dist = acos(sin(l1) * sin(l2) + cos(l1) * cos(l2) * cos(g1 - g2));
        if(dist < 0)
        {
            dist = dist + Math.PI;
        }


        // Testing
        double dLon = Math.toRadians(lng2 - lng1);

        //convert to radians
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);
        lng1 = Math.toRadians(lng1);

        double Bx = Math.cos(lat2) * Math.cos(dLon);
        double By = Math.cos(lat2) * Math.sin(dLon);
        latitude = Math.atan2(Math.sin(lat1) + Math.sin(lat2), Math.sqrt((Math.cos(lat1) + Bx) * (Math.cos(lat1) + Bx) + By * By));
        longitude = lng1 + Math.atan2(By, Math.cos(lat1) + Bx);

        latitude =  Math.toDegrees(latitude);
        longitude =  Math.toDegrees(longitude);


        return Math.round(dist * 6378100);
    }


    //** A method to download json data from url *//*
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb  = new StringBuffer();

            String line = "";
            while( ( line = br.readLine())  != null){
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        }catch(Exception e){
            //Log.d("Exception while downloading url", e.toString());
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

            // For storing data from web service

            String data = "";

            try{
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }

    //** A class to parse the Google Places in JSON format *//*
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >{

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try{
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
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



            // Traversing through all the routes

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



                Bitmap icon = BitmapFactory.decodeResource(getResources(),
                        R.drawable.car_icon1);

                setAnimation(googleMap, points, icon);





               lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path1 = result.get(i);


                // Fetching all the points in i-th route
                for(int j=0;j<path1.size();j++){



                    int sizeOfPoints = path1.size()/2;

                    if(j<sizeOfPoints || j>sizeOfPoints)
                    {
                        HashMap<String, String> point = path.get(j);

                        double lat = Double.parseDouble(point.get("lat"));
                        double lng = Double.parseDouble(point.get("lng"));
                        LatLng position = new LatLng(lat, lng);
                        points.add(position);

                    }
                    else
                    {
                        HashMap<String, String> point = path.get(j);

                        double lat = Double.parseDouble(point.get("lat"));
                        double lng = Double.parseDouble(point.get("lng"));
                        LatLng position = new LatLng(lat, lng);

                        Polyline polyLine = googleMap.addPolyline(new PolylineOptions()
                                .add(position)
                                .width(5)
                                .visible(false)
                                .color(Color.RED));

                        googleMap.addMarker(new MarkerOptions()
                                .position(new LatLng(lat, lng))
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.car_icon)));
                    }
                }


                Polyline polyLine = googleMap.addPolyline(new PolylineOptions()
                        .addAll(points)
                        .width(5)
                        .color(Color.DKGRAY));


            }

        }
    }




    public void setAnimation(GoogleMap myMap, final List<LatLng> directionPoint, final Bitmap bitmap) {

        Marker marker = myMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
                .position(directionPoint.get(0))
                .flat(true));
        //markerList.add(marker);
        myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(directionPoint.get(0), 10));

        animateMarker(myMap, marker, directionPoint, false);
    }

    LatLng ll = null;
    private void animateMarker(GoogleMap myMap, final Marker marker, final List<LatLng> directionPoint,
                                      final boolean hideMarker) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = myMap.getProjection();
        final long duration = 30000;

        final Interpolator interpolator = new LinearInterpolator();

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
                        rotateMarker(marker, f);
                    }

                    marker.setPosition(directionPoint.get(i));
                    ll = directionPoint.get(i);
                }
                i++;

                handler.postDelayed(this, 16);
//                if (t < 1.0) {
//                    // Post again 16ms later.
//                    handler.postDelayed(this, 16);
//                } else {
//                    if (hideMarker) {
//                        marker.setVisible(false);
//                    } else {
//                        marker.setVisible(true);
//                    }
//                }
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
                    if (t < 1.0) {
                        // Post again 16ms later.
                        handler.postDelayed(this, 16);
                    } else {
                        isMarkerRotating = false;
                    }
                }
            });
        }
    }

//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        apiSataus = 1;
//    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch(keyCode){
            case KeyEvent.KEYCODE_BACK:

                apiSataus = 1;

                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

                return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void onStop() {
        super.onStop();
        apiSataus = 1;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        apiSataus = 1;
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


    public static void tripCompleteOrCancelled(String tripStatus, String msg)
    {
        try {
            if (tripStatus.equals("2") || tripStatus.equals("3")) {
                AppController.mp.driverDetail.clear();
                ArrayList<DTravellerStatus> ssList = new ArrayList<>();
                AppController.tDb.putListObject(Constant.dTravellerStatus, ssList);
                String trip = AppController.tDb.getString(Constant.lastTripData);
                try {
                    JSONObject lastTripObj = new JSONObject(trip);
                    lastTripObj.put("trip_complete", "1");
                    triCompleteSign = 0;
                    AppController.tDb.putString(Constant.lastTripData, lastTripObj.toString());
                } catch (Exception e) {

                }

                AppController.tDb.putString(Constant.userData, "");

                googleMap.clear();
                tripComp.setVisibility(View.VISIBLE);
                travelerBottomRl.setVisibility(View.GONE);
                topRl.setVisibility(View.GONE);
                tripCompRl.setClickable(false);
                tripCompRl.setFocusable(false);
                pickUp.setVisibility(View.GONE);
                tripCompRl.setBackgroundColor(activity.getResources().getColor(R.color.disable_button_color));
                soscall.setFocusable(false);
                soscall.setEnabled(false);
                soscall.setBackgroundColor(activity.getResources().getColor(R.color.disable_button_color));

                tripListMarker.clear();
                pickesEmpStatus.clear();
                markerPositio.clear();

            }

            CustomPopup cp = new CustomPopup(activity);
            cp.commonPopup(msg);

        }catch (Exception e)
        {
            e.printStackTrace();
        }
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

}
