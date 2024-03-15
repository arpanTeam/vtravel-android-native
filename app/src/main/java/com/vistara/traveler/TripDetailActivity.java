package com.vistara.traveler;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class TripDetailActivity extends AppCompatActivity {

    ListView listViewPastTrips ;
    public static ArrayList<PastTripBean> arrayListPastTrips ;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Trip  Detail");

        String tripDateAndTime = PastTripsActivity.arrayListPastTrips.get(PastTripsActivity.clickedPosition).tripDate+", "+PastTripsActivity.arrayListPastTrips.get(PastTripsActivity.clickedPosition).pickUpTime ;

        TextView textViewTripDateAndTimeId = (TextView)findViewById(R.id.textViewTripDateAndTimeId);
        TextView textViewDriverVehicleId = (TextView)findViewById(R.id.textViewDriverVehicleId);
        TextView textViewPickUpAddressId = (TextView)findViewById(R.id.textViewPickUpAddressId);
        TextView textViewDropAddressId = (TextView)findViewById(R.id.textViewDropAddressId);
        TextView textViewTravellersCountId = (TextView)findViewById(R.id.textViewTravellersCountId);
        TextView textViewTravellersNameId = (TextView)findViewById(R.id.textViewTravellersNameId);
        LinearLayout linearLayoutRatingId = (LinearLayout)findViewById(R.id.linearLayoutRatingId);
        RelativeLayout relLayFeedbackId = (RelativeLayout)findViewById(R.id.relLayFeedbackId);

        TextView textViewMsgId = (TextView)findViewById(R.id.textViewMsgId);
        TextView textViewMsgReplyId = (TextView)findViewById(R.id.textViewMsgReplyId);

        LinearLayout linearLayoutSelectedValuesFirstId = (LinearLayout)findViewById(R.id.linearLayoutSelectedValuesFirstId);
        LinearLayout linearLayoutSelectedValuesSecondId = (LinearLayout)findViewById(R.id.linearLayoutSelectedValuesSecondId);


        textViewTripDateAndTimeId.setText(tripDateAndTime);
        textViewDriverVehicleId.setText(PastTripsActivity.arrayListPastTrips.get(PastTripsActivity.clickedPosition).driverVehicle+" - "+PastTripsActivity.arrayListPastTrips.get(PastTripsActivity.clickedPosition).driverName);
        textViewPickUpAddressId.setText(PastTripsActivity.arrayListPastTrips.get(PastTripsActivity.clickedPosition).pickupAddress);
        textViewDropAddressId.setText(PastTripsActivity.arrayListPastTrips.get(PastTripsActivity.clickedPosition).dropAddress);

        textViewTravellersCountId.setText(" ("+String.valueOf(PastTripsActivity.arrayListPastTrips.get(PastTripsActivity.clickedPosition).arrayListTravellers.size())+")");
        if(PastTripsActivity.arrayListPastTrips.get(PastTripsActivity.clickedPosition).arrayListTravellers.size()>0)
        {
            String travellerName = "";
            int travellersCount = PastTripsActivity.arrayListPastTrips.get(PastTripsActivity.clickedPosition).arrayListTravellers.size();
            for(int n=0;n<travellersCount;n++)
            {
                travellerName = travellerName+String.valueOf(n+1)+". "+PastTripsActivity.arrayListPastTrips.get(PastTripsActivity.clickedPosition).arrayListTravellers.get(n)+"\n";
            }
            textViewTravellersNameId.setVisibility(View.VISIBLE);
            textViewTravellersNameId.setText(travellerName);
        }


        if(PastTripsActivity.arrayListPastTrips.get(PastTripsActivity.clickedPosition).ratingStar!=null && !PastTripsActivity.arrayListPastTrips.get(PastTripsActivity.clickedPosition).ratingStar.equals(""))
        {
            try
            {
                int rating = Integer.parseInt(PastTripsActivity.arrayListPastTrips.get(PastTripsActivity.clickedPosition).ratingStar);
                for(int a=0;a<rating;a++)
                {
                    ImageView imageView = new ImageView(TripDetailActivity.this);
                    imageView.setImageResource(R.drawable.star);
//                    imageView.setBackgroundColor(getResources().getColor(R.color.golden));
                    linearLayoutRatingId.addView(imageView);
                }

                if(rating!=5)
                {
                    String feedbackValues = PastTripsActivity.arrayListPastTrips.get(PastTripsActivity.clickedPosition).selectedFeedbackValues;
                    if(feedbackValues!=null && !feedbackValues.equals(""))
                    {
                        String[] selectedValues = feedbackValues.split(",");
                        for(int x=0;x<selectedValues.length;x++)
                        {
                            String text = "";
                            if(selectedValues[x].equals("1"))
                            {
                                text = "Courteous";
                            }
                            else if(selectedValues[x].equals("2"))
                            {
                                text = "Driving Skills";
                            }
                            else if(selectedValues[x].equals("3"))
                            {
                                text = "Comfort";
                            }
                            else if(selectedValues[x].equals("4"))
                            {
                                text = "Timely Pickup";
                            }
                            else if(selectedValues[x].equals("5"))
                            {
                                text = "Cleanliness";
                            }

                            TextView textView = new TextView(TripDetailActivity.this);
                            textView.setText(text);
                            textView.setTextColor(Color.WHITE);
                            textView.setBackground(getResources().getDrawable(R.drawable.feedback_button));

                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            params.setMargins(20,5,20,5);
                            textView.setLayoutParams(params);

                            if(x<3)
                            {
                                linearLayoutSelectedValuesFirstId.setVisibility(View.VISIBLE);
                                linearLayoutSelectedValuesFirstId.addView(textView);
                            }
                            else
                            {
                                linearLayoutSelectedValuesSecondId.setVisibility(View.VISIBLE);
                                linearLayoutSelectedValuesSecondId.addView(textView);
                            }
                        }
                    }
                    else
                    {
                        linearLayoutSelectedValuesFirstId.setVisibility(View.GONE);
                        linearLayoutSelectedValuesSecondId.setVisibility(View.GONE);
                    }
                }

                textViewMsgId.setText(":  "+PastTripsActivity.arrayListPastTrips.get(PastTripsActivity.clickedPosition).feedbackMessage);
                textViewMsgReplyId.setText(":  "+PastTripsActivity.arrayListPastTrips.get(PastTripsActivity.clickedPosition).replyMessage);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            relLayFeedbackId.setVisibility(View.GONE);
        }




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


}
