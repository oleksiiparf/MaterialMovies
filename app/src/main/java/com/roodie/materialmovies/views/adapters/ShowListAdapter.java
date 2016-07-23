package com.roodie.materialmovies.views.adapters;

import android.view.View;

import com.marshalchen.ultimaterecyclerview.UltimateRecyclerviewViewHolder;
import com.marshalchen.ultimaterecyclerview.quickAdapter.easyRegularAdapter;
import com.roodie.materialmovies.MMoviesApp;
import com.roodie.materialmovies.R;
import com.roodie.materialmovies.views.custom_views.MMoviesImageView;
import com.roodie.materialmovies.views.custom_views.MMoviesTextView;
import com.roodie.materialmovies.views.listeners.RecyclerItemClickListener;
import com.roodie.model.entities.ShowWrapper;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Roodie on 17.07.2016.
 */

public class ShowListAdapter extends easyRegularAdapter<ShowWrapper, ShowListAdapter.WatchableListViewHolder> {

    private RecyclerItemClickListener mClickListener;

    private final Date mDate;

    private DateFormat movieReleaseDate = DateFormat.getDateInstance(DateFormat.MEDIUM);

    public ShowListAdapter(List<ShowWrapper> list, RecyclerItemClickListener mClickListener) {
        super(list);
        mDate = new Date();
        this.mClickListener = mClickListener;
    }

    @Override
    protected int getNormalLayoutResId() {
        return R.layout.item_list_3line;
    }

    @Override
    protected WatchableListViewHolder newViewHolder(View view) {
        return new WatchableListViewHolder(view, true);
    }

    @Override
    public WatchableListViewHolder newHeaderHolder(View view) {
        return new WatchableListViewHolder(view, false);
    }

    @Override
    protected void withBindHolder(WatchableListViewHolder holder, ShowWrapper data, int position) {

        if (data.getYear() > 0) {
            holder.title.setText(MMoviesApp.get().getAppContext().getString(R.string.movie_title_year, data.getTitle(), data.getYear()));
        } else {
            holder.title.setText(data.getTitle());
        }

        holder.rating.setText(MMoviesApp.get().getAppContext().getString(R.string.movie_rating_votes, data.getAverageRatingPercent(), data.getRatingVotes()));

        mDate.setTime(data.getReleasedTime());
        holder.release.setText(MMoviesApp.get().getAppContext().getString(R.string.movie_release_date, movieReleaseDate.format(mDate)));

        holder.poster.loadPoster(data);
    }


    public class WatchableListViewHolder extends UltimateRecyclerviewViewHolder implements View.OnClickListener {
        View container;
        MMoviesTextView title;
        MMoviesTextView rating;
        MMoviesTextView release;
        MMoviesImageView poster;

        public WatchableListViewHolder(View itemView, boolean isItem) {
            super(itemView);

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

