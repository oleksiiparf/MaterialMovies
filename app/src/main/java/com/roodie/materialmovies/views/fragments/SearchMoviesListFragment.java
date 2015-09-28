package com.roodie.materialmovies.views.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.google.common.base.Preconditions;
import com.roodie.materialmovies.views.adapters.SearchMoviesSectionedListAdapter;
import com.roodie.materialmovies.views.fragments.base.BaseSearchListFragment;
import com.roodie.model.entities.ListItem;
import com.roodie.model.entities.MovieWrapper;

import java.util.List;

/**
 * Created by Roodie on 06.09.2015.
 */

public class SearchMoviesListFragment extends BaseSearchListFragment<MovieWrapper> {

    private SearchMoviesSectionedListAdapter mListAdapter;

    private OnShowMovieListener mListener = mModelListener;

    public interface OnShowMovieListener {
        public void showMovieDetail(String movieId, View view);
    }

    private static OnShowMovieListener mModelListener = new OnShowMovieListener() {
        @Override
        public void showMovieDetail(String movieId, View view) {

        }
    };

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mListener = (OnShowMovieListener) activity;
        } catch (ClassCastException ex) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnShowMovieListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mListener = mModelListener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mListAdapter = new SearchMoviesSectionedListAdapter(getActivity());
        setListAdapter(mListAdapter);
    }

    @Override
    public MovieQueryType getQueryType() {
        return MovieQueryType.SEARCH_MOVIES;
    }

    @Override
    public void showMovieDetail(MovieWrapper movie, View view) {
        Preconditions.checkNotNull(movie, "movie cannot be null");
        Preconditions.checkNotNull(movie.getTmdbId(), "movie id cannot be null");

        mListener.showMovieDetail(String.valueOf(movie.getTmdbId()), view);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        ListItem<MovieWrapper> item = (ListItem<MovieWrapper>) l.getItemAtPosition(position);
        if (item.getListType() == ListItem.TYPE_ITEM) {
            showMovieDetail(item.getListItem(), v);
        }
    }

    @Override
    public void setItems(List<ListItem<MovieWrapper>> listItems) {
        mListAdapter.setItems(listItems);
    }
}
