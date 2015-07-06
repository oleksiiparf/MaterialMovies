package com.roodie.materialmovies.mvp.presenters;

import com.roodie.materialmovies.mvp.views.BaseMovieListView;
import com.roodie.model.entities.MMoviesTrailer;
import com.roodie.model.state.MoviesState;
import com.uwetrottmann.tmdb.entities.Movie;

import java.util.Set;

/**
 * Created by Roodie on 06.07.2015.
 */
public class MovieListPresenter extends BasePresenter {

    private MovieListView mMovieListView;

    public interface MovieListView extends BaseMovieListView<Movie> {

        void setFiltersVisibility(boolean visible);

        void showActiveFilters(Set<MoviesState.MovieFilter> filters);

        void playTrailer(MMoviesTrailer trailer);
    }
}
