package com.roodie.materialmovies.views.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import com.google.common.base.Preconditions;
import com.roodie.materialmovies.mvp.presenters.ShowGridPresenter;
import com.roodie.materialmovies.mvp.presenters.ShowTabPresenter;
import com.roodie.materialmovies.util.StringUtils;
import com.roodie.materialmovies.views.MMoviesApplication;
import com.roodie.materialmovies.views.fragments.base.BaseTabFragment;
import com.roodie.model.Display;
import com.roodie.model.network.NetworkError;

import java.util.ArrayList;

/**
 * Created by Roodie on 01.08.2015.
 */

public class ShowsTabFragment extends BaseTabFragment implements ShowTabPresenter.ShowsTabView {

    private ShowTabPresenter mPresenter;

    private ShowGridPresenter mGridPresenter;

    private ShowTabs[] mTabs;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mPresenter = MMoviesApplication.from(activity.getApplicationContext()).getShowTabsPresenter();
        mGridPresenter = MMoviesApplication.from(activity.getApplicationContext()).getShowGridPresenter();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mPresenter.attachView(this);
        mPresenter.initialize();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mPresenter.detachView(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.onResume();
        mGridPresenter.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mGridPresenter.onPause();
        mPresenter.onPause();
    }


    @Override
    public void updateDisplayTitle(String title) {
        Display display = getDisplay();
        if (display != null) {
            display.setActionBarTitle(title);
        }
    }

    @Override
    public void showError(NetworkError error) {

    }

    @Override
    public void showLoadingProgress(boolean visible) {

    }

    @Override
    public void showSecondaryLoadingProgress(boolean visible) {

    }

    @Override
    public String getRequestParameter() {
        return null;
    }

    @Override
    public MovieQueryType getQueryType() {
        return MovieQueryType.SHOWS_TAB;
    }

    @Override
    public boolean isModal() {
        return false;
    }

    @Override
    public void setupTabs(ShowTabs... tabs) {
        Preconditions.checkNotNull(tabs, "tabs cannot be null");
        mTabs = tabs;

        if (getAdapter().getCount() != tabs.length) {
            ArrayList<Fragment> fragments = new ArrayList<>();
            for (int i = 0; i < tabs.length; i++) {
                fragments.add(createFragmentForTab(tabs[i]));
            }
            setFragments(fragments);
        }
    }

    @Override
    protected String getTabTitle(int position) {
        if (mTabs != null) {
            return getString(StringUtils.getShowsStringResId(mTabs[position]));
        }
        return null;
    }

    private Fragment createFragmentForTab(ShowTabs tab) {
        switch (tab) {
            case POPULAR:
                return new PopularShowsFragment();
            case ON_THE_AIR:
                return new OnTheAirShowsFragment();
        }
        return null;
    }
}
