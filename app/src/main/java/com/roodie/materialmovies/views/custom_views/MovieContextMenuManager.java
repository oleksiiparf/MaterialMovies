package com.roodie.materialmovies.views.custom_views;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.OvershootInterpolator;

import com.roodie.materialmovies.util.UiUtils;

/**
 * Created by Roodie on 31.08.2015.
 */

    /**
     * Action manager for custom popup menu
     */
    public class MovieContextMenuManager extends RecyclerView.OnScrollListener implements View.OnAttachStateChangeListener {

        private static MovieContextMenuManager instance;

        private MovieContextMenu contextMenuView;
        private boolean isContextMenuShowing = false;

        public static MovieContextMenuManager getInstance() {
            if (instance == null) {
                instance = new MovieContextMenuManager();
            }
            return instance;
        }

        private MovieContextMenuManager() {

        }

    public void toggleContextMenuFromView(View openingView, int movieItem, MovieContextMenu.OnMovieContextMenuItemClickListener listener) {
            if (contextMenuView == null) {
                showContextMenuFromView(openingView, movieItem,  listener);
            } else {
                hideContextMenu();
            }
        }

        private void showContextMenuFromView(final View openingView, int movieItem, MovieContextMenu.OnMovieContextMenuItemClickListener listener) {
            if (!isContextMenuShowing) {
                //isContextMenuShowing = true;
                contextMenuView = new MovieContextMenu(openingView.getContext());
                contextMenuView.bindToItem(movieItem);
                contextMenuView.addOnAttachStateChangeListener(this);
                contextMenuView.setOnMovieMenuItemClickListener(listener);

                ((ViewGroup) openingView.getRootView().findViewById(android.R.id.content)).addView(contextMenuView);

                contextMenuView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        contextMenuView.getViewTreeObserver().removeOnPreDrawListener(this);
                        setupContextMenuInitialPosition(openingView);
                        performShowAnimation();
                        return false;
                    }
                });
            }
        }

        private void setupContextMenuInitialPosition(View openingView) {
            final int[] openingViewLocation = new int[2];
            openingView.getLocationOnScreen(openingViewLocation);
            int additionalBottomMargin = UiUtils.dpToPx(16);
            contextMenuView.setTranslationX(openingViewLocation[0] - contextMenuView.getWidth() / 3);
            contextMenuView.setTranslationY(openingViewLocation[1] - contextMenuView.getHeight() - additionalBottomMargin);
        }

        private void performShowAnimation() {
            contextMenuView.setPivotX(contextMenuView.getWidth() / 2);
            contextMenuView.setPivotY(contextMenuView.getHeight());
            contextMenuView.setScaleX(0.1f);
            contextMenuView.setScaleY(0.1f);
            contextMenuView.animate()
                    .scaleX(1f).scaleY(1f)
                    .setDuration(150)
                    .setInterpolator(new OvershootInterpolator())
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            isContextMenuShowing = true;
                        }
                    });
        }

        public void hideContextMenu() {
            if (isContextMenuShowing) {
                isContextMenuShowing = false;
                performDismissAnimation();
            }
        }

        private void performDismissAnimation() {
            contextMenuView.setPivotX(contextMenuView.getWidth() / 2);
            contextMenuView.setPivotY(contextMenuView.getHeight());
            contextMenuView.animate()
                    .scaleX(0.1f).scaleY(0.1f)
                    .setDuration(150)
                    .setInterpolator(new AccelerateInterpolator())
                    .setStartDelay(100)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            if (contextMenuView != null) {
                                contextMenuView.dismiss();
                            }
                        }
                    });
        }

        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            if (contextMenuView != null) {
                hideContextMenu();
                contextMenuView.setTranslationY(contextMenuView.getTranslationY() - dy);
            }
        }

        @Override
        public void onViewAttachedToWindow(View v) {

        }

        @Override
        public void onViewDetachedFromWindow(View v) {
            contextMenuView = null;
        }
    }
