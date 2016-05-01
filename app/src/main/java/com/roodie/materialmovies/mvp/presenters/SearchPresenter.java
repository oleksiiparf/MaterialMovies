package com.roodie.materialmovies.mvp.presenters;


import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.roodie.materialmovies.MMoviesApp;
import com.roodie.materialmovies.R;
import com.roodie.materialmovies.mvp.views.SearchView;
import com.roodie.materialmovies.mvp.views.UiView;
import com.roodie.model.state.BaseState;
import com.roodie.model.state.MoviesState;
import com.roodie.model.tasks.BaseRunnable;
import com.roodie.model.tasks.FetchSearchMovieRunnable;
import com.roodie.model.tasks.FetchSearchPeopleRunnable;
import com.roodie.model.tasks.FetchSearchShowRunnable;
import com.squareup.otto.Subscribe;

/**
 * Created by Roodie on 20.08.2015.
 */


@InjectViewState
public class SearchPresenter extends MvpPresenter<SearchView> implements BaseListPresenter<SearchView> {

    private static final String LOG_TAG = SearchPresenter.class.getSimpleName();

    public SearchPresenter() {
        super();
        MMoviesApp.get().getState().registerForEvents(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MMoviesApp.get().getState().unregisterForEvents(this);
    }

    public void search(SearchView view, UiView.MMoviesQueryType queryType, String query) {
        final int callingId = getId(view);
        switch (queryType) {
            case SEARCH:
                fetchSearchResults(callingId, query);
                break;
        }
    }

    public void clearSearch(SearchView view) {
        final int callingId = getId(view);
        MMoviesApp.get().getState().setSearchResult(callingId, null);
    }

    @Override
    public String getUiTitle(UiView.MMoviesQueryType queryType) {
        switch (queryType) {
            case SEARCH:
                MoviesState.SearchResult result = MMoviesApp.get().getState().getSearchResult();
                if (result != null) {
                    return result.query;
                } else {
                    return MMoviesApp.get().getStringFetcher().getString(R.string.search_title);
                }
        }
        return null;
    }

    @Override
    public void onUiAttached(SearchView view, UiView.MMoviesQueryType queryType, String parameter) {
        populateUi(view, queryType);
    }

    @Override
    public void populateUi(SearchView view, UiView.MMoviesQueryType queryType) {
        view.setData(MMoviesApp.get().getState().getSearchResult());
    }

    @Override
    public boolean canFetchNextPage(BaseState.PaginatedResult<?> paginatedResult) {
        return false;
    }

    @Override
    public void refresh(SearchView view, UiView.MMoviesQueryType queryType) {
        //NTD
    }

    @Override
    public void onScrolledToBottom(SearchView view, UiView.MMoviesQueryType queryType) {
        //NTD
    }

    @Override
    public void populateUiFromEvent(BaseState.BaseEvent event, UiView.MMoviesQueryType queryType) {
        //NTD
    }

    @Override
    public SearchView findUi(int id) {
        return null;
    }

    @Override
    public int getId(SearchView view) {
        return view.hashCode();
    }

    @Override
    public <BR> void executeNetworkTask(BaseRunnable<BR> task) {
        MMoviesApp.get().inject(task);
        MMoviesApp.get().getBackgroundExecutor().execute(task);
    }

    @Subscribe
    public void onSearchResultChanged(MoviesState.SearchResultChangedEvent event) {
        populateUi(getViewState(), UiView.MMoviesQueryType.SEARCH);
    }

    @Subscribe
    public void onNetworkError(BaseState.OnErrorEvent event) {
        if (null != event.error) {
            getViewState().showError(event.error);
        }
    }

    @Subscribe
    public void onLoadingProgressVisibilityChanged(BaseState.ShowLoadingProgressEvent event) {
            if (!event.secondary) {
                getViewState().showLoadingProgress(event.show);
            }
    }

    private void fetchSearchResults(final int callingId, String query) {
        MMoviesApp.get().getState().setSearchResult(callingId, new MoviesState.SearchResult(query));
        fetchMovieSearchResults(callingId, query, TMDB_FIRST_PAGE);
        fetchPeopleSearchResults(callingId, query, TMDB_FIRST_PAGE);
        fetchShowsSearchResults(callingId, query, TMDB_FIRST_PAGE);
    }


    private void fetchMovieSearchResults(final int callingId, String query, int page) {
        executeNetworkTask(new FetchSearchMovieRunnable(callingId, query, page));
    }

    private void fetchPeopleSearchResults(final int callingId, String query, int page) {
        executeNetworkTask(new FetchSearchPeopleRunnable(callingId, query, page));
    }

    private void fetchShowsSearchResults(final int callingId, String query, int page) {
        executeNetworkTask(new FetchSearchShowRunnable(callingId, query, page));
    }


}
