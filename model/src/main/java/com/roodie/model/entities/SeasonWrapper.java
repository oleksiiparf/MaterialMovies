package com.roodie.model.entities;

import android.text.TextUtils;

import com.google.common.base.Preconditions;
import com.roodie.model.util.MoviesCollections;
import com.uwetrottmann.tmdb.entities.TvSeason;

import java.util.Date;
import java.util.List;

/**
 * Created by Roodie on 13.08.2015.
 */
public class SeasonWrapper extends BasicWrapper<SeasonWrapper> {

    Long _id;

    Integer tmdbId;

    long airDate;

    int episodeCount;

    String title;

    String overview;

    int seasonNumber;

    String posterUrl;

    transient List<MovieCreditWrapper> cast;
    transient List<MovieCreditWrapper> crew;
    transient List<EpisodeWrapper> episodes;

    public SeasonWrapper() {
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
            airDate = unbox(airDate, season.air_date);
        }

        if (!TextUtils.isEmpty(season.overview)) {
            overview = season.overview;
        }

        seasonNumber = season.season_number;

        if (!TextUtils.isEmpty(season.poster_path)) {
            posterUrl = season.poster_path;
        }
    }

    private boolean needFullFetch() {
        return MoviesCollections.isEmpty(cast)
                || MoviesCollections.isEmpty(crew)
                || MoviesCollections.isEmpty(episodes);
    }

    private static long unbox(long currentValue, Date newValue) {
        return newValue != null ? newValue.getTime() : currentValue;
    }

    public Long getDbId() {
        return _id;
    }

    public Integer getTmdbId() {
        return tmdbId;
    }

    public long getAirDate() {
        return airDate;
    }

    public int getEpisodeCount() {
        return episodeCount;
    }

    public String getTitle() {
        return title;
    }

    public String getOverview() {
        return overview;
    }

    public int getSeasonNumber() {
        return seasonNumber;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public boolean hasPosterUrl() {
        return !TextUtils.isEmpty(posterUrl);
    }

    public List<MovieCreditWrapper> getCast() {
        return cast;
    }

    public List<MovieCreditWrapper> getCrew() {
        return crew;
    }

    public List<EpisodeWrapper> getEpisodes() {
        return episodes;
    }

    public void setCast(List<MovieCreditWrapper> cast) {
        this.cast = cast;
    }

    public void setCrew(List<MovieCreditWrapper> crew) {
        this.crew = crew;
    }

    public void setEpisodes(List<EpisodeWrapper> episodes) {
        this.episodes = episodes;
    }


}
