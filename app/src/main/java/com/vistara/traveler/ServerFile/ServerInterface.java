package com.vistara.traveler.ServerFile;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Base64;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.vistara.traveler.AppController;
import com.vistara.traveler.R;
import com.vistara.traveler.internal.StringXORer;
import com.vistara.traveler.utils.ApiResponseParser;
import com.vistara.traveler.utils.Utility;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import javax.security.cert.CertificateException;

/**
 * Created by Sharad on 22-06-2017.
 */

public final class ServerInterface {

    private Context mContext;
    static ServerInterface instance;// = new ServerInterface();
    public static boolean latLonBoolean = true;
    public static boolean travelerLatLon = true;
    String res = "";
    int MY_SOCKET_TIMEOUT_MS = 30000;

    private ServerInterface() {
    }

    public static ServerInterface getInstance(Context context) {
        if (instance == null) {
            instance = new ServerInterface();
        }
        instance.mContext = context;
        return instance;
    }


    public String makeServiceCall(String userId, String deviceId,  String url, final String params, ApiResponseParser obj, boolean showProgressBar, String waitMessage) {
        Log.e("Hitting Url is", url);
        return postParameterRequestWithOuth(userId, deviceId, url, params, obj, showProgressBar, waitMessage);
    }


    // post parameter request for otp validation
    public String postParameterRequest(String url, final String params1, ApiResponseParser obj, boolean showProgressBar, String waitMessage) {
        Log.e("Request is ", params1.toString());
        Log.e("Hitting Url is", url);
        final ApiResponseParser obj1 = obj;
        final ProgressDialog pDialog = new ProgressDialog(mContext);
        if (showProgressBar) {
            pDialog.setMessage(waitMessage);
            /*mContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                }
            });*/
            pDialog.show();
        }

        HurlStack hurlStack = new HurlStack() {
            @Override
            protected HttpURLConnection createConnection(URL url) throws IOException {
                HttpURLConnection httpsURLConnection = (HttpURLConnection) super.createConnection(url);

                try {


                  // httpURLConnection.setSSLSocketFactory(getSSLSocketFactory());


                   // httpURLConnection.setHostnameVerifier(getHostnameVerifier());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return httpsURLConnection;
            }
        };

        StringRequest strreq = new StringRequest(Request.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String Response) {
                        // get response
                        try {
                            if (pDialog.isShowing())
                                pDialog.dismiss();
                        } catch (Exception e) {

                        }

                        res = Response;
                        obj1.parseResponse(res);

                        Log.e("Response is",res.toString());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError e) {
                try {
                    if (pDialog.isShowing())
                        pDialog.dismiss();

                    res = "server_error";
                    obj1.parseResponse(res);
                } catch (Exception e1) {
                }
                e.printStackTrace();

            }
        }) {
            @Override
            public Map<String, String> getParams() {

                Map<String, String> params = new HashMap<>();
                params.put("cmd", params1);
                return params;
            }
        };


        strreq.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(strreq, hurlStack);


