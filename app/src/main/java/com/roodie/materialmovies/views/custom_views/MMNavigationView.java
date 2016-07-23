package com.roodie.materialmovies.views.custom_views;

import android.content.Context;
import android.support.design.widget.NavigationView;
import android.util.AttributeSet;

import com.roodie.materialmovies.util.UiUtils;

/**
 * Implementation of {@link NavigationView} with customizable font menu items
 */
public class MMNavigationView extends NavigationView {

    public MMNavigationView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void inflateMenu(int paramInt)
    {
        super.inflateMenu(paramInt);
        UiUtils.getInstance().applyFontToMenu(getMenu(), getContext());
    }
}
