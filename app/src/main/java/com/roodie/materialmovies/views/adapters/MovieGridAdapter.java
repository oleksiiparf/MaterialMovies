package com.roodie.materialmovies.views.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.roodie.materialmovies.R;
import com.roodie.model.entities.ListItem;
import com.uwetrottmann.tmdb.entities.Movie;

import java.util.List;
import java.util.Objects;

/**
 * Created by Roodie on 29.06.2015.
 */
public class MovieGridAdapter extends BaseAdapter {

    private final Activity mActivity;
    private final LayoutInflater mLayoutInflater;

    private List<ListItem<Movie>> mItems;

    public MovieGridAdapter(Activity activity) {
        this.mActivity = activity;
        mLayoutInflater = mActivity.getLayoutInflater();
    }

private void setitems(List<ListItem<Movie>> items) {
    if (!Objects.equals(items, mItems)) {
        mItems = items;
        notifyDataSetChanged();
    }
}

    @Override
    public int getCount() {
        return mItems != null ? mItems.size() : 0;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public ListItem<Movie> getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            view = mLayoutInflater.inflate(R.layout.item_grid_movie, parent, false);
        }
        return view;
    }
}
