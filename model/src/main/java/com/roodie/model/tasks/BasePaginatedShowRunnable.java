package com.roodie.model.tasks;

import com.roodie.model.entities.ShowWrapper;
import com.roodie.model.state.ApplicationState;
import com.uwetrottmann.tmdb.entities.TvResultsPage;
import com.uwetrottmann.tmdb.entities.TvShowComplete;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Roodie on 14.08.2015.
 */
abstract class BasePaginatedShowRunnable extends BasePaginatedRunnable <
        ApplicationState.ShowPaginatedResult, ShowWrapper, TvResultsPage> {

    public BasePaginatedShowRunnable(int callingId, int mPage) {
        super(callingId, mPage);
    }

    @Override
    protected void updatePaginatedResult(
            ApplicationState.ShowPaginatedResult result,
            TvResultsPage tmdbResult) {
        List<ShowWrapper> shows = new ArrayList<>(tmdbResult.results.size());
        for (TvShowComplete tvShow: tmdbResult.results) {
            shows.add(getEntityMapper().map(tvShow));
        }

        result.items.addAll(shows);

        result.page = tmdbResult.page;
        if (tmdbResult.total_pages != null) {
            result.totalPages = tmdbResult.total_pages;
        }
    }


    @Override
    protected ApplicationState.ShowPaginatedResult createPaginatedResult() {
        return new ApplicationState.ShowPaginatedResult();
    }
}
