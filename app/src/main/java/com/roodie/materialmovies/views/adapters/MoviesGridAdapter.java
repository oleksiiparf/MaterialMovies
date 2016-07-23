package com.roodie.materialmovies.views.adapters;

import android.graphics.Bitmap;
import android.os.Build;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.marshalchen.ultimaterecyclerview.UltimateRecyclerviewViewHolder;
import com.marshalchen.ultimaterecyclerview.quickAdapter.easyRegularAdapter;
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
public class MoviesGridAdapter extends easyRegularAdapter<MovieWrapper, MoviesGridAdapter.MovieGridViewHolder> {

    private static final String LOG_TAG = MoviesGridAdapter.class.getName();

    private DateFormat movieReleaseDate = DateFormat.getDateInstance(DateFormat.MEDIUM);

    private RecyclerItemClickListener mClickListener;

    public MoviesGridAdapter(List<MovieWrapper> list, RecyclerItemClickListener mClickListener) {
        super(list);
        this.mClickListener = mClickListener;
    }

    @Override
    protected int getNormalLayoutResId() {
        return R.layout.item_grid_movie_card;
    }

    @Override
    protected MovieGridViewHolder newViewHolder(View view) {
        return new MovieGridViewHolder(view, true);
    }

    @Override
    public MovieGridViewHolder newFooterHolder(View view) {
        return new MovieGridViewHolder(view, false);
    }

    @Override
    public MovieGridViewHolder newHeaderHolder(View view) {
        return new MovieGridViewHolder(view, false);
    }

    @Override
    protected void withBindHolder(final MovieGridViewHolder holder, MovieWrapper data, final int position) {

        holder.title.setText(data.getTitle());

        if (data.getReleasedTime() > 0) {
            Date DATE = new Date(data.getReleasedTime());
            DateFormat dateFormat = DateFormat.getDateInstance();
            holder.subtitle.setText(dateFormat.format(DATE));
        } else {
            holder.subtitle.setText("");
        }
        //load poster
        holder.poster.setAutoFade(true);
        holder.poster.loadPoster(data, new MMoviesImageView.OnLoadedListener() {
            @Override
            public void onSuccess(MMoviesImageView imageView, Bitmap bitmap, String imageUrl) {
                holder.poster.setTag(imageUrl);
            }

            @Override
            public void onError(MMoviesImageView imageView) {

            }
        });

        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    holder.poster.setTransitionName(holder.itemView.getResources().getString(R.string.transition_poster));
                }
                mClickListener.onClick(holder.poster, position);
            }
        });

        holder.contextMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickListener.onPopupMenuClick(holder.contextMenu, position);
            }
        });
    }

    public class MovieGridViewHolder extends UltimateRecyclerviewViewHolder {
        View container;
        TextView title;
        TextView subtitle;
        MMoviesImageView poster;
        ImageView contextMenu;
        View bottomContainer;

         public MovieGridViewHolder(View itemView, boolean isItem) {
             super(itemView);

             if (isItem) {
                 container = itemView.findViewById(R.id.card_content_holder);
                 poster = (MMoviesImageView) itemView.findViewById(R.id.imageview_poster);
                 title = (TextView) itemView.findViewById(R.id.title);
                 subtitle = (TextView) itemView.findViewById(R.id.textview_subtitle_1);
                 bottomContainer = itemView.findViewById(R.id.bottom_container);
                 contextMenu = (ImageView) itemView.findViewById(R.id.context_menu);
             }
         }
     }

}
