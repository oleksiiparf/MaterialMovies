package com.roodie.materialmovies.mvp.presenters;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.roodie.materialmovies.MMoviesApp;
import com.roodie.materialmovies.R;
import com.roodie.materialmovies.mvp.views.ListTvShowsView;
import com.roodie.materialmovies.mvp.views.UiView;
import com.roodie.model.entities.ShowWrapper;
import com.roodie.model.state.BaseState;
import com.roodie.model.state.MoviesState;
import com.roodie.model.tasks.BaseRunnable;
import com.roodie.model.tasks.FetchSearchShowRunnable;
import com.roodie.model.util.FileLog;
import com.squareup.otto.Subscribe;

import java.util.List;

/**
 * Created by Roodie on 22.03.2016.
 */
@InjectViewState
public class ListTvShowsPresenter extends MvpPresenter<ListTvShowsView> implements BaseListPresenter<ListTvShowsView> {

    public ListTvShowsPresenter() {
        super();
        MMoviesApp.get().getState().registerForEvents(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MMoviesApp.get().getState().unregisterForEvents(this);
    }

    @Subscribe
    public void onSearchResultChanged(MoviesState.SearchResultChangedEvent event) {
        FileLog.d("lce", "Search results changed");
        populateUiFromEvent(event, UiView.MMoviesQueryType.SEARCH_PEOPLE);
    }

    public void search(ListTvShowsView view, UiView.MMoviesQueryType queryType, String query) {
        final int callingId = getId(view);
        switch (queryType) {
            case SEARCH_SHOWS:
                fetchTvShowsSearchResults(callingId, query);
                break;
        }
    }


    @Override
    public void onUiAttached(ListTvShowsView view, UiView.MMoviesQueryType queryType, String parameter) {
        switch (queryType) {
            case SEARCH_SHOWS:
                break;
        }

        populateUi(view, queryType);
    }

    @Override
    public String getUiTitle(UiView.MMoviesQueryType queryType) {
        switch (queryType) {
            case SEARCH_SHOWS:
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
    public void populateUi(ListTvShowsView view, UiView.MMoviesQueryType queryType) {

        List<ShowWrapper> items = null;
        switch (queryType) {
            case SEARCH_SHOWS:
                MoviesState.SearchResult searchResult = MMoviesApp.get().getState().getSearchResult();
                if (searchResult != null && searchResult.shows != null) {
                    items = searchResult.shows.items;
                }
                break;
        }
        view.setData(items);
    }

    @Override
    public boolean canFetchNextPage(BaseState.PaginatedResult<?> paginatedResult) {
        return paginatedResult != null && paginatedResult.page < paginatedResult.totalPages;
    }

    @Override
    public void refresh(ListTvShowsView view, UiView.MMoviesQueryType queryType) {
        //NTD
    }

    @Override
    public void onScrolledToBottom(ListTvShowsView view, UiView.MMoviesQueryType queryType) {
        MoviesState.SearchResult searchResult;
        switch (queryType) {
            case SEARCH_SHOWS:
                searchResult = MMoviesApp.get().getState().getSearchResult();
                if (searchResult != null && canFetchNextPage(searchResult.shows)) {
                    fetchTvShowsSearchResults(
                            getId(view),
                            searchResult.query,
                            searchResult.people.page + 1);
                }
                break;

        }
    }

    @Override
    public void populateUiFromEvent(BaseState.BaseEvent event, UiView.MMoviesQueryType queryType) {
        final ListTvShowsView ui = findUi(event.callingId);
        if (ui != null) {
            populateUi(ui, queryType);
        }
    }

    @Override
    public ListTvShowsView findUi(int id) {
        for (ListTvShowsView ui: getAttachedViews()) {
            if (getId(ui) == id) {
                return ui;
            }
        }
        return null;
    }

    @Override
    public int getId(ListTvShowsView view) {
        return view.hashCode();
    }

    @Override
    public <BR> void executeNetworkTask(BaseRunnable<BR> task) {
        MMoviesApp.get().inject(task);
        MMoviesApp.get().getBackgroundExecutor().execute(task);
    }

    private void fetchTvShowsSearchResults(final int callingId, String query) {
        MMoviesApp.get().getState().setSearchResult(callingId, new MoviesState.SearchResult(query));
        fetchTvShowsSearchResults(callingId, query, TMDB_FIRST_PAGE);
    }

    private void fetchTvShowsSearchResults(final int callingId, String query, int page) {
        executeNetworkTask(new FetchSearchShowRunnable(callingId, query, page));
    }
}
