package com.roodie.materialmovies.mvp.presenters;

import android.util.Log;
import android.view.View;

import com.roodie.materialmovies.R;
import com.roodie.materialmovies.mvp.views.BaseMovieListView;
import com.roodie.materialmovies.mvp.views.UiView;
import com.roodie.materialmovies.qualifiers.GeneralPurpose;
import com.roodie.model.entities.MovieWrapper;
import com.roodie.model.state.ApplicationState;
import com.roodie.model.state.BaseState;
import com.roodie.model.state.MoviesState;
import com.roodie.model.tasks.FetchInTheatresRunnable;
import com.roodie.model.tasks.FetchPopularMoviesRunnable;
import com.roodie.model.tasks.FetchUpcomingMoviesRunnable;
import com.roodie.model.util.BackgroundExecutor;
import com.roodie.model.util.Injector;
import com.roodie.model.util.MoviesCollections;
import com.roodie.model.util.StringFetcher;
import com.squareup.otto.Subscribe;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;


/**
 * Created by Roodie on 10.07.2015.
 */


@Singleton
public class MovieGridPresenter extends BaseListPresenter<MovieGridPresenter.MovieGridView> {

    private static final String LOG_TAG = MovieGridPresenter.class.getSimpleName();

    @Inject
    public MovieGridPresenter(ApplicationState moviesState, @GeneralPurpose BackgroundExecutor executor, Injector injector, StringFetcher stringFetcher) {
        super(moviesState, executor, injector, stringFetcher);
    }

    @Override
    public void initialize() {

    }

    @Override
    protected void onUiAttached(final MovieGridView ui) {
        final UiView.MovieQueryType queryType = ui.getQueryType();

        final int callingId = getId(ui);

        switch (queryType) {
            case POPULAR_MOVIES:
                fetchPopularIfNeeded(callingId);
                break;
            case IN_THEATERS_MOVIES:
                fetchNowPlayingIfNeeded(callingId);
                break;
            case UPCOMING_MOVIES:
                fetchUpcomingIfNeeded(callingId);
                break;
        }

    }

    @Subscribe
    public void onPopularChanged(MoviesState.PopularMoviesChangedEvent event) {
        Log.d(LOG_TAG, "Popular changed");
        populateUiFromQueryType(UiView.MovieQueryType.POPULAR_MOVIES);
    }

    @Subscribe
    public void onInTheatresChanged(MoviesState.InTheatresMoviesChangedEvent event) {
        Log.d(LOG_TAG, "In Theatres changed");
        populateUiFromQueryType(UiView.MovieQueryType.IN_THEATERS_MOVIES);
    }

    @Subscribe
    public void onUpcomingChanged(MoviesState.UpcomingMoviesChangedEvent event) {
        Log.d(LOG_TAG, "Upcoming changed");
        populateUiFromQueryType(UiView.MovieQueryType.UPCOMING_MOVIES);
    }

    @Subscribe
    public void onNetworkError(BaseState.OnErrorEvent event) {
        Log.d(LOG_TAG, "On Network error");
        MovieGridView ui = findUi(event.callingId);

        if (ui != null && null != event.error) {
            ui.showError(event.error);
        }
    }

    @Subscribe
    public void onLoadingProgressVisibilityChanged(BaseState.ShowLoadingProgressEvent event) {
        Log.d(LOG_TAG, "Loading progress visibility changed");
        MovieGridView ui = findUi(event.callingId);
        if (ui != null) {
            if (event.secondary) {
                ui.showSecondaryLoadingProgress(event.show);
            } else {
                ui.showLoadingProgress(event.show);
            }
        }
    }

    public void refresh(MovieGridView ui) {
        switch (ui.getQueryType()) {
            case POPULAR_MOVIES:
                Log.d(LOG_TAG, "Refresh popular.");
                fetchPopular(getId(ui));
                break;
            case UPCOMING_MOVIES:
                Log.d(LOG_TAG, "Refresh upcoming.");
                fetchUpcoming(getId(ui));
                break;
            case IN_THEATERS_MOVIES:
                Log.d(LOG_TAG, "Refresh in theatres.");
                fetchNowPlaying(getId(ui));
                break;
        }
    }

