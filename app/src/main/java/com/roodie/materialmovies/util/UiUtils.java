package com.roodie.materialmovies.util;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Rect;
import android.view.View;
import android.widget.Toast;

import com.nineoldandroids.view.ViewHelper;
import com.roodie.materialmovies.R;

/**
 * Created by Roodie on 17.08.2015.
 */
public class UiUtils {

    private static int offsetHeight;

    public static void attachToastPopup(final Activity paramActivity, final View paramView) {
        if (offsetHeight == 0) {
            View localView = paramActivity.getWindow().getDecorView();
            if (localView != null)
            {
                Rect localRect = new Rect();
                localView.getWindowVisibleDisplayFrame(localRect);
                offsetHeight = localRect.top + paramActivity.getResources().getDimensionPixelOffset(R.dimen.spacing_minor);
            }
        }
        paramView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(paramActivity, paramView.getContentDescription(), Toast.LENGTH_SHORT);
                return false;
            }
        });
    }


    public static float centerX(View view){
        return ViewHelper.getX(view) + view.getWidth()/2;
    }

    public static float centerY(View view){
        return ViewHelper.getY(view) + view.getHeight()/2;
    }

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }



}
