package com.vistara.traveler;

import android.app.Activity;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PastTripsAdapter extends BaseAdapter {

    Activity context;

    PastTripsAdapter(Activity context)
    {
//        Collections.reverse(notificationList);
        this.context = context;
    }

    private class ViewHolder {
        TextView textViewDateId ;
        TextView textViewMonthId ;
        TextView textViewYearId ;
        TextView textViewTripTypeId ;
        TextView textViewSourceAddressId;
        TextView textViewPickUpTimeId ;
        TextView textViewDestAddressId ;
        TextView textViewDestTimeId ;
        TextView driverVehicleId ;
        TextView driverNameId ;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;


        LayoutInflater mInflater = (LayoutInflater)context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
        {
            convertView = mInflater.inflate(R.layout.past_trip_adapter_item, null);
            holder = new ViewHolder();

            holder.textViewDateId = (TextView) convertView.findViewById(R.id.textViewDateId);
            holder.textViewMonthId = (TextView) convertView.findViewById(R.id.textViewMonthId);
            holder.textViewYearId= (TextView) convertView.findViewById(R.id.textViewYearId);
            holder.textViewTripTypeId= (TextView) convertView.findViewById(R.id.textViewTripTypeId);

            holder.textViewSourceAddressId= (TextView) convertView.findViewById(R.id.textViewSourceAddressId);
            holder.textViewPickUpTimeId= (TextView) convertView.findViewById(R.id.textViewPickUpTimeId);
            holder.textViewDestAddressId= (TextView) convertView.findViewById(R.id.textViewDestAddressId);
            holder.textViewDestTimeId= (TextView) convertView.findViewById(R.id.textViewDestTimeId);

            holder.driverVehicleId= (TextView) convertView.findViewById(R.id.driverVehicleId);
            holder.driverNameId= (TextView) convertView.findViewById(R.id.driverNameId);




            convertView.setTag(holder);

        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        String day = "" ; // 20
        String monthString = "" ; // Jun
        String year = ""    ; // 2013
        try
        {
            String tripDate = PastTripsActivity.arrayListPastTrips.get(position).tripDate;
            SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
            Date dt1 = format1.parse(tripDate);

             day          = (String) DateFormat.format("dd",   dt1); // 20
             monthString  = (String) DateFormat.format("MMM",  dt1); // Jun
             year         = (String) DateFormat.format("yyyy", dt1); // 2013
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }


        holder.textViewDateId.setText(day);
        holder.textViewMonthId.setText(monthString);
        holder.textViewYearId.setText(year);

        String tripType= "";
        if(PastTripsActivity.arrayListPastTrips.get(position).tripType.equals("1"))
        {
            tripType = "Drop       "+"|" ;
        }
        else
        {
            tripType = "Pick Up    "+"|" ;
        }
        holder.textViewTripTypeId.setText(tripType);

        holder.textViewSourceAddressId.setText(PastTripsActivity.arrayListPastTrips.get(position).pickupAddress);
        holder.textViewPickUpTimeId.setText("( "+PastTripsActivity.arrayListPastTrips.get(position).pickUpTime+" )");
        holder.textViewDestAddressId.setText(PastTripsActivity.arrayListPastTrips.get(position).dropAddress);
        holder.textViewDestTimeId.setText("( "+PastTripsActivity.arrayListPastTrips.get(position).dropTargetTime+" )");

        holder.driverVehicleId.setText("    "+PastTripsActivity.arrayListPastTrips.get(position).driverVehicle);
        holder.driverNameId.setText(" ("+PastTripsActivity.arrayListPastTrips.get(position).driverName+")");


        return convertView;
    }

    @Override
    public int getCount() {
        return PastTripsActivity.arrayListPastTrips.size();
    }

    @Override
    public Object getItem(int position) {
        return PastTripsActivity.arrayListPastTrips.get(position);
    }

    @Override
    public long getItemId(int position) {
        return PastTripsActivity.arrayListPastTrips.indexOf(getItem(position));
    }

}
