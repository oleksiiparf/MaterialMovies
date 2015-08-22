package com.roodie.materialmovies.views.fragments;

import android.util.Log;
import android.view.View;

import com.roodie.materialmovies.views.adapters.SearchMovieGridAdapter;
import com.roodie.materialmovies.views.fragments.base.SearchGridFragment;
import com.roodie.model.entities.ListItem;
import com.roodie.model.entities.MovieWrapper;

import java.util.List;


/**
 * Created by Roodie on 22.08.2015.
 */
public class MovieSearchGridFragment extends SearchGridFragment<MovieWrapper, SearchMovieGridAdapter> {


    @Override
    public void showMovieDetail(MovieWrapper movie, View view) {
    }

    @Override
    public MovieQueryType getQueryType() {
        return MovieQueryType.SEARCH_MOVIES;
    }

    @Override
    public void onClick(View view, int position) {

    }

    @Override
    public void initializeRecycler() {
        super.initializeRecycler();
        getAdapter().setClickListener(this);
    }



    @Override
    protected SearchMovieGridAdapter createAdapter() {
        Log.d(LOG_TAG, "create Adapter");
        return new SearchMovieGridAdapter(null);
    }

    @Override
    public void setItems(List<ListItem<MovieWrapper>> listItems) {
        Log.d(LOG_TAG, "Set items to adapter");
        getAdapter().setItems(listItems);
    }
}
