package com.roodie.materialmovies.util;

import android.content.Context;
import android.preference.PreferenceManager;


/**
 * Created by Roodie on 11.07.2015.
 */
public class MMoviesPreferences {


    public static final String KEY_ONLYWIFI = "com.roodie.materialmovies.autoupdatewlanonly";
    public static final String KEY_THEME = "com.roodie.materialmovies.theme";
    public static final String KEY_FIRST_VISIT = "com.roodie.materialmovies.visit";


    public static boolean isLargeDataOverWifiOnly(Context mContext) {
        return PreferenceManager.getDefaultSharedPreferences(mContext).getBoolean(KEY_ONLYWIFI, false);
    }

    public static void seLargeDataOverWifiOnly(Context mContext, boolean allow) {
        PreferenceManager.getDefaultSharedPreferences(mContext).edit().putBoolean(KEY_ONLYWIFI, allow).apply();
    }

    public static int getApplicationTheme(Context mContext) {
        return  Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(mContext).getString(KEY_THEME, "0"));
    }

    public static void setApplicationTheme(Context mContext, int themeNumber) {
        PreferenceManager.getDefaultSharedPreferences(mContext).edit().putString(KEY_THEME, String.valueOf(themeNumber)).apply();
    }

    public static boolean getFirstVisitPerformed(Context mContext) {
        return PreferenceManager.getDefaultSharedPreferences(mContext).getBoolean(KEY_FIRST_VISIT, false);
    }

    public static void setFirstVisitPerformed(Context mContext, boolean performed) {
        PreferenceManager.getDefaultSharedPreferences(mContext).edit().putBoolean(KEY_FIRST_VISIT, performed).apply();
    }

    public static boolean isSetFirstVisitPerformed(Context mContext) {
        return PreferenceManager.getDefaultSharedPreferences(mContext).contains(KEY_FIRST_VISIT);
    }


}
