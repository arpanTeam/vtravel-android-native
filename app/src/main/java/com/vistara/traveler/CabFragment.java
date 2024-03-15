package com.vistara.traveler;


import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.vistara.traveler.LocationService.GPSTracker;
import com.vistara.traveler.ServerFile.ServerInterface;
import com.vistara.traveler.ServerFile.UrlConfig;
import com.vistara.traveler.internal.TinyDB;
import com.vistara.traveler.singlton.UserDetail;
import com.vistara.traveler.utils.ApiResponseParser;
import com.vistara.traveler.utils.CompleteTrip;
import com.vistara.traveler.utils.CustomPopup;
import com.vistara.traveler.utils.TelephonyManagerInfo;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

/**
 * Created by Sharad on 21-06-2017.
 */

public class CabFragment extends Fragment implements ApiResponseParser {

    MapView mMapView;
    public static GoogleMap googleMap;
    public static int i = 0;
    private static final int mRESOURCE = R.layout.cab_fragment;
    private int apiRes = 0;
    private double lat = 0.0;
    private double lan = 0.0;
    ApiResponseParser parseActivity;

    static ArrayList<LatLng> MarkerPoints;
    static GoogleApiClient mGoogleApiClient;
    static Location mLastLocation;
    static  Marker mCurrLocationMarker;
    static LocationRequest mLocationRequest;

    View rootView;

    TextView textView;
    FrameLayout frameLayout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView        = inflater.inflate(mRESOURCE, container, false);
        mMapView        = (MapView)rootView. findViewById(R.id.mapView);
        textView        = (TextView) rootView. findViewById(R.id.text_view);
        frameLayout     = (FrameLayout) rootView.findViewById(R.id.framelayout);


        mMapView.onCreate(savedInstanceState);
        parseActivity = CabFragment.this;


        if(TravelerHomePage.tripFound.equals("")) {
            TravelerHomePage.sosRl.setBackgroundColor(getResources().getColor(R.color.disable_button_color));
            TravelerHomePage.sosRl.setEnabled(false);
            TravelerHomePage.sosRl.setFocusable(false);
        } else {
            TravelerHomePage.sosRl.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            TravelerHomePage.sosRl.setEnabled(true);
            TravelerHomePage.sosRl.setFocusable(true);
        }

        if(TravelerHomePage.isTripFound == 1)
        {
            if(TravelerHomePage.noOfFellowTravelers > 0)
            {
                TravelerHomePage.travelersRl.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                TravelerHomePage.travelersRl.setEnabled(true);
                TravelerHomePage.travelersRl.setFocusable(true);
            }
            else
            {
                TravelerHomePage.travelersRl.setBackgroundColor(getResources().getColor(R.color.disable_button_color));
                TravelerHomePage.travelersRl.setEnabled(false);
                TravelerHomePage.travelersRl.setFocusable(false);
            }
        }

