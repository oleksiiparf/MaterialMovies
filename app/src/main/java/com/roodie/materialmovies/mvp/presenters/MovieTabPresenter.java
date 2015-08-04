package com.roodie.materialmovies.mvp.presenters;

import com.google.common.base.Preconditions;
import com.roodie.materialmovies.mvp.views.MovieView;
import com.roodie.materialmovies.mvp.views.UiView;
import com.roodie.model.state.ApplicationState;

import javax.inject.Inject;

/**
 * Created by Roodie on 02.08.2015.
 */
public class MovieTabPresenter extends BasePresenter {

    MoviesTabView mView;

    private final ApplicationState mState;

    private boolean attached = false;

    @Inject
    public MovieTabPresenter(ApplicationState state) {
        mState = Preconditions.checkNotNull(state, "state can not be null");
    }

    public void attachView(MoviesTabView view) {
        Preconditions.checkNotNull(view, "View cannot be null");
        this.mView = view;
        attached = true;
    }

    @Override
    public void initialize() {
        checkViewAlreadySetted();

        populateMovieTabsUi();
    }

    @Override
    public void onResume() {
        mState.registerForEvents(this);
    }

    @Override
    public void onPause() {
        mState.unregisterForEvents(this);
    }

    private void checkViewAlreadySetted() {
        Preconditions.checkState(attached = true, "View not attached");
    }

    private void populateMovieTabsUi() {
           // mView.setupTabs(UiView.MovieTab.UPCOMING);
            mView.setupTabs(UiView.MovieTab.POPULAR, UiView.MovieTab.IN_THEATRES, UiView.MovieTab.UPCOMING);

    }

    public interface MoviesTabView extends MovieView {
        void setupTabs(MovieTab... tabs);
    }
}
