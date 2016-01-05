package com.miracolab.junkcalls.utils;

import android.content.Context;
import android.content.SharedPreferences;

import rx.Observable;

/**
 * Created by vincent on 2016/1/4.
 */
public class SharedPreferencesUtil {

    public static <T> void set(Context context, String key, T obj) {
        SharedPreferences pref = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        pref.edit().putString(key, String.valueOf(obj)).apply();
    }

    public static String get(Context context, String key) {
        SharedPreferences pref = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        return pref.getString(key, "");
    }

    public static Boolean getBoolean(Context context, String key, Boolean def) {
        try {
            return Boolean.valueOf(get(context, key));
        } catch(Exception e) {
        }
        return def;
    }
}
