package com.roodie.materialmovies.modules.library;

import android.content.Context;

import com.roodie.materialmovies.MMoviesApp;
import com.roodie.materialmovies.qualifiers.AppContext;
import com.roodie.materialmovies.qualifiers.Database;
import com.roodie.materialmovies.qualifiers.GeneralPurpose;
import com.roodie.materialmovies.util.AndroidStringFetcher;
import com.roodie.materialmovies.util.MMoviesBackgroundExecutor;
import com.roodie.materialmovies.util.MMoviesCountryProvider;
import com.roodie.model.util.BackgroundExecutor;
import com.roodie.model.util.CountryProvider;
import com.roodie.model.util.StringFetcher;
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
        return MMoviesApp.get().getBus();
    }

    @Provides @Singleton
    public CountryProvider provideCountryProvider(@AppContext Context context) {
        return new MMoviesCountryProvider(context);
    }

    @Provides @Singleton @GeneralPurpose
    public BackgroundExecutor provideMultiThreadExecutor() {
        final int numberCores = Runtime.getRuntime().availableProcessors();
        return new MMoviesBackgroundExecutor(Executors.newFixedThreadPool(numberCores * 2 + 1));
    }

    @Provides @Singleton @Database
    public BackgroundExecutor provideDatabaseThreadExecutor() {
        return new MMoviesBackgroundExecutor(Executors.newSingleThreadExecutor());
    }

    @Provides @Singleton
    public StringFetcher provideStringFetcher(@AppContext Context context) {
        return new AndroidStringFetcher(context);
    }


}