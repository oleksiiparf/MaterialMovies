package com.roodie.materialmovies.util;

import android.content.Context;
import android.preference.PreferenceManager;


/**
 * Created by Roodie on 11.07.2015.
 */
public class MMoviesPreferences{

    public static final String KEY_ONLYWIFI = "com.roodie.materialmovies.autoupdatewlanonly";

    public static boolean isLargeDataOverWifiOnly(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(KEY_ONLYWIFI, false);
    }

    public static void seLargeDataOverWifiOnly(Context context, boolean allow) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(KEY_ONLYWIFI, allow).apply();
    }
}
