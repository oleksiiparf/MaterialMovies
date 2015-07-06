package com.roodie.materialmovies.mvp.presenters;

import com.roodie.materialmovies.mvp.views.MainView;
import com.roodie.materialmovies.mvp.views.UiView;
import com.roodie.model.controllers.DrawerMenuItem;

/**
 * Created by Roodie on 25.06.2015.
 */
public class TrendingMoviesPresenter extends  BasePresenter {

    private TrendingMoviesView mTrendingMoviesView;

    public TrendingMoviesPresenter() {
    }


    @Override
    public void onResume() {

    }

    @Override
    protected void onInited() {
        super.onInited();
    }

    @Override
    protected void onPaused() {
        super.onPaused();
    }

    public interface TrendingMoviesView  extends UiView {

    }



}
