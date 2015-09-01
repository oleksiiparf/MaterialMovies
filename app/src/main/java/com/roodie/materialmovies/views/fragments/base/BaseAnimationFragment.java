package com.roodie.materialmovies.views.fragments.base;

import android.annotation.TargetApi;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ObjectAnimator;
import com.roodie.materialmovies.R;
import com.roodie.materialmovies.util.UiUtils;
import com.roodie.materialmovies.views.listeners.RotateAnimationListener;

import io.codetail.animation.SupportAnimator;
import io.codetail.animation.ViewAnimationUtils;
import io.codetail.animation.arcanimator.ArcAnimator;
import io.codetail.animation.arcanimator.Side;

/**
 * Created by Roodie on 12.08.2015.
 */
public abstract class BaseAnimationFragment extends BaseDetailFragment implements View.OnClickListener {

    protected static final String KEY_REVEAL_START_LOCATION = "reveal_start_location";
    protected static final String KEY_VIEW = "image_view";
    protected static final String KEY_IMAGE_URL = "_image";

    protected FrameLayout mAnimationLayout;
    protected ImageView mAnimationStarImageView;
    protected TextView mAnimationTextView;
    protected FloatingActionButton mFloatingButton;
    private FrameLayout mAnimationContainer;
    private FrameLayout mDataContainer;



    private float startAnimationX, startAnimationY;

    private int endAnimationX, endAnimationY;
    private  int startAnimationPairBottom;

    final static AccelerateInterpolator ACCELERATE = new AccelerateInterpolator();
    final static DecelerateInterpolator DECELERATE = new DecelerateInterpolator();
    final static AccelerateDecelerateInterpolator ACCELERATE_DECELERATE = new AccelerateDecelerateInterpolator();
    final static  Interpolator INTERPOLATOR = new DecelerateInterpolator();

    public static Interpolator getInterpolator() {
        return INTERPOLATOR;
    }

    protected boolean hasAnimationContainer() {
        return mAnimationContainer != null;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAnimationLayout = (FrameLayout) view.findViewById(R.id.animation_layout);
        mAnimationStarImageView =  (ImageView) view.findViewById(R.id.image_view_star);

        mAnimationTextView =  (TextView) view.findViewById(R.id.confirmation_text_view);
        mFloatingButton =  (FloatingActionButton) view.findViewById(R.id.like_fab);
        mAnimationContainer = (FrameLayout) view.findViewById(R.id.animation_container);
        mDataContainer = (FrameLayout) view.findViewById(R.id.data_container);

        if (hasAnimationContainer()) {
            mAnimationStarImageView.setVisibility(View.GONE);
            mAnimationTextView.setVisibility(View.GONE);
            mAnimationContainer.setVisibility(View.GONE);
            mDataContainer.setVisibility(View.VISIBLE);
        }

        initiaizeStartAnimation();

        if (mFloatingButton != null && hasAnimationContainer()) {
            mFloatingButton.setOnClickListener(this);
        }
    }


