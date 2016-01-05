package com.miracolab.junkcalls.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by vincent on 2016/1/4.
 */
public class NetworkUtil {

    public static boolean isWifiConnected(Context ctx) {
        ConnectivityManager mgr = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = mgr.getActiveNetworkInfo();
        return info != null && info.isConnected() && info.getType() == ConnectivityManager.TYPE_WIFI;
    }

    public static boolean isNetworkConnected(Context ctx) {
        ConnectivityManager mgr = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = mgr.getActiveNetworkInfo();
        return info != null && info.isConnected();
    }

    public static boolean isNetworkConnected(Context ctx, boolean isWifiOnly) {
        if (isWifiOnly) {
            return isWifiConnected(ctx);
        } else {
            return isNetworkConnected(ctx);
        }
    }

    public static boolean isWifiNetwork(Context ctx) {
        ConnectivityManager mgr = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = mgr.getActiveNetworkInfo();
        return info != null && info.getType() == ConnectivityManager.TYPE_WIFI;
    }
}
