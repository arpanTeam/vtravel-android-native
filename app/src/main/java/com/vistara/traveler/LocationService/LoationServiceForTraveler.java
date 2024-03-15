/*
package com.servicecenter.travelerproject.LocationService;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.servicecenter.travelerproject.AppController;
import com.servicecenter.travelerproject.CabFragment;
import com.servicecenter.travelerproject.Constant;
import com.servicecenter.travelerproject.DriverHomePage;
import com.servicecenter.travelerproject.R;
import com.servicecenter.travelerproject.ServerFile.ServerInterface;
import com.servicecenter.travelerproject.ServerFile.UrlConfig;
import com.servicecenter.travelerproject.singlton.UserDetail;
import com.servicecenter.travelerproject.utils.ApiResponseParser;
import com.servicecenter.travelerproject.utils.TelephonyManagerInfo;

import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;


*/
/**
 * Created by Sharad Gupta on 11/22/2016.
 *//*


public class LoationServiceForTraveler extends Service  implements  GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, ApiResponseParser, LocationListener {

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
    float speed ;
    Context  mContext;
    private LocationCallback mLocationCallback;

    double lat =0.0;
    double lang = 0.0;
    ArrayList<Marker> markerList;



    @Override
    public void onCreate() {
        super.onCreate();

        int a = 15;
        bUpdateNotifOff = true;
        bUpdateNotifOn = true;
        mContext = LoationServiceForTraveler.this;

        googleApiClient = new GoogleApiClient.Builder(LoationServiceForTraveler.this)
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
                    }catch (Exception e)
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
        locationEnabledHandler();

        return START_STICKY;
    }

    public void locationEnabledHandler() {
        mHandler = new Handler();
        mHandler.postDelayed(mRunnable, 2000);
    }

    private Runnable mRunnable = new Runnable() {

        @Override
        public void run() {

            if(stopPolling == false)
            {
                try {

                    try {

                        if(mLastLocation== null) {

                            if(!googleApiClient.isConnected())
                            {
                                googleApiClient.connect();
                            }
                            mHandler.postDelayed(mRunnable, 2000);
                            return;
                        }

//                        Thread t = new Thread() {
//                            @Override
//                            public void run() {
//                                try {
//                                    while (!isInterrupted()) {
//                                        Thread.sleep(1000);

//                        if(lastLon==0.0 || lastLat==0.0)
//                            return;

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
                                                    Toast.makeText(mContext, "" + lastLat + "   " + lastLon, Toast.LENGTH_SHORT).show();
                                                    if (lastLat != lat && lastLon != lang) {
                                                        for (int i = 0; i < markerList.size(); i++) {
                                                            Marker mm = markerList.get(i);
                                                            mm.remove();
                                                        }
                                                        markerList.clear();
                                                        LatLng currentPosition = new LatLng(lastLat, lastLon);
                                                        CameraPosition cameraPosition = null;
                                                        cameraPosition = new CameraPosition.Builder().target(currentPosition).zoom(12).build();
                                                        DriverHomePage.googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                                                        MarkerOptions markerOptions = new MarkerOptions();
                                                        markerOptions.position(currentPosition);
                                                        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.car_icon));
                                                        Marker m = CabFragment.googleMap.addMarker(markerOptions);
                                                        m.setPosition(new LatLng(lastLat, lastLon));
                                                        markerList.add(m);
                                                        lat = lastLat;
                                                        lang = lastLon;
                                                    }
                                                }catch (Exception e)
                                                {}
                                            }
                                        });

//                                    }
//                                } catch (InterruptedException ignored) {}
//                            }
//                        };
//                        t.start();




                    } catch (SecurityException e) {
                        e.printStackTrace();
                    }

                    mHandler.postDelayed(mRunnable, 10000);
} catch (Exception e) {
        //       Log.e("LiveFieldService", e.getMessage());
        e.printStackTrace();
        }
        }
        }
        };


    public void parseResponse(String response) {

        Log.e("","");

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

                    JSONObject rootLatLon = new JSONObject();
                    try {
                        String userId = AppController.tDb.getString(Constant.userId);
                        String tripId = AppController.tDb.getString(Constant.firstTripId);
                        String carId = AppController.tDb.getString(Constant.firstCarId);
                        String deviceId = TelephonyManagerInfo.getIMEI(mContext);

                        if(tripId.equals(""))
                            return;

                        rootLatLon.put("userid", userId);
                        rootLatLon.put("tripid", tripId);
                        rootLatLon.put("carid", carId);
                        rootLatLon.put("deviceid", deviceId);
                        rootLatLon.put("speed", speed);
                        rootLatLon.put("lat", lastLat);
                        rootLatLon.put("lon", lastLon);
                        rootLatLon.put("batteryplugged", "1");
                        rootLatLon.put("gpson", "1");

                    }catch (Exception e)
                    {

                    }
                   // 300000
                    long time = AppController.tDb.getLong(Constant.lastTimeForDriverApiCall,0);
                    long currentMilliSec = System.currentTimeMillis();
                    if(time ==0) {
                        if(ServerInterface.latLonBoolean) {
                            ServerInterface.getInstance(LoationServiceForTraveler.this).driverRequest(UrlConfig.updateCarParams, rootLatLon.toString(), LoationServiceForTraveler.this,
                                    true);
                            AppController.tDb.putLong(Constant.lastTimeForDriverApiCall, currentMilliSec);
                        }
                    }else if(currentMilliSec - time>180000)
                    {
                        if(ServerInterface.latLonBoolean) {
                            ServerInterface.getInstance(LoationServiceForTraveler.this).driverRequest(UrlConfig.updateCarParams, rootLatLon.toString(), LoationServiceForTraveler.this,
                                    true);
                            AppController.tDb.putLong(Constant.lastTimeForDriverApiCall, currentMilliSec);
                        }
                    }
                }
            }
        }
    }


    String notifTitle = "Vistara App Service";

    private void addNotification(String detailtext, boolean onlyDetailUpdate) {
        // create the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(LoationServiceForTraveler.this);

        Intent intent = new Intent(this, LoationServiceForTraveler.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        Bitmap bitmap= BitmapFactory.decodeResource(LoationServiceForTraveler.this.getResources(), R.drawable.icon);

        if(!onlyDetailUpdate) {
            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("dd MMM hh:mm aa");
            String formattedDate = df.format(c.getTime());
            notifTitle = notifTitle + "   " + formattedDate;
        }

        Notification notification = builder.setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_launcher).setTicker(notifTitle).setWhen(0)
                .setAutoCancel(true).setContentTitle(notifTitle).setLargeIcon(bitmap)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(detailtext))
                .setContentText(detailtext).build();
        try {
            m_notificationManager = (NotificationManager)
                    LoationServiceForTraveler.this.getSystemService(Context.NOTIFICATION_SERVICE);
            // send the notification
            int i = (int) System.currentTimeMillis();
            //m_notificationManager.notify(i, notification);
            startForeground(i, notification);
        }catch(Exception e)
        {

        }
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
        mContext = LoationServiceForTraveler.this;
        Toast.makeText(getApplicationContext(), "Custo Field Service", Toast.LENGTH_LONG).show();
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

}

*/
