package com.roodie.materialmovies.views.fragments.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;

import com.roodie.materialmovies.R;
import com.roodie.materialmovies.mvp.presenters.MovieGridPresenter;
import com.roodie.materialmovies.views.MMoviesApplication;
import com.roodie.materialmovies.views.adapters.MovieGridAdapter;
import com.roodie.model.entities.ListItem;
import com.roodie.model.entities.MovieWrapper;
import com.roodie.model.network.NetworkError;

import java.util.List;

/**
 * Created by Roodie on 29.06.2015.
 */
public abstract class MovieGridFragment extends ListFragment<GridView> implements MovieGridPresenter.MovieGridView {

    private MovieGridPresenter mMovieGridPresenter;
    private MovieGridAdapter mMovieGridAdapter;

    private static final String LOG_TAG = MovieGridFragment.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mMovieGridAdapter = new MovieGridAdapter(getActivity());
        setListAdapter(mMovieGridAdapter);


    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mMovieGridPresenter = MMoviesApplication.from(activity.getApplicationContext()).getGridPresenter();

    }

    @Override
    public void onPause() {
        super.onPause();
        mMovieGridPresenter.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMovieGridPresenter.onResume();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
       super.onCreateOptionsMenu(menu, inflater);
          }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_about:
                return false;
            case R.id.menu_refresh:
                mMovieGridPresenter.refresh();
                return true;
        }
        return false;
    }

    @Override
    public void setItems(List<ListItem<MovieWrapper>> listItems) {
        mMovieGridAdapter.setItems(listItems);
        //moveListViewToSavedPositions();

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final int spacing = getResources().getDimensionPixelSize(R.dimen.movie_grid_spacing);
        getListView().setPadding(spacing, spacing, spacing, spacing);
        mMovieGridPresenter.attachView(this);
        mMovieGridPresenter.initialize();
    }

    @Override
    protected boolean onScrolledToBottom() {
        if (hasPresenter()) {
            getPresenter().onScrolledToBottom();
            return true;
        }
        return false;
    }

    @Override
    public void onListItemClick(GridView l, View v, int position, long id) {
       if (hasPresenter()) {
           ListItem<MovieWrapper> item =  (ListItem<MovieWrapper>) l.getItemAtPosition(position);
           if (item.getListType() == ListItem.TYPE_ITEM) {
               getPresenter().showMovieDetail(item.getListItem(),
                      null);
           }
       }
    }

    @Override
    public GridView createListView(Context context, LayoutInflater inflater) {
        return (GridView) inflater.inflate(R.layout.view_grid, null);
    }

    protected final boolean hasPresenter() {
        return mMovieGridPresenter != null;
    }

    public MovieGridPresenter getPresenter() {
        return mMovieGridPresenter;
    }

    @Override
    public void showError(NetworkError error) {
        setListShown(true);

        switch (error) {
            case NETWORK_ERROR:
                setEmptyText(getString(R.string.empty_network_error, "getTitle()"));
                break;
            case UNKNOWN:
                setEmptyText(getString(R.string.empty_unknown_error, "getTitle()"));
                break;
        }
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
    public void showSecondaryLoadingProgress(boolean visible) {
        setSecondaryProgressShown(visible);
    }

    @Override
    public String getRequestParameter() {
        return null;
    }
}
