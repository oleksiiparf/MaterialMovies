package com.roodie.model.state;

import com.google.common.base.Preconditions;
import com.roodie.model.entities.MovieWrapper;
import com.roodie.model.entities.PersonWrapper;
import com.roodie.model.entities.SeasonWrapper;
import com.roodie.model.entities.ShowWrapper;
import com.roodie.model.entities.TmdbConfiguration;
import com.roodie.model.entities.Watchable;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by Roodie on 25.06.2015.
 */
public interface MoviesState extends BaseState {

    Map<String, MovieWrapper> getTmdbIdMovies();

    Map<String, ShowWrapper> getTvShows();

    ShowWrapper getTvShow(String id);

    ShowWrapper getTvShow(int id);

    SeasonWrapper getTvSeason(String id);

    SeasonWrapper getTvSeason(int id);

    MovieWrapper getMovie(String id);

    MovieWrapper getMovie(int id);

    void putMovie(MovieWrapper movie);

    void putShow(ShowWrapper show);

    MoviePaginatedResult getPopularMovies();

    void setPopularMovies(int callingId, MoviePaginatedResult popular);

    MoviePaginatedResult getNowPlaying();

    void setNowPlaying(int callingId, MoviePaginatedResult nowPlaying);

    MoviePaginatedResult getUpcoming();

    void setUpcoming(int callingId, MoviePaginatedResult upcoming);

    ShowPaginatedResult getPopularShows();

    void setPopularShows(int callingId, ShowPaginatedResult popular);

    ShowPaginatedResult getOnTheAirShows();

    void setOnTheAirShows(int callingId, ShowPaginatedResult onTheAir);

    void setSearchResult(int callingId, SearchResult result);

    SearchResult getSearchResult();

    TmdbConfiguration getTmdbConfiguration();

    void setTmdbConfiguration(TmdbConfiguration configuration);

    Map<String, PersonWrapper> getPeople();

    PersonWrapper getPerson(int id);

    PersonWrapper getPerson(String id);

    List<Watchable> getWatched();

    class PopularMoviesChangedEvent extends BaseEvent {
        public PopularMoviesChangedEvent(int callingId) {
            super(callingId);
        }
    }

    class InTheatresMoviesChangedEvent extends BaseEvent {
        public InTheatresMoviesChangedEvent(int callingId) {
            super(callingId);
        }
    }

    class UpcomingMoviesChangedEvent extends BaseEvent {
        public UpcomingMoviesChangedEvent(int callingId) {
            super(callingId);
        }
    }

    class PopularShowsChangedEvent extends BaseEvent{
        public PopularShowsChangedEvent(int callingId) {
            super(callingId);
        }
    }

    class OnTheAirShowsChangedEvent extends BaseEvent{
        public OnTheAirShowsChangedEvent(int callingId) {
            super(callingId);
        }
    }

    class SearchResultChangedEvent extends BaseEvent{
        public SearchResultChangedEvent(int callingId) {
            super(callingId);
        }
    }

    class TmdbConfigurationChangedEvent{
    }

    class WatchedChangedEvent {
    }

    class WatchedClearedEvent{
    }

    class MovieInformationUpdatedEvent extends BaseArgumentEvent<MovieWrapper> {
        public MovieInformationUpdatedEvent(int callingId, MovieWrapper item) {
            super(callingId, item);
        }
    }

    class MovieReleasesUpdatedEvent extends BaseArgumentEvent<MovieWrapper> {
        public MovieReleasesUpdatedEvent(int callingId, MovieWrapper item) {
            super(callingId, item);
        }
    }

    class MovieRelatedItemsUpdatedEvent extends BaseArgumentEvent<MovieWrapper> {
        public MovieRelatedItemsUpdatedEvent(int callingId, MovieWrapper item) {
            super(callingId, item);
        }
    }

    class MovieVideosItemsUpdatedEvent extends BaseArgumentEvent<MovieWrapper> {
        public MovieVideosItemsUpdatedEvent(int callingId, MovieWrapper item) {
            super(callingId, item);
        }
    }

    class MovieImagesUpdatedEvent extends BaseArgumentEvent<MovieWrapper> {
        public MovieImagesUpdatedEvent(int callingId, MovieWrapper item) {
            super(callingId, item);
        }
    }

    class MovieCastItemsUpdatedEvent extends BaseArgumentEvent<MovieWrapper> {
        public MovieCastItemsUpdatedEvent(int callingId, MovieWrapper item) {
            super(callingId, item);
        }
    }

    class PersonChangedEvent extends BaseArgumentEvent<PersonWrapper> {
        public PersonChangedEvent(int callingId, PersonWrapper item) {
            super(callingId, item);
        }
    }

    class TvShowInformationUpdatedEvent extends BaseArgumentEvent<ShowWrapper> {
        public TvShowInformationUpdatedEvent(int callingId, ShowWrapper item) {
            super(callingId, item);
        }
    }

    class TvShowImagesUpdatedEvent extends BaseArgumentEvent<ShowWrapper> {
        public TvShowImagesUpdatedEvent(int callingId, ShowWrapper item) {
            super(callingId, item);
        }
    }

    class TvShowCastItemsUpdatedEvent extends BaseArgumentEvent<ShowWrapper> {
        public TvShowCastItemsUpdatedEvent(int callingId, ShowWrapper item) {
            super(callingId, item);
        }
    }

    class TvShowSeasonUpdatedEvent extends DoubleArgumentEvent<String, SeasonWrapper> {
        public TvShowSeasonUpdatedEvent(int callingId,String showId, SeasonWrapper item) {
            super(callingId, showId, item);
        }
    }

    class MovieFlagUpdateEvent extends BaseArgumentEvent<MovieWrapper> {
        public MovieFlagUpdateEvent(int callingId, MovieWrapper item) {
            super(callingId, item);
        }
    }

    class ShowFlagUpdateEvent extends BaseArgumentEvent<ShowWrapper> {
        public ShowFlagUpdateEvent(int callingId, ShowWrapper item) {
            super(callingId, item);
        }
    }

    class MoviePaginatedResult extends PaginatedResult<MovieWrapper> {
    }

    class PersonPaginatedResult extends PaginatedResult<PersonWrapper> {
    }

    class ShowPaginatedResult extends PaginatedResult<ShowWrapper> {

    }

    class SearchResult implements Serializable {
        public final String query;
        public MoviePaginatedResult movies;
        public PersonPaginatedResult people;
        public ShowPaginatedResult shows;

        public SearchResult(String query) {
            this.query = Preconditions.checkNotNull(query, "query cannot be null");
        }
    }




}
