package com.roodie.materialmovies.modules.library;

import com.roodie.model.Constants;
import com.uwetrottmann.tmdb.Tmdb;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Roodie on 25.06.2015.
 */

@Module (
        library = true,
        includes = ContextProvider.class
)

public class NetworkProvider {

    @Provides @Singleton
    public Tmdb provideTmdbClient() {
        Tmdb tmdb = new Tmdb();
        tmdb.setApiKey(Constants.TMDB_API_KEY);
        tmdb.setIsDebug(Constants.DEBUG_NETWORK);
        return tmdb;
    }
}
