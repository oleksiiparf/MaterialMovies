package com.roodie.model.state;

import com.roodie.model.entities.MovieWrapper;
import com.roodie.model.entities.ShowWrapper;

import java.util.Collection;


/**
 * Created by Roodie on 24.06.2015.
 */
public interface AsyncDatabaseHelper {

    public void putMovies(Collection<MovieWrapper> movies);

    public void putShows(Collection<ShowWrapper> shows);

    public void put(MovieWrapper movie);

    public void put(ShowWrapper show);

    public void deleteMovies(Collection<MovieWrapper> movies);

    public void deleteShows(Collection<ShowWrapper> shows);

    public void close();

    public void deleteAllMovies();

    public void deleteAllShows();

    public interface Callback<T> {
        public void onFinished(T result);
    }

}