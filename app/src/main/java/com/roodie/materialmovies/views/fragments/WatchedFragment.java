package com.roodie.materialmovies.views.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.google.common.base.Preconditions;
import com.marshalchen.ultimaterecyclerview.quickAdapter.easyRegularAdapter;
import com.roodie.materialmovies.R;
import com.roodie.materialmovies.mvp.presenters.ListWatchedPresenter;
import com.roodie.materialmovies.mvp.views.WatchedListView;
import com.roodie.materialmovies.views.activities.BaseNavigationActivity;
import com.roodie.materialmovies.views.adapters.WatchedGridAdapter;
import com.roodie.materialmovies.views.custom_views.recyclerview.RecyclerInsetsDecoration;
import com.roodie.materialmovies.views.fragments.base.BaseGridFragment;
import com.roodie.model.Display;
import com.roodie.model.entities.Watchable;

import java.util.List;

/**
 * Created by Roodie on 06.03.2016.
 */

public class WatchedFragment extends BaseGridFragment<WatchedGridAdapter.WatchedItemViewHolder, Watchable> implements WatchedListView {

    @InjectPresenter
    ListWatchedPresenter mPresenter;

    @Override
    public void onClick(View view, int position) {
        Watchable item = mAdapter.getObjects().get(position);
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
        getRecyclerView().setHasFixedSize(true);
        getRecyclerView().addItemDecoration(new RecyclerInsetsDecoration(getActivity(), NavigationGridType.WATCHED));
        getRecyclerView().disableLoadmore();
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
    protected easyRegularAdapter<Watchable, WatchedGridAdapter.WatchedItemViewHolder> createAdapter(List<Watchable> data) {
        return new WatchedGridAdapter(data, this);
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

    @Override
    public void onEmptyViewShow(View mView) {
        ImageView icon = (ImageView) mView.findViewById(R.id.empty_screen_icon);
        icon.setImageResource(R.drawable.ic_heart_white_96dp);
        TextView title = (TextView) mView.findViewById(R.id.empty_screen_title);
        title.setText(R.string.empty_library_title);
        TextView body = (TextView) mView.findViewById(R.id.empty_screen_body);
        body.setText(R.string.empty_library_body);
        TextView action = (TextView) mView.findViewById(R.id.empty_screen_action);
        action.setText(R.string.explore);
        action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    ((BaseNavigationActivity)getActivity()).setMoviesItemNavigationView();
            }
        });
    }
}
