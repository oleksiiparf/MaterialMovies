package com.roodie.materialmovies.mvp.presenters;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.google.common.base.Preconditions;
import com.roodie.materialmovies.MMoviesApp;
import com.roodie.materialmovies.R;
import com.roodie.materialmovies.mvp.views.ListMoviesView;
import com.roodie.materialmovies.mvp.views.UiView;
import com.roodie.materialmovies.mvp.views.UiView.MMoviesQueryType;
import com.roodie.model.entities.MovieWrapper;
import com.roodie.model.state.ApplicationState;
import com.roodie.model.state.BaseState;
import com.roodie.model.state.MoviesState;
import com.roodie.model.tasks.BaseRunnable;
import com.roodie.model.tasks.FetchInTheatresRunnable;
import com.roodie.model.tasks.FetchPopularMoviesRunnable;
import com.roodie.model.tasks.FetchRelatedMoviesRunnable;
import com.roodie.model.tasks.FetchSearchMovieRunnable;
import com.roodie.model.tasks.FetchUpcomingMoviesRunnable;
import com.roodie.model.util.FileLog;
import com.roodie.model.util.MoviesCollections;
import com.squareup.otto.Subscribe;

import java.util.List;


/**
 * Created by Roodie on 10.07.2015.
 */


@InjectViewState
public class ListMoviesPresenter extends MvpPresenter<ListMoviesView> implements BaseListPresenter<ListMoviesView> {

    public static final String LOG_TAG = "ListMoviesPresenter";

    private String mRequestedParameter;

    public ListMoviesPresenter() {
        super();
        MMoviesApp.get().getState().registerForEvents(this);
    }

    @Override
    public boolean canFetchNextPage(BaseState.PaginatedResult<?> paginatedResult) {
        return paginatedResult != null && paginatedResult.page < paginatedResult.totalPages;
    }

    @Override
    public int getId(ListMoviesView view) {
        return view.hashCode();
    }

    @Override
    public <BR> void executeNetworkTask(BaseRunnable<BR> task) {
        MMoviesApp.get().inject(task);
        MMoviesApp.get().getBackgroundExecutor().execute(task);
    }

    @Override
    public void onUiAttached(ListMoviesView view, UiView.MMoviesQueryType queryType, String parameter) {
        FileLog.d("presenter", "Attach UI by query = " + queryType);

        String subtitle = null;
        final int callingId = getId(view);
         switch (queryType) {
            case WATCHED:
                break;
            case POPULAR_MOVIES:
                fetchPopularIfNeeded(callingId);
                break;
            case IN_THEATERS_MOVIES:
                fetchNowPlayingIfNeeded(callingId);
                break;
            case UPCOMING_MOVIES:
                fetchUpcomingIfNeeded(callingId);
                break;
            case RELATED_MOVIES:
                mRequestedParameter = parameter;
                fetchRelatedIfNeeded(callingId, mRequestedParameter);
                subtitle = MMoviesApp.get().getStringFetcher().getString(R.string.related_movies);
                view.updateDisplaySubtitle(subtitle);
                break;
            case SEARCH_MOVIES:
                subtitle = getUiTitle(queryType);
                view.updateDisplayTitle(subtitle);
                break;

        }

        populateUi(view, queryType);
    }

    @Override
    public String getUiTitle(MMoviesQueryType queryType) {
        switch (queryType) {
            case POPULAR_MOVIES:
                return MMoviesApp.get().getStringFetcher().getString(R.string.popular_title);
            case UPCOMING_MOVIES:
                return MMoviesApp.get().getStringFetcher().getString(R.string.upcoming_title);
            case IN_THEATERS_MOVIES:
                return MMoviesApp.get().getStringFetcher().getString(R.string.in_theatres_title);
            case RELATED_MOVIES: {
                final MovieWrapper movie = MMoviesApp.get().getState().getMovie(mRequestedParameter);
                if (movie != null) {
                    return movie.getTitle();
                }
            }
            case SEARCH_MOVIES: {
                final MoviesState.SearchResult result = MMoviesApp.get().getState().getSearchResult();
                if (result != null) {
                    return result.query;
                } else {
                    return MMoviesApp.get().getStringFetcher().getString(R.string.search_title);
                }
            }
        }
        return null;
    }

    @Override
    public String getUiSubtitle(MMoviesQueryType queryType) {
        switch (queryType) {
            case SEARCH_MOVIES:
                return MMoviesApp.get().getStringFetcher().getString(R.string.movies_title);
        }
        return null;
    }

    @Override
    public void refresh(ListMoviesView view, UiView.MMoviesQueryType queryType) {
        final int callingId = getId(view);
        switch (queryType) {
            case POPULAR_MOVIES:
                fetchPopular(callingId);
                break;
            case UPCOMING_MOVIES:
                fetchUpcoming(callingId);
                break;
            case IN_THEATERS_MOVIES:
                fetchNowPlaying(callingId);
                break;
        }
    }

