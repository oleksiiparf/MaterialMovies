package com.roodie.materialmovies.views.adapters;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.common.base.Objects;
import com.roodie.materialmovies.R;
import com.roodie.materialmovies.views.custom_views.MMoviesImageView;
import com.roodie.materialmovies.views.listeners.RecyclerItemClickListener;
import com.roodie.model.entities.ListItem;
import com.roodie.model.entities.MovieWrapper;

import java.text.DateFormat;
import java.util.List;

/**
 * Created by Roodie on 29.06.2015.
 */
public class MovieGridAdapter extends RecyclerView.Adapter<MovieGridAdapter.MovieViewHolder> {

    private static final String LOG_TAG = MovieGridAdapter.class.getName();

    private DateFormat movieReleaseDate = DateFormat.getDateInstance(DateFormat.MEDIUM);

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
        View rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_grid_movie_card, parent, false);
        return new MovieViewHolder(rowView, mClickListener);
    }

    @Override
    public void onBindViewHolder(final MovieViewHolder holder, int position) {
        final MovieWrapper movie = getItem(position).getListItem();

        holder.title.setText(movie.getTitle());
        if (movie.getReleaseDate() != null) {
            holder.subtitle.setText(movieReleaseDate.format(movie.getReleaseDate()));
        } else {
            holder.subtitle.setText("");
        }
        //load poster
        holder.poster.setAutoFade(true);
        holder.poster.loadPoster(movie, new MMoviesImageView.OnLoadedListener() {
            @Override
            public void onSuccess(MMoviesImageView imageView, Bitmap bitmap, String imageUrl) {
                holder.poster.setTag(imageUrl);

                /*
                Palette.generateAsync(bitmap, new Palette.PaletteAsyncListener() {
                    @Override
                    public void onGenerated(Palette palette) {

                        final Palette.Swatch darkVibrantSwatch = palette.getDarkVibrantSwatch();
                        final Palette.Swatch darkMutedSwatch = palette.getDarkMutedSwatch();
                        final Palette.Swatch lightVibrantSwatch = palette.getLightVibrantSwatch();
                        final Palette.Swatch lightMutedSwatch = palette.getLightMutedSwatch();

                        final Palette.Swatch textColor = (darkVibrantSwatch != null)
                                ? darkVibrantSwatch : darkMutedSwatch;

                        final Palette.Swatch contentColor = (darkVibrantSwatch != null)
                                ? lightVibrantSwatch : lightMutedSwatch;

                        holder.title.setTextColor(textColor.getRgb());
                        holder.subtitle.setTextColor(textColor.getRgb());
                        if (contentColor != null) {
                            holder.bottomContainer.setBackgroundColor(contentColor.getRgb());
                        }
                    }
                });
                */

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
        TextView subtitle;
        MMoviesImageView poster;
        ImageView contextMenu;
        View bottomContainer;

         public MovieViewHolder(View itemView, RecyclerItemClickListener listener) {
             super(itemView);

             container = itemView.findViewById(R.id.card_content_holder);
             container.setOnClickListener(this);
             poster = (MMoviesImageView) itemView.findViewById(R.id.poster);
             title = (TextView) itemView.findViewById(R.id.title);
             subtitle = (TextView) itemView.findViewById(R.id.subtitle_1);
             bottomContainer = itemView.findViewById(R.id.bottom_container);
             contextMenu = (ImageView) itemView.findViewById(R.id.context_menu);
             contextMenu.setOnClickListener(this);
             this.onClickListener = listener;
         }

         @Override
         public void onClick(View v) {
             final int viewId=  v.getId();
             switch (viewId) {
                 case R.id.card_content_holder :
                     onClickListener.onClick(poster, getPosition());
                     break;
                 case R.id.context_menu:
                     onClickListener.onPopupMenuClick(contextMenu, getPosition());
                     break;
             }
         }
     }

}