        return res;
    }


    // post parameter request for sign up
    public String signUp(String url, final String params1, ApiResponseParser obj, boolean showProgressBar) {
        final ApiResponseParser obj1 = obj;
        final ProgressDialog pDialog = new ProgressDialog(mContext);

        try {
            if (showProgressBar) {
                pDialog.setMessage("Signing up. Please Wait…");
                pDialog.show();
            }
        }catch(Exception e) {

        }

        HurlStack hurlStack = new HurlStack() {
            @Override
            protected HttpURLConnection createConnection(URL url) throws IOException {
                HttpsURLConnection httpsURLConnection = (HttpsURLConnection) super.createConnection(url);
                try {
                    httpsURLConnection.setSSLSocketFactory(getSSLSocketFactory());
                    httpsURLConnection.setHostnameVerifier(getHostnameVerifier());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return httpsURLConnection;
            }
        };

        StringRequest strreq = new StringRequest(Request.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String Response) {
                        // get response
                        res = Response;
                        //sharad - moved it before parse
                        try {
                            if (pDialog.isShowing())
                                pDialog.dismiss();
                        }catch(Exception e)
                        {

                        }
                        obj1.parseResponse(res);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError e) {
                e.printStackTrace();
                try {
                    if (pDialog.isShowing())
                        pDialog.dismiss();

                    res = "server_error";
                    obj1.parseResponse(res);
                } catch (Exception e1) {
                }
            }
        }) {
            @Override
            public Map<String, String> getParams() {

                Map<String, String> params = new HashMap<>();
                params.put("cmd", params1);
                return params;
            }
        };

        strreq.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(strreq, hurlStack);
        return res;
    }



    // post parameter request with authentication
    public String postParameterRequestWithOuth(final String userId, final String deviceId, String url, final String params1, ApiResponseParser obj, boolean showProgressBar, String waitMessage) {
        final ApiResponseParser obj1 = obj;
        final ProgressDialog pDialog = new ProgressDialog(mContext);
        if (showProgressBar) {
            pDialog.setMessage(waitMessage);
            pDialog.show();
        }

        HurlStack hurlStack = new HurlStack() {
            @Override
            protected HttpURLConnection createConnection(URL url) throws IOException {
                HttpURLConnection httpsURLConnection = (HttpURLConnection) super.createConnection(url);
                try {
                   // httpsURLConnection.setSSLSocketFactory(getSSLSocketFactory());
                   // httpsURLConnection.setHostnameVerifier(getHostnameVerifier());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return httpsURLConnection;
            }
        };

        StringRequest strreq = new StringRequest(Request.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String Response) {
                        // get response
                        try {
                            if (pDialog.isShowing())
                                pDialog.dismiss();
                        } catch (Exception e) {

                        }

                        res = Response;
                        obj1.parseResponse(res);
                        Log.e("Response is",res.toString());

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError e) {
                try {
                    if (pDialog.isShowing())
                        pDialog.dismiss();

                    res = "server_error";
                    obj1.parseResponse(res);
                } catch (Exception e1) {
                }
                e.printStackTrace();

            }
        }) {
            @Override
            public Map<String, String> getParams() {

                Map<String, String> params = new HashMap<>();
                params.put("cmd", params1);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String time = Utility.currentTime();
                String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                String credentials = userId + "@@" + deviceId;
                String base64EncodedCredentials = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                StringXORer decodexor = new StringXORer();
                String value = decodexor.encode(base64EncodedCredentials, date);
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization1", value);
                headers.put("Date", time);
                return headers;
            }

        };


        strreq.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(strreq, hurlStack);


        return res;
    }



    // post parameter request for driver lat long with auth
    public String driverRequest(final String userId, final String deviceId, String url, final String params1, ApiResponseParser obj, boolean showProgressBar) {
        latLonBoolean = false;
        final ApiResponseParser obj1 = obj;

        HurlStack hurlStack = new HurlStack() {
            @Override
            protected HttpURLConnection createConnection(URL url) throws IOException {
                HttpsURLConnection httpsURLConnection = (HttpsURLConnection) super.createConnection(url);
                try {
                    httpsURLConnection.setSSLSocketFactory(getSSLSocketFactory());
                    httpsURLConnection.setHostnameVerifier(getHostnameVerifier());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return httpsURLConnection;
            }
        };


        StringRequest strreq = new StringRequest(Request.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String Response) {
                        // get response
                        latLonBoolean = true;
                        res = Response;
                        obj1.parseResponse(res);
                        //            pDialog.dismiss();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError e) {
                e.printStackTrace();
                latLonBoolean = true;

                res = "server_error";
                obj1.parseResponse(res);
                //        pDialog.dismiss();
            }
        }) {
            @Override
            public Map<String, String> getParams() {

                Map<String, String> params = new HashMap<>();
                params.put("cmd", params1);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                String time = Utility.currentTime();
                String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                String credentials = userId + "@@" + deviceId;
                String base64EncodedCredentials = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                StringXORer decodexor = new StringXORer();
                String value = decodexor.encode(base64EncodedCredentials, date);
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization1", value);
                headers.put("Date", time);
                return headers;
            }
        };

        strreq.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(strreq, hurlStack);
        return res;
    }


    // post parameter request for traveller getting lat long with auth
    public String TraveRequest(final String userId, final String deviceId, String url, final String params1, ApiResponseParser obj, boolean showProgressBar) {
        travelerLatLon = false;
        final ApiResponseParser obj1 = obj;

        HurlStack hurlStack = new HurlStack() {
            @Override
            protected HttpURLConnection createConnection(URL url) throws IOException {
                HttpsURLConnection httpsURLConnection = (HttpsURLConnection) super.createConnection(url);
                try {
                    httpsURLConnection.setSSLSocketFactory(getSSLSocketFactory());
                    httpsURLConnection.setHostnameVerifier(getHostnameVerifier());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return httpsURLConnection;
            }
        };


        StringRequest strreq = new StringRequest(Request.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String Response) {
                        // get response
                        travelerLatLon = true;
                        res = Response;
                        obj1.parseResponse(res);
                        //            pDialog.dismiss();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError e) {
                e.printStackTrace();
                travelerLatLon = true;
                //        pDialog.dismiss();
            }
        }) {
            @Override
            public Map<String, String> getParams() {

                Map<String, String> params = new HashMap<>();
                params.put("cmd", params1);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                String time = Utility.currentTime();
                String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                String credentials = userId + "@@" + deviceId;
                String base64EncodedCredentials = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                StringXORer decodexor = new StringXORer();
                String value = decodexor.encode(base64EncodedCredentials, date);
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization1", value);
                headers.put("Date", time);
                return headers;
            }
        };

        strreq.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(strreq, hurlStack);
        return res;
    }



    private SSLSocketFactory getSSLSocketFactory()
            throws CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException, KeyManagementException {
        TrustManager[] wrappedTrustManagers = null;
        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            InputStream caInput = mContext.getResources().openRawResource(R.raw.crt);

            Certificate ca = cf.generateCertificate(caInput);
            caInput.close();

            KeyStore keyStore = KeyStore.getInstance("BKS");
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);

            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keyStore);

            wrappedTrustManagers = getWrappedTrustManagers(tmf.getTrustManagers());
        }catch (Exception e)
        {
            e.printStackTrace();
        }

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, wrappedTrustManagers, null);

        return sslContext.getSocketFactory();
    }


    private TrustManager[] getWrappedTrustManagers(TrustManager[] trustManagers) {
        final X509TrustManager originalTrustManager = (X509TrustManager) trustManagers[0];
        return new TrustManager[]{


                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return originalTrustManager.getAcceptedIssuers();
                    }

                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                        try {
                            if (certs != null && certs.length > 0){
                                certs[0].checkValidity();
                            } else {
                                originalTrustManager.checkClientTrusted(certs, authType);
                            }
                        } catch (Exception e) {
                            Log.w("checkClientTrusted", e.toString());
                        }
                    }

                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                        try {
                            if (certs != null && certs.length > 0){
                                certs[0].checkValidity();
                            } else {
                                originalTrustManager.checkServerTrusted(certs, authType);
                            }
                        } catch (Exception e) {
                            Log.w("checkServerTrusted", e.toString());
                        }
                    }
                }
        };
    }

    private HostnameVerifier getHostnameVerifier() {
        return new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                HostnameVerifier hv = HttpsURLConnection.getDefaultHostnameVerifier();

//                return hv.verify("vtravel.airvistara.com", session);
//                return hv.verify("custolife.com", session);
                return hv.verify(UrlConfig.hostVerifyName, session);
            }
        };
    }
    public  static class UnsafeSSLSocketFactory {
        public static void trustAllCertificates() {
         try {
            // Create a trust manager that trusts all certificates
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(X509Certificate[] chain, String authType)  {
                        }

                        @Override
                        public void checkServerTrusted(X509Certificate[] chain, String authType) {
                        }

                        @Override
                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[]{};
                        }
                    }
            };

            // Install the all-trusting trust manager
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

            // Set the all-trusting SSL context to the HttpsURLConnection
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
        }}}


}


