package com.roodie.materialmovies.util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicBlur;
import android.transition.Transition;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewPropertyAnimator;
import android.view.animation.Interpolator;
import android.widget.ImageView;

import com.google.common.base.Objects;
import com.roodie.materialmovies.R;

/**
 * Created by Roodie on 20.08.2015.
 */

/**
 * This is a set of helper methods using for showing various "animations" in the app.
 */
public class AnimUtils {

        private static final String LOG_TAG = AnimUtils.class.getSimpleName();

        private static Interpolator fastOutSlowIn;
        private static Interpolator fastOutLinearIn;
        private static Interpolator linearOutSlowIn;

        /**
         * Turn on when you're interested in fading animation. Intentionally untied from other debug
         * settings.
         */
        private static final boolean FADE_DBG = false;

        /**
         * Duration for animations in msec, which can be used with {@link ViewPropertyAnimator#setDuration(long)}
         * for example.
         */
        public static final int ANIMATION_DURATION = 500;

        private AnimUtils() {
        }

        private static final float BITMAP_BLUR_SCALE = 0.3f;
    //Set the radius of the Blur. Supported range 0 < radius <= 25
        private static final float BITMAP_BLUR_RADIUS = 20.0f;

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

        public static void hideViewByScale(View paramView) {
            paramView.animate().setStartDelay(30L).scaleX(0.0F).scaleY(0.0F).start();
        }

        public static void revealImageCircular(final View paramView) {
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

        public static void showBackMenu(final View paramView) {
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

    /**
     * Simple Utility class that runs fading animations on specified views.
     */
    public static class Fade {

        // View tag that's set during the fade-out animation; see hide() and
        // isFadingOut().
        private static final int FADE_STATE_KEY = R.id.fade_state;

        private static final String FADING_OUT = "fading_out";

        /**
         * Sets the visibility of the specified view to View.VISIBLE and then fades it in. If the
         * view is already visible (and not in the middle of a fade-out animation), this method will
         * return without doing anything.
         *
         * @param view The view to be faded in
         */
        public static void show(final View view) {
            if (FADE_DBG) log("Fade: SHOW view " + view + "...");
            if (FADE_DBG) log("Fade: - visibility = " + view.getVisibility());

            if (view.getVisibility() != View.VISIBLE || isFadingOut(view)) {
                view.animate().cancel();
                // ...and clear the FADE_STATE_KEY tag in case we just
                // canceled an in-progress fade-out animation.
                view.setTag(FADE_STATE_KEY, null);

                view.setAlpha(0);
                view.setVisibility(View.VISIBLE);
                view.animate().setDuration(ANIMATION_DURATION);
                view.animate().alpha(1);
                if (FADE_DBG) {
                    log("Fade: ==> SHOW " + view
                            + " DONE.  Set visibility = " + View.VISIBLE);
                }
            } else {
                if (FADE_DBG) log("Fade: ==> Ignoring, already visible AND not fading out.");
            }
        }

        /**
         * Fades out the specified view and then sets its visibility to the specified value (either
         * View.INVISIBLE or View.GONE). If the view is not currently visibile, the method will
         * return without doing anything.
         *
         * Note that *during* the fade-out the view itself will still have visibility View.VISIBLE,
         * although the isFadingOut() method will return true (in case the UI code needs to detect
         * this state.)
         *
         * @param view       The view to be hidden
         * @param visibility The value to which the view's visibility will be set after it fades
         *                   out. Must be either View.INVISIBLE or View.GONE.
         */
        public static void hide(final View view, final int visibility) {
            if (FADE_DBG) log("Fade: HIDE view " + view + "...");
            if (view.getVisibility() == View.VISIBLE &&
                    (visibility == View.INVISIBLE || visibility == View.GONE)) {

                // Use a view tag to mark this view as being in the middle
                // of a fade-out animation.
                view.setTag(FADE_STATE_KEY, FADING_OUT);

                view.animate().cancel();
                view.animate().setDuration(ANIMATION_DURATION);
                view.animate().alpha(0f).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        view.setAlpha(1);
                        view.setVisibility(visibility);
                        view.animate().setListener(null);
                        // ...and we're done with the fade-out, so clear the view tag.
                        view.setTag(FADE_STATE_KEY, null);
                        if (FADE_DBG) {
                            log("Fade: HIDE " + view
                                    + " DONE.  Set visibility = " + visibility);
                        }
                    }
                });
            }
        }

