package com.roodie.model.tasks;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.roodie.model.state.MoviesState;
import com.uwetrottmann.tmdb.entities.PersonResultsPage;

import retrofit.RetrofitError;

/**
 * Created by Roodie on 20.08.2015.
 */
public class FetchSearchPeopleResult extends BasePaginatedPersonRunnable {

    private String mQuery;

    public FetchSearchPeopleResult(int callingId, String query, int page) {
        super(callingId, page);
        mQuery = Preconditions.checkNotNull(query, "query cannot be null");
    }

    @Override
    public PersonResultsPage doBackgroundCall() throws RetrofitError {
        return getTmdbClient().searchService().person(mQuery, getPage(), null, null);
    }

    @Override
    protected MoviesState.PersonPaginatedResult getResultFromState() {
        MoviesState.SearchResult searchResult = mState.getSearchResult();
        return searchResult != null ? searchResult.people : null;
    }

    @Override
    protected void updateState(MoviesState.PersonPaginatedResult result) {
        MoviesState.SearchResult searchResult = mState.getSearchResult();
        if (searchResult != null && Objects.equal(mQuery, searchResult.query)) {
            searchResult.people = result;
            mState.setSearchResult(searchResult);
        }
    }

    @Override
    protected MoviesState.PersonPaginatedResult createPaginatedResult() {
        return new MoviesState.PersonPaginatedResult();
    }

}
