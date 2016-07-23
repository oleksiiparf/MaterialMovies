package com.roodie.materialmovies.views.adapters;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.v7.graphics.Palette;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageButton;
import android.widget.TextView;

import com.arellomobile.mvp.MvpDelegate;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.PresenterType;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerviewViewHolder;
import com.marshalchen.ultimaterecyclerview.quickAdapter.easyRegularAdapter;
import com.roodie.materialmovies.R;
import com.roodie.materialmovies.mvp.presenters.TvShowPresenter;
import com.roodie.materialmovies.mvp.views.TvShowWatchedView;
import com.roodie.materialmovies.util.AnimUtils;
import com.roodie.materialmovies.views.custom_views.MMoviesImageView;
import com.roodie.materialmovies.views.listeners.RecyclerItemClickListener;
import com.roodie.model.entities.ShowWrapper;
import com.roodie.model.util.FileLog;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import io.fabric.sdk.android.Fabric;

/**
 * Created by Roodie on 15.08.2015.
 */
public class ShowsGridAdapter extends easyRegularAdapter<ShowWrapper, ShowsGridAdapter.ShowViewHolder> implements TvShowWatchedView {

    @InjectPresenter(type = PresenterType.GLOBAL, tag = TvShowPresenter.TAG)
    TvShowPresenter mPresenter;

    private MvpDelegate<? extends ShowsGridAdapter> mMvpDelegate;
    private MvpDelegate<?> mParentDelegate;
    private String mChildId;

    private Context context;

    private static final int SCALE_DELAY = 30;

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

    public ShowsGridAdapter(List<ShowWrapper> list, Context context, MvpDelegate<?> mParentDelegate, RecyclerItemClickListener mClickListener) {
        super(list);
        this.context = context;
        this.mParentDelegate = mParentDelegate;
        mChildId = String.valueOf(0);
        getMvpDelegate().onCreate();

        this.mClickListener = mClickListener;
    }

    @Override
    protected int getNormalLayoutResId() {
        return R.layout.item_grid_show_card;
    }

    @Override
    protected ShowViewHolder newViewHolder(View view) {
        return new ShowViewHolder(view, true);
    }

    @Override
    public ShowViewHolder newFooterHolder(View view) {
        return new ShowViewHolder(view, false);
    }

    @Override
    public ShowViewHolder newHeaderHolder(View view) {
        return new ShowViewHolder(view, false);
    }

    @Override
    protected void withBindHolder(final ShowViewHolder holder, final ShowWrapper data, final int position) {

        holder.title.setText(data.getTitle());
        if (data.getReleasedTime() > 0) {
            Date DATE = new Date(data.getReleasedTime());
            holder.subtitle.setText(dateFormat.format(DATE));
            holder.subtitle.setVisibility(View.VISIBLE);
        } else {
            holder.subtitle.setVisibility(View.GONE);
        }
        holder.poster.loadPoster(data, new MMoviesImageView.OnLoadedListener() {
            @Override
            public void onSuccess(MMoviesImageView imageView, Bitmap bitmap, String imageUrl) {
                //animateHolder(holder, position);
                Palette.generateAsync(bitmap, new Palette.PaletteAsyncListener() {
                    @Override
                    public void onGenerated(Palette palette) {
                        Palette.Swatch primary = palette.getVibrantSwatch();

                        if (primary != null) {
                            holder.title.setTextColor(primary.getTitleTextColor());
                            holder.subtitle.setTextColor(primary.getTitleTextColor());

                            AnimUtils.animateViewColor(holder.containerBar, context.getResources().getColor(R.color.color_primary_default), primary.getRgb());
                        } else {
                            FileLog.d("Palette", "Can`t get Swatch from Palette");
                            Fabric.getLogger().e("Palette", "Can`t get Swatch from Palette");
                        }

                    }
                });
            }

            @Override
            public void onError(MMoviesImageView imageView) {

            }
        });

        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    holder.poster.setTag(String.valueOf(position));
                    holder.poster.setTransitionName(holder.itemView.getResources().getString(R.string.transition_poster));
                }
                mClickListener.onClick(holder.poster, position);
            }
        });

        updateHeartButton(data, holder, false);
        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateHeartButton(data, holder, true);
                mPresenter.toggleShowWatched(data, position);
            }
        });
    }

    private void animateHolder(ShowViewHolder holder, int position) {
        if (!holder.animated) {

            holder.animated = true;
            holder.container.setScaleY(0);
            holder.container.setScaleX(0);
            holder.container.animate()
                    .scaleY(1).scaleX(1)
                    .setDuration(200)
                    .setStartDelay(SCALE_DELAY * position)
                    .start();
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

    public class ShowViewHolder extends UltimateRecyclerviewViewHolder {
        View container;
        View containerBar;
        TextView title;
        TextView subtitle;
        ImageButton like;
        MMoviesImageView poster;
        protected boolean animated = false;

        public ShowViewHolder(View itemView, boolean isItem) {
            super(itemView);

            if (isItem) {
                container = itemView.findViewById(R.id.item_show_container);
                containerBar = itemView.findViewById(R.id.content_bar);
                poster = (MMoviesImageView) itemView.findViewById(R.id.imageview_poster);
                title = (TextView) itemView.findViewById(R.id.title);
                subtitle = (TextView) itemView.findViewById(R.id.release);
                like = (ImageButton) itemView.findViewById(R.id.like_button);
            }
        }
    }
}
