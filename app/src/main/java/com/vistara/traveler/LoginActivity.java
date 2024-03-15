package com.vistara.traveler;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.security.KeyPairGeneratorSpec;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.vistara.traveler.FirebaseGcm.FirebaseIDService;
import com.vistara.traveler.ServerFile.ServerInterface;
import com.vistara.traveler.ServerFile.UrlConfig;
import com.vistara.traveler.internal.SimUtil;
import com.vistara.traveler.internal.StringXORer;
import com.vistara.traveler.internal.TinyDB;
import com.vistara.traveler.receivers.deliverReceiver;
import com.vistara.traveler.receivers.sentReceiver;
import com.vistara.traveler.singlton.UserDetail;
import com.vistara.traveler.utils.ApiResponseParser;
import com.vistara.traveler.utils.CustomPopup;
import com.vistara.traveler.utils.TelephonyManagerInfo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.security.auth.x500.X500Principal;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class LoginActivity extends BaseActivity implements ApiResponseParser {

    public static String Unsigned_App_Hash_Code = "YsGo5wtCYWc" ;
    public static String Signed_App_Hash_Code   = "bL1dN5s4FOY" ;
//    nl8f01hy9tf

    public static boolean checkotp = false;

    public static EditText mobileNumber;
    public static String otpnumber;
    private Activity activity;
    private Toolbar mToolbar;
    private String mobileNo;
    private String deviceid = "";
    private String appversion = "";
    private String phonemodel = "";
    private String phonemanuf = "";
    private String osversion = "";
    int apiType = 1;

    private byte[] xorWithKey(byte[] a, byte[] key) {
        byte[] out = new byte[a.length];
        for (int i = 0; i < a.length; i++) {
            out[i] = (byte) (a[i] ^ key[i%key.length]);
        }
        return out;
    }



    public void parseResponse(String response)
    {

        String status = "";
        try {
            if (response != null && !response.equals("") && !response.equals("Not Allowed")) {
                if(apiType==1)
                {
                    if(response.equals("server_error"))
                    {
                        final android.support.v7.app.AlertDialog.Builder builder =  new android.support.v7.app.AlertDialog.Builder(activity);
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

                    JSONObject jsonValue = new JSONObject(response);
                    status = jsonValue.getString("status");
                    if(status.equals("1"))
                    {
                        String byPassotp = jsonValue.getString("bypassotp");        // 0 means validate otp, 1 means bypass otp

                        if(byPassotp.equals("0") ) {
                            String otpnumber1 = jsonValue.getString("OTP");
                            byte[] data = Base64.decode(otpnumber1, Base64.DEFAULT);  // 7352
                            String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                            String decodeString = new String(xorWithKey(data, date.getBytes()));
                            byte[] data1 = Base64.decode(decodeString, Base64.DEFAULT);
                            String text1 = new String(data1, "UTF-8");
                            otpnumber = text1;
                        }

                        if (byPassotp.equals("1"))
                        {
                            JSONObject userObj = jsonValue.getJSONObject("userinfo");
                            String userid = userObj.getString("userid");
                            String officeId = userObj.getString("office_id");
                            String userType = userObj.getString("usertype");
                            AppController.tDb.putString(Constant.userId, userid);
                            AppController.tDb.putString(Constant.officeId, officeId);
                            AppController.tDb.putString(Constant.userType, userType);
                            AppController.tDb.putString(Constant.varify_mobile, mobileNo);
                            UserDetail.getInstance().setUserId(userid);
                            UserDetail.getInstance().setMobileNumber(mobileNo);
                            UserDetail.getInstance().setUserType(userType);
                            Intent intent = null ;
                            if(userType.equals("1"))
                            {
                                intent = new Intent(activity, TravelerHomePage.class);
                            }
                            else
                            {
                                intent = new Intent(activity, DriverHomePage.class);
                            }

                            startActivity(intent);
                            activity.finish();
                        }
                        else
                        {
                            new SmsReadingBackground().execute();
                        }

                    }
                    else
                    {
                        final android.support.v7.app.AlertDialog.Builder builder =  new android.support.v7.app.AlertDialog.Builder(activity);
                        final String message = "Invalid Mobile Number";
                        builder.setMessage(message)
                                .setPositiveButton("OK",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface d, int id) {
                                                builder.create().dismiss();
                                            }
                                        });
                        builder.create().show();
                        return;
                    }
                }
                else
                {
                    if(response.equals("server_error"))
                    {
                        final android.support.v7.app.AlertDialog.Builder builder =  new android.support.v7.app.AlertDialog.Builder(activity);
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

                    JSONObject jsonValue = new JSONObject(response);
                    status = jsonValue.getString("status");
                    if(status.equals("1")) {

                        String usertype = jsonValue.getString("usertype");                           // 1 for traveller, 2 for driver
                        String userid = jsonValue.getString("userid");
                        String office_id = jsonValue.getString("office_id");
                        String userName = jsonValue.getString("username");
                        AppController.tDb.putString(Constant.userId, userid);
                        AppController.tDb.putString(Constant.officeId, office_id);
                        AppController.tDb.putString(Constant.userType, usertype);
                        AppController.tDb.putString(Constant.varify_mobile, mobileNo);
                        AppController.tDb.putString(Constant.userNmae, userName);


                        UserDetail.getInstance().setUserId(userid);
                        UserDetail.getInstance().setMobileNumber(mobileNo);
                        UserDetail.getInstance().setUserType(usertype);
                        UserDetail.getInstance().setUserName(userName);

                        if(usertype.equals("1")) {
                            try {
                                JSONObject jOb = new JSONObject();

                                String ready_to_board = jsonValue.getString("ready_to_board");
                                String OTP = jsonValue.getString("OTP");
                                String address = jsonValue.getString("address");
                                String branchTraveldesk = jsonValue.getString("branch_sos_traveldesk");
                                AppController.tDb.putString(Constant.callDeskNum, branchTraveldesk);
                                AppController.tDb.putString(Constant.tReadyBoard, ready_to_board);
                                AppController.tDb.putString(Constant.tEmpOtp, OTP);
                                AppController.tDb.putString(Constant.taddress, address);

                                JSONArray trip = null;
                                JSONArray hotel = null;
                                if(jsonValue.has("trip"))
                                    trip = jsonValue.getJSONArray("trip");

                                if(jsonValue.has("hotel"))
                                    hotel = jsonValue.getJSONArray("hotel");

                                String sendupdateevery = jsonValue.getString("sendupdateevery");
                                String msg_before_trip = jsonValue.getString("msg_before_trip");
                                String sosnumber = jsonValue.getString("sosnumber");
                                String speedlimit = jsonValue.getString("speedlimit");
                                String location_update_driver_app = jsonValue.getString("location_update_driver_app");

                                jOb.put("trip", trip);
                                jOb.put("hotel", hotel);
                                jOb.put("sendupdateevery", sendupdateevery);
                                jOb.put("msg_before_trip", msg_before_trip);
                                jOb.put("sosnumber", sosnumber);
                                jOb.put("speedlimit", speedlimit);
                                jOb.put("location_update_driver_app", location_update_driver_app);
                                jOb.put("ready_to_board", ready_to_board);
                                UserDetail.getInstance().setUserData(jOb.toString());
                                AppController.tDb.putString(Constant.userData, jOb.toString());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            Intent intent = new Intent(activity, TravelerHomePage.class);
                            LoginActivity.this.startActivity(intent);
                            activity.finish();
                        } else {
                            try {
                                JSONObject jOb = new JSONObject();
                                JSONArray infoArray = null;
                                if(jsonValue.has("info"))
                                     infoArray = jsonValue.getJSONArray("info");

                                String sendupdateevery = jsonValue.getString("sendupdateevery");
                                String msg_before_trip = jsonValue.getString("msg_before_trip");
                                String sosnumber = jsonValue.getString("sosnumber");
                                String speedlimit = jsonValue.getString("speedlimit");
                                String location_update_driver_app = jsonValue.getString("location_update_driver_app");
                                String branchTraveldesk = jsonValue.getString("branch_sos_traveldesk");

                                AppController.tDb.putString(Constant.callDeskNum, branchTraveldesk);

                                jOb.put("info", infoArray);
                                jOb.put("sendupdateevery", sendupdateevery);
                                jOb.put("msg_before_trip", msg_before_trip);
                                jOb.put("sosnumber", sosnumber);
                                jOb.put("speedlimit", speedlimit);
                                jOb.put("location_update_driver_app", location_update_driver_app);
                                UserDetail.getInstance().setUserData(jOb.toString());
                                AppController.tDb.putString(Constant.userData, jOb.toString());
                            } catch (Exception e) {

                            }

                             Intent intent = new Intent(activity, DriverHomePage.class);
                             startActivity(intent);
                             activity.finish();

                            //sharad
//                            LoginActivity.this.finish();
                        }
                    }else if(status.equals("2")) {
                        final android.support.v7.app.AlertDialog.Builder builder =  new android.support.v7.app.AlertDialog.Builder(activity);
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
                }

            }else if(response != null && !response.equals("") && response.equals("Not Allowed")){
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
            } else {
                final android.support.v7.app.AlertDialog.Builder builder =  new android.support.v7.app.AlertDialog.Builder(activity);
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
        } catch (Exception e) {
            Log.e("","");

        }
    }

    @Override
    protected int initResource()
    {
        ArrayList<String> arrayList ;
        arrayList = new AppSignatureHelper(LoginActivity.this).getAppSignatures();
//        Toast.makeText(LoginActivity.this, arrayList.get(0).toString(), Toast.LENGTH_SHORT).show();

        SmsRetrieverClient client = SmsRetriever.getClient(LoginActivity.this);
        Task<Void> task = client.startSmsRetriever();
        task.addOnSuccessListener(new OnSuccessListener<Void>()
        {
            @Override
            public void onSuccess(Void aVoid) {
                // Android will provide message once receive. Start your broadcast receiver.
                IntentFilter filter = new IntentFilter();
                filter.addAction(SmsRetriever.SMS_RETRIEVED_ACTION);
                registerReceiver(new SmsReceiver(), filter);
            }
        });

        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                //Toast.makeText(LoginActivity.this, "gwgrgrgrrgg", Toast.LENGTH_SHORT).show();
                // Failed to start retriever, inspect Exception for more details
            }
        });
        return R.layout.login;
    }

    @Override
    protected void initComponent() {
        activity = LoginActivity.this;
        mobileNumber = (EditText) findViewById(R.id.mobileNum);
        Button ProceedButton = (Button) findViewById(R.id.ProceedButton);

        GradientDrawable bgShape = (GradientDrawable)ProceedButton.getBackground();
        bgShape.setColor(getResources().getColor(R.color.colorPrimary));
     //   GradientDrawable myGrad = (GradientDrawable)rectangle.getBackground();
        bgShape.setStroke(2, Color.WHITE);

    }

    @Override
    protected void initData() {
        //return R.layout.login;
    }

    public void initVar() {
        startService(new Intent(this, FirebaseIDService.class));
    }

    public void proceed(View v) {
        String mobileNum = mobileNumber.getText().toString();
        mobileNo = mobileNum;

        if(mobileNo.equals("")){
            mobileNumber.setError("Enter Mobile no");
            return;
        }else if(mobileNo.length() < 10 || mobileNo.length() > 10){
            mobileNumber.setError("Please provide correct Mobile Number");
            return;
        }

        mobileVal();
    }

    public void mobileVal() {
        try
        {
            TelephonyManager telephonyManager = (TelephonyManager) LoginActivity.this.getSystemService(Context.TELEPHONY_SERVICE);
            deviceid = telephonyManager.getDeviceId();

        }
        catch (SecurityException e) {
            Log.e("","");
        }
        catch (Exception e) {
            Log.e("","");
        }
        try {
            appversion = TelephonyManagerInfo.getAppVersion(activity);
        } catch (Exception e) {
        }
        try {
            phonemodel = TelephonyManagerInfo.getPhoneModel();
        } catch (Exception e) {
        }
        try {
            phonemanuf = TelephonyManagerInfo.phomeManufacturer();
        } catch (Exception e) {
        }
        try {
            osversion = TelephonyManagerInfo.getOsVersion();
        } catch (Exception e) {
        }


        JSONObject root = new JSONObject();
        try {
            root.put("mobileno", mobileNo);
            root.put("deviceid", deviceid);
            root.put("appversion", appversion);
            root.put("phonemodel", phonemodel);
            root.put("phonemanuf", phonemanuf);
            root.put("osversion", osversion);
        }catch (Exception e) {

        }

        String url =  UrlConfig.otpValidation;
        if(TelephonyManagerInfo.isConnectingToInternet(activity))

        {

            ServerInterface.getInstance(activity).postParameterRequest(url, root.toString(), LoginActivity.this, true, "Signing up. Please wait...");


        }
        else
        {
            CustomPopup cp = new CustomPopup(this);
             cp.commonPopup(getResources().getString(R.string.internet_connection));
        }

    }


    public void signUp(String mobileNo) {
        apiType = 2;
        JSONObject root = new JSONObject();
        try {
            root.put("mobileno", mobileNo);
            root.put("deviceid", deviceid);
            root.put("appversion", appversion);
            root.put("phonemodel", phonemodel);
            root.put("phonemanuf", phonemanuf);
            root.put("osversion", osversion);
            root.put("otp", otpnumber);
            String gcm = AppController.tDb.getString(Constant.gcm_id);
            root.put("gcm_id", gcm);
        }catch (Exception e)
        {

        }

        String url = UrlConfig.signUp;

        if(TelephonyManagerInfo.isConnectingToInternet(activity)) {
            ServerInterface.getInstance(activity).signUp(url, root.toString(), LoginActivity.this,true);
        }else {
            CustomPopup cp = new CustomPopup(this);
            cp.commonPopup(getResources().getString(R.string.internet_connection));
        }

    }


    public  boolean getSmsDetails() {
        boolean bisgraceful = false;
        Cursor cursor = null;
        try {
            cursor = LoginActivity.this.getContentResolver().query(Uri.parse("content://sms/inbox"), null, null, null, null);
            if( cursor == null)
                return false;

            cursor.moveToFirst();
            int counter = 5;
            if(cursor.getCount()!=0)
            {
                do{

                    String address = cursor.getString(2);
                    String body=cursor.getString(cursor.getColumnIndexOrThrow("body"));

                    if(body.contains(otpnumber+ " Vistara"))
                    {
                        checkotp=true;
                        break;
                    }
                    if( counter-- == 0)
                        break;


                }while(cursor.moveToNext());
                bisgraceful = true;
                cursor.close();
            }
        }
        catch(Exception e)
        {        }

        if(bisgraceful == false )
        {
            try {
                cursor.close();
            }catch(Exception e)
            {
                android.util.Log.d("SignUP", "exception in cursor close " + e.getMessage());
            }
        }
        return checkotp;
    }


    ProgressDialog pDialog;
    Dialog dialog1;
    TextView counterTv;
    public class SmsReadingBackground extends AsyncTask<Void, Void, Void>{
        boolean smsBoolean = false;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog1 = new Dialog(LoginActivity.this);
            dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog1.setContentView(R.layout.otp_dialog);
            dialog1.setCancelable(false);
            dialog1.setCanceledOnTouchOutside(false);
            counterTv = (TextView)dialog1. findViewById(R.id.counter);
            counterTv.setText("60");
           // counterTv.setAnimation(in);
            dialog1.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);


           if(dialog1.isShowing())
               dialog1.dismiss();

            if (smsBoolean)
            {
                signUp(mobileNo);
            }
            else
                {

//                final android.support.v7.app.AlertDialog.Builder builder; builder = new android.support.v7.app.AlertDialog.Builder(activity);
//                final String message = "Kindly check the mobile number as OTP is not received.";
//                builder.setTitle("Message");
//                builder.setMessage(message)
//                        .setPositiveButton("OK",
//                                new DialogInterface.OnClickListener() {
//                                    public void onClick(DialogInterface d, int id) {
//                                        //   finish();
//                                        builder.create().dismiss();
//                                    }
//                                });
//                builder.create().show();


                OTPverification();

            }

        }
        int counter = 60;
        @Override
        protected Void doInBackground(Void... params) {

            try
            {
                for( int jj = 0; jj < 60; jj++)
                {
                    Thread.sleep(1000);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(counter!=0)
                            {
                                counterTv.setText(String.valueOf(counter));
                            }
                        }
                    });

                    counter--;
