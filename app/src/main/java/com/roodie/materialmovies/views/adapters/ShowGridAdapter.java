package com.roodie.materialmovies.views.adapters;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.common.base.Objects;
import com.roodie.materialmovies.R;
import com.roodie.materialmovies.views.custom_views.MMoviesImageView;
import com.roodie.materialmovies.views.custom_views.RecyclerItemClickListener;
import com.roodie.model.entities.ListItem;
import com.roodie.model.entities.ShowWrapper;

import java.util.List;

/**
 * Created by Roodie on 15.08.2015.
 */
public class ShowGridAdapter extends RecyclerView.Adapter<ShowGridAdapter.ShowViewHolder> {

    private static final String LOG_TAG = ShowGridAdapter.class.getName();

    private RecyclerItemClickListener mClickListener;

    private List<ListItem<ShowWrapper>> mItems;


    public ShowGridAdapter(List<ListItem<ShowWrapper>> items) {
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
    public ShowViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_grid_show, parent, false);
        return new ShowViewHolder(rowView, mClickListener);
    }

    @Override
    public void onBindViewHolder(final ShowViewHolder holder, int position) {
        final ShowWrapper show = getItem(position).getListItem();

        holder.title.setText(show.getTitle());
        holder.subtitle.setText(show.getGenres());
        //load poster
        holder.poster.loadPoster(show, new MMoviesImageView.OnLoadedListener() {
            @Override
            public void onSuccess(MMoviesImageView imageView, Bitmap bitmap) {
                //holder.containerBar.change background
            }

            @Override
            public void onError(MMoviesImageView imageView) {

            }
        });

    }

    class ShowViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final RecyclerItemClickListener onClickListener;
        View container;
        View containerBar;
        TextView title;
        TextView subtitle;
        ImageButton like;
        MMoviesImageView poster;

        public ShowViewHolder(View itemView, RecyclerItemClickListener listener) {
            super(itemView);

            container = itemView.findViewById(R.id.item_show_container);
            containerBar = itemView.findViewById(R.id.content_bar);
            container.setOnClickListener(this);
            poster = (MMoviesImageView) itemView.findViewById(R.id.poster);
            title = (TextView) itemView.findViewById(R.id.title);
            subtitle = (TextView) itemView.findViewById(R.id.genres);
            like = (ImageButton) itemView.findViewById(R.id.like_button);
            this.onClickListener = listener;
        }

        @Override
        public void onClick(View v) {
            onClickListener.onClick(v, getPosition());
        }
    }
}
