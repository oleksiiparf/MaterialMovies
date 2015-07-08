package com.roodie.materialmovies.modules.library;

import android.content.Context;

import com.roodie.materialmovies.qualifiers.AppContext;
import com.roodie.materialmovies.qualifiers.Database;
import com.roodie.materialmovies.qualifiers.GeneralPurpose;
import com.roodie.materialmovies.util.MMoviesBackgroundExecutor;
import com.roodie.materialmovies.util.MMoviesCountryProvider;
import com.roodie.model.util.BackgroundExecutor;
import com.roodie.model.util.CountryProvider;
import com.roodie.model.util.ImageHelper;
import com.squareup.otto.Bus;

import java.util.concurrent.Executors;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Roodie on 25.06.2015.
 */
@Module(
        includes = ContextProvider.class,
        library = true
)
public class UtilProvider {

    @Provides
    @Singleton
    public Bus provideEventBus() {
        return new Bus();
    }

    @Provides @Singleton
    public CountryProvider provideCountryProvider(@AppContext Context context) {
        return new MMoviesCountryProvider(context);
    }


    @Provides @Singleton @Database
    public BackgroundExecutor provideDatabaseThreadExecutor() {
        return new MMoviesBackgroundExecutor(Executors.newSingleThreadExecutor());
    }



}