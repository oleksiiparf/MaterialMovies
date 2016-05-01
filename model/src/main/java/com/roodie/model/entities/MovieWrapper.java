package com.roodie.model.entities;

import android.text.TextUtils;

import com.google.common.base.Preconditions;
import com.roodie.model.Constants;
import com.roodie.model.util.CountryProvider;
import com.roodie.model.util.IntUtils;
import com.roodie.model.util.MoviesCollections;
import com.uwetrottmann.tmdb.entities.CountryRelease;
import com.uwetrottmann.tmdb.entities.Genre;
import com.uwetrottmann.tmdb.entities.Image;
import com.uwetrottmann.tmdb.entities.Movie;
import com.uwetrottmann.tmdb.entities.Network;
import com.uwetrottmann.tmdb.entities.Releases;
import com.uwetrottmann.tmdb.entities.SpokenLanguage;
import com.uwetrottmann.tmdb.entities.Videos;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static com.roodie.model.util.TimeUtils.isPastStartingPoint;

/**
 * Created by Roodie on 07.07.2015.
 */

public class MovieWrapper extends Watchable {

    private static final long serialVersionUID = 7371884463695497963L;

    private static final Calendar CALENDAR = Calendar.getInstance();

    public String tmdbTagline;

    public boolean tmdbIsAdult;

    public int tmdbBudget;

    public String tmdbReleasedCountryCode;

    public String tmdbCertification;

    boolean loadedFromTmdb;

    transient List<MovieWrapper> related;
    transient List<TrailerWrapper> trailers;
    transient List<BackdropImage> backdropImages;

    public MovieWrapper(String parentId) {
        super(parentId);
    }

    @Override
    public WatchableType getWatchableType() {
        return WatchableType.MOVIE;
    }

