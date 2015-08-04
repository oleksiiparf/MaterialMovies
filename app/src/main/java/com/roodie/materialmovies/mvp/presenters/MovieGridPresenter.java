package com.roodie.materialmovies.mvp.presenters;

import android.os.Bundle;
import android.util.Log;

import com.google.common.base.Preconditions;
import com.roodie.materialmovies.mvp.views.BaseMovieListView;
import com.roodie.materialmovies.mvp.views.UiView;
import com.roodie.materialmovies.qualifiers.GeneralPurpose;
import com.roodie.model.entities.ListItem;
import com.roodie.model.entities.MovieWrapper;
import com.roodie.model.state.ApplicationState;
import com.roodie.model.state.BaseState;
import com.roodie.model.tasks.BaseMovieRunnable;
import com.roodie.model.tasks.FetchInTheatresRunnable;
import com.roodie.model.tasks.FetchPopularRunnable;
import com.roodie.model.tasks.FetchUpcomingRunnable;
import com.roodie.model.util.BackgroundExecutor;
import com.roodie.model.util.Injector;
import com.roodie.model.util.MoviesCollections;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.inject.Inject;
import javax.inject.Singleton;


/**
 * Created by Roodie on 10.07.2015.
 */


@Singleton
public class MovieGridPresenter extends BasePresenter {

    private static final String LOG_TAG = MovieGridPresenter.class.getSimpleName();

    private final Set<MovieGridView> mUis;
    private final Set<MovieGridView> mUnmodifiableUis;

    private final BackgroundExecutor mExecutor;
    private final ApplicationState mState;
    private final Injector mInjector;

    private static final int TMDB_FIRST_PAGE = 1;

    @Inject
    public MovieGridPresenter(ApplicationState moviesState,
                              @GeneralPurpose BackgroundExecutor executor,
                              Injector injector) {
        mState = Preconditions.checkNotNull(moviesState, "mState can not be null");
        mExecutor = Preconditions.checkNotNull(executor, "executor cannot be null");
        mInjector = Preconditions.checkNotNull(injector, "injector cannot be null");
        mUis = new CopyOnWriteArraySet<>();
        mUnmodifiableUis = Collections.unmodifiableSet(mUis);
    }

    @Override
    public void initialize() {
    }

    private void onUiAttached(final MovieGridView ui) {
        final UiView.MovieQueryType queryType = ui.getQueryType();

        final int callingId = getId(ui);

        switch (queryType) {
            case POPULAR:
                fetchPopularIfNeeded(callingId);
                break;
            case IN_THEATERS:
                fetchNowPlayingIfNeeded(callingId);
                break;
            case UPCOMING:
                fetchUpcomingIfNeeded(callingId);
                break;
        }

    }

    @Override
    public void onResume() {
        mState.registerForEvents(this);
    }

    @Override
    public void onPause() {
        mState.unregisterForEvents(this);
    }

    public synchronized final void attachUi(MovieGridView view) {
        Preconditions.checkNotNull(view, "View cannot be null");
        Preconditions.checkState(!mUis.contains(view), "UI is already attached");
        mUis.add(view);
        onUiAttached(view);
        populateUi(view);
    }

    public synchronized final void detachUi(MovieGridView view) {
        Preconditions.checkArgument(view != null, "ui cannot be null");
        Preconditions.checkState(mUis.contains(view), "ui is not attached");
        mUis.remove(view);
    }

    protected final Set<MovieGridView> getUis() {
        return mUnmodifiableUis;
    }

    protected int getId(MovieGridView view) {
        return view.hashCode();
    }

    protected synchronized MovieGridView findUi(final int id) {
        for (MovieGridView ui : mUis) {
            if (getId(ui) == id) {
                return ui;
            }
        }
        return null;
    }

    private MovieGridView findUiFromQueryType(UiView.MovieQueryType queryType) {
        for (MovieGridView ui : getUis()) {
            if (ui.getQueryType() == queryType) {
                return ui;
            }
        }
        return null;
    }

