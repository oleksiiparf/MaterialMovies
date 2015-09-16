package com.roodie.model.entities;

import android.text.TextUtils;

import com.google.common.base.Preconditions;
import com.roodie.model.util.IntUtils;
import com.roodie.model.util.MoviesCollections;
import com.uwetrottmann.tmdb.entities.Genre;
import com.uwetrottmann.tmdb.entities.TvShowComplete;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

//import com.uwetrottmann.tmdb.entities.TvShowComplete;

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

    long firstAirDate;
    long lastAirDate;

    String backdropUrl;
    String posterUrl;

    String genres;

    int ratingPercent;
    int ratingVotes;
    float popularity;

    String status;
    String type;

    int amountOfEpisodes;
    int amountOfSeasons;

    boolean isLiked = false;

    transient long lastFullFetchFromTmdbStarted;
    transient long lastFullFetchFromTmdbCompleted;

    transient List<CreditWrapper> cast;
    transient List<CreditWrapper> crew;
    transient List<SeasonWrapper> seasons;



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
        }

        if (!TextUtils.isEmpty(show.poster_path)) {
            posterUrl = show.poster_path;
        }

        if (!TextUtils.isEmpty(show.backdrop_path)) {
            backdropUrl = show.backdrop_path;
        }

        ratingPercent = unbox(ratingPercent, show.vote_average);
        ratingVotes = unbox(ratingVotes, show.vote_count);

        popularity = roundPopularity(show.popularity, 1);

        if (show.number_of_seasons != null) {
            amountOfSeasons = show.number_of_seasons;
        }

        if (show.number_of_episodes != null) {
            amountOfEpisodes = show.number_of_episodes;
        }

        if (show.episode_run_time != null) {
             runtime = getAverageRuntime(show.episode_run_time);
        }

        if (show.genres != null) {
            genres = getGenresString(show.genres);
        }

        if (!TextUtils.isEmpty(show.status)) {
            status = show.status;
        }

        if (!TextUtils.isEmpty(show.type)) {
            type = show.type;
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

    private static int getAverageRuntime(List<Integer> runTime) {
        double average = 0;
        if (runTime.size() > 0) {
            double sum = 0;
            for (int j = 0; j < runTime.size(); j++) {
                sum += runTime.get(j);
            }
            average = sum / runTime.size();

            return (int) (sum/ average);
        }
        return 0;

    }

    public static float roundPopularity(double d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Double.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
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

    public float getPopularity() {
        return popularity;
    }

    public int getAmountOfEpisodes() {
        return amountOfEpisodes;
    }

    public int getAmountOfSeasons() {
        return amountOfSeasons;
    }

    public List<CreditWrapper> getCast() {
        return cast;
    }

    public List<CreditWrapper> getCrew() {
        return crew;
    }

    public List<SeasonWrapper> getSeasons() {
        return seasons;
    }

    public void setCast(List<CreditWrapper> cast) {
        this.cast = cast;
    }

    public void setCrew(List<CreditWrapper> crew) {
        this.crew = crew;
    }

    public void setSeasons(List<SeasonWrapper> seasons) {
        this.seasons = seasons;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean isLiked) {
        this.isLiked = isLiked;
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
                || MoviesCollections.isEmpty(seasons);
    }

    public void markFullFetchStarted() {
        lastFullFetchFromTmdbStarted = System.currentTimeMillis();
    }

    public void markFullFetchCompleted() {
        lastFullFetchFromTmdbCompleted = System.currentTimeMillis();
    }


}

