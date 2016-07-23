package com.roodie.model.entities;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.common.base.Preconditions;
import com.roodie.model.Constants;
import com.roodie.model.state.EntityMapper;
import com.roodie.model.util.MoviesCollections;
import com.roodie.model.util.Tmdb;
import com.uwetrottmann.tmdb.entities.ContentRating;
import com.uwetrottmann.tmdb.entities.TvContentRatings;
import com.uwetrottmann.tmdb.entities.TvShowComplete;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.roodie.model.util.TimeUtils.isPastStartingPoint;


/**
 * Created by Roodie on 13.08.2015.
 */

public class ShowWrapper extends Watchable {

    private static final long serialVersionUID = 6765523899215923228L;

    private static final Calendar CALENDAR = Calendar.getInstance();

    public String originalTitle;

    List<String> originCountries;

    public String originalLanguage;

    public Date tmdbLastAirDate;

    public long tmdbLastAirTime;

    public String networks;

    public float popularity;

    public int status;

    public String type;

    public int amountOfEpisodes;

    public int amountOfSeasons;

    public String contentRating;

    Map<String, SeasonWrapper> seasons;


    public static class Status {
        public static final int CONTINUING = 1;
        public static final int ENDED = -1;
        public static final int UNKNOWN = 0;
    }

    public ShowWrapper(String parentId) {
        super(parentId);
    }

    @Override
    public WatchableType getWatchableType() {
        return WatchableType.TV_SHOW;
    }

    public void setFromShow(TvShowComplete show) {
        Preconditions.checkNotNull(show, "Show cannot be null");

        tmdbId = show.id;

        if (!TextUtils.isEmpty(show.name)) {
            tmdbTitle = show.name;
        }

        if (!TextUtils.isEmpty(show.original_name)) {
            originalTitle = show.original_name;
        }

        if (!TextUtils.isEmpty(show.overview)) {
            tmdbOverview = show.overview;
        }

        if (show.first_air_date != null) {
            tmdbFirstAirDate = show.first_air_date;
            tmdbFirstReleasedTime = unbox(tmdbFirstReleasedTime, show.first_air_date);
        }

        if (tmdbYear == 0 && tmdbFirstReleasedTime != 0) {
            CALENDAR.setTimeInMillis(tmdbFirstReleasedTime);
            tmdbYear = CALENDAR.get(Calendar.YEAR);
        }

        if (show.last_air_date != null) {
            tmdbLastAirDate = show.last_air_date;
            tmdbLastAirTime = unbox(tmdbLastAirTime, show.last_air_date);
        }

        if (!TextUtils.isEmpty(show.poster_path)) {
            tmdbPosterUrl = show.poster_path;
        }

        if (!TextUtils.isEmpty(show.backdrop_path)) {
            tmdbBackdropUrl = show.backdrop_path;
        }

        if ( show.vote_average != null) {
              tmdbRatingVotesAverage =  show.vote_average;
        }

        tmdbRatingPercent = unbox(tmdbRatingPercent, show.vote_average);
        tmdbRatingVotesAmount = unbox(tmdbRatingVotesAmount, show.vote_count);

        popularity = roundPopularity(show.popularity, 1);

        if (show.number_of_seasons != null) {
            amountOfSeasons = show.number_of_seasons;
        }

        seasons = new LinkedHashMap<>(amountOfSeasons);

        if (show.number_of_episodes != null) {
            amountOfEpisodes = show.number_of_episodes;
        }

        if (show.episode_run_time != null) {
             tmdbRuntime = getAverageRuntime(show.episode_run_time);
        }

        if (show.genres != null) {
            tmdbGenres = getGenresString(show.genres);
            mGenres = EntityMapper.mapGenres(show.genres);

        }

      if (show.networks != null) {
          networks = getNetworksString(show.networks);
      }

        if (!TextUtils.isEmpty(show.status)) {
            status = encodeShowStatus(show.status);
        }

        if (!TextUtils.isEmpty(show.type)) {
            type = show.type;
        }

      if (!TextUtils.isEmpty(show.original_language)) {
          originalLanguage = show.original_language;
      }

    }