    @Override
    public void onScrolledToBottom(MovieGridView ui){
        ApplicationState.MoviePaginatedResult result;

        switch (ui.getQueryType()) {
            case POPULAR_MOVIES:
                result = mState.getPopularMovies();
                Log.d(LOG_TAG, result.toString());
                if (canFetchNextPage(result)) {
                    Log.d(LOG_TAG, "Fetching page " + result.page + 1);
                    fetchPopular(getId(ui), result.page + 1);
                }
                break;

            case UPCOMING_MOVIES:
                result = mState.getUpcoming();
                if (canFetchNextPage(result)) {
                    fetchUpcoming(getId(ui), result.page + 1);
                }
                break;
        }
    }

    /**
     * Fetch popular movies task
     */
    private void fetchPopular(final int callingId, final int page) {
        executeTask(new FetchPopularMoviesRunnable(callingId, page));
    }

    private void fetchPopular(final int callingId) {
        mState.setPopularMovies(null);
        fetchPopular(callingId, TMDB_FIRST_PAGE);
    }

    private void fetchPopularIfNeeded(final int callingId) {
        ApplicationState.MoviePaginatedResult popular = mState.getPopularMovies();
        if (popular == null || MoviesCollections.isEmpty(popular.items)) {
            fetchPopular(callingId, TMDB_FIRST_PAGE);
        }
    }

    /**
     * Fetch now playing movies task
     */
    private void fetchNowPlayingIfNeeded(final int callingId) {
        ApplicationState.MoviePaginatedResult nowPlaying = mState.getNowPlaying();
        if (nowPlaying == null || MoviesCollections.isEmpty(nowPlaying.items)) {
            fetchNowPlaying(callingId, TMDB_FIRST_PAGE);
        }
    }

    private void fetchNowPlaying(final int callingId) {
        mState.setNowPlaying(null);
        fetchNowPlaying(callingId, TMDB_FIRST_PAGE);
    }

    private void fetchNowPlaying(final int callingId, final int page) {
        executeTask(new FetchInTheatresRunnable(callingId, page));
    }

    /**
     * Fetch upcoming movies task
     */
    private void fetchUpcomingIfNeeded(final int callingId) {
        Log.d(LOG_TAG, "Fetch upcoming if needed.");
        ApplicationState.MoviePaginatedResult upcoming = mState.getUpcoming();
        if (upcoming == null || MoviesCollections.isEmpty(upcoming.items)) {
            fetchUpcoming(callingId, TMDB_FIRST_PAGE);
        }
    }

    private void fetchUpcoming(final int callingId) {
        mState.setUpcoming(null);
        fetchUpcoming(callingId, TMDB_FIRST_PAGE);
    }

    private void fetchUpcoming(final int callingId, final int page) {
        executeTask(new FetchUpcomingMoviesRunnable(callingId, page));
    }


    @Override
    protected void populateUi(final MovieGridView ui){
        Log.d(LOG_TAG, "populateUi: " + ui.getClass().getSimpleName());
        final UiView.MovieQueryType queryType = ui.getQueryType();

        List<MovieWrapper> items = null;
        switch (queryType) {
            case POPULAR_MOVIES:
                ApplicationState.MoviePaginatedResult popular = mState.getPopularMovies();
                if (popular != null) {
                    items = popular.items;
                }
                break;
            case IN_THEATERS_MOVIES:
                ApplicationState.MoviePaginatedResult nowPlaying = mState.getNowPlaying();
                if (nowPlaying != null) {
                    items = nowPlaying.items;
                }
                break;
            case UPCOMING_MOVIES:
                ApplicationState.MoviePaginatedResult upcoming = mState.getUpcoming();
                if (upcoming != null) {
                    items = upcoming.items;
                }
                break;
        }

        if (items == null) {
            ui.setItems(null);
        } else  {
            ui.setItems(createListItemList(items));
        }
    }

    @Override
    public String getUiTitle(MovieGridView ui) {
        switch (ui.getQueryType()) {
            case POPULAR_MOVIES:
                return mStringFetcher.getString(R.string.popular_title);
            case IN_THEATERS_MOVIES:
                return mStringFetcher.getString(R.string.in_theatres_title);
            case UPCOMING_MOVIES:
                return mStringFetcher.getString(R.string.upcoming_title);
        }
        return null;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("{");
        sb.append("hashcode").append(hashCode());
        sb.append('}');
        for (MovieGridView ui : getmUnmodifiableUis()){
            sb.append("view : " + getId(ui) + ",");
        }

        return sb.toString();
    }

    public interface MovieGridView extends BaseMovieListView<MovieWrapper> {

        void showMovieDetail(MovieWrapper movie, View view);
    }
}
