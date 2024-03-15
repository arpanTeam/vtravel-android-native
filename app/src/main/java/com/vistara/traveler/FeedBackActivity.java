package com.vistara.traveler;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.vistara.traveler.LocationService.GPSTracker;
import com.vistara.traveler.ServerFile.ServerInterface;
import com.vistara.traveler.ServerFile.UrlConfig;
import com.vistara.traveler.singlton.UserDetail;
import com.vistara.traveler.utils.ApiResponseParser;
import com.vistara.traveler.utils.CompleteTrip;
import com.vistara.traveler.utils.CustomPopup;
import com.vistara.traveler.utils.TelephonyManagerInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FeedBackActivity extends AppCompatActivity implements ApiResponseParser {
    String ratingS="5";
    EditText input_FeedBack;
    RatingBar  ratingbar;
    TextView textMsg;
    CheckedTextView btn_Professionalism,btn_Driving,btn_PickUp,btn_Cleanliness,btn_Comfort,btn_Others;
    Button buttonSubmit;
    String typeFeedBack;
    RelativeLayout safeReachRl;
    TextView safeReachedText;
    ImageView safeReachIV;
    ApiResponseParser parseActivity;
    private int apiRes = 0;
    boolean isTripComplete = false;
    static int safeReachClicked = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_back);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Feedback");

        safeReachRl         = (RelativeLayout) findViewById(R.id.safeReach);
        safeReachIV         = (ImageView) findViewById(R.id.safeReachIV);
        safeReachedText     = (TextView) findViewById(R.id.safeReachedText);
        textMsg             = (TextView)findViewById(R.id.textTitleId);
        ratingbar           = (RatingBar) findViewById(R.id.ratingBar1);
        ratingbar.setRating(5);
        btn_Professionalism = (CheckedTextView)findViewById(R.id.ctv);
        btn_Driving         = (CheckedTextView)findViewById(R.id.btnDrivingId);
        btn_PickUp          = (CheckedTextView)findViewById(R.id.btnPickUpId);
        btn_Cleanliness     = (CheckedTextView)findViewById(R.id.btnCleanlinessId);
        btn_Comfort         = (CheckedTextView)findViewById(R.id.btnComfortId);
        btn_Others          = (CheckedTextView)findViewById(R.id.btnOthersId);

        input_FeedBack      = (EditText)findViewById(R.id.editFeedBackId);

        buttonSubmit        = (Button) findViewById(R.id.btnSubmitId);
        parseActivity = FeedBackActivity.this;

        Intent intent       = getIntent();
        isTripComplete      = intent.getBooleanExtra("isTripComplete", false);

//        if(isTripComplete)
        if(safeReachClicked == 0)
        {
            safeReachRl.setVisibility(View.VISIBLE);
        }
        else
        {
            safeReachRl.setVisibility(View.GONE);
        }

        ArrayList<String> btnTitleTextList = new ArrayList<>();
        btnTitleTextList.add("Courteous");
        btnTitleTextList.add("Driving Skills");
        btnTitleTextList.add("Timely Pickup");
        btnTitleTextList.add("Cleanliness");
        btnTitleTextList.add("Comfort");
        btnTitleTextList.add("Others");
        ratingbar.setStepSize(1);

        textMsg.setVisibility(View.GONE);
        btn_Professionalism.setVisibility(View.GONE);
        btn_Driving.setVisibility(View.GONE);
        btn_PickUp.setVisibility(View.GONE);
        btn_Cleanliness.setVisibility(View.GONE);
        btn_Comfort.setVisibility(View.GONE);
        btn_Others.setVisibility(View.GONE);


