package com.roodie.materialmovies.views.fragments;

import android.app.Activity;
import android.support.v4.app.Fragment;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.google.common.base.Preconditions;
import com.roodie.materialmovies.MMoviesApp;
import com.roodie.materialmovies.mvp.presenters.TabMoviesPresenter;
import com.roodie.materialmovies.mvp.views.MoviesTabView;
import com.roodie.materialmovies.mvp.views.UiView;
import com.roodie.materialmovies.util.StringUtils;
import com.roodie.materialmovies.views.fragments.base.BaseTabFragment;
import com.roodie.model.Display;
import com.roodie.model.network.NetworkError;
import com.roodie.model.util.MoviesCollections;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Roodie on 02.08.2015.
 */

public class MoviesTabFragment extends BaseTabFragment<BaseTabFragment.TabPagerAdapter> implements MoviesTabView {

    @InjectPresenter
    TabMoviesPresenter mPresenter;

    private List<UiView.MovieTabs> mTabs;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }


    @Override
    protected void attachUiToPresenter() {
        mPresenter.attachUiToPresenter(this);
    }

    @Override
    public UiView.MMoviesQueryType getQueryType() {
        return UiView.MMoviesQueryType.MOVIES_TAB;
    }

    @Override
    public void updateDisplayTitle(String title) {
        Display display = getDisplay();
        if (display != null) {
            display.setActionBarTitle(title);
        }
    }

    @Override
    public void updateDisplaySubtitle(String subtitle) {
        Display display = getDisplay();
        if (display != null) {
            display.setActionBarSubtitle(subtitle);
        }
    }

    @Override
    protected TabPagerAdapter setupAdapter() {
        return new TabPagerAdapter(getChildFragmentManager());
    }

    @Override
    public void setData(List<MovieTabs> data) {
        Preconditions.checkNotNull(data, "tabs cannot be null");
        mTabs = data;

        if (getAdapter().getCount() != mTabs.size()) {
            ArrayList<Fragment> fragments = new ArrayList<>();
            for (int i = 0; i < mTabs.size(); i++) {
                fragments.add(createFragmentForTab(mTabs.get(i)));
            }
            setFragments(fragments);
        }
    }

    @Override
    public void showError(NetworkError error) {

    }

    @Override
    public void showLoadingProgress(boolean visible) {

    }

    @Override
    public void onRefreshData(boolean visible) {

    }

    @Override
    protected String getTabTitle(int position) {
        if (!MoviesCollections.isEmpty(mTabs)) {
            return getResources().getString(StringUtils.getTabTitle(mTabs.get(position)));
        }
        return null;
    }

    private Fragment createFragmentForTab(UiView.MovieTabs tab) {
        switch (tab) {
            case POPULAR:
                return Fragment.instantiate(MMoviesApp.get().getAppContext(), PopularMoviesFragment.class.getName());
            case IN_THEATRES:
                return Fragment.instantiate(MMoviesApp.get().getAppContext(), InTheatresMoviesFragment.class.getName());
            case UPCOMING:
                return Fragment.instantiate(MMoviesApp.get().getAppContext(), UpcomingMoviesFragment.class.getName());
        }
        return null;
    }
}
