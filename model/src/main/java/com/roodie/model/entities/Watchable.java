package com.roodie.model.entities;

import android.text.TextUtils;

import com.google.common.base.Objects;
import com.roodie.model.util.IntUtils;
import com.roodie.model.util.MoviesCollections;
import com.uwetrottmann.tmdb.entities.Genre;
import com.uwetrottmann.tmdb.entities.Network;

import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Roodie on 07.07.2015.
 */

public abstract class Watchable extends Entity{

    private static final long serialVersionUID = 7112183194708331797L;

    private  Date mDate;

    public static final Comparator<Watchable> COMPARATOR__ITEM_DATE_ASC
            = new ItemReleaseDateComparator(true);

    public static final Comparator<Watchable> COMPARATOR__ITEM_DATE_DESC
            = new ItemReleaseDateComparator(false);

    private static class ItemReleaseDateComparator implements Comparator<Watchable> {

        private final boolean ascending;

        ItemReleaseDateComparator(boolean ascending) {
            this.ascending = ascending;
        }

        @Override
        public int compare(Watchable item1, Watchable item2) {

                final long time1 = item1.getReleasedTime();
                final long time2 = item2.getReleasedTime();
                if (time1 < time2) {
                    return ascending ? -1 : 1;
                } else if (time1 > time2) {
                    return ascending ? 1 : -1;
                }

            return 0;
        }
    }

    public Watchable(String parentId) {
        super(parentId);
        tmdbId = Integer.valueOf(parentId);
    }

    public Integer tmdbId;
    public String tmdbTitle;

    public String tmdbOverview;

    public String tmdbBackdropUrl;

    public String tmdbPosterUrl;

    public String tmdbGenres;

    public transient List<com.roodie.model.entities.Genre> mGenres;

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

    public abstract WatchableType getWatchableType();

    protected static boolean unbox(boolean currentValue, Boolean newValue) {
        return newValue != null ? newValue : currentValue;
    }

    protected static int unbox(int currentValue, Integer newValue) {
        return newValue != null ? newValue : currentValue;
    }

    protected static int unbox(int currentValue, Double newValue) {
        return newValue != null ? ((int) (newValue * 10)) : currentValue;
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

    public Double getVotesAverage() {
        return tmdbRatingVotesAverage;
    }

    public String getRatingVoteAverage() {
        return tmdbRatingVotesAverage == null || tmdbRatingVotesAverage == 0 ? "--"
                : String.format(Locale.getDefault(), "%.1f", tmdbRatingVotesAverage);
    }



    public int getRatingVotes() {
        return tmdbRatingVotesAmount;
    }

    public int getAverageRatingPercent() {
        if ( tmdbRatingPercent > 0) {
            return IntUtils.weightedAverage(
                    tmdbRatingPercent, tmdbRatingVotesAmount);
        } else {
            return tmdbRatingPercent;
        }
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

    public void setTmdbId(Integer id) {
        this.tmdbId = id;
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

    public String getGenresString() {
        return tmdbGenres;
    }

    public long getReleasedTime() {
        return tmdbFirstReleasedTime;
    }

    public void setReleasedTime(long tmdbFirstReleasedTime) {
        this.tmdbFirstReleasedTime = tmdbFirstReleasedTime;
    }

    public int getRuntime() {
        return tmdbRuntime;
    }

    public Date getReleaseDate() {
        return tmdbFirstAirDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.tmdbFirstAirDate = releaseDate;
    }

    public int getYear() {
        int year = 0;
        if (tmdbYear > 0) {
            year = tmdbYear;
        } else if (tmdbFirstReleasedTime > 0) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(tmdbFirstReleasedTime);
            year = cal.get(Calendar.YEAR);
        }
        return year;
    }

    public void setTmdbOverview(String tmdbOverview) {
        this.tmdbOverview = tmdbOverview;
    }

    public void setTmdbBackdropUrl(String tmdbBackdropUrl) {
        this.tmdbBackdropUrl = tmdbBackdropUrl;
    }

    public void setTmdbPosterUrl(String tmdbPosterUrl) {
        this.tmdbPosterUrl = tmdbPosterUrl;
    }

    public void setTmdbRatingVotesAmount(int tmdbRatingVotesAmount) {
        this.tmdbRatingVotesAmount = tmdbRatingVotesAmount;
    }

    public void setTmdbRatingVotesAverage(Double tmdbRatingVotesAverage) {
        this.tmdbRatingVotesAverage = tmdbRatingVotesAverage;
    }

    public static String getGenresString(List<Genre> list) {
        if (!MoviesCollections.isEmpty(list)) {
            StringBuilder sb = new StringBuilder();
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


    public List<com.roodie.model.entities.Genre> getGenres() {
        return mGenres;
    }

    public static String getNetworksString(List<Network> list) {
        if (!MoviesCollections.isEmpty(list)) {
            StringBuilder sb = new StringBuilder();
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Watchable that = (Watchable) o;

        if (tmdbId != null && that.tmdbId != null) {
            return tmdbId.equals(that.tmdbId);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(tmdbId);
    }


}
