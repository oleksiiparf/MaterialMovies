package com.roodie.materialmovies.settings;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 * Created by Roodie on 11.07.2015.
 */
public class DisplaySettings {


    /**
     * Returns true for all screens with dpi higher than {@link DisplayMetrics#DENSITY_HIGH}.
     */
    public static boolean isHighDestinyScreen(Context context) {
        return context.getResources().getDisplayMetrics().densityDpi > DisplayMetrics.DENSITY_HIGH;
    }
}
