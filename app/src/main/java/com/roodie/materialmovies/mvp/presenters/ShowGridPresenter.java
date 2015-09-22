package com.roodie.materialmovies.mvp.presenters;

import android.util.Log;

import com.google.common.base.Preconditions;
import com.roodie.materialmovies.R;
import com.roodie.materialmovies.mvp.views.BaseMovieListView;
import com.roodie.materialmovies.mvp.views.UiView;
import com.roodie.materialmovies.qualifiers.GeneralPurpose;
import com.roodie.model.entities.ShowWrapper;
import com.roodie.model.state.ApplicationState;
import com.roodie.model.state.BaseState;
import com.roodie.model.state.MoviesState;
import com.roodie.model.tasks.BaseMovieRunnable;
import com.roodie.model.tasks.FetchOnTheAirShowsRunnable;
import com.roodie.model.tasks.FetchPopularShowsRunnable;
import com.roodie.model.util.BackgroundExecutor;
import com.roodie.model.util.Injector;
import com.roodie.model.util.MoviesCollections;
import com.roodie.model.util.StringFetcher;
import com.squareup.otto.Subscribe;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by Roodie on 14.08.2015.
 */

@Singleton
public class ShowGridPresenter extends BaseListPresenter<ShowGridPresenter.ShowGridView> {

    private static final String LOG_TAG = ShowGridPresenter.class.getSimpleName();

    private final BackgroundExecutor mExecutor;
    private final ApplicationState mState;
    private final Injector mInjector;
    private final StringFetcher mStringFetcher;

    @Inject
    public ShowGridPresenter(ApplicationState moviesState,
                             @GeneralPurpose BackgroundExecutor executor,
                             Injector injector,
                             StringFetcher stringFetcher) {
        super();
        mState = Preconditions.checkNotNull(moviesState, "mState can not be null");
        mExecutor = Preconditions.checkNotNull(executor, "executor cannot be null");
        mInjector = Preconditions.checkNotNull(injector, "injector cannot be null");
        mStringFetcher = Preconditions.checkNotNull(stringFetcher, "stringFetcher cannot be null");


    }

    @Override
    public void initialize() {

    }

    @Override
    public void onResume() {
        mState.registerForEvents(this);
    }

    @Override
    public void onPause() {
        mState.unregisterForEvents(this);

    }

    @Override
    protected void onUiAttached(final ShowGridView ui) {
        final UiView.MovieQueryType queryType = ui.getQueryType();

        final int callingId = getId(ui);

        switch (queryType) {
            case POPULAR_SHOWS:
                fetchPopularIfNeeded(callingId);
                break;
            case ON_THE_AIR_SHOWS:
                fetchOnTheAirIfNeeded(callingId);
                break;
        }
    }

    @Subscribe
    public void onPopularChanged(MoviesState.PopularShowsChangedEvent event) {
        Log.d(LOG_TAG, "Popular changed");
        populateUiFromQueryType(UiView.MovieQueryType.POPULAR_SHOWS);
    }

    @Subscribe
    public void onTheAirChanged(MoviesState.OnTheAirShowsChangedEvent event) {
        Log.d(LOG_TAG, "On the air changed");
        populateUiFromQueryType(UiView.MovieQueryType.ON_THE_AIR_SHOWS);
    }

    @Subscribe
    public void onNetworkError(BaseState.OnErrorEvent event) {
        Log.d(LOG_TAG, "On Network error");
        ShowGridView ui = findUi(event.callingId);

        if (ui != null && null != event.error) {
            ui.showError(event.error);
        }
    }

    @Subscribe
    public void onLoadingProgressVisibilityChanged(BaseState.ShowLoadingProgressEvent event) {
        Log.d(LOG_TAG, "Loading progress visibility changed");
        ShowGridView ui = findUi(event.callingId);
        if (ui != null) {
            if (event.secondary) {
                ui.showSecondaryLoadingProgress(event.show);
            } else {
                ui.showLoadingProgress(event.show);
            }
        }
    }

