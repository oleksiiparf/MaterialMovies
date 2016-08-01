package com.roodie.materialmovies.views.adapters;

import android.view.View;

import com.marshalchen.ultimaterecyclerview.UltimateGridLayoutAdapter;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerviewViewHolder;
import com.roodie.materialmovies.R;
import com.roodie.materialmovies.views.custom_views.MMoviesImageView;
import com.roodie.materialmovies.views.custom_views.MMoviesTextView;
import com.roodie.materialmovies.views.listeners.RecyclerItemClickListener;
import com.roodie.model.entities.Watchable;

import java.util.List;

/**
 * Created by Roodie on 05.03.2016.
 */
public class WatchedGridAdapter extends UltimateGridLayoutAdapter<Watchable, WatchedGridAdapter.WatchedItemViewHolder> {

    private RecyclerItemClickListener mClickListener;

    public WatchedGridAdapter(List<Watchable> list, RecyclerItemClickListener mClickListener) {
        super(list);
        this.mClickListener = mClickListener;
    }

    @Override
    protected int getNormalLayoutResId() {
        return R.layout.item_watched_list;
    }

    @Override
    protected WatchedItemViewHolder newViewHolder(View view) {
        return new WatchedItemViewHolder(view, true);
    }

    @Override
    public WatchedItemViewHolder newHeaderHolder(View view) {
        return new WatchedItemViewHolder(view, false);
    }

    @Override
    protected void bindNormal(WatchedItemViewHolder holder, Watchable data, int position) {
        holder.title.setText(data.getTitle());
        holder.watchedType.setText(data.getWatchableType().getResId());

        holder.poster.loadPoster(data);
    }

    @Override
    protected void withBindHolder(WatchedItemViewHolder holder, Watchable data, int position) {
    }

    public class WatchedItemViewHolder extends UltimateRecyclerviewViewHolder implements View.OnClickListener {
        View container;
        MMoviesTextView title;
        MMoviesTextView watchedType;
        MMoviesImageView poster;

        public WatchedItemViewHolder(View itemView, boolean isItem) {
            super(itemView);

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
