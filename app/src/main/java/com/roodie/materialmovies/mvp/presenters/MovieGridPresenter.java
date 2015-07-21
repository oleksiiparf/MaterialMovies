package com.roodie.materialmovies.mvp.presenters;

import android.os.Bundle;
import android.util.Log;

import com.google.common.base.Preconditions;
import com.roodie.materialmovies.mvp.views.BaseMovieListView;
import com.roodie.materialmovies.qualifiers.GeneralPurpose;
import com.roodie.model.Display;
import com.roodie.model.entities.ListItem;
import com.roodie.model.entities.MovieWrapper;
import com.roodie.model.state.ApplicationState;
import com.roodie.model.state.BaseState;
import com.roodie.model.tasks.BaseMovieRunnable;
import com.roodie.model.tasks.FetchConfigurationRunnable;
import com.roodie.model.tasks.FetchPopularRunnable;
import com.roodie.model.util.BackgroundExecutor;
import com.roodie.model.util.Injector;
import com.roodie.model.util.MoviesCollections;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;


/**
 * Created by Roodie on 10.07.2015.
 */

@Singleton
public class MovieGridPresenter extends BasePresenter {

    private static final String LOG_TAG = MovieGridPresenter.class.getSimpleName();

    private MovieGridView mMovieGridView;
    private final BackgroundExecutor mExecutor;
    private final ApplicationState mState;
    private final Injector mInjector;

    private static final int TMDB_FIRST_PAGE = 1;

    private boolean attached = false;

    @Inject
    public MovieGridPresenter(ApplicationState moviesState,
                              @GeneralPurpose BackgroundExecutor executor,
                              Injector injector) {
        mState = Preconditions.checkNotNull(moviesState, "mState can not be null");
        mExecutor = Preconditions.checkNotNull(executor, "executor cannot be null");
        mInjector = Preconditions.checkNotNull(injector, "injector cannot be null");

    }




    @Override
    public void initialize() {
        checkViewAlreadySetted();
        //on ui attached
        fetchPopularIfNeeded(1);
    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {
        mState.unregisterForEvents(this);
    }

    public void attachView (MovieGridView view) {
        Preconditions.checkNotNull(view, "View cannot be null");
        this.mMovieGridView = view;
        attached = true;
        mState.registerForEvents(this);
    }

    public void attachDisplay(Display display) {
        Preconditions.checkNotNull(display, "display is null");
        Preconditions.checkState(getDisplay() == null, "we currently have a display");
        setDisplay(display);
    }

    @Subscribe
    public void onPopularChanged(ApplicationState.PopularChangedEvent event) {
        Log.d(LOG_TAG, "Popular changed");
        populateUi();
    }

    @Subscribe
    public void onNetworkError(BaseState.OnErrorEvent event) {
        Log.d(LOG_TAG, "On Network error");

        if (mMovieGridView != null && null != event.error) {
            mMovieGridView.showError(event.error);
        }
    }

    @Subscribe
    public void onTmdbConfigurationChanged(ApplicationState.TmdbConfigurationChangedEvent event) {
        populateUi();
    }

    @Subscribe
    public void onLoadingProgressVisibilityChanged(BaseState.ShowLoadingProgressEvent event) {
        Log.d(LOG_TAG, "Loading progress visibility changed");
        if (attached) {
            if (event.secondary) {
                mMovieGridView.showSecondaryLoadingProgress(event.show);
            } else {
                mMovieGridView.showLoadingProgress(event.show);
            }
        }
    }


    public void showMovieDetail(MovieWrapper movie, Bundle bundle){
        Preconditions.checkNotNull(movie, "movie cannot be null");
        Display display = getDisplay();
        if (display != null) {
            if (movie.getTmdbId() != null) {
                display.startMovieDetailActivity(String.valueOf(movie.getTmdbId()), bundle);
            }
        }
    }

    public void refresh() {
        fetchPopular(1);
    }


    private void checkViewAlreadySetted() {
      Preconditions.checkState(attached = true, "View not attached");
    }

    public void onScrolledToBottom(){
        ApplicationState.MoviePaginatedResult result;

        result = mState.getPopular();
        if (canFetchNextPage(result)) {
            fetchPopular(1, result.page + 1);
        }
    }

    private boolean canFetchNextPage(ApplicationState.PaginatedResult<?> paginatedResult) {
        return paginatedResult != null && paginatedResult.page < paginatedResult.totalPages;
    }

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

    private void fetchTmdbConfiguration() {
        FetchConfigurationRunnable task = new FetchConfigurationRunnable();
        mInjector.inject(task);
        mExecutor.execute(task);
    }



    public void  populateUi(){
            Log.d(LOG_TAG, "populateUi: " + mMovieGridView.getClass().getSimpleName());
        List<MovieWrapper> items = null;
        ApplicationState.MoviePaginatedResult popular = mState.getPopular();
        if (popular != null) {
            items = popular.items;
        }
        if (items == null) {
            mMovieGridView.setItems(null);
        } else  {
            mMovieGridView.setItems(createListItemList(items));
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



    public interface MovieGridView extends BaseMovieListView<MovieWrapper> {

    }
}
