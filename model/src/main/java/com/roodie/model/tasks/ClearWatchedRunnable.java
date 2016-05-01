package com.roodie.model.tasks;

import com.roodie.model.entities.MovieWrapper;
import com.roodie.model.entities.ShowWrapper;
import com.roodie.model.state.ApplicationState;

/**
 * Created by Roodie on 29.04.2016.
 */
public class ClearWatchedRunnable extends DatabaseBackgroundRunnable<Void>{

    private final ApplicationState.Callback<Void> callback;

    public ClearWatchedRunnable(int callingId,  final ApplicationState.Callback<Void> callback) {
        super(callingId);
        this.callback = callback;
    }

    @Override
    public Void doDatabaseCall() {
        mState.getRepositoryInstance(MovieWrapper.class).removeAll();
        mState.getRepositoryInstance(ShowWrapper.class).removeAll();
        return null;
    }

    @Override
    public void postExecute(Void result) {
        callback.onFinished(result);
    }
}
