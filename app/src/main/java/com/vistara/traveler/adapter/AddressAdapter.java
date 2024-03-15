package com.vistara.traveler.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.vistara.traveler.AddressListActivity;
import com.vistara.traveler.AppController;
import com.vistara.traveler.Constant;
import com.vistara.traveler.DriverHomePage;
import com.vistara.traveler.R;
import com.vistara.traveler.ServerFile.ServerInterface;
import com.vistara.traveler.ServerFile.UrlConfig;
import com.vistara.traveler.model.DDriverDetail;
import com.vistara.traveler.model.DTravellerStatus;
import com.vistara.traveler.model.NotifictionBean;
import com.vistara.traveler.singlton.UserDetail;
import com.vistara.traveler.utils.ApiResponseParser;
import com.vistara.traveler.utils.CustomPopup;
import com.vistara.traveler.utils.TelephonyManagerInfo;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;


/**
 * Created by vinaymahipal on 2/16/2016.
 */
public class AddressAdapter extends BaseAdapter {

    List<DDriverDetail> traverDeatil;
    ApiResponseParser parseActivity;
    Activity activity;
    String [] lat, lon;
    // ImageLoader imageLoader;

    ArrayList<Button> waitingButtonList;
    ArrayList<Button> arrivingButtonList;

    public AddressAdapter(Activity act, ApiResponseParser activity, List<DDriverDetail> tripListMarker) {
        // TODO Auto-generated constructor stub
    //	Collections.reverse(notificationList);
        this.traverDeatil =  tripListMarker.get(0).traverDeatil;//tripListMarker;
        this.activity = act;
        parseActivity = activity;
        waitingButtonList = new ArrayList<>();
        arrivingButtonList = new ArrayList<>();
        lat = new String[this.traverDeatil.size()];
        lon = new String[this.traverDeatil.size()];
    }

    /*private view holder class*/
    private class ViewHolder {
        TextView name, time;
        TextView address,mobileNo, notReportedTxt;
        Button arrivingButton, waitingButton, notRepordButton,pickedUpButton;
        RelativeLayout buttonLay;
        ImageView navigateIcon, imgCallIconId ;
    }


    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;

        LayoutInflater mInflater = (LayoutInflater)
                activity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.address_list_adapter, null);
            holder = new ViewHolder();

            holder.name             = (TextView) convertView.findViewById(R.id.name);
            holder.address          = (TextView) convertView.findViewById(R.id.address);
            holder.time             = (TextView) convertView.findViewById(R.id.time);
            holder.imgCallIconId    = (ImageView) convertView.findViewById(R.id.imgCallIconId);
            holder.navigateIcon     = (ImageView) convertView.findViewById(R.id.navigate);

            holder.mobileNo         = (TextView) convertView.findViewById(R.id.mobileNo);
            holder.notReportedTxt   = (TextView) convertView.findViewById(R.id.not_reported_text);

            holder.arrivingButton   = (Button) convertView.findViewById(R.id.arrivingButton1);
            holder.waitingButton    = (Button) convertView.findViewById(R.id.waitingButton);
            holder.notRepordButton  = (Button) convertView.findViewById(R.id.notRepordButton);
            holder.pickedUpButton   = (Button) convertView.findViewById(R.id.pickedUpButton);
            holder.buttonLay        =  (RelativeLayout) convertView.findViewById(R.id.buttonLay1);

            waitingButtonList.add(holder.waitingButton);
            arrivingButtonList.add(holder.arrivingButton);
            convertView.setTag(holder);

        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }


        GradientDrawable bgShape1 = (GradientDrawable) holder.notRepordButton.getBackground();
        bgShape1.setColor(activity.getResources().getColor(R.color.light_red));

        GradientDrawable bgShape2 = (GradientDrawable) holder.pickedUpButton.getBackground();
        bgShape2.setColor(activity.getResources().getColor(R.color.green));

        GradientDrawable bgShape = (GradientDrawable) holder.waitingButton.getBackground();
        bgShape.setColor(activity.getResources().getColor(R.color.yellow));

//        try {

        final GradientDrawable bgShape3  = (GradientDrawable) holder.arrivingButton.getBackground();
        bgShape3.setColor(activity.getResources().getColor(R.color.yellow));
