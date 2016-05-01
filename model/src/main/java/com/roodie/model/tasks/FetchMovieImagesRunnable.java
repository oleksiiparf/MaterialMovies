package com.roodie.model.tasks;

import com.roodie.model.entities.MovieWrapper;
import com.roodie.model.state.MoviesState;
import com.roodie.model.util.MoviesCollections;
import com.uwetrottmann.tmdb.entities.Image;
import com.uwetrottmann.tmdb.entities.Images;

import java.util.ArrayList;
import java.util.List;

import retrofit.RetrofitError;

/**
 * Created by Roodie on 24.06.2015.
 */
public class FetchMovieImagesRunnable extends BaseRunnable<Images> {

    private final int mId;

    public FetchMovieImagesRunnable(int callingId, int mId) {
        super(callingId);
        this.mId = mId;
    }

    @Override
    public Images doBackgroundCall() throws RetrofitError {
        return getTmdbClient().moviesService().images(mId);
    }

    @Override
    public void onSuccess(Images result) {
        MovieWrapper movie = mState.getMovie(mId);
        if (movie != null) {
            if (!MoviesCollections.isEmpty(result.backdrops)) {
                List<MovieWrapper.BackdropImage> backdrops = new ArrayList<>();
                for (Image image : result.backdrops) {
                    backdrops.add(new MovieWrapper.BackdropImage(image));
                }
                movie.setBackdropImages(backdrops);
            }

            getEventBus().post(new MoviesState.MovieImagesUpdatedEvent(getCallingId(), movie));

        }
    }

}
