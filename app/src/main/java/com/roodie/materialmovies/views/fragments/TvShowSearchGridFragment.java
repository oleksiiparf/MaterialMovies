package com.roodie.materialmovies.views.fragments;

import android.view.View;

import com.roodie.materialmovies.views.adapters.SearchShowGridAdapter;
import com.roodie.materialmovies.views.fragments.base.SearchGridFragment;
import com.roodie.model.entities.ListItem;
import com.roodie.model.entities.ShowWrapper;

import java.util.List;

/**
 * Created by Roodie on 22.08.2015.
 */
public class TvShowSearchGridFragment extends SearchGridFragment<ShowWrapper, SearchShowGridAdapter> {

    @Override
    public void showTvShowDialog(ShowWrapper tvShow) {
        super.showTvShowDialog(tvShow);
    }

    @Override
    public MovieQueryType getQueryType() {
        return MovieQueryType.SEARCH_SHOWS;
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
    protected SearchShowGridAdapter createAdapter() {
        return new SearchShowGridAdapter(null);
    }

    @Override
    public void setItems(List<ListItem<ShowWrapper>> listItems) {
        getAdapter().setItems(listItems);
    }
}
