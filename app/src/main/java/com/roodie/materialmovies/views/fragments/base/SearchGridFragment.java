package com.roodie.materialmovies.views.fragments.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.roodie.materialmovies.R;
import com.roodie.materialmovies.mvp.presenters.SearchPresenter;
import com.roodie.materialmovies.views.MMoviesApplication;
import com.roodie.materialmovies.views.custom_views.RecyclerInsetsDecoration;
import com.roodie.model.Display;
import com.roodie.model.entities.BasicWrapper;
import com.roodie.model.entities.MovieWrapper;
import com.roodie.model.entities.PersonWrapper;
import com.roodie.model.entities.ShowWrapper;
import com.roodie.model.network.NetworkError;
import com.roodie.model.state.MoviesState;


/**
 * Created by Roodie on 22.08.2015.
 */
public abstract class SearchGridFragment<D extends BasicWrapper, E extends RecyclerView.Adapter> extends BaseGridFragment implements SearchPresenter.SearchView<D> {

    protected static final String LOG_TAG = SearchGridFragment.class.getSimpleName();

    protected E mAdapter;
    protected SearchPresenter mPresenter;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mPresenter = MMoviesApplication.from(activity.getApplicationContext()).getSearchPresenter();
    }

    @Override
    public void onPause() {
        super.onPause();
        mPresenter.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.onResume();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mPresenter.detachView(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.movie_view_recycler, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //set actionbar up navigation
        final Display display = getDisplay();
        if (!isModal()) {
            display.showUpNavigation(getQueryType() != null && getQueryType().showUpNavigation());
        }
        mPresenter.attachView(this);
        mPresenter.initialize();
    }

    @Override
    public void initializeRecycler() {
        Log.d(LOG_TAG, "Initialize recycler");
        getRecyclerView().addItemDecoration(new RecyclerInsetsDecoration(getActivity().getApplicationContext()));
        mAdapter = createAdapter();
        getRecyclerView().setAdapter(mAdapter);
        getRecyclerView().setOnScrollListener(recyclerScrollListener);
    }

    @Override
    protected boolean onScrolledToBottom() {
        if (hasPresenter()) {
            getPresenter().onScrolledToBottom();
            return true;
        }
        return false;
    }

    protected final boolean hasPresenter() {
        return mPresenter != null;
    }

    public SearchPresenter getPresenter() {
        return mPresenter;
    }

    protected abstract E createAdapter();

    public E getAdapter() {
        return mAdapter;
    }

    @Override
    public boolean isModal() {
        return false;
    }

    @Override
    public void updateDisplayTitle(String title) {

    }

    @Override
    public String getRequestParameter() {
        return null;
    }

    @Override
    public void showSecondaryLoadingProgress(boolean visible) {
        setSecondaryProgressShown(visible);

    }

    @Override
    public void showLoadingProgress(boolean visible) {
        Log.d(LOG_TAG, "show loading progress");
        if (visible) {
            setGridShown(false);
        } else {
            setGridShown(true);
        }

    }

    @Override
    public void showError(NetworkError error) {
        Log.d(LOG_TAG, "show error");
        setGridShown(true);
        switch (error) {
            case NETWORK_ERROR:
                setEmptyText(getString(com.roodie.materialmovies.R.string.empty_network_error, getTitle()));
                break;
            case UNKNOWN:
                setEmptyText(getString(com.roodie.materialmovies.R.string.empty_unknown_error, getTitle()));
                break;
        }

    }

    @Override
    public String getSubtitle() {
        if (hasPresenter()) {
            return getPresenter().getUiSubTitle();
        }
        return  null;
    }

    @Override
    public String getTitle() {
        if (hasPresenter()) {
            return getPresenter().getUiTitle();
        }
        return  null;
    }

    @Override
    public void setSearchResult(MoviesState.SearchResult result) {
        //TODO
    }

    @Override
    public void showMovieDetail(MovieWrapper movie, View view) {
        //TODO
    }

    @Override
    public void showPersonDetail(PersonWrapper person, View view) {
        //TODO
    }

    @Override
    public void showTvShowDialog(ShowWrapper tvShow) {
        //TODO
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

}
