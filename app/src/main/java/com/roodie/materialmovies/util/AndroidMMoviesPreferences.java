package com.roodie.materialmovies.util;

import android.content.SharedPreferences;

import com.google.common.base.Preconditions;
import com.roodie.model.util.MMoviesPreferences;


/**
 * Created by Roodie on 11.07.2015.
 */
public class AndroidMMoviesPreferences  implements MMoviesPreferences {

    public static final String KEY_ONLYWIFI = "com.roodie.materialmovies.autoupdatewlanonly";

    private final SharedPreferences mPrefs;

    public AndroidMMoviesPreferences(SharedPreferences mPrefs) {
        this.mPrefs = Preconditions.checkNotNull(mPrefs, "Preferences cannot be null");
    }

    @Override
    public boolean isLargeDataOverWifiOnly() {
        return mPrefs.getBoolean(KEY_ONLYWIFI, false);
    }

    @Override
    public void seLargeDataOverWifiOnly(boolean allow) {
        mPrefs.edit().putBoolean(KEY_ONLYWIFI, allow).apply();
    }
}
