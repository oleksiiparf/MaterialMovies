package com.roodie.materialmovies.views.activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import com.roodie.materialmovies.R;
import com.roodie.materialmovies.util.MMoviesVisitManager;
import com.roodie.materialmovies.views.custom_views.CirclePageIndicator;
import com.roodie.model.Display;
import com.roodie.model.util.VisitManager;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by Roodie on 03.09.2015.
 */
public class WelcomeActivity extends BaseActivity {

    @Inject VisitManager mVisitManager;

    private static final int WELCOME_ACTIVITY_PAGER_SIZE = 3;

    @InjectView(R.id.viewpager)
    ViewPager mViewPager;

    @InjectView(R.id.welcome_view_1)
    View mWelcomeViewPage1;

    @InjectView(R.id.welcome_view_2)
    View mWelcomeViewPage2;

    @InjectView(R.id.welcome_view_3)
    View mWelcomeViewPage3;

    @InjectView(R.id.welcome_pager_dot_indicator)
    CirclePageIndicator mPageIndicator;


    @Override
    protected int getThemeResId() {
        return R.style.Theme_MMovies_Green;
    }

    private void finishIfFirstVisitAlreadyPerformed() {
        if (mVisitManager.isFirstVisitPerformed()) {
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (mVisitManager == null) {
            // #Issue
            mVisitManager = new MMoviesVisitManager(this);
        }
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        WelcomePagerAdapter welcomePagerAdapter = new WelcomePagerAdapter();
        this.mViewPager.setAdapter(welcomePagerAdapter);
        this.mViewPager.setOffscreenPageLimit(WELCOME_ACTIVITY_PAGER_SIZE);
        this.mPageIndicator.setViewPager(this.mViewPager);
    }

    @Override
    protected void onResume() {
        super.onResume();
        finishIfFirstVisitAlreadyPerformed();
    }

    @Override
    protected void handleIntent(Intent intent, Display display) {
        if (mVisitManager.isFirstVisitPerformed()) {
            if (display != null) {
                display.startWatchlistActivity();
            }
        }
    }

    @OnClick({R.id.continue_btn})
    public void onContinueClicked() {
        mVisitManager.recordFirstVisitPerformed();
        if (getDisplay() != null) {
            getDisplay().startWatchlistActivity();
        }
    }

    @Override
    protected int getContentViewLayoutId() {
        return R.layout.activity_welcome;
    }

    private class WelcomePagerAdapter extends PagerAdapter {

        public WelcomePagerAdapter() {
        }

        @Override
        public int getCount() {
            return WELCOME_ACTIVITY_PAGER_SIZE;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            switch (position) {
                default:
                    return null;
                case 0:
                    return WelcomeActivity.this.mWelcomeViewPage1;
                case 1:
                    return WelcomeActivity.this.mWelcomeViewPage2;
                case 2:
                    return WelcomeActivity.this.mWelcomeViewPage3;
            }
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }
}
