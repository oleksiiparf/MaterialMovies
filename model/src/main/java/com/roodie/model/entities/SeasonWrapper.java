package com.roodie.model.entities;

import android.text.TextUtils;

import com.google.common.base.Preconditions;
import com.roodie.model.Constants;
import com.roodie.model.util.MoviesCollections;
import com.uwetrottmann.tmdb.entities.TvEpisode;
import com.uwetrottmann.tmdb.entities.TvSeason;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import static com.roodie.model.util.TimeUtils.isPastStartingPoint;

/**
 * Created by Roodie on 13.08.2015.
 */
public class SeasonWrapper extends BasicWrapper<SeasonWrapper> implements Serializable {

    Long _id;

    Integer tmdbId;

    Date airDate;

    Integer episodeCount;

    String title;

    String overview;

    Integer seasonNumber;

    String posterUrl;

    boolean isStarred;

    transient long lastFullFetchFromTmdbStarted;
    transient long lastFullFetchFromTmdbCompleted;

    transient List<CreditWrapper> cast;
    transient List<CreditWrapper> crew;
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

        if (_id == null) {
            if (tmdbId != null) {
                _id = new Long(tmdbId.hashCode());
            }
        }

        if (!TextUtils.isEmpty(season.name)) {
            title = season.name;
        }

        if (season.air_date != null) {
            airDate = season.air_date;
        }

        if (!TextUtils.isEmpty(season.overview)) {
            overview = season.overview;
        }

        seasonNumber = season.season_number;

        if (!TextUtils.isEmpty(season.poster_path)) {
            posterUrl = season.poster_path;
        }

        isStarred = false;
    }

    private static long unbox(long currentValue, Date newValue) {
        return newValue != null ? newValue.getTime() : currentValue;
    }

    public Long getDbId() {
        return _id;
    }

    public Integer getId() {
        return tmdbId;
    }

    public Date getAirDate() {
        return airDate;
    }

    public Integer getEpisodeCount() {
        return episodeCount;
    }

    public String getTitle() {
        return title;
    }

    public String getOverview() {
        return overview;
    }

    public Integer getSeasonNumber() {
        return seasonNumber;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public boolean hasPosterUrl() {
        return !TextUtils.isEmpty(posterUrl);
    }

    public List<CreditWrapper> getCast() {
        return cast;
    }

    public List<CreditWrapper> getCrew() {
        return crew;
    }

    public List<TvEpisode> getEpisodes() {
        return episodes;
    }

    public void setCast(List<CreditWrapper> cast) {
        this.cast = cast;
    }

    public void setCrew(List<CreditWrapper> crew) {
        this.crew = crew;
    }

    public void setEpisodes(List<TvEpisode> episodes) {
        this.episodes = episodes;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public boolean isStarred() {
        return isStarred;
    }

    public void setStarred(boolean isStarred) {
        this.isStarred = isStarred;
    }

    public boolean needFullFetch() {
        return  (!TextUtils.isEmpty(title))
                || (!TextUtils.isEmpty(overview))
                || MoviesCollections.isEmpty(episodes);
    }

    public boolean needFullFetchFromTmdb() {
        return (needFullFetch() || isPastStartingPoint(lastFullFetchFromTmdbCompleted,
                Constants.STALE_MOVIE_DETAIL_THRESHOLD)) &&
                isPastStartingPoint(lastFullFetchFromTmdbStarted,
                        Constants.FULL_MOVIE_DETAIL_ATTEMPT_THRESHOLD);
    }

    public void markFullFetchStarted() {
        lastFullFetchFromTmdbStarted = System.currentTimeMillis();
    }

    public void markFullFetchCompleted() {
        lastFullFetchFromTmdbCompleted = System.currentTimeMillis();
    }


    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Season ");
        sb.append(seasonNumber).append(" {");
        sb.append("tmdbId=").append(tmdbId);
        sb.append(", title='").append(title).append('\'');
        sb.append(", overview='").append(overview).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
