package com.vistara.traveler;
import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.vistara.traveler.ServerFile.ServerInterface;
import com.vistara.traveler.ServerFile.UrlConfig;
import com.vistara.traveler.gcm.RegistrationIntentService;
import com.vistara.traveler.internal.StringXORer;
import com.vistara.traveler.singlton.UserDetail;
import com.vistara.traveler.utils.ApiResponseParser;
import com.vistara.traveler.utils.TelephonyManagerInfo;
import com.vistara.traveler.utils.Utility;

import org.json.JSONObject;

public class SplshScreen extends BaseActivity implements ApiResponseParser {
	
	  private final int SPLASH_TIME_OUT = 3000;
	  public Context activity;
	  public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
	  TextView versionNumber;


	@Override
	protected void initData() {

	}

	@Override
	protected int initResource() {
		return R.layout.splash_screen;
	}

	@Override
	protected void initComponent() {
		activity = SplshScreen.this;

//		String f3w = AppController.tDb.getString(Constant.gcm_id);
//		Toast.makeText(activity, AppController.tDb.getString(Constant.gcm_id), Toast.LENGTH_SHORT).show();

//		Intent intent = new Intent(this, RegistrationIntentService.class);
//		startService(intent);

//		try
//		{
//			FirebaseApp.initializeApp(this);
//			String refreshedToken = FirebaseInstanceId.getInstance().getToken();
//			Toast.makeText(activity, "", Toast.LENGTH_SHORT).show();
//
//		}
//		catch (Exception e)
//		{
//			e.printStackTrace();
//			Toast.makeText(activity, "", Toast.LENGTH_SHORT).show();
//
//		}

	}



	@Override
	protected void onResume() {
		super.onResume();
		if (android.os.Build.VERSION.SDK_INT == Build.VERSION_CODES.M || android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.M ) {
			if (checkLocationPermission()) {
				if (ContextCompat.checkSelfPermission(this,
						android.Manifest.permission.ACCESS_FINE_LOCATION)
						== PackageManager.PERMISSION_GRANTED) {
					redirectActivity();
				}
			}
		}else
		{
			redirectActivity();
		}

	}


