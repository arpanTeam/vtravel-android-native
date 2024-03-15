package com.vistara.traveler.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.vistara.traveler.FeedBackActivity;
import com.vistara.traveler.R;
import com.vistara.traveler.ServerFile.ServerInterface;
import com.vistara.traveler.ServerFile.UrlConfig;
import com.vistara.traveler.TravelerHomePage;
import com.vistara.traveler.singlton.UserDetail;

import org.json.JSONObject;

public class CustomPopup {


	Activity mActivity;
	//TinyDB tDb;
	public CustomPopup(Activity activity) {
		this.mActivity = activity;
	//	this.tDb = new TinyDB(this.mActivity);
	}
	
	public void commonPopup(String msg)
	{
		final Dialog dialog1 = new Dialog(mActivity);
		dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog1.setContentView(R.layout.common_dialog);
		dialog1.setCanceledOnTouchOutside(true);
		RelativeLayout rl = (RelativeLayout)dialog1. findViewById(R.id.ok);
		TextView tv2 = (TextView)dialog1. findViewById(R.id.tv2);
		tv2.setText(msg);
		TextView textView2 = (TextView)dialog1. findViewById(R.id.textView2);
		textView2.setText("OK");
		
		rl.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog1.dismiss();

			}
		});
		
		RelativeLayout cancel = (RelativeLayout)dialog1. findViewById(R.id.cancel);
		cancel.setVisibility(View.GONE);
		dialog1.show();
	}


    public void feebackPopup(final String msg, final ApiResponseParser activity)
    {
        final Dialog dialog1 = new Dialog(mActivity);
        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog1.setContentView(R.layout.feedback_dialog);
        dialog1.setCanceledOnTouchOutside(true);
        RelativeLayout rl = (RelativeLayout)dialog1. findViewById(R.id.ok);
        final EditText feedbackPopup = (EditText) dialog1. findViewById(R.id.feedbackPopup);


        rl.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if(feedbackPopup.getText().toString().equals("")) {
                    Toast.makeText(mActivity, "Please write down feedback", Toast.LENGTH_LONG).show();
                    return;
                }

                try {
                    JSONObject jObj = new JSONObject(msg);
                    jObj.put("feedback", feedbackPopup.getText().toString());

                    if(TelephonyManagerInfo.isConnectingToInternet(mActivity)) {
                        String deviceId="";
                        try {
                            deviceId = TelephonyManagerInfo.getIMEI(mActivity);
                        }catch (Exception e)
                        {}

                        String userId = UserDetail.getInstance().getUserId();

                        ServerInterface.getInstance(mActivity).makeServiceCall(userId, deviceId, UrlConfig.appFeedback,
                                jObj.toString(), activity, true, "Please wait...");
                    }else
                    {
                        CustomPopup cp = new CustomPopup(mActivity);
                        cp.commonPopup(mActivity.getResources().getString(R.string.internet_connection));
                    }
                }catch (Exception e)
                {

                }

                dialog1.dismiss();

            }
        });

        dialog1.show();
    }


	public void twoButtonDynamicPopup(String msg, final String page, boolean button1, boolean button2, String button1text, String button2Text)
	{
		final Dialog dialog1 = new Dialog(mActivity);
		dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog1.setContentView(R.layout.common_dialog);
		dialog1.setCanceledOnTouchOutside(true);
		TextView mainMessage = (TextView)dialog1. findViewById(R.id.tv2);
		mainMessage.setText(msg);

		RelativeLayout rl = (RelativeLayout)dialog1. findViewById(R.id.ok);
		TextView firstButtonText = (TextView)dialog1. findViewById(R.id.textView2);
        firstButtonText.setText(button1text);

		RelativeLayout cancel = (RelativeLayout)dialog1. findViewById(R.id.cancel);
		TextView secondButtonText = (TextView)dialog1. findViewById(R.id.textView3);
        secondButtonText.setText(button2Text);

		if(button1)
		{
			rl.setVisibility(View.VISIBLE);
		}else
		{
			rl.setVisibility(View.GONE);
		}

		if(button2)
		{
			cancel.setVisibility(View.VISIBLE);
		}else
		{
			cancel.setVisibility(View.GONE);

		}

		rl.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog1.dismiss();
			}
		});

		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog1.dismiss();
				if(page.equals("feedback_pop"))
				{
					mActivity.startActivity(new Intent(mActivity, FeedBackActivity.class));
				}

			}
		});


		dialog1.show();
	}



//	added by vijay
	public void safeReachedPopup(String msg, String buttonTitle)
	{
		final Dialog dialog1 = new Dialog(mActivity);
		dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog1.setContentView(R.layout.safe_reached_dialog);
		dialog1.setCanceledOnTouchOutside(true);
		TextView mainMessage 	 = (TextView)dialog1. findViewById(R.id.safeReachedText);
		RelativeLayout okButton  = (RelativeLayout)dialog1. findViewById(R.id.okButton);
		TextView okText 		 = (TextView)dialog1. findViewById(R.id.okText);

		mainMessage.setText(msg);
		okText.setText(buttonTitle);

		okButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog1.dismiss();
			}
		});

		dialog1.show();
	}

}
