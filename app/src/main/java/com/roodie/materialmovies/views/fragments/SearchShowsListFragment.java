package com.roodie.materialmovies.views.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.google.common.base.Preconditions;
import com.roodie.materialmovies.views.adapters.SearchShowsSectionedListAdapter;
import com.roodie.materialmovies.views.fragments.base.BaseSearchListFragment;
import com.roodie.model.entities.ListItem;
import com.roodie.model.entities.ShowWrapper;

import java.util.List;

/**
 * Created by Roodie on 07.09.2015.
 */
public class SearchShowsListFragment extends BaseSearchListFragment<ShowWrapper> {

    private SearchShowsSectionedListAdapter mListAdapter;

    private OnShowTvShowListener mListener = mModelListener;

    public interface OnShowTvShowListener {
        public void showTvShowDetail(String showId, View view);
    }

    private static OnShowTvShowListener mModelListener = new OnShowTvShowListener() {
        @Override
        public void showTvShowDetail(String showId, View view) {

        }
    };

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mListener = (OnShowTvShowListener) activity;
        } catch (ClassCastException ex) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnShowPersonListener");
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

        mListAdapter = new SearchShowsSectionedListAdapter(getActivity());
        setListAdapter(mListAdapter);
    }

    @Override
    public MovieQueryType getQueryType() {
        return MovieQueryType.SEARCH_SHOWS;
    }

    @Override
    public void showTvShowDetail(ShowWrapper show, View view) {
        Preconditions.checkNotNull(show, "show cannot be null");
        Preconditions.checkNotNull(show.getTmdbId(), "show id cannot be null");

        mListener.showTvShowDetail(String.valueOf(show.getTmdbId()), view);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        ListItem<ShowWrapper> item = (ListItem<ShowWrapper>) l.getItemAtPosition(position);
        if (item.getListType() == ListItem.TYPE_ITEM) {
            showTvShowDetail(item.getListItem(), v);
        }
    }

    @Override
    public void setItems(List<ListItem<ShowWrapper>> listItems) {
        mListAdapter.setItems(listItems);
    }
}
