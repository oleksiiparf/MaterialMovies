package com.roodie.materialmovies.mvp.presenters;

import android.util.Log;
import android.view.View;

import com.google.common.base.Preconditions;
import com.roodie.materialmovies.R;
import com.roodie.materialmovies.mvp.views.MovieView;
import com.roodie.materialmovies.qualifiers.GeneralPurpose;
import com.roodie.model.entities.ListItem;
import com.roodie.model.entities.SeasonWrapper;
import com.roodie.model.entities.ShowWrapper;
import com.roodie.model.state.ApplicationState;
import com.roodie.model.state.BaseState;
import com.roodie.model.state.MoviesState;
import com.roodie.model.tasks.BaseMovieRunnable;
import com.roodie.model.tasks.FetchDetailTvShowRunnable;
import com.roodie.model.util.BackgroundExecutor;
import com.roodie.model.util.Injector;
import com.roodie.model.util.MoviesCollections;
import com.roodie.model.util.StringFetcher;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by Roodie on 16.09.2015.
 */
public class ShowDetailPresenter extends BasePresenter<ShowDetailPresenter.ShowDetailView> {

    private static final String LOG_TAG = ShowDetailPresenter.class.getSimpleName();

    private final BackgroundExecutor mExecutor;

    private final Injector mInjector;

    private final StringFetcher mStringFetcher;

    @Inject
    public ShowDetailPresenter(
            @GeneralPurpose BackgroundExecutor executor,
            ApplicationState state, Injector injector, StringFetcher stringFetcher) {
        super(state);
        mExecutor = Preconditions.checkNotNull(executor, "executor can not be null");
        mInjector = Preconditions.checkNotNull(injector, "injector cannot be null");
        mStringFetcher = Preconditions.checkNotNull(stringFetcher, "stringFetcher cannot be null");
    }

    @Subscribe
    public void onShowDetailChanged(MoviesState.TvShowInformationUpdatedEvent event) {
        Log.d(LOG_TAG, "show detail changed");
        populateUi();
        checkDetailTvShowResult(event.callingId, event.item);
    }

    @Subscribe
    public void onNetworkError(BaseState.OnErrorEvent event) {
        Log.d(LOG_TAG, "network error");
        if (isViewAttached() && null != event.error) {
            getView().showError(event.error);
        }
    }

    @Subscribe
    public void onLoadingProgressVisibilityChanged(BaseState.ShowLoadingProgressEvent event) {
        Log.d(LOG_TAG, "loading progress chenged");
        if (isViewAttached()) {
            if (event.secondary) {
                getView().showSecondaryLoadingProgress(event.show);
            } else {
                getView().showLoadingProgress(event.show);
            }
        }
    }

    @Override
    public void initialize() {
        Log.d(LOG_TAG, "initialize");
        switch (getView().getQueryType()) {
            case TV_SHOW_DETAIL:
                fetchDetailTvShowIfNeeded(getView().hashCode(), getView().getRequestParameter());
                break;
            case TV_SEASONS_LIST: {

                break;
            }
        }


    }

    public void populateUi() {
        Log.d(LOG_TAG, "populateUi: " + getView().getClass().getSimpleName());

        final ShowWrapper show = mState.getTvShow(getView().getRequestParameter());
        getView().updateDisplayTitle(show.getTitle());

        switch (getView().getQueryType()) {
            case TV_SHOW_DETAIL:
                if (show != null) {
                    getView().setTvShow(show);
                }
                break;
            case TV_SEASONS_LIST: {
                if (show != null && !MoviesCollections.isEmpty(show.getSeasons())) {
                    getView().updateDisplaySubtitle(getUiSubtitle());
                    getView().setTvSeasons(createListItemList(show.getSeasons()));
                }
                break;
            }
        }
    }

    public String getUiSubtitle() {
        switch (getView().getQueryType()) {
            case TV_SEASONS_LIST: {
                return mStringFetcher.getString(R.string.seasons_title);
            }
        }
        return null;
    }



    /**
     * Fetch detail movie information
     */
    private void fetchDetailTvShow(final int callingId, String id) {
        Preconditions.checkNotNull(id, "id cannot be null");

        final ShowWrapper tvShow = mState.getTvShow(id);
        if (tvShow != null) {

            fetchDetailTvShowIfNeeded(callingId, tvShow, true);
        }
    }

    private void fetchDetailTvShowFromTmdb(final int callingId, int id) {
        Preconditions.checkNotNull(id, "id cannot be null");
        Log.d(LOG_TAG, "fetch show from tmdb");
        ShowWrapper show = mState.getTvShow(id);
        if (show != null) {
            show.markFullFetchStarted();
        }

        executeTask(new FetchDetailTvShowRunnable(callingId, id));
    }

    private void fetchDetailTvShowIfNeeded(final int callingId, String id) {
        Log.d(LOG_TAG, "fetch show if needed");
        Preconditions.checkNotNull(id, "id cannot be null");

        ShowWrapper cached = mState.getTvShow(id);
        if (cached == null) {
            Log.d(LOG_TAG, "cached == null");
            fetchDetailTvShow(callingId, id);
        } else {
            Log.d(LOG_TAG, "cached != null");
            fetchDetailTvShowIfNeeded(callingId, cached, false);
        }
    }

    private void fetchDetailTvShowIfNeeded(int callingId, ShowWrapper show, boolean force) {
        Preconditions.checkNotNull(show, "show cannot be null");

        if (force || show.needFullFetchFromTmdb()) {
            if (show.getTmdbId() != null) {
                Log.d(LOG_TAG, "show.getTmdbId() != null");
                fetchDetailTvShowFromTmdb(callingId, show.getTmdbId());
            }
        } else {
            populateUi();
        }
    }

    public void refresh() {
        Log.d(LOG_TAG, "Refresh");
        fetchDetailTvShow(getView().hashCode(), getView().getRequestParameter());
    }

    private void checkDetailTvShowResult(int callingId, ShowWrapper show) {
        Log.d(LOG_TAG, "check detail tv show result");
        Preconditions.checkNotNull(show, "show cannot be null");
        fetchDetailTvShowIfNeeded(callingId, show, false);
    }

    private <R> void executeTask(BaseMovieRunnable<R> task) {
        mInjector.inject(task);
        mExecutor.execute(task);
    }

    private <T extends ListItem<T>> List<ListItem<T>> createListItemList(final List<T> items) {
        Preconditions.checkNotNull(items, "items cannot be null");
        ArrayList<ListItem<T>> listItems = new ArrayList<>(items.size());
        for (ListItem<T> item : items) {
            listItems.add(item);
        }
        return listItems;
    }


    public interface ShowDetailView extends MovieView {

        void setTvShow(ShowWrapper show);

        void showTvShowImages(ShowWrapper movie);

        void showTvShowCreditsDialog(MovieQueryType queryType);

        void setTvSeasons(List<ListItem<SeasonWrapper>> items);

        void showSeasonDetail(Integer seasonId, View view, int position);

        void updateDisplaySubtitle(CharSequence subtitle);
    }


}
