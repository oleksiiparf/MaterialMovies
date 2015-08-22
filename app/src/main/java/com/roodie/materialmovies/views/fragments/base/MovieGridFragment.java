package com.roodie.materialmovies.views.fragments.base;

import android.app.Activity;
import android.os.Build;
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

import com.google.common.base.Preconditions;
import com.roodie.materialmovies.R;
import com.roodie.materialmovies.mvp.presenters.MovieGridPresenter;
import com.roodie.materialmovies.views.MMoviesApplication;
import com.roodie.materialmovies.views.adapters.MovieGridAdapter;
import com.roodie.materialmovies.views.custom_views.RecyclerInsetsDecoration;
import com.roodie.model.Display;
import com.roodie.model.entities.ListItem;
import com.roodie.model.entities.MovieWrapper;
import com.roodie.model.network.NetworkError;

import java.util.List;

/**
 * Created by Roodie on 29.06.2015.
 */
public abstract class MovieGridFragment extends BaseGridFragment implements MovieGridPresenter.MovieGridView{

    protected MovieGridPresenter mMovieGridPresenter;
    private MovieGridAdapter mMovieGridAdapter;

    private static final String LOG_TAG = MovieGridFragment.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.movie_view_recycler, container, false);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mMovieGridPresenter = MMoviesApplication.from(activity.getApplicationContext()).getMovieGridPresenter();
        System.out.println("Presenter: " + mMovieGridPresenter);
    }

    @Override
    public void onPause() {
        super.onPause();
        mMovieGridPresenter.detachUi(this);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDetach() {
        super.onDetach();
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
            case R.id.menu_refresh:
                mMovieGridPresenter.refresh(this);
                return true;
            case  R.id.menu_search:
                getDisplay().showSearchFragment();
            return true;
        }
        return false;
    }

    @Override
    public void setItems(List<ListItem<MovieWrapper>> listItems) {
        mMovieGridAdapter.setItems(listItems);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //set actionbar up navigation
        final Display display = getDisplay();
        if (!isModal()) {
            display.showUpNavigation(getQueryType() != null && getQueryType().showUpNavigation());
        }
        mMovieGridPresenter.attachUi(this);
    }


    @Override
    public void initializeRecycler() {
        getRecyclerView().addItemDecoration(new RecyclerInsetsDecoration(getActivity().getApplicationContext()));
        mMovieGridAdapter = new MovieGridAdapter(null);
        mMovieGridAdapter.setClickListener(this);
        getRecyclerView().setAdapter(mMovieGridAdapter);
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

    @Override
    public void onClick(View view, int position) {
        if (hasPresenter()) {

            ListItem<MovieWrapper> item = mMovieGridAdapter.getItem(position);
            // Log.d(LOG_TAG, "List item clicked  " + item.getListItem().getTitle());
            Log.d(LOG_TAG, getQueryType() + " clicked");
            if (item.getListType() == ListItem.TYPE_ITEM) {
                showMovieDetail(item.getListItem(), view);
            }
        }
    }

    protected final boolean hasPresenter() {
        return mMovieGridPresenter != null;
    }

    public MovieGridPresenter getPresenter() {
        return mMovieGridPresenter;
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
    public String getTitle() {
        if (hasPresenter()) {
            return getPresenter().getUiTitle(this);
        }
        return null;
    }

    @Override
    public void showMovieDetail(MovieWrapper movie,View view){
        Preconditions.checkNotNull(movie, "movie cannot be null");
        int[] startingLocation = new int[2];
        view.getLocationOnScreen(startingLocation);
        startingLocation[0] += view.getWidth() / 2;
        startingLocation[1] += view.getHeight() / 2;

        Display display = getDisplay();
        if (display != null) {
            if (movie.getTmdbId() != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    System.out.println("Start by shared element");
                    display.startMovieDetailActivityBySharedElements(String.valueOf(movie.getTmdbId()), view, (String) view.getTag());
                } else {
                    System.out.println("Start by animation");
                    display.startMovieDetailActivityByAnimation(String.valueOf(movie.getTmdbId()), startingLocation);
                }
            }
        }
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

}
