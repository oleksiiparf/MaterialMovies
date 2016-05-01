package com.roodie.materialmovies.mvp.callbacks;

import com.roodie.materialmovies.MMoviesApp;
import com.roodie.model.entities.MovieWrapper;
import com.roodie.model.entities.ShowWrapper;
import com.roodie.model.entities.Watchable;
import com.roodie.model.state.ApplicationState;
import com.roodie.model.util.FileLog;
import com.roodie.model.util.MoviesCollections;

import java.util.List;

/**
 * Created by Roodie on 30.03.2016.
 */
public class WatchedDbLoadCallback implements ApplicationState.Callback<List<Watchable>> {
    @Override
    public void onFinished(List<Watchable> result) {
        FileLog.d("watched", "LoadCallback : result is empty? : " + (MoviesCollections.isEmpty(result)));
        MMoviesApp.get().getState().setWatched(result);
        if (!MoviesCollections.isEmpty(result)) {
            for (Watchable item : result) {
                switch (item.getWatchableType()) {
                    case MOVIE:
                        FileLog.d("watched", "LoadCallback : watchable type = movie : " + item.getTitle());
                        MMoviesApp.get().getState().putMovie((MovieWrapper)item);
                        break;
                    case TV_SHOW:
                        FileLog.d("watched", "LoadCallback : watchable type = show : " + item.getTitle());
                        MMoviesApp.get().getState().putShow((ShowWrapper)item);
                        break;
                }
            }
        }

        MMoviesApp.get().getState().setPopulatedWatchedFromDb(true);
    }
}