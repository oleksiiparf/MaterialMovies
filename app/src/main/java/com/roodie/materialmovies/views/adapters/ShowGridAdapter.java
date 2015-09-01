package com.roodie.materialmovies.views.adapters;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Bitmap;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.common.base.Objects;
import com.roodie.materialmovies.R;
import com.roodie.materialmovies.views.custom_views.MMoviesImageView;
import com.roodie.materialmovies.views.listeners.RecyclerItemClickListener;
import com.roodie.model.entities.ListItem;
import com.roodie.model.entities.ShowWrapper;

import java.util.List;

/**
 * Created by Roodie on 15.08.2015.
 */
public class ShowGridAdapter extends RecyclerView.Adapter<ShowGridAdapter.ShowViewHolder> {

    private static final String LOG_TAG = ShowGridAdapter.class.getName();

    private static final AccelerateInterpolator ACCELERATE_INTERPOLATOR = new AccelerateInterpolator();
    private static final OvershootInterpolator OVERSHOOT_INTERPOLATOR = new OvershootInterpolator(4);

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
        View rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_grid_show_card, parent, false);
        return new ShowViewHolder(rowView, mClickListener);
    }

    @Override
    public void onBindViewHolder(final ShowViewHolder holder, int position) {
        final ShowWrapper show = getItem(position).getListItem();

        holder.title.setText(show.getTitle());
        holder.subtitle.setText("" + show.getPopularity());

        holder.poster.loadPoster(show, new MMoviesImageView.OnLoadedListener() {
            @Override
            public void onSuccess(MMoviesImageView imageView, Bitmap bitmap, String imageUrl) {
                holder.poster.setTag(imageUrl);
                Palette.generateAsync(bitmap, new Palette.PaletteAsyncListener() {
                    @Override
                    public void onGenerated(Palette palette) {
                        Palette.Swatch primary = palette.getVibrantSwatch();

                        if (primary == null) {
                            primary = palette.getMutedSwatch();
                        }

                        final int primaryAccent = primary.getRgb();
                        holder.containerBar.setBackgroundColor(primaryAccent);
                    }
                });
            }

            @Override
            public void onError(MMoviesImageView imageView) {

            }
        });

        updateHeartButton(show, holder, false);
        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateHeartButton(show, holder, true);
            }
        });

    }

    private void updateHeartButton(final ShowWrapper show, final ShowViewHolder holder, boolean animated) {
        if (animated) {
            if (!show.isLiked()) {
                show.setLiked(true);

                AnimatorSet animatorSet = new AnimatorSet();
                ObjectAnimator rotationAnim = ObjectAnimator.ofFloat(holder.like, "rotation", 0f, 360f);
                rotationAnim.setDuration(300);
                rotationAnim.setInterpolator(ACCELERATE_INTERPOLATOR);

                ObjectAnimator bounceAnimX = ObjectAnimator.ofFloat(holder.like, "scaleX", 0.2f, 1f);
                bounceAnimX.setDuration(300);
                bounceAnimX.setInterpolator(OVERSHOOT_INTERPOLATOR);

                ObjectAnimator bounceAnimY = ObjectAnimator.ofFloat(holder.like, "scaleY", 0.2f, 1f);
                bounceAnimY.setDuration(300);
                bounceAnimY.setInterpolator(OVERSHOOT_INTERPOLATOR);
                bounceAnimY.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        holder.like.setImageResource(R.drawable.ic_heart_red);
                    }
                });

                animatorSet.play(rotationAnim);
                animatorSet.play(bounceAnimX).with(bounceAnimY).after(rotationAnim);

                animatorSet.start();
            } else {
                show.setLiked(false);
                holder.like.setImageResource(R.drawable.ic_heart_white_24dp);
            }
        } else {
            if (show.isLiked()) {
                holder.like.setImageResource(R.drawable.ic_heart_red);
            } else {
                holder.like.setImageResource(R.drawable.ic_heart_white_24dp);
            }
        }

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
            final int viewId = v.getId();
            switch (viewId) {
                case R.id.item_show_container :
                    onClickListener.onClick(v, getPosition());
                    break;
            }

        }


    }
}
