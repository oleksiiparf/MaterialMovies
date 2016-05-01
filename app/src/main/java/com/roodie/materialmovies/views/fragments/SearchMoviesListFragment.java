package com.roodie.materialmovies.views.fragments;

import android.view.View;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.google.common.base.Preconditions;
import com.roodie.materialmovies.mvp.presenters.ListMoviesPresenter;
import com.roodie.materialmovies.mvp.views.ListMoviesView;
import com.roodie.materialmovies.views.adapters.FooterViewListAdapter;
import com.roodie.materialmovies.views.adapters.WatchableListAdapter;
import com.roodie.materialmovies.views.fragments.base.BaseListFragment;
import com.roodie.model.Display;
import com.roodie.model.entities.MovieWrapper;

import java.util.List;

/**
 * Created by Roodie on 06.09.2015.
 */

public class SearchMoviesListFragment extends BaseListFragment<WatchableListAdapter.WatchableListViewHolder, List<MovieWrapper>, ListMoviesView> implements ListMoviesView {

    @InjectPresenter
    ListMoviesPresenter mPresenter;

    @Override
    public MMoviesQueryType getQueryType() {
        return MMoviesQueryType.SEARCH_MOVIES;
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
        MovieWrapper item = mAdapter.getItems().get(position);
        showItemDetail(item, view);
    }

    @Override
    public void showItemDetail(MovieWrapper movie, View view) {
        Preconditions.checkNotNull(movie, "movie cannot be null");

        Display display = getDisplay();
        if (display != null) {
            if (movie.getTmdbId() != null) {
                display.startMovieDetailActivity(String.valueOf(movie.getTmdbId()), null);
            }
        }
    }

    @Override
    public void onPopupMenuClick(View view, int position) {

    }
}
