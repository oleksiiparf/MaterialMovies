package com.roodie.materialmovies.mvp.presenters;

import android.util.Log;

import com.google.common.base.Preconditions;
import com.roodie.materialmovies.mvp.views.SeasonDetailView;
import com.roodie.materialmovies.qualifiers.GeneralPurpose;
import com.roodie.model.entities.SeasonWrapper;
import com.roodie.model.entities.ShowWrapper;
import com.roodie.model.state.ApplicationState;
import com.roodie.model.state.BaseState;
import com.roodie.model.state.MoviesState;
import com.roodie.model.tasks.FetchShowSeasonRunnable;
import com.roodie.model.util.BackgroundExecutor;
import com.roodie.model.util.Injector;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

/**
 * Created by Roodie on 27.09.2015.
 */
public class SeasonDetailPresenter extends BaseSeasonPresenter<SeasonDetailView> {

    private static final String LOG_TAG = SeasonDetailPresenter.class.getSimpleName();

    @Inject
    public SeasonDetailPresenter(ApplicationState state, @GeneralPurpose BackgroundExecutor executor, Injector injector) {
        super(state, executor, injector);
    }

    @Subscribe
    public void onSeasonDetailChanged(MoviesState.TvShowSeasonUpdatedEvent event) {
        Log.d(LOG_TAG, "season detail changed");
        populateUi();
        checkDetailTvSeasonResult(event.callingId, event.item , event.secondaryItem);
    }

    @Subscribe
    public void onNetworkError(BaseState.OnErrorEvent event) {
        if (isViewAttached() && null != event.error) {
            getView().showError(event.error);
        }
    }

    @Subscribe
    public void onLoadingProgressVisibilityChanged(BaseState.ShowTvSeasonLoadingProgressEvent event) {
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

        fetchDetailSeasonIfNeeded(getView().hashCode(), getView().getRequestTvShow(), getView().getRequestParameter());
    }

    @Override
    public void attachView(SeasonDetailView view) {
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

        final ShowWrapper show = mState.getTvShow(getView().getRequestTvShow());
        final SeasonWrapper season = show.getSeason(getView().getRequestParameter());

        switch (getView().getQueryType()) {
            case TV_SEASON_DETAIL:
                if (season != null) {
                    getView().updateDisplayTitle(season.getTitle());
                    getView().setTvSeason(season);
                }
                break;
        }
    }

    public void refresh() {
        fetchDetailSeason(getView().hashCode(), getView().getRequestTvShow(), getView().getRequestParameter());
    }

    /**
     * Fetch detail movie information
     */
    private void fetchDetailSeason(final int callingId, String showId, String seasonNumber) {
        Preconditions.checkNotNull(showId, "showId cannot be null");
        Preconditions.checkNotNull(seasonNumber, "seasonNumber cannot be null");

        final ShowWrapper show = mState.getTvShow(showId);
        final SeasonWrapper season = show.getSeason(seasonNumber);
        if (season != null) {

            fetchDetailSeasonIfNeeded(callingId, show, season, true);
        }
    }

    private void fetchDetailSeasonFromTmdb(final int callingId, int showId, int seasonNumber) {
        Preconditions.checkNotNull(showId, "showId cannot be null");
        Preconditions.checkNotNull(seasonNumber, "seasonNumber cannot be null");

        SeasonWrapper season = mState.getTvShow(showId).getSeason(String.valueOf(seasonNumber));
        if (season != null) {
            season.markFullFetchStarted();
        }

        executeTask(new FetchShowSeasonRunnable(callingId, showId, seasonNumber));
    }

    private void fetchDetailSeasonIfNeeded(final int callingId, String showId, String seasonNumber) {
        Preconditions.checkNotNull(showId, "showId cannot be null");
        Preconditions.checkNotNull(seasonNumber, "seasonNumber cannot be null");

        ShowWrapper show = mState.getTvShow(showId);
        SeasonWrapper cached = show.getSeason(seasonNumber);
        if (cached == null) {
            fetchDetailSeason(callingId, showId, seasonNumber);
        } else {
            fetchDetailSeasonIfNeeded(callingId, show, cached, false);
        }
    }

    private void fetchDetailSeasonIfNeeded(int callingId,ShowWrapper show, SeasonWrapper season, boolean force) {
        Preconditions.checkNotNull(show, "tvShow cannot be null");
        Preconditions.checkNotNull(season, "tvSeason cannot be null");

        if (force || season.needFullFetchFromTmdb()) {
            if (show.getTmdbId() != null) {
                fetchDetailSeasonFromTmdb(callingId, show.getTmdbId(), season.getId());
            } else {
                populateUi();
            }
        }
    }

    private void checkDetailTvSeasonResult(int callingId, ShowWrapper show, SeasonWrapper season) {
        Preconditions.checkNotNull(show, "tvShow cannot be null");
        Preconditions.checkNotNull(season, "season cannot be null");
        fetchDetailSeasonIfNeeded(callingId, show, season, false);
    }


}
