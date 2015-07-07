package com.roodie.model.state;

import com.uwetrottmann.tmdb.entities.Movie;

import java.util.Collection;
import java.util.List;

/**
 * Created by Roodie on 24.06.2015.
 */
public interface AsyncDatabaseHelper {

    public void mergeWatchlist(List<Movie> watchlist);

    public void getWatchlist(Callback<List<Movie>> callback);


    public void put(Collection<Movie> movies);

    public void put(Movie movie);

    public void delete(Collection<Movie> movies);

    public void close();

    public void deleteAllMovies();

    public interface Callback<T> {
        public void onFinished(T result);
    }

}