    @Override
    public ListMoviesView findUi(final int id) {
        for (ListMoviesView ui: getAttachedViews()) {
            if (getId(ui) == id) {
                return ui;
            }
        }
        return null;
    }

    @Override
    public void onScrolledToBottom(ListMoviesView ui, UiView.MMoviesQueryType queryType){
        ApplicationState.MoviePaginatedResult result;
        MoviesState.SearchResult searchResult;

        final int callingId = getId(ui);

        switch (queryType) {
            case POPULAR_MOVIES:
                result = MMoviesApp.get().getState().getPopularMovies();
                if (canFetchNextPage(result)) {
                    fetchPopular(callingId, result.page + 1);
                }
                break;

            case UPCOMING_MOVIES:
                result = MMoviesApp.get().getState().getUpcoming();
                if (canFetchNextPage(result)) {
                    fetchUpcoming(callingId, result.page + 1);
                }
                break;

            case SEARCH_MOVIES:
                searchResult = MMoviesApp.get().getState().getSearchResult();
                if (searchResult != null && canFetchNextPage(searchResult.movies)) {
                    fetchMovieSearchResults(
                            getId(ui),
                            searchResult.query,
                            searchResult.movies.page + 1);
                }
                break;
        }
    }

    @Override
    public void populateUiFromEvent(MoviesState.BaseEvent event, MMoviesQueryType queryType) {
        Preconditions.checkNotNull(event, "event cannot be null");
        FileLog.d("mvp", "Populate Ui from event");

        final ListMoviesView ui = findUi(event.callingId);
        if (ui != null) {
             populateUi(ui, queryType);
        }
    }

