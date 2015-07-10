package com.roodie.materialmovies.views.adapters;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.roodie.materialmovies.R;
import com.roodie.materialmovies.views.custom_views.MMoviesImageView;
import com.roodie.model.entities.ListItem;
import com.roodie.model.entities.MovieWrapper;

import java.util.List;

/**
 * Created by Roodie on 29.06.2015.
 */
public class MovieGridAdapter extends BaseAdapter {

    private final Activity mActivity;
    private final LayoutInflater mLayoutInflater;

    private List<ListItem<MovieWrapper>> mItems;

    public MovieGridAdapter(Activity activity) {
        this.mActivity = activity;
        mLayoutInflater = mActivity.getLayoutInflater();
    }

    public void setItems(List<ListItem<MovieWrapper>> items) {
    if (!items.equals(mItems)) {
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
    public ListItem<MovieWrapper> getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            view = mLayoutInflater.inflate(R.layout.item_grid_movie, parent, false);
        }
        final MovieWrapper movie = getItem(position).getListItem();

        final MMoviesImageView imageView = (MMoviesImageView) view.findViewById(R.id.image_poster);
        imageView.loadPoster(movie, new MMoviesImageView.OnLoadedListener() {
            @Override
            public void onSuccess(MMoviesImageView imageView, Bitmap bitmap) {

            }

            @Override
            public void onError(MMoviesImageView imageView) {

            }
        });

        final TextView title = (TextView) view.findViewById(R.id.textview_title);
        title.setText(movie.getTmdbTitle());


        return view;
    }
}
