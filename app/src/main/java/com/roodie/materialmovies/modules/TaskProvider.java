package com.roodie.materialmovies.modules;

import com.roodie.materialmovies.modules.library.NetworkProvider;
import com.roodie.materialmovies.modules.library.StateProvider;
import com.roodie.materialmovies.modules.library.UtilProvider;
import com.roodie.model.tasks.FetchDetailMovieRunnable;
import com.roodie.model.tasks.FetchMovieCreditsRunnable;
import com.roodie.model.tasks.FetchMovieImagesRunnable;
import com.roodie.model.tasks.FetchMovieReleasesRunnable;
import com.roodie.model.tasks.FetchMovieTrailersRunnable;
import com.roodie.model.tasks.FetchPersonCreditsRunnable;
import com.roodie.model.tasks.FetchPersonRunnable;
import com.roodie.model.tasks.FetchPoularRunnable;
import com.roodie.model.tasks.FetchRelatedMoviesRunnable;
import com.roodie.model.tasks.FetchConfigurationRunnable;

import dagger.Module;

/**
 * Created by Roodie on 02.07.2015.
 */

@Module (
        injects = {
                FetchDetailMovieRunnable.class,
                FetchMovieCreditsRunnable.class,
                FetchMovieImagesRunnable.class,
                FetchMovieReleasesRunnable.class,
                FetchMovieTrailersRunnable.class,
                FetchPersonCreditsRunnable.class,
                FetchPersonRunnable.class,
                FetchPoularRunnable.class,
                FetchRelatedMoviesRunnable.class,
                FetchConfigurationRunnable.class
        },
        includes = {
                StateProvider.class,
                UtilProvider.class,
                NetworkProvider.class
        }
)

public class TaskProvider {
}