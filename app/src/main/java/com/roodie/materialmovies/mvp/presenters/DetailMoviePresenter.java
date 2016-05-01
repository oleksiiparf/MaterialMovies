package com.roodie.materialmovies.mvp.presenters;


import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.google.common.base.Preconditions;
import com.roodie.materialmovies.MMoviesApp;
import com.roodie.materialmovies.mvp.views.MovieDetailView;
import com.roodie.model.entities.MovieWrapper;
import com.roodie.model.state.BaseState;
import com.roodie.model.state.MoviesState;
import com.roodie.model.tasks.BaseRunnable;
import com.roodie.model.tasks.DatabaseBackgroundRunnable;
import com.roodie.model.tasks.FetchDetailMovieRunnable;
import com.roodie.model.tasks.MarkEntitySeenRunnable;
import com.roodie.model.tasks.MarkEntityUnseenRunnable;
import com.roodie.model.util.FileLog;
import com.squareup.otto.Subscribe;


/**
 * Created by Roodie on 25.06.2015.
 */

@InjectViewState
public class DetailMoviePresenter extends MvpPresenter<MovieDetailView> implements BaseDetailPresenter<MovieDetailView> {

    private String mRequestParameter;

    private static final String LOG_TAG = DetailMoviePresenter.class.getSimpleName();

    public DetailMoviePresenter() {
        super();
        MMoviesApp.get().getState().registerForEvents(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MMoviesApp.get().getState().unregisterForEvents(this);
        mRequestParameter = null;
    }

    @Override
    public int getId(MovieDetailView view) {
        return view.hashCode();
    }

    @Override
    public String getUiTitle(String parameter) {
        final MovieWrapper movie = MMoviesApp.get().getState().getMovie(parameter);
        if (movie != null) {
            return movie.getTitle();
        }
        return null;
    }

    @Override
    public <BR> void executeNetworkTask(BaseRunnable<BR> task) {
        MMoviesApp.get().inject(task);
        MMoviesApp.get().getBackgroundExecutor().execute(task);
    }

    public <BR> void executeBackgroundTask(DatabaseBackgroundRunnable<BR> task) {
        MMoviesApp.get().inject(task);
        MMoviesApp.get().getBackgroundExecutor().execute(task);
    }

    @Override
    public void attachUiByParameter(MovieDetailView view, String requestedParameter) {
        final int callingId = getId(view);
        mRequestParameter = requestedParameter;
        fetchDetailMovieIfNeeded(callingId, requestedParameter);
        populateUi(view, mRequestParameter);
    }

    @Override
    public void refresh(MovieDetailView view, String parameter) {
        final int callingId = getId(view);
        fetchDetailMovie(callingId, parameter);
    }

    private void checkDetailMovieResult(int callingId, MovieWrapper movie) {
        Preconditions.checkNotNull(movie, "movie cannot be null");
        fetchDetailMovieIfNeeded(callingId, movie, false);
    }

    public void toggleMovieWatched(MovieDetailView view, MovieWrapper movie) {
        Preconditions.checkNotNull(movie, "movie cannot be null");
        final int callingId = getId(view);
        if (movie.isWatched()) {
            markMovieUnseen(callingId, movie);
        } else {
            markMovieSeen(callingId, movie);
        }
    }

    @Override
    public void populateUi(MovieDetailView view, String parameter) {
        final MovieWrapper movie = MMoviesApp.get().getState().getMovie(parameter);
        if (movie != null) {
            view.updateDisplayTitle(movie.getTitle());
            view.setData(movie);
        }
    }

    @Subscribe
    public void onMovieDetailChanged(MoviesState.MovieInformationUpdatedEvent event) {
        populateUi(getViewState(), mRequestParameter);
        checkDetailMovieResult(event.callingId, event.item);
    }

    @Subscribe
    public void onMovieWatchedChanged(MoviesState.MovieFlagUpdateEvent event) {
        populateUi(getViewState(), mRequestParameter);
    }


    @Subscribe
    public void onNetworkError(BaseState.OnErrorEvent event) {
        if (null != event.error) {
            getViewState().showError(event.error);
        }
    }

    @Subscribe
    public void onLoadingProgressVisibilityChanged(BaseState.ShowLoadingProgressEvent event) {
        if(!event.secondary) {
            getViewState().showLoadingProgress(event.show);
        }
    }

    /**
     * Fetch detail movie information
     */
    private void fetchDetailMovie(final int callingId, String id) {
        Preconditions.checkNotNull(id, "id cannot be null");

        final MovieWrapper movie = MMoviesApp.get().getState().getMovie(id);
        if (movie != null) {
            fetchDetailMovieIfNeeded(callingId, movie, true);
        }
    }

    private void fetchDetailMovieFromTmdb(final int callingId, int id) {
        Preconditions.checkNotNull(id, "id cannot be null");

        MovieWrapper movie = MMoviesApp.get().getState().getMovie(id);
        if (movie != null) {
            movie.markFullFetchStarted();
        }

        executeNetworkTask(new FetchDetailMovieRunnable(callingId, id));
    }

    private void fetchDetailMovieIfNeeded(final int callingId, String id) {
        Preconditions.checkNotNull(id, "id cannot be null");

        MovieWrapper cached = MMoviesApp.get().getState().getMovie(id);
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
                fetchDetailMovieFromTmdb(callingId, movie.getTmdbId());
            }
        }
    }

    private void markMovieSeen(int callingId, MovieWrapper movie) {
        FileLog.d("watched", "DetailMoviePresenter : Mark movie seen");
        executeBackgroundTask(new MarkEntitySeenRunnable(callingId, movie));


    }

    private void markMovieUnseen(int callingId, MovieWrapper movie) {
        executeBackgroundTask(new MarkEntityUnseenRunnable(callingId, movie));

    }
}
