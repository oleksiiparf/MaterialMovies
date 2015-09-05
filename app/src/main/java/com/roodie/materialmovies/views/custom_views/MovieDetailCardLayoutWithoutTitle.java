package com.roodie.materialmovies.views.custom_views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.roodie.materialmovies.R;

/**
 * Created by Roodie on 06.09.2015.
 */
public class MovieDetailCardLayoutWithoutTitle extends FrameLayout {

    private LinearLayout mCardContent;

    public MovieDetailCardLayoutWithoutTitle(Context context) {
        this(context, null);
    }

    public MovieDetailCardLayoutWithoutTitle(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MovieDetailCardLayoutWithoutTitle(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        LayoutInflater.from(context).inflate(R.layout.include_movie_detail_card_without_title, this, true);

        mCardContent = (LinearLayout) findViewById(R.id.card_content_holder);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MovieDetailCardLayoutWithoutTitle);
        a.recycle();
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if (mCardContent != null) {
            mCardContent.addView(child, index, params);
        } else {
            super.addView(child, index, params);
        }
    }
}