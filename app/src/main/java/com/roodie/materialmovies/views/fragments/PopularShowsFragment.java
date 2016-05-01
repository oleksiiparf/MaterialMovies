package com.roodie.materialmovies.views.fragments;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.roodie.materialmovies.mvp.presenters.GridTvShowsPresenter;
import com.roodie.materialmovies.views.fragments.base.TvShowsGridFragment;
import com.roodie.model.Display;

/**
 * Created by Roodie on 14.08.2015.
 */

public class PopularShowsFragment extends TvShowsGridFragment {

    @InjectPresenter
    GridTvShowsPresenter mPresenter;

    @Override
    protected void attachUiToPresenter() {
        mPresenter.onUiAttached(this, getQueryType(), null);
        Display display = getDisplay();
        if ( display != null) {
            display.showUpNavigation(getQueryType() != null && getQueryType().showUpNavigation());
        }
    }

    @Override
    public void onScrolledToBottom() {
        super.onScrolledToBottom();
        mPresenter.onScrolledToBottom(this, getQueryType());
    }

    @Override
    public void onRefreshData(boolean visible) {
        super.onRefreshData(visible);
        mPresenter.refresh(this, getQueryType());

    }

    @Override
    public MMoviesQueryType getQueryType() {
        return MMoviesQueryType.POPULAR_SHOWS;
    }
}
