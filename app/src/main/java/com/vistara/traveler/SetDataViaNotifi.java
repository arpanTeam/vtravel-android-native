package com.vistara.traveler;

import android.content.Context;
import android.os.Message;

/**
 * Created by Sharad on 21-07-2017.
 */

public class SetDataViaNotifi  {

    Context context;
    String type;
    String userId;

    public SetDataViaNotifi(Context context)
    {
        this.context=context;
    }


    public void getDriverData()
    {
        if(type.equals("2"))
        {
            Message m = new Message();
            m.what = 2;
            DriverHomePage.sendMessageToActivity(2);
        }


    }

}
