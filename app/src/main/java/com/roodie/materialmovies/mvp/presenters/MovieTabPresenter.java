package com.roodie.materialmovies.mvp.presenters;

import com.google.common.base.Preconditions;
import com.roodie.materialmovies.R;
import com.roodie.materialmovies.mvp.views.MovieView;
import com.roodie.materialmovies.mvp.views.UiView;
import com.roodie.model.state.ApplicationState;
import com.roodie.model.util.StringFetcher;

import javax.inject.Inject;

/**
 * Created by Roodie on 02.08.2015.
 */
public class MovieTabPresenter extends BasePresenter {

    MoviesTabView mView;

    private final ApplicationState mState;
    private final StringFetcher mStringFetcher;

    private boolean attached = false;

    @Inject
    public MovieTabPresenter(ApplicationState state,
                             StringFetcher stringFetcher) {
        mState = Preconditions.checkNotNull(state, "state can not be null");
        mStringFetcher = Preconditions.checkNotNull(stringFetcher, "stringFetcher cannot be null");
    }

    public void attachView(MoviesTabView view) {
        Preconditions.checkNotNull(view, "View cannot be null");
        this.mView = view;
        attached = true;

        if (!view.isModal()) {
            mView.updateDisplayTitle(mStringFetcher.getString(R.string.movies_title));
        }
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
