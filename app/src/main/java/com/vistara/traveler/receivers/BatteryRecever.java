package com.vistara.traveler.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.view.View;

import com.vistara.traveler.DriverHomePage;
import com.vistara.traveler.singlton.UserDetail;

/**
 * Created by Sharad on 03-07-2017.
 */

public class BatteryRecever extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {

        int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE,-1);
        // Display the battery scale in TextView
     //   mTextViewInfo.setText("Battery Scale : " + scale);
        int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL,-1);
        float percentage = level/ (float) scale;
        // Update the progress bar to display current battery charged percentage
        int mProgressStatus = (int)((percentage)*100);

        if(40>mProgressStatus) {
            DriverHomePage.batteryImage.setVisibility(View.VISIBLE);
            UserDetail.getInstance().setBatteryStatus("0");
        }
        else {
            DriverHomePage.batteryImage.setVisibility(View.GONE);
            UserDetail.getInstance().setBatteryStatus("1");
        }

    }

}
