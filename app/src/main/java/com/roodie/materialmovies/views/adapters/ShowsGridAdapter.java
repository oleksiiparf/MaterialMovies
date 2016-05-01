package com.roodie.materialmovies.views.adapters;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.graphics.Palette;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageButton;
import android.widget.TextView;

import com.arellomobile.mvp.MvpDelegate;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.PresenterType;
import com.roodie.materialmovies.R;
import com.roodie.materialmovies.mvp.presenters.TvShowPresenter;
import com.roodie.materialmovies.mvp.views.TvShowWatchedView;
import com.roodie.materialmovies.views.custom_views.MMoviesImageView;
import com.roodie.materialmovies.views.listeners.RecyclerItemClickListener;
import com.roodie.model.entities.ShowWrapper;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Roodie on 15.08.2015.
 */
public class ShowsGridAdapter extends FooterViewListAdapter<List<ShowWrapper>, ShowsGridAdapter.ShowViewHolder> implements TvShowWatchedView {

    @InjectPresenter(type = PresenterType.GLOBAL, tag = TvShowPresenter.TAG)
    TvShowPresenter mPresenter;

    private MvpDelegate<? extends ShowsGridAdapter> mMvpDelegate;
    private MvpDelegate<?> mParentDelegate;
    private String mChildId;

    DateFormat dateFormat = DateFormat.getDateInstance();

    private static final String LOG_TAG = ShowsGridAdapter.class.getName();

    private static final AccelerateInterpolator ACCELERATE_INTERPOLATOR = new AccelerateInterpolator();
    private static final OvershootInterpolator OVERSHOOT_INTERPOLATOR = new OvershootInterpolator(4);

    private RecyclerItemClickListener mClickListener;

    public MvpDelegate getMvpDelegate() {
        if (mMvpDelegate == null) {
            mMvpDelegate = new MvpDelegate<>(this);
            mMvpDelegate.setParentDelegate(mParentDelegate, mChildId);
        }
        return mMvpDelegate;
    }

    public ShowsGridAdapter(Context context,MvpDelegate<?> parentDelegate, RecyclerItemClickListener mClickListener) {
        super(context);
        this.mClickListener = mClickListener;

        mParentDelegate = parentDelegate;
        mChildId = String.valueOf(0);

        getMvpDelegate().onCreate();
    }

    @Override
    public ShowViewHolder getViewHolder(View view) {
        return new ShowViewHolder(view, false);
    }

    @Override
    public ShowViewHolder onCreateViewHolder(ViewGroup parent) {
        View rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_grid_show_card, parent, false);
        return new ShowViewHolder(rowView, true);
    }

    @Override
    public void onBindViewHolder(final ShowViewHolder holder, final int position) {
        if (position < getTotalItemsCount() && position < getItemCount()) {

            final ShowWrapper show = items.get(position);

            holder.title.setText(show.getTitle());
            if (show.getReleasedTime() > 0) {
                Date DATE = new Date(show.getReleasedTime());
                holder.subtitle.setText(dateFormat.format(DATE));
                holder.subtitle.setVisibility(View.VISIBLE);
            } else {
                holder.subtitle.setVisibility(View.GONE);
            }
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

                            //Issue : getRgb() may produce NPE
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
                    mPresenter.toggleShowWatched(show, position);
                }
            });
        }

    }

    @Override
    public void updateShowWatched(ShowWrapper item, int position) {
       /* if(items != null) {
          items.set(position, item);
            notifyDataSetChanged();
        }*/
    }

    private void updateHeartButton(final ShowWrapper show, final ShowViewHolder holder, boolean animated) {
        if (animated) {
            if (!show.isWatched()) {
                //show.setWatched(true);

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
               // show.setWatched(false);
                holder.like.setImageResource(R.drawable.ic_heart_white_24dp);
            }
        } else {
            if (show.isWatched()) {
                holder.like.setImageResource(R.drawable.ic_heart_red);
            } else {
                holder.like.setImageResource(R.drawable.ic_heart_white_24dp);
            }
        }

    }

    public class ShowViewHolder extends ListViewHolder implements View.OnClickListener{
        View container;
        View containerBar;
        TextView title;
        TextView subtitle;
        ImageButton like;
        MMoviesImageView poster;

        public ShowViewHolder(View itemView, boolean isItem) {
            super(itemView, isItem);

            if (isItem) {
                container = itemView.findViewById(R.id.item_show_container);
                containerBar = itemView.findViewById(R.id.content_bar);
                container.setOnClickListener(this);
                poster = (MMoviesImageView) itemView.findViewById(R.id.imageview_poster);
                title = (TextView) itemView.findViewById(R.id.title);
                subtitle = (TextView) itemView.findViewById(R.id.release);
                like = (ImageButton) itemView.findViewById(R.id.like_button);
            }
        }

        @Override
        public void onClick(View v) {
            final int viewId = v.getId();
            switch (viewId) {
                case R.id.item_show_container :
                    mClickListener.onClick(poster, getPosition());
                    break;
            }

        }

    }
}
