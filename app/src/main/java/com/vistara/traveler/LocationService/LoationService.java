package com.vistara.traveler.LocationService;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.vistara.traveler.AppController;
import com.vistara.traveler.Constant;
import com.vistara.traveler.DriverHomePage;
import com.vistara.traveler.R;
import com.vistara.traveler.ServerFile.ServerInterface;
import com.vistara.traveler.ServerFile.UrlConfig;
import com.vistara.traveler.SplshScreen;
import com.vistara.traveler.singlton.UserDetail;
import com.vistara.traveler.utils.ApiResponseParser;
import com.vistara.traveler.utils.TelephonyManagerInfo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


/**
 * Created by Sharad Gupta on 11/22/2016.
 */

public class LoationService extends Service  implements  GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, ApiResponseParser, LocationListener {


    public static final String ANDROID_CHANNEL_ID = "com.vistara.traveler";



    Handler mHandler;
    boolean stopPolling = false;
    int checkLocationStatusInSeconds = 60*60; //60 minutes
    int inprogress = 0;

    private static final String TAG = "TravelerServoce";
    public static Location mLastLocation;

    private final Object lock = new Object();

    public boolean bUpdateNotifOff, bUpdateNotifOn;

    public static String m_TrackingDate;

    public static ArrayList<MyRecordedLocation> m_recordedLocationArr;

    NotificationManager m_notificationManager;
    long lastLocationReceivedTime = 0;
    FusedLocationProviderApi fusedLocationProviderApi;
    public GoogleApiClient googleApiClient;
    private LocationCallback mLocationCallback;
    float speed ;
    Context  mContext;


    double lat =0.0;
    double lang = 0.0;
    public static ArrayList<Marker> markerList;


    @Override
    public void onCreate() {
        super.onCreate();

        int a = 15;
        bUpdateNotifOff = true;
        bUpdateNotifOn = true;
        mContext = LoationService.this;

        googleApiClient = new GoogleApiClient.Builder(LoationService.this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();


        addNotification("Location Tracking is ON", false);

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    try {
                        mLastLocation = fusedLocationProviderApi.getLastLocation(googleApiClient);

                        sendLocationUpdate(mLastLocation);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            };
        };

    }


