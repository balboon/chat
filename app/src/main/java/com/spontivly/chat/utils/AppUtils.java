package com.spontivly.chat.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.location.Address;
import android.location.Geocoder;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.RECEIVE_BOOT_COMPLETED;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class AppUtils {


    public static boolean hasForcedSnackbar;
    public static Activity appContext;

    public static float screenWidth;
    public static float screenHeight;

    public static HashMap<String, Bitmap> pinBitmapsByType = new HashMap<>();

    public static void Init(Activity context) {

        appContext = context;

        Display display = context.getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float density = context.getResources().getDisplayMetrics().density;
        screenHeight = outMetrics.heightPixels;// / density;
        screenWidth = outMetrics.widthPixels;// / density;
    }

    public static boolean isGoogleServicesSupported() {
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int isAvailable = api.isGooglePlayServicesAvailable(appContext);
        if (isAvailable == ConnectionResult.SUCCESS) {
            return true;
        }
        Toast.makeText(appContext, "Can't connect to Google Play Services...", Toast.LENGTH_LONG).show();
        return false;
    }

    public static void snack(String value, View rootView) {
        if(appContext == null){return;}
        if (rootView == null) {
            appContext.findViewById(android.R.id.content);
        }
        Snackbar sb = Snackbar.make(rootView, value, 2500);
        AppUtils.forceSnackbarToShow(sb);
        sb.show();

    }

    public static void devToast(String s, boolean isDev) {
        if(isDev){
            toast(s);
        }
    }
    public static void toast(final String value){ toast(value, false); }
    public static void toast(final String value, final boolean doShort) {
        if(appContext == null){return;}
        appContext.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(appContext, value, doShort? Toast.LENGTH_SHORT : Toast.LENGTH_LONG).show();
            }
        });
    }

    public static void GoHome() {
        if(appContext == null){return;}
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        appContext.startActivity(startMain);
    }

    public static Intent getLaunchIntent(String appId, Context context) {
        PackageManager pm = context.getPackageManager();
        Intent main = new Intent(Intent.ACTION_MAIN, null);
        main.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> launchables = pm.queryIntentActivities(main, 0);
        Collections.sort(launchables, new ResolveInfo.DisplayNameComparator(pm));
        Intent launchIntent = null;
        for (int i = launchables.size(); i-- > 0; ) {
            ResolveInfo info = launchables.get(i);
            if (info.activityInfo.packageName.equals(appId)) {
                ComponentName name = new ComponentName(info.activityInfo.applicationInfo.packageName,
                        info.activityInfo.name);
                launchIntent = new Intent(Intent.ACTION_MAIN);
                launchIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                launchIntent.setComponent(name);
                break;
            }
        }
        return launchIntent;
    }

    public static boolean locationPermissionsGranted(Context context) {
        if (context == null) {
            return false;
        }
        return ActivityCompat.checkSelfPermission(context, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(context, ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    public static void forceSnackbarToShow(Snackbar sb) {
        if (hasForcedSnackbar) {
            return;
        }
        try {
            Field mAccessibilityManagerField = BaseTransientBottomBar.class.getDeclaredField("mAccessibilityManager");
            mAccessibilityManagerField.setAccessible(true);
            AccessibilityManager accessibilityManager = (AccessibilityManager) mAccessibilityManagerField.get(sb);
            Field mIsEnabledField = AccessibilityManager.class.getDeclaredField("mIsEnabled");
            mIsEnabledField.setAccessible(true);
            mIsEnabledField.setBoolean(accessibilityManager, false);
            mAccessibilityManagerField.set(sb, accessibilityManager);
            hasForcedSnackbar = true;
        } catch (Exception ex) {
            Log.d("Snackbar", "Reflection error: " + ex.toString());
        }
    }

    public static void alert(String title, String message) {
        alert(title, message, true, null, null, "Ok", null);
    }

    public static void alert(String title, String message, DialogInterface.OnClickListener okCallback, DialogInterface.OnClickListener cancelCallback) {
        alert(title, message, true, okCallback, cancelCallback, "Ok", "Cancel");
    }

    public static void alert(String title, String message, Boolean isCancelable, DialogInterface.OnClickListener okCallback, DialogInterface.OnClickListener cancelCallback, String okLabel, String cancelLabel) {
        if(appContext == null){return;}
        AlertDialog.Builder builder = new AlertDialog.Builder(appContext, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
                .setCancelable(isCancelable)
                .setTitle(title)
                .setMessage(message);
        builder.setPositiveButton(okLabel, okCallback);
        if(cancelCallback != null){
            builder.setNegativeButton(cancelLabel, cancelCallback);
        }
        builder.create().show();
    }

    public static Bitmap tintImage(Bitmap bitmap, int color) {
        Paint paint = new Paint();
        paint.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN));
        Bitmap bitmapResult = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmapResult);
        canvas.drawBitmap(bitmap, 0, 0, paint);
        return bitmapResult;
    }

    public static Bitmap resizeBitmap(String iconName, double scale) {
        Resources r = appContext.getResources();
        if(r == null){ return null; }

        int iconId = r.getIdentifier(iconName, "drawable", appContext.getPackageName());
        Bitmap imageBitmap = BitmapFactory.decodeResource(appContext.getResources(), iconId);
        return  resizeBitmap(imageBitmap, scale);
    }

    public static Bitmap resizeBitmap(Bitmap imageBitmap, double scale) {
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, (int) (imageBitmap.getWidth() * scale), (int) (imageBitmap.getHeight() * scale), false);
        return resizedBitmap;
    }

    public static Bitmap createPinBitmap(int typeId, double scale, boolean isMyPin, boolean isActivated) {
        String key = typeId + scale + (isMyPin ? "owner" : "") + (isActivated ? "" : "grey");
        if (!pinBitmapsByType.containsKey(key)) {
            Bitmap pinBitmap = resizeBitmap(isActivated ? (isMyPin ? "finalpin_base_mine" : "finalpin_base") : "finalpin_base_grey", scale);
            //pinBitmap = resizeBitmap("finalpin_base", scale);

            int iconResourceId = 0;//MainActivity.model.getEventType(typeId).icon;
            Bitmap iconBitmap = BitmapFactory.decodeResource(appContext.getResources(), iconResourceId);
            iconBitmap = resizeBitmap(iconBitmap, scale * .52f);
            iconBitmap = tintImage(iconBitmap, 0xFF000000);

            Bitmap drawTarget = Bitmap.createBitmap(pinBitmap.getWidth(), pinBitmap.getHeight(), pinBitmap.getConfig());
            Canvas canvas = new Canvas(drawTarget);
            canvas.drawBitmap(pinBitmap, new Matrix(), null);

            Matrix m = new Matrix();
            m.setTranslate((int)(pinBitmap.getWidth() * .5), (int)(pinBitmap.getHeight() * .08f));
            //m.setTranslate((int)(iconBitmap.getWidth() * scale * 4),(int) (iconBitmap.getHeight() * scale));
            canvas.drawBitmap(iconBitmap, m, null);
            //Cache new bitmap, never to be drawn again!
            pinBitmapsByType.put(key, drawTarget);
        }
        return pinBitmapsByType.get(key);
    }

    public static int getResourceId(String pVariableName, String pResourcename, String pPackageName) {
        try {
            return appContext.getResources().getIdentifier(pVariableName, pResourcename, pPackageName);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static double getLatLngDistance(LatLng p1, LatLng p2) {

        int Radius = 6371;//radius of earth in Km
        double lat1 = p1.latitude;
        double lat2 = p2.latitude;
        double lon1 = p1.longitude;
        double lon2 = p2.longitude;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        double km = valueResult / 1;
        DecimalFormat newFormat = new DecimalFormat("####");
        int kmInDec = Integer.valueOf(newFormat.format(km));
        double meter = valueResult % 1000;
        int meterInDec = Integer.valueOf(newFormat.format(meter));
        return Radius * c * 1000;

    }

    public static LatLng getLatLngFromAddress(String address, Context context) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocationName(address, 1);
            if (addresses != null && addresses.size() > 0) {
                Address a = addresses.get(0);
                return new LatLng(a.getLatitude(), a.getLongitude());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getAddress(LatLng pos, Context context) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(pos.latitude, pos.longitude, 1);
            if (addresses != null && addresses.size() > 0) {
                Address a = addresses.get(0);
                //Build address lines
                ArrayList<String> addressFragments = new ArrayList<>();
                for(int i = 0; i <= a.getMaxAddressLineIndex(); i++) {
                    addressFragments.add(a.getAddressLine(i));
                }
                //Full returnAddress
                String returnAddress =  TextUtils.join(System.getProperty("line.separator"), addressFragments);

                //Truncate address at city, or postalCode
                try {
                    String city = a.getLocality();
                    String postalCode = a.getPostalCode();
                    if (!AppUtils.isEmpty(city) && returnAddress.contains(city)) {
                        //Chop off everything after City
                        returnAddress = returnAddress.substring(0, returnAddress.indexOf(city) + city.length());
                    }
                    //If that doesn't work, chop off everything after postalCode
                    else if (!AppUtils.isEmpty(postalCode) && returnAddress.contains(postalCode)) {
                        returnAddress = returnAddress.substring(0, returnAddress.indexOf(postalCode));
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
                return returnAddress;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static int LOCATION_PERMISSIONS_REQUEST = 1;
    public static int PHONE_PERMISSIONS_REQUEST = 2;

    public static void requestLocationPermissions() {
        ActivityCompat.requestPermissions(appContext, new String[]{
                ACCESS_FINE_LOCATION,
                ACCESS_COARSE_LOCATION,
                RECEIVE_BOOT_COMPLETED,
                WRITE_EXTERNAL_STORAGE
        }, LOCATION_PERMISSIONS_REQUEST);
    }
/*
    public static void requestPhonePermissions() {
        ActivityCompat.requestPermissions(appContext, new String[]{
                READ_PHONE_STATE
        }, PHONE_PERMISSIONS_REQUEST);
    }
*/
    public static String urlenc(String value) {
        if (value != null) {
            try {
                return URLEncoder.encode(value, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    public static String urldec(String value) {
        if (value != null) {
            try {
                return URLDecoder.decode(value, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return "";
    }


    public static void devLog(String s, boolean isDevMode) {
        if(!isDevMode){return;}
        Log.d("Spontivly-dbg", s);
    }

    public static void popAllBackStacks(FragmentActivity activity) {
        FragmentManager fm = activity.getSupportFragmentManager();
        for(int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }
    }

    public static BroadcastReceiver addScreenOnBroadcastReceiver(Context context) {
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        BroadcastReceiver mReceiver = null;//new AppBroadcastReceiver();
        context.registerReceiver(mReceiver, filter);
        return mReceiver;
    }

    public static void triggerBackPress(FragmentActivity activity) {
        activity.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK));
        activity.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK));
    }

    public static boolean isEmpty(String value) {
        return value == null || value.isEmpty();
    }
}
