package com.roodie.materialmovies.views.fragments;


import com.arellomobile.mvp.presenter.InjectPresenter;
import com.roodie.materialmovies.mvp.presenters.ListMoviesPresenter;
import com.roodie.materialmovies.views.fragments.base.MoviesGridFragment;
import com.roodie.model.Display;


/**
 * Created by Roodie on 09.07.2015.
 */

public class PopularMoviesFragment extends MoviesGridFragment {

    @InjectPresenter
    ListMoviesPresenter mMoviesPresenter;

    @Override
    protected void attachUiToPresenter() {
        mMoviesPresenter.onUiAttached(this, getQueryType(), null);
        Display display = getDisplay();
        if ( display != null) {
            display.showUpNavigation(getQueryType() != null && getQueryType().showUpNavigation());
        }
    }

    @Override
    public void onScrolledToBottom() {
        super.onScrolledToBottom();
        mMoviesPresenter.onScrolledToBottom(this, getQueryType());
    }

    @Override
    public void onRefreshData(boolean visible) {
        super.onRefreshData(visible);
        mMoviesPresenter.refresh(this, getQueryType());
    }

    @Override
    public MMoviesQueryType getQueryType() {
        return MMoviesQueryType.POPULAR_MOVIES;
    }



}
