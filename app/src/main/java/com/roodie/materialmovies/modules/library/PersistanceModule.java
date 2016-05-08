package com.roodie.materialmovies.modules.library;

import android.content.Context;

import com.roodie.materialmovies.qualifiers.AppContext;
import com.roodie.materialmovies.qualifiers.FilesDirectory;
import com.roodie.materialmovies.util.AndroidFileManager;
import com.roodie.model.repository.MovieRepository;
import com.roodie.model.repository.ShowRepository;
import com.roodie.model.sqlite.SQLiteHelper;
import com.roodie.model.util.FileManager;

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
                ContextModule.class,
                UtilModule.class
        }
)
public class PersistanceModule {

    @Provides @Singleton
    public FileManager provideFileManager(@FilesDirectory File file) {
        return new AndroidFileManager(file);
    }

    @Provides @Singleton
    public SQLiteHelper getDatabaseHelper(@AppContext Context context) {
        return new SQLiteHelper(context);
    }

    @Provides @Singleton
    public MovieRepository getMovieDatabaseHelper(SQLiteHelper databaseHelper) {
        return new MovieRepository(databaseHelper);
    }

    @Provides @Singleton
    public ShowRepository getShowDatabaseHelper(SQLiteHelper databaseHelper) {
        return new ShowRepository(databaseHelper);
    }


}
