package com.roodie.materialmovies.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.widget.Toast;

import com.roodie.materialmovies.R;
import com.roodie.materialmovies.views.activities.SettingsActivity;

/**
 * Created by Roodie on 22.07.2015.
 */
public class Utils {

    /**
     * Returns true if there is an active connection which is approved by the user for large data
     * downloads (e.g. images).
     *
     * @param showOfflineToast If true, displays a toast asking the user to connect to a network.
     */
    public static boolean isAllowedLargeDataConnection(Context context, boolean showOfflineToast) {
        boolean isConnected;
        boolean largeDataOverWifiOnly = MMoviesPreferences.isLargeDataOverWifiOnly(context);

        // check connection state
        if (largeDataOverWifiOnly) {
            isConnected = MMoviesAndroidUtils.isWifiConnected(context);
        } else {
            isConnected = MMoviesAndroidUtils.isNetworkConnected(context);
        }

        // display optional offline toast
        if (showOfflineToast && !isConnected) {
            Toast.makeText(context,
                    largeDataOverWifiOnly ? R.string.offline_no_wifi : R.string.offline,
                    Toast.LENGTH_LONG).show();
        }
        return isConnected;
    }

    /**
     * Sets the global app theme variable. Applied by all activities once they are created.
     */
    public static synchronized void updateTheme(Context context, String themeIndex) {
        int theme = Integer.valueOf(themeIndex);
        switch (theme) {
            case 1: {
                SettingsActivity.THEME = R.style.Theme_MMovies_Dark;
                MMoviesPreferences.setApplicationTheme(context, 1);
            }
                break;
         /*   case 2: {
                SettingsActivity.THEME = R.style.Theme_MMovies_Green;
                MMoviesPreferences.setApplicationTheme(context, 2);
            }
                break;*/
            default : {
                SettingsActivity.THEME = R.style.Theme_MMovies_Light;
                MMoviesPreferences.setApplicationTheme(context, 0);
            }
                break;
        }
    }

    /**
     * Calls {@link Context#startActivity(Intent)} with the given {@link Intent}. If it is
     * <b>implicit</b>, makes sure there is an Activity to handle it. If <b>explicit</b>,
     * will intercept {@link android.content.ActivityNotFoundException}. Can show an error toast on
     * failure.
     *
     * <p> E.g. an implicit intent may fail if e.g. the web browser has been disabled through
     * restricted profiles.
     *
     * @return Whether the {@link Intent} could be handled.
     */


    public static String getVersion(Context context) {
        String version;
        try {
            version = context.getPackageManager().getPackageInfo(context.getPackageName(),
                    PackageManager.GET_META_DATA).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            version = "UnknownVersion";
        }
        return version;
    }


}
