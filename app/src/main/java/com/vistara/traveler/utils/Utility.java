package com.vistara.traveler.utils;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import com.google.android.gms.auth.GoogleAuthUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;

/**
 * Created by shahbaz on 27/7/15.
 */
public class Utility {

    private final static String PlayStorePackageNameOld = "com.google.market";
    private final static String PlayStorePackageNameNew = "com.android.vending";
    static final String DATEFORMAT = "yyyy-MM-dd HH:mm:ss";

    public static enum APP {
        WHATSAPP, FACEBOOK, TWITTER, GMAIL, INSTAVOICE
    }

    public final static LinkedHashMap<APP, String> PRIORITY_APPS = new LinkedHashMap<APP, String>() {{
        put(APP.WHATSAPP, "com.whatsapp");
        put(APP.FACEBOOK, "com.facebook.katana");
        put(APP.TWITTER, "com.twitter.android");
        put(APP.GMAIL, "com.google.android.gm");
        put(APP.INSTAVOICE, "com.kirusa.instavoice");
    }};





    public static String[] split(String string, boolean sort, boolean trimPerString) {
        String[] data = string.split(",");
        if (sort)
            Arrays.sort(data);
        if(trimPerString)
            for (int i=0; i<data.length; i++) if (!TextUtils.isEmpty(data[i])) data[i] = data[i].trim();
        return data;
    }

    public static String[] split(String string, boolean sort, boolean selector, String selectorLabel, boolean trimPerString) {
        String[] data = split(string, sort, trimPerString);
        if (selector) {
            List<String> temp = new ArrayList<String>(Arrays.asList(data));
            temp.add(0, selectorLabel);
            data = temp.toArray(new String[temp.size()]);
        }
        return data;
    }



    public static boolean storeBitmapToInternalStorage(String path, String fileName, Bitmap bitmapImage, int quality) {
        boolean ack = false;
        createNoMedia(path);
        try {
            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            OutputStream fOut = null;
            File file = new File(path, fileName);
            file.createNewFile();
            fOut = new FileOutputStream(file);

            // 100 means no compression, the lower you go, the stronger the compression
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, quality, fOut);
            fOut.flush();
            fOut.close();
            ack = true;
        } catch (Exception e) {
            e.printStackTrace();
            ack = false;
        } finally {
            return ack;
        }
    }

    public static String getUriPath(Context context, Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndex(projection[0]);
        cursor.moveToFirst();
        String imgDecodableString = cursor.getString(column_index);
        cursor.close();
        return imgDecodableString;
    }

    public static Bitmap getOrientedBitmap(String photoPath, Bitmap bitmap) throws IOException {
        try {
            ExifInterface ei = new ExifInterface(photoPath);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    return rotateImage(bitmap, 90);
                case ExifInterface.ORIENTATION_ROTATE_180:
                    return rotateImage(bitmap, 180);
                case ExifInterface.ORIENTATION_ROTATE_270:
                    return rotateImage(bitmap, 270);
            }
        } catch (IOException exception) {
            return null;
        }
        return null;
    }

    private static boolean createNoMedia(String path) {
        File baseDirectory = new File(path);
        File nomediaFile = new File(baseDirectory, ".nomedia");
        try {
            return nomediaFile.createNewFile();
        } catch (IOException exception) {
            //exception.printStackTrace();
        }
        return false;
    }

    public static boolean deleteFileRecursive(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                File temp = new File(dir, children[i]);
                deleteFileRecursive(temp);
            }
        }
        return dir.delete();
    }


    public static Bitmap rotateImage(Bitmap source, float angle) {
        Bitmap bitmap = null;
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        try {
            bitmap = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                    matrix, true);
        } catch (OutOfMemoryError err) {
            return null;
        }
        return bitmap;
    }

    public static String getPath(Context context, Uri uri) {
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(uri, filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        return cursor.getString(columnIndex);
    }

    public static String getFormattedAmount(double r) {
        int decimalPlaces = 2;
        BigDecimal bd = new BigDecimal(r);
        // setScale is immutable
        bd = bd.setScale(decimalPlaces, BigDecimal.ROUND_HALF_EVEN);
        r = bd.doubleValue();

        if (r % 1 == 0)
            return String.valueOf((int) Math.ceil(r));

        return String.valueOf(r);
    }

    public static String getFormattedData(double r) {
        return getFormattedAmount(r);
    }

    public static void hideKeyboard(Activity activity) {
        // Check if no view has focus:
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public static void showKeyboard(Activity activity, EditText editField) {
        InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        editField.requestFocus();
        inputManager.showSoftInput(editField, InputMethodManager.SHOW_IMPLICIT);
    }


    public static String getSettingsDateFormat(Context context) {
        String format = null;
        try {
            format = Settings.System.getString(context.getContentResolver(), Settings.System.DATE_FORMAT);
            if (TextUtils.isEmpty(format)) {
                return "yyyy-MM-dd";
            }
        } catch (Exception exception) {
            //exception.printStackTrace();
        }
        return format;
    }

    public static java.text.DateFormat getDateFormat(Context context) {
        final String format = Settings.System.getString(context.getContentResolver(),
                Settings.System.DATE_FORMAT);
        if (TextUtils.isEmpty(format)) {
            return DateFormat.getDateFormat(context);
        }
        return new SimpleDateFormat(format);
    }

    public static String getPrimaryEmail(Context context) {
        AccountManager accountManager = AccountManager.get(context);
        Account account = getAccount(accountManager);

        if (account == null) {
            return null;
        } else {
            return account.name;
        }
    }


    private static Account getAccount(AccountManager accountManager) {
        Account[] accounts = accountManager.getAccountsByType(GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE);
        Account account;
        if (accounts.length > 0) {
            account = accounts[0];
        } else {
            account = null;
        } return account;
    }

    public static int dpToPx(Context context, int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    public static int pxToDp(Context context, float px) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round(px / density);
    }

    public static int pxToDp(Context context, int px)
    {
        return pxToDp(context, (float) px);
    }

    public static int[] splitToComponentTimes(int totalSecs)
    {


        int hours = totalSecs / 3600;
        int minutes = (totalSecs % 3600) / 60;
        int seconds = totalSecs % 60;

        int[] ints = {hours , minutes , seconds};
        return ints;
    }



    private static String deviceId;
    public static String getDeviceId(Context context){
        if(deviceId == null){
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            String tmDevice, tmSerial, tmPhone, androidId;
            tmDevice = "" + tm.getDeviceId();
            tmSerial = "" + tm.getSimSerialNumber();
            androidId = "" + Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

            UUID deviceUuid = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());
            deviceId = deviceUuid.toString();
        }
        return deviceId;
    }

    /**
     *  Represents that to get scaled bitmap if image size is larger
     *  @param file
     *  @return The decoded bitmap, or null if the image could not be decoded.
     */
    public static Bitmap decodeFile(File file) {
        try {
            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(file), null, o);

            // The new size we want to scale to
            final int REQUIRED_SIZE = 70;

            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while(o.outWidth / scale / 2 >= REQUIRED_SIZE &&
                    o.outHeight / scale / 2 >= REQUIRED_SIZE) {
                scale *= 2;
            }

            // Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(file), null, o2);
        } catch (FileNotFoundException e1) {}
        catch (Exception e2) {}
        return null;
    }

    public static String currentTime(){

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String formattedDate = df.format(c.getTime());

        return formattedDate;
    }

}
