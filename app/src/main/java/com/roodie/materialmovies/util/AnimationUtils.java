package com.roodie.materialmovies.util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Dialog;
import android.os.Build;
import android.view.View;
import android.view.ViewAnimationUtils;

/**
 * Created by Roodie on 20.08.2015.
 */
public class AnimationUtils {

        public static final int SCALE_FACTOR = 30;

        public static int getRadius(View paramView)
        {
            return paramView.getWidth();
        }

        public static int getX(View paramView)
        {
            return (paramView.getLeft() + paramView.getRight()) / 2;
        }

        public static int getY(View paramView)
        {
            return (paramView.getTop() + paramView.getBottom()) / 2;
        }

        public static void hideBackMenu(final View paramView)
        {
            if (Build.VERSION.SDK_INT >= 21)
            {
                Animator localAnimator = ViewAnimationUtils.createCircularReveal(paramView, paramView.getRight(), paramView.getBottom(), paramView.getWidth(), 0.0F);
                localAnimator.addListener(new AnimatorListenerAdapter()
                {
                    public void onAnimationEnd(Animator paramAnimator)
                    {
                        super.onAnimationEnd(paramAnimator);
                        paramView.setVisibility(View.INVISIBLE);
                    }
                });
                localAnimator.start();
                return;
            }
            paramView.setVisibility(View.INVISIBLE);
        }

        public static void hideImageCircular(final View paramView, final Dialog paramDialog)
        {
            if (Build.VERSION.SDK_INT >= 21)
            {
                Animator localAnimator = ViewAnimationUtils.createCircularReveal(paramView, getX(paramView), getY(paramView), getRadius(paramView), 0.0F);
                localAnimator.setDuration(800L);
                localAnimator.addListener(new AnimatorListenerAdapter()
                {
                    public void onAnimationEnd(Animator paramAnimator)
                    {
                        super.onAnimationEnd(paramAnimator);
                        paramView.setVisibility(View.INVISIBLE);
                        if (paramDialog != null)
                            paramDialog.dismiss();
                    }
                });
                localAnimator.start();
                return;
            }
            paramView.setVisibility(View.INVISIBLE);
        }

        public static void hideViewByScale(View paramView)
        {
            paramView.animate().setStartDelay(30L).scaleX(0.0F).scaleY(0.0F).start();
        }

        public static void revealImageCircular(final View paramView)
        {
            if (Build.VERSION.SDK_INT >= 21)
            {
                Animator localAnimator = ViewAnimationUtils.createCircularReveal(paramView, getX(paramView), getY(paramView), 0.0F, getRadius(paramView));
                localAnimator.setDuration(800L);
                localAnimator.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationStart(Animator paramAnimator) {
                        super.onAnimationStart(paramAnimator);
                        paramView.setVisibility(View.VISIBLE);
                    }
                });
                localAnimator.start();
                return;
            }
            paramView.setVisibility(View.VISIBLE);
        }

        public static void showBackMenu(final View paramView)
        {
            if (Build.VERSION.SDK_INT >= 21)
            {
                Animator localAnimator = ViewAnimationUtils.createCircularReveal(paramView, paramView.getRight(),
                        paramView.getBottom(), 0.0F, Math.max(paramView.getWidth(), paramView.getHeight()));
                paramView.setVisibility(View.VISIBLE);
                localAnimator.start();
                return;
            }
            paramView.setVisibility(View.VISIBLE);
        }

        public static void showViewByScale(final View paramView)
        {
            paramView.animate().setStartDelay(300L).scaleX(1.0F).scaleY(1.0F).start();
        }
    }
