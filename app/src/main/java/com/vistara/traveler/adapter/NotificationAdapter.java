package com.vistara.traveler.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.vistara.traveler.R;
import com.vistara.traveler.model.NotifictionBean;

import java.util.ArrayList;
import java.util.Collections;


/**
 * Created by vinaymahipal on 2/16/2016.
 */
public class NotificationAdapter extends BaseAdapter {

    ArrayList<NotifictionBean> notificationList;
    Activity activity;
   // ImageLoader imageLoader;

    public NotificationAdapter(Activity activity, ArrayList<NotifictionBean> notificationList) {
        // TODO Auto-generated constructor stub
    	Collections.reverse(notificationList);
        this.notificationList = notificationList;
        this.activity = activity;

    }

    /*private view holder class*/
    private class ViewHolder {
        TextView title;
        TextView notificationDate;
        TextView message;

    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;


        LayoutInflater mInflater = (LayoutInflater)
                activity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_textview, null);
            holder = new ViewHolder();

            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.notificationDate = (TextView) convertView.findViewById(R.id.date);
            holder.message= (TextView) convertView.findViewById(R.id.meaasge);

            
            convertView.setTag(holder);

        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.title.setText(notificationList.get(position).title);
        holder.notificationDate.setText(notificationList.get(position).date);
        holder.message.setText(notificationList.get(position).message);

        return convertView;
    }

    @Override
    public int getCount() {
        if (notificationList != null) {
            return Math.min(notificationList.size(), 10);
        } else {
            return 0;
        }
//        return notificationList.size();
    }

    @Override
    public Object getItem(int position) {
        return notificationList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return notificationList.indexOf(getItem(position));
    }

}
