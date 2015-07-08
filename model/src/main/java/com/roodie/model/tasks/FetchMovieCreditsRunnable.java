package com.roodie.model.tasks;

import com.roodie.model.entities.MovieCreditWrapper;
import com.roodie.model.entities.MovieWrapper;
import com.roodie.model.state.BaseState;
import com.roodie.model.state.MoviesState;
import com.roodie.model.util.MoviesCollections;
import com.uwetrottmann.tmdb.entities.Credits;

import java.util.Collections;
import java.util.List;

import retrofit.RetrofitError;

/**
 * Created by Roodie on 24.06.2015.
 */
public class FetchMovieCreditsRunnable extends BaseMovieRunnable<Credits> {

    private final int mId;

    public FetchMovieCreditsRunnable(int callingId, int mId) {
        super(callingId);
        this.mId = mId;
    }

    @Override
    public Credits doBackgroundCall() throws RetrofitError {
        return getTmdbClient().moviesService().credits(mId);
    }

    @Override
    public void onSuccess(Credits result) {
        MovieWrapper movie = mMoviesState.getMovie(mId);

        if (movie != null) {
            if (!MoviesCollections.isEmpty(result.cast)) {
                //Cast list should be mapped due to entity mapper
                List<MovieCreditWrapper> cast = getEntityMapper().mapCredits(result.cast);
                Collections.sort(cast);
                movie.setCast(cast);
            }

            if (!MoviesCollections.isEmpty(result.crew)) {
                //Crew list should be mapped due to entity mapper
                List<MovieCreditWrapper> crew = getEntityMapper().mapCredits(result.crew);
                Collections.sort(crew);
                movie.setCrew(crew);
            }

            getEventBus().post(new MoviesState.MovieCastItemsUpdatedEvent(getCallingId(), movie));
        }
    }


    @Override
    public void onError(RetrofitError re) {
        super.onError(re);

        MovieWrapper movie  = mMoviesState.getMovie(mId);
        if (movie != null) {
            getEventBus().post(new MoviesState.MovieCastItemsUpdatedEvent(getCallingId(), movie));
        }
    }

    @Override
    protected Object createLoadingProgressEvent(boolean show) {
        return new BaseState.ShowCreditLoadingProgressEvent(getCallingId(), show);
    }
}
