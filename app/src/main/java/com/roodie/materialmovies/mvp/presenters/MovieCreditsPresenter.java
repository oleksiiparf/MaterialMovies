package com.roodie.materialmovies.mvp.presenters;

import com.roodie.materialmovies.mvp.views.BaseMovieListView;
import com.uwetrottmann.tmdb.entities.Credits;
import com.uwetrottmann.tmdb.entities.Movie;

/**
 * Created by Roodie on 06.07.2015.
 */
public class MovieCreditsPresenter extends BasePresenter {

    private MovieCreditListView mMovieCreditListView;

    public MovieCreditsPresenter() {
    }

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

    public interface MovieCreditListView extends BaseMovieListView<Credits> {

        void showCastList(Movie movie);

        void showCrewList(Movie movie);

    }
}
