package com.roodie.model.state;

import com.roodie.model.entities.ListItem;
import com.roodie.model.entities.TmdbConfiguration;
import com.google.common.base.Preconditions;
import com.uwetrottmann.tmdb.entities.Configuration;
import com.uwetrottmann.tmdb.entities.Movie;
import com.uwetrottmann.tmdb.entities.Person;

import java.util.List;
import java.util.Map;

/**
 * Created by Roodie on 25.06.2015.
 */
public interface MoviesState extends BaseState {

    public Map<String, Movie> getTmdbIdMovies();

    public Movie getMovie(String id);

    public Movie getMovie(int id);

    public void putMovie(Movie movie);

    public List<Movie> getTrending();

    public void setTrending(List<Movie> trending);

    public MoviePaginatedResult getPopular();

    public void setPopular(MoviePaginatedResult popular);

    public MoviePaginatedResult getNowPlaying();

    public void setNowPlaying(MoviePaginatedResult nowPlaying);

    public MoviePaginatedResult getUpcoming();

    public void setUpcoming(MoviePaginatedResult upcoming);

    public List<Movie> getWatchlist();

    public void setWatchlist(List<Movie> watchlist);

    public List<Movie> getRecommended();

    public void setRecommended(List<Movie> recommended);

    public Configuration getTmdbConfiguration();

    public void setTmdbConfiguration(TmdbConfiguration configuration);


    public Map<String, Person> getPeople();

    public Person getPerson(int id);

    public Person getPerson(String id);

    public static class LibraryChangedEvent {}

    public static class PopularChangedEvent {}

    public static class InTheatresChangedEvent {}

    public static class TrendingChangedEvent {}

    public static class WatchlistChangedEvent {}

    public static class SearchResultChangedEvent {}

    public static class UpcomingChangedEvent {}

    public static class RecommendedChangedEvent {}

    public static class TmdbConfigurationChangedEvent {}

    public static class WatchingMovieUpdatedEvent {}

    public static class MovieInformationUpdatedEvent extends BaseArgumentEvent<Movie> {
        public MovieInformationUpdatedEvent(int callingId, Movie item) {
            super(callingId, item);
        }
    }

    public static class MovieReleasesUpdatedEvent extends BaseArgumentEvent<Movie> {
        public MovieReleasesUpdatedEvent(int callingId, Movie item) {
            super(callingId, item);
        }
    }

    public static class MovieRelatedItemsUpdatedEvent extends BaseArgumentEvent<Movie> {
        public MovieRelatedItemsUpdatedEvent(int callingId, Movie item) {
            super(callingId, item);
        }
    }

    public static class MovieVideosItemsUpdatedEvent extends BaseArgumentEvent<Movie> {
        public MovieVideosItemsUpdatedEvent(int callingId, Movie item) {
            super(callingId, item);
        }
    }

    public static class MovieImagesUpdatedEvent extends BaseArgumentEvent<Movie> {
        public MovieImagesUpdatedEvent(int callingId, Movie item) {
            super(callingId, item);
        }
    }

    public static class MovieCastItemsUpdatedEvent extends BaseArgumentEvent<Movie> {
        public MovieCastItemsUpdatedEvent(int callingId, Movie item) {
            super(callingId, item);
        }
    }

    public static class MovieUserRatingChangedEvent extends BaseArgumentEvent<Movie> {
        public MovieUserRatingChangedEvent(int callingId, Movie item) {
            super(callingId, item);
        }
    }

    public static class PersonChangedEvent extends BaseArgumentEvent<Person> {
        public PersonChangedEvent(int callingId, Person item) {
            super(callingId, item);
        }
    }

    public static class MovieFlagsUpdatedEvent extends BaseArgumentEvent<List<Movie>> {
        public MovieFlagsUpdatedEvent(int callingId, List<Movie> item) {
            super(callingId, item);
        }
    }


    public class MoviePaginatedResult extends PaginatedResult<Movie> {
    }

    public class PersonPaginatedResult extends PaginatedResult<Person> {
    }

    public class SearchResult {
        public final String query;
        public MoviePaginatedResult movies;
        public PersonPaginatedResult people;

        public SearchResult(String query) {
            this.query = Preconditions.checkNotNull(query, "query cannot be null");
        }
    }


    public interface Filter<T> extends ListItem<T> {
        boolean isFiltered(T item);
        void sortListItems(List<ListItem<T>> items);
    }


    public static enum MovieFilter implements Filter<Movie> {

        /**
         * Filters {@link Movie} that have not been released yet.
         */
        NOT_RELEASED,

        /**
         * Filters {@link Movie} that have already been released.
         */
        RELEASED,

        /**
         * Filters {@link Movie} that are unreleased, and will be released in the far future.
         */
        UPCOMING,

        /**
         * Filters {@link Movie} that are unreleased, and will be released in the near future.
         */
        SOON,

        /**
         * Filters {@link Movie} which are highly rated, either by the user or the public.
         */
        HIGHLY_RATED;

        @Override
        public boolean isFiltered(Movie movie) {
            Preconditions.checkNotNull(movie, "movie cannot be null");

          /*  switch (this) {
                case NOT_RELEASED:
                    return isInFuture(movie.getReleasedTime());
                case UPCOMING:
                    return isAfterThreshold(movie.getReleasedTime(),
                            Constants.FUTURE_SOON_THRESHOLD);
                case SOON:
                    return isInFuture(movie.getReleasedTime()) && isBeforeThreshold(
                            movie.getReleasedTime(), Constants.FUTURE_SOON_THRESHOLD);
                case RELEASED:
                    return isInPast(movie.getReleasedTime());
                case HIGHLY_RATED:
                    return Math.max(movie.getTraktRatingPercent(), movie.getUserRating() * 10)
                            >= Constants.FILTER_HIGHLY_RATED;
            }
            */
            return false;
        }


        public void sortListItems(List<ListItem<Movie>> items) {
            switch (this) {
                default:
                    //   Collections.sort(items, Movie.COMPARATOR_LIST_ITEM_DATE_ASC);
                    break;
            }
        }

        @Override
        public int getListSectionTitle() {

            return 0;
        }

        @Override
        public Movie getListItem() {
            return null;
        }

        @Override
        public int getListType() {
            return ListItem.TYPE_SECTION;
        }
    }

}
