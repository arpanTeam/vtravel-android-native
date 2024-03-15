package com.vistara.traveler.FirebaseGcm;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.vistara.traveler.Constant;
import com.vistara.traveler.gcm.RegistrationIntentService;
import com.vistara.traveler.internal.TinyDB;

/**
 * Created by Sharad on 22-06-2017.
 */

public class FirebaseIDService extends FirebaseInstanceIdService {
    private static final String TAG = "FirebaseIDService";

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.

        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // TODO: Implement this method to send any registration to your app's servers.
        sendRegistrationToServer(refreshedToken);
    }

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token)
    {
        // Add custom implementation, as needed.
         try
         {
            TinyDB tdb=new TinyDB(FirebaseIDService.this);
            tdb.putString(Constant.gcm_id, token);
         }
         catch(Exception e)
         {
            e.printStackTrace();
         }
    }
}
