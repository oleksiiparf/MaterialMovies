package com.roodie.model.entities;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.common.base.Preconditions;
import com.roodie.model.Constants;
import com.roodie.model.util.IntUtils;
import com.roodie.model.util.MoviesCollections;
import com.roodie.model.util.Tmdb;
import com.uwetrottmann.tmdb.entities.ContentRating;
import com.uwetrottmann.tmdb.entities.Genre;
import com.uwetrottmann.tmdb.entities.Network;
import com.uwetrottmann.tmdb.entities.TvContentRatings;
import com.uwetrottmann.tmdb.entities.TvShowComplete;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.roodie.model.util.TimeUtils.isPastStartingPoint;


/**
 * Created by Roodie on 13.08.2015.
 */
public class ShowWrapper extends BasicWrapper<ShowWrapper> implements Serializable {

    Long _id;
    Integer tmdbId;

    String originalTitle;
    String title;

    String overview;

    int runtime;

    List<String> originCountries;

    String originalLanguage;

    long firstAirDate;
    long lastAirDate;
    long lastAirTime;

    String backdropUrl;
    String posterUrl;

    String genres;
    String networks;

    int ratingPercent;
    int ratingVotes;
    Double ratingVotesAverage;

    float popularity;

    int status;
    String type;

    int amountOfEpisodes;
    int amountOfSeasons;

    String contentRating;

    boolean isLiked = false;

    transient long lastFullFetchFromTmdbStarted;
    transient long lastFullFetchFromTmdbCompleted;

    transient List<CreditWrapper> cast;
    transient List<CreditWrapper> crew;
    Map<String, SeasonWrapper> seasons;


    public static class Status {
        public static final int CONTINUING = 1;
        public static final int ENDED = 0;
        public static final int UNKNOWN = -1;
    }


    public ShowWrapper() {
    }

