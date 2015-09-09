package com.roodie.materialmovies.views.fragments.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.roodie.materialmovies.R;
import com.roodie.materialmovies.mvp.presenters.SearchPresenter;
import com.roodie.materialmovies.views.MMoviesApplication;
import com.roodie.model.Display;
import com.roodie.model.entities.BasicWrapper;
import com.roodie.model.entities.MovieWrapper;
import com.roodie.model.entities.PersonWrapper;
import com.roodie.model.entities.ShowWrapper;
import com.roodie.model.network.NetworkError;
import com.roodie.model.state.MoviesState;

/**
 * Created by Roodie on 07.09.2015.
 */
public abstract class BaseSearchListFragment<M extends BasicWrapper> extends BaseListFragment<ListView> implements SearchPresenter.SearchView<M> {

    private SearchPresenter mPresenter;

    private boolean hasPresenter() {
        return mPresenter != null;
    }

    public SearchPresenter getPresenter() {
        return mPresenter;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mPresenter = MMoviesApplication.from(activity.getApplicationContext()).getSearchPresenter();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_list, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case  R.id.menu_refresh:
                getPresenter().refresh();
                return true;
        }
        return false;
    }

    @Override
    public ListView createListView(Context context, LayoutInflater inflater) {
        return (ListView) inflater.inflate(com.roodie.materialmovies.R.layout.view_pinned_list, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPresenter.attachView(this);
        mPresenter.initialize();
    }

    @Override
    public void onResume() {
        mPresenter.onResume();
        super.onResume();
    }

    @Override
    public void onPause() {
        mPresenter.onPause();
        super.onPause();
    }

    @Override
    protected boolean onScrolledToBottom() {
        return false;
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
        if (visible) {
            setListShown(false);
        } else {
            setListShown(true);
        }
    }

    @Override
    public void showError(NetworkError error) {
        setListShown(true);

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
    public String getSubtitle() {
        if (hasPresenter()) {
            return getPresenter().getUiSubTitle();
        }
        return null;
    }

    @Override
    public String getTitle() {
        if (hasPresenter()) {
            return getPresenter().getUiTitle();
        }
        return null;
    }

    @Override
    public void onUiAttached() {
        Display display = getDisplay();
        if (display != null) {
            display.setActionBarTitle(getTitle());
            display.setActionBarSubtitle(getSubtitle());
        }

    }

    @Override
    public void showTvShowDialog(ShowWrapper tvShow) {
        //TODO
    }

    @Override
    public void showPersonDetail(PersonWrapper person, View view) {
        //TODO
    }

    @Override
    public void showMovieDetail(MovieWrapper movie, View view) {
        //TODO
    }

    @Override
    public void showTvShowDetail(ShowWrapper show, View view) {
        //TODO
    }

    @Override
    public void setSearchResult(MoviesState.SearchResult result) {
        //TODO
    }
}
