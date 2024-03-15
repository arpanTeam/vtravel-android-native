package com.vistara.traveler.receivers;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;

import com.vistara.traveler.Constant;
import com.vistara.traveler.internal.TinyDB;

public class sentReceiver extends BroadcastReceiver {

	private TinyDB tDb;
	
@Override
public void onReceive(Context context, Intent intent) {
	// TODO Auto-generated method stub

	if(tDb==null)
		tDb = new TinyDB(context);

	try {
		context.unregisterReceiver(Constant.sentReceiver1);
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}

	switch (getResultCode()) {
	case Activity.RESULT_OK:

//		tDb.putBoolean(Constant.smsSuccess, false);
		break;
	case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
//		tDb.putBoolean(Constant.smsSuccess, true);

		
		break;
	case SmsManager.RESULT_ERROR_NO_SERVICE:
//		tDb.putBoolean(Constant.smsSuccess, true);

		
		break;
	case SmsManager.RESULT_ERROR_NULL_PDU:
//		tDb.putBoolean(Constant.smsSuccess, true);

		break;
	case SmsManager.RESULT_ERROR_RADIO_OFF:
//		tDb.putBoolean(Constant.smsSuccess, true);


		break;
	}
	
}
}