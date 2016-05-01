package com.roodie.materialmovies.mvp.presenters;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.roodie.materialmovies.MMoviesApp;
import com.roodie.materialmovies.R;
import com.roodie.materialmovies.mvp.views.ListPeopleView;
import com.roodie.materialmovies.mvp.views.UiView;
import com.roodie.model.entities.PersonWrapper;
import com.roodie.model.state.BaseState;
import com.roodie.model.state.MoviesState;
import com.roodie.model.tasks.BaseRunnable;
import com.roodie.model.tasks.FetchSearchPeopleRunnable;
import com.roodie.model.util.FileLog;
import com.squareup.otto.Subscribe;

import java.util.List;

/**
 * Created by Roodie on 22.03.2016.
 */
@InjectViewState
public class ListPeoplePresenter extends MvpPresenter<ListPeopleView> implements BaseListPresenter<ListPeopleView> {

    public ListPeoplePresenter() {
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
        FileLog.d("network results", "Search results changed");
        populateUiFromEvent(event, UiView.MMoviesQueryType.SEARCH_PEOPLE);
    }

    public void search(ListPeopleView view, UiView.MMoviesQueryType queryType, String query) {
        final int callingId = getId(view);
        switch (queryType) {
            case SEARCH:
                //fetchSearchResults(getId(ui), query);
                break;
            case SEARCH_MOVIES:
                //fetchMovieSearchResults(getId(ui), query);
                break;
            case SEARCH_PEOPLE:
                fetchPeopleSearchResults(callingId, query);
                break;
        }
    }


    @Override
    public void onUiAttached(ListPeopleView view, UiView.MMoviesQueryType queryType, String parameter) {
        switch (queryType) {
            case SEARCH_PEOPLE:
                break;
        }

        populateUi(view, queryType);
    }

    @Override
    public String getUiTitle(UiView.MMoviesQueryType queryType) {
        switch (queryType) {
            case SEARCH_PEOPLE:
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
    public void populateUi(ListPeopleView view, UiView.MMoviesQueryType queryType) {

        List<PersonWrapper> items = null;
        switch (queryType) {
            case SEARCH_PEOPLE:
                MoviesState.SearchResult searchResult = MMoviesApp.get().getState().getSearchResult();
                if (searchResult != null && searchResult.people != null) {
                    items = searchResult.people.items;
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
    public void refresh(ListPeopleView view, UiView.MMoviesQueryType queryType) {
        //NTD
    }

    @Override
    public void onScrolledToBottom(ListPeopleView view, UiView.MMoviesQueryType queryType) {
        MoviesState.SearchResult searchResult;
        switch (queryType) {
            case SEARCH_PEOPLE:
                searchResult = MMoviesApp.get().getState().getSearchResult();
                if (searchResult != null && canFetchNextPage(searchResult.people)) {
                    fetchPeopleSearchResults(
                            getId(view),
                            searchResult.query,
                            searchResult.people.page + 1);
                }
                break;

        }
    }

    @Override
    public void populateUiFromEvent(BaseState.BaseEvent event, UiView.MMoviesQueryType queryType) {
        final ListPeopleView ui = findUi(event.callingId);
        if (ui != null) {
            populateUi(ui, queryType);
        }
    }

    @Override
    public ListPeopleView findUi(int id) {
        for (ListPeopleView ui: getAttachedViews()) {
            if (getId(ui) == id) {
                return ui;
            }
        }
        return null;
    }

    @Override
    public int getId(ListPeopleView view) {
        return view.hashCode();
    }

    @Override
    public <BR> void executeNetworkTask(BaseRunnable<BR> task) {
        MMoviesApp.get().inject(task);
        MMoviesApp.get().getBackgroundExecutor().execute(task);
    }

    private void fetchPeopleSearchResults(final int callingId, String query) {
        MMoviesApp.get().getState().setSearchResult(callingId, new MoviesState.SearchResult(query));
        fetchPeopleSearchResults(callingId, query, TMDB_FIRST_PAGE);
    }

    private void fetchPeopleSearchResults(final int callingId, String query, int page) {
        executeNetworkTask(new FetchSearchPeopleRunnable(callingId, query, page));
    }
}
