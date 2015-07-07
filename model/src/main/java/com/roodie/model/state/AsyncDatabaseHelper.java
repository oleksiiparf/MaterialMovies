package com.roodie.model.state;

import com.roodie.model.entities.MovieWrapper;
import com.uwetrottmann.tmdb.entities.Movie;

import java.util.Collection;
import java.util.List;

/**
 * Created by Roodie on 24.06.2015.
 */
public interface AsyncDatabaseHelper {

    public void mergeWatchlist(List<MovieWrapper> watchlist);

    public void getWatchlist(Callback<List<MovieWrapper>> callback);


    public void put(Collection<MovieWrapper> movies);

    public void put(MovieWrapper movie);

    public void delete(Collection<MovieWrapper> movies);

    public void close();

    public void deleteAllMovies();

    public interface Callback<T> {
        public void onFinished(T result);
    }

}