//        }catch (Exception e) {
//            e.printStackTrace();
//        }


        final String id         = traverDeatil.get(position).id;
        String name             = traverDeatil.get(position).name;
        final String empcode    = traverDeatil.get(position).empcode;
        String address          = traverDeatil.get(position).address;
        String pickuptime       = traverDeatil.get(position).pickuptime;
        final String loggedin   = traverDeatil.get(position).loggedin;
        final String otp        = traverDeatil.get(position).otp;
        final String mobileNo   = traverDeatil.get(position).mobileNo;
        lat[position]           = traverDeatil.get(position).lat;
        lon[position]           = traverDeatil.get(position).lon;

        holder.name.setText(name);
        holder.address.setText(address);
        holder.time.setText(pickuptime);
        holder.mobileNo.setText(mobileNo);
        holder.time.setText(pickuptime);

        if(mobileNo.equals("") || mobileNo==null || mobileNo.length()==0)
        {
            holder.imgCallIconId.setVisibility(View.GONE);
        }

        holder.imgCallIconId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_DIAL);

                try
                {
                    intent.setData(Uri.parse("tel:" + mobileNo));
                    activity.startActivity(intent);
                }
                catch (Exception e)
                {

                }

            }
        });


        if(AppController.tDb.getString(Constant.tripType).equals("1"))
            holder.time.setVisibility(View.GONE);
        else
            holder.time.setVisibility(View.VISIBLE);

        ArrayList<DTravellerStatus> statusList = (ArrayList) AppController.tDb.getListObject(Constant.dTravellerStatus, DTravellerStatus.class);
        if(statusList.size()>0) {
            for(int i=0; i<statusList.size(); i++) {
                if(statusList.get(i).getTravellerId().equals(id)) {
                    if(statusList.get(i).getTravellerStatus().equals("1")) {
                        bgShape3.setColor(activity.getResources().getColor(R.color.disable_button_color));
                        bgShape3.setCornerRadius(5);
                        holder.arrivingButton.setBackground(bgShape3);
                        holder.arrivingButton.setVisibility(View.VISIBLE);
                        holder.arrivingButton.setEnabled(false);
                        holder.arrivingButton.setFocusable(false);
                        holder.waitingButton.setVisibility(View.GONE);
                    }else {
                        holder.waitingButton.setVisibility(View.VISIBLE);
                        holder.arrivingButton.setVisibility(View.GONE);
                    }
                    break;
                }

//                if(statusList.size()==(i+1)) {
//                    holder.arrivingButton.setEnabled(true);
//                    holder.arrivingButton.setFocusable(true);
//                    holder.arrivingButton.setVisibility(View.VISIBLE);
//                    holder.waitingButton.setVisibility(View.GONE);
//                    holder.arrivingButton.setBackgroundColor(activity.getResources().getColor(R.color.yellow));
//                }

            }
        }


        if (DriverHomePage.pickesEmpStatus.size() > 0 || DriverHomePage.notReportedEmpStatus.size() > 0)
        {
            if (DriverHomePage.pickesEmpStatus.contains(id))
            {
                lat[position]   = DriverHomePage.destinationLat;
                lon[position]   = DriverHomePage.destinationLon;
                holder.buttonLay.setVisibility(View.GONE);
            }
            else if (DriverHomePage.notReportedEmpStatus.contains(id))
            {
                lat[position]   = DriverHomePage.destinationLat;
                lon[position]   = DriverHomePage.destinationLon;
                holder.buttonLay.setVisibility(View.GONE);
                holder.notReportedTxt.setText("Not Reported");
            }
            else
            {
                holder.buttonLay.setVisibility(View.VISIBLE);
            }

        }
        else
        {
            if (loggedin.equals("3"))
            {
                lat[position]   = DriverHomePage.destinationLat;
                lon[position]   = DriverHomePage.destinationLon;
                holder.buttonLay.setVisibility(View.GONE);
            }
            else if(loggedin.equals("2"))
            {
                lat[position]   = DriverHomePage.destinationLat;
                lon[position]   = DriverHomePage.destinationLon;
                holder.buttonLay.setVisibility(View.GONE);
                holder.notReportedTxt.setText("Not Reported");
            }
        }


        holder.navigateIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("https://www.google.com/maps/dir/?api=1&destination="+lat[position]+","+lon[position]+"&travelmode=driving"));
                activity.startActivity(intent);
            }
        });//&origin=24.924999,71.659721


        holder.waitingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lat[position] = DriverHomePage.destinationLat;
                lon[position] = DriverHomePage.destinationLon;

                DriverHomePage.apiSataus = 4;
                JSONObject data = new JSONObject();
                String userId = UserDetail.getInstance().getUserId();
                String deviceId="";
                try {
                    deviceId = TelephonyManagerInfo.getIMEI(activity);
                }catch (Exception e)
                {}
                try {
                    data.put("carid", DriverHomePage.firstCarId);
                    data.put("tripid", DriverHomePage.firstTripId);
                    data.put("emp_id", empcode);
                    data.put("empcode", "");
                    data.put("status", "1");
                }catch (Exception e) {
                        e.printStackTrace();
                }
                if(TelephonyManagerInfo.isConnectingToInternet(activity)) {
                    ServerInterface.getInstance(activity).makeServiceCall(userId, deviceId, UrlConfig.updateTravellerStatus, data.toString(),
                            DriverHomePage.apiResActivity, true, "Please wait...");
                }else
                {
                    CustomPopup cp = new CustomPopup(activity);
                    cp.commonPopup(activity.getResources().getString(R.string.internet_connection));
                }


            }
        });



        holder.notRepordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                DriverHomePage.apiSataus = 5;
