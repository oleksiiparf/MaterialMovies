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
public class ShowDetailPresenter extends BasePresenter {

    private ShowDetailView mView;

    private static final String LOG_TAG = ShowDetailPresenter.class.getSimpleName();

    private final BackgroundExecutor mExecutor;

    private final ApplicationState mState;
    private final Injector mInjector;


    private boolean attached = false;

    @Inject
    public ShowDetailPresenter(
            @GeneralPurpose BackgroundExecutor executor,
            ApplicationState state, Injector injector) {
        super();
        mExecutor = Preconditions.checkNotNull(executor, "executor can not be null");
        mState = Preconditions.checkNotNull(state, "application state cannot be null");
        mInjector = Preconditions.checkNotNull(injector, "injector cannot be null");
    }

    @Subscribe
    public void onShowDetailChanged(MoviesState.TvShowInformationUpdatedEvent event) {
        populateUi();
        checkDetailTvShowResult(event.callingId, event.item);
    }

    @Subscribe
    public void onNetworkError(BaseState.OnErrorEvent event) {
        if (attached && null != event.error) {
            mView.showError(event.error);
        }
    }

    @Subscribe
    public void onLoadingProgressVisibilityChanged(BaseState.ShowLoadingProgressEvent event) {
        if (attached) {
            if (event.secondary) {
                mView.showSecondaryLoadingProgress(event.show);
            } else {
                mView.showLoadingProgress(event.show);
            }
        }
    }

    @Override
    public void initialize() {
        checkViewAlreadySetted();

        fetchDetailTvShowIfNeeded(2, mView.getRequestParameter());
    }

    public void attachView(ShowDetailView view) {
        Preconditions.checkNotNull(view, "View cannot be null");
        this.mView = view;
        attached = true;
    }

    @Override
    public void onResume() {
        mState.registerForEvents(this);
    }

    @Override
    public void onPause() {
        mState.unregisterForEvents(this);
    }

    public void populateUi() {
        Log.d(LOG_TAG, "populateUi: " + mView.getClass().getSimpleName());

        final ShowWrapper show = mState.getTvShow(mView.getRequestParameter());

        switch (mView.getQueryType()) {
            case SHOW_DETAIL:
                if (show != null) {
                    mView.updateDisplayTitle(show.getTitle());
                    mView.setTvShow(show);
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

        ShowWrapper show = mState.getTvShow(id);
        if (show != null) {
            show.markFullFetchStarted();
        }

        executeTask(new FetchDetailTvShowRunnable(callingId, id));
    }

    private void fetchDetailTvShowIfNeeded(final int callingId, String id) {
        Preconditions.checkNotNull(id, "id cannot be null");

        ShowWrapper cached = mState.getTvShow(id);
        if (cached == null) {
            fetchDetailTvShow(callingId, id);
        } else {
            fetchDetailTvShowIfNeeded(callingId, cached, false);
        }
    }

    private void fetchDetailTvShowIfNeeded(int callingId, ShowWrapper show, boolean force) {
        Preconditions.checkNotNull(show, "show cannot be null");

        if (force || show.needFullFetch()) {
            if (show.getTmdbId() != null)
                fetchDetailTvShowFromTmdb(callingId, show.getTmdbId());
        } else {
            populateUi();
        }
    }

    public void refresh() {
        fetchDetailTvShow(2, mView.getRequestParameter());
    }

    private void checkDetailTvShowResult(int callingId, ShowWrapper show) {
        Preconditions.checkNotNull(show, "show cannot be null");
        fetchDetailTvShowIfNeeded(callingId, show, false);
    }

    private void checkViewAlreadySetted() {
        Preconditions.checkState(attached = true, "View not attached");
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
