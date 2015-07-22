package com.roodie.materialmovies.util;

import android.content.Context;
import android.widget.Toast;

import com.roodie.materialmovies.R;

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
}