        try {
            MapsInitializer.initialize(getActivity());
        } catch (Exception e) {

        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                CabFragment.this.googleMap = googleMap;
                ((TravelerHomePage)getActivity()).parseData();
                googleMap.getUiSettings().setZoomControlsEnabled(true);

//                if(!TravelerHomePage.routeDraw)
//                {
//                    if(TravelerHomePage.lineOptions != null)
//                    {
//                        CabFragment.googleMap.addPolyline(TravelerHomePage.lineOptions);
//                    }
//                }
               try {

                   double lat;
                   double lon;
                   try {
                       GPSTracker gps = new GPSTracker(getActivity());
                       lat = gps.getLatitude();
                       lon = gps.getLongitude();
                       Log.e("","");

                   }
                   catch(Exception e)
                   {
                       lat = 28.644800;
                       lon = 77.216721;
                       e.printStackTrace();
                   }


                   LatLng currentPosition = new LatLng(lat, lon);
                   CameraPosition cameraPosition = null;
                   cameraPosition = new CameraPosition.Builder().target(currentPosition).zoom(12).build();
                   googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                   googleMap.setMyLocationEnabled(true);


               }

               catch (Exception e)
               {

               }


                if (TravelerHomePage.type.equals("1")) {
                    try {
                        try {

                            LatLng currentPosition2 = new LatLng(Double.parseDouble(AppController.mp.travelerDetail.get(0).tripList.get(0).destlat),
                                    Double.parseDouble(AppController.mp.travelerDetail.get(0).tripList.get(0).destlon)); // drop location
                            MarkerOptions markerOptions2 = new MarkerOptions();
                            markerOptions2.position(currentPosition2);
                            CabFragment.googleMap.addMarker(markerOptions2);
                        }catch (Exception e)
                        {}


                        try {
                            LatLng currentPosition1 = new LatLng(Double.parseDouble(AppController.mp.travelerDetail.get(0).tripList.get(0).pickuplat),      // pick up location
                                    Double.parseDouble(AppController.mp.travelerDetail.get(0).tripList.get(0).pickuplon));
                            MarkerOptions markerOptions1 = new MarkerOptions();
                            markerOptions1.position(currentPosition1);

                            markerOptions1.icon(BitmapDescriptorFactory.fromResource(R.drawable.man_green));
                            CabFragment.googleMap.addMarker(markerOptions1);
                      //      googleMap.setMyLocationEnabled(true);
                        }catch (Exception e)
                        {

                        }

                        try {
                            LatLng currentPosition3 = new LatLng(Double.parseDouble(AppController.mp.travelerDetail.get(0).tripList.get(0).carlat),      // pick up location
                                    Double.parseDouble(AppController.mp.travelerDetail.get(0).tripList.get(0).carlon));
                            MarkerOptions markerOptions3 = new MarkerOptions();
                            markerOptions3.position(currentPosition3);

                            if(AppController.mp.travelTime.equals("") || AppController.mp.travelTime == null)
                            {
                                markerOptions3.icon(BitmapDescriptorFactory.fromResource(R.drawable.car_icon1));
                            }
                            else
                            {
                                textView.setText(AppController.mp.travelTime);
                                Bitmap bitmap = frameLayoutToBitmap(frameLayout);
                                markerOptions3.icon(BitmapDescriptorFactory.fromBitmap(bitmap));
                            }
//                            textView.setText(duration);
//                            Bitmap bitmap = frameLayoutToBitmap(frameLayout);
//                            markerOptions3.icon(BitmapDescriptorFactory.fromBitmap(bitmap));
//                            markerOptions3.icon(BitmapDescriptorFactory.fromResource(R.drawable.car_icon1));
                            Marker m = CabFragment.googleMap.addMarker(markerOptions3);
                            m.setPosition(new LatLng(Double.parseDouble(AppController.mp.travelerDetail.get(0).tripList.get(0).carlat),      // pick up location
                                    Double.parseDouble(AppController.mp.travelerDetail.get(0).tripList.get(0).carlon)));
                            TravelerHomePage.markerList.add(m);
                        }catch (Exception e)
                        {

                        }
                    } catch (Exception e) {

                    }
                }else if(TravelerHomePage.type.equals("2"))
                {
                    try {
                        try {

                            LatLng currentPosition2 = new LatLng(Double.parseDouble(AppController.mp.travelerDetail.get(0).tripList.get(0).destlat),
                                    Double.parseDouble(AppController.mp.travelerDetail.get(0).tripList.get(0).destlon)); // drop location
                            MarkerOptions markerOptions2 = new MarkerOptions();
                            markerOptions2.position(currentPosition2);
                            CabFragment.googleMap.addMarker(markerOptions2);

                        }catch (Exception e)
                        {

                        }


                        try {
                            LatLng currentPosition1 = new LatLng(Double.parseDouble(AppController.mp.travelerDetail.get(0).tripList.get(0).pickuplat),      // pick up location
                                    Double.parseDouble(AppController.mp.travelerDetail.get(0).tripList.get(0).pickuplon));
                            MarkerOptions markerOptions1 = new MarkerOptions();
                            markerOptions1.position(currentPosition1);
                            markerOptions1.icon(BitmapDescriptorFactory.fromResource(R.drawable.man_green));
                            CabFragment.googleMap.addMarker(markerOptions1);

                  //          googleMap.setMyLocationEnabled(true);
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }

                        try {
                            String duration = AppController.mp.travelTime;
                            LatLng currentPosition3 = new LatLng(Double.parseDouble(AppController.mp.travelerDetail.get(0).tripList.get(0).carlat),      // pick up location
                                    Double.parseDouble(AppController.mp.travelerDetail.get(0).tripList.get(0).carlon));
                            MarkerOptions markerOptions3 = new MarkerOptions();
                            markerOptions3.position(currentPosition3);


                            if(AppController.mp.travelTime.equals("") || AppController.mp.travelTime == null)
                            {
                                markerOptions3.icon(BitmapDescriptorFactory.fromResource(R.drawable.car_icon1));
                            }
                            else
                            {


                                textView.setText(AppController.mp.travelTime);
                                Bitmap bitmap = frameLayoutToBitmap(frameLayout);
                                markerOptions3.icon(BitmapDescriptorFactory.fromBitmap(bitmap));


                            }
//                            textView.setText(duration);
//                            Bitmap bitmap = frameLayoutToBitmap(frameLayout);
//                            markerOptions3.icon(BitmapDescriptorFactory.fromBitmap(bitmap));
//                            markerOptions3.icon(BitmapDescriptorFactory.fromResource(R.drawable.car_icon1));
                            Marker m = CabFragment.googleMap.addMarker(markerOptions3);
                            m.setPosition(new LatLng(Double.parseDouble(AppController.mp.travelerDetail.get(0).tripList.get(0).carlat),      // pick up location
                                    Double.parseDouble(AppController.mp.travelerDetail.get(0).tripList.get(0).carlon)));
                            TravelerHomePage.markerList.add(m);
                        }catch (Exception e)
                        {

                        }
                    } catch (Exception e) {

                    }
                }
            }
        });

