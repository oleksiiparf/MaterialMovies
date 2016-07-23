package com.roodie.materialmovies.views.custom_views.recyclerview;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;
import com.roodie.materialmovies.R;

/**
 * Implementation of {@link UltimateRecyclerView} with {@link AutofitGridRecyclerView}
 * instead of RecyclerView
 */
public class MMoviesRecyclerView extends UltimateRecyclerView {

    private boolean isRecyclerAutofit;

    public MMoviesRecyclerView(Context context) {
        super(context);
    }

    public MMoviesRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MMoviesRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void initAttrs(AttributeSet attrs) {
        super.initAttrs(attrs);
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.MMoviesRecyclerView);
        try {
            isRecyclerAutofit = typedArray.getBoolean(R.styleable.MMoviesRecyclerView_recyclerAutofit, false);
        } finally {
            typedArray.recycle();
        }
    }

    protected void checkRecyclerAutofit() {
        if (isRecyclerAutofit) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mSwipeRefreshLayout.removeView(mRecyclerView);
            View animationGridRecyclerView = inflater.inflate(R.layout.recyclerview_layout_grid, mSwipeRefreshLayout, true);
            mRecyclerView = (AutofitGridRecyclerView) animationGridRecyclerView.findViewById(R.id.recycler_view);
        }
    }

    @Override
    protected void setScrollbars() {
        super.setScrollbars();
        checkRecyclerAutofit();
    }

    /* Show empty view regardless from empty_policy. */
    @Override
    public boolean showEmptyView() {
        if (mEmpty != null && mEmptyView != null ) {
                mEmpty.setVisibility(View.VISIBLE);
                if (mEmptyViewListener != null) {
                    mEmptyViewListener.onEmptyViewShow(mEmptyView);
                }
            return true;
        } else {
            Log.d(VIEW_LOG_TAG, "it is unable to show empty view");
            return false;
        }
    }
}
