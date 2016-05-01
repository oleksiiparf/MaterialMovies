package com.roodie.materialmovies.mvp.presenters;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.roodie.materialmovies.MMoviesApp;
import com.roodie.materialmovies.R;
import com.roodie.materialmovies.mvp.views.MoviesTabView;
import com.roodie.materialmovies.mvp.views.UiView;

import java.util.Arrays;

/**
 * Created by Roodie on 02.08.2015.
 */

@InjectViewState
public class TabMoviesPresenter extends MvpPresenter<MoviesTabView> {

    public TabMoviesPresenter() {
        super();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void attachUiToPresenter(MoviesTabView view) {
        super.attachView(view);
        getViewState().updateDisplayTitle(MMoviesApp.get().getStringFetcher().getString(R.string.movies_title));
        populateUi();
    }

    private void populateUi() {
            UiView.MovieTabs[] tabs = {UiView.MovieTabs.POPULAR, UiView.MovieTabs.IN_THEATRES, UiView.MovieTabs.UPCOMING};
            getViewState().setData(Arrays.asList(tabs));
    }

}
