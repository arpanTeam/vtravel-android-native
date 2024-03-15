package com.vistara.traveler.receivers;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.vistara.traveler.Constant;
import com.vistara.traveler.internal.TinyDB;

public class deliverReceiver extends BroadcastReceiver {
	
	private TinyDB tDb;

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
	
	 if(tDb==null)
		 tDb = new TinyDB(context);


	try {
		context.unregisterReceiver(Constant.deliverReceiver1);
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	switch (getResultCode()) {
	case Activity.RESULT_OK:


	//	tDb.putBoolean(Constant.smsSuccess, false);

		
		break;
	case Activity.RESULT_CANCELED:

	//	tDb.putBoolean(Constant.smsSuccess, true);
		
		break;
	}
}
}
