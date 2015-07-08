package com.roodie.materialmovies.util;

import android.content.res.Resources;

/**
 * Created by Roodie on 30.06.2015.
 */
public class MMoviesProgressBarUtils {

    public static float dp2px(Resources resources, float dp) {
        final float scale = resources.getDisplayMetrics().density;
        return  dp * scale + 0.5f;
    }

    public static float sp2px(Resources resources, float sp){
        final float scale = resources.getDisplayMetrics().scaledDensity;
        return sp * scale;
    }
}
