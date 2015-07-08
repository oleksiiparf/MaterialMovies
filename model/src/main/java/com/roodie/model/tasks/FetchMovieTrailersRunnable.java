package com.roodie.model.tasks;

import com.roodie.model.entities.MovieWrapper;
import com.roodie.model.state.BaseState;
import com.roodie.model.state.MoviesState;
import com.uwetrottmann.tmdb.entities.Movie;
import com.uwetrottmann.tmdb.entities.Videos;

import retrofit.RetrofitError;

/**
 * Created by Roodie on 24.06.2015.
 */
public class FetchMovieTrailersRunnable extends BaseMovieRunnable<Videos> {

    private final int mId;

    public FetchMovieTrailersRunnable(int callingId, int mId) {
        super(callingId);
        this.mId = mId;
    }

    @Override
    public Videos doBackgroundCall() throws RetrofitError {
        return getTmdbClient().moviesService().videos(mId, getCountryProvider().getTwoLetterLanguageCode());
    }

    @Override
    public void onSuccess(Videos result) {
        MovieWrapper movie = mMoviesState.getMovie(mId);

        if (movie != null) {
            movie.updateVideos(result);

            getEventBus().post(new MoviesState.MovieVideosItemsUpdatedEvent(getCallingId(), movie));
        }
    }

    @Override
    public void onError(RetrofitError re) {
        super.onError(re);

        MovieWrapper movie = mMoviesState.getMovie(mId);
        if (movie != null) {
            getEventBus().post(new MoviesState.MovieVideosItemsUpdatedEvent(getCallingId(), movie));
        }
    }

    @Override
    protected Object createLoadingProgressEvent(boolean show) {
        return new BaseState.ShowVideosLoadingProgressEvent(getCallingId(), show);
    }
}
