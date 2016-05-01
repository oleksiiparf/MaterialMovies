package com.roodie.materialmovies.views.listeners;

import android.support.design.widget.AppBarLayout;

import com.roodie.model.util.FileLog;

/**
 * Created by Roodie on 18.03.2016.
 */
public abstract class AppBarStateChangeListener implements AppBarLayout.OnOffsetChangedListener {
    private static final String TAG = "AppBarStateChange";

    public enum State {
        EXPANDED,
        COLLAPSED,
        IDLE
    }

    private State mCurrentState = State.EXPANDED;
    private int mInitialPosition = 0;
    private boolean isAnimating;

    @Override
    public final void onOffsetChanged(AppBarLayout appBarLayout, int i) {
        boolean mWasExpanded;
        if (i == 0) {
            if (mCurrentState != State.EXPANDED) {
                onStateChanged(appBarLayout, State.EXPANDED);
            }
            mCurrentState = State.EXPANDED;
            mInitialPosition = 0;
            mWasExpanded = true;
            FileLog.d(TAG, "onOffsetChanged to EXPANDED");
            isAnimating = false;
            appBarLayout.setEnabled(true);
        } else if (Math.abs(i) >= appBarLayout.getTotalScrollRange()) {
            if (mCurrentState != State.COLLAPSED) {
                onStateChanged(appBarLayout, State.COLLAPSED);
            }
            mCurrentState = State.COLLAPSED;
            mInitialPosition = appBarLayout.getTotalScrollRange();
            mWasExpanded = false;
            FileLog.d(TAG, "onOffsetChanged to COLLAPSED");
            isAnimating = false;
            appBarLayout.setEnabled(true);
        } else {
            FileLog.d(TAG, "onOffsetChanged to IDLE");
            int diff = Math.abs(Math.abs(i) - mInitialPosition);
            if (diff >= appBarLayout.getTotalScrollRange() / 3 && !isAnimating) {
                FileLog.d(TAG, "onOffsetChanged 4");
                isAnimating = true;
                appBarLayout.setEnabled(false);
                //Not need actually
                //appBarLayout.setExpanded(!mWasExpanded,true);
            }
            if (mCurrentState != State.IDLE) {
                onStateChanged(appBarLayout, State.IDLE);
            }
            mCurrentState = State.IDLE;
        }
    }



    public abstract void onStateChanged(AppBarLayout appBarLayout, State state);

    public State getCurrentState() {
        return mCurrentState;
    }
}
