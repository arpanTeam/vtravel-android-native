package com.vistara.traveler.adapter;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.vistara.traveler.R;
import com.vistara.traveler.model.DDriverDetail;
import com.vistara.traveler.model.TravelerDetail;
import com.vistara.traveler.utils.ApiResponseParser;

import java.util.ArrayList;

public class TravellerAdapter extends BaseAdapter {
    ArrayList<TravelerDetail> arrayListTraveller;
    Activity activity;


    public TravellerAdapter(Activity act, ArrayList<TravelerDetail> arrayListTraveller)
    {
        this.arrayListTraveller = arrayListTraveller;
        this.activity = act;
    }

    /*private view holder class*/
    private class ViewHolder
    {
        TextView id, name, mobile, address,travellerPickUpStatus;
        ImageView call_btn;
    }


    public View getView(int position, View convertView, ViewGroup parent) {
        final TravellerAdapter.ViewHolder holder;

        LayoutInflater mInflater = (LayoutInflater) activity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
        {
            convertView     = mInflater.inflate(R.layout.traveller_list_adapter, null);
            holder          = new TravellerAdapter.ViewHolder();

            holder.id       = (TextView) convertView.findViewById(R.id.id);
            holder.name     = (TextView) convertView.findViewById(R.id.name);
            holder.mobile   = (TextView) convertView.findViewById(R.id.mobile);
            holder.address  = (TextView) convertView.findViewById(R.id.address);
            holder.call_btn = (ImageView) convertView.findViewById(R.id.call_btn);
            holder.travellerPickUpStatus = (TextView) convertView.findViewById(R.id.travellerPickUpStatus);

            convertView.setTag(holder);

        }
        else
        {
            holder            = (TravellerAdapter.ViewHolder) convertView.getTag();
        }
        String id                           = arrayListTraveller.get(position).empId;
        String name                         = arrayListTraveller.get(position).empName;
        final String mobile                 = arrayListTraveller.get(position).empMobileNo;
        String address                      = arrayListTraveller.get(position).empAddress;
        String travellerPickUpStatus        = arrayListTraveller.get(position).travellerPickUpStatus;

        holder.id.setText(id);
        holder.name.setText(name);
        holder.mobile.setText(mobile);
        holder.address.setText(address);
        holder.travellerPickUpStatus.setText(travellerPickUpStatus);


        if (mobile.equals("") || mobile == null || mobile.length() == 0) {
            holder.call_btn.setVisibility(View.GONE);
        }

        holder.call_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_DIAL);

                try
                {
                    intent.setData(Uri.parse("tel:" + mobile));
                    activity.startActivity(intent);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
        return convertView;
    }

    @Override
    public int getCount() {
        return arrayListTraveller.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayListTraveller.get(position);
    }

    @Override
    public long getItemId(int position) {
        return arrayListTraveller.indexOf(getItem(position));
    }

}
