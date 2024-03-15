package com.vistara.traveler.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.BatteryManager;
import android.os.Build;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.util.List;

public class TelephonyManagerInfo {

	/**
	 *  Represents that to get sim status
	 *  @param context
	 */
	public static int getSimStatus(Context context) {
		TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		return telephonyManager.getSimState();
	}

	/**
	 *  Represents that to get sim status
	 *  @param context
	 */
	@TargetApi(Build.VERSION_CODES.LOLLIPOP_MR1)
	public static boolean isSimAvailable(Context context) {
		//return !(getSimStatus(context) == TelephonyManager.SIM_STATE_ABSENT);
		if(getSdk() >= Build.VERSION_CODES.LOLLIPOP_MR1){
			if (SubscriptionManager.from(context).getActiveSubscriptionInfoCount()>0)
				return true;
		}
		return !(getSimStatus(context) == TelephonyManager.SIM_STATE_ABSENT);
	}

	public static String getOsVersion() {
		return String.valueOf(Build.VERSION.RELEASE);
	}

	public static int getSdk() {
		return Build.VERSION.SDK_INT;
	}

	public static String getAppVersion(Context context) {
		String versionName = null;
		try {
			versionName = context.getPackageManager().getPackageInfo(getAppPackageName(context), 0).versionName;
		} catch (Exception exception) {

		} finally {
			return versionName;
		}
	}

	public static String getAppPackageName(Context context) {
		return String.valueOf(context.getApplicationInfo().packageName);
	}


	/**
	 *  Represents that that the device is telephony or not
	 *  @param context
	 */
	public static boolean isTelephonyDevice(Context context) {
		TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		return !(telephonyManager.getPhoneType() == TelephonyManager.PHONE_TYPE_NONE);  //return true if supported
	}

	/**
	 *  Represents that to get a device IMEI
	 *  @param context
	 */
	public static String getIMEI(Context context) {
		String deviceId = "";
		try {
			TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			deviceId = telephonyManager.getDeviceId().toString();
		}catch (Exception e)
		{

		}
		return deviceId;
	}

	/**
	 *  Represents that to get a sim country code
	 *  @param context
	 */
	public static String getCountryCode(Context context) {
		TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		return telephonyManager.getSimCountryIso().toString();
	}

	/**
	 *  Represents that to get a mobile number
	 *  @param context
	 */
	@TargetApi(Build.VERSION_CODES.LOLLIPOP_MR1)
	public static String getMobileNumber1(Context context) {
		if(getSdk() >= Build.VERSION_CODES.LOLLIPOP_MR1){
			SubscriptionManager subscriptionManager = SubscriptionManager.from(context);
			List<SubscriptionInfo> subscriptionInfoList = subscriptionManager.getActiveSubscriptionInfoList();
			if (subscriptionInfoList.size()>0){
				for (SubscriptionInfo subscriptionInfo: subscriptionInfoList) {
					if(!TextUtils.isEmpty(subscriptionInfo.getNumber()))
						return subscriptionInfo.getNumber();
				}
			}
		}
		TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		return telephonyManager.getLine1Number().toString();
	}

	/**
	 *  Represents that to get a sim operator code
	 *  @param context
	 */
	public static int getOperatorCode(Context context) {
		TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		//return telephonyManager.getNetworkOperator();
		return Integer.parseInt(telephonyManager.getSimOperator());
	}

	/**
	 *  Represents that to get a sim operator name
	 *  @param context
	 */
	public static String getOperatorName(Context context) {
		TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		return telephonyManager.getSimOperatorName().toString();
	}

	public static String getPhoneModel()
	{
		String model = Build.MODEL;
		return model;
	}

	public static String phomeManufacturer()
	{
		String manuct = Build.MANUFACTURER;
		return manuct;
	}

	public static boolean gpsCheck(Context context)
	{
		boolean statusOfGPS;
		LocationManager manager = (LocationManager)context. getSystemService(Context.LOCATION_SERVICE );
		statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);

		return statusOfGPS;
	}

	public static boolean isPlugged(Context context) {
		boolean isPlugged= false;
		Intent intent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
		int plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
		isPlugged = plugged == BatteryManager.BATTERY_PLUGGED_AC || plugged == BatteryManager.BATTERY_PLUGGED_USB;
		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
			isPlugged = isPlugged || plugged == BatteryManager.BATTERY_PLUGGED_WIRELESS;
		}
		return isPlugged;
	}

	public static boolean isConnectingToInternet(Context _context) {

		boolean retval = false;

		ConnectivityManager connectivity = (ConnectivityManager) _context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity != null) {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null)
				for (int i = 0; i < info.length; i++)
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						retval = true;
						break;
					}
		}
		return retval;
	}

}
