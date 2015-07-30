package com.roodie.materialmovies.mvp.presenters;

import com.google.common.base.Preconditions;
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
import com.squareup.otto.Subscribe;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by Roodie on 06.07.2015.
 */
public class MovieImagesPresenter extends BasePresenter {

    private MovieImagesView mView;

    private final ApplicationState mState;
    private final BackgroundExecutor mExecutor;
    private final Injector mInjector;

    private boolean attached = false;

    @Inject
    public MovieImagesPresenter(ApplicationState applicationState,
                                @GeneralPurpose BackgroundExecutor executor,
                                Injector injector) {
        mState = Preconditions.checkNotNull(applicationState, "mState can not be null");
        mExecutor = Preconditions.checkNotNull(executor, "executor cannot be null");
        mInjector = Preconditions.checkNotNull(injector, "injector cannot be null");
    }

    @Subscribe
    public void onMovieImagesChanged(MoviesState.MovieImagesUpdatedEvent event) {
        populateUi(event);
    }

    @Override
    public void onResume() {
        mState.registerForEvents(this);
    }

    @Override
    public void initialize() {

        checkViewAlreadySetted();

        fetchMovieImagesIfNeeded(5,mView.getRequestParameter());
    }

    @Override
    public void onPause() {
        mState.unregisterForEvents(this);
    }

    public void attachView (MovieImagesView view) {
        Preconditions.checkNotNull(view, "View cannot be null");
        this.mView = view;
        attached = true;
    }

    private void checkViewAlreadySetted() {
        Preconditions.checkState(attached = true, "View not attached");
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
        final MovieWrapper movie = mState.getMovie(mView.getRequestParameter());

        if (movie != null && !MoviesCollections.isEmpty(movie.getBackdropImages())) {
            mView.setItems(Collections.unmodifiableList(movie.getBackdropImages()));
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
