package com.vistara.traveler;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.vistara.traveler.adapter.NotificationAdapter;
import com.vistara.traveler.database.DatabaseAdapter;
import com.vistara.traveler.model.NotifictionBean;
import com.vistara.traveler.utils.CustomPopup;
import java.util.ArrayList;


/**
 * Created by Sharad on 13-02-2016.
 */
public class NotificationActivity extends AppCompatActivity {
    ListView _notificationList;
    ArrayList<NotifictionBean> notificationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Notifications");
        _notificationList = (ListView) findViewById(R.id.notificationList);
        ImageView notifiImage = (ImageView)  findViewById(R.id.notificationIV);
   //     notificationList;// = new ArrayList<>();
        DatabaseAdapter db = new DatabaseAdapter(this);
        notificationList = db.getNotifi();
        if(notificationList.size()==0) {
            notifiImage.setVisibility(View.VISIBLE);
            _notificationList.setVisibility(View.GONE);
        }else
        {
            notifiImage.setVisibility(View.GONE);
            _notificationList.setVisibility(View.VISIBLE);
            NotificationAdapter adapter = new NotificationAdapter(this, notificationList);
            _notificationList.setAdapter(adapter);

        }


        _notificationList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,int position, long arg3)
            {
                CustomPopup cp = new CustomPopup(NotificationActivity.this);
                cp.commonPopup(notificationList.get(position).message);
            }
        });

      }


    static class CustomArrayAdapter<CompanyBean> extends ArrayAdapter<CompanyBean>
	 {
	     public CustomArrayAdapter(Context ctx, ArrayList<CompanyBean> objects)
	     {
	         super(ctx, R.layout.spinnertext3, objects);
	     }

	     //other constructors

	     @Override
	     public View getDropDownView(int position, View convertView, ViewGroup parent)
	     {
	         View view = super.getView(position, convertView, parent);

	             TextView text = (TextView)view.findViewById(R.id.spinnertext);
	             text.setTextColor(Color.BLACK);//choose your color :)
	         

	         return view;

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