//                DriverHomePage.empId = id;
//                JSONObject data = new JSONObject();
//                String deviceId="";
//                String userId = UserDetail.getInstance().getUserId();
//                try {
//                    deviceId = TelephonyManagerInfo.getIMEI(activity);
//                }catch (Exception e)
//                {}
//                try {
//                 //   data.put("userid", UserDetail.getInstance().getUserId());
//                    data.put("carid", DriverHomePage.firstCarId);
//                    data.put("tripid", DriverHomePage.firstTripId);
//                    data.put("emp_id", empcode);
//                    data.put("empcode", "");
//                    data.put("status", "2");
//                }catch (Exception e)
//                {
//                }
//
//                if(!DriverHomePage.notReportedEmpStatus.contains(id))
//                    DriverHomePage.notReportedEmpStatus.add(id);
//
//                if(TelephonyManagerInfo.isConnectingToInternet(activity)) {
//                    ServerInterface.getInstance(activity).makeServiceCall(userId, deviceId, UrlConfig.updateTravellerStatus, data.toString(),
//                            DriverHomePage.apiResActivity, true, "Please wait...");
//                }else
//                {
//                    CustomPopup cp = new CustomPopup(activity);
//                    cp.commonPopup(activity.getResources().getString(R.string.internet_connection));
//                }

                lat[position] = DriverHomePage.destinationLat;
                lon[position] = DriverHomePage.destinationLon;

                DriverHomePage.apiSataus = 5;
                DriverHomePage.empId = id;

                JSONObject data = new JSONObject();
                String userId = UserDetail.getInstance().getUserId();
                String deviceId="";
                try {
                    deviceId = TelephonyManagerInfo.getIMEI(activity);
                }catch (Exception e)
                {}
                try {
                    //   data.put("userid", UserDetail.getInstance().getUserId());
                    data.put("carid", DriverHomePage.firstCarId);
                    data.put("tripid", DriverHomePage.firstTripId);
                    data.put("emp_id", empcode);
                    data.put("empcode", "");
                    data.put("status", "2");
                }catch (Exception e)
                {
                }

                holder.buttonLay.setVisibility(View.GONE);
                holder.notReportedTxt.setText("Not Reported");

                if(!DriverHomePage.notReportedEmpStatus.contains(id))
                    DriverHomePage.notReportedEmpStatus.add(id);


                if(TelephonyManagerInfo.isConnectingToInternet(activity))
                {
                    ServerInterface.getInstance(activity).makeServiceCall(userId, deviceId,UrlConfig.updateTravellerStatus, data.toString(),
                            DriverHomePage.apiResActivity, true, "Please wait...");
                }
                else
                {
                    CustomPopup cp = new CustomPopup(activity);
                    cp.commonPopup(activity.getResources().getString(R.string.internet_connection));
                }








            }
        });



        holder.arrivingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lat[position] = DriverHomePage.destinationLat;
                lon[position] = DriverHomePage.destinationLon;

                DriverHomePage.apiSataus = 8;
                DriverHomePage.empId = id;
                JSONObject data = new JSONObject();
                String userId = UserDetail.getInstance().getUserId();
                String deviceId="";
                try {
                    deviceId = TelephonyManagerInfo.getIMEI(activity);
                }catch (Exception e)
                {}
                try {
                  //  data.put("userid", UserDetail.getInstance().getUserId());
                    data.put("carid", DriverHomePage.firstCarId);
                    data.put("tripid", DriverHomePage.firstTripId);
                    data.put("emp_id", empcode);
                    data.put("empcode", "");
                    data.put("status", "4");
                }catch (Exception e)
                {
                }

                if(TelephonyManagerInfo.isConnectingToInternet(activity)) {
                    bgShape3.setColor(activity.getResources().getColor(R.color.disable_button_color));
                    bgShape3.setCornerRadius(5);
                    holder.arrivingButton.setBackground(bgShape3);
                    ServerInterface.getInstance(activity).makeServiceCall(userId, deviceId, UrlConfig.updateTravellerStatus, data.toString(),
                            DriverHomePage.apiResActivity, true, "Please wait...");
                }else {
                    CustomPopup cp = new CustomPopup(activity);
                    cp.commonPopup(activity.getResources().getString(R.string.internet_connection));
                }


            }
        });


        holder.pickedUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lat[position] = DriverHomePage.destinationLat;
                lon[position] = DriverHomePage.destinationLon;

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
                final EditText travellerOtp = (EditText)dialog1.  findViewById(R.id.travellerOtp);
                travellerOtp.setVisibility(View.VISIBLE);
                rl.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        dialog1.dismiss();

                        DriverHomePage.apiSataus = 6;
                        DriverHomePage.empId = id;
                        String value = travellerOtp.getText().toString();

                        String otpLowerCase = otp.toLowerCase();
                        String valueLowerCase="";
                        if(!value.equals(""))
                            valueLowerCase = value.toLowerCase();

                        if(valueLowerCase.equals(""))
                        {
                            Toast.makeText(activity, "Please enter traveler OTP", Toast.LENGTH_LONG).show();
                            return;
                        }else if(!valueLowerCase.equals(otpLowerCase))
                        {
                            Toast.makeText(activity, "Please enter correct OTP", Toast.LENGTH_LONG).show();
                            return;
                        }

                        JSONObject data = new JSONObject();
                        String userId = UserDetail.getInstance().getUserId();
                        String deviceId="";
                        try {
                            deviceId = TelephonyManagerInfo.getIMEI(activity);
                        }catch (Exception e)
                        {}
                        try {
                         //   data.put("userid", UserDetail.getInstance().getUserId());
                            data.put("carid", DriverHomePage.firstCarId);
                            data.put("tripid", DriverHomePage.firstTripId);
                            data.put("emp_id", empcode);
                            data.put("empcode", value);
                            data.put("status", "3");
                        }catch (Exception e)
                        {
                        }


                        if(!DriverHomePage.pickesEmpStatus.contains(id))
                             DriverHomePage.pickesEmpStatus.add(id);


                        if(TelephonyManagerInfo.isConnectingToInternet(activity)) {
                            ServerInterface.getInstance(activity).makeServiceCall(userId, deviceId,UrlConfig.updateTravellerStatus, data.toString(),
                                    DriverHomePage.apiResActivity, true, "Please wait...");
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
                                DriverHomePage.pickesEmpStatus.add(id);          // for current employee picked status
                            }catch (Exception e)
                            {

                            }
                        }


                    }
                });

                RelativeLayout cancel = (RelativeLayout)dialog1. findViewById(R.id.cancel);
                cancel.setVisibility(View.GONE);
                dialog1.show();
            }
        });

        return convertView;
    }

    @Override
    public int getCount() {
        return traverDeatil.size();
    }

    @Override
    public Object getItem(int position) {
        return traverDeatil.get(position);
    }

    @Override
    public long getItemId(int position) {
        return traverDeatil.indexOf(getItem(position));
    }

}
