package com.roodie.materialmovies.views.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.common.base.Objects;
import com.roodie.materialmovies.R;
import com.roodie.materialmovies.settings.DisplaySettings;
import com.roodie.materialmovies.settings.TmdbSettings;
import com.roodie.model.entities.ListItem;
import com.roodie.model.entities.MovieWrapper;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Roodie on 29.06.2015.
 */
public class MovieGridAdapter extends BaseAdapter {

    private static final String LOG_TAG = MovieGridAdapter.class.getName();

    private final Activity mActivity;
    private final LayoutInflater mLayoutInflater;
    private Context mContext;

    private List<ListItem<MovieWrapper>> mItems;

    private String mImageBaseUrl;

    public MovieGridAdapter(Activity activity) {
        this.mActivity = activity;
        mLayoutInflater = mActivity.getLayoutInflater();
        this.mContext = mActivity.getApplicationContext();

        if (DisplaySettings.isHighDestinyScreen(mContext)) {
            mImageBaseUrl = TmdbSettings.getImageBaseUrl(mContext)
                    + TmdbSettings.POSTER_SIZE_SPEC_W342;
        } else {
            mImageBaseUrl = TmdbSettings.getImageBaseUrl(mContext)
                    + TmdbSettings.POSTER_SIZE_SPEC_W154;
        }

    }

    public void setItems(List<ListItem<MovieWrapper>> items) {
    if (!Objects.equal(items, mItems)) {
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
        ViewHolder holder;

        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.item_grid_movie,parent, false);

            holder = new ViewHolder();
            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.poster = (ImageView) convertView.findViewById(R.id.poster);
            holder.poster.setDrawingCacheEnabled(true);


            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final MovieWrapper movie = getItem(position).getListItem();

        holder.title.setText(movie.getTitle());
        //load poster
        Picasso.with(mContext)
                .load(mImageBaseUrl + movie.getPosterUrl())
                .fit().centerCrop().into(holder.poster);
        return convertView;
    }

     class ViewHolder {
        TextView title;
        ImageView poster;
    }

}
