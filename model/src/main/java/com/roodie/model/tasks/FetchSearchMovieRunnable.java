package com.roodie.model.tasks;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.roodie.model.state.MoviesState;
import com.uwetrottmann.tmdb.entities.MovieResultsPage;

import retrofit.RetrofitError;

/**
 * Created by Roodie on 20.08.2015.
 */
public class FetchSearchMovieRunnable extends BasePaginatedMovieRunnable {
    private final String mQuery;

    public FetchSearchMovieRunnable(int callingId, String query, int page) {
        super(callingId, page);
        mQuery = Preconditions.checkNotNull(query, "query cannot be null");
    }

    @Override
    public MovieResultsPage doBackgroundCall() throws RetrofitError {
        return getTmdbClient().searchService().movie(
                mQuery,
                getPage(),
                getCountryProvider().getTwoLetterLanguageCode(),
                null,
                null,
                null,
                null);
    }

    @Override
    protected MoviesState.MoviePaginatedResult getResultFromState() {
        MoviesState.SearchResult searchResult = mState.getSearchResult();
        return searchResult != null ? searchResult.movies : null;
    }

    @Override
    protected void updateState(MoviesState.MoviePaginatedResult result) {
        MoviesState.SearchResult searchResult = mState.getSearchResult();
        if (searchResult != null && Objects.equal(mQuery, searchResult.query)) {
            searchResult.movies = result;
            mState.setSearchResult(searchResult);
        }
    }

    @Override
    protected MoviesState.MoviePaginatedResult createPaginatedResult() {
        return new MoviesState.MoviePaginatedResult();
    }
}
