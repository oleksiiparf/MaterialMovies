package com.roodie.materialmovies.views.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.roodie.materialmovies.R;
import com.roodie.materialmovies.views.custom_views.MMoviesImageView;
import com.roodie.materialmovies.views.listeners.RecyclerItemClickListener;
import com.roodie.model.entities.MovieWrapper;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Roodie on 29.06.2015.
 */
public class MoviesGridAdapter extends FooterViewListAdapter<List<MovieWrapper>, MoviesGridAdapter.MovieGridViewHolder> {

    private static final String LOG_TAG = MoviesGridAdapter.class.getName();

    private DateFormat movieReleaseDate = DateFormat.getDateInstance(DateFormat.MEDIUM);

    private RecyclerItemClickListener mClickListener;

    public MoviesGridAdapter(Context context, RecyclerItemClickListener mClickListener) {
        super(context);
        this.mClickListener = mClickListener;
    }

    @Override
    public void onBindViewHolder(final MovieGridViewHolder holder, final int position) {
        if (position < getTotalItemsCount() && position < getItemCount() ) {

            final MovieWrapper movie = items.get(position);

            holder.title.setText(movie.getTitle());

            if (movie.getReleasedTime() > 0) {
                Date DATE = new Date(movie.getReleasedTime());
                DateFormat dateFormat = DateFormat.getDateInstance();
                holder.subtitle.setText( dateFormat.format(DATE));
            } else {
                holder.subtitle.setText("");
            }
            //load poster
            holder.poster.setAutoFade(true);
            holder.poster.loadPoster(movie, new MMoviesImageView.OnLoadedListener() {
                @Override
                public void onSuccess(MMoviesImageView imageView, Bitmap bitmap, String imageUrl) {
                    holder.poster.setTag(imageUrl);
                }

                @Override
                public void onError(MMoviesImageView imageView) {

                }
            });

        }

    }

    @Override
    public MovieGridViewHolder getViewHolder(View view) {
        return new MovieGridViewHolder(view, false);
    }

    @Override
    public MovieGridViewHolder onCreateViewHolder(ViewGroup parent) {
        View rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_grid_movie_card, parent, false);
        return new MovieGridViewHolder(rowView, true);
    }



    public class MovieGridViewHolder extends ListViewHolder implements View.OnClickListener {
        View container;
        TextView title;
        TextView subtitle;
        MMoviesImageView poster;
        ImageView contextMenu;
        View bottomContainer;

         public MovieGridViewHolder(View itemView, boolean isItem) {
             super(itemView, isItem);

             if (isItem) {
                 container = itemView.findViewById(R.id.card_content_holder);
                 container.setOnClickListener(this);
                 poster = (MMoviesImageView) itemView.findViewById(R.id.imageview_poster);
                 title = (TextView) itemView.findViewById(R.id.title);
                 subtitle = (TextView) itemView.findViewById(R.id.textview_subtitle_1);
                 bottomContainer = itemView.findViewById(R.id.bottom_container);
                 contextMenu = (ImageView) itemView.findViewById(R.id.context_menu);
                 contextMenu.setOnClickListener(this);
             }
         }

         @Override
         public void onClick(View v) {
             final int viewId = v.getId();
             switch (viewId) {
                 case R.id.card_content_holder : {
                     mClickListener.onClick(poster, getPosition());
                 }
                     break;
                 case R.id.context_menu:
                     mClickListener.onPopupMenuClick(contextMenu, getPosition());
                     break;
             }
         }
     }

}
