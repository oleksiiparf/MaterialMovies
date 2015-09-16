package com.roodie.model.state;

import com.roodie.model.entities.MovieWrapper;
import com.roodie.model.entities.ShowWrapper;

import java.util.Collection;

/**
 * Created by Roodie on 24.06.2015.
 */
public interface DatabaseHelper {

    void put(MovieWrapper movie);

    void putMovies(Collection<MovieWrapper> movies);

    void put(ShowWrapper show);

    void putShows(Collection<ShowWrapper> shows);

    void deleteMovies(Collection<MovieWrapper> movies);

    void deleteShows(Collection<ShowWrapper> shows);

    void deleteAllMovies();

    void deleteAllShows();

    void close();

    boolean isClosed();
}
