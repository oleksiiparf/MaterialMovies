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
public class ShowTabPresenter extends BasePresenter<ShowTabPresenter.ShowsTabView> {

    private final StringFetcher mStringFetcher;

    @Inject
    public ShowTabPresenter(ApplicationState state,
                             StringFetcher stringFetcher) {
        super(state);
        mStringFetcher = Preconditions.checkNotNull(stringFetcher, "stringFetcher cannot be null");
    }

    @Override
    public void attachView(ShowsTabView view) {
        super.attachView(view);

        if (!view.isModal()) {
            getView().updateDisplayTitle(mStringFetcher.getString(R.string.shows_title));
        }
    }

    @Override
    public void initialize() {

        populateMovieTabsUi();
    }

    private void populateMovieTabsUi() {
        getView().setupTabs(UiView.ShowTabs.POPULAR, UiView.ShowTabs.ON_THE_AIR);

    }

    public interface ShowsTabView extends MovieView {
        void setupTabs(ShowTabs... tabs);
    }
}
