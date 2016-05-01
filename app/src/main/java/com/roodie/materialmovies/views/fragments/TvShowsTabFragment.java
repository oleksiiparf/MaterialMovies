package com.roodie.materialmovies.views.fragments;

import android.support.v4.app.Fragment;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.google.common.base.Preconditions;
import com.roodie.materialmovies.MMoviesApp;
import com.roodie.materialmovies.mvp.presenters.TabShowsPresenter;
import com.roodie.materialmovies.mvp.views.TvShowsTabView;
import com.roodie.materialmovies.mvp.views.UiView;
import com.roodie.materialmovies.util.StringUtils;
import com.roodie.materialmovies.views.fragments.base.BaseTabFragment;
import com.roodie.model.Display;
import com.roodie.model.network.NetworkError;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Roodie on 01.08.2015.
 */

public class TvShowsTabFragment extends BaseTabFragment<BaseTabFragment.TabPagerAdapter> implements TvShowsTabView {

    @InjectPresenter
    TabShowsPresenter mPresenter;

    private List<ShowTabs> mTabs;

    @Override
    public void updateDisplayTitle(String title) {
        Display display = getDisplay();
        if (display != null) {
            display.setActionBarTitle(title);
        }
    }

    @Override
    protected void attachUiToPresenter() {
        mPresenter.attachUiToPresenter(this);
    }

    @Override
    public void updateDisplaySubtitle(String subtitle) {
        Display display = getDisplay();
        if (display != null) {
            display.setActionBarSubtitle(subtitle);
        }
    }

    @Override
    public void setData(List<ShowTabs> data) {
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
    public UiView.MMoviesQueryType getQueryType() {
         return UiView.MMoviesQueryType.SHOWS_TAB;
     }

    @Override
    protected TabPagerAdapter setupAdapter() {
        return new TabPagerAdapter(getChildFragmentManager());
    }

    @Override
    protected String getTabTitle(int position) {
        if (mTabs != null) {
            return getResources().getString(StringUtils.getShowsStringResId(mTabs.get(position)));
        }
        return null;
    }

    private Fragment createFragmentForTab(UiView.ShowTabs tab) {
        switch (tab) {
            case POPULAR:
                return Fragment.instantiate(MMoviesApp.get().getAppContext(), PopularShowsFragment.class.getName());
            case ON_THE_AIR:
                return Fragment.instantiate(MMoviesApp.get().getAppContext(), OnTheAirShowsFragment.class.getName());
        }
        return null;
    }
}
