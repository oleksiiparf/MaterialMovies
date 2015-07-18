package com.roodie.materialmovies.mvp.presenters;

import android.os.Bundle;

import com.google.common.base.Preconditions;
import com.roodie.materialmovies.mvp.views.MovieView;
import com.roodie.materialmovies.qualifiers.GeneralPurpose;
import com.roodie.model.entities.MovieWrapper;
import com.roodie.model.entities.PersonCreditWrapper;
import com.roodie.model.state.ApplicationState;
import com.roodie.model.state.AsyncDatabaseHelper;
import com.roodie.model.util.BackgroundExecutor;
import com.roodie.model.util.Injector;

import javax.inject.Inject;

/**
 * Created by Roodie on 25.06.2015.
 */
public class MovieDetailPresenter extends BasePresenter {

    private static final int TMDB_FIRST_PAGE = 1;

    private final BackgroundExecutor mExecutor;
    private final AsyncDatabaseHelper mDbHelper;
    private MovieDetailView mMoviesView;
    private final ApplicationState mState;
    private final Injector mInjector;

    private boolean attached = false;

    @Inject
    public MovieDetailPresenter(
            @GeneralPurpose BackgroundExecutor executor,
            AsyncDatabaseHelper dbHelper, ApplicationState state, Injector injector) {
        super();
        mExecutor = Preconditions.checkNotNull(executor, "executor can not be null");
        mDbHelper = Preconditions.checkNotNull(dbHelper, "executor can not be null");
        mState = Preconditions.checkNotNull(state, "application state cannot be null");
        mInjector = Preconditions.checkNotNull(injector, "injector cannot be null");
    }

    @Override
    public void initialize() {
        checkViewAlreadySetted();
    }

    public void attachView (MovieDetailView view) {
        Preconditions.checkNotNull(view, "View cannot be null");
        this.mMoviesView = view;
        attached = true;
        mState.registerForEvents(this);
    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {
        mState.unregisterForEvents(this);
    }

    public void refresh() {}



    private void checkViewAlreadySetted() {
        Preconditions.checkState(attached = true, "View not attached");
    }



    public interface MovieDetailView extends MovieView {

        void setMovie(MovieWrapper movie);

        void showMovieDetail(MovieWrapper movie, Bundle bundle);

        void showMovieDetail(PersonCreditWrapper credit, Bundle bundle);

        void showRelatedMovies(MovieWrapper movie);

    }
}
