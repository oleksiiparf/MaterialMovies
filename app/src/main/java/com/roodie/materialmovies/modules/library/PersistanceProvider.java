package com.roodie.materialmovies.modules.library;

import com.roodie.materialmovies.qualifiers.FilesDirectory;
import com.roodie.materialmovies.util.AndroidFileManager;
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
                ContextProvider.class,
                UtilProvider.class
        }


)

public class PersistanceProvider {

    @Provides @Singleton
    public FileManager provideFileManager(@FilesDirectory File file) {
        return new AndroidFileManager(file);
    }

}
