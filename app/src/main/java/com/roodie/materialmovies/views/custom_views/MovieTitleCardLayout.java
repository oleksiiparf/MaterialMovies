package com.roodie.materialmovies.views.custom_views;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.roodie.materialmovies.R;

/**
 * Created by Roodie on 28.02.2016.
 */
public class MovieTitleCardLayout extends FrameLayout {

    private final View mTitleLayout;
    private final TextView mTitleTextView;
    private LinearLayout mCardContent;


    public MovieTitleCardLayout(Context context) {
        this(context, null);
    }

    public MovieTitleCardLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MovieTitleCardLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        LayoutInflater.from(context).inflate(R.layout.include_movie_title_card, this, true);

        mTitleLayout = findViewById(R.id.card_header);

        mTitleTextView = (TextView) mTitleLayout.findViewById(R.id.title);

        mCardContent = (LinearLayout) findViewById(R.id.card_content_holder);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MovieDetailCardLayout);
        final String title = a.getString(R.styleable.MovieDetailCardLayout_title);
        if (!TextUtils.isEmpty(title)) {
            mTitleTextView.setText(title);
        }
        a.recycle();
    }

    public void setTitle(CharSequence title) {
        mTitleTextView.setText(title);
    }

    public void setTitle(int titleResId) {
        setTitle(getResources().getString(titleResId));
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