package com.roodie.model.entities;

import android.text.TextUtils;

import com.google.common.base.Preconditions;
import com.roodie.model.util.IntUtils;
import com.roodie.model.util.MoviesCollections;
import com.uwetrottmann.tmdb.entities.TvEpisode;

import java.util.Date;
import java.util.List;

/**
 * Created by Roodie on 13.08.2015.
 */
public class EpisodeWrapper extends BasicWrapper<EpisodeWrapper> {

    Long _id;

    Integer tmdbId;

    long airDate;

    int episodeNumber;

    String title;

    String overview;

    String production_code;

    int seasonNumber;

    String stillUrl;

    int ratingPercent;
    int ratingVotes;

    transient List<MovieCreditWrapper> cast;
    transient List<MovieCreditWrapper> crew;

    transient long lastFullFetchStarted;
    transient long lastFullFetchCompleted;

    public EpisodeWrapper() {
    }

    public void setFromEpisode(TvEpisode episode) {
        Preconditions.checkNotNull(episode, "Episode cannot be null");

        tmdbId = episode.id;

        if (_id == null) {
            if (tmdbId != null) {
                _id = new Long(tmdbId.hashCode());
            }
        }

        if (!TextUtils.isEmpty(episode.name)) {
            title = episode.name;
        }

        if (episode.air_date != null) {
            airDate = unbox(airDate, episode.air_date);
        }

        if (episode.episode_number != null) {
            episodeNumber = episode.episode_number;
        }

        ratingPercent = unbox(ratingPercent, episode.vote_average);
        ratingVotes = unbox(ratingVotes, episode.vote_count);

        if (!TextUtils.isEmpty(episode.overview)) {
            overview = episode.overview;
        }

        seasonNumber = episode.season_number;

        if (!TextUtils.isEmpty(episode.still_path)) {
            stillUrl = episode.still_path;
        }

    }

    private boolean needFullFetch() {
        return MoviesCollections.isEmpty(cast)
                || MoviesCollections.isEmpty(crew);
    }



    private static long unbox(long currentValue, Date newValue) {
        return newValue != null ? newValue.getTime() : currentValue;
    }

    private static int unbox(int currentValue, Double newValue) {
        return newValue != null ? ((int) (newValue * 10)) : currentValue;
    }

    private static int unbox(int currentValue, Integer newValue) {
        return newValue != null ? newValue : currentValue;
    }

    public Long getDBId() {
        return _id;
    }

    public long getAirDate() {
        return airDate;
    }

    public void setAirDate(long airDate) {
        this.airDate = airDate;
    }

    public int getEpisodeNumber() {
        return episodeNumber;
    }

    public void setEpisodeNumber(int episodeNumber) {
        this.episodeNumber = episodeNumber;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getProduction_code() {
        return production_code;
    }

    public void setProduction_code(String production_code) {
        this.production_code = production_code;
    }

    public int getSeasonNumber() {
        return seasonNumber;
    }

    public void setSeasonNumber(int season_number) {
        this.seasonNumber = season_number;
    }

    public String getStillUrl() {
        return stillUrl;
    }

    public boolean hasStillUrl() {
        return !TextUtils.isEmpty(stillUrl);
    }

    public void setStillUrl(String still_path) {
        this.stillUrl = still_path;
    }

    public int getRatingPercent() {
        return ratingPercent;
    }

    public void setRatingPercent(int ratingPercent) {
        this.ratingPercent = ratingPercent;
    }

    public int getRatingVotes() {
        return ratingVotes;
    }

    public void setRatingVotes(int ratingVotes) {
        this.ratingVotes = ratingVotes;
    }

    public List<MovieCreditWrapper> getCast() {
        return cast;
    }

    public void setCast(List<MovieCreditWrapper> cast) {
        this.cast = cast;
    }

    public List<MovieCreditWrapper> getCrew() {
        return crew;
    }

    public void setCrew(List<MovieCreditWrapper> crew) {
        this.crew = crew;
    }

    public void markFullFetchStarted() {
        lastFullFetchStarted = System.currentTimeMillis();
    }

    public void markFullFetchCompleted() {
        lastFullFetchCompleted = System.currentTimeMillis();
    }

    public int getAverageRatingPercent() {
        if ( ratingPercent > 0) {
            return IntUtils.weightedAverage(
                    ratingPercent, ratingVotes);
        } else {
            return ratingPercent;
        }
    }


}