    public void setFromMovie (Movie movie) {
        Preconditions.checkNotNull(movie, "Movie cannot be null");

        loadedFromTmdb = true;

        tmdbId = movie.id;

        if (!TextUtils.isEmpty(movie.title)) {
            tmdbTitle = movie.title;
        }

        if (!TextUtils.isEmpty(movie.overview)) {
            tmdbOverview = movie.overview;
        }

        if (!TextUtils.isEmpty(movie.tagline)) {
            tmdbTagline = movie.tagline;
        }

        if (movie.release_date != null && tmdbReleasedCountryCode == null) {
            tmdbFirstAirDate = movie.release_date;
            tmdbFirstReleasedTime = unbox(tmdbFirstReleasedTime, movie.release_date);
        }

        if (tmdbYear == 0 && tmdbFirstReleasedTime != 0) {
            CALENDAR.setTimeInMillis(tmdbFirstReleasedTime);
            tmdbYear = CALENDAR.get(Calendar.YEAR);
        }

        tmdbIsAdult = unbox(tmdbIsAdult, movie.adult);

        tmdbBudget = unbox(tmdbBudget, movie.budget);

        tmdbRatingPercent = unbox(tmdbRatingPercent, movie.vote_average);
        tmdbRatingVotesAmount = unbox(tmdbRatingVotesAmount, movie.vote_count);

        if (movie.vote_average != null) {
            tmdbRatingVotesAverage = movie.vote_average;
        }

        if (!TextUtils.isEmpty(movie.backdrop_path)) {
            tmdbBackdropUrl = movie.backdrop_path;
        }
        if (!TextUtils.isEmpty(movie.poster_path)) {
            tmdbPosterUrl = movie.poster_path;
        }

        if (movie.genres != null) {
            tmdbGenres = getGenresString(movie.genres);
        }

        if (!MoviesCollections.isEmpty(movie.spoken_languages)) {
            SpokenLanguage mainLang = movie.spoken_languages.get(0);
            if (mainLang != null) {
                tmdbMainLanguage = mainLang.name;
            }
        }

        tmdbRuntime = unbox(tmdbRuntime, movie.runtime);

        if (movie.videos != null) {
            updateVideos(movie.videos);
        }
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

    public void updateVideos(final Videos videos) {
        Preconditions.checkNotNull(videos, "videos cannot be null");

        if (!MoviesCollections.isEmpty(videos.results)) {
            final ArrayList<TrailerWrapper> philmMovieVideos = new ArrayList<>();

            for (Videos.Video video : videos.results) {
                if (TrailerWrapper.isValid(video)) {
                    final TrailerWrapper philmMovieVideo = new TrailerWrapper();
                    philmMovieVideo.set(video);
                    philmMovieVideos.add(philmMovieVideo);
                }
            }

            setTrailers(philmMovieVideos);
        }
    }

    public void updateReleases(final Releases releases, final String countryCode) {
        Preconditions.checkNotNull(releases, "releases cannot be null");

        if (!MoviesCollections.isEmpty(releases.countries)) {
            CountryRelease countryRelease = null;
            CountryRelease usRelease = null;

            for (CountryRelease release : releases.countries) {
                if (countryCode != null && countryCode.equalsIgnoreCase(release.iso_3166_1)) {
                    countryRelease = release;
                    break;
                } else if (CountryProvider.US_TWO_LETTER_CODE
                        .equalsIgnoreCase(release.iso_3166_1)) {
                    usRelease = release;
                }
            }

            if (countryRelease == null) {
                countryRelease = usRelease;
            }

            if (countryRelease != null) {
                if (!TextUtils.isEmpty(countryRelease.certification)) {
                    tmdbCertification = countryRelease.certification;
                }
                if (countryRelease.release_date != null) {
                    tmdbFirstReleasedTime = countryRelease.release_date.getTime();
                    tmdbReleasedCountryCode = countryRelease.iso_3166_1;

                    if (tmdbYear == 0 && tmdbFirstReleasedTime != 0) {
                        CALENDAR.setTimeInMillis(tmdbFirstReleasedTime);
                        tmdbYear = CALENDAR.get(Calendar.YEAR);
                    }
                }
            }
        }
    }


    public List<BackdropImage> getBackdropImages() {
        return backdropImages;
    }

    public void setBackdropImages(List<BackdropImage> backdropImages) {
        this.backdropImages = backdropImages;
    }

    public void setTrailers(List<TrailerWrapper> trailers) {
        this.trailers = trailers;
    }

    public void setRelated(List<MovieWrapper> related) {
        this.related = related;
    }

    public boolean hasTrailers() {
        return trailers != null;
    }

    public List<TrailerWrapper> getTrailers() {
        return trailers;
    }

    public List<MovieWrapper> getRelated() {
        return related;
    }

    public String getCertification() {
        return tmdbCertification;
    }

    public String getReleasedCountryCode() {
        return tmdbReleasedCountryCode;
    }

    public int getBudget() {
        return tmdbBudget;
    }

    public boolean isTmdbIsAdult() {
        return tmdbIsAdult;
    }

    public int getYear() {
        return tmdbYear;
    }

    public String getTagline() {
        return tmdbTagline;
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

    private boolean needFullFetch() {
        return MoviesCollections.isEmpty(trailers)
                || MoviesCollections.isEmpty(cast)
                || MoviesCollections.isEmpty(crew)
                || MoviesCollections.isEmpty(related);
    }

    public boolean needFullFetchFromTmdb() {
        return (needFullFetch() || isPastStartingPoint(lastFullFetchFromTmdbCompleted,
                Constants.STALE_MOVIE_DETAIL_THRESHOLD)) &&
                isPastStartingPoint(lastFullFetchFromTmdbStarted,
                        Constants.FULL_MOVIE_DETAIL_ATTEMPT_THRESHOLD);
    }

    @Override
    public String toString() {
        return "Movie = " + tmdbId + ", isWatched = " + isWatched();
    }

    public static class BackdropImage {
        public final String url;

        public BackdropImage(String url) {
            this.url = Preconditions.checkNotNull(url, "url cannot be null");
        }

        public BackdropImage(Image image) {
            this.url = image.file_path;
        }
    }

}
