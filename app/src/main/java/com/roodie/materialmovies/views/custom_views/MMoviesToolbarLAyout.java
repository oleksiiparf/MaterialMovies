package com.roodie.materialmovies.views.custom_views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.support.design.widget.CollapsingToolbarLayout;
import android.util.AttributeSet;

import com.roodie.materialmovies.MMoviesApp;
import com.roodie.materialmovies.R;
import com.roodie.materialmovies.util.FontManager;

import javax.inject.Inject;

/**
 * Created by Roodie on 02.04.2016.
 */
public class MMoviesToolbarLayout extends CollapsingToolbarLayout {

    @Inject
    FontManager mFontManager;

    public MMoviesToolbarLayout(Context context) {
        this(context, null);
    }

    public MMoviesToolbarLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MMoviesToolbarLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        MMoviesApp.from(context).inject(this);

        if (!isInEditMode()) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MMoviesToolbarLayout);
            setCollapsedTitleTypeface(getTypeface(a.getInt(R.styleable.MMoviesToolbarLayout_font_collapsed, 7)));
            setExpandedTitleTypeface(getTypeface(a.getInt(R.styleable.MMoviesToolbarLayout_font_expanded, 7)));
            a.recycle();
        }
    }

    public Typeface getTypeface(final int font) {
        Typeface typeface = mFontManager.getFont(font);
        return typeface;
    }
}
