package com.roodie.model.tasks;

import android.text.TextUtils;

import com.roodie.model.entities.SeasonWrapper;
import com.roodie.model.entities.ShowWrapper;
import com.roodie.model.state.BaseState;
import com.roodie.model.state.MoviesState;
import com.roodie.model.util.MoviesCollections;
import com.uwetrottmann.tmdb.entities.TvSeason;

import retrofit.RetrofitError;

/**
 * Created by Roodie on 16.09.2015.
 */
public class FetchShowSeasonRunnable extends BaseMovieRunnable<TvSeason> {

    private final int showId;
    private final int seasonNumber;

    public FetchShowSeasonRunnable(int callingId, int showId, int seasonNumber) {
        super(callingId);
        this.showId = showId;
        this.seasonNumber = seasonNumber;
    }

    @Override
    public TvSeason doBackgroundCall() throws RetrofitError {
        return getTmdbClient().tvSeasonsService().season(showId, seasonNumber, getCountryProvider().getTwoLetterLanguageCode());
    }

    @Override
    public void onSuccess(TvSeason result) {
        ShowWrapper show = mState.getTvShow(showId);
        SeasonWrapper season = show.getSeason(String.valueOf(seasonNumber));
        season.markFullFetchCompleted();
        if (season != null) {
            if (!TextUtils.isEmpty(result.name)) {
                season.setTitle(result.name);
            }

            if (!TextUtils.isEmpty(result.overview)) {
                season.setOverview(result.overview);
            }

            if (!MoviesCollections.isEmpty(result.episodes)) {
                season.setEpisodes(result.episodes);
            }

            getEventBus().post(new MoviesState.TvShowSeasonUpdatedEvent(getCallingId(), show, season));
        }

    }

    @Override
    public void onError(RetrofitError re) {
        super.onError(re);

        ShowWrapper show = mState.getTvShow(showId);
        SeasonWrapper season = show.getSeason(String.valueOf(seasonNumber));
        if (season != null) {
            getEventBus().post(new MoviesState.TvShowSeasonUpdatedEvent(getCallingId(), show, season));
        }
    }

    @Override
    protected Object createLoadingProgressEvent(boolean show) {
        return new BaseState.ShowTvSeasonLoadingProgressEvent(getCallingId(), show);
    }
}
