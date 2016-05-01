package com.roodie.model.tasks;

import com.roodie.model.entities.MovieWrapper;
import com.roodie.model.state.BaseState;
import com.roodie.model.state.MoviesState;
import com.uwetrottmann.tmdb.entities.Movie;
import com.uwetrottmann.tmdb.entities.MovieResultsPage;

import java.util.ArrayList;
import java.util.List;

import retrofit.RetrofitError;

/**
 * Created by Roodie on 24.06.2015.
 */
public class FetchRelatedMoviesRunnable extends BaseRunnable<MovieResultsPage> {

    private final int mId;

    public FetchRelatedMoviesRunnable(int callingId, int mId) {
        super(callingId);
        this.mId = mId;
    }

    @Override
    public MovieResultsPage doBackgroundCall() throws RetrofitError {
        return getTmdbClient().moviesService().similarMovies(mId,
                null,
                getCountryProvider().getTwoLetterLanguageCode());
    }

    @Override
    public void onSuccess(MovieResultsPage result) {
        MovieWrapper movie = mState.getMovie(String.valueOf(mId));

        if (movie != null) {
            List<MovieWrapper> movies = new ArrayList<>(result.results.size());
            for (Movie mMovie: result.results) {
                movies.add(getEntityMapper().map(mMovie));
            }
            movie.setRelated(movies);
            getEventBus().post(new MoviesState.MovieRelatedItemsUpdatedEvent(
                    getCallingId(), movie));
        }
    }

    @Override
    public void onError(RetrofitError re) {
        super.onError(re);

        MovieWrapper movie = mState.getMovie(String.valueOf(mId));
        if (movie != null) {
            getEventBus().post(new MoviesState.MovieRelatedItemsUpdatedEvent(
                    getCallingId(), movie));
        }
    }

    @Override
    protected Object createLoadingProgressEvent(boolean show) {
        return new BaseState.ShowRelatedLoadingProgressEvent(getCallingId(),show);
    }
}