    public void refresh(ShowGridView ui) {
        switch (ui.getQueryType()) {
            case POPULAR_SHOWS:
                Log.d(LOG_TAG, "Refresh popular.");
                fetchPopular(getId(ui));
                break;
            case ON_THE_AIR_SHOWS:
                Log.d(LOG_TAG, "Refresh upcoming.");
                fetchOnTheAir(getId(ui));
                break;
        }
    }

    @Override
    public void onScrolledToBottom(ShowGridView ui){
        ApplicationState.ShowPaginatedResult result;

        switch (ui.getQueryType()) {
            case POPULAR_SHOWS:
                result = mState.getPopularShows();
                Log.d(LOG_TAG, result.toString());
                if (canFetchNextPage(result)) {
                    Log.d(LOG_TAG,"Fetching page " + result.page + 1 );
                    fetchPopular(getId(ui), result.page + 1);
                }
                break;

            case ON_THE_AIR_SHOWS:
                result = mState.getOnTheAirShows();
                if (canFetchNextPage(result)) {
                    fetchOnTheAir(getId(ui), result.page + 1);
                }
                break;
        }
    }

    /**
     * Fetch popular shows task
     */
    private void fetchPopular(final int callingId, final int page) {
        executeTask(new FetchPopularShowsRunnable(callingId, page));
    }

    private void fetchPopular(final int callingId) {
        mState.setPopularShows(null);
        fetchPopular(callingId, TMDB_FIRST_PAGE);
    }

    private void fetchPopularIfNeeded(final int callingId) {
        ApplicationState.ShowPaginatedResult popular = mState.getPopularShows();
        if (popular == null || MoviesCollections.isEmpty(popular.items)) {
            fetchPopular(callingId, TMDB_FIRST_PAGE);
        }
    }

    /**
     * Fetch on the air shows task
     */
    private void fetchOnTheAir(final int callingId, final int page) {
        executeTask(new FetchOnTheAirShowsRunnable(callingId, page));
    }

    private void fetchOnTheAir(final int callingId) {
        mState.setOnTheAirShows(null);
        fetchOnTheAir(callingId, TMDB_FIRST_PAGE);
    }

    private void fetchOnTheAirIfNeeded(final int callingId) {
        ApplicationState.ShowPaginatedResult onTheAir = mState.getOnTheAirShows();
        if (onTheAir == null || MoviesCollections.isEmpty(onTheAir.items)) {
            fetchOnTheAir(callingId, TMDB_FIRST_PAGE);
        }
    }

    @Override
    protected void populateUi(ShowGridView ui) {
        Log.d(LOG_TAG, "populateUi: " + ui.getClass().getSimpleName());
        final UiView.MovieQueryType queryType = ui.getQueryType();

        List<ShowWrapper> items = null;
        switch (queryType) {
            case POPULAR_SHOWS:
                ApplicationState.ShowPaginatedResult popular = mState.getPopularShows();
                if (popular != null) {
                    items = popular.items;
                }
                break;
            case ON_THE_AIR_SHOWS:
                ApplicationState.ShowPaginatedResult onTheAir = mState.getOnTheAirShows();
                if (onTheAir != null) {
                    items = onTheAir.items;
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
    public String getUiTitle(ShowGridView ui) {
        switch (ui.getQueryType()) {
            case POPULAR_SHOWS:
                return mStringFetcher.getString(R.string.popular_title);
            case ON_THE_AIR_SHOWS:
                return mStringFetcher.getString(R.string.on_the_air_title);
        }
        return null;
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
        for (ShowGridView ui : getmUnmodifiableUis()){
            sb.append("view : " + getId(ui) + ",");
        }

        return sb.toString();
    }


    public interface ShowGridView extends BaseMovieListView<ShowWrapper> {

        void showTvShowDialog(ShowWrapper tvShow);
        void showTvDetail(ShowWrapper tvShow);

    }
}