    @Override
    public void populateUi(ListMoviesView ui, MMoviesQueryType queryType){
        FileLog.d("ult", "Populate UI by query = " + queryType);
        List<MovieWrapper> items = null;

        switch (queryType) {
            case POPULAR_MOVIES:
                ApplicationState.MoviePaginatedResult popular = MMoviesApp.get().getState().getPopularMovies();
                if (popular != null) {
                    items = popular.items;
                }
                break;
            case IN_THEATERS_MOVIES:
                ApplicationState.MoviePaginatedResult nowPlaying = MMoviesApp.get().getState().getNowPlaying();
                if (nowPlaying != null) {
                    items = nowPlaying.items;
                }
                break;
            case UPCOMING_MOVIES:
                ApplicationState.MoviePaginatedResult upcoming = MMoviesApp.get().getState().getUpcoming();
                if (upcoming != null) {
                    items = upcoming.items;
                }
                break;
            case RELATED_MOVIES:
                final MovieWrapper movie = MMoviesApp.get().getState().getMovie(mRequestedParameter);
                if (movie != null) {
                    items = movie.getRelated();
                    ui.updateDisplayTitle(getUiTitle(MMoviesQueryType.RELATED_MOVIES));
                }
                break;
            case SEARCH_MOVIES:
                MoviesState.SearchResult searchResult = MMoviesApp.get().getState().getSearchResult();
                if (searchResult != null && searchResult.movies != null) {
                    items = searchResult.movies.items;
                    ui.updateDisplaySubtitle(getUiSubtitle(MMoviesQueryType.SEARCH_MOVIES));
                }
                break;

        }

        if (items == null) {
            FileLog.d("lce", "GridMoviePresenter : updateShowWatched(null)");
            ui.setData(null);
        } else  {
            FileLog.d("lce", "GridMoviePresenter : updateShowWatched(items)");
            ui.setData(items);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mRequestedParameter = null;
        MMoviesApp.get().getState().unregisterForEvents(this);
    }

    @Subscribe
    public void onSearchResultChanged(MoviesState.SearchResultChangedEvent event) {
        populateUiFromEvent(event, UiView.MMoviesQueryType.SEARCH_MOVIES );
     }

    @Subscribe
    public void onPopularChanged(MoviesState.PopularMoviesChangedEvent event) {
        FileLog.d("lce", "Popular changed");
        populateUiFromEvent(event, UiView.MMoviesQueryType.POPULAR_MOVIES);
    }

    @Subscribe
    public void onInTheatresChanged(MoviesState.InTheatresMoviesChangedEvent event) {
        FileLog.d("lce", "In theatre changed");
        populateUiFromEvent(event, UiView.MMoviesQueryType.IN_THEATERS_MOVIES);
    }

    @Subscribe
    public void onUpcomingChanged(MoviesState.UpcomingMoviesChangedEvent event) {
        populateUiFromEvent(event, UiView.MMoviesQueryType.UPCOMING_MOVIES);
    }

    @Subscribe
    public void onRelatedChanged(MoviesState.MovieRelatedItemsUpdatedEvent event) {
        populateUiFromEvent(event, MMoviesQueryType.RELATED_MOVIES);
    }


    @Subscribe
    public void onNetworkError(BaseState.OnErrorEvent event) {
        FileLog.d("lce", "Network error");
        ListMoviesView ui = findUi(event.callingId);
        if (ui != null && event.error != null) {
            ui.showError(event.error);
        }
    }

    @Subscribe
    public void onLoadingProgressVisibilityChanged(BaseState.ShowLoadingProgressEvent event) {
        FileLog.d("lce", "Loading progress visibility changed");

        ListMoviesView ui = findUi(event.callingId);
        if (ui != null) {
            if (!event.secondary) {
                ui.showLoadingProgress(event.show);
            }
        }
    }

    public void search(ListMoviesView view, UiView.MMoviesQueryType queryType, String query) {
        final int callingId = getId(view);
        switch (queryType) {
            case SEARCH_MOVIES:
                fetchMovieSearchResults(callingId, query);
                break;
        }
    }

    /**
     * Fetch popular movies task
     */
    private void fetchPopular(final int callingId, final int page) {
        executeNetworkTask(new FetchPopularMoviesRunnable(callingId, page));
    }

    private void fetchPopular(final int callingId) {
        MMoviesApp.get().getState().setPopularMovies(callingId, null);
        fetchPopular(callingId, TMDB_FIRST_PAGE);
    }

    public void fetchPopularIfNeeded(final int callingId) {
        ApplicationState.MoviePaginatedResult popular = MMoviesApp.get().getState().getPopularMovies();
        if (popular == null || MoviesCollections.isEmpty(popular.items)) {
            fetchPopular(callingId, TMDB_FIRST_PAGE);
        }
    }

    /**
     * Fetch now playing movies task
     */
    private void fetchNowPlayingIfNeeded(final int callingId) {
        ApplicationState.MoviePaginatedResult nowPlaying = MMoviesApp.get().getState().getNowPlaying();
        if (nowPlaying == null || MoviesCollections.isEmpty(nowPlaying.items)) {
            fetchNowPlaying(callingId, TMDB_FIRST_PAGE);
        }
    }

    private void fetchNowPlaying(final int callingId) {
        MMoviesApp.get().getState().setNowPlaying(callingId, null);
        fetchNowPlaying(callingId, TMDB_FIRST_PAGE);
    }

    private void fetchNowPlaying(final int callingId, final int page) {
       executeNetworkTask(new FetchInTheatresRunnable(callingId, page));
    }

    /**
     * Fetch upcoming movies task
     */
    public void fetchUpcomingIfNeeded(final int callingId) {
        ApplicationState.MoviePaginatedResult upcoming = MMoviesApp.get().getState().getUpcoming();
        if (upcoming == null || MoviesCollections.isEmpty(upcoming.items)) {
            fetchUpcoming(callingId, TMDB_FIRST_PAGE);
        }
    }

    public void fetchUpcoming(final int callingId) {
        MMoviesApp.get().getState().setUpcoming(callingId, null);
        fetchUpcoming(callingId, TMDB_FIRST_PAGE);
    }

    public void fetchUpcoming(final int callingId, final int page) {
         executeNetworkTask(new FetchUpcomingMoviesRunnable(callingId, page));
    }

    /**
     * Fetch related movies task
     */
    private void fetchRelatedIfNeeded(final int callingId, String id) {
        Preconditions.checkNotNull(id, "id cannot be null");

        MovieWrapper movie = MMoviesApp.get().getState().getMovie(id);
        if (movie != null && MoviesCollections.isEmpty(movie.getRelated())) {
            fetchRelatedMovies(callingId, movie);
        }
    }

    private void fetchRelatedMovies(final int callingId, MovieWrapper movie) {
        if (movie.getTmdbId() != null) {
            executeNetworkTask(new FetchRelatedMoviesRunnable(callingId, movie.getTmdbId()));
        }
    }

    /**
     * Fetch searched movies task
     */
    private void fetchMovieSearchResults(final int callingId, String query) {
        MMoviesApp.get().getState().setSearchResult(callingId, new MoviesState.SearchResult(query));
        fetchMovieSearchResults(callingId, query, TMDB_FIRST_PAGE);
    }

    private void fetchMovieSearchResults(final int callingId, String query, int page) {
        executeNetworkTask(new FetchSearchMovieRunnable(callingId, query, page));
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ListMoviesPresenter{");
        sb.append("hashcode=").append(hashCode());
        sb.append('}');
        for (ListMoviesView ui : getAttachedViews()){
            sb.append("view : ").append(getId(ui)).append(",");
        }
        return sb.toString();
    }
}
