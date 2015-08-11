package com.roodie.materialmovies.views.fragments.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;

import com.google.common.base.Preconditions;
import com.roodie.materialmovies.R;
import com.roodie.materialmovies.mvp.presenters.MovieGridPresenter;
import com.roodie.materialmovies.views.MMoviesApplication;
import com.roodie.materialmovies.views.adapters.MovieGridAdapter;
import com.roodie.model.Display;
import com.roodie.model.entities.ListItem;
import com.roodie.model.entities.MovieWrapper;
import com.roodie.model.network.NetworkError;

import java.util.List;

/**
 * Created by Roodie on 29.06.2015.
 */
public abstract class MovieGridFragment extends ListFragment<GridView> implements MovieGridPresenter.MovieGridView {

    protected MovieGridPresenter mMovieGridPresenter;
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
        System.out.println("Presenter: " + mMovieGridPresenter);

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mMovieGridPresenter.detachUi(this);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onStop() {
        super.onStop();
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
                mMovieGridPresenter.refresh(this);
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

        //set actionbar up navigation
        final Display display = getDisplay();
        if (!isModal()) {
            display.showUpNavigation(getQueryType() != null && getQueryType().showUpNavigation());
        }
        mMovieGridPresenter.attachUi(this);
    }

    @Override
    protected boolean onScrolledToBottom() {
        if (hasPresenter()) {
            getPresenter().onScrolledToBottom(this);
            return true;
        }
        return false;
    }

    @Override
    public void onListItemClick(GridView l, View v, int position, long id) {
       if (hasPresenter()) {

           ListItem<MovieWrapper> item =  (ListItem<MovieWrapper>) l.getItemAtPosition(position);
          // Log.d(LOG_TAG, "List item clicked  " + item.getListItem().getTitle());
           Log.d(LOG_TAG, getQueryType() + " clicked");
           if (item.getListType() == ListItem.TYPE_ITEM) {
               showMovieDetail(item.getListItem(), v);
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
    public void showMovieDetail(MovieWrapper movie,  View view){
        Preconditions.checkNotNull(movie, "movie cannot be null");
        int[] startingLocation = new int[2];
        view.getLocationOnScreen(startingLocation);
        startingLocation[0] += view.getWidth() / 2;

        Display display = getDisplay();
        if (display != null) {
            if (movie.getTmdbId() != null) {
                display.startMovieDetailActivity(String.valueOf(movie.getTmdbId()), startingLocation);
            }
        }
    }

    @Override
    public String getRequestParameter() {
        return null;
    }

}
