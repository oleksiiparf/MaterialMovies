package com.roodie.model.tasks;

import com.roodie.model.state.ApplicationState;
import com.uwetrottmann.tmdb.entities.TvResultsPage;

import retrofit.RetrofitError;

/**
 * Created by Roodie on 14.08.2015.
 */
public class FetchPopularShowsRunnable extends BasePaginatedShowRunnable {

    public FetchPopularShowsRunnable(int callingId, int mPage) {
        super(callingId, mPage);
    }

    @Override
    public TvResultsPage doBackgroundCall() throws RetrofitError {
        return getTmdbClient().tvService().popular(getPage(),
                getCountryProvider().getTwoLetterLanguageCode());
    }

    @Override
    protected ApplicationState.ShowPaginatedResult getResultFromState() {
        return mState.getPopularShows();
    }

    @Override
    protected void updateState(ApplicationState.ShowPaginatedResult result) {
        mState.setPopularShows(getCallingId(), result);
    }
}
