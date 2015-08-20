package com.roodie.materialmovies.mvp.presenters;


import com.google.common.base.Preconditions;
import com.roodie.materialmovies.mvp.views.MovieView;
import com.roodie.materialmovies.mvp.views.UiView;
import com.roodie.materialmovies.qualifiers.GeneralPurpose;
import com.roodie.model.entities.ListItem;
import com.roodie.model.entities.MovieWrapper;
import com.roodie.model.entities.PersonWrapper;
import com.roodie.model.entities.ShowWrapper;
import com.roodie.model.state.ApplicationState;
import com.roodie.model.state.MoviesState;
import com.roodie.model.tasks.BaseMovieRunnable;
import com.roodie.model.tasks.FetchSearchMovieRunnable;
import com.roodie.model.tasks.FetchSearchPeopleResult;
import com.roodie.model.tasks.FetchSearchShowRunnable;
import com.roodie.model.util.BackgroundExecutor;
import com.roodie.model.util.Injector;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by Roodie on 20.08.2015.
 */
public class SearchPresenter extends BasePresenter {

    private SearchView mSearchView;

    protected static final int TMDB_FIRST_PAGE = 1;

    private static final String LOG_TAG = SearchPresenter.class.getSimpleName();

    private final BackgroundExecutor mExecutor;
    private final ApplicationState mState;
    private final Injector mInjector;

    private boolean attached = false;

    @Inject
    public SearchPresenter(@GeneralPurpose BackgroundExecutor mExecutor,
                           ApplicationState mState,
                           Injector mInjector) {
        this.mExecutor = Preconditions.checkNotNull(mExecutor, "mExecutor cannot be null");
        this.mState = Preconditions.checkNotNull(mState, "mState cannot be null");
        this.mInjector = Preconditions.checkNotNull(mInjector, "mInjector cannot be null");
    }

    @Subscribe
    public void onSearchResultChanged(MoviesState.SearchResultChangedEvent event) {
        populateUisFromQueryTypes(UiView.MovieQueryType.SEARCH,
                UiView.MovieQueryType.SEARCH_MOVIES,
                UiView.MovieQueryType.SEARCH_SHOWS,
                UiView.MovieQueryType.SEARCH_PEOPLE);
    }

    @Override
    public void initialize() {

    }

    public void attachView(SearchView view) {
        Preconditions.checkNotNull(view, "View cannot be null");
        this.mSearchView = view;
        attached = true;
    }

    @Override
    public void onResume() {
        mState.registerForEvents(this);
    }

    @Override
    public void onPause() {
        mState.unregisterForEvents(this);
    }

    protected int getId(SearchView view) {
        return view.hashCode();
    }

    public void search(String query) {
        switch (mSearchView.getQueryType()) {
            case SEARCH:
                fetchSearchResults(getId(mSearchView), query);
                break;
            case SEARCH_MOVIES:
                fetchMovieSearchResults(getId(mSearchView), query);
                break;
            case SEARCH_PEOPLE:
                fetchPeopleSearchResults(getId(mSearchView), query);
                break;
            case SEARCH_SHOWS:
                fetchShowsSearchResults(getId(mSearchView), query);
                break;
        }
    }

    private void fetchSearchResults(final int callingId, String query) {
        mState.setSearchResult(new MoviesState.SearchResult(query));
        fetchMovieSearchResults(callingId, query, TMDB_FIRST_PAGE);
        fetchPeopleSearchResults(callingId, query, TMDB_FIRST_PAGE);
        fetchShowsSearchResults(callingId, query, TMDB_FIRST_PAGE);
    }

    private void fetchMovieSearchResults(final int callingId, String query) {
        mState.setSearchResult(new MoviesState.SearchResult(query));
        fetchMovieSearchResults(callingId, query, TMDB_FIRST_PAGE);
    }

