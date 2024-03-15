package com.vistara.traveler.notification;


import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.vistara.traveler.R;
import com.vistara.traveler.SplshScreen;

import org.json.JSONObject;

import java.util.Map;
import java.util.Random;

public class MyFirebaseMessagingService extends FirebaseMessagingService
{
    public static final String ANDROID_CHANNEL_ID = "com.vistara.traveler";
//    public static final String ANDROID_CHANNEL_ID = "com.example.softdew.gensetapp.ANDROID";

    private static final String TAG ="" ;
    NotificationManager notificationManager ;


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

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

                notificationTitle = object.getString("title");
                notificationBody  = object.getString("body");
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
        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.arrow);

        int notificationId = new Random().nextInt(60000);
        Intent notificationIntent = new Intent(this, SplshScreen.class);

        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent intent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        int importance = NotificationManager.IMPORTANCE_HIGH;

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);


        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this,ANDROID_CHANNEL_ID)
                .setContentIntent(intent)
                .setSmallIcon(R.drawable.arrow)  //a resource for your custom small icon
                .setContentTitle(notificationTitle) //the "title" value you sent in your notification
                .setContentText(notificationBody) //ditto
                .setAutoCancel(true)  //dismisses the notification on click
                .setSound(defaultSoundUri)
                .setLargeIcon(icon)
                .setChannelId(ANDROID_CHANNEL_ID)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(notificationBody));

//                .setPriority(NotificationManager.IMPORTANCE_HIGH)




        NotificationManager notificationManager = (NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE);
        int i = (int) System.currentTimeMillis();
        notificationManager.notify(i /* ID of notification */, notificationBuilder.build());

    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setupChannels(){
        CharSequence adminChannelName = "V-Travel";
        String adminChannelDescription = "Vistara Travel App";

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

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);

        PendingIntent service = PendingIntent.getService(getApplicationContext(),1001, new Intent(getApplicationContext(), MyFirebaseMessagingService.class), PendingIntent.FLAG_ONE_SHOT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, 1000, service);
    }
}