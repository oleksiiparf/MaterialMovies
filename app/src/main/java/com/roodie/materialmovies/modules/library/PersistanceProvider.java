package com.roodie.materialmovies.modules.library;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.roodie.materialmovies.qualifiers.AppContext;
import com.roodie.materialmovies.qualifiers.Database;
import com.roodie.materialmovies.qualifiers.FilesDirectory;
import com.roodie.materialmovies.util.AndroidFileManager;
import com.roodie.materialmovies.util.AndroidMMoviesPreferences;
import com.roodie.materialmovies.util.MMovieSQLiteOpenHelper;
import com.roodie.model.state.AsyncDatabaseHelper;
import com.roodie.model.state.AsyncDatabaseHelperImpl;
import com.roodie.model.state.DatabaseHelper;
import com.roodie.model.util.BackgroundExecutor;
import com.roodie.model.util.FileManager;
import com.roodie.model.util.MMoviesPreferences;

import java.io.File;

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


    @Provides @Singleton
    public DatabaseHelper getDatabaseHelper(@AppContext Context context) {
        return new MMovieSQLiteOpenHelper(context);
    }

    @Provides @Singleton
    public AsyncDatabaseHelper getAsyncDatabaseHelper(
            @Database BackgroundExecutor executor,
            DatabaseHelper databaseHelper) {
        return new AsyncDatabaseHelperImpl(executor, databaseHelper);
    }

    @Provides @Singleton
    public FileManager provideFileManager(@FilesDirectory File file) {
        return new AndroidFileManager(file);
    }

}
