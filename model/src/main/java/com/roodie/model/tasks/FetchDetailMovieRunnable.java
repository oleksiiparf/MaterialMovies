package com.roodie.model.tasks;

import com.roodie.model.state.MoviesState;
import com.uwetrottmann.tmdb.entities.AppendToResponse;
import com.uwetrottmann.tmdb.entities.Movie;
import com.uwetrottmann.tmdb.enumerations.AppendToResponseItem;

import retrofit.RetrofitError;

/**
 * Created by Roodie on 24.06.2015.
 */
public class FetchDetailMovieRunnable extends  BaseMovieRunnable<Movie> {

    private final int mId;

    public FetchDetailMovieRunnable(int callingId, int mId) {
        super(callingId);
        this.mId = mId;
    }

    @Override
    public Movie doBackgroundCall() throws RetrofitError {
        return getTmdbClient().moviesService().summary(mId,
                getCountryProvider().getTwoLetterLanguageCode(),
                new AppendToResponse(
                        AppendToResponseItem.CREDITS,
                        AppendToResponseItem.RELEASES,
                        AppendToResponseItem.VIDEOS,
                        AppendToResponseItem.SIMILAR
                )
        );
    }

    @Override
    public void onSuccess(Movie result) {

        getDbHelper().put(result);
        getEventBus().post(new MoviesState.MovieInformationUpdatedEvent(getCallingId(), result));
    }

    @Override
    public void onError(RetrofitError re) {
        if (re.getResponse() != null && re.getResponse().getStatus() == 404) {
            Movie movie = mMoviesState.getMovie(mId);
            if (movie != null) {
                getDbHelper().put(movie);
                getEventBus()
                        .post(new MoviesState.MovieInformationUpdatedEvent(getCallingId(), movie));
            }
        }
        super.onError(re);
    }
}
