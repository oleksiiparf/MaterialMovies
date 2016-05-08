package com.roodie.materialmovies.views.custom_views.recyclerview;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.roodie.materialmovies.R;
import com.roodie.materialmovies.views.adapters.FooterViewListAdapter;
import com.roodie.materialmovies.views.fragments.base.BaseDetailFragment;

/**
 * Created by Roodie on 27.02.2016.
 */
public class BaseRecyclerLayout extends FrameLayout {

    protected LAYOUT_MANAGER_TYPE layoutManagerType;

    public RecyclerView.Adapter mAdapter;

    protected FrameLayout mLayoutContainer;
    public RecyclerView mRecyclerView;

    protected RecyclerView.OnScrollListener mOnScrollListener;

    private RecyclerViewPositionHelper mRecyclerViewHelper;

    private OnLoadMoreListener onLoadMoreListener;

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


    protected int mVisibleItemCount = 0;
    protected int mTotalItemCount = 0;
    protected int previousTotal = 0;
    protected int mFirstVisibleItem;

    protected int lastVisibleItemPosition;

    private boolean isLoadingMore = false;

    private boolean mIsLoadMoreWidgetEnabled;

    protected int[] defaultLoadingMoreColors = null;

    protected RecyclerView.AdapterDataObserver emptyObserver = null;

    // added to support scrollbars
    private static final int RECYCLER_LIST = 0;
    private static final int RECYCLER_AUTOFIT_GRID = 1;
    private int mRecyclerType;

    public BaseRecyclerLayout(Context context) {
        super(context);
        initViews();
    }

