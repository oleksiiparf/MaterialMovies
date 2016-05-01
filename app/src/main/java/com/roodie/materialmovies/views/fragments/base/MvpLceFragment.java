package com.roodie.materialmovies.views.fragments.base;

import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;

import com.roodie.materialmovies.mvp.views.MvpLceView;

/**
 * Created by Roodie on 12.02.2016.
 */
public abstract class MvpLceFragment<CV extends View, M, V extends MvpLceView<M>> extends BaseMvpFragment implements MvpLceView<M> {

    private static final String LOG_TAG = MvpLceFragment.class.getSimpleName();

    protected View mProgressView;
    protected CV mContentView;
    boolean mContentShown;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

       // mProgressView = view.findViewById(R.id.loading_view);
       // mContentView = (CV) view.findViewById(R.id.content_view);

      /*  if (mProgressView == null) {
            throw new NullPointerException(
                    "Loading view is null! Have you specified a loading view in your layout xml file?"
                            + " You have to give your loading View the id R.id.loading_view");
        }

        if (mContentView == null) {
            throw new NullPointerException(
                    "Content view is null! Have you specified a content view in your layout xml file?"
                            + " You have to give your content View the id R.id.content_view");
        }

        mContentShown = true;

        // Assume we won't have our data right away
        // while starting and start with the progress indicator.
        setContentShown(false, false);*/
    }

    protected void onErrorViewClicked() {
        onRefreshData(true);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
      /*  mProgressView = null;
        mContentView = null;
        mContentShown = false;*/
    }

    public abstract M getData();

    public void setContentShown(boolean shown) {
        //setContentShown(shown, true);
    }

    private void setContentShown(boolean shown, boolean animate) {
        if (mProgressView == null) {
            throw new IllegalStateException("Can't be used with a custom content view");
        }
        if (mContentShown == shown) {
            return;
        }
        mContentShown = shown;
        if (shown) {
            if (animate) {
                mProgressView.startAnimation(AnimationUtils.loadAnimation(
                        getActivity(), android.R.anim.fade_out));
                mContentView.startAnimation(AnimationUtils.loadAnimation(
                        getActivity(), android.R.anim.fade_in));
            } else {
                mProgressView.clearAnimation();
                mContentView.clearAnimation();
            }
            mProgressView.setVisibility(View.GONE);
            mContentView.setVisibility(View.VISIBLE);
        } else {
            if (animate) {
                mProgressView.startAnimation(AnimationUtils.loadAnimation(
                        getActivity(), android.R.anim.fade_in));
                mContentView.startAnimation(AnimationUtils.loadAnimation(
                        getActivity(), android.R.anim.fade_out));
            } else {
                mProgressView.clearAnimation();
                mContentView.clearAnimation();
            }
            mProgressView.setVisibility(View.VISIBLE);
            mContentView.setVisibility(View.GONE);
        }
    }

    /**
     * Animates the error view in (instead of displaying content view / loading view)
     */
    /*protected void animateErrorViewIn() {
        LceAnimator.showErrorView(mLoadingView, mContentView, mErrorView);
    }*/

    /**
     * Called to animate from loading view to content view
     */
    /*protected void animateContentViewIn() {
        LceAnimator.showContent(mLoadingView, mContentView, mErrorView);
    }*/

    /**
     * Override this method if you want to provide your own animation for showing the loading view
     */
   /* protected void animateLoadingViewIn() {
        LceAnimator.showLoading(mLoadingView, mContentView, mErrorView);
    }*/


}
