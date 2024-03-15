package com.vistara.traveler.FirebaseGcm;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
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
import com.vistara.traveler.services.GCMIntentService;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * Created by Sharad on 22-06-2017.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    public static final String ANDROID_CHANNEL_ID = "com.vistara.travel.ANDROID";

    TinyDB tinyDb;
    DatabaseAdapter db;
    String tripId = "";

    private static final String TAG = "FCM Service";
    NotificationManager notificationManager ;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage)
    {
        db                  = new DatabaseAdapter(MyFirebaseMessagingService.this);
        tinyDb              = new TinyDB(MyFirebaseMessagingService.this);
        notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        String notificationTitle = "";
        String notificationBody  = "";
        try
        {
            // Check if message contains a notification payload.
            if (remoteMessage.getNotification() != null)
            {
                notificationTitle = remoteMessage.getNotification().getTitle();
                notificationBody  = remoteMessage.getNotification().getBody();
            }
            else
            {
                Map<String, String> params = remoteMessage.getData();
                JSONObject object = new JSONObject(params);

                notificationTitle = object.getString("taskType");
                notificationBody  = object.getString("taskType");
            }

            //Setting up Notification channels for android O and above
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
            {
                setupChannels();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }


        if (notificationBody != null && !notificationBody.equals(""))
        {
            String tasktypeSplit[]  = notificationBody.split("=", 2);
            String tasktype         = tasktypeSplit[1];
            if (tasktypeSplit[0].equals("broadcast"))
            {
                String id           = "";
                String message      = "";
                String title        = "";
                String date         = "";
                String taskData[]   = tasktype.split("@#@");
                try
                {
//                    id = taskData[0];
//                    title = taskData[1];
//                    message = taskData[2];
//                    date = taskData[3];

                    title = taskData[0];
                    message = taskData[1];
                    date = taskData[2];

                }
                catch (ArrayIndexOutOfBoundsException e)
                {
                    e.printStackTrace();
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                {
                    // 3 For Broadcast
                    simpleBroadcastForOrio(MyFirebaseMessagingService.this, title, message,"3","1");
                }
                else
                {
                    simpleBroadcast(MyFirebaseMessagingService.this, title, message,"1");
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
                    simpleBroadcastForOrio(MyFirebaseMessagingService.this,title,message,"1","2");
                }
                else
                {
//                    simpleBroadcast(MyFirebaseMessagingService.this, title, message,"2");
                    tripCompleted(MyFirebaseMessagingService.this, title, message);
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
                    simpleBroadcastForOrio(MyFirebaseMessagingService.this, title,message,"1","3");
                }
                else
                {
                    newTrip(MyFirebaseMessagingService.this, title, message);
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
                    simpleBroadcastForOrio(MyFirebaseMessagingService.this,title,message,"4","4");
                }
                else
                {
                    simpleBroadcast(MyFirebaseMessagingService.this, title, message,"3");
                }
            }
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setupChannels()
    {
        CharSequence adminChannelName = "Yellow Rentals";
        String adminChannelDescription = "Yellow Rentals Supervisor App";

        NotificationChannel adminChannel;
        adminChannel = new NotificationChannel(ANDROID_CHANNEL_ID, adminChannelName, NotificationManager.IMPORTANCE_HIGH);
        adminChannel.setDescription(adminChannelDescription);
        adminChannel.enableLights(true);
        adminChannel.setLightColor(Color.RED);
        adminChannel.enableVibration(true);
        if (notificationManager != null)
        {
            notificationManager.createNotificationChannel(adminChannel);
        }
    }


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
                    if(statusForActivityOpened.equals("2"))
                    {
                        String[] tripCompleted = message.split(" ");
                        if(tripCompleted[0].equals("Trip"))
                        {
                            TravelerHomePage.sendMessageToActivity(3);
                        }
                    }
                    else
                    {
                        TravelerHomePage.sendMessageToActivity(1);
                    }
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
        Notification notification =  new Notification.Builder(MyFirebaseMessagingService.this)
                .setContentIntent(intent)
                .setContentTitle(titel)
                .setContentText(message)
                .setSmallIcon(R.drawable.notification_icon)
                .setChannelId(CHANNEL_ID)
                .setAutoCancel(true)
                .setLargeIcon(bitmap)
                .setStyle(new Notification.BigTextStyle().bigText(message))
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
//                    String lastTripData = tinyDb.getString(Constant.lastTripData);
//                    JSONObject jObj = new JSONObject(lastTripData);
//                    String tripStatus = jObj.getString("trip_complete");

//                    if(tripStatus.equals("1"))
                        DriverHomePage.sendMessageToActivity(2);

                }
                else
                {
                    TravelerHomePage.isFirstEntry++;
                    TravelerHomePage.updateTime = 60;
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




    // trip completed broadcast
    public void tripCompleted(Context context, String titel, String message) {
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
                if(type.equals("1"))
                {
                    String[] tripCompleted = message.split(" ");
                    if(tripCompleted[0].equals("Trip"))
                    {
                        TravelerHomePage.sendMessageToActivity(3);
                    }

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


}