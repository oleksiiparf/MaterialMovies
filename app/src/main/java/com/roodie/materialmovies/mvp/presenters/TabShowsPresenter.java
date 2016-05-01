package com.roodie.materialmovies.mvp.presenters;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.roodie.materialmovies.MMoviesApp;
import com.roodie.materialmovies.R;
import com.roodie.materialmovies.mvp.views.TvShowsTabView;
import com.roodie.materialmovies.mvp.views.UiView;

import java.util.Arrays;

/**
 * Created by Roodie on 14.08.2015.
 */

@InjectViewState
public class TabShowsPresenter extends MvpPresenter<TvShowsTabView> {

    public TabShowsPresenter() {
        super();
    }

    public void attachUiToPresenter(TvShowsTabView view) {
        super.attachView(view);
        getViewState().updateDisplayTitle(MMoviesApp.get().getStringFetcher().getString(R.string.shows_title));
        populateUi();
    }

    private void populateUi() {
        UiView.ShowTabs[] tabs = {UiView.ShowTabs.POPULAR, UiView.ShowTabs.ON_THE_AIR};
        getViewState().setData(Arrays.asList(tabs));

    }


}
