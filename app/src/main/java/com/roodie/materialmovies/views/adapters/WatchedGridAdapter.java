package com.roodie.materialmovies.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.roodie.materialmovies.R;
import com.roodie.materialmovies.views.custom_views.MMoviesImageView;
import com.roodie.materialmovies.views.custom_views.MMoviesTextView;
import com.roodie.materialmovies.views.listeners.RecyclerItemClickListener;
import com.roodie.model.entities.Watchable;

import java.util.List;

/**
 * Created by Roodie on 05.03.2016.
 */
public class WatchedGridAdapter extends FooterViewListAdapter<List<Watchable>, WatchedGridAdapter.WatchedItemViewHolder> {

    private RecyclerItemClickListener mClickListener;

    public WatchedGridAdapter(Context context, RecyclerItemClickListener mClickListener) {
        super(context);
        this.mClickListener = mClickListener;
        setHasStableIds(true);
    }

    @Override
    public WatchedItemViewHolder getViewHolder(View view) {
        return new WatchedItemViewHolder(view, false);
    }

    @Override
    public WatchedItemViewHolder onCreateViewHolder(ViewGroup parent) {
        View rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_watched_list, parent, false);
        return new WatchedItemViewHolder(rowView, true);
    }

    @Override
    public void onBindViewHolder(WatchedItemViewHolder holder, int position) {
        if (position < getTotalItemsCount() && position < getItemCount()) {

            final Watchable item = items.get(position);

            holder.title.setText(item.getTitle());
            holder.watchedType.setText(item.getWatchableType().getResId());

            holder.poster.loadPoster(item);
        }
    }
    public class WatchedItemViewHolder extends ListViewHolder implements View.OnClickListener {
        View container;
        MMoviesTextView title;
        MMoviesTextView watchedType;
        MMoviesImageView poster;

        public WatchedItemViewHolder(View itemView, boolean isItem) {
            super(itemView, isItem);

            if (isItem) {
                container = itemView.findViewById(R.id.container);
                container.setOnClickListener(this);
                poster = (MMoviesImageView) itemView.findViewById(R.id.imageview_poster);
                title = (MMoviesTextView) itemView.findViewById(R.id.title);
                watchedType = (MMoviesTextView) itemView.findViewById(R.id.subtitle);
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
