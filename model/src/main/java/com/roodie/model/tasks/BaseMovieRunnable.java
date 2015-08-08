package com.roodie.model.tasks;

import android.util.Log;

import com.roodie.model.network.NetworkCallRunnable;
import com.roodie.model.network.NetworkError;
import com.roodie.model.state.ApplicationState;
import com.roodie.model.state.AsyncDatabaseHelper;
import com.roodie.model.state.BaseState;
import com.roodie.model.state.EntitityMapper;
import com.roodie.model.state.MoviesState;
import com.roodie.model.util.CountryProvider;
import com.squareup.otto.Bus;
import com.uwetrottmann.tmdb.Tmdb;

import javax.inject.Inject;

import dagger.Lazy;
import retrofit.RetrofitError;

/**
 * Created by Roodie on 24.06.2015.
 */
public abstract class BaseMovieRunnable<R> extends NetworkCallRunnable<R> {

    public static final String LOG_TAG = BaseMovieRunnable.class.getSimpleName();

    @Inject
    ApplicationState mState;

    @Inject Lazy<Tmdb> mTmdbClient;
    @Inject Lazy<Bus> mEventBus;
    @Inject Lazy<AsyncDatabaseHelper> mDbHelper;
    @Inject Lazy<CountryProvider> mCountryProvider;
    @Inject Lazy<EntitityMapper> mLazyEntityMapper;



    private final int mCallingId;

    public BaseMovieRunnable(int callingId) {
        mCallingId = callingId;
    }

    @Override
    public void onPreTmdbCall() {
        getEventBus().post(createLoadingProgressEvent(true));
    }


    @Override
    public void onError(RetrofitError re) {
        getEventBus().post(new BaseState.OnErrorEvent(getCallingId(),
                NetworkError.from(re)));

    }

    @Override
    public void onFinished() {
       getEventBus().post(createLoadingProgressEvent(false));
    }



    protected Bus getEventBus() {return mEventBus.get(); }

    public  Tmdb getTmdbClient() {
        return mTmdbClient.get();
    }

    public AsyncDatabaseHelper getDbHelper() {
        return mDbHelper.get();
    }

    public CountryProvider getCountryProvider() {
        return mCountryProvider.get();
    }

    public int getCallingId() {
        return mCallingId;
    }

    protected Object createLoadingProgressEvent(boolean show) {
        return new BaseState.ShowLoadingProgressEvent(getCallingId(), show);
    }

    public EntitityMapper getEntityMapper() {
        return mLazyEntityMapper.get();
    }
}
