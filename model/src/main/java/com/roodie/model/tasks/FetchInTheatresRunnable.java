package com.roodie.model.tasks;

import com.roodie.model.state.ApplicationState;
import com.uwetrottmann.tmdb.entities.MovieResultsPage;

import retrofit.RetrofitError;

/**
 * Created by Roodie on 02.08.2015.
 */
public class FetchInTheatresRunnable extends BasePaginatedMovieRunnable {

    public FetchInTheatresRunnable(int callingId, int page) {
        super(callingId, page);
    }

    @Override
    public MovieResultsPage doBackgroundCall() throws RetrofitError {
        return getTmdbClient().moviesService().nowPlaying(
                getPage(),
                getCountryProvider().getTwoLetterLanguageCode());
    }

    @Override
    protected ApplicationState.MoviePaginatedResult getResultFromState() {
        return mState.getNowPlaying();
    }

    @Override
    protected void updateState(ApplicationState.MoviePaginatedResult result) {
        mState.setNowPlaying(result);
    }
}
