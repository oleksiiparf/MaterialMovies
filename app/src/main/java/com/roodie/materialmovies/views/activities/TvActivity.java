package com.roodie.materialmovies.views.activities;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.TargetApi;
import android.app.SharedElementCallback;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;

import com.google.common.base.Preconditions;
import com.roodie.materialmovies.R;
import com.roodie.materialmovies.util.AnimUtils;
import com.roodie.materialmovies.util.CircularReveal;
import com.roodie.materialmovies.util.MMoviesPreferences;
import com.roodie.materialmovies.views.custom_views.MMoviesImageView;
import com.roodie.model.Display;
import com.roodie.model.util.FileLog;

import java.util.List;

/**
 * Created by Roodie on 16.09.2015.
 */
public class TvActivity extends BaseNavigationActivity {

    private boolean mIsReturning;

    private Fragment currentFragment;

    @Override
    protected int getContentViewLayoutId() {
        return R.layout.activity_no_drawer;
    }

    @Override
    protected void handleIntent(Intent intent, Display display) {
        if (!display.hasMainFragment()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && MMoviesPreferences.areAnimationsEnabled(this)) {
                currentFragment = display.showTvDetailFragmentBySharedElement(intent.getStringExtra(Display.PARAM_ID));
            } else {
                display.showTvDetailFragment(intent.getStringExtra(Display.PARAM_ID));
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && MMoviesPreferences.areAnimationsEnabled(this)) {
            setupWindowAnimations();
            animateManually(true);
        } else {
            animateManually(false);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        currentFragment = null;
    }

    @TargetApi(21)
    private void setupWindowAnimations() {
        postponeEnterTransition();
        setEnterSharedElementCallback(new SharedElementCallback() {
            @Override
            public void onSharedElementStart(List<String> sharedElementNames, List<View> sharedElements, List<View> sharedElementSnapshots) {
                FileLog.d("animations", "TvActivity: onSharedElementStart");
                if (!mIsReturning) {
                    getWindow().setEnterTransition(makeEnterTransition(getSharedElement(sharedElements)));
                }
            }

            @Override
            public void onSharedElementEnd(List<String> sharedElementNames, List<View> sharedElements, List<View> sharedElementSnapshots) {
             /*   FileLog.d("animations", "TvActivity: onSharedElementEnd");
                if (!mIsReturning) {
                    getWindow().setReturnTransition(makeReturnTransition());
                 }*/
            }

            private View getSharedElement(List<View> sharedElements) {
                for (final View view : sharedElements) {
                    if (view instanceof ImageView) {
                        return view;
                    }
                }
                return null;
            }
        });
        // We are not interested in defining a new Enter Transition. Instead we change default transition duration
        getWindow().getEnterTransition().setDuration(getResources().getInteger(R.integer.anim_duration_medium));
    }

    @TargetApi(21)
    private void setupFinishAnimations() {
        getWindow().setReturnTransition(makeReturnTransition());
    }

    @TargetApi(21)
    private Transition makeEnterTransition(View sharedElement) {
        Preconditions.checkNotNull(currentFragment, "Current Fragment cannot be null");
        View rootView = currentFragment.getView();

        assert rootView != null;

        TransitionSet enterTransition = new TransitionSet();

        if (sharedElement != null) {
            // Play a circular reveal animation starting beneath the shared element.
            Transition circularReveal = new CircularReveal(sharedElement);
            circularReveal.addTarget(rootView.findViewById(R.id.data_container));
            enterTransition.addTransition(circularReveal);
        }

        // Slide the cards in through the bottom of the screen.
        Transition cardSlide = new Slide(Gravity.BOTTOM);
        cardSlide.addTarget(rootView.findViewById(R.id.primary_recycler_view));
        enterTransition.addTransition(cardSlide);

        // Don't fade the navigation/status bars.
        Transition fade = new Fade();
        fade.excludeTarget(android.R.id.navigationBarBackground, true);
        fade.excludeTarget(android.R.id.statusBarBackground, true);
        enterTransition.addTransition(fade);

        final Resources res = getResources();
        final MMoviesImageView backgroundImage = (MMoviesImageView) rootView.findViewById(R.id.fanart_image);
        backgroundImage.setAlpha(0f);
        final FloatingActionButton fButton = (FloatingActionButton) rootView.findViewById(R.id.button_fab);
        if (fButton != null) {
            fButton.setAlpha(0f);
        }
        enterTransition.addListener(new AnimUtils.TransitionListenerAdapter() {
            @Override
            public void onTransitionEnd(Transition transition) {
                backgroundImage.animate().alpha(1f).setDuration(res.getInteger(R.integer.image_background_fade_millis));

                if (fButton != null) {
                    // we rely on the window enter content transition to show the fab. This isn't run on
                    // orientation changes so manually show it.
                    Animator showFab = ObjectAnimator.ofPropertyValuesHolder(fButton,
                            PropertyValuesHolder.ofFloat(View.ALPHA, 0f, 1f),
                            PropertyValuesHolder.ofFloat(View.SCALE_X, 0f, 1f),
                            PropertyValuesHolder.ofFloat(View.SCALE_Y, 0f, 1f));
                    showFab.setStartDelay(300L);
                    showFab.setDuration(300L);
                    showFab.setInterpolator(AnimUtils.getLinearOutSlowInInterpolator(TvActivity.this));
                    showFab.start();
                }

            }
        });
        enterTransition.setDuration(getResources().getInteger(R.integer.anim_duration_long));
        return enterTransition;
    }

    @TargetApi(21)
    private Transition makeReturnTransition() {
        Preconditions.checkNotNull(currentFragment, "Current Fragment cannot be null");
        View rootView = currentFragment.getView();
        assert rootView != null;

        final FloatingActionButton fButton = (FloatingActionButton) rootView.findViewById(R.id.button_fab);
        if (fButton != null) {
            fButton.setAlpha(0f);
        }

        TransitionSet returnTransition = new TransitionSet();

        // Slide and fade the circular reveal container off the top of the screen.
        TransitionSet slideFade = new TransitionSet();
        slideFade.addTarget(rootView.findViewById(R.id.data_container));
        slideFade.addTransition(new Slide(Gravity.TOP));
        slideFade.addTransition(new Fade());
        returnTransition.addTransition(slideFade);

        // Slide the cards off the bottom of the screen.
        Transition cardSlide = new Slide(Gravity.BOTTOM);
        cardSlide.addTarget(rootView.findViewById(R.id.primary_recycler_view));
        returnTransition.addTransition(cardSlide);

        returnTransition.setDuration(getResources().getInteger(R.integer.anim_duration_medium));
        return returnTransition;
    }

   @Override
    public void finishAfterTransition() {
       mIsReturning = true;
       if (currentFragment == null || !MMoviesPreferences.areAnimationsEnabled(this)) {
           animateManually(false);
           finish();
       } else {
          // setupFinishAnimations();
           super.finishAfterTransition();
       }
    }
}
