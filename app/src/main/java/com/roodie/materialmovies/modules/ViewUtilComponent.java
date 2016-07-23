package com.roodie.materialmovies.modules;

import android.content.res.AssetManager;

import com.roodie.materialmovies.modules.library.ContextModule;
import com.roodie.materialmovies.modules.library.UtilModule;
import com.roodie.materialmovies.util.FlagUrlProvider;
import com.roodie.materialmovies.util.FontManager;
import com.roodie.materialmovies.views.custom_views.AutofitTextView;
import com.roodie.materialmovies.views.custom_views.ExpandableTextView;
import com.roodie.materialmovies.views.custom_views.MMoviesButton;
import com.roodie.materialmovies.views.custom_views.MMoviesEditText;
import com.roodie.materialmovies.views.custom_views.MMoviesTextView;
import com.roodie.materialmovies.views.custom_views.MMoviesToolbar;
import com.roodie.materialmovies.views.custom_views.MMoviesToolbarLayout;
import com.roodie.materialmovies.views.fragments.MovieDetailFragment;
import com.roodie.materialmovies.views.fragments.TvShowDetailFragment;

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
                MMoviesEditText.class,
                MMoviesButton.class,
                ExpandableTextView.class,
                MMoviesToolbar.class,
                AutofitTextView.class,
                MMoviesToolbarLayout.class,
                MovieDetailFragment.class,
                TvShowDetailFragment.class
        }
)

public class ViewUtilComponent {

        @Provides @Singleton
        public FontManager provideFontManager(AssetManager assetManager) {
                return new FontManager(assetManager);
        }

        @Provides @Singleton
        public FlagUrlProvider getFlagUrlProvider() {
                return new FlagUrlProvider();
        }



}
