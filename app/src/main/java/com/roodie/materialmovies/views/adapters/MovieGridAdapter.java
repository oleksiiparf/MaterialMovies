package com.roodie.materialmovies.views.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.common.base.Objects;
import com.roodie.materialmovies.R;
import com.roodie.materialmovies.views.custom_views.MMoviesImageView;
import com.roodie.materialmovies.views.custom_views.RecyclerItemClickListener;
import com.roodie.model.entities.ListItem;
import com.roodie.model.entities.MovieWrapper;

import java.util.List;

/**
 * Created by Roodie on 29.06.2015.
 */
public class MovieGridAdapter extends RecyclerView.Adapter<MovieGridAdapter.MovieViewHolder> {

    private static final String LOG_TAG = MovieGridAdapter.class.getName();

    private RecyclerItemClickListener mClickListener;

    private List<ListItem<MovieWrapper>> mItems;


    public MovieGridAdapter(List<ListItem<MovieWrapper>> items) {
        if (!Objects.equal(items, mItems)) {
            mItems = items;
        }

    }

    public void setClickListener(RecyclerItemClickListener mClickListener) {
        this.mClickListener = mClickListener;
    }

    public void setItems(List<ListItem<MovieWrapper>> items) {
    if (!Objects.equal(items, mItems)) {
        mItems = items;
        notifyDataSetChanged();
        }
    }

    @Override
    public int getItemCount() {
        return mItems != null ? mItems.size() : 0;
    }

    public ListItem<MovieWrapper> getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_grid_movie, parent, false);
        return new MovieViewHolder(rowView, mClickListener);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        final MovieWrapper movie = getItem(position).getListItem();

        holder.title.setText(movie.getTitle());
        //load poster
        holder.poster.loadPoster(movie);

    }

     class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final RecyclerItemClickListener onClickListener;
        View container;
        TextView title;
        MMoviesImageView poster;

         public MovieViewHolder(View itemView, RecyclerItemClickListener listener) {
             super(itemView);

             container = itemView.findViewById(R.id.item_movie_container);
             container.setOnClickListener(this);
             poster = (MMoviesImageView) itemView.findViewById(R.id.poster);
             title = (TextView) itemView.findViewById(R.id.title);
             this.onClickListener = listener;
         }

         @Override
         public void onClick(View v) {
             onClickListener.onClick(v, getPosition());
         }
     }

}
