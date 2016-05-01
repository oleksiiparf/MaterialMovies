package com.roodie.materialmovies.mvp.presenters;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.roodie.materialmovies.MMoviesApp;
import com.roodie.materialmovies.mvp.views.UserHeaderView;
import com.roodie.model.entities.Watchable;
import com.roodie.model.state.MoviesState;
import com.roodie.model.tasks.BaseRunnable;
import com.roodie.model.tasks.DatabaseBackgroundRunnable;
import com.roodie.model.util.FileLog;
import com.roodie.model.util.MoviesCollections;
import com.squareup.otto.Subscribe;

import java.util.List;

/**
 * Created by Roodie on 28.03.2016.
 */

@InjectViewState
public class UserHeaderPresenter extends MvpPresenter<UserHeaderView> implements BasePresenter<UserHeaderView> {

    public UserHeaderPresenter() {
        super();
        MMoviesApp.get().getState().registerForEvents(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MMoviesApp.get().getState().unregisterForEvents(this);
    }


    public void onUiAttached(UserHeaderView view) {
        final int callingId = getId(view);
        fetchWatchedIfNeeded(callingId);

        populateUi(view);

    }

    @Subscribe
    public void onWatchedChanged(MoviesState.WatchedChangeEvent event) {
        populateUi(getViewState());
    }


    public void populateUi(UserHeaderView view) {
        int[] watchedCount = new int[]{0, 0};
        List<Watchable> items = MMoviesApp.get().getState().getWatched();

        for (Watchable item: items) {
            switch (item.getWatchableType()) {
                case MOVIE:
                    ++watchedCount[0];
                    break;
                case TV_SHOW:
                    ++watchedCount[1];
            }
        }
        view.setData(watchedCount);
    }

    @Override
    public int getId(UserHeaderView view) {
        return view.hashCode();
    }

    @Override
    public <BR> void executeNetworkTask(BaseRunnable<BR> task) {

    }

    private void fetchWatchedIfNeeded(final int callingId) {
        FileLog.d("watched", "Is populated from DB = " + MMoviesApp.get().getState().isPopulatedWatchedFromDb());
        if (!MMoviesApp.get().getState().isPopulatedWatchedFromDb() || MoviesCollections.isEmpty(MMoviesApp.get().getState().getWatched())) {
            fetchWatched(callingId);
        }
    }

    private void fetchWatched(final int callingId) {
        FileLog.d("watched", "ListWatchedPresenter: Fetching watched from db");
        if (MMoviesApp.get().isAuthentificatedFeatureEnabled()) {
           // executeBackgroundTask(new FetchWatchedRunnable(callingId, new ApplicationState.WatchedDbLoadCallback()));
        }
    }

    public <BR> void executeBackgroundTask(DatabaseBackgroundRunnable<BR> task) {
        MMoviesApp.get().inject(task);
        MMoviesApp.get().getBackgroundExecutor().execute(task);
    }
}
