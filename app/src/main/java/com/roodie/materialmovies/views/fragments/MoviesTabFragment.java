package com.roodie.materialmovies.views.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import com.google.common.base.Preconditions;
import com.roodie.materialmovies.mvp.presenters.MovieGridPresenter;
import com.roodie.materialmovies.mvp.presenters.MovieTabPresenter;
import com.roodie.materialmovies.util.StringUtils;
import com.roodie.materialmovies.views.MMoviesApplication;
import com.roodie.materialmovies.views.fragments.base.BaseTabFragment;
import com.roodie.model.Display;
import com.roodie.model.network.NetworkError;

import java.util.ArrayList;

/**
 * Created by Roodie on 02.08.2015.
 */

public class MoviesTabFragment extends BaseTabFragment<BaseTabFragment.TabPagerAdapter> implements MovieTabPresenter.MoviesTabView {

    private MovieTabPresenter mPresenter;

    private MovieGridPresenter mGridPresenter;


    private MovieTabs[] mTabs;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mPresenter = MMoviesApplication.from(activity.getApplicationContext()).getMovieTabsPresenter();
        mGridPresenter = MMoviesApplication.from(activity.getApplicationContext()).getMovieGridPresenter();
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
    public boolean isModal() {
        return false;
    }

    @Override
    public MovieQueryType getQueryType() {
        return MovieQueryType.MOVIES_TAB;
    }

    @Override
    public String getRequestParameter() {
        return null;
    }

    @Override
    public void showSecondaryLoadingProgress(boolean visible) {

    }

    @Override
    public void showLoadingProgress(boolean visible) {

    }

    @Override
    public void showError(NetworkError error) {

    }

    @Override
    protected TabPagerAdapter setupAdapter() {
        return new TabPagerAdapter(getChildFragmentManager());
    }

    @Override
    public void setupTabs(MovieTabs... tabs) {
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
            return getString(StringUtils.getMoviesStringResId(mTabs[position]));
        }
        return null;
    }

    private Fragment createFragmentForTab(MovieTabs tab) {
        switch (tab) {
            case POPULAR:
                return new PopularMoviesFragment();
            case IN_THEATRES:
                return new InTheatresMoviesFragment();
            case UPCOMING:
                return new UpcomingMoviesFragment();
        }
        return null;
    }


}
