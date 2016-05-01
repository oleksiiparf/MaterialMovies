package com.roodie.materialmovies.views.fragments;

import android.view.View;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.google.common.base.Preconditions;
import com.roodie.materialmovies.mvp.presenters.ListTvShowsPresenter;
import com.roodie.materialmovies.mvp.views.ListTvShowsView;
import com.roodie.materialmovies.views.adapters.FooterViewListAdapter;
import com.roodie.materialmovies.views.adapters.WatchableListAdapter;
import com.roodie.materialmovies.views.fragments.base.BaseListFragment;
import com.roodie.model.Display;
import com.roodie.model.entities.ShowWrapper;

import java.util.List;

/**
 * Created by Roodie on 07.09.2015.
 */

public class SearchShowsListFragment extends BaseListFragment<WatchableListAdapter.WatchableListViewHolder, List<ShowWrapper>, ListTvShowsView> implements ListTvShowsView {

    @InjectPresenter
    ListTvShowsPresenter mPresenter;

    @Override
    public MMoviesQueryType getQueryType() {
        return MMoviesQueryType.SEARCH_SHOWS;
    }

    @Override
    protected FooterViewListAdapter createAdapter() {
        return new WatchableListAdapter(getActivity(), this);
    }

    @Override
    protected void attachUiToPresenter() {
        mPresenter.onUiAttached(this, getQueryType(), null);
        Display display = getDisplay();
        if (display != null) {
            display.showUpNavigation(getQueryType() != null && getQueryType().showUpNavigation());
        }
    }

    @Override
    public void onClick(View view, int position) {
        ShowWrapper item = mAdapter.getItems().get(position);
        showItemDetail(item, view);
    }

    @Override
    public void showItemDetail(ShowWrapper tvShow, View ui) {
        Preconditions.checkNotNull(tvShow, "tv cannot be null");

        Display display = getDisplay();
        if (display != null) {
            if (tvShow.getTmdbId() != null) {
                display.startTvDetailActivity(String.valueOf(tvShow.getTmdbId()), null);
            }
        }
    }

    @Override
    public void onPopupMenuClick(View view, int position) {

    }
}
