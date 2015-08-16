package com.roodie.materialmovies.views.fragments.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.roodie.materialmovies.R;
import com.roodie.materialmovies.mvp.presenters.ShowGridPresenter;
import com.roodie.materialmovies.views.MMoviesApplication;
import com.roodie.materialmovies.views.adapters.ShowGridAdapter;
import com.roodie.materialmovies.views.custom_views.RecyclerInsetsDecoration;
import com.roodie.model.Display;
import com.roodie.model.entities.ListItem;
import com.roodie.model.entities.ShowWrapper;
import com.roodie.model.network.NetworkError;

import java.util.List;

/**
 * Created by Roodie on 15.08.2015.
 */
public abstract class ShowGridFragment extends BaseGridFragment implements ShowGridPresenter.ShowGridView {

    protected ShowGridPresenter mPresenter;
    private ShowGridAdapter mShowsAdapter;

    private static final String LOG_TAG = ShowGridFragment.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.show_view_recycler, container, false);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mPresenter = MMoviesApplication.from(activity.getApplicationContext()).getShowGridPresenter();
        Log.d(LOG_TAG, "Presenter: " + mPresenter);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mPresenter.detachUi(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_refresh:
                mPresenter.refresh(this);
                return true;
        }
        return false;
    }

    @Override
    public void setItems(List<ListItem<ShowWrapper>> listItems) {
         mShowsAdapter.setItems(listItems);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //set actionbar up navigation
        final Display display = getDisplay();
        if (!isModal()) {
            display.showUpNavigation(getQueryType() != null && getQueryType().showUpNavigation());
        }
        mPresenter.attachUi(this);
    }

    @Override
    public void initializeReceicler() {
        getRecyclerView().addItemDecoration(new RecyclerInsetsDecoration(getActivity().getApplicationContext()));
        mShowsAdapter = new ShowGridAdapter(null);
        mShowsAdapter.setClickListener(this);
        getRecyclerView().setAdapter(mShowsAdapter);
        getRecyclerView().setOnScrollListener(recyclerScrollListener);
    }

    @Override
    protected boolean onScrolledToBottom() {
        if (hasPresenter()) {
            getPresenter().onScrolledToBottom(this);
            return true;
        }
        return false;
    }

    protected final boolean hasPresenter() {
        return mPresenter != null;
    }

    public ShowGridPresenter getPresenter() {
        return mPresenter;
    }

    @Override
    public void updateDisplayTitle(String title) {
    }

    @Override
    public void showError(NetworkError error) {
        setGridShown(true);

        switch (error) {
            case NETWORK_ERROR:
                setEmptyText(getString(R.string.empty_network_error, getTitle()));
                break;
            case UNKNOWN:
                setEmptyText(getString(R.string.empty_unknown_error, getTitle()));
                break;
        }
    }

    @Override
    public String getTitle() {
        if (hasPresenter()) {
            return getPresenter().getUiTitle(this);
        }
        return null;
    }

    @Override
    public void showLoadingProgress(boolean visible) {
        if (visible) {
            setGridShown(false);
        } else {
            setGridShown(true);
        }
    }

    @Override
    public void showSecondaryLoadingProgress(boolean visible) {
        setSecondaryProgressShown(visible);
    }

    @Override
    public String getRequestParameter() {
        return null;
    }

    private RecyclerView.OnScrollListener recyclerScrollListener = new RecyclerView.OnScrollListener() {

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

            super.onScrolled(recyclerView, dx, dy);

            int visibleItemCount    = getRecyclerView().getLayoutManager().getChildCount();
            int totalItemCount      = getRecyclerView().getLayoutManager().getItemCount();
            int pastVisibleItems    = ((GridLayoutManager) getRecyclerView().getLayoutManager())
                    .findFirstVisibleItemPosition();

            if((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                onScrolledToBottom();
            }
        }
    };

    @Override
    public void onClick(View view, int position) {
    }
}
