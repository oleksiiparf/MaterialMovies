package com.roodie.model.tasks;

import com.roodie.model.state.ApplicationState;
import com.uwetrottmann.tmdb.entities.MovieResultsPage;

import retrofit.RetrofitError;

/**
 * Created by Roodie on 24.06.2015.
 */
public class FetchPopularMoviesRunnable extends BasePaginatedMovieRunnable {

    public FetchPopularMoviesRunnable(int callingId, int mPage) {
        super(callingId, mPage);
    }

    @Override
    public MovieResultsPage doBackgroundCall() throws RetrofitError {
        return getTmdbClient().moviesService().popular(getPage(),
                getCountryProvider().getTwoLetterLanguageCode());

    }

    @Override
    protected ApplicationState.MoviePaginatedResult getResultFromState() {
        return mState.getPopularMovies();
    }

    @Override
    protected void updateState(ApplicationState.MoviePaginatedResult result) {
        mState.setPopularMovies(getCallingId(), result);
    }
}
