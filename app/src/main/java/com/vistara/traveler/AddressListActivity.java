package com.vistara.traveler;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.vistara.traveler.adapter.AddressAdapter;
import com.vistara.traveler.adapter.TravellerAdapter;
import com.vistara.traveler.model.NotifictionBean;
import com.vistara.traveler.utils.ApiResponseParser;

import java.util.ArrayList;


/**
 * Created by Sharad on 13-02-2016.
 */
public class AddressListActivity extends AppCompatActivity implements ApiResponseParser {
    ListView _notificationList;
    ArrayList<NotifictionBean> notificationList;
   // public static int apiSataus = 1;
    public static String empId = "";
    public static AddressAdapter adapter;
    public static TravellerAdapter travellerAdapter;

    String destinationLat, destinationLon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.address_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Travelers");
        _notificationList = (ListView) findViewById(R.id.notificationList);
        ImageView notifiImage = (ImageView)  findViewById(R.id.notificationIV);
        ImageView destination = (ImageView)  findViewById(R.id.destinationIV);
        notifiImage.setVisibility(View.GONE);

        Bundle extras = getIntent().getExtras();
        String id = extras.getString("navigateFrom");
        // 1 for driver & 2 for traveler
        if(id.equals("1"))
        {
            destinationLat = extras.getString("lat");
            destinationLon = extras.getString("lon");
            destination.setVisibility(View.VISIBLE);
            adapter = new AddressAdapter(AddressListActivity.this, AddressListActivity.this, DriverHomePage.tripListMarker);
            _notificationList.setAdapter(adapter);
        }
        else
        {
            destination.setVisibility(View.GONE);
            travellerAdapter = new TravellerAdapter(AddressListActivity.this, TravelerHomePage.arrayListTraveller);
            _notificationList.setAdapter(travellerAdapter);
        }

        destination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("https://www.google.com/maps/dir/?api=1&destination="+destinationLat+","+destinationLon+"&travelmode=driving"));
                startActivity(intent);
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


    public void parseResponse(String res) {


    }

}
