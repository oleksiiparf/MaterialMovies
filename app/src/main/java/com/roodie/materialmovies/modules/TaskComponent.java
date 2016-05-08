package com.roodie.materialmovies.modules;

import com.roodie.materialmovies.modules.library.ContextModule;
import com.roodie.materialmovies.modules.library.NetworkModule;
import com.roodie.materialmovies.modules.library.PersistanceModule;
import com.roodie.materialmovies.modules.library.StateModule;
import com.roodie.materialmovies.modules.library.UtilModule;
import com.roodie.model.tasks.ClearWatchedRunnable;
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
import com.roodie.model.tasks.FetchSearchPeopleRunnable;
import com.roodie.model.tasks.FetchSearchShowRunnable;
import com.roodie.model.tasks.FetchShowCreditsRunnable;
import com.roodie.model.tasks.FetchUpcomingMoviesRunnable;
import com.roodie.model.tasks.FetchWatchedRunnable;
import com.roodie.model.tasks.MarkEntitySeenRunnable;
import com.roodie.model.tasks.MarkEntityUnseenRunnable;

import dagger.Module;

/**
 * Created by Roodie on 02.07.2015.
 */

@Module(
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
                FetchSearchPeopleRunnable.class,
                FetchSearchShowRunnable.class,
                FetchDetailTvShowRunnable.class,
                FetchDetailTvSeasonRunnable.class,
                FetchShowCreditsRunnable.class,
                FetchWatchedRunnable.class,
                MarkEntitySeenRunnable.class,
                MarkEntityUnseenRunnable.class,
                ClearWatchedRunnable.class

        },
        includes = {
                ContextModule.class,
                PersistanceModule.class,
                StateModule.class,
                UtilModule.class,
                NetworkModule.class
        }
)

public class TaskComponent {
}
