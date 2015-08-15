package com.roodie.materialmovies.mvp.presenters;

import com.google.common.base.Preconditions;
import com.roodie.materialmovies.R;
import com.roodie.materialmovies.mvp.views.MovieView;
import com.roodie.materialmovies.mvp.views.UiView;
import com.roodie.model.state.ApplicationState;
import com.roodie.model.util.StringFetcher;

import javax.inject.Inject;

/**
 * Created by Roodie on 14.08.2015.
 */
public class ShowTabPresenter extends BasePresenter {

    ShowsTabView mView;

    private final ApplicationState mState;
    private final StringFetcher mStringFetcher;

    private boolean attached = false;

    @Inject
    public ShowTabPresenter(ApplicationState state,
                             StringFetcher stringFetcher) {
        mState = Preconditions.checkNotNull(state, "state can not be null");
        mStringFetcher = Preconditions.checkNotNull(stringFetcher, "stringFetcher cannot be null");
    }

    public void attachView(ShowsTabView view) {
        Preconditions.checkNotNull(view, "View cannot be null");
        this.mView = view;
        attached = true;

        if (!view.isModal()) {
            mView.updateDisplayTitle(mStringFetcher.getString(R.string.shows_title));
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
        mView.setupTabs(UiView.ShowTabs.POPULAR, UiView.ShowTabs.ON_THE_AIR);

    }

    public interface ShowsTabView extends MovieView {
        void setupTabs(ShowTabs... tabs);
    }
}
