package com.roodie.materialmovies.views.adapters;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.common.base.Objects;
import com.roodie.materialmovies.R;
import com.roodie.materialmovies.views.custom_views.MMoviesImageView;
import com.roodie.materialmovies.views.listeners.RecyclerItemClickListener;
import com.roodie.model.entities.ListItem;
import com.roodie.model.entities.ShowWrapper;

import java.util.List;

/**
 * Created by Roodie on 22.08.2015.
 */
public class SearchShowGridAdapter extends RecyclerView.Adapter<SearchShowGridAdapter.MovieViewHolder> {

    private static final String LOG_TAG = SearchShowGridAdapter.class.getName();

    private RecyclerItemClickListener mClickListener;

    private List<ListItem<ShowWrapper>> mItems;


    public SearchShowGridAdapter(List<ListItem<ShowWrapper>> items) {
        if (!Objects.equal(items, mItems)) {
            mItems = items;
        }

    }

    public void setClickListener(RecyclerItemClickListener mClickListener) {
        this.mClickListener = mClickListener;
    }

    public void setItems(List<ListItem<ShowWrapper>> items) {
        if (!Objects.equal(items, mItems)) {
            mItems = items;
            notifyDataSetChanged();
        }
    }

    @Override
    public int getItemCount() {
        return mItems != null ? mItems.size() : 0;
    }

    public ListItem<ShowWrapper> getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_movie_card, parent, false);
        return new MovieViewHolder(rowView, mClickListener);
    }

    @Override
    public void onBindViewHolder(final MovieViewHolder holder, int position) {
        final ShowWrapper show = getItem(position).getListItem();

        holder.title.setText(show.getTitle());
        holder.subtitle1.setText(show.getGenres());
        //load poster
        holder.poster.loadPoster(show, new MMoviesImageView.OnLoadedListener() {
            @Override
            public void onSuccess(MMoviesImageView imageView, Bitmap bitmap, String imageUrl) {
                holder.poster.setTag(imageUrl);
            }

            @Override
            public void onError(MMoviesImageView imageView) {

            }
        });

    }

    class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final RecyclerItemClickListener onClickListener;
        View container;
        TextView title;
        TextView subtitle1;
        MMoviesImageView poster;

        public MovieViewHolder(View itemView, RecyclerItemClickListener listener) {
            super(itemView);

            container = itemView.findViewById(R.id.card_content_holder);
            container.setOnClickListener(this);
            poster = (MMoviesImageView) itemView.findViewById(R.id.poster);
            title = (TextView) itemView.findViewById(R.id.title);
            subtitle1 = (TextView) itemView.findViewById(R.id.subtitle_1);
            this.onClickListener = listener;
        }

        @Override
        public void onClick(View v) {
            onClickListener.onClick(poster, getPosition());
        }
    }

}