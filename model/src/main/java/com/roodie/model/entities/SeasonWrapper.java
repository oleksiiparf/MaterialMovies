package com.roodie.model.entities;

import android.text.TextUtils;

import com.google.common.base.Preconditions;
import com.roodie.model.Constants;
import com.roodie.model.util.MoviesCollections;
import com.uwetrottmann.tmdb.entities.TvEpisode;
import com.uwetrottmann.tmdb.entities.TvSeason;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.roodie.model.util.TimeUtils.isPastStartingPoint;

/**
 * Created by Roodie on 13.08.2015.
 */


public class SeasonWrapper implements Serializable{

    private static final long serialVersionUID = 1L;

    private static final Calendar CALENDAR = Calendar.getInstance();

    public Integer tmdbId;

    public String tmdbTitle;

    public String tmdbOverview;

    public String tmdbBackdropUrl;

    public String tmdbPosterUrl;

    public String tmdbGenres;

    public int tmdbRuntime;

    public int tmdbRatingPercent;

    public int tmdbRatingVotesAmount;

    public Double tmdbRatingVotesAverage;

    public String tmdbMainLanguage;

    public boolean isLiked;

    public boolean isWatched;

    public long tmdbFirstReleasedTime;

    public Date tmdbFirstAirDate;

    public int tmdbYear;

    public transient long lastFullFetchFromTmdbStarted;
    public transient long lastFullFetchFromTmdbCompleted;

    protected transient List<CreditWrapper> cast;
    protected transient List<CreditWrapper> crew;

    Integer episodeCount;

    Integer seasonNumber;

    transient List<TvEpisode> episodes;

    public SeasonWrapper() {
    }

    public SeasonWrapper(TvSeason season) {
        Preconditions.checkNotNull(season, "TvSeason cannot be null");
        setFromSeason(season);
    }

    public void setFromSeason(TvSeason season) {

        Preconditions.checkNotNull(season, "Season cannot be null");

        tmdbId = season.id;

        if (!TextUtils.isEmpty(season.name)) {
            tmdbTitle = season.name;
        }

        if (season.air_date != null) {
            tmdbFirstAirDate = season.air_date;
            tmdbFirstReleasedTime = unbox(tmdbFirstReleasedTime, season.air_date);
        }

        if (tmdbYear == 0 && tmdbFirstReleasedTime != 0) {
            CALENDAR.setTimeInMillis(tmdbFirstReleasedTime);
            tmdbYear = CALENDAR.get(Calendar.YEAR);
        }

        if (!TextUtils.isEmpty(season.overview)) {
            tmdbOverview = season.overview;
        }

        seasonNumber = season.season_number;

        if (!TextUtils.isEmpty(season.poster_path)) {
            tmdbPosterUrl = season.poster_path;
        }

        setWatched(false);
    }

    protected static long unbox(long currentValue, Date newValue) {
        return newValue != null ? newValue.getTime() : currentValue;
    }

    public boolean hasPosterUrl() {
        return !TextUtils.isEmpty(tmdbPosterUrl);
    }

    public boolean hasBackdropUrl() {
        return !TextUtils.isEmpty(tmdbBackdropUrl);
    }

    public List<CreditWrapper> getCrew() {
        return crew;
    }

    public List<CreditWrapper> getCast() {
        return cast;
    }

    public long getLastFullFetchFromTmdbCompleted() {
        return lastFullFetchFromTmdbCompleted;
    }

    public long getLastFullFetchFromTmdbStarted() {
        return lastFullFetchFromTmdbStarted;
    }

    public void setCrew(List<CreditWrapper> crew) {
        this.crew = crew;
    }

    public void setCast(List<CreditWrapper> cast) {
        this.cast = cast;
    }

    public void markFullFetchStarted() {
        lastFullFetchFromTmdbStarted = System.currentTimeMillis();
    }

    public void markFullFetchCompleted() {
        lastFullFetchFromTmdbCompleted = System.currentTimeMillis();
    }

    public Integer getEpisodeCount() {
        return episodeCount;
    }

    public Integer getSeasonNumber() {
        return seasonNumber;
    }

    public List<TvEpisode> getEpisodes() {
        return episodes;
    }

    public void setEpisodes(List<TvEpisode> episodes) {
        this.episodes = episodes;
    }

    public boolean isWatched() {
        return isWatched;
    }

    public void setWatched(boolean watched) {
        isWatched = watched;
    }

    public String getTitle() {
        return tmdbTitle;
    }

    public void setTitle(String title) {
        this.tmdbTitle = title;
    }

    public Integer getTmdbId() {
        return tmdbId;
    }

    public String getOverview() {
        return tmdbOverview;
    }

    public void setOverview(String overview) {
        this.tmdbOverview = overview;
    }

    public String getPosterUrl() {
        return tmdbPosterUrl;
    }

    public String getBackdropUrl() {
        return tmdbBackdropUrl;
    }

    public String getMainLanguageTitle() {
        return tmdbMainLanguage;
    }

    public int getRatingPercent() {
        return tmdbRatingPercent;
    }

    public String getGenres() {
        return tmdbGenres;
    }

    public long getReleasedTime() {
        return tmdbFirstReleasedTime;
    }

    public int getRuntime() {
        return tmdbRuntime;
    }

    public Date getReleaseDate() {
        return tmdbFirstAirDate;
    }

    public int getYear() {
        return tmdbYear;
    }

    public boolean needFullFetch() {
        return  (!TextUtils.isEmpty(getTitle()))
                || (!TextUtils.isEmpty(getOverview()))
                || MoviesCollections.isEmpty(episodes);
    }

    public boolean needFullFetchFromTmdb() {
        return (needFullFetch() || isPastStartingPoint(lastFullFetchFromTmdbCompleted,
                Constants.STALE_MOVIE_DETAIL_THRESHOLD)) &&
                isPastStartingPoint(lastFullFetchFromTmdbStarted,
                        Constants.FULL_MOVIE_DETAIL_ATTEMPT_THRESHOLD);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Season ");
        sb.append(seasonNumber).append(" {");
        sb.append("id=").append(tmdbId);
        sb.append(", title='").append(getTitle()).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
