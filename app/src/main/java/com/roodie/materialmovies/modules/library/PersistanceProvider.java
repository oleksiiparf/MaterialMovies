package com.roodie.materialmovies.modules.library;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.roodie.materialmovies.qualifiers.AppContext;
import com.roodie.materialmovies.util.AndroidMMoviesPreferences;
import com.roodie.model.util.MMoviesPreferences;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Roodie on 11.07.2015.
 */

@Module(
        library = true,
        includes = {
                ContextProvider.class,
                UtilProvider.class
        }
)

public class PersistanceProvider {

    @Provides @Singleton
    public MMoviesPreferences provideMMoviesPreferences(@AppContext Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return new AndroidMMoviesPreferences(preferences);
    }
}
