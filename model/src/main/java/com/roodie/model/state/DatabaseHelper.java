package com.roodie.model.state;

import com.uwetrottmann.tmdb.entities.Movie;

import java.util.Collection;
import java.util.List;

/**
 * Created by Roodie on 24.06.2015.
 */
public interface DatabaseHelper {

    void put(Movie movie);

    void put(Collection<Movie> movies);

    List<Movie> getWatchList();

    void delete(Collection<Movie> movies);

    void deleteAllMovies();

    void close();

    boolean isClosed();
}
