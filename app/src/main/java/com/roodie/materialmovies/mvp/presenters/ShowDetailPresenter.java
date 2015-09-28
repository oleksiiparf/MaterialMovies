package com.roodie.materialmovies.mvp.presenters;

import android.util.Log;

import com.google.common.base.Preconditions;
import com.roodie.materialmovies.mvp.views.MovieView;
import com.roodie.materialmovies.qualifiers.GeneralPurpose;
import com.roodie.model.entities.ShowWrapper;
import com.roodie.model.state.ApplicationState;
import com.roodie.model.state.BaseState;
import com.roodie.model.state.MoviesState;
import com.roodie.model.tasks.BaseMovieRunnable;
import com.roodie.model.tasks.FetchDetailTvShowRunnable;
import com.roodie.model.util.BackgroundExecutor;
import com.roodie.model.util.Injector;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

/**
 * Created by Roodie on 16.09.2015.
 */
public class ShowDetailPresenter extends BasePresenter<ShowDetailPresenter.ShowDetailView> {

    private static final String LOG_TAG = ShowDetailPresenter.class.getSimpleName();

    private final BackgroundExecutor mExecutor;

    private final Injector mInjector;

    @Inject
    public ShowDetailPresenter(
            @GeneralPurpose BackgroundExecutor executor,
            ApplicationState state, Injector injector) {
        super(state);
        mExecutor = Preconditions.checkNotNull(executor, "executor can not be null");
        mInjector = Preconditions.checkNotNull(injector, "injector cannot be null");
    }

    @Subscribe
    public void onShowDetailChanged(MoviesState.TvShowInformationUpdatedEvent event) {
        Log.d(LOG_TAG, "show detail changed");
        populateUi();
        checkDetailTvShowResult(event.callingId, event.item);
    }

    @Subscribe
    public void onNetworkError(BaseState.OnErrorEvent event) {
        Log.d(LOG_TAG, "network error");
        if (isViewAttached() && null != event.error) {
            getView().showError(event.error);
        }
    }

    @Subscribe
    public void onLoadingProgressVisibilityChanged(BaseState.ShowLoadingProgressEvent event) {
        Log.d(LOG_TAG, "loading progress chenged");
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
        Log.d(LOG_TAG, "initialize");

        fetchDetailTvShowIfNeeded(getView().hashCode(), getView().getRequestParameter());
    }

    public void populateUi() {
        Log.d(LOG_TAG, "populateUi: " + getView().getClass().getSimpleName());

        final ShowWrapper show = mState.getTvShow(getView().getRequestParameter());

        switch (getView().getQueryType()) {
            case SHOW_DETAIL:
                if (show != null) {
                    getView().updateDisplayTitle(show.getTitle());
                    getView().setTvShow(show);
                }
                break;
        }
    }

    /**
     * Fetch detail movie information
     */
    private void fetchDetailTvShow(final int callingId, String id) {
        Preconditions.checkNotNull(id, "id cannot be null");

        final ShowWrapper tvShow = mState.getTvShow(id);
        if (tvShow != null) {

            fetchDetailTvShowIfNeeded(callingId, tvShow, true);
        }
    }

    private void fetchDetailTvShowFromTmdb(final int callingId, int id) {
        Preconditions.checkNotNull(id, "id cannot be null");
        Log.d(LOG_TAG, "fetch show from tmdb");
        ShowWrapper show = mState.getTvShow(id);
        if (show != null) {
            show.markFullFetchStarted();
        }

        executeTask(new FetchDetailTvShowRunnable(callingId, id));
    }

    private void fetchDetailTvShowIfNeeded(final int callingId, String id) {
        Log.d(LOG_TAG, "fetch show if needed");
        Preconditions.checkNotNull(id, "id cannot be null");

        ShowWrapper cached = mState.getTvShow(id);
        if (cached == null) {
            Log.d(LOG_TAG, "cached == null");
            fetchDetailTvShow(callingId, id);
        } else {
            Log.d(LOG_TAG, "cached != null");
            fetchDetailTvShowIfNeeded(callingId, cached, false);
        }
    }

    private void fetchDetailTvShowIfNeeded(int callingId, ShowWrapper show, boolean force) {
        Preconditions.checkNotNull(show, "show cannot be null");

        if (force || show.needFullFetchFromTmdb()) {
            if (show.getTmdbId() != null) {
                Log.d(LOG_TAG, "show.getTmdbId() != null");
                fetchDetailTvShowFromTmdb(callingId, show.getTmdbId());
            }
        } else {
            populateUi();
        }
    }

    public void refresh() {
        Log.d(LOG_TAG, "Refresh");
        fetchDetailTvShow(getView().hashCode(), getView().getRequestParameter());
    }

    private void checkDetailTvShowResult(int callingId, ShowWrapper show) {
        Log.d(LOG_TAG, "check detail tv show result");
        Preconditions.checkNotNull(show, "show cannot be null");
        fetchDetailTvShowIfNeeded(callingId, show, false);
    }

    private <R> void executeTask(BaseMovieRunnable<R> task) {
        mInjector.inject(task);
        mExecutor.execute(task);
    }


    public interface ShowDetailView extends MovieView {

        void setTvShow(ShowWrapper show);

        void showTvShowImages(ShowWrapper movie);

        void showTvShowCreditsDialog(MovieQueryType queryType);
    }


}
