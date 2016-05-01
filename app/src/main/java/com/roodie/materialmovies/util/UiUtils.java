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

    private static  UiUtils instance;

    private int offsetHeight;

    public static UiUtils getInstance() {
        if (instance == null)
            instance = new UiUtils();
        return instance;
    }

    /**
     *  Customize the location of your Toast under the custom view
     *
     * @param paramView view, needed for toast
     *
     */
    public void attachToastPopup(final Activity paramActivity, final View paramView) {
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
                Toast.makeText(paramActivity, paramView.getContentDescription(), Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }


    public float centerX(View view){
        return ViewHelper.getX(view) + view.getWidth()/2;
    }

    public float centerY(View view){
        return ViewHelper.getY(view) + view.getHeight()/2;
    }

    public int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public int dp(float value) {
        if (value == 0) {
            return 0;
        }
        return (int) Math.ceil(Resources.getSystem().getDisplayMetrics().density * value);
    }

}
