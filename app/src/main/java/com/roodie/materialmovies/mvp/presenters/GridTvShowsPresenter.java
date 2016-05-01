package com.roodie.materialmovies.mvp.presenters;

import android.util.Log;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.google.common.base.Preconditions;
import com.roodie.materialmovies.MMoviesApp;
import com.roodie.materialmovies.R;
import com.roodie.materialmovies.mvp.views.TvShowsGridView;
import com.roodie.materialmovies.mvp.views.UiView;
import com.roodie.materialmovies.mvp.views.UiView.MMoviesQueryType;
import com.roodie.model.entities.ShowWrapper;
import com.roodie.model.state.ApplicationState;
import com.roodie.model.state.BaseState;
import com.roodie.model.state.MoviesState;
import com.roodie.model.tasks.BaseRunnable;
import com.roodie.model.tasks.FetchOnTheAirShowsRunnable;
import com.roodie.model.tasks.FetchPopularShowsRunnable;
import com.roodie.model.util.MoviesCollections;
import com.squareup.otto.Subscribe;

import java.util.List;

/**
 * Created by Roodie on 14.08.2015.
 */

@InjectViewState
public class GridTvShowsPresenter extends MvpPresenter<TvShowsGridView> implements BaseListPresenter<TvShowsGridView> {

    private static final String LOG_TAG = GridTvShowsPresenter.class.getSimpleName();


