package com.roodie.model.tasks;

import com.roodie.model.entities.MovieWrapper;
import com.roodie.model.state.MoviesState;
import com.roodie.model.util.CountryProvider;
import com.uwetrottmann.tmdb.entities.Movie;
import com.uwetrottmann.tmdb.entities.Releases;

import javax.inject.Inject;

import retrofit.RetrofitError;

/**
 * Created by Roodie on 24.06.2015.
 */
public class FetchMovieReleasesRunnable extends BaseMovieRunnable<Releases>{

    @Inject
    CountryProvider mCountryProvider;
    private final int mId;

    public FetchMovieReleasesRunnable(int callingId, int mId) {
        super(callingId);
        this.mId = mId;
    }

    @Override
    public Releases doBackgroundCall() throws RetrofitError {
        return getTmdbClient().moviesService().releases(mId);
    }

    @Override
    public void onSuccess(Releases result) {
        final String countryCode = mCountryProvider.getTwoLetterCountryCode();
        MovieWrapper movie = mState.getMovie(mId);
        if (movie != null) {

            movie.updateReleases(result, countryCode);

            getDbHelper().put(movie);
            getEventBus().post(new MoviesState.MovieReleasesUpdatedEvent(getCallingId(), movie));
        }

    }
}
