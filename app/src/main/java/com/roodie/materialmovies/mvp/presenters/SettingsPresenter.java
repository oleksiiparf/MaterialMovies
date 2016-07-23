package com.roodie.materialmovies.mvp.presenters;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.roodie.materialmovies.MMoviesApp;
import com.roodie.materialmovies.mvp.views.SettingsView;
import com.roodie.model.state.ApplicationState;
import com.roodie.model.state.MoviesState;
import com.roodie.model.tasks.ClearWatchedRunnable;
import com.roodie.model.tasks.DatabaseBackgroundRunnable;
import com.roodie.model.util.FileLog;
import com.squareup.otto.Subscribe;

/**
 * Created by Roodie on 29.04.2016.
 */

@InjectViewState
public class SettingsPresenter extends MvpPresenter<SettingsView> {

    public SettingsPresenter() {
        super();
        MMoviesApp.get().getState().registerForEvents(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MMoviesApp.get().getState().unregisterForEvents(this);
    }

    @Subscribe
    public void onWatchedChanged(MoviesState.WatchedClearedEvent event) {
        FileLog.d("watched", "MainPresenter: on watched changed");

        getViewState().onWatchedCleared();
    }

    private  <BR> void executeBackgroundTask(DatabaseBackgroundRunnable<BR> task) {
        MMoviesApp.get().inject(task);
        MMoviesApp.get().getBackgroundExecutor().execute(task);
    }

    public int getId(SettingsView view) {
        return view.hashCode();
    }


    public void clearWatched(SettingsView view) {
        final int callingId = getId(view);
        if (MMoviesApp.get().isAuthentificatedFeatureEnabled()) {
            executeBackgroundTask(new ClearWatchedRunnable(callingId, new WatchedDbClearedCallback()));
        }
    }

    public final class WatchedDbClearedCallback implements ApplicationState.Callback<Void> {
        @Override
        public void onFinished(Void result) {
            MMoviesApp.get().getState().setWatchedCleared();
            MMoviesApp.get().getState().setPopulatedWatchedFromDb(true);
        }
    }
}
