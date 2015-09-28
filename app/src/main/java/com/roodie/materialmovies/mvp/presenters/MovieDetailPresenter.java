package com.roodie.materialmovies.mvp.presenters;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.common.base.Preconditions;
import com.roodie.materialmovies.mvp.views.MovieView;
import com.roodie.materialmovies.qualifiers.GeneralPurpose;
import com.roodie.model.entities.MovieWrapper;
import com.roodie.model.entities.PersonWrapper;
import com.roodie.model.state.ApplicationState;
import com.roodie.model.state.BaseState;
import com.roodie.model.state.MoviesState;
import com.roodie.model.tasks.BaseMovieRunnable;
import com.roodie.model.tasks.FetchDetailMovieRunnable;
import com.roodie.model.util.BackgroundExecutor;
import com.roodie.model.util.Injector;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

/**
 * Created by Roodie on 25.06.2015.
 */
public class MovieDetailPresenter extends BasePresenter<MovieDetailPresenter.MovieDetailView> {

    private static final String LOG_TAG = MovieDetailPresenter.class.getSimpleName();

    private final BackgroundExecutor mExecutor;

    private final Injector mInjector;

    @Inject
    public MovieDetailPresenter(
            @GeneralPurpose BackgroundExecutor executor,
            ApplicationState state, Injector injector) {
        super(state);
        mExecutor = Preconditions.checkNotNull(executor, "executor can not be null");
        mInjector = Preconditions.checkNotNull(injector, "injector cannot be null");
    }

    @Subscribe
    public void onMovieDetailChanged(MoviesState.MovieInformationUpdatedEvent event) {
        Log.d(LOG_TAG, "movie detail changed");
        populateUi();
        checkDetailMovieResult(event.callingId, event.item);
    }

    @Subscribe
    public void onNetworkError(BaseState.OnErrorEvent event) {
        if (isViewAttached() && null != event.error) {
            getView().showError(event.error);
        }
    }

    @Subscribe
    public void onLoadingProgressVisibilityChanged(BaseState.ShowLoadingProgressEvent event) {
        if (isViewAttached()) {
            if (event.secondary) {
                getView().showSecondaryLoadingProgress(event.show);
            } else {
                getView().showLoadingProgress(event.show);
            }
        }
    }

    @Override
    public void initialize() {
        Log.d(LOG_TAG, "initialize()");

        fetchDetailMovieIfNeeded(getView().hashCode(), getView().getRequestParameter());
    }

    @Override
    public void attachView(MovieDetailView view) {
        Log.d(LOG_TAG, "attachView()");
        super.attachView(view);

    }

    @Override
    public void detachView(boolean retainInstance) {
        Log.d(LOG_TAG, "detachView()");
        super.detachView(retainInstance);
    }


    public void populateUi() {
        Log.d(LOG_TAG, "populateUi: " + getView().getClass().getSimpleName());

        final MovieWrapper movie = mState.getMovie(getView().getRequestParameter());

        switch (getView().getQueryType()) {
            case MOVIE_DETAIL:
                if (movie != null) {
                    getView().updateDisplayTitle(movie.getTitle());
                    getView().setMovie(movie);
                }
                break;
        }
    }

    public void refresh() {
        fetchDetailMovie(getView().hashCode(), getView().getRequestParameter());
    }

    /**
     * Fetch detail movie information
     */
    private void fetchDetailMovie(final int callingId, String id) {
        Preconditions.checkNotNull(id, "id cannot be null");

        final MovieWrapper movie = mState.getMovie(id);
        if (movie != null) {

            fetchDetailMovieIfNeeded(callingId, movie, true);
        }
    }

    private void fetchDetailMovieFromTmdb(final int callingId, int id) {
        Preconditions.checkNotNull(id, "id cannot be null");

        MovieWrapper movie = mState.getMovie(id);
        if (movie != null) {
            movie.markFullFetchStarted();
        }

        executeTask(new FetchDetailMovieRunnable(callingId, id));
    }

    private void fetchDetailMovieIfNeeded(final int callingId, String id) {
        Preconditions.checkNotNull(id, "id cannot be null");

        MovieWrapper cached = mState.getMovie(id);
        if (cached == null) {
            fetchDetailMovie(callingId, id);
        } else {
            fetchDetailMovieIfNeeded(callingId, cached, false);
        }
    }

    private void fetchDetailMovieIfNeeded(int callingId, MovieWrapper movie, boolean force) {
        Preconditions.checkNotNull(movie, "movie cannot be null");

        if (force || movie.needFullFetchFromTmdb()) {
            if (movie.getTmdbId() != null) {
                fetchDetailMovieFromTmdb(callingId, movie.getTmdbId());
            } else {
                fetchDetailMovieFromTmdb(callingId, Integer.valueOf(movie.getImdbId()));
            }
        } else {
            populateUi();
        }
    }

    private void checkDetailMovieResult(int callingId, MovieWrapper movie) {
        Preconditions.checkNotNull(movie, "movie cannot be null");
        fetchDetailMovieIfNeeded(callingId, movie, false);
    }


    private <R> void executeTask(BaseMovieRunnable<R> task) {
        mInjector.inject(task);
        mExecutor.execute(task);
    }

    public interface MovieDetailView extends MovieView {

        void setMovie(MovieWrapper movie);

        void showMovieDetail(MovieWrapper movie, View view);

        void playTrailer();

        void showPersonDetail(PersonWrapper person, Bundle bundle);

        void showPersonDetail(PersonWrapper person, View view);

        void showMovieImages(MovieWrapper movie);

        void showMovieCreditsDialog(MovieQueryType queryType);
    }
}
