package com.vistara.traveler;

import android.support.multidex.MultiDexApplication;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.LocationRequest;
import com.vistara.traveler.authentication.SessionManeger;
import com.vistara.traveler.internal.TinyDB;
import com.vistara.traveler.utils.LruBitmapCache;

/**
 * Created by Sharad on 22-06-2017.
 */

public class AppController extends MultiDexApplication {
    public static final String TAG = AppController.class
            .getSimpleName();

    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;

    private static AppController mInstance;
    public static TinyDB tDb;
    public static MainApp mp;
    public static SessionManeger sessionManeger;
    public static LocationRequest mLocationRequest;
   // private static final long INTERVAL = 1000 * 120;
    private static final long INTERVAL = 1000 * 20;
    private static final long FASTEST_INTERVAL = 1000 * 10;


    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        tDb =  new TinyDB(this);
        mp = new MainApp(this);
        sessionManeger = new SessionManeger(this);

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    }

  /*  protected void attachBaseContext(Context base) {
        super.attachBaseContext(context);
        AppController.install(this);
    }*/

    public static synchronized AppController getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue(HurlStack hurlStack) {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext(), hurlStack);
        }

        return mRequestQueue;
    }

//    public <T> void addToRequestQueue(Request<T> req, String tag) {
//        // set the default tag if tag is empty
//        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
//        getRequestQueue().add(req);
//    }

    public <T> void addToRequestQueue(Request<T> req, HurlStack hurlStack) {
        req.setTag(TAG);
        getRequestQueue(hurlStack).add(req);
    }


}
