package com.roodie.materialmovies.modules.library;

import android.content.Context;

import com.roodie.materialmovies.qualifiers.AppContext;
import com.roodie.materialmovies.qualifiers.CacheDirectory;
import com.roodie.materialmovies.util.MMoviesTmdb;
import com.roodie.model.Constants;
import com.uwetrottmann.tmdb.Tmdb;

import java.io.File;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Roodie on 25.06.2015.
 */

 @Module(
        library = true,
        includes = ContextModule.class
)
public class NetworkModule {

   @Provides @Singleton
    public Tmdb provideTmdbClient(@AppContext Context context) {
        Tmdb tmdb = new MMoviesTmdb(context);
        tmdb.setApiKey(Constants.TMDB_API_KEY);
        tmdb.setIsDebug(Constants.DEBUG_NETWORK);
        return tmdb;
    }


    @Provides @Singleton @CacheDirectory
    public File provideHttpCacheLocation(@AppContext Context context) {
        return context.getCacheDir();
    }


}
