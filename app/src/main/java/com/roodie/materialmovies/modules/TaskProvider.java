package com.roodie.materialmovies.modules;

import com.roodie.materialmovies.modules.library.NetworkProvider;
import com.roodie.materialmovies.modules.library.PersistanceProvider;
import com.roodie.materialmovies.modules.library.StateProvider;
import com.roodie.materialmovies.modules.library.UtilProvider;
import com.roodie.model.tasks.FetchConfigurationRunnable;
import com.roodie.model.tasks.FetchDetailMovieRunnable;
import com.roodie.model.tasks.FetchDetailTvSeasonRunnable;
import com.roodie.model.tasks.FetchDetailTvShowRunnable;
import com.roodie.model.tasks.FetchInTheatresRunnable;
import com.roodie.model.tasks.FetchMovieCreditsRunnable;
import com.roodie.model.tasks.FetchMovieImagesRunnable;
import com.roodie.model.tasks.FetchMovieReleasesRunnable;
import com.roodie.model.tasks.FetchMovieTrailersRunnable;
import com.roodie.model.tasks.FetchOnTheAirShowsRunnable;
import com.roodie.model.tasks.FetchPersonCreditsRunnable;
import com.roodie.model.tasks.FetchPersonRunnable;
import com.roodie.model.tasks.FetchPopularMoviesRunnable;
import com.roodie.model.tasks.FetchPopularShowsRunnable;
import com.roodie.model.tasks.FetchRelatedMoviesRunnable;
import com.roodie.model.tasks.FetchSearchMovieRunnable;
import com.roodie.model.tasks.FetchSearchPeopleResult;
import com.roodie.model.tasks.FetchSearchShowRunnable;
import com.roodie.model.tasks.FetchShowCreditsRunnable;
import com.roodie.model.tasks.FetchUpcomingMoviesRunnable;

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
                FetchPopularMoviesRunnable.class,
                FetchInTheatresRunnable.class,
                FetchUpcomingMoviesRunnable.class,
                FetchRelatedMoviesRunnable.class,
                FetchConfigurationRunnable.class,
                FetchPopularShowsRunnable.class,
                FetchOnTheAirShowsRunnable.class,
                FetchSearchMovieRunnable.class,
                FetchSearchPeopleResult.class,
                FetchSearchShowRunnable.class,
                FetchDetailTvShowRunnable.class,
                FetchDetailTvSeasonRunnable.class,
                FetchShowCreditsRunnable.class

        },
        includes = {
                PersistanceProvider.class,
                StateProvider.class,
                UtilProvider.class,
                NetworkProvider.class
        }
)

public class TaskProvider {
}
