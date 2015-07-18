package com.roodie.materialmovies.modules.library;

import android.content.Context;

import com.google.common.base.Preconditions;
import com.roodie.materialmovies.qualifiers.AppContext;
import com.roodie.materialmovies.qualifiers.FilesDirectory;

import java.io.File;

import dagger.Module;
import dagger.Provides;


@Module (
        library = true
)
public class ContextProvider {

    private final Context mAppContext;

    public ContextProvider(Context context) {
        mAppContext = Preconditions.checkNotNull(context, "context can not be null");
    }

    @Provides @AppContext
    public Context provideAppContext() {return  mAppContext;}

    @Provides @FilesDirectory
    public File providePrivateFileDirectory() {
        return mAppContext.getFilesDir();
    }

}