        /**
         * @return true if the specified view is currently in the middle of a fade-out animation.
         * (During the fade-out, the view's visibility is still VISIBLE, although in many cases the
         * UI should behave as if it's already invisible or gone.  This method allows the UI code to
         * detect that state.)
         * @see #hide(View, int)
         */
        public static boolean isFadingOut(final View view) {
            if (FADE_DBG) {
                log("Fade: isFadingOut view " + view + "...");
                log("Fade:   - getTag() returns: " + view.getTag(FADE_STATE_KEY));
                log("Fade:   - returning: " + (view.getTag(FADE_STATE_KEY) == FADING_OUT));
            }
            return (view.getTag(FADE_STATE_KEY) == FADING_OUT);
        }

    }

    private static void log(String msg) {
        Log.d(LOG_TAG, msg);
    }

    /**
     * Drawable achieving cross-fade, just like TransitionDrawable. We can have call-backs via
     * animator object (see also {@link CrossFadeDrawable#getAnimator()}).
     */
    private static class CrossFadeDrawable extends LayerDrawable {

        private final ObjectAnimator mAnimator;
        private int mCrossFadeAlpha;

        public CrossFadeDrawable(Drawable... layers) {
            super(layers);
            mAnimator = ObjectAnimator.ofInt(this, "crossFadeAlpha", 0xff, 0);
        }

        /**
         * This will be used from ObjectAnimator. Note: this method is protected by proguard.flags
         * so that it won't be removed automatically.
         */
        @SuppressWarnings("unused")
        public void setCrossFadeAlpha(int alpha) {
            mCrossFadeAlpha = alpha;
            invalidateSelf();
        }

        public ObjectAnimator getAnimator() {
            return mAnimator;
        }

        @Override
        public void draw(Canvas canvas) {
            Drawable first = getDrawable(0);
            Drawable second = getDrawable(1);

            if (mCrossFadeAlpha > 0) {
                first.setAlpha(mCrossFadeAlpha);
                first.draw(canvas);
                first.setAlpha(255);
            }

            if (mCrossFadeAlpha < 0xff) {
                second.setAlpha(0xff - mCrossFadeAlpha);
                second.draw(canvas);
                second.setAlpha(0xff);
            }
        }
    }

    private static boolean drawableEquals(Drawable first, Drawable second) {
        return first.equals(second) ||
                (first instanceof BitmapDrawable &&
                        second instanceof BitmapDrawable &&
                        Objects.equal(
                                ((BitmapDrawable) first).getBitmap(),
                                ((BitmapDrawable) second).getBitmap()));
    }

    /**
     * Starts cross-fade animation using TransitionDrawable. Nothing will happen if "from" and "to"
     * are the same.
     */
    public static void startCrossFade(final ImageView imageView, final Drawable from,
                                      final Drawable to) {

        // We skip the cross-fade when those two Drawables are equal, or they are BitmapDrawables
        // pointing to the same Bitmap.
        if (drawableEquals(from, to)) {
            imageView.setImageDrawable(to);
            return;
        }

        CrossFadeDrawable crossFadeDrawable = new CrossFadeDrawable(from, to);
        imageView.setImageDrawable(crossFadeDrawable);

        ObjectAnimator animator = crossFadeDrawable.getAnimator();
        animator.setDuration(ANIMATION_DURATION);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                if (FADE_DBG) {
                    log("cross-fade animation start ("
                            + Integer.toHexString(from.hashCode()) + " -> "
                            + Integer.toHexString(to.hashCode()) + ")");
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (FADE_DBG) {
                    log("cross-fade animation ended ("
                            + Integer.toHexString(from.hashCode()) + " -> "
                            + Integer.toHexString(to.hashCode()) + ")");
                }
                animation.removeAllListeners();
                // Workaround for issue 6300562; this will force the drawable to the
                // resultant one regardless of animation glitch.
                imageView.setImageDrawable(to);
            }
        });

        animator.start();
    }

    /**
     * Make image blur effect using ScriptIntrinsicBlur from the RenderScript library.
     */
    public static void makeBlur(final ImageView imageView, final Bitmap image) {
        int width = Math.round(image.getWidth() * BITMAP_BLUR_SCALE);
        int height = Math.round(image.getHeight() * BITMAP_BLUR_SCALE);

        Bitmap inputBitmap = Bitmap.createScaledBitmap(image, width, height, false);
        Bitmap outputBitmap = Bitmap.createBitmap(inputBitmap);

        RenderScript rs = RenderScript.create(imageView.getContext());
        ScriptIntrinsicBlur theIntrinsic = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        Allocation tmpIn = Allocation.createFromBitmap(rs, inputBitmap);
        Allocation tmpOut = Allocation.createFromBitmap(rs, outputBitmap);
        theIntrinsic.setRadius(BITMAP_BLUR_RADIUS);
        theIntrinsic.setInput(tmpIn);
        theIntrinsic.forEach(tmpOut);
        tmpOut.copyTo(outputBitmap);

        imageView.setImageDrawable(new BitmapDrawable(imageView.getResources(), outputBitmap));
    }

    public static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }


    public static Interpolator getFastOutSlowInInterpolator(Context context) {
        if (fastOutSlowIn == null) {
            fastOutSlowIn = android.view.animation.AnimationUtils.loadInterpolator(context,
                    android.R.interpolator.fast_out_slow_in);
        }
        return fastOutSlowIn;
    }

    public static Interpolator getFastOutLinearInInterpolator(Context context) {
        if (fastOutLinearIn == null) {
            fastOutLinearIn = android.view.animation.AnimationUtils.loadInterpolator(context,
                    android.R.interpolator.fast_out_linear_in);
        }
        return fastOutLinearIn;
    }

    public static Interpolator getLinearOutSlowInInterpolator(Context context) {
        if (linearOutSlowIn == null) {
            linearOutSlowIn = android.view.animation.AnimationUtils.loadInterpolator(context,
                    android.R.interpolator.linear_out_slow_in);
        }
        return linearOutSlowIn;
    }

    @SuppressLint("NewApi")
    public static class TransitionListenerAdapter implements Transition.TransitionListener {

        @Override
        public void onTransitionStart(Transition transition) {

        }

        @Override
        public void onTransitionEnd(Transition transition) {

        }

        @Override
        public void onTransitionCancel(Transition transition) {

        }

        @Override
        public void onTransitionPause(Transition transition) {

        }

        @Override
        public void onTransitionResume(Transition transition) {

        }
    }


    }
