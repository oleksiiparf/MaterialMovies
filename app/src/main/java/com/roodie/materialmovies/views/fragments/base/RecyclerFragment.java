package com.roodie.materialmovies.views.fragments.base;


import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.roodie.materialmovies.R;
import com.roodie.materialmovies.mvp.views.BaseListView;
import com.roodie.materialmovies.mvp.views.MvpLceView;
import com.roodie.materialmovies.views.adapters.FooterViewListAdapter;
import com.roodie.materialmovies.views.custom_views.recyclerview.BaseRecyclerLayout;
import com.roodie.materialmovies.views.listeners.RecyclerItemClickListener;
import com.roodie.model.Display;
import com.roodie.model.network.NetworkError;
import com.roodie.model.util.FileLog;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Roodie on 12.12.2015.
 */
public abstract class RecyclerFragment<VH extends RecyclerView.ViewHolder, M extends List<? extends Serializable>, V extends MvpLceView<M>>
        extends BaseMvpFragment implements BaseListView<M>, RecyclerItemClickListener {

    protected BaseRecyclerLayout mPrimaryRecyclerView;
    protected FooterViewListAdapter<M, VH> mAdapter = null;

    protected abstract FooterViewListAdapter<M, VH> createAdapter();

    protected BaseRecyclerLayout getRecyclerView() {
        return mPrimaryRecyclerView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mPrimaryRecyclerView = (BaseRecyclerLayout) view.findViewById(R.id.primary_recycler_view);
        mPrimaryRecyclerView.setHasFixedSize(true);
    }

    @Override
    public void setData(M data) {
        if (data != null) {
            mPrimaryRecyclerView.setAdapter(getAdapter());
            getAdapter().setItems(data);
            getAdapter().notifyDataSetChanged();
        }
    }

    public boolean hasAdapter() {
        return mAdapter != null;
    }
    @Override
    public void updateDisplayTitle(String title) {
        Display display = getDisplay();
        if (display != null) {
            display.setActionBarTitle(title);
        }
    }

    @Override
    public void updateDisplaySubtitle(String subtitle) {
        Display display = getDisplay();
        if (display != null) {
            display.setActionBarSubtitle(subtitle);
        }
    }

    @Override
    public void onScrolledToBottom() {
    }

    @Override
    public void showError(NetworkError error) {
        FileLog.d("lce", "RecyclerFragment: showError()");
        switch (error) {
            case NETWORK_ERROR:
                mPrimaryRecyclerView.setErrorText(getString(R.string.empty_network_error));
                break;
            case UNKNOWN:
                mPrimaryRecyclerView.setErrorText(getString(R.string.error_no_connection_body));
                break;
        }
    }

    @Override
    public void showLoadingProgress(boolean visible) {
        if (visible) {
            mPrimaryRecyclerView.setContentShown(false);
        } else {
            mPrimaryRecyclerView.setContentShown(true);
        }
    }

    @Override
    public void onRefreshData(boolean visible) {
        FileLog.d("lce", "RecyclerFragment: onRefreshData()");
        if (visible) {
            mPrimaryRecyclerView.setContentShown(false);
        } else {
            mPrimaryRecyclerView.setContentShown(true, false);
        }
    }

    public FooterViewListAdapter<M, VH> getAdapter() {
        if (mAdapter == null) {
            mAdapter = createAdapter();
        }
        return mAdapter;
    }
}