    public GridTvShowsPresenter() {
        super();

        MMoviesApp.get().getState().registerForEvents(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MMoviesApp.get().getState().unregisterForEvents(this);
    }

    @Override
    public void onUiAttached(TvShowsGridView view, UiView.MMoviesQueryType queryType, String parameter) {
        final int callingId = getId(view);
        switch (queryType) {
            case POPULAR_SHOWS:
                fetchPopularIfNeeded(callingId);
                break;
            case ON_THE_AIR_SHOWS:
                fetchOnTheAirIfNeeded(callingId);
                break;
        }
    }

    @Override
    public String getUiTitle(MMoviesQueryType queryType) {
        switch (queryType) {
            case POPULAR_SHOWS:
                return MMoviesApp.get().getStringFetcher().getString(R.string.popular_title);
            case ON_THE_AIR_SHOWS:
                return MMoviesApp.get().getStringFetcher().getString(R.string.on_the_air_title);
        }
        return null;
    }

    @Override
    public int getId(TvShowsGridView view) {
        return view.hashCode();
    }

    @Override
    public TvShowsGridView findUi(final int id) {
        for (TvShowsGridView ui: getAttachedViews()) {
            if (getId(ui) == id) {
                return ui;
            }
        }
        return null;
    }

    @Override
    public void refresh(TvShowsGridView view, UiView.MMoviesQueryType queryType) {
        final int callingId = getId(view);

        switch (queryType) {
            case POPULAR_SHOWS:
                fetchPopular(callingId);
                break;
            case ON_THE_AIR_SHOWS:
                fetchOnTheAir(callingId);
                break;
        }
    }

    @Override
    public void onScrolledToBottom(TvShowsGridView view, UiView.MMoviesQueryType queryType) {
        ApplicationState.ShowPaginatedResult result;
        final int callingId = getId(view);

        switch (queryType) {
            case POPULAR_SHOWS:
                result = MMoviesApp.get().getState().getPopularShows();
                Log.d(LOG_TAG, result.toString());
                if (canFetchNextPage(result)) {
                    fetchPopular(callingId, result.page + 1);
                }
                break;

            case ON_THE_AIR_SHOWS:
                result = MMoviesApp.get().getState().getOnTheAirShows();
                if (canFetchNextPage(result)) {
                    fetchOnTheAir(callingId, result.page + 1);
                }
                break;
        }
    }

    @Override
    public boolean canFetchNextPage(BaseState.PaginatedResult<?> paginatedResult) {
        return paginatedResult != null && paginatedResult.page < paginatedResult.totalPages;
    }

    @Override
    public <BR> void executeNetworkTask(BaseRunnable<BR> task) {
        MMoviesApp.get().inject(task);
        MMoviesApp.get().getBackgroundExecutor().execute(task);
    }

    @Subscribe
    public void onPopularChanged(MoviesState.PopularShowsChangedEvent event) {
        populateUiFromEvent(event, UiView.MMoviesQueryType.POPULAR_SHOWS);
    }

    @Subscribe
    public void onTheAirChanged(MoviesState.OnTheAirShowsChangedEvent event) {
        populateUiFromEvent(event, UiView.MMoviesQueryType.ON_THE_AIR_SHOWS);
    }

    @Subscribe
    public void onNetworkError(BaseState.OnErrorEvent event) {
        TvShowsGridView ui = findUi(event.callingId);
        if (ui != null) {
            ui.showError(event.error);
        }
    }

    @Subscribe
    public void onLoadingProgressVisibilityChanged(BaseState.ShowLoadingProgressEvent event) {
        TvShowsGridView ui = findUi(event.callingId);
        if (ui != null) {
            if (!event.secondary) {
                getViewState().showLoadingProgress(event.show);
            }
        }
    }

    /**
     * Fetch popular shows task
     */
    private void fetchPopular(final int callingId, final int page) {
        executeNetworkTask(new FetchPopularShowsRunnable(callingId, page));
    }

    private void fetchPopular(final int callingId) {
        MMoviesApp.get().getState().setPopularShows(callingId, null);
        fetchPopular(callingId, TMDB_FIRST_PAGE);
    }

    private void fetchPopularIfNeeded(final int callingId) {
        ApplicationState.ShowPaginatedResult popular = MMoviesApp.get().getState().getPopularShows();
        if (popular == null || MoviesCollections.isEmpty(popular.items)) {
            fetchPopular(callingId, TMDB_FIRST_PAGE);
        } else {
            final TvShowsGridView ui = findUi(callingId);
            if (ui != null) {
                populateUi(ui, MMoviesQueryType.POPULAR_SHOWS);
            }
        }
    }

    /**
     * Fetch on the air shows task
     */
    private void fetchOnTheAir(final int callingId, final int page) {
        executeNetworkTask(new FetchOnTheAirShowsRunnable(callingId, page));
    }

    private void fetchOnTheAir(final int callingId) {
        MMoviesApp.get().getState().setOnTheAirShows(callingId, null);
        fetchOnTheAir(callingId, TMDB_FIRST_PAGE);
    }

    private void fetchOnTheAirIfNeeded(final int callingId) {
        ApplicationState.ShowPaginatedResult onTheAir = MMoviesApp.get().getState().getOnTheAirShows();
        if (onTheAir == null || MoviesCollections.isEmpty(onTheAir.items)) {
            fetchOnTheAir(callingId, TMDB_FIRST_PAGE);
        } else {
            final TvShowsGridView ui = findUi(callingId);
            if (ui != null) {
                populateUi(ui, MMoviesQueryType.ON_THE_AIR_SHOWS);
            }
        }
    }

    @Override
    public void populateUi(TvShowsGridView ui, UiView.MMoviesQueryType queryType) {
        List<ShowWrapper> items = null;
        switch (queryType) {
            case POPULAR_SHOWS:
                ApplicationState.ShowPaginatedResult popular = MMoviesApp.get().getState().getPopularShows();
                if (popular != null) {
                    items = popular.items;
                }
                break;
            case ON_THE_AIR_SHOWS:
                ApplicationState.ShowPaginatedResult onTheAir = MMoviesApp.get().getState().getOnTheAirShows();
                if (onTheAir != null) {
                    items = onTheAir.items;
                }
                break;
        }
            ui.setData(items);
    }

    @Override
    public void populateUiFromEvent(MoviesState.BaseEvent event, UiView.MMoviesQueryType queryType) {
        Preconditions.checkNotNull(event, "event cannot be null");

        final TvShowsGridView ui = findUi(event.callingId);
        if (ui != null) {
            populateUi(ui, queryType);
        }
    }

    public String getUiTitle() {
        return null;
    }

}
