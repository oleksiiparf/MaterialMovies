package com.roodie.materialmovies.views.custom_views.recyclerview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.roodie.materialmovies.R;
import com.roodie.model.util.FileLog;

/**
 * Created by Roodie on 27.02.2016.
 */
public abstract class BaseRecyclerLayout extends FrameLayout {

    protected FrameLayout mLayoutContainer;
    public RecyclerView mRecyclerView;

    protected RecyclerView.OnScrollListener mOnScrollListener;

    protected View mEmptyView;
    protected View mLoadingView;
    boolean mContentShown;

    protected boolean isEmptyVisible;

    protected int mPadding;
    protected int mPaddingTop;
    protected int mPaddingBottom;
    protected int mPaddingLeft;
    protected int mPaddingRight;
    protected boolean mClipToPadding;

    protected RecyclerView.AdapterDataObserver emptyObserver = null;

    public BaseRecyclerLayout(Context context) {
        super(context);
    }

    public BaseRecyclerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseRecyclerLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    protected abstract void initializeEmptyObserver();

    protected void initViews() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.recyclerview_layout, this);

        mLayoutContainer = (FrameLayout) view.findViewById(R.id.layout_container);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

        setRecyclerType();

        if (mRecyclerView != null) {
            mRecyclerView.setClipToPadding(mClipToPadding);
            if (mPadding != -1.1f) {
                mRecyclerView.setPadding(mPadding, mPadding, mPadding, mPadding);
            } else {
                mRecyclerView.setPadding(mPaddingLeft, mPaddingTop, mPaddingRight, mPaddingBottom);
            }
        }

        setDefaultScrollListener();

        mEmptyView = view.findViewById(R.id.not_available_view);
        mLoadingView = view.findViewById(R.id.loading_view);

        if (mEmptyView == null) {
            throw new NullPointerException(
                    "Empty view is null! Have you specified an empty view in your layout xml file?"
                            + " You have to give your Empty View the id R.id.not_available_view");
        }

        if (mLoadingView == null) {
            throw new NullPointerException(
                    "Loading view is null! Have you specified a loading view in your layout xml file?"
                            + " You have to give your loading View the id R.id.loading_view");
        }


        initializeEmptyObserver();
        mContentShown = true;
        // Assume we won't have our data right away
        // while starting and start with the progress indicator.
        setContentShown(false);

    }


    protected abstract void setRecyclerType();

    protected void setDefaultScrollListener() {
        mRecyclerView.removeOnScrollListener(mOnScrollListener);
        mOnScrollListener = new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        };

        mRecyclerView.addOnScrollListener(mOnScrollListener);
    }

    /**
     * Set a listener that will be notified of any changes in scroll state or position.
     *
     * @param customOnScrollListener to set or null to clear
     * @deprecated Use {@link #addOnScrollListener(RecyclerView.OnScrollListener)} and
     * {@link #removeOnScrollListener(RecyclerView.OnScrollListener)}
     */
    public void setOnScrollListener(RecyclerView.OnScrollListener customOnScrollListener) {
        mRecyclerView.setOnScrollListener(customOnScrollListener);
    }

    public void addOnScrollListener(RecyclerView.OnScrollListener customOnScrollListener) {
        mRecyclerView.addOnScrollListener(customOnScrollListener);
    }

    public void removeOnScrollListener(RecyclerView.OnScrollListener customOnScrollListener) {
        mRecyclerView.removeOnScrollListener(customOnScrollListener);
    }

    /**
     * Add an {@link RecyclerView.ItemDecoration} to this RecyclerView. Item decorations can affect both measurement and drawing of individual item views. Item decorations are ordered. Decorations placed earlier in the list will be run/queried/drawn first for their effects on item views. Padding added to views will be nested; a padding added by an earlier decoration will mean further item decorations in the list will be asked to draw/pad within the previous decoration's given area.
     *
     * @param itemDecoration Decoration to add
     */
    public void addItemDecoration(RecyclerView.ItemDecoration itemDecoration) {
        mRecyclerView.addItemDecoration(itemDecoration);
    }

    /**
     * Add an {@link RecyclerView.ItemDecoration} to this RecyclerView. Item decorations can affect both measurement and drawing of individual item views.
     * <p>Item decorations are ordered. Decorations placed earlier in the list will be run/queried/drawn first for their effects on item views. Padding added to views will be nested; a padding added by an earlier decoration will mean further item decorations in the list will be asked to draw/pad within the previous decoration's given area.</p>
     *
     * @param itemDecoration Decoration to add
     * @param index          Position in the decoration chain to insert this decoration at. If this value is negative the decoration will be added at the end.
     */
    public void addItemDecoration(RecyclerView.ItemDecoration itemDecoration, int index) {
        mRecyclerView.addItemDecoration(itemDecoration, index);
    }

    /**
     * Sets the {@link RecyclerView.ItemAnimator} that will handle animations involving changes
     * to the items in this RecyclerView. By default, RecyclerView instantiates and
     * uses an instance of {@link android.support.v7.widget.DefaultItemAnimator}. Whether item animations are enabled for the RecyclerView depends on the ItemAnimator and whether
     * the LayoutManager {@link android.support.v7.widget.RecyclerView.LayoutManager#supportsPredictiveItemAnimations()
     * supports item animations}.
     *
     * @param animator The ItemAnimator being set. If null, no animations will occur
     *                 when changes occur to the items in this RecyclerView.
     */
    public void setItemAnimator(RecyclerView.ItemAnimator animator) {
        mRecyclerView.setItemAnimator(animator);
    }

    /**
     * Set the layout manager to the recycler
     *
     * @param manager lm
     */
    public void setLayoutManager(RecyclerView.LayoutManager manager) {
        mRecyclerView.setLayoutManager(manager);
    }

    public void setContentShown(boolean shown) {
        //FileLog.d("lce", "Adapter : setContent shown short");
        setContentShown(shown, true);
    }

    public void setContentShown(boolean shown, boolean animate) {
        //FileLog.d("lce", "Adapter : setContent shown to" + shown);

        if (mLoadingView == null) {
            throw new IllegalStateException("Can't be used with a custom content view");
        }

        if (mEmptyView == null) {
            throw new IllegalStateException("Can't be used with a custom empty view");
        }

        if (mContentShown == shown) {
            return;
        }
        FileLog.d("lce", "Adapter : setContent shown to" + shown);

        //emptyObserver.onChanged();

        mContentShown = shown;
        if (shown) {
            if (animate) {
                mLoadingView.startAnimation(AnimationUtils.loadAnimation(
                        getContext(), android.R.anim.fade_out));
                mRecyclerView.startAnimation(AnimationUtils.loadAnimation(
                        getContext(), android.R.anim.fade_in));
            } else {
                mLoadingView.clearAnimation();
                mRecyclerView.clearAnimation();
            }
            //mEmptyView.setVisibility(View.GONE);
            mLoadingView.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        } else {
            if (animate) {
                mLoadingView.startAnimation(AnimationUtils.loadAnimation(
                        getContext(), android.R.anim.fade_in));
                mRecyclerView.startAnimation(AnimationUtils.loadAnimation(
                        getContext(), android.R.anim.fade_out));
                mEmptyView.startAnimation(AnimationUtils.loadAnimation(
                        getContext(), android.R.anim.fade_out));
            } else {
                mEmptyView.clearAnimation();
                mLoadingView.clearAnimation();
                mRecyclerView.clearAnimation();
            }
            mEmptyView.setVisibility(View.GONE);
            mLoadingView.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        }
    }


    public void setErrorText(CharSequence text) {
        if (mEmptyView == null) {
            throw new IllegalStateException("Can't be used with a custom content view");
        }
        TextView emptyText = (TextView)mEmptyView.findViewById(R.id.body_not_available);
        emptyText.setText(text);

        emptyObserver.onChanged();
    }


    public void setHasFixedSize(boolean hasFixedSize) {
        mRecyclerView.setHasFixedSize(hasFixedSize);
    }


}