    private void fetchPeopleSearchResults(final int callingId, String query) {
        mState.setSearchResult(new MoviesState.SearchResult(query));
        fetchPeopleSearchResults(callingId, query, TMDB_FIRST_PAGE);
    }

    private void fetchShowsSearchResults(final int callingId, String query) {
        mState.setSearchResult(new MoviesState.SearchResult(query));
        fetchShowsSearchResults(callingId, query, TMDB_FIRST_PAGE);
    }

    private void fetchMovieSearchResults(final int callingId, String query, int page) {
        executeTask(new FetchSearchMovieRunnable(callingId, query, page));
    }

    private void fetchPeopleSearchResults(final int callingId, String query, int page) {
        executeTask(new FetchSearchPeopleResult(callingId, query, page));
    }

    private void fetchShowsSearchResults(final int callingId, String query, int page) {
        executeTask(new FetchSearchShowRunnable(callingId, query, page));
    }

    public void populateUisFromQueryTypes(UiView.MovieQueryType... queryTypes) {
        final List<UiView.MovieQueryType> list = Arrays.asList(queryTypes);

        for (UiView.MovieQueryType type : list) {
            if (mSearchView.getQueryType().equals(queryTypes)) {
                populateUiFromQueryType(type);
                break;
            }
        }
    }

    public void populateUiFromQueryType(UiView.MovieQueryType queryType) {

        if (mSearchView.getQueryType() == queryType) {
            switch (queryType) {
                case SEARCH:
                    populateSearchUi();
                    break;
                case SEARCH_MOVIES:
                    populateSearchMovieUi();
                    break;
                case SEARCH_PEOPLE:
                    populateSearchPersonUi();
                    break;
                case SEARCH_SHOWS:
                    populateSearchShowUi();
                    break;
            }
        }


    }

    private void populateSearchUi() {
        mSearchView.setSearchResult(mState.getSearchResult());
    }

    private void populateSearchMovieUi() {
        MoviesState.SearchResult result = mState.getSearchResult();
        mSearchView.updateDisplayTitle(result != null ? result.query : null);

        // Now carry on with list ui population
        List<MovieWrapper> items = null;

        ApplicationState.MoviePaginatedResult popular = mState.getPopularMovies();
        if (popular != null) {
            items = popular.items;
        }

        if (items == null) {
            mSearchView.setMovieItems(null);
        } else  {
            mSearchView.setMovieItems(createListItemList(items));
        }
    }

    private void populateSearchPersonUi() {
        ApplicationState.SearchResult searchResult = mState.getSearchResult();
        mSearchView.updateDisplayTitle(searchResult != null ? searchResult.query : null);

        // Now carry on with list ui population
        if (searchResult != null && searchResult.people != null) {
            mSearchView.setPersonItems(createListItemList(searchResult.people.items));
        }
    }

    private void populateSearchShowUi() {
        ApplicationState.SearchResult result = mState.getSearchResult();
        mSearchView.updateDisplayTitle(result != null ? result.query : null);

        if (result != null && result.shows != null) {
            mSearchView.setTvShowItems(createListItemList(result.shows.items));
        }

    }

    private <T extends ListItem<T>> List<ListItem<T>> createListItemList(final List<T> items) {
        Preconditions.checkNotNull(items, "items cannot be null");
        ArrayList<ListItem<T>> listItems = new ArrayList<>(items.size());
        for (ListItem<T> item : items) {
            listItems.add(item);
        }
        return listItems;
    }


    private void checkViewAlreadySetted() {
        Preconditions.checkState(attached = true, "View not attached");
    }


    private <R> void executeTask(BaseMovieRunnable<R> task) {
        mInjector.inject(task);
        mExecutor.execute(task);
    }

    public interface SearchView extends MovieView {

        void setSearchResult(MoviesState.SearchResult result);

        void setMovieItems(List<ListItem<MovieWrapper>> items);

        void setPersonItems(List<ListItem<PersonWrapper>> items);

        void setTvShowItems(List<ListItem<ShowWrapper>> items);

    }

}
