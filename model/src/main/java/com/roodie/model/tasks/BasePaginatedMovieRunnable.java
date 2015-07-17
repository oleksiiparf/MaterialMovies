package com.roodie.model.tasks;

import com.roodie.model.entities.MovieWrapper;
import com.roodie.model.state.ApplicationState;
import com.uwetrottmann.tmdb.entities.Movie;
import com.uwetrottmann.tmdb.entities.MovieResultsPage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Roodie on 02.07.2015.
 */
abstract class BasePaginatedMovieRunnable extends BasePaginatedRunnable <
        ApplicationState.MoviePaginatedResult, MovieWrapper, MovieResultsPage> {

    public BasePaginatedMovieRunnable(int callingId, int mPage) {
        super(callingId, mPage);
    }

    @Override
    protected void updatePaginatedResult(
            ApplicationState.MoviePaginatedResult result,
            MovieResultsPage tmdbResult) {
        List<MovieWrapper> movies = new ArrayList<>(tmdbResult.results.size());
        for (Movie mMovie: tmdbResult.results) {
            movies.add(getEntityMapper().map(mMovie));
        }

        result.items.addAll(movies);

        result.page = tmdbResult.page;
        if (tmdbResult.total_pages != null) {
            result.totalPages = tmdbResult.total_pages;
        }

    }

    @Override
    protected ApplicationState.MoviePaginatedResult createPaginatedResult() {
        return new ApplicationState.MoviePaginatedResult();
    }
}
