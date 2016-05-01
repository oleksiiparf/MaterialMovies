package com.roodie.materialmovies.views.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.google.common.base.Preconditions;
import com.roodie.materialmovies.R;
import com.roodie.materialmovies.mvp.presenters.ListWatchedPresenter;
import com.roodie.materialmovies.mvp.views.WatchedListView;
import com.roodie.materialmovies.views.adapters.FooterViewListAdapter;
import com.roodie.materialmovies.views.adapters.WatchedGridAdapter;
import com.roodie.materialmovies.views.fragments.base.BaseGridFragment;
import com.roodie.model.Display;
import com.roodie.model.entities.Watchable;

import java.util.List;

/**
 * Created by Roodie on 06.03.2016.
 */

public class WatchedFragment extends BaseGridFragment<WatchedGridAdapter.WatchedItemViewHolder, List<Watchable>, WatchedListView> implements WatchedListView {

    @InjectPresenter
    ListWatchedPresenter mPresenter;

    @Override
    public void onClick(View view, int position) {
        Watchable item = mAdapter.getItems().get(position);
        showWatchableDetail(item, view);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_grid_recycler_with_toolbar;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPopupMenuClick(View view, int position) {
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getRecyclerView().disableLoadMore();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_search:
                return false;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void showWatchableDetail(Watchable item, View container) {
        Preconditions.checkNotNull(item, "Watchable cannot be null");
        Preconditions.checkNotNull(item.getTmdbId(), "watchable id cannot be null");

        Display display = getDisplay();
        if (display != null) {
            switch (item.getWatchableType()) {
                case MOVIE : {
                    display.startMovieDetailActivity(String.valueOf(item.getTmdbId()), null);
                    break;
                }
                case TV_SHOW: {
                    display.startTvDetailActivity(String.valueOf(item.getTmdbId()), null);
                    break;
                }
            }

        }
    }

    @Override
    protected FooterViewListAdapter<List<Watchable>, WatchedGridAdapter.WatchedItemViewHolder> createAdapter() {
        return new WatchedGridAdapter(getActivity(), this);
    }

    @Override
    protected void attachUiToPresenter() {
        mPresenter.onUiAttached(this, getQueryType(), null);
        Display display = getDisplay();
        if ( display != null) {
            display.showUpNavigation(getQueryType() != null && getQueryType().showUpNavigation());
        }

    }

    @Override
    public MMoviesQueryType getQueryType() {
        return MMoviesQueryType.WATCHED;
    }


}
