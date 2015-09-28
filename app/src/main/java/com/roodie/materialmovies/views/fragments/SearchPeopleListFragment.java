package com.roodie.materialmovies.views.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.google.common.base.Preconditions;
import com.roodie.materialmovies.views.adapters.SearchPeopleSectionedListAdapter;
import com.roodie.materialmovies.views.fragments.base.BaseSearchListFragment;
import com.roodie.model.entities.ListItem;
import com.roodie.model.entities.PersonWrapper;

import java.util.List;

/**
 * Created by Roodie on 07.09.2015.
 */

public class SearchPeopleListFragment extends BaseSearchListFragment <PersonWrapper>{

    private SearchPeopleSectionedListAdapter mListAdapter;

    private OnShowPersonListener mListener = mModelListener;

    public interface OnShowPersonListener {
        public void showPersonDetail(String personId, View view);
    }

    private static OnShowPersonListener mModelListener = new OnShowPersonListener() {
        @Override
        public void showPersonDetail(String personId, View view) {

        }
    };

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mListener = (OnShowPersonListener) activity;
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

        mListAdapter = new SearchPeopleSectionedListAdapter(getActivity());
        setListAdapter(mListAdapter);
    }

    @Override
    public MovieQueryType getQueryType() {
        return MovieQueryType.SEARCH_PEOPLE;
    }

    @Override
    public void showPersonDetail(PersonWrapper person, View view) {
            Preconditions.checkNotNull(person, "person cannot be null");
            Preconditions.checkNotNull(person.getTmdbId(), "person id cannot be null");

            mListener.showPersonDetail(String.valueOf(person.getTmdbId()), view);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        ListItem<PersonWrapper> item = (ListItem<PersonWrapper>) l.getItemAtPosition(position);
        if (item.getListType() == ListItem.TYPE_ITEM) {
            showPersonDetail(item.getListItem(), v);
        }
    }

    @Override
    public void setItems(List<ListItem<PersonWrapper>> listItems) {
        mListAdapter.setItems(listItems);
    }
}
