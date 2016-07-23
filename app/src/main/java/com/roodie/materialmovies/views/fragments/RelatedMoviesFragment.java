package com.roodie.materialmovies.views.fragments;

import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.google.common.base.Preconditions;
import com.marshalchen.ultimaterecyclerview.quickAdapter.easyRegularAdapter;
import com.roodie.materialmovies.mvp.presenters.ListMoviesPresenter;
import com.roodie.materialmovies.mvp.views.ListMoviesView;
import com.roodie.materialmovies.views.adapters.MovieListAdapter;
import com.roodie.materialmovies.views.fragments.base.BaseListFragment;
import com.roodie.model.Display;
import com.roodie.model.entities.MovieWrapper;

import java.util.List;

/**
 * Created by Roodie on 28.02.2016.
 */
public class RelatedMoviesFragment extends BaseListFragment<MovieListAdapter.WatchableListViewHolder, MovieWrapper> implements ListMoviesView {

    @InjectPresenter
    ListMoviesPresenter mMoviesPresenter;

    protected static final String QUERY_MOVIE_ID = "movie_id";

    public static  RelatedMoviesFragment newInstance(String id) {
        Preconditions.checkArgument(id != null, "movieId can not be null");

        Bundle bundle = new Bundle();
        bundle.putString(QUERY_MOVIE_ID, id);
        RelatedMoviesFragment fragment = new RelatedMoviesFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    protected void attachUiToPresenter() {
        mMoviesPresenter.onUiAttached(this, getQueryType(), getRequestParameter());
        Display display = getDisplay();
        if ( display != null) {
            display.showUpNavigation(getQueryType() != null && getQueryType().showUpNavigation());
        }

    }

    @Override
    public MMoviesQueryType getQueryType() {
        return MMoviesQueryType.RELATED_MOVIES;
    }

    public String getRequestParameter() {
        return getArguments().getString(QUERY_MOVIE_ID);
    }

    @Override
    protected easyRegularAdapter<MovieWrapper, MovieListAdapter.WatchableListViewHolder> createAdapter(List<MovieWrapper> data) {
        return new MovieListAdapter(data, this);
    }

    @Override
    public void onClick(View view, int position) {
        MovieWrapper item = getAdapter().getObjects().get(position);
        showItemDetail(item, view);
    }

    @Override
    public void onPopupMenuClick(View view, int position) {

    }

    @Override
    public void showItemDetail(MovieWrapper movie, View view) {
        Preconditions.checkNotNull(movie, "movie cannot be null");

        Display display = getDisplay();
        if (display != null) {
            if (movie.getTmdbId() != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    display.startMovieDetailActivityBySharedElements(String.valueOf(movie.getTmdbId()), view, (String) view.getTag());
                } else {
                    display.startMovieDetailActivity(String.valueOf(movie.getTmdbId()), null);
                }
            }
        }
    }


}