    private static int getAverageRuntime(List<Integer> runTime) {
        if (runTime.size() > 0) {
            double sum = 0;
            for (int j = 0; j < runTime.size(); j++) {
                sum += runTime.get(j);
            }
            return (int) (sum/ runTime.size());
        }
        return 0;
    }

    public static float roundPopularity(double d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Double.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }

    public void updateContentRating(final TvContentRatings ratings, final String countryCode) {
        Preconditions.checkNotNull(ratings, "ratings cannot be null");

        if (!MoviesCollections.isEmpty(ratings.results)) {
            ContentRating cRating = null;

            for (ContentRating rating : ratings.results) {
                if (countryCode != null && countryCode.equalsIgnoreCase(rating.iso_3166_1)) {
                    cRating = rating;
                    break;
                }
            }

            if (cRating != null) {
                if (!TextUtils.isEmpty(cRating.rating)) {
                    contentRating = cRating.rating;
                }
            }
        }
    }

    public String getOriginalTitle() {
        return originalTitle;
    }


    public List<String> getOriginCountries() {
        return originCountries;
    }


    public Date getLastAirDate() {
        return tmdbLastAirDate;
    }

    public long getLastAirTime() {
        return tmdbLastAirTime;
    }

    public float getPopularity() {
        return popularity;
    }

    public int getAmountOfEpisodes() {
        return amountOfEpisodes;
    }

    public int getAmountOfSeasons() {
        return amountOfSeasons;
    }

    public String getOriginalLanguage() {
        return originalLanguage;
    }

    public String getContentRating() {
        return contentRating;
    }

    public void setContentRating(String contentRating) {
        this.contentRating = contentRating;
    }

    public int getStatus() {
       return status;
    }

    public String getNetworks() {
        return networks;
    }

    public void setSeasons(List<SeasonWrapper> seasons) {
        for (SeasonWrapper season : seasons) {
            if (season.getTmdbId() != null && season.getSeasonNumber() != null)
            this.seasons.put(String.valueOf(season.getSeasonNumber()), season);
        }
    }

    public void updateSeason(SeasonWrapper season) {
      if (season.getTmdbId() != null && season.getSeasonNumber() != null) {
          this.seasons.put(String.valueOf(season.getSeasonNumber()), season);
      }
    }

    public List<SeasonWrapper> getSeasons() {
        if (seasons == null)
            return null;
        return new ArrayList<>(seasons.values());
    }

    public SeasonWrapper getSeason(Integer position) {
        return (new ArrayList<>(seasons.values())).get(position);
    }

    public SeasonWrapper getSeason(String seasonNumber) {
        if (this.seasons.containsKey(seasonNumber)) {
            return this.seasons.get(seasonNumber);
        }
        return null;
    }

    public boolean needFullFetch() {
        return  MoviesCollections.isEmpty(cast)
                || MoviesCollections.isEmpty(crew)
                || MoviesCollections.isEmpty(seasons.values());
    }

    public boolean needFullFetchFromTmdb() {
        return (needFullFetch() || isPastStartingPoint(lastFullFetchFromTmdbCompleted,
                Constants.STALE_MOVIE_DETAIL_THRESHOLD)) &&
                isPastStartingPoint(lastFullFetchFromTmdbStarted,
                        Constants.FULL_MOVIE_DETAIL_ATTEMPT_THRESHOLD);
    }

    public static int encodeShowStatus(@Nullable String status) {
        if (status == null) {
            return Status.UNKNOWN;
        }
        switch (status) {
            case Tmdb.ShowStatusExport.CONTINUING:
                return Status.CONTINUING;
            case Tmdb.ShowStatusExport.ENDED:
                return Status.ENDED;
            default:
                return Status.UNKNOWN;
        }
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Show = ");
        sb.append("tmdbId = ").append(tmdbId);
        sb.append(", isWatched = ").append(isWatched());

        return sb.toString();
    }
}

