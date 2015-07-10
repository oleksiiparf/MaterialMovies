package com.roodie.materialmovies.views.fragments.base;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridView;

import com.roodie.materialmovies.R;
import com.roodie.materialmovies.mvp.presenters.MovieGridPresenter;
import com.roodie.materialmovies.views.adapters.MovieGridAdapter;
import com.roodie.model.entities.ListItem;
import com.roodie.model.entities.MovieWrapper;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by Roodie on 29.06.2015.
 */
public abstract class MovieGridFragment extends ListFragment<GridView> implements MovieGridPresenter.MovieGridView {

    @Inject MovieGridPresenter mMovieGridPresenter;
    private MovieGridAdapter mMovieGridAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMovieGridAdapter = new MovieGridAdapter(getActivity());
        setListAdapter(mMovieGridAdapter);
    }

    @Override
    public void setItems(List<ListItem<MovieWrapper>> listItems) {
        mMovieGridAdapter.setItems(listItems);
        moveListViewToSavedPositions();

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final int spacing = getResources().getDimensionPixelSize(R.dimen.movie_grid_spacing);
        getListView().setPadding(spacing, spacing, spacing, spacing);
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
        return (GridView) inflater.inflate(com.roodie.model.R.layout.view_grid, null);
    }

    protected final boolean hasPresenter() {
        return mMovieGridPresenter != null;
    }

    public MovieGridPresenter getPresenter() {
        return mMovieGridPresenter;
    }
}
