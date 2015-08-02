package com.roodie.model.tasks;

import com.roodie.model.state.ApplicationState;
import com.uwetrottmann.tmdb.entities.MovieResultsPage;

import retrofit.RetrofitError;

/**
 * Created by Roodie on 02.08.2015.
 */
public class FetchUpcomingRunnable  extends BasePaginatedMovieRunnable {

    public FetchUpcomingRunnable(int callingId, int page) {
        super(callingId, page);
    }

    @Override
    public MovieResultsPage doBackgroundCall() throws RetrofitError {
        return getTmdbClient().moviesService().upcoming(getPage(),
                getCountryProvider().getTwoLetterLanguageCode());
    }

    @Override
    protected ApplicationState.MoviePaginatedResult getResultFromState() {
        return mState.getUpcoming();
    }

    @Override
    protected void updateState(ApplicationState.MoviePaginatedResult result) {
        mState.setUpcoming(result);
    }
}