//        TravelerHomePage.safeReachRl.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                if (TelephonyManagerInfo.isConnectingToInternet(getActivity())) {
//                    apiRes = 1;
//                    JSONObject data = new JSONObject();
//                    String userId = UserDetail.getInstance().getUserId();
//                    String deviceId="";
//                    try {
//                        deviceId = TelephonyManagerInfo.getIMEI(getActivity());
//                    }catch (Exception e)
//                    {}
//
//                    try {
//                        GPSTracker gps = new GPSTracker(getActivity());
//                        lat = gps.getLatitude();
//                        lan = gps.getLongitude();
//                        Log.e("","");
//                    }catch(Exception e)
//                    {
//                        lat = 0.0;
//                        lan = 0.0;
//                        e.printStackTrace();
//                    }
//
//                    try {
//                        data.put("tripid", TravelerHomePage.tripId);
//                        data.put("userid", userId);
//                        data.put("deviceid", deviceId);
//                        data.put("lat", lat);
//                        data.put("lon", lan);
//                        data.put("flag", "1");
//                    }catch (Exception e)
//                    {
//                    }
//                    ServerInterface.getInstance(getActivity()).makeServiceCall(userId, deviceId, UrlConfig.t_reached_getready, data.toString(),
//                            parseActivity, true, "Please wait...");
//                }else
//                {
//                    CustomPopup cp = new CustomPopup(getActivity());
//                    cp.commonPopup(getResources().getString(R.string.internet_connection));
//                }
//            }
//        });

       TravelerHomePage.readyToBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TelephonyManagerInfo.isConnectingToInternet(getActivity())) {
                    apiRes = 2;
                    JSONObject data = new JSONObject();
                    String userId = UserDetail.getInstance().getUserId();
                    String deviceId="";
                    try {
                        deviceId = TelephonyManagerInfo.getIMEI(getActivity());
                    }catch (Exception e)
                    {}

                    try {
                        GPSTracker gps = new GPSTracker(getActivity());
                        lat = gps.getLatitude();
                        lan = gps.getLongitude();
                        Log.e("","");
                    }catch(Exception e)
                    {
                        lat = 0.0;
                        lan = 0.0;
                        e.printStackTrace();
                    }

                    try {
                        data.put("tripid", "");
                        data.put("userid", userId);
                        data.put("deviceid", deviceId);
                        data.put("lat", lat);
                        data.put("lon", lan);
                        data.put("flag", "2");
                    }catch (Exception e)
                    {
                    }
                    ServerInterface.getInstance(getActivity()).makeServiceCall(userId, deviceId, UrlConfig.t_reached_getready, data.toString(),
                            parseActivity, true, "Please wait...");
                }else
                {
                    CustomPopup cp = new CustomPopup(getActivity());
                    cp.commonPopup(getResources().getString(R.string.internet_connection));
                }
            }

        });



        return rootView;
    }


  //  ArrayList<Marker> markerList;
    public void parseResponse(String response) {

        try {
            if (response != null && !response.equals("") && !response.equals("Not Allowed")) {

                if (response.equals("server_error")) {
                    final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
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

//                if (apiRes == 1) {            // reach safe
//
//                    try {
//                        JSONObject jsonValue = new JSONObject(response);
//                        String status = jsonValue.getString("api_status");
//                        String msg = jsonValue.getString("msg");
//                        if(status.equals("1")) {
//                            CompleteTrip cp1 = TravelerHomePage.cTripObj;
//                            cp1.completeTrip();
//                        }
//                        CustomPopup cp = new CustomPopup(getActivity());
//                        cp.twoButtonDynamicPopup(msg, "feedback_pop", true, true, "Cancel", "Feedback");
//                    } catch (Exception e) {
//                    }
//
//                }
                else if (apiRes == 2) {            // ready to board

                    try {
                        JSONObject jsonValue = new JSONObject(response);
                        String status = jsonValue.getString("api_status");
                        String msg = jsonValue.getString("msg");
                        CustomPopup cp = new CustomPopup(getActivity());
                        cp.commonPopup(msg);
                    } catch (Exception e) {
                    }
                }
            }
        } catch (Exception e) {
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();

        String value = AppController.tDb.getString(Constant.tReadyBoard);
     //   (TravelerHomePage.safeReach == 0  && TravelerHomePage.isTripFound == 1)
//        if (TravelerHomePage.safeReach == 1) {
//            TravelerHomePage.safeReachRl.setVisibility(View.VISIBLE);
//            TravelerHomePage.readyToBoard.setVisibility(View.GONE);
//            TravelerHomePage.travelersRl.setVisibility(View.GONE);
//        }else
//            if(AppController.tDb.getString(Constant.tReadyBoard).equals("1") && TravelerHomePage.isTripFound == 0) {
//            TravelerHomePage.readyToBoard.setVisibility(View.VISIBLE);
////            TravelerHomePage.safeReachRl.setVisibility(View.GONE);
//            TravelerHomePage.travelersRl.setVisibility(View.GONE);
//        }  else {
////            TravelerHomePage.safeReachRl.setVisibility(View.GONE);
//            TravelerHomePage.readyToBoard.setVisibility(View.GONE);
//            TravelerHomePage.travelersRl.setVisibility(View.VISIBLE);
////            TravelerHomePage.travelersRl.setBackgroundColor(getResources().getColor(R.color.colorAccent));
//        }

    }

    @Override
    public void setMenuVisibility(final boolean visible) {
        super.setMenuVisibility(visible);
        if (visible) {

            if(!TravelerHomePage.tripFound.equals("")) {
                TravelerHomePage.topRl.setVisibility(View.GONE);
            }
            else {
                TravelerHomePage.topRl.setVisibility(View.VISIBLE);
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
