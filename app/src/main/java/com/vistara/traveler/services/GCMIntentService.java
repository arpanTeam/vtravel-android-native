/*
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.vistara.traveler.services;


import android.app.ActivityManager;
import android.app.IntentService;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.GcmListenerService;
import com.vistara.traveler.AppController;
import com.vistara.traveler.Constant;
import com.vistara.traveler.DriverHomePage;
import com.vistara.traveler.NotificationActivity;
import com.vistara.traveler.PastTripsActivity;
import com.vistara.traveler.R;
import com.vistara.traveler.SplshScreen;
import com.vistara.traveler.TravelerHomePage;
import com.vistara.traveler.database.DatabaseAdapter;
import com.vistara.traveler.internal.TinyDB;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.List;

import static android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
import static android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE;


/**
 * {@link IntentService} responsible for handling GCM messages.
 */
public class GCMIntentService extends GcmListenerService {
	
	private static final String TAG = "GCMIntentService";
	GCMIntentService mContext ;
	TinyDB tinyDb;
	DatabaseAdapter db;
	
	public GCMIntentService() {

	}

	/**
	 * Issues a notification to inform the user that server has sent a message.
	 */

	@Override
	public void onMessageReceived(String from, Bundle bundle) {
		// TODO Auto-generated method stub
		super.onMessageReceived(from, bundle);
		mContext = GCMIntentService.this;
		db = new DatabaseAdapter(mContext);
		Log.d("GCM", "RECIEVED A MESSAGE");
		tinyDb = new TinyDB(mContext);

//		boolean fbf = isAppInForeground(mContext);
//		Toast.makeText(mContext, String.valueOf(fbf), Toast.LENGTH_SHORT).show();
		if(bundle != null) {

			String response = bundle.getString("taskType");


			if (response != null)
			{
				String tasktypeSplit[] = response.split("=", 2);
				String tasktype = tasktypeSplit[1];
				if (tasktypeSplit[0].equals("broadcast"))
				{
					String id = "";
					String message = "";
					String title = "";
					String date = "";
					String taskData[] = tasktype.split("@#@");
					id = taskData[0];
					title = taskData[1];
					message = taskData[2];
					date = taskData[3];
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    {
                    	// 3 For Broadcast
                        simpleBroadcastForOrio(GCMIntentService.this, title, message,"3","1");
                    }
                    else
                    {
                        simpleBroadcast(mContext, title, message,"1");
                    }
					db.inserNotifiDetail(id, title, message, date, "", "");
				}
				else if(tasktypeSplit[0].equals("waiting"))
				{
					String message = "";
					String title = "";
					String taskData[] = tasktype.split("@#@");
					title = taskData[0];
					message = taskData[1];

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    {
						// 2 For Waiting
						simpleBroadcastForOrio(GCMIntentService.this,title,message,"2","2");
                    }
                    else
                    {
                        simpleBroadcast(mContext, title, message,"2");
                    }
				}
				else if(tasktypeSplit[0].equals("newtrip"))
				{
					String message = "";
					String title = "";
					String taskData[] = tasktype.split("@#@");
					title = taskData[0];
					message = taskData[1];

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    {
						// 1 For New Trip
						simpleBroadcastForOrio(GCMIntentService.this, title,message,"1","3");
                    }
                    else
                    {
                        newTrip(mContext, title, message);
                    }
				}
				else if(tasktypeSplit[0].equals("ffms"))
				{
					String message = "";
					String title = "";
					String taskData[] = tasktype.split("@#@");
					title = taskData[0];
					message = taskData[1];

					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
					{
						// 2 For Waiting
						simpleBroadcastForOrio(GCMIntentService.this,title,message,"4","4");
					}
					else
					{
						simpleBroadcast(mContext, title, message,"3");
					}
				}
			}
		}
	}


