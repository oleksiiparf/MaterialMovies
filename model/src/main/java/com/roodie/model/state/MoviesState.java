package com.roodie.model.state;

import com.google.common.base.Preconditions;
import com.roodie.model.entities.MovieWrapper;
import com.roodie.model.entities.PersonWrapper;
import com.roodie.model.entities.SeasonWrapper;
import com.roodie.model.entities.ShowWrapper;
import com.roodie.model.entities.TmdbConfiguration;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by Roodie on 25.06.2015.
 */
public interface MoviesState extends BaseState {

    public Map<String, MovieWrapper> getTmdbIdMovies();

    public Map<String, MovieWrapper> getImdbIdMovies();

    public Map<String, ShowWrapper> getTmdbShows();

    public ShowWrapper getTvShow(String id);

    public ShowWrapper getTvShow(int id);

    public SeasonWrapper getTvSeason(String id);

    public SeasonWrapper getTvSeason(int id);

    public MovieWrapper getMovie(String id);

    public MovieWrapper getMovie(int id);

    public void putMovie(MovieWrapper movie);

    public MoviePaginatedResult getPopularMovies();

    public void setPopularMovies(MoviePaginatedResult popular);

    public MoviePaginatedResult getNowPlaying();

    public void setNowPlaying(MoviePaginatedResult nowPlaying);

    public MoviePaginatedResult getUpcoming();

    public void setUpcoming(MoviePaginatedResult upcoming);

    public ShowPaginatedResult getPopularShows();

    public void setPopularShows(ShowPaginatedResult popular);

    public ShowPaginatedResult getOnTheAirShows();

    public void setOnTheAirShows(ShowPaginatedResult onTheAir);

    public void setSearchResult(SearchResult result);

    public SearchResult getSearchResult();

    public TmdbConfiguration getTmdbConfiguration();

    public void setTmdbConfiguration(TmdbConfiguration configuration);

    public Map<String, PersonWrapper> getPeople();

    public PersonWrapper getPerson(int id);

    public PersonWrapper getPerson(String id);

    public static class PopularMoviesChangedEvent {}

    public static class InTheatresMoviesChangedEvent {}

    public static class UpcomingMoviesChangedEvent {}

    public static class PopularShowsChangedEvent {}

    public static class OnTheAirShowsChangedEvent {}

    public static class SearchResultChangedEvent {}

    public static class TmdbConfigurationChangedEvent {}

    public static class TvSeasonsChangeEvent {}

    public static class MovieInformationUpdatedEvent extends BaseArgumentEvent<MovieWrapper> {
        public MovieInformationUpdatedEvent(int callingId, MovieWrapper item) {
            super(callingId, item);
        }
    }

    public static class MovieReleasesUpdatedEvent extends BaseArgumentEvent<MovieWrapper> {
        public MovieReleasesUpdatedEvent(int callingId, MovieWrapper item) {
            super(callingId, item);
        }
    }

    public static class MovieRelatedItemsUpdatedEvent extends BaseArgumentEvent<MovieWrapper> {
        public MovieRelatedItemsUpdatedEvent(int callingId, MovieWrapper item) {
            super(callingId, item);
        }
    }

    public static class MovieVideosItemsUpdatedEvent extends BaseArgumentEvent<MovieWrapper> {
        public MovieVideosItemsUpdatedEvent(int callingId, MovieWrapper item) {
            super(callingId, item);
        }
    }

    public static class MovieImagesUpdatedEvent extends BaseArgumentEvent<MovieWrapper> {
        public MovieImagesUpdatedEvent(int callingId, MovieWrapper item) {
            super(callingId, item);
        }
    }

    public static class MovieCastItemsUpdatedEvent extends BaseArgumentEvent<MovieWrapper> {
        public MovieCastItemsUpdatedEvent(int callingId, MovieWrapper item) {
            super(callingId, item);
        }
    }

    public static class PersonChangedEvent extends BaseArgumentEvent<PersonWrapper> {
        public PersonChangedEvent(int callingId, PersonWrapper item) {
            super(callingId, item);
        }
    }

    public static class TvShowInformationUpdatedEvent extends BaseArgumentEvent<ShowWrapper> {
        public TvShowInformationUpdatedEvent(int callingId, ShowWrapper item) {
            super(callingId, item);
        }
    }

    public static class TvShowImagesUpdatedEvent extends BaseArgumentEvent<ShowWrapper> {
        public TvShowImagesUpdatedEvent(int callingId, ShowWrapper item) {
            super(callingId, item);
        }
    }

    public static class TvShowCastItemsUpdatedEvent extends BaseArgumentEvent<ShowWrapper> {
        public TvShowCastItemsUpdatedEvent(int callingId, ShowWrapper item) {
            super(callingId, item);
        }
    }

    public static class TvShowSeasonUpdatedEvent extends DoubleArgumentEvent<String, SeasonWrapper> {
        public TvShowSeasonUpdatedEvent(int callingId,String showId, SeasonWrapper item) {
            super(callingId, showId, item);
        }
    }





    public class MoviePaginatedResult extends PaginatedResult<MovieWrapper> {
    }

    public class PersonPaginatedResult extends PaginatedResult<PersonWrapper> {
    }

    public class ShowPaginatedResult extends PaginatedResult<ShowWrapper> {

    }

    public class SearchResult implements Serializable {
        public final String query;
        public MoviePaginatedResult movies;
        public PersonPaginatedResult people;
        public ShowPaginatedResult shows;

        public SearchResult(String query) {
            this.query = Preconditions.checkNotNull(query, "query cannot be null");
        }
    }




}
