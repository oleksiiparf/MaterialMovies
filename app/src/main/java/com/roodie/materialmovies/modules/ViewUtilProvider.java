package com.roodie.materialmovies.modules;

import android.content.res.AssetManager;

import com.roodie.materialmovies.modules.library.ContextProvider;
import com.roodie.materialmovies.modules.library.PersistanceProvider;
import com.roodie.materialmovies.modules.library.UtilProvider;
import com.roodie.materialmovies.util.MMoviesServiceUtils;
import com.roodie.materialmovies.util.TypefaceManager;
import com.roodie.materialmovies.views.adapters.MovieGridAdapter;
import com.roodie.materialmovies.views.custom_views.AutofitTextView;
import com.roodie.materialmovies.views.custom_views.ExpandableTextView;
import com.roodie.materialmovies.views.custom_views.MMoviesTextView;
import com.roodie.model.util.AndroidPreferences;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Roodie on 16.07.2015.
 */

@Module(
        includes = {
                UtilProvider.class,
                ContextProvider.class
        },
        injects = {
                MMoviesTextView.class,
                AutofitTextView.class,
                ExpandableTextView.class
        }
)

public class ViewUtilProvider {

        @Provides @Singleton
        public TypefaceManager provideTypefaceManager(AssetManager assetManager) {
                return new TypefaceManager(assetManager);
        }

}
