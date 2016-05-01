package com.roodie.model.tasks;

import com.roodie.model.entities.MovieWrapper;
import com.roodie.model.entities.ShowWrapper;
import com.roodie.model.entities.Watchable;
import com.roodie.model.state.ApplicationState;
import com.roodie.model.util.Lists;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Roodie on 08.03.2016.
 */
public class FetchWatchedRunnable extends DatabaseBackgroundRunnable<List<Watchable>> {

    private final ApplicationState.Callback<List<Watchable>> callback;

    public FetchWatchedRunnable(int callingId, final ApplicationState.Callback<List<Watchable>> callback) {
        super(callingId);
        this.callback = callback;
    }


    @Override
    public List<Watchable> doDatabaseCall() {

        ArrayList watchedList = Lists.newArrayList();

        List<MovieWrapper> movies = mState.getRepositoryInstance(MovieWrapper.class).getAll();
        //Issue : iterator is not working right, actually it`s not working
         /*Iterator moviesIterator = movies.iterator();
        while (moviesIterator.hasNext()) { */
        for (MovieWrapper movie: movies) {
            /*MovieWrapper movie = (MovieWrapper)moviesIterator.next();*/
            if (movie.isWatched()) {
                watchedList.add(movie);

            }
        }

        /*Iterator showsIterator = Lists.newArrayList(mState.getRepositoryInstance(ShowWrapper.class).getAll()).iterator();*/
        List<ShowWrapper> shows = mState.getRepositoryInstance(ShowWrapper.class).getAll();
     /*   showsIterator.next();
        while (showsIterator.hasNext()) {*/
        for (ShowWrapper show: shows) {
           /* ShowWrapper show = (ShowWrapper)showsIterator.next();*/
            if (show.isWatched()) {
                watchedList.add(show);
            }
        }

        if (!watchedList.isEmpty()) {
                Collections.sort(watchedList, Watchable.COMPARATOR__ITEM_DATE_DESC);
        }
        return watchedList;
    }

    @Override
    public void postExecute(List<Watchable> result) {
        if (result != null) {
            callback.onFinished(result);
        }
    }
}
