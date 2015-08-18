package com.roodie.materialmovies.util;

import android.view.View;

import com.nineoldandroids.view.ViewHelper;

/**
 * Created by Roodie on 17.08.2015.
 */
public class ViewUtils {

    public static float centerX(View view){
        return ViewHelper.getX(view) + view.getWidth()/2;
    }

    public static float centerY(View view){
        return ViewHelper.getY(view) + view.getHeight()/2;
    }


}
