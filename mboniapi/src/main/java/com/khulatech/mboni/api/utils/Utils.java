package com.khulatech.mboni.api.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Handler;

import java.io.File;
import java.util.List;


/**
 * Created by Joane SETANGNI on 09/06/2016.
 */
public class Utils {


    private static Utils utilsInstance = new Utils();
    private static final Handler HANDLER = new Handler();


    private Utils() {
    }

    public static Utils getInstance() {
        return utilsInstance;
    }


    public static boolean isEmpty(String obj) {
        return obj == null || obj.isEmpty();
    }

    public static boolean notEmptyString(String s){
        return s!=null && !s.isEmpty();
    }

    public static boolean isConnected(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netinfo = cm.getActiveNetworkInfo();

        if (netinfo != null && netinfo.isConnectedOrConnecting()) {
            NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            NetworkInfo mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if((mobile != null && mobile.isConnectedOrConnecting()) || (wifi != null && wifi.isConnectedOrConnecting())) return true;
            else return false;
        } else
            return false;
    }

//    /**
//     * Metjode pour copier un text dans le presse papier
//     * @param cxt
//     * @param text
//     */
//    public static void copyToClipBoard(Context cxt, String text){
//        int sdk = android.os.Build.VERSION.SDK_INT;
//        if(sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
//            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) cxt.getSystemService(Context.CLIPBOARD_SERVICE);
//            clipboard.setText(text);
//        } else {
//            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) cxt.getSystemService(Context.CLIPBOARD_SERVICE);
//            android.content.ClipData clip = android.content.ClipData.newPlainText(cxt.getString(R.string.app_name),text);
//            clipboard.setPrimaryClip(clip);
//        }
//    }



    public static String formatSeconds(int seconds) {
        return getTwoDecimalsValue(seconds / 3600) + ":"
                + getTwoDecimalsValue(seconds / 60) + ":"
                + getTwoDecimalsValue(seconds % 60);
    }

    private static String getTwoDecimalsValue(int value) {
        if (value >= 0 && value <= 9) {
            return "0" + value;
        } else {
            return value + "";
        }
    }


    public static void wait(int millis, Runnable callback){
        HANDLER.postDelayed(callback, millis);
    }
    /**
     * Crée une arborescence de dossier si cette derniere n'existe pas
     * @param dirPath le chemin du dossier
     */
    public static void createDirIfNotExist(String dirPath) {
        File file = new File(dirPath);
        if(!file.exists()){
            file.mkdirs();
        }
    }


    public static void installMBoniApp(Context context) {
        String appId = "com.jeancre.dev.skrach";
        Intent rateIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appId));
        boolean marketFound = false;

        // find all applications able to handle our rateIntent
        final List<ResolveInfo> otherApps = context.getPackageManager().queryIntentActivities(rateIntent, 0);
        for (ResolveInfo otherApp: otherApps) {
            // look for Google Play application
            if (otherApp.activityInfo.applicationInfo.packageName.equals("com.android.vending")) {

                ActivityInfo otherAppActivity = otherApp.activityInfo;
                ComponentName componentName = new ComponentName(
                        otherAppActivity.applicationInfo.packageName,
                        otherAppActivity.name
                );
                rateIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                rateIntent.setComponent(componentName);
                context.startActivity(rateIntent);
                marketFound = true;
                break;

            }
        }

        // if GP not present on device, open web browser
        if (!marketFound) {
            Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id="+appId));
            context.startActivity(webIntent);
        }
    }

}
