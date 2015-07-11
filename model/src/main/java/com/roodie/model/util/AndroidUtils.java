package com.roodie.model.util;

import android.content.Context;

/**
 * Created by Roodie on 11.07.2015.
 */
public interface AndroidUtils {

    /**
     * Returns true whether there is an active WiFi connection.
     */
        boolean isWifiConnected(Context context);

    /**
     * Whether there is any network connected.
     */
       boolean isNetworkConnected(Context context);
}
