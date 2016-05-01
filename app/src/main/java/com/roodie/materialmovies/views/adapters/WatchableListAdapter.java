package com.roodie.materialmovies.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.roodie.materialmovies.MMoviesApp;
import com.roodie.materialmovies.R;
import com.roodie.materialmovies.views.custom_views.MMoviesImageView;
import com.roodie.materialmovies.views.custom_views.MMoviesTextView;
import com.roodie.materialmovies.views.listeners.RecyclerItemClickListener;
import com.roodie.model.entities.Watchable;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Roodie on 28.02.2016.
 */
public class WatchableListAdapter extends FooterViewListAdapter<List<Watchable>, WatchableListAdapter.WatchableListViewHolder> {

    private RecyclerItemClickListener mClickListener;

    private final Date mDate;

    private DateFormat movieReleaseDate = DateFormat.getDateInstance(DateFormat.MEDIUM);

    public WatchableListAdapter(Context context, RecyclerItemClickListener mClickListener) {
        super(context);
        mDate = new Date();
        this.mClickListener = mClickListener;
    }

    @Override
    public WatchableListViewHolder onCreateViewHolder(ViewGroup parent) {
        View rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_3line, parent, false);
        return new WatchableListViewHolder(rowView, true);
    }

    @Override
    public WatchableListViewHolder getViewHolder(View view) {
        return new WatchableListViewHolder(view, false);
    }

    @Override
    public void onBindViewHolder(WatchableListViewHolder holder, int position) {
        if (position < getTotalItemsCount() && position < getItemCount() ) {

            final Watchable item = items.get(position);

            if (item.getYear() > 0) {
                holder.title.setText(MMoviesApp.get().getAppContext().getString(R.string.movie_title_year, item.getTitle(), item.getYear()));
            } else {
                holder.title.setText(item.getTitle());
            }

            holder.rating.setText(MMoviesApp.get().getAppContext().getString(R.string.movie_rating_votes, item.getAverageRatingPercent(), item.getRatingVotes()));

            mDate.setTime(item.getReleasedTime());
            holder.release.setText(MMoviesApp.get().getAppContext().getString(R.string.movie_release_date, movieReleaseDate.format(mDate)));

            holder.poster.loadPoster(item);
        }
    }

    public class WatchableListViewHolder extends ListViewHolder implements View.OnClickListener {
        View container;
        MMoviesTextView title;
        MMoviesTextView rating;
        MMoviesTextView release;
        MMoviesImageView poster;

        public WatchableListViewHolder(View itemView, boolean isItem) {
            super(itemView, isItem);

            if (isItem) {
                container = itemView.findViewById(R.id.container);
                container.setOnClickListener(this);
                poster = (MMoviesImageView) itemView.findViewById(R.id.imageview_poster);
                title = (MMoviesTextView) itemView.findViewById(R.id.title);
                rating = (MMoviesTextView) itemView.findViewById(R.id.textview_subtitle_1);
                release = (MMoviesTextView) itemView.findViewById(R.id.textview_subtitle_2);
            }
        }

        @Override
        public void onClick(View v) {
            final int viewId = v.getId();
            switch (viewId) {
                case R.id.container :
                    mClickListener.onClick(poster, getPosition());
                    break;
            }
        }
    }
}