// SafeReach Button listener
        safeReachRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                apiRes = 1;
                double lat = 0.0;
                double lan = 0.0;

                if (TelephonyManagerInfo.isConnectingToInternet(FeedBackActivity.this)) {

                    JSONObject data = new JSONObject();
                    String userId = UserDetail.getInstance().getUserId();
                    String deviceId="";
                    try {
                        deviceId = TelephonyManagerInfo.getIMEI(FeedBackActivity.this);
                    }catch (Exception e)
                    {}

                    try {
                        GPSTracker gps = new GPSTracker(FeedBackActivity.this);
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
                        data.put("tripid", TravelerHomePage.tripId);
                        data.put("userid", userId);
                        data.put("deviceid", deviceId);
                        data.put("lat", lat);
                        data.put("lon", lan);
                        data.put("flag", "1");
                    }catch (Exception e)
                    {
                    }
                    ServerInterface.getInstance(FeedBackActivity.this).makeServiceCall(userId, deviceId, UrlConfig.t_reached_getready, data.toString(),
                            parseActivity, true, "Please wait...");
                }
                else
                {
                    CustomPopup cp = new CustomPopup(FeedBackActivity.this);
                    cp.commonPopup(getResources().getString(R.string.internet_connection));
                }
            }
        });


        ratingbar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {

            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                // TODO Auto-generated method stub
                ratingS = String.valueOf(rating);
                if(ratingS.equals("5.0"))
                {

//                    textMsg.setText("Give a compliment?");
//                    btn_Professionalism.setText("Entertaining Driver");
//                    btn_Driving.setText("Cool Car");
//                    btn_PickUp.setText("Awesome music");
//                    btn_Cleanliness.setText("Neat and Tidy");
//                    btn_Comfort.setText("Great Amenities");
//                    btn_Others.setText("Above and Beyond");

                    textMsg.setVisibility(View.GONE);
                    btn_Professionalism.setVisibility(View.GONE);
                    btn_Driving.setVisibility(View.GONE);
                    btn_PickUp.setVisibility(View.GONE);
                    btn_Cleanliness.setVisibility(View.GONE);
                    btn_Comfort.setVisibility(View.GONE);
                    btn_Others.setVisibility(View.GONE);
                }
                else
                {

                    textMsg.setVisibility(View.VISIBLE);
                    btn_Professionalism.setVisibility(View.VISIBLE);
                    btn_Driving.setVisibility(View.VISIBLE);
                    btn_PickUp.setVisibility(View.VISIBLE);
                    btn_Cleanliness.setVisibility(View.VISIBLE);
                    btn_Comfort.setVisibility(View.VISIBLE);
                    btn_Others.setVisibility(View.INVISIBLE);

                    textMsg.setText("What can be improved?");
                    btn_Professionalism.setText("Courteous");
                    btn_Driving.setText("Driving Skills");
                    btn_PickUp.setText("Timely Pickup");
                    btn_Cleanliness.setText("Cleanliness");
                    btn_Comfort.setText("Comfort");
                    btn_Others.setText("Other");
                }
            }
        });


      final JSONObject jObjTypeFedd = new JSONObject();

        ((CheckedTextView)findViewById(R.id.ctv)).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                boolean isChecked = ((CheckedTextView)findViewById(R.id.ctv)).isChecked();

                if(isChecked) {
                    ((CheckedTextView) findViewById(R.id.ctv)).setChecked(false);
                    typeFeedBack = "0";
                }
                else {
                    ((CheckedTextView) findViewById(R.id.ctv)).setChecked(true);
                    typeFeedBack = "1";

                }
                if(!typeFeedBack.equals("0")) {
                    try {
                        jObjTypeFedd.put("first",typeFeedBack);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }


            }
        });

        ((CheckedTextView)findViewById(R.id.btnDrivingId)).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                boolean isChecked = ((CheckedTextView)findViewById(R.id.btnDrivingId)).isChecked();

                if(isChecked)
                {
                    typeFeedBack = "0";
                    ((CheckedTextView) findViewById(R.id.btnDrivingId)).setChecked(false);
                }
                else
                {
                    ((CheckedTextView) findViewById(R.id.btnDrivingId)).setChecked(true);
                    typeFeedBack = "2";
                }
                if(!typeFeedBack.equals("0"))
                {
                    try
                    {
                        jObjTypeFedd.put("second",typeFeedBack);
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        });

        ((CheckedTextView)findViewById(R.id.btnPickUpId)).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                boolean isChecked = ((CheckedTextView)findViewById(R.id.btnPickUpId)).isChecked();

                if(isChecked) {
                    ((CheckedTextView) findViewById(R.id.btnPickUpId)).setChecked(false);
                    typeFeedBack = "0";

                }
                else {
                    ((CheckedTextView) findViewById(R.id.btnPickUpId)).setChecked(true);
                    typeFeedBack = "4";
                }
                if(!typeFeedBack.equals("0")) {
                    try {
                        jObjTypeFedd.put("third",typeFeedBack);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        ((CheckedTextView)findViewById(R.id.btnCleanlinessId)).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                boolean isChecked = ((CheckedTextView)findViewById(R.id.btnCleanlinessId)).isChecked();

                if(isChecked) {
                    ((CheckedTextView) findViewById(R.id.btnCleanlinessId)).setChecked(false);
                    typeFeedBack = "0";

                }
                else {
                    ((CheckedTextView) findViewById(R.id.btnCleanlinessId)).setChecked(true);
                    typeFeedBack = "5";

                }
                if(!typeFeedBack.equals("0")) {
                    try {
                        jObjTypeFedd.put("fourth",typeFeedBack);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
           });
        ((CheckedTextView)findViewById(R.id. btnComfortId)).setOnClickListener(new View.OnClickListener() {

           @Override
           public void onClick(View arg0) {

               boolean isChecked = ((CheckedTextView) findViewById(R.id.btnComfortId)).isChecked();

               if (isChecked) {
                   ((CheckedTextView) findViewById(R.id.btnComfortId)).setChecked(false);
                   typeFeedBack = "0";

               }
               else {
                   ((CheckedTextView) findViewById(R.id.btnComfortId)).setChecked(true);
                   typeFeedBack = "3";

               }
               if(!typeFeedBack.equals("0")) {
                   try {
                       jObjTypeFedd.put("fifth",typeFeedBack);
                   } catch (JSONException e) {
                       e.printStackTrace();
                   }
               }
           }
       });


        ((CheckedTextView)findViewById(R.id. btnOthersId)).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                boolean isChecked = ((CheckedTextView) findViewById(R.id.btnOthersId)).isChecked();

                if (isChecked) {
                    ((CheckedTextView) findViewById(R.id.btnOthersId)).setChecked(false);
                    typeFeedBack = "0";

                }
                else {
                    ((CheckedTextView) findViewById(R.id.btnOthersId)).setChecked(true);
                    typeFeedBack = "6";

                }
                if(!typeFeedBack.equals("0")) {
                    try {
                        jObjTypeFedd.put("sixth",typeFeedBack);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });



        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                apiRes = 2;
                String JsonObjString = jObjTypeFedd.toString();
                String user = UserDetail.getInstance().getUserId();
                String deviceId = "";
                try
                {
                    TelephonyManager telephonyManager = (TelephonyManager) FeedBackActivity.this.getSystemService(Context.TELEPHONY_SERVICE);
                    deviceId = telephonyManager.getDeviceId();
                }
                catch (SecurityException e)
                {
                    Log.e("","");
                }
                catch (Exception e)
                {
                    Log.e("","");
                }
                JSONObject dataObj = new JSONObject();
                try {

                  //  dataObj.put("userid", user);
                    dataObj.put("carid", TravelerHomePage.carId);
                    dataObj.put("tripid", TravelerHomePage.tripId);
                 //   dataObj.put("deviceid", deviceId);
                    dataObj.put("feedback", input_FeedBack.getText().toString());
                    dataObj.put("rating", ratingS);

                    try{
                        JSONObject jObj = new JSONObject(JsonObjString);
                        JSONObject newObj =  new JSONObject();
                        if(jObj.has("first"))
                        {
                            newObj.put("first", jObj.getString("first"));
                        }

                        if(jObj.has("second"))
                        {
                            newObj.put("second", jObj.getString("second"));
                        }

                        if(jObj.has("third"))
                        {
                            newObj.put("third", jObj.getString("third"));
                        }

                        if(jObj.has("fourth"))
                        {
                            newObj.put("fourth", jObj.getString("fourth"));
                        }

                        if(jObj.has("fifth"))
                        {
                            newObj.put("fifth", jObj.getString("fifth"));
                        }

                        if(jObj.has("sixth"))
                        {
                            newObj.put("sixth", jObj.getString("sixth"));
                        }

                        dataObj.put("button_text", newObj);
                    }

                    catch (Exception e)
                    {

                    }

                 //   Log.e("","");
                }catch (Exception e)
                {
                    e.printStackTrace();
                }

                if(TelephonyManagerInfo.isConnectingToInternet(FeedBackActivity.this)) {
                    ServerInterface.getInstance(FeedBackActivity.this).makeServiceCall(user, deviceId, UrlConfig.userFeedback,
                            dataObj.toString(), FeedBackActivity.this, true, "Please wait...");
                }else {
                    CustomPopup cp = new CustomPopup(FeedBackActivity.this);
                    cp.commonPopup(getResources().getString(R.string.internet_connection));
                }


                // new FeedBackAsync().execute(ratingS,input_FeedBack.getText().toString(),JsonObjString);
            }
        });
    }

    public void parseResponse(String response) {
        try {

            if(response == null || response.equals("") || response.equals("server_error"))
            {
                final AlertDialog.Builder builder =  new AlertDialog.Builder(FeedBackActivity.this);
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
            else if(response.equals("Not Allowed"))
            {
                final AlertDialog.Builder builder =  new AlertDialog.Builder(FeedBackActivity.this);
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
                return;
            }
            else
            {
                JSONObject jsObj =  new JSONObject(response);



//                added by Vijay
                if (apiRes == 1) {            // reach safe

                    safeReachClicked++;
                    try {
                        String status = jsObj.getString("api_status");
                        String msg = jsObj.getString("msg");
                        if(status.equals("1")) {
                            CompleteTrip cp1 = TravelerHomePage.cTripObj;
//                            cp1.completeTrip();
                        }
                        CustomPopup cp = new CustomPopup(FeedBackActivity.this);
                        cp.safeReachedPopup(msg, "OK");
//                        cp.twoButtonDynamicPopup(msg, "feedback_pop", true, true, "Cancel", "Feedback");
                    } catch (Exception e) {
                    }

                    safeReachRl.setVisibility(View.GONE);

                    return;
                }





                if (apiRes == 2)
                {
                    String satus = jsObj.getString("status");
                    if(satus.equals("1"))
                    {
                        ((CheckedTextView) findViewById(R.id.ctv)).setChecked(false);
                        ((CheckedTextView) findViewById(R.id.btnComfortId)).setChecked(false);
                        ((CheckedTextView) findViewById(R.id.btnCleanlinessId)).setChecked(false);
                        ((CheckedTextView) findViewById(R.id.btnOthersId)).setChecked(false);
                        ((CheckedTextView) findViewById(R.id.btnDrivingId)).setChecked(false);
                        ((CheckedTextView) findViewById(R.id.btnPickUpId)).setChecked(false);
                        input_FeedBack.setText("");
                        Toast.makeText(FeedBackActivity.this,"Thanks For Your Valuable FeedBack !",Toast.LENGTH_LONG).show();

                        AppController.tDb.putString(Constant.FEEDBACK_STATUS,"1");
                        AppController.tDb.putString(Constant.DUPLICATE_TRIP_ID,TravelerHomePage.tripId);

                        if(isTripComplete)
                        {
                            TravelerHomePage.feedbackRl.setBackgroundColor(getResources().getColor(R.color.disable_button_color));
                            TravelerHomePage.feedbackRl.setEnabled(false);
                            TravelerHomePage.feedbackRl.setFocusable(false);
                            TravelerHomePage.travelersRl.setVisibility(View.GONE);
                            FeedBackActivity.this.finish();
                        }
                        else
                        {
                            FeedBackActivity.this.finish();
                        }


                    }
                }



            }


        }catch (Exception e)
        {
            Toast.makeText(FeedBackActivity.this,getResources().getString(R.string.error_message),Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}




