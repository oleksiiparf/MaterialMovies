package com.roodie.materialmovies.views.custom_views;

import android.content.Context;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.roodie.materialmovies.MMoviesApp;
import com.roodie.materialmovies.util.FontManager;

import javax.inject.Inject;

/**
 * Implementation of {@link Toolbar} with customizable font
 */
public class MMoviesToolbar extends Toolbar {

    @Inject
    FontManager mFontManager;

    public MMoviesToolbar(Context context) {
        this(context, null);
    }

    public MMoviesToolbar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MMoviesToolbar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        MMoviesApp.from(context).inject(this);
    }

    public TextView getToolbarTitle(Toolbar paramToolbar)
    {
        int i = paramToolbar.getChildCount();
        for (int j = 0; j < i; j++)
        {
            View localView = paramToolbar.getChildAt(j);
            if ((localView instanceof TextView))
                return (TextView)localView;
        }
        throw new IllegalStateException("Toolbar title not found!");
    }

    public void setToolbarTitleTypeface() {
        TextView localTextView = getToolbarTitle(this);
        if (localTextView != null)
        {
            mFontManager.setFont(localTextView);
        }
    }


}
