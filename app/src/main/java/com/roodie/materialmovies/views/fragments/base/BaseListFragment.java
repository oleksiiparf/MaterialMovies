package com.roodie.materialmovies.views.fragments.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.marshalchen.ultimaterecyclerview.UltimateRecyclerviewViewHolder;
import com.marshalchen.ultimaterecyclerview.layoutmanagers.ScrollSmoothLineaerLayoutManager;
import com.roodie.materialmovies.R;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Roodie on 12.02.2016.
 */
public abstract class BaseListFragment<VH extends UltimateRecyclerviewViewHolder, M extends Serializable>
        extends RecyclerFragment<VH, M> {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem search = menu.findItem(R.id.menu_search);
        if (search != null)
            search.setVisible(false);
        MenuItem refresh = menu.findItem(R.id.menu_refresh);
        if (refresh != null)
            refresh.setVisible(false);
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_list_recycler_with_toolbar;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //getRecyclerView().enableLoadMoreView(false);
        getRecyclerView().setHasFixedSize(true);
        getRecyclerView().setLayoutManager(new ScrollSmoothLineaerLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false, 300));
        enableEmptyViewPolicy();
        mAdapter = createAdapter(new ArrayList<M>());
        mUltimateRecyclerView.setAdapter(mAdapter);
        //mRecyclerView.addItemDecoration(new RecyclerInsetsDecoration(getActivity()));
    }

    @Override
    public void onEmptyViewShow(View mView) {

    }
}