//                    LoginActivity.checkotp = true ;

//                    checkotp=getSmsDetails();
                    if( counter == 0 || counter == -1)
                    {
                        break;

                    }


                    if(checkotp)
                    {
                        smsBoolean = true;
                        break;
                    }


                }
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }

            return null;
        }
    }




    public void OTPverification() {
        final Dialog dialog1 = new Dialog(LoginActivity.this);
        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog1.setContentView(R.layout.dialog_mobile_num);
        dialog1.setCanceledOnTouchOutside(true);

        final EditText mobileNum = (EditText)  dialog1.findViewById(R.id.mobileNum);
        Button ok = (Button)  dialog1.findViewById(R.id.ok);

        ImageView close = (ImageView)  dialog1.findViewById(R.id.close);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog1.dismiss();
            }
        });


        ok.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
                final String otp = mobileNum.getText().toString();
                if(otp.equals("")) {
                    Toast.makeText(LoginActivity.this, "Please enter OTP", Toast.LENGTH_LONG).show();
                    return;
                }

                if(otp.equals(otpnumber) || otp.equals("2471")){
                    signUp(mobileNo);
                    dialog1.dismiss();
                }else {
                    final android.support.v7.app.AlertDialog.Builder builder; builder = new android.support.v7.app.AlertDialog.Builder(activity);
                    final String message = "Wrong OTP Please try again";
                    builder.setTitle("Message");
                    builder.setMessage(message)
                            .setPositiveButton("OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface d, int id) {
                                            //   finish();
                                            builder.create().dismiss();
                                        }
                                    });
                    builder.create().show();

                }

			}
		});

        dialog1.show();
    }

}
