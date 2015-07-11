package com.roodie.materialmovies.util;

import android.content.Context;
import android.widget.Toast;

import com.google.common.base.Preconditions;
import com.roodie.materialmovies.R;
import com.roodie.model.util.AndroidUtils;
import com.roodie.model.util.MMoviesPreferences;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import javax.inject.Inject;

/**
 * Created by Roodie on 11.07.2015.
 */
public  class MMoviesServiceUtils {

    private  final MMoviesPreferences mPreferences;
    private final AndroidUtils mAndroidUtils;

    private static Picasso mPicasso;

    @Inject
    public MMoviesServiceUtils(MMoviesPreferences preferences, AndroidUtils utils) {
        mPreferences = Preconditions.checkNotNull(preferences, "preferences cannot be null");
        mAndroidUtils = Preconditions.checkNotNull(utils, "preferences cannot be null");
    }

    public static synchronized Picasso getPicasso(Context context) {
        if (mPicasso == null) {
            mPicasso = new Picasso.Builder(context).build();
        }
        return mPicasso;
    }

    public  RequestCreator loadWithPicasso(Context context, String path) {
        RequestCreator requestCreator = getPicasso(context).load(path);
        if (!isAllowedLargeDataConnection(context, false)) {
            // avoid the network, hit the cache immediately + accept stale images.
            requestCreator.networkPolicy(NetworkPolicy.OFFLINE);
        }
        return requestCreator;
    }

    /**
     * Returns true if there is an active connection which is approved by the user for large data
     * downloads (e.g. images).
     *
     * @param showOfflineToast If true, displays a toast asking the user to connect to a network.
     */
    public  boolean isAllowedLargeDataConnection(Context context, boolean showOfflineToast) {
        boolean isConnected;
         boolean largeDataOverWifiOnly = mPreferences.isLargeDataOverWifiOnly();

        // check connection state
        if (largeDataOverWifiOnly) {
            isConnected = mAndroidUtils.isWifiConnected(context);
        } else {
            isConnected = mAndroidUtils.isNetworkConnected(context);
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
