package com.roodie.model.tasks;

import com.roodie.model.state.ApplicationState;
import com.uwetrottmann.tmdb.entities.TvResultsPage;

import retrofit.RetrofitError;

/**
 * Created by Roodie on 14.08.2015.
 */
public class FetchOnTheAirShowsRunnable extends BasePaginatedShowRunnable {

    public FetchOnTheAirShowsRunnable(int callingId, int mPage) {
        super(callingId, mPage);
    }

    @Override
    public TvResultsPage doBackgroundCall() throws RetrofitError {
        return getTmdbClient().tvService().onTheAir(getPage(),
                getCountryProvider().getTwoLetterLanguageCode());
    }

    @Override
    protected void updateState(ApplicationState.ShowPaginatedResult result) {
      mState.setOnTheAirShows(result);
    }

    @Override
    protected ApplicationState.ShowPaginatedResult getResultFromState() {
        return mState.getOnTheAirShows();
    }
}
