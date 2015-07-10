package com.roodie.materialmovies.mvp.presenters;

import android.view.View;

import com.roodie.materialmovies.mvp.views.BaseMovieListView;
import com.roodie.model.entities.MovieWrapper;


/**
 * Created by Roodie on 10.07.2015.
 */
public class MovieGridPresenter extends BasePresenter {

    private MovieGridView mMovieGridView;

    @Override
    public void onResume() {

    }

    @Override
    protected void onInited() {
        super.onInited();
    }

    @Override
    protected void onPaused() {
        super.onPaused();
    }

    @Override
    public boolean hasCallbacks() {
        return mMovieGridView != null;
    }

    protected void attachView(MovieGridView view) {
        this.mMovieGridView = view;
    }

    public MovieGridView getCallbacks() {
        return mMovieGridView;
    }

    public interface MovieGridView extends BaseMovieListView<MovieWrapper> {

    }
}
