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
import com.uwetrottmann.tmdb.entities.Releases;
import com.uwetrottmann.tmdb.entities.SpokenLanguage;
import com.uwetrottmann.tmdb.entities.Video;
import com.uwetrottmann.tmdb.entities.Videos;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.roodie.model.util.TimeUtils.isPastStartingPoint;

/**
 * Created by Roodie on 07.07.2015.
 */
public class MovieWrapper extends BasicWrapper<MovieWrapper> {

    public static final int NOT_SET = 0;
    public static final int TYPE_TMDB = 1;
    public static final int TYPE_IMDB = 2;

    private static final Calendar CALENDAR = Calendar.getInstance();

    Long _id;
    int idType;
    String imdbId;
    Integer tmdbId;

    String tmdbTitle;

    String tmdbSortTitle;

    String tmdbOverview;

    String tmdbTagline;

    String tmdbPosterUrl;

    String tmdbBackdropUrl;

    int tmdbYear;

    boolean tmdbIsAdult;

    int tmdbBudget;

    long tmdbReleasedTime;
    String tmdbReleasedCountryCode;

    int tmdbRatingPercent;
    int tmdbRatingVotes;

    int tmdbRuntime;

    String tmdbCertification;

    String tmdbGenres;

    String tmdbMainLanguage;

    transient long lastFullFetchFromTmdbStarted;
    transient long lastFullFetchFromTmdbCompleted;

    boolean loadedFromTmdb;

    transient List<MovieWrapper> related;
    transient List<MovieCreditWrapper> cast;
    transient List<MovieCreditWrapper> crew;
    transient List<TrailerWrapper> trailers;
    transient List<BackgroundImage> backgroundImages;

    public MovieWrapper() {
    }

    public void setFromMovie (Movie movie) {
        Preconditions.checkNotNull(movie, "Movie cannot be null");

        loadedFromTmdb = true;

        tmdbId = movie.id;

        if (!TextUtils.isEmpty(movie.imdb_id)) {
            imdbId = movie.imdb_id;
        }

        if (_id == null || idType == NOT_SET ) {
            if (!TextUtils.isEmpty(imdbId)) {
                _id = new Long(imdbId.hashCode());
                idType = TYPE_IMDB;
            } else if (tmdbId != null) {
                _id = new Long(tmdbId);
                idType = TYPE_TMDB;
            } else {
                idType = NOT_SET;
            }
        }

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
            tmdbReleasedTime = unbox(tmdbReleasedTime, movie.release_date);
        }

        if (tmdbYear == 0 && tmdbReleasedTime != 0) {
            CALENDAR.setTimeInMillis(tmdbReleasedTime);
            tmdbYear = CALENDAR.get(Calendar.YEAR);
        }

        tmdbIsAdult = unbox(tmdbIsAdult, movie.adult);

        tmdbBudget = unbox(tmdbBudget, movie.budget);

        tmdbRatingPercent = unbox(tmdbRatingPercent, movie.vote_average);
        tmdbRatingVotes = unbox(tmdbRatingVotes, movie.vote_count);

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


    public void updateVideos(final Videos videos) {
        Preconditions.checkNotNull(videos, "videos cannot be null");

        if (!MoviesCollections.isEmpty(videos.results)) {
            final ArrayList<TrailerWrapper> philmMovieVideos = new ArrayList<>();

            for (Video video : videos.results) {
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
                    tmdbReleasedTime = countryRelease.release_date.getTime();
                    tmdbReleasedCountryCode = countryRelease.iso_3166_1;

                    if (tmdbYear == 0 && tmdbReleasedTime != 0) {
                        CALENDAR.setTimeInMillis(tmdbReleasedTime);
                        tmdbYear = CALENDAR.get(Calendar.YEAR);
                    }
                }
            }
        }
    }

    private static String getGenresString(List<Genre> list) {
        if (!MoviesCollections.isEmpty(list)) {
            StringBuffer sb = new StringBuffer();
            for (int i = 0, z = list.size(); i < z; i++) {
                sb.append(list.get(i).name);
                if (i < z - 1) {
                    sb.append(", ");
                }
            }
            return sb.toString();
        }
        return null;
    }


    private static boolean unbox(boolean currentValue, Boolean newValue) {
        return newValue != null ? newValue : currentValue;
    }

    private static int unbox(int currentValue, Integer newValue) {
        return newValue != null ? newValue : currentValue;
    }

    private static int unbox(int currentValue, Double newValue) {
        return newValue != null ? ((int) (newValue * 10)) : currentValue;
    }

    private static long unbox(long currentValue, Date newValue) {
        return newValue != null ? newValue.getTime() : currentValue;
    }

    public Long getDBId() {
        return _id;
    }

    public int getIdType() {
        return idType;
    }

    public String getImdbId() {
        return imdbId;
    }

    public Integer getTmdbId() {
        return tmdbId;
    }

    public String getTmdbTitle() {
        return tmdbTitle;
    }


    public boolean hasPosterUrl() {
        return !TextUtils.isEmpty(tmdbPosterUrl);
    }

    public String getPosterUrl() {
        return tmdbPosterUrl;
    }

    public boolean hasBackdropUrl() {
        return !TextUtils.isEmpty(tmdbBackdropUrl);
    }

    public List<BackgroundImage> getBackgroundImages() {
        return backgroundImages;
    }

    public boolean isLoadedFromTmdb() {
        return loadedFromTmdb;
    }

    public void setBackgroundImages(List<BackgroundImage> backgroundImages) {
        this.backgroundImages = backgroundImages;
    }

    public int getYear() {
        return tmdbYear;
    }

    public List<TrailerWrapper> getTrailers() {
        return trailers;
    }

    public void setTrailers(List<TrailerWrapper> trailers) {
        this.trailers = trailers;
    }

    public List<MovieCreditWrapper> getCrew() {
        return crew;
    }

    public void setCrew(List<MovieCreditWrapper> crew) {
        this.crew = crew;
    }

    public List<MovieCreditWrapper> getCast() {
        return cast;
    }

    public void setCast(List<MovieCreditWrapper> cast) {
        this.cast = cast;
    }

    public long getTmdbReleasedTime() {
        return tmdbReleasedTime;
    }

    public void setTmdbReleasedTime(long tmdbReleasedTime) {
        this.tmdbReleasedTime = tmdbReleasedTime;
    }

    public String getOverview() {
        return tmdbOverview;
    }

    public String getTmdbBackdropUrl() {
        return tmdbBackdropUrl;
    }

    public List<MovieWrapper> getRelated() {
        return related;
    }

    public void setRelated(List<MovieWrapper> related) {
        this.related = related;
    }

    public int getAverageRatingPercent() {
        if ( tmdbRatingPercent > 0) {
            return IntUtils.weightedAverage(
                    tmdbRatingPercent, tmdbRatingVotes);
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

    public void markFullFetchStarted() {
         lastFullFetchFromTmdbStarted = System.currentTimeMillis();
    }

    public void markFullFetchCompleted() {
                lastFullFetchFromTmdbCompleted = System.currentTimeMillis();
        }

    @Override
    public String toString() {
        return new StringBuffer().append(tmdbId).append(" ").append(imdbId).append(" ").append(tmdbTitle+ "  ").toString();
    }

    public static class BackgroundImage {
        public final String url;
        public final int sourceType;

        public BackgroundImage(String url, int sourceType) {
            this.url = Preconditions.checkNotNull(url, "url cannot be null");
            this.sourceType = sourceType;
        }

        public BackgroundImage(Image image) {
            this.url = image.file_path;
            this.sourceType = MovieWrapper.TYPE_TMDB;
        }
    }

}
