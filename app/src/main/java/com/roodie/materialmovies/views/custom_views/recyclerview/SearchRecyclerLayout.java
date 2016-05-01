package com.roodie.materialmovies.views.custom_views.recyclerview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AnimationUtils;

/**
 * Created by Roodie on 26.03.2016.
 */
public class SearchRecyclerLayout extends DetailRecyclerLayout {

    public SearchRecyclerLayout(Context context) {
        super(context);
    }

    public SearchRecyclerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SearchRecyclerLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setContentShown(boolean shown, boolean animate) {
        mEmptyView.setVisibility(View.GONE);
        mLoadingView.setVisibility(View.GONE);

        if (mContentShown == shown) {
            return;
        }
        mContentShown = shown;

        if (shown) {
            if (animate) {
                mRecyclerView.startAnimation(AnimationUtils.loadAnimation(
                        getContext(), android.R.anim.fade_in));
            } else {
                mRecyclerView.clearAnimation();
            }
            mRecyclerView.setVisibility(View.VISIBLE);
        } else {
            if (animate) {
                mRecyclerView.startAnimation(AnimationUtils.loadAnimation(
                        getContext(), android.R.anim.fade_out));
            } else {
                mRecyclerView.clearAnimation();
            }
            mRecyclerView.setVisibility(View.GONE);
        }

    }
}
