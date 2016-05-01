package com.roodie.materialmovies.mvp.presenters;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.roodie.materialmovies.MMoviesApp;
import com.roodie.materialmovies.R;
import com.roodie.materialmovies.mvp.callbacks.WatchedDbLoadCallback;
import com.roodie.materialmovies.mvp.views.UiView;
import com.roodie.materialmovies.mvp.views.WatchedListView;
import com.roodie.model.entities.Watchable;
import com.roodie.model.state.BaseState;
import com.roodie.model.state.MoviesState;
import com.roodie.model.tasks.BaseRunnable;
import com.roodie.model.tasks.DatabaseBackgroundRunnable;
import com.roodie.model.tasks.FetchWatchedRunnable;
import com.roodie.model.util.FileLog;
import com.roodie.model.util.MoviesCollections;
import com.squareup.otto.Subscribe;

import java.util.Collections;
import java.util.List;

/**
 * Created by Roodie on 06.03.2016.
 */

@InjectViewState
public class ListWatchedPresenter extends MvpPresenter<WatchedListView> implements BaseListPresenter<WatchedListView> {


    public ListWatchedPresenter() {
        super();
        MMoviesApp.get().getState().registerForEvents(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MMoviesApp.get().getState().unregisterForEvents(this);
    }

    @Override
    public void onUiAttached(WatchedListView view, UiView.MMoviesQueryType queryType, String parameter) {
        final int callingId = getId(view);
        switch (queryType) {
            case WATCHED:
                fetchWatchedIfNeeded(callingId);
                break;
        }
        view.updateDisplayTitle(getUiTitle(queryType));
        populateUi(view, queryType);

    }

    @Override
    public String getUiTitle(UiView.MMoviesQueryType queryType) {
        switch (queryType) {
            case WATCHED:
                return MMoviesApp.get().getStringFetcher().getString(R.string.watched_title);
        }
        return null;
    }

    @Override
    public void populateUi(WatchedListView view, UiView.MMoviesQueryType queryType) {
        FileLog.d("watched", "Populate UI by query = " + queryType);
        List<Watchable> items = null;
        switch (queryType) {
            case WATCHED:
                items = MMoviesApp.get().getState().getWatched();
                Collections.sort(items, Watchable.COMPARATOR__ITEM_DATE_DESC);
                break;
        }

        view.updateDisplayTitle(getUiTitle(UiView.MMoviesQueryType.WATCHED));

        view.setData(items);
    }

    @Override
    public boolean canFetchNextPage(BaseState.PaginatedResult<?> paginatedResult) {
        return false;
    }

    @Override
    public void refresh(WatchedListView view, UiView.MMoviesQueryType queryType) {
        //NTD
    }

    @Override
    public void onScrolledToBottom(WatchedListView view, UiView.MMoviesQueryType queryType) {
        //NTD
    }

    @Override
    public void populateUiFromEvent(BaseState.BaseEvent event, UiView.MMoviesQueryType queryType) {
        //NTD
    }

    @Override
    public WatchedListView findUi(int id) {
        for (WatchedListView ui: getAttachedViews()) {
            if (getId(ui) == id) {
                return ui;
            }
        }
        return null;
    }

    @Override
    public int getId(WatchedListView view) {
        return view.hashCode();
    }

    @Override
    public <BR> void executeNetworkTask(BaseRunnable<BR> task) {
    }

    public <BR> void executeBackgroundTask(DatabaseBackgroundRunnable<BR> task) {
        MMoviesApp.get().inject(task);
        MMoviesApp.get().getBackgroundExecutor().execute(task);
    }

    @Subscribe
    public void onWatchedChanged(MoviesState.WatchedChangeEvent event) {
        populateUi(getViewState(), UiView.MMoviesQueryType.WATCHED);
    }

    private void fetchWatchedIfNeeded(final int callingId) {
          if (!MMoviesApp.get().getState().isPopulatedWatchedFromDb() || MoviesCollections.isEmpty(MMoviesApp.get().getState().getWatched())) {
            fetchWatched(callingId);
        }
    }

    private void fetchWatched(final int callingId) {
        if (MMoviesApp.get().isAuthentificatedFeatureEnabled()) {
            executeBackgroundTask(new FetchWatchedRunnable(callingId, new WatchedDbLoadCallback()));
        }
    }


}
