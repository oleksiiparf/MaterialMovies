package com.roodie.model.state;

import com.roodie.model.entities.MovieWrapper;
import com.uwetrottmann.tmdb.entities.Movie;

import java.util.Collection;
import java.util.List;

/**
 * Created by Roodie on 24.06.2015.
 */
public interface DatabaseHelper {

    void put(MovieWrapper movie);

    void put(Collection<MovieWrapper> movies);

    List<MovieWrapper> getWatchList();

    void delete(Collection<MovieWrapper> movies);

    void deleteAllMovies();

    void close();

    boolean isClosed();
}