    public static final String ANDROID_CHANNEL_ID = "com.vistara.traveller.ANDROID";
    @RequiresApi(api = Build.VERSION_CODES.O)
	public  void simpleBroadcastForOrio(Context context, String titel, String message,String GCMForWhich,String statusForActivityOpened)
	{

		Intent notificationIntent=null;
		if(buildNotification(context))
		{
			if(statusForActivityOpened.equals("1"))
			{
				notificationIntent = new Intent(context, NotificationActivity.class);
			}
			else if(statusForActivityOpened.equals("4"))
			{
				notificationIntent = new Intent(context, PastTripsActivity.class);
			}
			else
			{
				notificationIntent = new Intent(context, SplshScreen.class);
			}
		}
		else
		{
			if(statusForActivityOpened.equals("1"))
			{
				notificationIntent = new Intent(context, SplshScreen.class);
			}
			else if(statusForActivityOpened.equals("4"))
			{
				notificationIntent = new Intent(context, PastTripsActivity.class);
			}
			else
			{
				notificationIntent = new Intent(context, SplshScreen.class);
			}
		}

		if(GCMForWhich.equals("1"))
		{
			try
			{
				String type = AppController.tDb.getString(Constant.userType);
				if(type.equals("2"))
				{
					String lastTripData = tinyDb.getString(Constant.lastTripData);
					JSONObject jObj = new JSONObject(lastTripData);
					String tripStatus = jObj.getString("trip_complete");

					if(tripStatus.equals("1"))
						DriverHomePage.sendMessageToActivity(2);
				}
				else
				{
					TravelerHomePage.sendMessageToActivity(1);
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}

        String CHANNEL_ID = ANDROID_CHANNEL_ID;// The id of the channel.
        CharSequence name = "Vistara";// The user-visible name of the channel.
        int importance = NotificationManager.IMPORTANCE_HIGH;
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent intent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(mChannel);

        Bitmap bitmap= BitmapFactory.decodeResource(context.getResources(), R.drawable.icon);
        Notification notification =  new Notification.Builder(GCMIntentService.this)
                .setContentIntent(intent)
                .setContentTitle(titel)
                .setContentText(message)
                .setSmallIcon(R.drawable.notification_icon)
                .setChannelId(CHANNEL_ID)
                .setAutoCancel(true)
                .setLargeIcon(bitmap)
                .build();

         int i = (int) System.currentTimeMillis();
         notificationManager.notify(i, notification);

    }





    // simple notification for broadcast
	public void simpleBroadcast(Context context, String titel, String message,String statusForActivityOpened) {
		NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
		String title = titel;
        Intent notificationIntent=null;
		if(buildNotification(context))
		{
			if(statusForActivityOpened.equals("1"))
			{
				notificationIntent = new Intent(context, NotificationActivity.class);
			}
			else if(statusForActivityOpened.equals("3"))
			{
				notificationIntent = new Intent(context, PastTripsActivity.class);
			}
			else
			{
				notificationIntent = new Intent(context, SplshScreen.class);
			}
		}
		else
		{
			if(statusForActivityOpened.equals("1"))
			{
				notificationIntent = new Intent(context, SplshScreen.class);
			}
			else if(statusForActivityOpened.equals("3"))
			{
				notificationIntent = new Intent(context, PastTripsActivity.class);
			}
			else
			{
				notificationIntent = new Intent(context, SplshScreen.class);
			}
		}

		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent intent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

		Bitmap bitmap= BitmapFactory.decodeResource(context.getResources(), R.drawable.icon);
		NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

		Notification notification = builder.setContentIntent(intent)
				.setSmallIcon(R.drawable.notification_icon).setTicker(title).setWhen(0)
				.setAutoCancel(true).setContentTitle(title)
				.setLargeIcon(bitmap)
				.setStyle(new NotificationCompat.BigTextStyle().bigText(message))
				.setContentText(message).setWhen(Calendar.getInstance().getTimeInMillis() + 1000*60+60).build();
		notification.defaults |= Notification.DEFAULT_SOUND;
		notification.defaults |= Notification.DEFAULT_VIBRATE;
		int i = (int) System.currentTimeMillis();
		notificationManager.notify(i, notification);

	}

    // new trip broadcast
    public void newTrip(Context context, String titel, String message) {
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        String title = titel;
        Intent notificationIntent=null;
        if(buildNotification(context))
        {
            notificationIntent = new Intent(context, SplshScreen.class);

			try
			{
				String type = AppController.tDb.getString(Constant.userType);
				if(type.equals("2"))
				{
					String lastTripData = tinyDb.getString(Constant.lastTripData);
					JSONObject jObj = new JSONObject(lastTripData);
					String tripStatus = jObj.getString("trip_complete");

					if(tripStatus.equals("1"))
						DriverHomePage.sendMessageToActivity(2);

				}
				else
				{
					TravelerHomePage.sendMessageToActivity(1);
				}
			}
			catch (Exception e)
			{
              e.printStackTrace();
			}
        }
        else
		{
            notificationIntent = new Intent(context, SplshScreen.class);
        }

        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent intent = PendingIntent.getActivity(context, 0,
                notificationIntent, 0);

        Bitmap bitmap= BitmapFactory.decodeResource(context.getResources(),
                R.drawable.icon);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                context);
        Notification notification = builder.setContentIntent(intent)
                .setSmallIcon(R.drawable.notification_icon).setTicker(title).setWhen(0)
                .setAutoCancel(true).setContentTitle(title)
                .setLargeIcon(bitmap)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setContentText(message).setWhen(Calendar.getInstance().getTimeInMillis() + 1000*60+60).build();
        notification.defaults |= Notification.DEFAULT_SOUND;
        notification.defaults |= Notification.DEFAULT_VIBRATE;
        int i = (int) System.currentTimeMillis();
        notificationManager.notify(i, notification);

    }


	public boolean buildNotification(Context appContext) {

		ActivityManager activityManager = (ActivityManager) appContext.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningTaskInfo> services = activityManager
				.getRunningTasks(Integer.MAX_VALUE);
		boolean isActivityFound = false;

		if (services.get(0).topActivity.getPackageName().toString().equalsIgnoreCase(appContext.getPackageName().toString())) {
			isActivityFound = true;
		}


	return  isActivityFound;
	}


	private boolean isAppInForeground(Context context)
	{
		if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
		{
			ActivityManager am = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
			ActivityManager.RunningTaskInfo foregroundTaskInfo = am.getRunningTasks(1).get(0);
			String foregroundTaskPackageName = foregroundTaskInfo.topActivity.getPackageName();

			return foregroundTaskPackageName.toLowerCase().equals(context.getPackageName().toLowerCase());
		}
		else
		{
			ActivityManager.RunningAppProcessInfo appProcessInfo = new ActivityManager.RunningAppProcessInfo();
			ActivityManager.getMyMemoryState(appProcessInfo);
			if (appProcessInfo.importance == IMPORTANCE_FOREGROUND || appProcessInfo.importance == IMPORTANCE_VISIBLE)
			{
				return true;
			}

			KeyguardManager km = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
			// App is foreground, but screen is locked, so show notification
			return km.inKeyguardRestrictedInputMode();
		}
	}

}
