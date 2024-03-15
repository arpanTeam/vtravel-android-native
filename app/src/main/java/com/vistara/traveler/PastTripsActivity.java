package com.vistara.traveler;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.vistara.traveler.ServerFile.ServerInterface;
import com.vistara.traveler.ServerFile.UrlConfig;
import com.vistara.traveler.utils.ApiResponseParser;
import com.vistara.traveler.utils.TelephonyManagerInfo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class PastTripsActivity extends AppCompatActivity implements ApiResponseParser {

    ListView listViewPastTrips ;
    public static ArrayList<PastTripBean> arrayListPastTrips ;
    public static int clickedPosition = 0 ;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_trips);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Past  Trips");

        arrayListPastTrips = new ArrayList<PastTripBean>();

        listViewPastTrips = (ListView) findViewById(R.id.listViewPastTripsId);

        String userId = AppController.tDb.getString(Constant.userId);
        String userType = AppController.tDb.getString(Constant.userType);
        String deviceId = TelephonyManagerInfo.getIMEI(PastTripsActivity.this);

        // UserType 1 for TRAVELLER and 2 for DRIVER
        JSONObject jsonObject = new JSONObject();
        try
        {
            jsonObject.put("userid",userId);
            jsonObject.put("type",userType);
            jsonObject.put("deviceid",deviceId);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        ServerInterface.getInstance(PastTripsActivity.this).makeServiceCall(userId,deviceId, UrlConfig.Past_Trips_API, jsonObject.toString(),PastTripsActivity.this,true, "Please wait...");



        listViewPastTrips.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                 if(AppController.tDb.getString(Constant.userType).equals("1"))
                 {
                     clickedPosition = position ;
                     Intent intent = new Intent(PastTripsActivity.this,TripDetailActivity.class);
                     startActivity(intent);
                 }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(menuItem);
    }


    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
        finish();
    }

    @Override
    public void parseResponse(String response)
    {
        if(response!=null && !response.equals(""))
        {
            if(response.equals("server_error"))
            {
                Toast.makeText(this, "Something went wrong !", Toast.LENGTH_SHORT).show();
            }
            else
            {
                try
                {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArrayTrip ;
                    if(jsonObject.has("trip"))
                    {
                        jsonArrayTrip = jsonObject.getJSONArray("trip");
                        if(jsonArrayTrip!=null && jsonArrayTrip.length()>0)
                        {
                            for(int a=0;a<jsonArrayTrip.length();a++)
                            {
                                PastTripBean pastTripBean   =  new PastTripBean();

                                pastTripBean.tripDate       = jsonArrayTrip.getJSONObject(a).getString("tripdate");
                                pastTripBean.tripType       = jsonArrayTrip.getJSONObject(a).getString("traveltype");
                                pastTripBean.driverName     = jsonArrayTrip.getJSONObject(a).getString("driver");
                                pastTripBean.driverVehicle  = jsonArrayTrip.getJSONObject(a).getString("car");
                                pastTripBean.pickupAddress  = jsonArrayTrip.getJSONObject(a).getString("pickupaddress");
                                pastTripBean.pickUpTime     = jsonArrayTrip.getJSONObject(a).getString("pickuptime");
                                pastTripBean.dropAddress    = jsonArrayTrip.getJSONObject(a).getString("destination");
                                pastTripBean.dropTargetTime = jsonArrayTrip.getJSONObject(a).getString("targettime");


                                try
                                {
                                    JSONArray jsonArrayFeedback   = jsonArrayTrip.getJSONObject(a).getJSONArray("feedback");
                                    if(jsonArrayFeedback!=null && jsonArrayFeedback.length()>0)
                                    {
                                        pastTripBean.ratingStar             = jsonArrayFeedback.getJSONObject(0).getString("rating");
                                        pastTripBean.selectedFeedbackValues = jsonArrayFeedback.getJSONObject(0).getString("button_text_values");
                                        pastTripBean.feedbackMessage        = jsonArrayFeedback.getJSONObject(0).getString("msg");
                                        if(pastTripBean.feedbackMessage==null || pastTripBean.feedbackMessage.equals(""))
                                        {
                                            pastTripBean.feedbackMessage = "N.A.";
                                        }
                                        pastTripBean.feedbackTimeAndDate    = jsonArrayFeedback.getJSONObject(0).getString("added_datetime");
                                        pastTripBean.replyMessage           = jsonArrayFeedback.getJSONObject(0).getString("sent_msg_reply");
                                        if(pastTripBean.replyMessage==null || pastTripBean.replyMessage.equals(""))
                                        {
                                            pastTripBean.replyMessage = "N.A.";
                                        }
                                        pastTripBean.replyTimeAndDate       = jsonArrayFeedback.getJSONObject(0).getString("sent_msg_datetime");
                                    }
                                    else
                                    {
                                        pastTripBean.ratingStar             = "";
                                        pastTripBean.feedbackMessage        = "";
                                        pastTripBean.selectedFeedbackValues = "";
                                        pastTripBean.feedbackTimeAndDate    = "";
                                        pastTripBean.replyMessage           = "";
                                        pastTripBean.replyTimeAndDate       = "";
                                    }
                                }
                                catch(Exception e)
                                {
                                    e.printStackTrace();
                                }


                                try
                                {
                                    JSONArray jsonArrayRestTravellers   = jsonArrayTrip.getJSONObject(a).getJSONArray("RestTravellers");
                                    if(jsonArrayRestTravellers!=null)
                                    {
                                        ArrayList<String> travellerName = new ArrayList<String>();
//                                        travellerName.add("Abhishek Yadav");
//                                        travellerName.add("Namresh Baghel");
//                                        travellerName.add("Ashok Saini");
//                                        travellerName.add("Chandan Gaur");
//                                        travellerName.add("Vijay Kumar Singh");
                                        if(jsonArrayRestTravellers.length()>0)
                                        {
                                            for(int z=0;z<jsonArrayRestTravellers.length();z++)
                                            {
                                                String fellowTravellerName =  jsonArrayRestTravellers.getJSONObject(z).getString("emp_name");
                                                travellerName.add(fellowTravellerName);
                                            }
                                            pastTripBean.arrayListTravellers = travellerName ;
                                        }
                                        else
                                        {
                                            pastTripBean.arrayListTravellers = travellerName ;
                                        }
                                    }
                                }
                                catch (Exception e)
                                {
                                    e.printStackTrace();
                                }


                                arrayListPastTrips.add(pastTripBean);
                            }
                        }
                    }
                    else
                    {
                        jsonArrayTrip = jsonObject.getJSONArray("info");
                        if(jsonArrayTrip!=null && jsonArrayTrip.length()>0)
                        {
                            for(int a=0;a<jsonArrayTrip.length();a++)
                            {
                                PastTripBean pastTripBean = new PastTripBean();
                                if(jsonArrayTrip.getJSONObject(a).getString("traveltype").equals("2"))
                                {
                                    pastTripBean.tripDate       = jsonArrayTrip.getJSONObject(a).getString("tripdate");
                                    pastTripBean.tripType       = jsonArrayTrip.getJSONObject(a).getString("traveltype");
                                    pastTripBean.driverVehicle  = "Traveller";
                                    pastTripBean.dropAddress    = jsonArrayTrip.getJSONObject(a).getString("destination");
                                    pastTripBean.dropTargetTime = jsonArrayTrip.getJSONObject(a).getString("targettime");

                                    JSONArray jsonArrayTravellers = jsonArrayTrip.getJSONObject(a).getJSONArray("travellers");
                                    if(jsonArrayTravellers!=null)
                                    {
                                        pastTripBean.pickupAddress  = jsonArrayTravellers.getJSONObject(0).getString("address");
                                        pastTripBean.pickUpTime     = jsonArrayTravellers.getJSONObject(0).getString("pickuptime");
                                        pastTripBean.driverName     = ""+jsonArrayTravellers.length();
                                    }
                                    else
                                    {
                                        pastTripBean.pickupAddress  = "";
                                        pastTripBean.pickUpTime     = "";
                                        pastTripBean.driverName     = "";
                                    }
                                }
                                else
                                {
                                    pastTripBean.tripDate       = jsonArrayTrip.getJSONObject(a).getString("tripdate");
                                    pastTripBean.tripType       = jsonArrayTrip.getJSONObject(a).getString("traveltype");
                                    pastTripBean.driverVehicle  = "Traveller";
                                    pastTripBean.pickupAddress  = jsonArrayTrip.getJSONObject(a).getString("pickupaddress");
                                    pastTripBean.pickUpTime     = jsonArrayTrip.getJSONObject(a).getString("pickuptime");

                                    JSONArray jsonArrayTravellers = jsonArrayTrip.getJSONObject(a).getJSONArray("travellers");
                                    if(jsonArrayTravellers!=null && jsonArrayTravellers.length()>0)
                                    {
                                        int lastItemPosition = jsonArrayTravellers.length()-1;
                                        pastTripBean.dropAddress    = jsonArrayTravellers.getJSONObject(lastItemPosition).getString("address");
                                        pastTripBean.dropTargetTime = jsonArrayTravellers.getJSONObject(lastItemPosition).getString("pickuptime");
                                        pastTripBean.driverName     = ""+jsonArrayTravellers.length();
                                    }
                                    else
                                    {
                                        pastTripBean.dropAddress    = "";
                                        pastTripBean.dropTargetTime = "";
                                        pastTripBean.driverName     = "";
                                    }
                                }

                                arrayListPastTrips.add(pastTripBean);
                            }
                        }
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            if(arrayListPastTrips.size()>0)
            {
                PastTripsAdapter pastTripsAdapter = new PastTripsAdapter(PastTripsActivity.this);
                listViewPastTrips.setAdapter(pastTripsAdapter);
            }
            else
            {
                Toast.makeText(this, "No Trip Found  !", Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            Toast.makeText(this, "Server Error  !", Toast.LENGTH_SHORT).show();
        }

    }
}
