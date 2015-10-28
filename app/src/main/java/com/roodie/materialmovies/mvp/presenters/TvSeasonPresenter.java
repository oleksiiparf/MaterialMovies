package com.roodie.materialmovies.mvp.presenters;

import android.util.Log;

import com.google.common.base.Preconditions;
import com.roodie.materialmovies.mvp.views.BaseSeasonView;
import com.roodie.materialmovies.qualifiers.GeneralPurpose;
import com.roodie.model.entities.SeasonWrapper;
import com.roodie.model.state.ApplicationState;
import com.roodie.model.state.BaseState;
import com.roodie.model.state.MoviesState;
import com.roodie.model.tasks.FetchDetailTvSeasonRunnable;
import com.roodie.model.util.BackgroundExecutor;
import com.roodie.model.util.Injector;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

/**
 * Created by Roodie on 23.09.2015.
 */
public class TvSeasonPresenter extends BaseSeasonPresenter<TvSeasonPresenter.TvSeasonView> {

    private static final String LOG_TAG = TvSeasonPresenter.class.getSimpleName();


    @Inject
    public TvSeasonPresenter(ApplicationState state, @GeneralPurpose BackgroundExecutor executor, Injector injector) {
        super(state, executor, injector);
    }

    @Subscribe
    public void onTvSeasonDetailsChanged(MoviesState.TvShowSeasonUpdatedEvent event) {
        Log.d(LOG_TAG, "season details changed");
        populateUi();
        checkDetailSeasonResult(event.callingId, event.item, event.secondaryItem);
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

        fetchDetailSeasonIfNeeded(getView().hashCode(), getView().getShowId(), getView().getRequestParameter());
    }


    public void populateUi() {
        Log.d(LOG_TAG, "populateUi: " + getView().getClass().getSimpleName());

        final SeasonWrapper season = mState.getTvSeason(getView().getRequestParameter());

        switch (getView().getQueryType()) {
            case TV_SEASON_DETAIL:
                if (season != null) {
                    getView().updateDisplayTitle(season.getTitle());
                    getView().setSeason(season);
                }
                break;
        }
    }


    /**
     * Fetch tv season details information
     */
    private void fetchDetailSeason(final int callingId, String showId, String seasonId) {
        Preconditions.checkNotNull(showId, "id cannot be null");
        Preconditions.checkNotNull(seasonId, "id cannot be null");

        final SeasonWrapper season = mState.getTvSeason(seasonId);
        if (season != null) {

            fetchDetailSeasonIfNeeded(callingId, showId, season, true);
        }
    }

    private void fetchDetailSeasonFromTmdb(final int callingId, String showId, Integer seasonId) {
        Preconditions.checkNotNull(showId, "showId cannot be null");
        Preconditions.checkNotNull(seasonId, "seasonId cannot be null");

        SeasonWrapper season = mState.getTvSeason(seasonId);
        if (season != null) {
            season.markFullFetchStarted();
        }

        executeTask(new FetchDetailTvSeasonRunnable(callingId, Integer.valueOf(showId), season.getSeasonNumber()));
    }

    private void fetchDetailSeasonIfNeeded(final int callingId, String showId, String seasonId) {
        Preconditions.checkNotNull(showId, "showId cannot be null");
        Preconditions.checkNotNull(seasonId, "seasonId cannot be null");

        SeasonWrapper cached = mState.getTvSeason(seasonId);
        if (cached == null) {
            fetchDetailSeason(callingId, showId, seasonId);
        } else {
            fetchDetailSeasonIfNeeded(callingId, showId, cached, false);
        }
    }

    private void fetchDetailSeasonIfNeeded(int callingId, String showId, SeasonWrapper season, boolean force) {
        Preconditions.checkNotNull(showId, "showId cannot be null");
        Preconditions.checkNotNull(season, "season cannot be null");

        if (force || season.needFullFetchFromTmdb()) {
            if (season.getId() != null)
                fetchDetailSeasonFromTmdb(callingId, showId, season.getId());
        } else {
            populateUi();
        }
    }

    private void checkDetailSeasonResult(int callingId, String showId, SeasonWrapper season) {
        Preconditions.checkNotNull(showId, "showId cannot be null");
        Preconditions.checkNotNull(season, "season cannot be null");
        fetchDetailSeasonIfNeeded(callingId, showId, season, false);
    }


    public interface TvSeasonView extends BaseSeasonView {

        String getShowId();

        void setSeason(SeasonWrapper season);

    }

}
