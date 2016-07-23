package com.roodie.model.tasks;

import com.roodie.model.entities.MovieWrapper;
import com.roodie.model.entities.ShowWrapper;
import com.roodie.model.entities.Watchable;
import com.roodie.model.entities.WatchableType;
import com.roodie.model.repository.Repository;
import com.roodie.model.state.MoviesState;
import com.roodie.model.util.FileLog;

import java.util.Collections;
import java.util.List;

/**
 * Created by Roodie on 08.03.2016.
 */
public abstract class WatchableActionRunnable<M extends Watchable> extends DatabaseBackgroundRunnable<Void> {

    private final M  mItem;

    public WatchableActionRunnable(int callingId, M item) {
        super(callingId);
        this.mItem = item;
        itemRequiresModifying(mItem);
    }

    protected abstract void itemRequiresModifying(M item);

    @Override
    public void preExecute() {
        checkState(mItem);
    }

    @Override
    public Void doDatabaseCall() {
        Repository movieRepository = mState.getRepositoryInstance(MovieWrapper.class);
        Repository showRepository = mState.getRepositoryInstance(ShowWrapper.class);



        //boolean shouldMarkAsWatched = mItem.isWatched();
        //if (shouldMarkAsWatched) {
            if ((WatchableType.MOVIE).equals(mItem.getWatchableType())) {
                MovieWrapper movie = (MovieWrapper)movieRepository.get(String.valueOf(mItem.getTmdbId()));
                if (movie != null) {
                    FileLog.d("sqlite", "Update movie");
                    movieRepository.update(mItem);
                } else {
                    FileLog.d("sqlite", "Add movie");
                    movieRepository.add(mItem);
                }
            } else if ((WatchableType.TV_SHOW).equals(mItem.getWatchableType())) {
                ShowWrapper show = (ShowWrapper) showRepository.get(String.valueOf(mItem.getTmdbId()));
                if (show != null) {
                    showRepository.update(mItem);
                } else {
                    showRepository.add(mItem);
                }
            }
        //}
        return null;
    }

    protected void checkState(M item) {
        final List<Watchable> watched = mState.getWatched();

        //if (!MoviesCollections.isEmpty(watched)) {
        boolean shouldMarkAsWatched = item.isWatched();

        if (shouldMarkAsWatched != watched.contains(item)) {
            if (shouldMarkAsWatched) {
                watched.add(item);
                Collections.sort(watched, Watchable.COMPARATOR__ITEM_DATE_DESC);
            } else {
                watched.remove(item);
            }
        }
        //}
    }

    protected M getItem() {
        return mItem;
    }

    @Override
    public void postExecute(Void result) {
        getEventBus().post(new MoviesState.WatchedChangedEvent());
    }
}
