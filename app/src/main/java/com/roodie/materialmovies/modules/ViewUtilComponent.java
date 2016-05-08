package com.roodie.materialmovies.modules;

import android.content.res.AssetManager;

import com.roodie.materialmovies.modules.library.ContextModule;
import com.roodie.materialmovies.modules.library.UtilModule;
import com.roodie.materialmovies.util.FontManager;
import com.roodie.materialmovies.views.custom_views.AutofitTextView;
import com.roodie.materialmovies.views.custom_views.ExpandableTextView;
import com.roodie.materialmovies.views.custom_views.MMoviesButton;
import com.roodie.materialmovies.views.custom_views.MMoviesEditText;
import com.roodie.materialmovies.views.custom_views.MMoviesTextView;
import com.roodie.materialmovies.views.custom_views.MMoviesToolbar;
import com.roodie.materialmovies.views.custom_views.MMoviesToolbarLayout;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Roodie on 16.07.2015.
 */

@Module(
        includes = {
                UtilModule.class,
                ContextModule.class
        }
        ,
        injects = {
                MMoviesTextView.class,
                AutofitTextView.class,
                ExpandableTextView.class,
                MMoviesEditText.class,
                MMoviesButton.class,
                MMoviesToolbar.class,
                MMoviesToolbarLayout.class
        }
)

public class ViewUtilComponent {

        @Provides @Singleton
        public FontManager provideFontManager(AssetManager assetManager) {
                return new FontManager(assetManager);
        }
}
