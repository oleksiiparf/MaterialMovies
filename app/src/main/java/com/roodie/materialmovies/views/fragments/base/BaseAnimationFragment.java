package com.roodie.materialmovies.views.fragments.base;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ObjectAnimator;
import com.roodie.materialmovies.R;

import io.codetail.animation.SupportAnimator;
import io.codetail.animation.ViewAnimationUtils;

/**
 * Created by Roodie on 12.08.2015.
 */
public abstract class BaseAnimationFragment extends BaseDetailFragment {

    protected static final String KEY_REVEAL_START_LOCATION = "reveal_start_location";
    protected static final String KEY_VIEW = "image_view";
    protected static final String KEY_IMAGE_URL = "_image";

    protected FrameLayout mAnimationLayout;

    private int endAnimationX, endAnimationY;
    private  int startAnimationPairBottom;

    final static AccelerateInterpolator ACCELERATE = new AccelerateInterpolator();
    final static DecelerateInterpolator DECELERATE = new DecelerateInterpolator();
    final static AccelerateDecelerateInterpolator ACCELERATE_DECELERATE = new AccelerateDecelerateInterpolator();
    final static Interpolator INTERPOLATOR = new DecelerateInterpolator();

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAnimationLayout = (FrameLayout) view.findViewById(R.id.transaction_container);
        mAnimationLayout.setVisibility(View.GONE);

        initiaizeStartAnimation();
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

        mAnimationLayout.post(new Runnable() {
            @Override
            public void run() {
                setUpVisibility();
                startCircleAnimation();
            }
        });
    }

    private void startCircleAnimation() {
        mAnimationLayout.setVisibility(View.VISIBLE);

        float finalRadius = Math.max(mAnimationLayout.getWidth(), mAnimationLayout.getHeight()) * 1.5f;

        SupportAnimator animator = ViewAnimationUtils.createCircularReveal(mAnimationLayout, endAnimationX, endAnimationY, 20 / 2f,
                finalRadius);
        animator.setDuration(500);
        animator.setInterpolator(ACCELERATE);
        animator.addListener(new SimpleListener() {
            @Override
            public void onAnimationEnd() {
                raiseUpAnimation();
            }
        });
        animator.start();
    }

    private void raiseUpAnimation(){
        startAnimationPairBottom = mAnimationLayout.getBottom();
        ObjectAnimator objectAnimator = ObjectAnimator.ofInt(mAnimationLayout, "bottom", mAnimationLayout.getBottom(), mAnimationLayout.getTop() + dpToPx(100));
        objectAnimator.addListener(new SimpleListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                //  appearRed();
                mAnimationLayout.setVisibility(View.GONE);
                initializePresenter();

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
                disappearBackgroundAnimation();
            }
        });
        objectAnimator.setInterpolator(ACCELERATE_DECELERATE);
        objectAnimator.start();
    }

    private void disappearBackgroundAnimation(){
        float finalRadius = Math.max(mAnimationLayout.getWidth(), mAnimationLayout.getHeight()) * 1.5f;

        SupportAnimator animator = ViewAnimationUtils.createCircularReveal(mAnimationLayout, endAnimationX, endAnimationY,
                finalRadius, 10);
        animator.setDuration(500);
        animator.addListener(new SimpleListener() {
            @Override
            public void onAnimationEnd() {
                mAnimationLayout.setVisibility(View.INVISIBLE);
            }
        });
        animator.setInterpolator(DECELERATE);
        animator.start();
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

    public static Interpolator getInterpolator() {
        return INTERPOLATOR;
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
