package com.roodie.model.state;

import com.roodie.model.entities.MovieWrapper;
import com.roodie.model.entities.PersonWrapper;
import com.roodie.model.entities.TmdbConfiguration;

import java.util.Map;

/**
 * Created by Roodie on 25.06.2015.
 */
public interface MoviesState extends BaseState {

    public Map<String, MovieWrapper> getTmdbIdMovies();

    public Map<String, MovieWrapper> getImdbIdMovies();

    public MovieWrapper getMovie(String id);

    public MovieWrapper getMovie(int id);

    public void putMovie(MovieWrapper movie);

    public MoviePaginatedResult getPopular();

    public void setPopular(MoviePaginatedResult popular);

    public TmdbConfiguration getTmdbConfiguration();

    public void setTmdbConfiguration(TmdbConfiguration configuration);

    public Map<String, PersonWrapper> getPeople();

    public PersonWrapper getPerson(int id);

    public PersonWrapper getPerson(String id);

    public static class PopularChangedEvent {}

    public static class TmdbConfigurationChangedEvent {}

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



    public class MoviePaginatedResult extends PaginatedResult<MovieWrapper> {
    }

    public class PersonPaginatedResult extends PaginatedResult<PersonWrapper> {
    }

}