    public BaseRecyclerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(attrs);
        initViews();
    }

    public BaseRecyclerLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(attrs);
        initViews();
    }

    protected void initializeEmptyObserver() {
        emptyObserver = new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                if (mEmptyView != null) {
                    if (mAdapter != null) {
                        footerLoadMoreChecker();
                        isEmptyVisible = false;
                        mEmptyView.setVisibility(View.GONE);
                        mRecyclerView.setVisibility(View.VISIBLE);
                    } else {
                        isEmptyVisible = true;
                        mEmptyView.setVisibility(View.VISIBLE);
                        mRecyclerView.setVisibility(View.GONE);
                    }
                }
            }
        };
    }

    protected void initAttrs(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.BaseRecyclerLayout);

        try {
            mPadding = (int) typedArray.getDimension(R.styleable.BaseRecyclerLayout_padding, -1.1f);
            mPaddingTop = (int) typedArray.getDimension(R.styleable.BaseRecyclerLayout_paddingTop, 0.0f);
            mPaddingBottom = (int) typedArray.getDimension(R.styleable.BaseRecyclerLayout_paddingBottom, 0.0f);
            mPaddingLeft = (int) typedArray.getDimension(R.styleable.BaseRecyclerLayout_paddingLeft, 0.0f);
            mPaddingRight = (int) typedArray.getDimension(R.styleable.BaseRecyclerLayout_paddingRight, 0.0f);
            mClipToPadding = typedArray.getBoolean(R.styleable.BaseRecyclerLayout_clipToPadding, false);
            mRecyclerType = typedArray.getInt(R.styleable.BaseRecyclerLayout_recyclerViewType, RECYCLER_AUTOFIT_GRID);
            int colorList = typedArray.getResourceId(R.styleable.BaseRecyclerLayout_defaultLoadingColor, 0);
            if (colorList != 0) {
                defaultLoadingMoreColors = getResources().getIntArray(R.array.loading_colors);
            }
        } finally {
            typedArray.recycle();
        }
    }

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
            Crashlytics.log("Empty view is null.");
            throw new NullPointerException(
                    "Empty view is null! Have you specified an empty view in your layout xml file?"
                            + " You have to give your Empty View the id R.id.not_available_view");
        }

        if (mLoadingView == null) {
            Crashlytics.log("Loading view is null.");
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


    protected void setRecyclerType() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        switch (mRecyclerType) {
            case RECYCLER_LIST:
                mLayoutContainer.removeView(mRecyclerView);
                View listRecyclerView = inflater.inflate(R.layout.recyclerview_layout_list, mLayoutContainer, true);
                mRecyclerView = (RecyclerView) listRecyclerView.findViewById(R.id.recycler_view);
                break;
            case RECYCLER_AUTOFIT_GRID:
                mLayoutContainer.removeView(mRecyclerView);
                View animationGridRecyclerView = inflater.inflate(R.layout.recyclerview_layout_grid, mLayoutContainer, true);
                mRecyclerView = (AutofitGridRecyclerView) animationGridRecyclerView.findViewById(R.id.recycler_view);
                break;
            default:
                break;
        }
    }

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

    public RecyclerView.Adapter getAdapter() {
        return mRecyclerView.getAdapter();
    }

    public void setAdapter(RecyclerView.Adapter adapter) {
        if (adapter instanceof FooterViewListAdapter) {
            if (adapter.equals(mAdapter))
                return;
        }
        this.mAdapter = adapter;
        mRecyclerView.setAdapter(mAdapter);

        if (mAdapter instanceof FooterViewListAdapter) {
            if (mAdapter != null) {
                setContentShown(true);
                    if (isLoadMoreEnabled())
                        enableLoadMore();
            }
                mRecyclerViewHelper = RecyclerViewPositionHelper.createHelper(mRecyclerView);

        } else if (mAdapter instanceof BaseDetailFragment.EnumListDetailAdapter) {
            if (mAdapter != null) {
                if (mAdapter.getItemCount() == 0)
                    setContentShown(false);
                else
                    setContentShown(true);

            }
        }
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

    /**
     * Get the layout manager of the recycler
     */
    public RecyclerView.LayoutManager getLayoutManager() {
        return mRecyclerView.getLayoutManager();
    }

    /**
     * If you have used {@link #disableLoadMore()} and want to enable loading more again,you can use this method.
     */
    public void reenableLoadMore() {
        enableLoadMore();
        if (mAdapter != null && mAdapter instanceof FooterViewListAdapter) {
            ((FooterViewListAdapter)mAdapter).setLoadMoreView(LayoutInflater.from(getContext())
                    .inflate(R.layout.secondary_progress_bar_default, null));
            ((FooterViewListAdapter)mAdapter).enableLoadMore(false);
        }
        mIsLoadMoreWidgetEnabled = true;
    }

    private void footerLoadMoreChecker() {
        if (!(getAdapter() instanceof FooterViewListAdapter)) {
            return;
        }
        if ( ((FooterViewListAdapter)mAdapter).getLoadMoreView() != null) {
            if ( ((FooterViewListAdapter)mAdapter).enableLoadMore()) {
                ((FooterViewListAdapter)mAdapter).getLoadMoreView().setVisibility(View.VISIBLE);
            } else {
                ((FooterViewListAdapter)mAdapter).getLoadMoreView().setVisibility(View.GONE);
            }
        }
    }

    /**
     * If you have used {@link #disableLoadMore()} and want to enable loading more again,you can use this method.
     *
     * @param customLoadingMoreView na
     */
    public void reenableLoadMore(View customLoadingMoreView) {
        enableLoadMore();
        if (mAdapter != null && mAdapter instanceof FooterViewListAdapter) {
            ((FooterViewListAdapter)mAdapter).setLoadMoreView(customLoadingMoreView);
            ((FooterViewListAdapter)mAdapter).enableLoadMore(true);
        }
        mIsLoadMoreWidgetEnabled = true;
    }

    public boolean isLoadMoreEnabled() {
        return mIsLoadMoreWidgetEnabled;
    }

    public void enableLoadMoreView(boolean enable) {
        mIsLoadMoreWidgetEnabled =  enable;
    }

    /**
     * Remove loading more scroll listener
     */
    public void disableLoadMore() {
        setDefaultScrollListener();
        if (mAdapter != null && mAdapter instanceof FooterViewListAdapter) {
            // mAdapter.setCustomLoadMoreView(null);
            //LayoutInflater.from(getContext()).inflate(R.layout.empty_progressbar, null)
            ((FooterViewListAdapter)mAdapter).enableLoadMore(false);
        }
        mIsLoadMoreWidgetEnabled = false;
    }

    /**
     * Enable loading more of the recyclerview
     */
    protected void enableLoadMore() {
        mRecyclerView.removeOnScrollListener(mOnScrollListener);
        mOnScrollListener = new RecyclerView.OnScrollListener() {
            private int[] lastPositions;

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();

                if (layoutManagerType == null) {
                    if (layoutManager instanceof GridLayoutManager) {
                        layoutManagerType = LAYOUT_MANAGER_TYPE.GRID;
                    } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                        layoutManagerType = LAYOUT_MANAGER_TYPE.STAGGERED_GRID;
                    } else if (layoutManager instanceof LinearLayoutManager) {
                        layoutManagerType = LAYOUT_MANAGER_TYPE.LINEAR;

                    } else {
                        throw new RuntimeException("Unsupported LayoutManager used. Valid ones are LinearLayoutManager, GridLayoutManager and StaggeredGridLayoutManager");
                    }
                }

                mTotalItemCount = layoutManager.getItemCount();
                mVisibleItemCount = layoutManager.getChildCount();

                switch (layoutManagerType) {
                    case LINEAR:
                        mFirstVisibleItem = mRecyclerViewHelper.findFirstVisibleItemPosition();
                        lastVisibleItemPosition = mRecyclerViewHelper.findLastVisibleItemPosition();
                        break;
                    case GRID:
                        if (layoutManager instanceof GridLayoutManager) {
                            GridLayoutManager ly = (GridLayoutManager) layoutManager;
                            lastVisibleItemPosition = ly.findLastVisibleItemPosition();
                            mFirstVisibleItem = ly.findFirstVisibleItemPosition();
                        }
                        break;
                    case STAGGERED_GRID:
                        if (layoutManager instanceof StaggeredGridLayoutManager) {
                            StaggeredGridLayoutManager sy = (StaggeredGridLayoutManager) layoutManager;

                            if (lastPositions == null)
                                lastPositions = new int[sy.getSpanCount()];

                            sy.findLastVisibleItemPositions(lastPositions);
                            lastVisibleItemPosition = findMax(lastPositions);

                            sy.findFirstVisibleItemPositions(lastPositions);
                            mFirstVisibleItem = findMin(lastPositions);
                        }
                        break;
                }

                if (isLoadingMore) {
                    if (mTotalItemCount > previousTotal) {
                        isLoadingMore = false;
                        previousTotal = mTotalItemCount;
                    }
                }
                boolean casetest = (mTotalItemCount - mVisibleItemCount) <= mFirstVisibleItem;
                if (!isLoadingMore && casetest) {
                    onLoadMoreListener.loadMore(mRecyclerView.getAdapter().getItemCount(), lastVisibleItemPosition);
                    isLoadingMore = true;
                    previousTotal = mTotalItemCount;
                }


            }
        };

        mRecyclerView.addOnScrollListener(mOnScrollListener);

        if (mAdapter != null && ((FooterViewListAdapter)mAdapter).getLoadMoreView() == null) {
            ((FooterViewListAdapter)mAdapter).setLoadMoreView(LayoutInflater.from(getContext())
                    .inflate(R.layout.secondary_progress_bar, null));
            ((FooterViewListAdapter)mAdapter).enableLoadMore(true);
        }

    }

    /**
     * Set the load more listener of recyclerview
     *
     * @param onLoadMoreListener load listen
     */
    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    /**
     * Swaps the current adapter with the provided one. It is similar to
     * {@link #setAdapter(android.support.v7.widget.RecyclerView.Adapter)} but assumes existing adapter and the new adapter uses the same
     * ViewHolder and does not clear the RecycledViewPool.
     * Note that it still calls onAdapterChanged callbacks.
     *
     * @param adapter                       The new adapter to set, or null to set no adapter.
     * @param removeAndRecycleExistingViews If set to true, RecyclerView will recycle all existing Views. If adapters have stable ids and/or you want to animate the disappearing views, you may prefer to set this to false.
     */
    public void swapAdapter(RecyclerView.Adapter adapter, boolean removeAndRecycleExistingViews) {
        mRecyclerView.swapAdapter(adapter, removeAndRecycleExistingViews);
    }


    /**
     * Gets the current ItemAnimator for this RecyclerView. A null return value
     * indicates that there is no animator and that item changes will happen without
     * any animations. By default, RecyclerView instantiates and
     * uses an instance of {@link android.support.v7.widget.DefaultItemAnimator}.
     *
     * @return ItemAnimator The current ItemAnimator. If null, no animations will occur
     * when changes occur to the items in this RecyclerView.
     */
    public RecyclerView.ItemAnimator getItemAnimator() {
        return mRecyclerView.getItemAnimator();
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

    private int findMax(int[] lastPositions) {
        int max = Integer.MIN_VALUE;
        for (int value : lastPositions) {
            if (value > max)
                max = value;
        }
        return max;
    }

    private int findMin(int[] lastPositions) {
        int min = Integer.MAX_VALUE;
        for (int value : lastPositions) {
            if (value != RecyclerView.NO_POSITION && value < min)
                min = value;
        }
        return min;
    }

    public interface OnLoadMoreListener {
        void loadMore(int itemsCount, final int maxLastVisiblePosition);
    }

    public enum LAYOUT_MANAGER_TYPE {
        LINEAR,
        GRID,
        STAGGERED_GRID,
        PUZZLE,
    }

}
