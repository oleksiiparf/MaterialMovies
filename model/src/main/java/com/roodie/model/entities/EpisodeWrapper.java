package com.roodie.model.entities;

import android.text.TextUtils;

import com.google.common.base.Preconditions;
import com.uwetrottmann.tmdb.entities.TvEpisode;

/**
 * Created by Roodie on 13.08.2015.
 */
public class EpisodeWrapper extends Watchable {

    int episodeNumber;

    String production_code;

    int seasonNumber;

    String stillUrl;

    public EpisodeWrapper(String parentId) {
        super(parentId);
    }

    @Override
    public WatchableType getWatchableType() {
        return WatchableType.TV_EPISODE;
    }

    public void setFromEpisode(TvEpisode episode) {
        Preconditions.checkNotNull(episode, "Episode cannot be null");

        tmdbId = episode.id;


        if (!TextUtils.isEmpty(episode.name)) {
            tmdbTitle = episode.name;
        }

        if (episode.air_date != null) {
            tmdbFirstAirDate = episode.air_date;
        }

        if (episode.episode_number != null) {
            episodeNumber = episode.episode_number;
        }

        tmdbRatingPercent = unbox(tmdbRatingPercent, episode.vote_average);
        tmdbRatingVotesAmount = unbox(tmdbRatingVotesAmount, episode.vote_count);

        if (!TextUtils.isEmpty(episode.overview)) {
            tmdbOverview = episode.overview;
        }

        seasonNumber = episode.season_number;

        if (!TextUtils.isEmpty(episode.still_path)) {
            stillUrl = episode.still_path;
        }

    }

    public int getEpisodeNumber() {
        return episodeNumber;
    }

    public void setEpisodeNumber(int episodeNumber) {
        this.episodeNumber = episodeNumber;
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


}
