package com.roodie.materialmovies.mvp.presenters;

import android.util.Log;

import com.google.common.base.Preconditions;
import com.roodie.materialmovies.R;
import com.roodie.materialmovies.mvp.views.MovieView;
import com.roodie.materialmovies.qualifiers.GeneralPurpose;
import com.roodie.model.entities.MovieWrapper;
import com.roodie.model.state.ApplicationState;
import com.roodie.model.state.MoviesState;
import com.roodie.model.tasks.BaseMovieRunnable;
import com.roodie.model.tasks.FetchMovieImagesRunnable;
import com.roodie.model.util.BackgroundExecutor;
import com.roodie.model.util.Injector;
import com.roodie.model.util.MoviesCollections;
import com.roodie.model.util.StringFetcher;
import com.squareup.otto.Subscribe;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by Roodie on 06.07.2015.
 */
public class MovieImagesPresenter extends BasePresenter<MovieImagesPresenter.MovieImagesView> {

    private static final String LOG_TAG = MovieImagesPresenter.class.getSimpleName();

    private final BackgroundExecutor mExecutor;
    private final Injector mInjector;
    private final StringFetcher mFetcher;

    @Inject
    public MovieImagesPresenter(ApplicationState applicationState,
                                @GeneralPurpose BackgroundExecutor executor,
                                Injector injector,
                                StringFetcher fetcher) {
        super(applicationState);
        mExecutor = Preconditions.checkNotNull(executor, "executor cannot be null");
        mInjector = Preconditions.checkNotNull(injector, "injector cannot be null");
        mFetcher = Preconditions.checkNotNull(fetcher, "fetcher cannot be null");
    }

    @Subscribe
    public void onMovieImagesChanged(MoviesState.MovieImagesUpdatedEvent event) {
        Log.d(LOG_TAG, "On movie images received");
        populateUi(event);
    }

    @Override
    public void initialize() {

        fetchMovieImagesIfNeeded(5, getView().getRequestParameter());
    }

    @Override
    public void attachView(MovieImagesView view) {
        super.attachView(view);
        getView().updateDisplayTitle(mFetcher.getString(R.string.images_movies));
    }

    @Override
    public void detachView(boolean retainInstance) {
        super.detachView(retainInstance);
    }

    private void fetchMovieImagesIfNeeded(final int callingId, String id) {
        Preconditions.checkNotNull(id, "id cannot be null");

        MovieWrapper movie = mState.getMovie(id);
        if (movie != null && MoviesCollections.isEmpty(movie.getBackdropImages())) {
            fetchMovieImages(callingId, id);
        }
    }

    private void fetchMovieImages(final int callingId, String id) {
        Preconditions.checkNotNull(id, "id cannot be null");

        MovieWrapper movie = mState.getMovie(id);
        if (movie != null && movie.getTmdbId() != null) {
            executeTask(new FetchMovieImagesRunnable(callingId, movie.getTmdbId()));
        }
    }

    private void populateUi(MoviesState.MovieImagesUpdatedEvent event) {

        Preconditions.checkNotNull(event, "Event cannot be null");
        final MovieWrapper movie = mState.getMovie(getView().getRequestParameter());

        if (movie != null && !MoviesCollections.isEmpty(movie.getBackdropImages())) {
            getView().setItems(Collections.unmodifiableList(movie.getBackdropImages()));
        }
    }

    private <R> void executeTask(BaseMovieRunnable<R> task) {
        mInjector.inject(task);
        mExecutor.execute(task);
    }

    public interface MovieImagesView extends MovieView {
        void setItems(List<MovieWrapper.BackdropImage> images);
    }

}
