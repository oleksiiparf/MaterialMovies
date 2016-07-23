package com.roodie.materialmovies.views.fragments;

import android.view.View;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.google.common.base.Preconditions;
import com.marshalchen.ultimaterecyclerview.quickAdapter.easyRegularAdapter;
import com.roodie.materialmovies.mvp.presenters.ListTvShowsPresenter;
import com.roodie.materialmovies.mvp.views.ListTvShowsView;
import com.roodie.materialmovies.views.adapters.ShowListAdapter;
import com.roodie.materialmovies.views.fragments.base.BaseListFragment;
import com.roodie.model.Display;
import com.roodie.model.entities.ShowWrapper;

import java.util.List;

/**
 * Created by Roodie on 07.09.2015.
 */

public class SearchShowsListFragment extends BaseListFragment<ShowListAdapter.WatchableListViewHolder, ShowWrapper> implements ListTvShowsView {

    @InjectPresenter
    ListTvShowsPresenter mPresenter;

    @Override
    public MMoviesQueryType getQueryType() {
        return MMoviesQueryType.SEARCH_SHOWS;
    }

    @Override
    protected easyRegularAdapter<ShowWrapper, ShowListAdapter.WatchableListViewHolder> createAdapter(List<ShowWrapper> data) {
        return new ShowListAdapter(data, this);
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
        ShowWrapper item = mAdapter.getObjects().get(position);
        showItemDetail(item, view);
    }

    @Override
    public void onScrolledToBottom() {
        super.onScrolledToBottom();
        mPresenter.onScrolledToBottom(this, getQueryType());
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
    public void showContextMenu(ShowWrapper tvShow) {
        //No impl.
    }

    @Override
    public void onPopupMenuClick(View view, int position) {

    }
}
