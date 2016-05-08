package com.roodie.model.tasks;

import com.roodie.model.network.BackgroundCallRunnable;
import com.roodie.model.state.ApplicationState;
import com.squareup.otto.Bus;

import javax.inject.Inject;

import dagger.Lazy;

/**
 * Created by Roodie on 08.03.2016.
 */
public abstract class DatabaseBackgroundRunnable<R> extends BackgroundCallRunnable<R> {

    @Inject ApplicationState mState;
    @Inject Lazy<Bus> mEventBus;


    private final int mCallingId;

    public DatabaseBackgroundRunnable(int callingId) {
        mCallingId = callingId;
    }

    @Override
    public final R runAsync() {


        return doDatabaseCall();
    }

    public abstract R doDatabaseCall();

    protected Bus getEventBus() {return mEventBus.get(); }

    public int getCallingId() {
        return mCallingId;
    }
}
