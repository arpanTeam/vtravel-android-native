package com.vistara.traveler.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.vistara.traveler.AddressListActivity;
import com.vistara.traveler.AppController;
import com.vistara.traveler.Constant;
import com.vistara.traveler.DriverHomePage;
import com.vistara.traveler.model.DTravellerStatus;

import java.util.ArrayList;

/**
 * Created by Sharad on 03-07-2017.
 */

public class ArrivingButtonReciever extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {

        String ee = intent.getStringExtra("id");
        ArrayList<DTravellerStatus> statusList = (ArrayList) AppController.tDb.getListObject(Constant.dTravellerStatus, DTravellerStatus.class);
        if(statusList.size()>0)
        {
            for(int i=0; i<statusList.size(); i++)
            {
                if(statusList.get(i).getTravellerId().equals(ee))
                {
                    if(statusList.get(i).getTravellerStatus().equals("1"))
                    {
                        try {
                            statusList.get(i).setTravellerStatus("2");
                            AppController.tDb.putListObject(Constant.dTravellerStatus, statusList);
                            DriverHomePage.arrivingButton.setVisibility(View.GONE);
                            DriverHomePage.waiting.setVisibility(View.VISIBLE);
                        }catch (Exception e)
                        {
                            e.printStackTrace();
                        }

                    }
                    break;
                }
            }

            try{

                if( AddressListActivity.adapter!=null)
                     AddressListActivity.adapter.notifyDataSetChanged();

            }catch (Exception e)
            {

            }
        }
    }

}