	public boolean checkLocationPermission() {
		if (ContextCompat.checkSelfPermission(this,
				android.Manifest.permission.ACCESS_FINE_LOCATION)
				!= PackageManager.PERMISSION_GRANTED) {

			if (ActivityCompat.shouldShowRequestPermissionRationale(this,
					android.Manifest.permission.ACCESS_FINE_LOCATION)) {
				new AlertDialog.Builder(this)
						.setTitle("Message")
						.setMessage("Permission necessary")
						.setPositiveButton("ok", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialogInterface, int i) {
								//Prompt the user once explanation has been shown
								ActivityCompat.requestPermissions(SplshScreen.this,
										new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,android.Manifest.permission.READ_PHONE_STATE,
												Manifest.permission.SEND_SMS,Manifest.permission.READ_SMS},
										MY_PERMISSIONS_REQUEST_LOCATION);
							}
						})
						.create()
						.show();


			} else {
				// No explanation needed, we can request the permission.
				ActivityCompat.requestPermissions(this,
						new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,android.Manifest.permission.READ_PHONE_STATE,
								Manifest.permission.SEND_SMS,Manifest.permission.READ_SMS},
						MY_PERMISSIONS_REQUEST_LOCATION);
			}
			return false;
		} else {
			return true;
		}
	}


	@Override
	public void onRequestPermissionsResult(int requestCode,
										   String permissions[], int[] grantResults) {
		switch (requestCode) {
			case MY_PERMISSIONS_REQUEST_LOCATION: {
				// If request is cancelled, the result arrays are empty.
				if (grantResults.length > 0
						&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {

					if (ContextCompat.checkSelfPermission(this,
							android.Manifest.permission.ACCESS_FINE_LOCATION)
							== PackageManager.PERMISSION_GRANTED) {
						redirectActivity();
					}

				} else {

					finish();

				}
				return;
			}

		}
	}

	public void getDriverData(String userId, String type)
	{
		double lat = 0.0;
		double lon = 0.0;
		lat = UserDetail.getInstance().getLat();
		lon = UserDetail.getInstance().getLon();
		if(lat==0.0 || lon==0.0)
		{
			lat = AppController.tDb.getDouble(Constant.lat,0);
			lon =  AppController.tDb.getDouble(Constant.lon,0);
		}
		String deviceid="";
		try {
			 deviceid = TelephonyManagerInfo.getIMEI(activity);
		}catch (Exception e)
		{}
		String gcm = AppController.tDb.getString(Constant.gcm_id);
		JSONObject root = new JSONObject();
		try {
			if(type.equals("2")) {
//				root.put("userid", userId);
//				root.put("deviceid", deviceid);
				root.put("lat", String.valueOf(lat));
				root.put("lon", String.valueOf(lon));
				root.put("gcm", gcm);

			}else
			{
//				root.put("userid", userId);
//				root.put("deviceid", deviceid);
				root.put("gcm", gcm);
			}
		}catch (Exception e)
		{

		}

		if(type.equals("2"))
			ServerInterface.getInstance(activity).makeServiceCall(userId,deviceid,UrlConfig.driverAppData, root.toString(),SplshScreen.this,true, "Please wait...");
		else
			ServerInterface.getInstance(activity).makeServiceCall(userId,deviceid,UrlConfig.travellerAppData, root.toString(),SplshScreen.this,true, "Please wait...");

	}


	public void redirectActivity()
	{
		new Handler().postDelayed( new Runnable() {
			@Override
			public void run() {
				Intent i=null;
				if(AppController.tDb.getString(Constant.userId).equals("")) {
					i = new Intent(SplshScreen.this, LoginActivity.class);
					startActivity(i);
					finish();
				}

				else {

					UserDetail.getInstance().setUserId(AppController.tDb.getString(Constant.userId));
					UserDetail.getInstance().setMobileNumber(AppController.tDb.getString(Constant.varify_mobile));
					UserDetail.getInstance().setUserType(AppController.tDb.getString(Constant.userType));

					if(TelephonyManagerInfo.isConnectingToInternet(activity))
						getDriverData(AppController.tDb.getString(Constant.userId), AppController.tDb.getString(Constant.userType));
					else {
						UserDetail.getInstance().setUserData(AppController.tDb.getString(Constant.userData));
						if(UserDetail.getInstance().getUserType().equals("2"))
							i = new Intent(activity, DriverHomePage.class);
						else
							i = new Intent(activity, TravelerHomePage.class);

						startActivity(i);
						SplshScreen.this.finish();


					}

				}

			}
		}, SPLASH_TIME_OUT);


	}


	public void parseResponse(String response)
	{
		if(response == null || response.equals("") || response.equals("server_error"))
		{
			final AlertDialog.Builder builder =  new AlertDialog.Builder(activity);
			final String message = "Sorry for inconvenience. Please try again later";

			builder.setMessage(message)
					.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface d, int id) {
								finish();
								}
							});
			builder.create().show();
			return;
		}
		else if(response.equals("Not Allowed"))
		{
			final android.support.v7.app.AlertDialog.Builder builder =  new android.support.v7.app.AlertDialog.Builder(activity);
			builder.setCancelable(false);
			final String message = "You are not allowed. Please contact admin.";
			builder.setMessage(message)
					.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface d, int id) {
									finish();
								}
							});

			builder.create().show();
			return;
		}

		UserDetail.getInstance().setUserData(response);
		AppController.tDb.putString(Constant.userData,response);
		try{
			JSONObject jsonValue = new JSONObject(response);
			String branchTraveldesk = jsonValue.getString("branch_sos_traveldesk");
			AppController.tDb.putString(Constant.callDeskNum, branchTraveldesk);
		}catch (Exception e){
			e.printStackTrace();
		}

		Intent intent;
		if(UserDetail.getInstance().getUserType().equals("2"))
			intent = new Intent(this, DriverHomePage.class);

		else

			intent = new Intent(this, TravelerHomePage.class);

		startActivity(intent);
		SplshScreen.this.finish();
	}

}
