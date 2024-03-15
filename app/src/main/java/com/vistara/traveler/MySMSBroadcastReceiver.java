package com.vistara.traveler;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.SmsMessage;
import android.util.Log;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Status;

/**
 * Created by Sharad on 25-02-2019.
 */


    /**
     * BroadcastReceiver to wait for SMS messages. This can be registered either
     * in the AndroidManifest or at runtime.  Should filter Intents on
     * SmsRetriever.SMS_RETRIEVED_ACTION.
     */
    public class MySMSBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent)
        {
            final Bundle bundle = intent.getExtras();

            try {

                if (bundle != null) {

                    final Object[] pdusObj = (Object[]) bundle.get("pdus");

                    for (int i = 0; i < pdusObj.length; i++) {

                        SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                        String phoneNumber = currentMessage.getDisplayOriginatingAddress();

                        String senderNum = phoneNumber;
                        String message = currentMessage.getDisplayMessageBody();

//                        message = message.substring(0, message.length()-1);
//                        Log.i("SmsReceiver", "senderNum: " + senderNum + "; message: " + message);
//
//                        Intent myIntent = new Intent("otp");
//                        myIntent.putExtra("message",message);
//                        LocalBroadcastManager.getInstance(context).sendBroadcast(myIntent);
                        // Show Alert

                    } // end for loop
                } // bundle is null

            } catch (Exception e) {
                Log.e("SmsReceiver", "Exception smsReceiver" +e);

            }

//            Bundle bundle1 = intent.getExtras();
//
//
//            if (intent.getAction() == "SMS_RECEIVED") {
//                Bundle bundle = intent.getExtras();
//                if (bundle != null) {
//                    Object[] pdus = (Object[])bundle.get("pdus");
//                    final SmsMessage[] messages = new SmsMessage[pdus.length];
//                    for (int i = 0; i < pdus.length; i++) {
//                        messages[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
//                    }
//                    if (messages.length > -1) {
//                        Log.i("hthtrh", "Message recieved: " + messages[0].getMessageBody());
//                    }
//                }
//            }
//
//            Bundle extras1 = intent.getExtras();
//            String message1 = (String) extras1.get(SmsRetriever.EXTRA_SMS_MESSAGE);


//            if (SmsRetriever.SMS_RETRIEVED_ACTION.equals(intent.getAction()))
//            {
//                Bundle extras = intent.getExtras();
//                Status status = (Status) extras.get(SmsRetriever.EXTRA_STATUS);
//
//                switch(status.getStatusCode())
//                {
//                    case CommonStatusCodes.SUCCESS:
//                        // Get SMS message contents
//                        String message = (String) extras.get(SmsRetriever.EXTRA_SMS_MESSAGE);
//                        // Extract one-time code from the message and complete verification
//                        // by sending the code back to your server.
//                        break;
//                    case CommonStatusCodes.TIMEOUT:
//                        // Waiting for SMS timed out (5 minutes)
//                        // Handle the error ...
//                        break;
//                }
//            }
        }
    }

