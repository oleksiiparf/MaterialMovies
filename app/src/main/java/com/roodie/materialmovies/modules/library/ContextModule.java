package com.roodie.materialmovies.modules.library;

import android.content.Context;
import android.content.res.AssetManager;

import com.google.common.base.Preconditions;
import com.roodie.materialmovies.qualifiers.AppContext;
import com.roodie.materialmovies.qualifiers.FilesDirectory;

import java.io.File;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;


@Module(
        library = true
)
public class ContextModule {

    private final Context mAppContext;

    public ContextModule(Context context) {
        mAppContext = Preconditions.checkNotNull(context, "context can not be null");
    }

    @Provides @AppContext
    public Context provideAppContext() {return  mAppContext;}

    @Provides @FilesDirectory
    public File providePrivateFileDirectory() {
        return mAppContext.getFilesDir();
    }

    @Provides @Singleton
    public AssetManager provideAssetManager() {
        return mAppContext.getAssets();
    }
}
