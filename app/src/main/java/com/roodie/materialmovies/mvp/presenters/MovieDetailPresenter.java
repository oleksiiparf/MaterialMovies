package com.roodie.materialmovies.mvp.presenters;

import android.os.Bundle;

import com.roodie.materialmovies.mvp.views.MovieView;
import com.roodie.materialmovies.qualifiers.GeneralPurpose;
import com.roodie.model.entities.MMoviesPersonCredit;
import com.roodie.model.state.AsyncDatabaseHelper;
import com.roodie.model.state.BaseState;
import com.roodie.model.state.MoviesState;
import com.roodie.model.tasks.BaseMovieRunnable;

import com.roodie.model.util.BackgroundExecutor;
import com.google.common.base.Preconditions;
import com.squareup.otto.Subscribe;
import com.uwetrottmann.tmdb.entities.Movie;

import javax.inject.Inject;

/**
 * Created by Roodie on 25.06.2015.
 */
public class MovieDetailPresenter extends BasePresenter {

    private static final int TMDB_FIRST_PAGE = 1;

    private final BackgroundExecutor mExecutor;
    private final AsyncDatabaseHelper mDbHelper;
    private final MoviesState mMoviesState;
    private MovieDetailView mMoviesView;

    @Inject
    public MovieDetailPresenter(
            @GeneralPurpose BackgroundExecutor executor,
            AsyncDatabaseHelper dbHelper, MoviesState movieState) {
        super();
        mMoviesState = Preconditions.checkNotNull(movieState, "moviesState cannot be null");
        mExecutor = Preconditions.checkNotNull(executor, "executor can not be null");
        mDbHelper = Preconditions.checkNotNull(dbHelper, "executor can not be null");
    }

    @Override
    public void onInited() {
        super.onInited();
        mMoviesState.registerForEvents(this);

    }

    @Override
    public void onResume() {

    }

    @Override
    protected void onPaused() {
        super.onPaused();
        mMoviesState.unregisterForEvents(this);
    }

    public void refresh() {

    }

    @Subscribe
    public void onTmdbConfigurationChanged(MoviesState.TmdbConfigurationChangedEvent event) {

    }

    @Subscribe
    public void onRecommendedChanged(MoviesState.RecommendedChangedEvent event) {

    }

    @Subscribe
    public void onMovieDetailChanged(MoviesState.MovieInformationUpdatedEvent event) {

    }

    @Subscribe
    public void onMovieReleasedChanged(MoviesState.MovieReleasesUpdatedEvent event) {

    }

    @Subscribe
    public void onMovieImagesChanged(MoviesState.MovieImagesUpdatedEvent event) {

    }

    @Subscribe
    public void onNetworkError(BaseState.OnErrorEvent event) {

    }


    private <R> void executeTask(BaseMovieRunnable<R> task) {
        mExecutor.execute(task);
    }


    public interface MovieDetailView extends MovieView {

        void setMovie(Movie movie);

        void showMovieDetail(Movie movie, Bundle bundle);

        void showMovieDetail(MMoviesPersonCredit credit, Bundle bundle);

        void showRelatedMovies(Movie movie);

    }
}
