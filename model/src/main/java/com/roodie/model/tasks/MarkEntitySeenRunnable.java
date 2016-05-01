package com.roodie.model.tasks;

import com.roodie.model.entities.MovieWrapper;
import com.roodie.model.entities.ShowWrapper;
import com.roodie.model.entities.Watchable;
import com.roodie.model.state.MoviesState;

/**
 * Created by Roodie on 09.03.2016.
 */
public class MarkEntitySeenRunnable<W extends Watchable> extends WatchableActionRunnable<W> {

    public MarkEntitySeenRunnable(int callingId, W item) {
        super(callingId,item);
    }

    @Override
    protected void itemRequiresModifying(W item) {
        item.setWatched(true);
    }

    @Override
    public void postExecute(Void result) {
        super.postExecute(result);
        switch (getItem().getWatchableType()) {
            case MOVIE:
                getEventBus().post(new MoviesState.MovieFlagUpdateEvent(getCallingId(), (MovieWrapper)getItem()));
                break;
            case TV_SHOW:
                getEventBus().post(new MoviesState.ShowFlagUpdateEvent(getCallingId(), (ShowWrapper)getItem()));
                break;
        }
    }
}
