package com.roodie.materialmovies.mvp.presenters;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.roodie.materialmovies.MMoviesApp;
import com.roodie.materialmovies.mvp.callbacks.WatchedDbLoadCallback;
import com.roodie.materialmovies.mvp.views.MainView;
import com.roodie.model.entities.Watchable;
import com.roodie.model.state.MoviesState;
import com.roodie.model.tasks.DatabaseBackgroundRunnable;
import com.roodie.model.tasks.FetchWatchedRunnable;
import com.roodie.model.util.FileLog;
import com.roodie.model.util.MoviesCollections;
import com.squareup.otto.Subscribe;

import java.util.List;

/**
 * Created by Roodie on 04.03.2016.
 */

@InjectViewState
public class MainPresenter extends MvpPresenter<MainView> {

    public MainPresenter() {
        super();
        MMoviesApp.get().getState().registerForEvents(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MMoviesApp.get().getState().unregisterForEvents(this);
    }

    @Subscribe
    public void onWatchedMoviesChanged(MoviesState.WatchedChangeEvent event) {
        populateUi(getViewState());
    }

    public int getId(MainView view) {
        return view.hashCode();
    }

    public void populateUi(MainView view) {
        int[] watchedCount = new int[]{0, 0};
        List<Watchable> items = MMoviesApp.get().getState().getWatched();

        for (Watchable item: items) {
            switch (item.getWatchableType()) {
                case MOVIE:
                    watchedCount[0]++;
                    break;
                case TV_SHOW:
                    watchedCount[1]++;
                    break;
            }
        }
        view.setData(watchedCount);
    }

    @Override
    public void attachView(MainView view) {
        super.attachView(view);
        final int callingId = getId(view);
            populateStateFromDb(callingId);
            populateUi(view);
    }

    private void populateStateFromDb(int callingId) {
        if (!MMoviesApp.get().getState().isPopulatedWatchedFromDb() || MoviesCollections.isEmpty(MMoviesApp.get().getState().getWatched())) {
            fetchWatched(callingId);
        }
    }

    public <BR> void executeBackgroundTask(DatabaseBackgroundRunnable<BR> task) {
        MMoviesApp.get().inject(task);
        MMoviesApp.get().getBackgroundExecutor().execute(task);
    }

    private void fetchWatched(final int callingId) {
        FileLog.d("watched", "MainPresenter: Fetching watched from db");
        if (MMoviesApp.get().isAuthentificatedFeatureEnabled()) {
            executeBackgroundTask(new FetchWatchedRunnable(callingId, new WatchedDbLoadCallback()));
        }
    }

}
