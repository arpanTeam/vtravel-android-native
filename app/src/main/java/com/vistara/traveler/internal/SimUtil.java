package com.vistara.traveler.internal;

import android.app.PendingIntent;
import android.content.Context;
import android.telephony.SmsManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.util.Log;

import java.util.List;


/**
 * Created by Sharad on 18-01-2017.
 */

public class SimUtil {

    public static boolean sendSMS(Context ctx, int simID, String toNum, String centerNum, String smsText, PendingIntent sentIntent, PendingIntent deliveryIntent)
    {
        boolean ret = false;
        try {


            SubscriptionManager subscriptionManager = SubscriptionManager.from(ctx);
            List<SubscriptionInfo> subscriptionInfoList = subscriptionManager.getActiveSubscriptionInfoList();
            int i=0;
            int sim1_subscriptionId = -1;
            int sim2_subscriptionId = -1;
            for (SubscriptionInfo subscriptionInfo : subscriptionInfoList) {

                //int subscriptionId = subscriptionInfo.getSubscriptionId();
                if (i == 0) {
                    sim1_subscriptionId = subscriptionInfo.getSubscriptionId();
                } else {
                    sim2_subscriptionId = subscriptionInfo.getSubscriptionId();
                }
                i++;
            }

            TinyDB tdb = new TinyDB(ctx);
            boolean sim1Status = tdb.getBoolean("sim1", false);
            if( sim1Status == false && sim1_subscriptionId != -1)
            {
                SmsManager.getSmsManagerForSubscriptionId( sim1_subscriptionId).sendTextMessage(toNum, null, smsText,sentIntent, deliveryIntent);
                tdb.putBoolean("sim1", true);

            }else {
                if( sim2_subscriptionId != -1) {
                    boolean sim2Status = tdb.getBoolean("sim2", false);
                    if( sim2Status == false) {
                        SmsManager.getSmsManagerForSubscriptionId( sim2_subscriptionId).sendTextMessage(toNum, null, smsText,sentIntent, deliveryIntent);
                        tdb.putBoolean("sim2", true);
                    }else {
                        tdb.putBoolean("sim1", false);
                        tdb.putBoolean("sim2", false);
                    }
                }else {
                    //only 1 sim is present - retry sending
                    SmsManager.getSmsManagerForSubscriptionId( sim1_subscriptionId).sendTextMessage(toNum, null, smsText,sentIntent, deliveryIntent);

                }
            }

            ret=true;

        } catch (Exception e) {
            Log.e("apipas", "Exception:" + e.getMessage());
        }
        return ret;
    }



}
