package com.roodie.materialmovies.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.Toast;

import com.google.common.base.Preconditions;
import com.nineoldandroids.view.ViewHelper;
import com.roodie.materialmovies.MMoviesApp;
import com.roodie.materialmovies.R;

import javax.inject.Inject;

/**
 * Created by Roodie on 17.08.2015.
 */
public class UiUtils {

    @Inject
    FontManager mFontManager;

    private Typeface menuTypeface;

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

    public void applyFontToMenu(Menu paramMenu, Context paramContext) {
        Preconditions.checkNotNull(paramMenu, "Menu cannot be null");
        if (menuTypeface == null) {
            menuTypeface = MMoviesApp.from(paramContext).getFontManager().getFont(FontManager.FONT_DIN_REGULAR);
           }
        for (int i = 0; i < paramMenu.size(); i++)
            applyFontToMenuItems(paramMenu.getItem(i), menuTypeface);
    }

    private static void applyFontToMenuItems(MenuItem paramMenuItem, Typeface paramTypeface)
    {
        paramMenuItem.setTitle(CustomTypefaceSpan.applySpan(paramMenuItem.getTitle(), paramTypeface));
        SubMenu localSubMenu = paramMenuItem.getSubMenu();
        if (localSubMenu != null)
            for (int i = 0; i < localSubMenu.size(); i++)
                applyFontToMenuItems(localSubMenu.getItem(i), paramTypeface);
    }
}