  public void setFromShow(TvShowComplete show) {
        Preconditions.checkNotNull(show, "Show cannot be null");

        tmdbId = show.id;

        if (_id == null) {
            if (tmdbId != null) {
                _id = new Long(tmdbId);
            }
        }

        if (!TextUtils.isEmpty(show.name)) {
            title = show.name;
        }

        if (!TextUtils.isEmpty(show.original_name)) {
            originalTitle = show.original_name;
        }

        if (!TextUtils.isEmpty(show.overview)) {
            overview = show.overview;
        }

        if (show.first_air_date != null) {
            firstAirDate = unbox(firstAirDate, show.first_air_date);
        }

        if (show.last_air_date != null) {
            lastAirDate = unbox(lastAirDate, show.last_air_date);
            lastAirTime = unbox(lastAirTime, show.last_air_date);
        }

        if (!TextUtils.isEmpty(show.poster_path)) {
            posterUrl = show.poster_path;
        }

        if (!TextUtils.isEmpty(show.backdrop_path)) {
            backdropUrl = show.backdrop_path;
        }

        if ( show.vote_average != null) {
              ratingVotesAverage =  show.vote_average;
        }

        ratingPercent = unbox(ratingPercent, show.vote_average);
        ratingVotes = unbox(ratingVotes, show.vote_count);

        popularity = roundPopularity(show.popularity, 1);

        if (show.number_of_seasons != null) {
            amountOfSeasons = show.number_of_seasons;
        }

        seasons = new HashMap<String, SeasonWrapper>(amountOfSeasons);

        if (show.number_of_episodes != null) {
            amountOfEpisodes = show.number_of_episodes;
        }

        if (show.episode_run_time != null) {
             runtime = getAverageRuntime(show.episode_run_time);
        }

        if (show.genres != null) {
            genres = getGenresString(show.genres);
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

    private static long unbox(long currentValue, Date newValue) {
        return newValue != null ? newValue.getTime() : currentValue;
    }

    private static int unbox(int currentValue, Double newValue) {
        return newValue != null ? ((int) (newValue * 10)) : currentValue;
    }

    private static int unbox(int currentValue, Integer newValue) {
        return newValue != null ? newValue : currentValue;
    }

    private static String getGenresString(List<Genre> list) {
        if (!MoviesCollections.isEmpty(list)) {
            StringBuffer sb = new StringBuffer();
            for (int i = 0, z = list.size(); i < z; i++) {
                sb.append(list.get(i).name);
                if (i < z - 1) {
                    sb.append(" | ");
                }
            }
            return sb.toString();
        }
        return null;
    }

    private static String getNetworksString(List<Network> list) {
        if (!MoviesCollections.isEmpty(list)) {
            StringBuffer sb = new StringBuffer();
            for (int i = 0, z = list.size(); i < z; i++) {
                sb.append(list.get(i).name);
                if (i < z - 1) {
                    sb.append(" | ");
                }
            }
            return sb.toString();
        }
        return null;
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

    public Long getDbId() {
        return _id;
    }

    public Integer getTmdbId() {
        return tmdbId;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public String getTitle() {
        return title;
    }

    public String getOverview() {
        return overview;
    }

    public List<String> getOriginCountries() {
        return originCountries;
    }

    public long getFirstAirDate() {
        return firstAirDate;
    }

    public long getLastAirDate() {
        return lastAirDate;
    }

    public long getLastAirTime() {
        return lastAirTime;
    }

    public String getBackdropUrl() {
        return backdropUrl;
    }

    public boolean hasBackdrodUrl () {
        return !TextUtils.isEmpty(backdropUrl);
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public boolean hasPosterUrl() {
        return !TextUtils.isEmpty(posterUrl);
    }


    public String getGenres() {
        return genres;
    }

    public int getRatingPercent() {
        return ratingPercent;
    }

    public int getRatingVotes() {
        return ratingVotes;
    }

    public String getRatingVoteAverage() {
        return ratingVotesAverage == null || ratingVotesAverage == 0 ? "--"
                : String.format(Locale.getDefault(), "%.1f", ratingVotesAverage);
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

    public List<CreditWrapper> getCast() {
        return cast;
    }

    public List<CreditWrapper> getCrew() {
        return crew;
    }

    public void setCast(List<CreditWrapper> cast) {
        this.cast = cast;
    }

    public void setCrew(List<CreditWrapper> crew) {
        this.crew = crew;
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
            System.out.println(season);
            if (season.getId() != null && season.getSeasonNumber() != null)
            this.seasons.put(String.valueOf(season.getSeasonNumber()), season);
        }

    }

    public void updateSeason(SeasonWrapper season) {
      if (season.getId() != null && season.getSeasonNumber() != null) {
          this.seasons.put(String.valueOf(season.getSeasonNumber()), season);
      }
    }

    public List<SeasonWrapper> getSeasons() {
        return new ArrayList<SeasonWrapper>(seasons.values());
    }

    public SeasonWrapper getSeason(Integer position) {
        return (new ArrayList<SeasonWrapper>(seasons.values())).get(position);
    }

    public SeasonWrapper getSeason(String seasonNumber) {
        if (this.seasons.containsKey(seasonNumber)) {
            return this.seasons.get(seasonNumber);
        }
        return null;
    }
    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean isLiked) {
        this.isLiked = isLiked;
    }

    public int getRuntime() {
        return runtime;
    }

    public int getAverageRatingPercent() {
        if ( ratingPercent > 0) {
            return IntUtils.weightedAverage(
                    ratingPercent, ratingVotes);
        } else {
            return ratingPercent;
        }
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

    public void markFullFetchStarted() {
        lastFullFetchFromTmdbStarted = System.currentTimeMillis();
    }

    public void markFullFetchCompleted() {
        lastFullFetchFromTmdbCompleted = System.currentTimeMillis();
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
        final StringBuffer sb = new StringBuffer("Show {");
        sb.append(", tmdbId=").append(tmdbId);
        sb.append(", originalTitle='").append(originalTitle).append('\'');
        sb.append(", status=").append(status);
        sb.append(", type='").append(type).append('\'');
        sb.append(", amountOfEpisodes=").append(amountOfEpisodes);
        sb.append(", amountOfSeasons=").append(amountOfSeasons);
        sb.append(", seasons=");
        for(SeasonWrapper seasonWrapper : new ArrayList<SeasonWrapper>(seasons.values())) {
            sb.append(seasonWrapper.toString());
        }
        sb.append('}');
        return sb.toString();
    }
}