    @Override
    public void onConnected(Bundle bundle) {
//        if(bundle == null)
//        {
//
//            return;
//        }

        Log.d(TAG, "onConnected - isConnected ...............: " + googleApiClient.isConnected());
        try {
            locationEnabledHandler();


            lastLat = fusedLocationProviderApi.getLastLocation(googleApiClient).getLatitude();
            lastLon = fusedLocationProviderApi.getLastLocation(googleApiClient).getLongitude();

            UserDetail.getInstance().setLat(lastLat);
            UserDetail.getInstance().setLon(lastLon);
            AppController.tDb.putDouble(Constant.lat, lastLat);
            AppController.tDb.putDouble(Constant.lon, lastLon);

            LocationServices.FusedLocationApi.requestLocationUpdates(
                    googleApiClient, AppController.mLocationRequest, this);
            Location ll = fusedLocationProviderApi.getLastLocation(googleApiClient);
            mLastLocation = ll;
            sendLocationUpdate(ll);

        }catch (Exception e)
        {
            googleApiClient.disconnect();
            e.printStackTrace();
        }

    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "Firing onLocationChanged..............................................");
        mLastLocation = location;
        sendLocationUpdate(location);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("CustoFieldService1", "onStartCommand");
        super.onStartCommand(intent, flags, startId);
        fusedLocationProviderApi = LocationServices.FusedLocationApi;
        googleApiClient.connect();
     //   Toast.makeText(mContext, "sticky" , Toast.LENGTH_LONG).show();
      //  finalMarker = null;
        points = new ArrayList<>();
        return START_STICKY;
    }

    public void locationEnabledHandler() {
        mHandler = new Handler();
        mHandler.postDelayed(mRunnable, 1000);
    }


    public static Marker finalMarker = null;
    int checkMarker = 0;
    LatLng oldLatLon ;
    private ArrayList<LatLng> points; //added
    Polyline line; //added


    private Runnable mRunnable = new Runnable() {

        @Override
        public void run() {

            if(stopPolling == false)
            {
                try {

                    try {

                        if(mLastLocation== null) {

                            if(!googleApiClient.isConnected()) {
                                googleApiClient.connect();
                            }
                            mHandler.postDelayed(mRunnable, 3000);
                            return;
                        }
                                        DriverHomePage.activity.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {

                                                if(markerList==null)
                                                {
                                                    markerList = new ArrayList<>();
                                                }
                                                try {
                                                    lastLat = fusedLocationProviderApi.getLastLocation(googleApiClient).getLatitude();
                                                    lastLon = fusedLocationProviderApi.getLastLocation(googleApiClient).getLongitude();

                                                    AppController.tDb.putDouble(Constant.lat, lastLat);
                                                    AppController.tDb.putDouble(Constant.lon, lastLon);


                                                    if ((finalMarker == null) || (lastLat != lat || lastLon != lang)) {
                                                        LatLng currentPosition = new LatLng(lastLat, lastLon);
                                                        points.add(currentPosition);

                                                        if (finalMarker == null) {
                                                            MarkerOptions markerOptions = new MarkerOptions();
                                                            markerOptions.position(currentPosition);
                                                            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.car_icon1));
                                                            finalMarker = DriverHomePage.googleMap.addMarker(markerOptions);
                                                        //    finalMarker.setFlat(true);
                                                        } else {
                                                            finalMarker.setPosition(currentPosition);
                                                          //  finalMarker.setFlat(true);
                                                            if(oldLatLon!=null) {
                                                                double sss = bearingBetweenLocations(oldLatLon, currentPosition);
                                                                float f = (float) sss;
                                                                rotateMarker(finalMarker, f);
                                                            }
                                                        }

                                                        lat = lastLat;
                                                        lang = lastLon;

                                                        if(points.size()>15) {
                                                            points.clear();
                                                        }

                                                        oldLatLon = currentPosition;

                                                    }

                                                }catch (Exception e)
                                                {
                                                    e.printStackTrace();
                                                }
                                            }
                                        });

                    } catch (SecurityException e) {
                        e.printStackTrace();
                    }

                    mHandler.postDelayed(mRunnable, 1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };


    public void parseResponse(String response) {

        try {if (response.equals("server_error"))
                return;

            JSONObject rootObj = new JSONObject(response);
            String status = rootObj.getString("api_status");
            String pickUpStaus = rootObj.getString("pickup_status");     // 0 for not update, 1 for update
            String tripStatus = rootObj.getString("trip_status");           // 0 fresh, 2 complete, 3 cancelled

            if (pickUpStaus.equals("1")) {
                ArrayList<String> data = new ArrayList<>();
                AppController.tDb.putListString(Constant.pickedTravObj, data);
            }

            if (tripStatus.equals("2") || tripStatus.equals("3")) {
                String msg = "";
                if(tripStatus.equals("2"))
                    msg = "Trip Completed.";
                else if(tripStatus.equals("3"))
                    msg = "Trip Cancelled.";

                DriverHomePage.tripCompleteOrCancelled(tripStatus, msg);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "Connection failed: " + connectionResult.toString());
    }


    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "Connection failed: ");
    }

    public void sendLocationUpdate(Location location)
    {
        if(location == null)
            return;

        lastLocationReceivedTime = System.currentTimeMillis();
        speed = location.getSpeed();
        boolean isMyTrackingOn = true;


        //first check - if tracking on
        if(isMyTrackingOn) {
         //   checkLocationStatusInSeconds = (3 + 2) *60;

            if (inprogress == 0) {
                boolean bSendFlag1 = true;//checkifWithInTimeRange();
                if(bSendFlag1) {
                    JSONArray pickedTravAray = new JSONArray();
                    try {
                        ArrayList<String> pickedTravList = AppController.tDb.getListString(Constant.pickedTravObj);

                        for (int i = 0; i < pickedTravList.size(); i++) {
                            JSONObject data = new JSONObject(pickedTravList.get(i));
                            String userId = data.getString("userid");
                            String status = data.getString("status");
                            String empId = data.getString("emp_id");
                            String empCode = data.getString("empcode");
                            String ddStr = data.getString("date_time");

                            JSONObject jObj = new JSONObject();
                         //   jObj.put("userid",userId);
                            jObj.put("emp_id",empId);
                            jObj.put("empcode",empCode);
                            jObj.put("date_time",ddStr);
                            jObj.put("status",status);
                            pickedTravAray.put(jObj);

                        }
                        JSONObject statusobj = new JSONObject();
                        statusobj.put("statusupdate",pickedTravAray);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    JSONObject rootLatLon = new JSONObject();
                    String deviceId="";
                    try {
                        deviceId = TelephonyManagerInfo.getIMEI(LoationService.this);
                    } catch (Exception e) {
                    }
                    String userId = UserDetail.getInstance().getUserId();

                    try {
                        String tripId = AppController.tDb.getString(Constant.firstTripId);
                        String carId = AppController.tDb.getString(Constant.firstCarId);

                        if(tripId.equals(""))
                            return;

                        String batteryStaus = UserDetail.getInstance().getBatteryStatus();
                        String gpsStatus = UserDetail.getInstance().getGpsStaus();

                    //    rootLatLon.put("userid", userId);
                        rootLatLon.put("tripid", tripId);
                        rootLatLon.put("carid", carId);
                     //   rootLatLon.put("deviceid", deviceId);
                        rootLatLon.put("speed", speed);
                        rootLatLon.put("lat", lastLat);
                        rootLatLon.put("lon", lastLon);
                        rootLatLon.put("batteryplugged", batteryStaus);
                        rootLatLon.put("gpson", gpsStatus);
                        rootLatLon.put("pickupdata", pickedTravAray);

//                        Toast.makeText(this, lastLat+"\n"+lastLon, Toast.LENGTH_LONG).show();

                    }catch (Exception e)
                    {

                    }
                   // 300000
                    String trip = AppController.tDb.getString(Constant.lastTripData);
                    String tripStaus = "0";
                    try {
                        JSONObject lastTripObj = new JSONObject(trip);
                        tripStaus = lastTripObj.getString("trip_complete");
                    } catch (Exception e) {

                    }
                    long time = AppController.tDb.getLong(Constant.lastTimeForDriverApiCall,0);
                    long currentMilliSec = System.currentTimeMillis();

                    if(tripStaus.equals("0")) {
                        if (time == 0) {
                            if (ServerInterface.latLonBoolean) {
                                ServerInterface.getInstance(LoationService.this).driverRequest(userId, deviceId, UrlConfig.updateCarParams, rootLatLon.toString(), LoationService.this,
                                        true);
                                AppController.tDb.putLong(Constant.lastTimeForDriverApiCall, currentMilliSec);
                            }
                        } else if (currentMilliSec - time > 90000) {
                            if (ServerInterface.latLonBoolean) {
                                ServerInterface.getInstance(LoationService.this).driverRequest(userId, deviceId, UrlConfig.updateCarParams, rootLatLon.toString(), LoationService.this,
                                        true);
                                AppController.tDb.putLong(Constant.lastTimeForDriverApiCall, currentMilliSec);
                            }
                        }
                    } // end if for trip complete

                }
            }
        }
    }


    String notifTitle = "Vistara App Service";
    private void addNotification(String detailtext, boolean onlyDetailUpdate) {
        // create the notification
//        NotificationManager notificationManager = (NotificationManager)
//                LoationService.this.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(LoationService.this);

//        Intent notificationIntent = new Intent(this, SplshScreen.class);
//
//        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//        PendingIntent intent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
//
//        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this,ANDROID_CHANNEL_ID)
//                .setContentIntent(intent)
//                .setSmallIcon(R.drawable.arrow)  //a resource for your custom small icon
//                .setContentTitle(notifTitle) //the "title" value you sent in your notification
//                .setContentText(detailtext) //ditto
//                .setAutoCancel(true)  //dismisses the notification on click



        Intent intent=null;
//        boolean bb= true;
//        try {
//             bb = isAppOnForeground(LoationService.this);
//        }catch (Exception e)
//        {}

        if (DriverHomePage.activity!=null)
            intent = new Intent(this, LoationService.class);
        else
            intent = new Intent(this, SplshScreen.class);


        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        Bitmap bitmap= BitmapFactory.decodeResource(LoationService.this.getResources(), R.drawable.icon);

        if(!onlyDetailUpdate) {
            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("dd MMM hh:mm aa");
            String formattedDate = df.format(c.getTime());
            notifTitle = notifTitle + "   " + formattedDate;
        }

        Notification notification = builder.setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.notification_icon).setTicker(notifTitle).setWhen(0)
                .setAutoCancel(true).setContentTitle(notifTitle).setLargeIcon(bitmap)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(detailtext))
                .setContentText(detailtext).build();
        try {
            m_notificationManager = (NotificationManager)
                    LoationService.this.getSystemService(Context.NOTIFICATION_SERVICE);
            // send the notification
            int i = (int) System.currentTimeMillis();
            //m_notificationManager.notify(i, notification);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                startMyOwnForeground(i, notifTitle);
            else
                startForeground(i, notification);
           // notificationManager.notify(i, notification);
        }catch(Exception e)
        {
            e.printStackTrace();
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private void startMyOwnForeground(int i, String notifTitle){
        String NOTIFICATION_CHANNEL_ID = ANDROID_CHANNEL_ID;
        String channelName = "My Background Service";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_DEFAULT);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.createNotificationChannel(chan);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(LoationService.this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle(notifTitle)
                .setPriority(NotificationManager.IMPORTANCE_DEFAULT)
                .setCategory(Notification.CATEGORY_MESSAGE)
                .build();
        startForeground(i, notification);
    }


    private boolean isAppOnForeground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null) {
            return false;
        }
        final String packageName = context.getPackageName();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && appProcess.processName.equals(packageName)) {
                return true;
            }
        }
        return false;
    }


    /*class ForegroundCheckTask extends AsyncTask<Context, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Context... params) {
            final Context context = params[0].getApplicationContext();
            return isAppOnForeground(context);
        }

        private boolean isAppOnForeground(Context context) {
            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
            if (appProcesses == null) {
                return false;
            }
            final String packageName = context.getPackageName();
            for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
                if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && appProcess.processName.equals(packageName)) {
                    return true;
                }
            }
            return false;
        }
    }*/

    public boolean buildNotification(Context appContext) {

        ActivityManager activityManager = (ActivityManager) appContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> services = activityManager
                .getRunningTasks(Integer.MAX_VALUE);
        boolean isActivityFound = false;

        if (services.get(0).topActivity.getPackageName().toString()
                .equalsIgnoreCase(appContext.getPackageName().toString())) {
            isActivityFound = true;
        }


        return  isActivityFound;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent){
        Log.e("CustoFieldService1", "onTaskRemoved");
        Intent restartServiceTask = new Intent(getApplicationContext(),this.getClass());
        restartServiceTask.setPackage(getPackageName());
        PendingIntent restartPendingIntent =PendingIntent.getService(getApplicationContext(), 1,restartServiceTask, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager myAlarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        myAlarmService.set(
                AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + 1000,
                restartPendingIntent);
        mContext = LoationService.this;
   //     Toast.makeText(getApplicationContext(), "Custo Field Service", Toast.LENGTH_LONG).show();
        super.onTaskRemoved(rootIntent);
    }

    @Override
    public IBinder onBind(Intent arg0)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onDestroy()
    {
        Log.e(TAG, "onDestroy Service");
        super.onDestroy();

        try {

            if(m_TrackingDate == null || m_TrackingDate.equals("") )
            {
                //shuld not be happen this
            }else {
//                TinyDB tDb = new TinyDB(LoationService.this);
//                int userId =  Integer.parseInt(tDb.getString(Constant.userId));
//                writeLocationsToFile(userId, m_TrackingDate);
            }

        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }//end onDestroy



    Double lastLat=0.0, lastLon=0.0;
    public void writeLocationsToFile(int userid, String date)
    {

        if( m_recordedLocationArr == null || m_recordedLocationArr.size() == 0 )
            return;

        try {
            String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
            File file = new File(path, "CustoField" );
            if (!file.exists()) {
                file.mkdirs();
            }

            file = new File(path, "CustoField" + "/" + userid + "_" + date + ".csv");

            String header = "time" + "," + "Lat" + "," + "Lon" + "," + "Speed" + "," + "DistanceTravelled" + "," + "Provider";
            FileWriter out = new FileWriter(file, true);
            out.append(header);
            int size = m_recordedLocationArr.size();
            for (int i = 0; i < size; i++) {
                MyRecordedLocation o = m_recordedLocationArr.get(i);
                String lineitem = o.time + "," + o.lat + "," +o.lon + "," + o.speed + "," + o.totalDistanceTravelled + "," + o.provider + "\n";
                out.append(lineitem);
            }
            m_recordedLocationArr.clear();
            out.flush();
            out.close();
        }catch(Exception e)
        {

        }
    }//end writeLocationsToFile

    public class MyRecordedLocation {
        public String time;
        public Double lat, lon;
        public String speed;
        public String totalDistanceTravelled;
        public String provider;
    };


    ///////////////////////////////////////////////////////
    Marker marker;
    public void setAnimation(GoogleMap myMap, final List<LatLng> directionPoint, final Bitmap bitmap) {

//        for (int i = 0; i < markerList.size(); i++) {
//            Marker mm = markerList.get(i);
//            mm.remove();
//        }
//        markerList.clear();
        if(marker==null) {
             marker = myMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
                    .position(directionPoint.get(0))
                    .flat(true));
        }else
        {
            marker.setPosition(directionPoint.get(0));
        }
   //     markerList.add(marker);
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

//            handler.post(new Runnable() {
//                @Override
//                public void run() {
//                    isMarkerRotating = true;

                    long elapsed = SystemClock.uptimeMillis() - start;
                    float t = interpolator.getInterpolation((float) elapsed / duration);

                    float rot = t * toRotation + (1 - t) * startRotation;

                    marker.setRotation(-rot > 180 ? rot / 2 : rot);
                    marker.setFlat(true);
//                    if (t < 1.0) {
//                        // Post again 16ms later.
//                        handler.postDelayed(this, 16);
//                    } else {
//                        isMarkerRotating = false;
//                    }

       //             handler.postDelayed(this, 16);
//                }
//            });
        }
    }


    public void setMarkerDirect(final LatLng latlon)
    {
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            int i = 0;

            @Override
            public void run() {

//                if (i < directionPoint.size()) {
//                    if(ll!=null) {
//                        double sss = bearingBetweenLocations(ll, (directionPoint.get(i)));
//                        float f = (float)sss;
//                        rotateMarker(marker, f);
//                    }

                    marker.setPosition(latlon);
//
//                    ll = directionPoint.get(i);
//                }
//                i++;

                handler.postDelayed(this, 16);

            }
        });
    }



}