    @Override
    public void onClick(View v) {
        startAnimationX = UiUtils.centerX(mFloatingButton);
        startAnimationY = UiUtils.centerY(mFloatingButton);

        endAnimationX = mAnimationContainer.getRight() / 2;
        endAnimationY = (int) (mAnimationContainer.getBottom() * 0.8f);

        System.out.println("Positions: " + startAnimationX +  ", " + startAnimationY + ", " + endAnimationX + ", " + endAnimationY);

        if (endAnimationX == 0 && endAnimationY == 0) {
            endAnimationX = (int) UiUtils.centerX(mFloatingButton);
            endAnimationY = (int) UiUtils.centerY(mFloatingButton);
            startCircleAnimation();
        } else {
            ArcAnimator arcAnimator = ArcAnimator.createArcAnimator(mFloatingButton, endAnimationX,
                    endAnimationY, 90, Side.RIGHT)
                    .setDuration(500);

            arcAnimator.addListener(new SimpleListener() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    startCircleAnimation();
                }
            });
            arcAnimator.start();
        }

    }

    private void initiaizeStartAnimation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            configureEnterTransition ();
        } else {
            configureEnterAnimation ();
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    protected abstract void configureEnterTransition();

    protected void configureEnterAnimation() {
        setUpVisibility();
        initializePresenter();
    }

    private void startCircleAnimation() {
        mFloatingButton.setVisibility(View.INVISIBLE);
        mDataContainer.setVisibility(View.GONE);
        mAnimationContainer.setVisibility(View.VISIBLE);
        mAnimationLayout.setVisibility(View.VISIBLE);

        mAnimationLayout.post(new Runnable() {
            @Override
            public void run() {
                float finalRadius = Math.max(mAnimationLayout.getWidth(), mAnimationLayout.getHeight()) * 1.5f;

                SupportAnimator animator = ViewAnimationUtils.createCircularReveal(mAnimationLayout, endAnimationX, endAnimationY, mFloatingButton.getWidth() / 2f,
                        finalRadius);
                animator.setDuration(500);
                animator.setInterpolator(ACCELERATE);
                animator.addListener(new SimpleListener() {
                    @Override
                    public void onAnimationEnd() {
                        animateConfirmationView();
                    }
                });
                animator.start();
            }
        });
    }

    private void animateConfirmationView() {

        mAnimationStarImageView.setVisibility(View.VISIBLE);
        mAnimationTextView.setVisibility(View.VISIBLE);

        Drawable drawable = mAnimationStarImageView.getDrawable();

            Animation rotate = AnimationUtils.loadAnimation(getActivity().getApplicationContext(),
                    R.anim.appear_rotate);
        rotate.setAnimationListener(new RotateAnimationListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                super.onAnimationEnd(animation);
                mAnimationStarImageView.setVisibility(View.GONE);
                mAnimationTextView.setVisibility(View.GONE);
                disappearBackgroundAnimation();
            }
        });
            mAnimationStarImageView.startAnimation(rotate);
    }

    private void disappearBackgroundAnimation(){
        float finalRadius = Math.max(mAnimationLayout.getWidth(), mAnimationLayout.getHeight()) * 1.5f;

        SupportAnimator animator = ViewAnimationUtils.createCircularReveal(mAnimationLayout, endAnimationX, endAnimationY,
                finalRadius, 10);
        animator.setDuration(500);
        animator.addListener(new SimpleListener() {
            @Override
            public void onAnimationEnd() {
                mAnimationLayout.setVisibility(View.GONE);
                mAnimationContainer.setVisibility(View.GONE);
                mDataContainer.setVisibility(View.VISIBLE);
                returnFloatingButtonAnimation();
            }
        });
        animator.setInterpolator(DECELERATE);
        animator.start();
    }

    private void returnFloatingButtonAnimation(){
        mFloatingButton.setVisibility(View.VISIBLE);
        if (endAnimationX != startAnimationX && endAnimationY != startAnimationY) {
            ArcAnimator arcAnimator = ArcAnimator.createArcAnimator(mFloatingButton, startAnimationX,
                    startAnimationY, 90, Side.RIGHT)
                    .setDuration(500);
            arcAnimator.start();
        }
    }

    private void raiseUpAnimation(){
        startAnimationPairBottom = mAnimationLayout.getBottom();
        ObjectAnimator objectAnimator = ObjectAnimator.ofInt(mAnimationLayout, "bottom", mAnimationLayout.getBottom(), mAnimationLayout.getTop() + dpToPx(100));
        objectAnimator.addListener(new SimpleListener() {
            @Override
            public void onAnimationEnd(Animator animation) {

            }
        });
        objectAnimator.setInterpolator(ACCELERATE_DECELERATE);
        objectAnimator.start();
    }

    private void comeDownAnimation(){
        ObjectAnimator objectAnimator = ObjectAnimator.ofInt(mAnimationLayout, "bottom", mAnimationLayout.getBottom(), startAnimationPairBottom);
        objectAnimator.addListener(new SimpleListener() {
            @Override
            public void onAnimationEnd(Animator animator) {
            }
        });
        objectAnimator.setInterpolator(ACCELERATE_DECELERATE);
        objectAnimator.start();
    }

    public int dpToPx(int dp){
        DisplayMetrics displayMetrics = getActivity().getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    protected void setEndAnimationX(int endAnimationX) {
        this.endAnimationX = endAnimationX;
    }

    protected void setEndAnimationY(int endAnimationY) {
        this.endAnimationY = endAnimationY;
    }

    protected void setStartAnimationPairBottom(int startAnimationPairBottom) {
        this.startAnimationPairBottom = startAnimationPairBottom;
    }



    private static class SimpleListener implements SupportAnimator.AnimatorListener, ObjectAnimator.AnimatorListener{

        @Override
        public void onAnimationStart() {
        }

        @Override
        public void onAnimationEnd() {
        }

        @Override
        public void onAnimationCancel() {
        }

        @Override
        public void onAnimationRepeat() {
        }

        @Override
        public void onAnimationStart(Animator animation) {
        }

        @Override
        public void onAnimationEnd(Animator animation) {
        }

        @Override
        public void onAnimationCancel(Animator animation) {
        }

        @Override
        public void onAnimationRepeat(Animator animation) {
        }
    }

}
