package com.roodie.materialmovies.mvp.presenters;

import android.os.Bundle;
import android.util.Log;

import com.google.common.base.Preconditions;
import com.roodie.materialmovies.mvp.views.MovieView;
import com.roodie.materialmovies.qualifiers.GeneralPurpose;
import com.roodie.model.Display;
import com.roodie.model.entities.MovieWrapper;
import com.roodie.model.entities.PersonCreditWrapper;
import com.roodie.model.entities.PersonWrapper;
import com.roodie.model.state.ApplicationState;
import com.roodie.model.state.AsyncDatabaseHelper;
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
public class MovieDetailPresenter extends BasePresenter {

    private MovieDetailView mMoviesView;

    private static final String LOG_TAG = MovieDetailPresenter.class.getSimpleName();

    private final BackgroundExecutor mExecutor;
    private final AsyncDatabaseHelper mDbHelper;

    private final ApplicationState mState;
    private final Injector mInjector;

    private boolean attached = false;

    @Inject
    public MovieDetailPresenter(
            @GeneralPurpose BackgroundExecutor executor,
            AsyncDatabaseHelper dbHelper, ApplicationState state, Injector injector) {
        super();
        mExecutor = Preconditions.checkNotNull(executor, "executor can not be null");
        mDbHelper = Preconditions.checkNotNull(dbHelper, "executor can not be null");
        mState = Preconditions.checkNotNull(state, "application state cannot be null");
        mInjector = Preconditions.checkNotNull(injector, "injector cannot be null");
    }

    @Subscribe
    public void onMovieDetailChanged(MoviesState.MovieInformationUpdatedEvent event) {
        populateUi(event);
        checkDetailMovieResult(event.callingId, event.item);
    }

    @Subscribe
    public void onNetworkError(BaseState.OnErrorEvent event) {
        if (attached && null != event.error) {
            mMoviesView.showError(event.error);
        }
    }

    @Subscribe
    public void onLoadingProgressVisibilityChanged(BaseState.ShowLoadingProgressEvent event) {
        if (attached) {
            if (event.secondary) {
                mMoviesView.showSecondaryLoadingProgress(event.show);
            } else {
                mMoviesView.showLoadingProgress(event.show);
            }
        }
    }

    @Override
    public void initialize() {
        checkViewAlreadySetted();
        fetchDetailMovieIfNeeded(2, mMoviesView.getRequestParameter());
    }

    public void attachView (MovieDetailView view) {
        Preconditions.checkNotNull(view, "View cannot be null");
        this.mMoviesView = view;
        attached = true;
        mState.registerForEvents(this);
    }

    @Override
    public void onResume() {
    }

    @Override
    public void onPause() {
        mState.unregisterForEvents(this);
    }


    public void populateUi(BaseState.BaseArgumentEvent event) {
        Preconditions.checkNotNull(event, "event cannot be null");

        Log.d(LOG_TAG, "populateUI: " + mMoviesView.getClass().getSimpleName());
        final MovieWrapper movie = mState.getMovie(mMoviesView.getRequestParameter());

        if (movie != null) {
            mMoviesView.setMovie(movie);
        }
    }

    public void refresh() {
        fetchDetailMovie(2, mMoviesView.getRequestParameter());
    }

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
                fetchDetailMovieFromTmdb(callingId,Integer.valueOf(movie.getImdbId()));
            }
        }
    }

    private void checkDetailMovieResult(int callingId, MovieWrapper movie) {
        Preconditions.checkNotNull(movie, "movie cannot be null");
        fetchDetailMovieIfNeeded(callingId, movie, false);
    }


    private void checkViewAlreadySetted() {
        Preconditions.checkState(attached = true, "View not attached");
    }

    private <R> void executeTask(BaseMovieRunnable<R> task) {
        mInjector.inject(task);
        mExecutor.execute(task);
    }


    public void showMovieDetail(MovieWrapper movie, Bundle bundle) {
        Preconditions.checkNotNull(movie, "movie cannot be null");

        Display display = getDisplay();
        if (display != null) {
            if (movie.getTmdbId() != null) {
                display.startMovieDetailActivity(String.valueOf(movie.getTmdbId()), bundle);
            }
        }
    }

    public void showMovieDetail(PersonCreditWrapper credit, Bundle bundle) {
        Preconditions.checkNotNull(credit, "credit cannot be null");

        Display display = getDisplay();
        if (display != null) {
            display.startMovieDetailActivity(String.valueOf(credit.getId()), bundle);
        }
    }

    public void showRelatedMovies(MovieWrapper movie) {
        Preconditions.checkNotNull(movie, "movie cannot be null");
        Display display = getDisplay();
        if (display != null) {
            display.showRelatedMovies(String.valueOf(movie.getTmdbId()));
        }
    }

    public void showMovieImages(MovieWrapper movie) {
        Preconditions.checkNotNull(movie, "movie cannot be null");

        final Display display = getDisplay();
        if (display != null) {
            display.startMovieImagesActivity(String.valueOf(movie.getTmdbId()));
        }
    }

    public void showPersonDetail(PersonWrapper person, Bundle bundle) {
        Preconditions.checkNotNull(person, "person cannot be null");
        Preconditions.checkNotNull(person.getTmdbId(), "person id cannot be null");

        Display display = getDisplay();
        if (display != null) {
            display.startPersonDetailActivity(String.valueOf(person.getTmdbId()), bundle);
        }
    }

    public void showCastList(MovieWrapper movie) {
        Preconditions.checkNotNull(movie, "movie cannot be null");

        Display display = getDisplay();
        if (display != null) {
            display.showCastListFragment(String.valueOf(movie.getTmdbId()));
        }
    }

    public void showCrewList(MovieWrapper movie) {
        Preconditions.checkNotNull(movie, "movie cannot be null");

        Display display = getDisplay();
        if (display != null) {
            display.showCrewListFragment(String.valueOf(movie.getTmdbId()));
        }
    }



    public interface MovieDetailView extends MovieView {

        void setMovie(MovieWrapper movie);
    }
}
