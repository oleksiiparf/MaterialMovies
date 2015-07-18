package com.roodie.materialmovies.views.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;

import com.google.common.base.Preconditions;
import com.roodie.materialmovies.R;
import com.roodie.materialmovies.mvp.presenters.MovieDetailPresenter;
import com.roodie.materialmovies.views.MMoviesApplication;
import com.roodie.materialmovies.views.fragments.base.BaseDetailFragment;
import com.roodie.model.entities.MovieWrapper;
import com.roodie.model.entities.PersonCreditWrapper;
import com.roodie.model.network.NetworkError;

import java.util.ArrayList;

/**
 * Created by Roodie on 27.06.2015.
 */
public class MovieDetailFragment extends BaseDetailFragment implements MovieDetailPresenter.MovieDetailView, View.OnClickListener, AbsListView.OnScrollListener {

    private MovieDetailPresenter mPresenter;


    private MovieWrapper mMovie;

    private final ArrayList<DetailItemType> mItems = new ArrayList<>();

    private static final String QUERY_MOVIE_ID = "movie_id";

    public static MovieDetailFragment newInstance(String movieId) {
        Preconditions.checkArgument(!TextUtils.isEmpty(movieId), "MovieId can not be null");

        Bundle bundle = new Bundle();
        bundle.putString(QUERY_MOVIE_ID, movieId);
        MovieDetailFragment fragment = new MovieDetailFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MMoviesApplication.from(getActivity()).inject(this);
        setHasOptionsMenu(true);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mPresenter = MMoviesApplication.from(activity.getApplicationContext()).getDetailMoviePresenter();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_movie_detail_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.movie_detail, menu);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_refresh: {
                mPresenter.refresh();
                return true;
            }
        }
        return false;
    }

    /**
     * MovieView
     */
    @Override
    public void showError(NetworkError error) {

    }

    @Override
    public void showLoadingProgress(boolean visible) {
        getActivity().setProgressBarIndeterminateVisibility(visible);
    }

    @Override
    public void showSecondaryLoadingProgress(boolean visible) {

    }

    @Override
    public String getRequestParameter() {
        return null;
    }

    /**
     * MovieDetailView
     */
    @Override
    public void setMovie(MovieWrapper movie) {
        mMovie = movie;
    }

    @Override
    public void showMovieDetail(MovieWrapper movie, Bundle bundle) {

    }

    @Override
    public void showMovieDetail(PersonCreditWrapper credit, Bundle bundle) {

    }

    @Override
    public void showRelatedMovies(MovieWrapper movie) {

    }

    /**
     *
     * OnScrollListener
     */
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }

    @Override
    public boolean isModal() {
        return false;
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    protected DetailAdapter createListAdapter() {
        return new DetailAdapter();
    }

    private enum DetailItemType implements DetailType {
        TITLE(R.layout.item_movie_detail_title),
        DETAILS(R.layout.item_movie_detail_details),
        SUMMARY(R.layout.item_movie_detail_summary),
        TRAILERS(R.layout.item_movie_detail_trailers),
        RELATED(R.layout.item_movie_detail_generic_card),
        CAST(R.layout.item_movie_detail_generic_card),
        CREW(R.layout.item_movie_detail_generic_card);

        private final int mLayoutId;

        DetailItemType(int mLayoutId) {
            this.mLayoutId = mLayoutId;
        }


        @Override
        public int getLayoutId() {
            return mLayoutId;
        }

        @Override
        public int getViewType() {
            return 0;
        }

        @Override
        public boolean isEnabled() {
            return false;
        }
    }


    private class DetailAdapter extends BaseAdapter {
    }
}
