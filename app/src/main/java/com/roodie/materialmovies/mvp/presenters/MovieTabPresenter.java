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
public class MovieTabPresenter extends BasePresenter<MovieTabPresenter.MoviesTabView> {

    private final StringFetcher mStringFetcher;

    @Inject
    public MovieTabPresenter(ApplicationState state,
                             StringFetcher stringFetcher) {
        super(state);
        mStringFetcher = Preconditions.checkNotNull(stringFetcher, "stringFetcher cannot be null");
    }


    @Override
    public void attachView(MoviesTabView view) {
        super.attachView(view);

        if (!getView().isModal()) {
            getView().updateDisplayTitle(mStringFetcher.getString(R.string.movies_title));
        }
    }

    @Override
    public void initialize() {

        populateMovieTabsUi();
    }


    private void populateMovieTabsUi() {
           // mView.setupTabs(UiView.MovieTabs.UPCOMING_MOVIES);
            getView().setupTabs(UiView.MovieTabs.POPULAR, UiView.MovieTabs.IN_THEATRES, UiView.MovieTabs.UPCOMING);

    }

    public interface MoviesTabView extends MovieView {
        void setupTabs(MovieTabs... tabs);
    }
}
