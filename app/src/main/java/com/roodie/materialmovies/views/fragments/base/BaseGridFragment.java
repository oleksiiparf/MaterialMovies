package com.roodie.materialmovies.views.fragments.base;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.roodie.materialmovies.R;
import com.roodie.materialmovies.views.listeners.RecyclerItemClickListener;

/**
 * Created by Roodie on 01.07.2015.
 */
public abstract class BaseGridFragment extends BaseFragment implements RecyclerItemClickListener {

    private RecyclerView mRecyclerView;
    private TextView mStandardEmptyView;
    private View mLoadingView;
    private View mProgressView;
    private ProgressBar mProgressBar;
    private ProgressBar mSecondaryProgressView;
    private FrameLayout mListContainer;

    boolean mGridShown;

    public BaseGridFragment() {
    }

    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mStandardEmptyView = (TextView) view.findViewById(R.id.empty_text_view);
        mStandardEmptyView.setVisibility(View.GONE);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        mSecondaryProgressView = (ProgressBar) view.findViewById(R.id.secondary_progress_bar);
        mSecondaryProgressView.setVisibility(View.GONE);
        mListContainer = (FrameLayout) view.findViewById(R.id.conteiner);
        mLoadingView = view.findViewById(R.id.loading_view);
        mLoadingView.setVisibility(View.GONE);
        mProgressView = view.findViewById(R.id.progress_view);
        mGridShown = true;

        System.out.println("Toolbar = " + getToolbar() != null);
        initializeRecycler();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public abstract void initializeRecycler();

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public void setEmptyText(CharSequence text) {
        //ensureList();
        if (mStandardEmptyView == null) {
            throw new IllegalStateException("Can't be used with a custom content view");
        }
        mProgressView.setVisibility(View.GONE);
        mLoadingView.setVisibility(View.VISIBLE);
        mStandardEmptyView.setVisibility(View.VISIBLE);
        mStandardEmptyView.setText(text);
    }

    /**
     * Control whether the grid is being displayed.  You can make it not
     * displayed if you are waiting for the initial data to show in it.  During
     * this time an indeterminant progress indicator will be shown instead.
     */
    public void setGridShown(boolean shown) {
        setGridShown(shown, true);
    }

    /**
     * Control whether the grid is being displayed.  You can make it not
     * displayed if you are waiting for the initial data to show in it.  During
     * this time an indeterminant progress indicator will be shown instead.
     */
    private void setGridShown(boolean shown, boolean animate) {
        //ensureList();
        if (mProgressBar == null) {
            throw new IllegalStateException("Can't be used with a custom content view");
        }
        if (mGridShown == shown) {
            return;
        }
        mGridShown = shown;
        if (shown) {
            if (animate) {
                mLoadingView.startAnimation(AnimationUtils.loadAnimation(
                        getActivity(), android.R.anim.fade_out));
                mListContainer.startAnimation(AnimationUtils.loadAnimation(
                        getActivity(), android.R.anim.fade_in));
            } else {
                mLoadingView.clearAnimation();
                mListContainer.clearAnimation();
            }
            mProgressView.setVisibility(View.GONE);
            mStandardEmptyView.setVisibility(View.VISIBLE);
            mLoadingView.setVisibility(View.GONE);
            mListContainer.setVisibility(View.VISIBLE);
        } else {
            if (animate) {
                mLoadingView.startAnimation(AnimationUtils.loadAnimation(
                        getActivity(), android.R.anim.fade_in));
                mListContainer.startAnimation(AnimationUtils.loadAnimation(
                        getActivity(), android.R.anim.fade_out));
            } else {
                mLoadingView.clearAnimation();
                mListContainer.clearAnimation();
            }
            mProgressView.setVisibility(View.VISIBLE);
            mStandardEmptyView.setVisibility(View.GONE);
            mLoadingView.setVisibility(View.VISIBLE);
            mListContainer.setVisibility(View.GONE);
        }
    }

    public void setSecondaryProgressShown(boolean visible) {
        Animation anim;
        if (visible) {
            mSecondaryProgressView.setVisibility(View.VISIBLE);
            anim = AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_in);
        } else {
            mSecondaryProgressView.setVisibility(View.GONE);
            anim = AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_out);
        }

        anim.setDuration(getResources().getInteger(android.R.integer.config_shortAnimTime));
        mSecondaryProgressView.startAnimation(anim);
    }

    protected abstract boolean onScrolledToBottom();

}