    @Subscribe
    public void onPopularChanged(ApplicationState.PopularChangedEvent event) {
        Log.d(LOG_TAG, "Popular changed");
        populateUiFromQueryType(UiView.MovieQueryType.POPULAR);
    }

    @Subscribe
    public void onInTheatresChanged(ApplicationState.InTheatresChangedEvent event) {
        Log.d(LOG_TAG, "In Theatres changed");
        populateUiFromQueryType(UiView.MovieQueryType.IN_THEATERS);
    }

    @Subscribe
    public void onUpcomingChanged(ApplicationState.UpcomingChangedEvent event) {
        Log.d(LOG_TAG, "Upcoming changed");
        populateUiFromQueryType(UiView.MovieQueryType.UPCOMING);
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
            case POPULAR:
                fetchPopular(getId(ui));
                break;
            case UPCOMING:
                fetchUpcoming(getId(ui));
                break;
            case IN_THEATERS:
                fetchNowPlaying(getId(ui));
                break;
        }
    }

    public void onScrolledToBottom(MovieGridView ui){
        ApplicationState.MoviePaginatedResult result;

        switch (ui.getQueryType()) {
            case POPULAR:
                result = mState.getPopular();
                if (canFetchNextPage(result)) {
                    fetchPopular(getId(ui), result.page + 1);
                }
                break;

            case UPCOMING:
                result = mState.getUpcoming();
                if (canFetchNextPage(result)) {
                    fetchUpcoming(getId(ui), result.page + 1);
                }
                break;
        }
    }

    private boolean canFetchNextPage(ApplicationState.PaginatedResult<?> paginatedResult) {
        return paginatedResult != null && paginatedResult.page < paginatedResult.totalPages;
    }

    /**
     * Fetch popular movies task
     */
    private void fetchPopular(final int callingId, final int page) {
        executeTask(new FetchPopularRunnable(callingId, page));
    }

    private void fetchPopular(final int callingId) {
        mState.setPopular(null);
        fetchPopular(callingId, TMDB_FIRST_PAGE);
    }

    private void fetchPopularIfNeeded(final int callingId) {
        ApplicationState.MoviePaginatedResult popular = mState.getPopular();
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
        executeTask(new FetchUpcomingRunnable(callingId, page));
    }

    private final void populateUiFromQueryType(UiView.MovieQueryType queryType) {
        MovieGridView ui = findUiFromQueryType(queryType);
        if (ui != null) {
            populateUi(ui);
        }
    }

    private void populateUi(final MovieGridView ui){
            Log.d(LOG_TAG, "populateUi: " + ui.getClass().getSimpleName());
        final UiView.MovieQueryType queryType = ui.getQueryType();

        List<MovieWrapper> items = null;
        switch (queryType) {
            case POPULAR:
                ApplicationState.MoviePaginatedResult popular = mState.getPopular();
                if (popular != null) {
                    items = popular.items;
                }
                break;
            case IN_THEATERS:
                ApplicationState.MoviePaginatedResult nowPlaying = mState.getNowPlaying();
                if (nowPlaying != null) {
                    items = nowPlaying.items;
                }
                break;
            case UPCOMING:
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

    private <T extends ListItem<T>> List<ListItem<T>> createListItemList(final List<T> items) {
        Preconditions.checkNotNull(items, "items cannot be null");
        ArrayList<ListItem<T>> listItems = new ArrayList<>(items.size());
        for (ListItem<T> item : items) {
            listItems.add(item);
        }
        return listItems;
    }


    private <R> void executeTask(BaseMovieRunnable<R> task) {
        mInjector.inject(task);
        mExecutor.execute(task);
    }


    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("{");
        sb.append("hashcode").append(hashCode());
        sb.append('}');
        for (MovieGridView ui : mUnmodifiableUis){
            sb.append("view : " + getId(ui) + ",");
        }

        return sb.toString();
    }

    public interface MovieGridView extends BaseMovieListView<MovieWrapper> {

        void showMovieDetail(MovieWrapper movie, Bundle bundle);
    }
}
