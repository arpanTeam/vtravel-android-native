package com.vistara.traveler;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Status;

public class SmsReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent)
    {
        if (SmsRetriever.SMS_RETRIEVED_ACTION.equals(intent.getAction()))
        {
            Bundle extras = intent.getExtras();
            Status status = (Status) extras.get(SmsRetriever.EXTRA_STATUS);

            switch (status.getStatusCode())
            {
                case CommonStatusCodes.SUCCESS:
                    // Get SMS message contents
                    String otp;
                    String otpMsg = (String) extras.get(SmsRetriever.EXTRA_SMS_MESSAGE);
                    if(otpMsg!=null)
                    {
                        if(LoginActivity.otpnumber!=null && !LoginActivity.otpnumber.equals(""))
                        {
                            if(otpMsg.contains(LoginActivity.otpnumber))
                            {
                                LoginActivity.checkotp = true ;
                            }
                        }

//                        String msg1 = otpMsg.replace("YsGo5wtCYWc","");
//                        String otpMsgDemo = "<#> Your OTP is";
//                        String msg2 = msg1.replace(otpMsgDemo,"");
//                        LoginActivity.mobileNumber.setText(msg2.trim());
                    }

                    // Extract one-time code from the message and complete verification
                    break;
                case CommonStatusCodes.TIMEOUT:
                    // Waiting for SMS timed out (5 minutes)
                    // Handle the error ...
                    break;
            }
        }
    }
}