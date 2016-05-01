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

import com.roodie.materialmovies.R;
import com.roodie.materialmovies.views.adapters.FooterViewListAdapter;

/**
 * Created by Roodie on 12.08.2015.
 */
public class LoadMoreRecyclerLayout extends BaseRecyclerLayout {

    protected LAYOUT_MANAGER_TYPE layoutManagerType;

    private OnLoadMoreListener onLoadMoreListener;

    private RecyclerViewPositionHelper mRecyclerViewHelper;


    private boolean isLoadingMore = false;

    private boolean isLoadMoreEnabled = false;


    private boolean mIsLoadMoreWidgetEnabled;

    private FooterViewListAdapter mAdapter;

    protected int[] defaultLoadingMoreColors = null;

    private int lastVisibleItemPosition;

    private int mVisibleItemCount = 0;
    private int mTotalItemCount = 0;
    private int previousTotal = 0;
    private int mFirstVisibleItem;

    // added by Sevan Joe to support scrollbars
    private static final int RECYCLER_LIST = 0;
    private static final int RECYCLER_AUTOFIT_GRID = 1;
    private int mRecyclerType;


    /**
     * control to show the loading view first when list is initiated at the beginning
     * true - assume there is a buffer to load things before and the adapter suppose zero data at the beignning
     * false - assume there is data to show at the beginning level
     */
    private boolean isFirstLoadingOnlineAdapter = false;

    public LoadMoreRecyclerLayout(Context context) {
        super(context);
        initViews();
    }

    public LoadMoreRecyclerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(attrs);
        initViews();
    }

    public LoadMoreRecyclerLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initAttrs(attrs);
        initViews();
    }

    @Override
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

    protected void initAttrs(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.LoadMoreRecyclerLayout);

        try {
            mPadding = (int) typedArray.getDimension(R.styleable.LoadMoreRecyclerLayout_padding, -1.1f);
            mPaddingTop = (int) typedArray.getDimension(R.styleable.LoadMoreRecyclerLayout_paddingTop, 0.0f);
            mPaddingBottom = (int) typedArray.getDimension(R.styleable.LoadMoreRecyclerLayout_paddingBottom, 0.0f);
            mPaddingLeft = (int) typedArray.getDimension(R.styleable.LoadMoreRecyclerLayout_paddingLeft, 0.0f);
            mPaddingRight = (int) typedArray.getDimension(R.styleable.LoadMoreRecyclerLayout_paddingRight, 0.0f);
            mClipToPadding = typedArray.getBoolean(R.styleable.LoadMoreRecyclerLayout_clipToPadding, false);
            mRecyclerType = typedArray.getInt(R.styleable.LoadMoreRecyclerLayout_recyclerViewType, RECYCLER_AUTOFIT_GRID);
           // mFloatingButtonId = typedArray.getResourceId(R.styleable.UltimateRecyclerview_recyclerviewFloatingActionView, 0);
            int colorList = typedArray.getResourceId(R.styleable.LoadMoreRecyclerLayout_defaultLoadingColor, 0);
            if (colorList != 0) {
                defaultLoadingMoreColors = getResources().getIntArray(R.array.loading_colors);
            }
        } finally {
            typedArray.recycle();
        }
    }

    /**
     * Enable loading more of the recyclerview
     */
    private void enableLoadMore() {
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

        if (mAdapter != null && mAdapter.getLoadMoreView() == null) {
            mAdapter.setLoadMoreView(LayoutInflater.from(getContext())
                    .inflate(R.layout.secondary_progress_bar, null));
            mAdapter.enableLoadMore(true);
        }

    }

    /**
     * If you have used {@link #disableLoadMore()} and want to enable loading more again,you can use this method.
     */
    public void reenableLoadMore() {
        enableLoadMore();
        if (mAdapter != null) {
            mAdapter.setLoadMoreView(LayoutInflater.from(getContext())
                    .inflate(R.layout.secondary_progress_bar_default, null));
            mAdapter.enableLoadMore(false);
        }
        mIsLoadMoreWidgetEnabled = true;
    }

    /**
     * If you have used {@link #disableLoadMore()} and want to enable loading more again,you can use this method.
     *
     * @param customLoadingMoreView na
     */
    public void reenableLoadMore(View customLoadingMoreView) {
        enableLoadMore();
        if (mAdapter != null) {
            mAdapter.setLoadMoreView(customLoadingMoreView);
            mAdapter.enableLoadMore(true);
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
        if (mAdapter != null) {
            // mAdapter.setCustomLoadMoreView(null);
            //LayoutInflater.from(getContext()).inflate(R.layout.empty_progressbar, null)
            mAdapter.enableLoadMore(false);
        }
        mIsLoadMoreWidgetEnabled = false;
    }

    public void setEmptyView(View emptyView) {
       // this.emptyView = emptyView;
       // emptyObserver.onChanged();
    }



    /**
     * Swaps the current adapter with the provided one. It is similar to
     * {@link #setAdapter(FooterViewListAdapter)} but assumes existing adapter and the new adapter uses the same
     * ViewHolder and does not clear the RecycledViewPool.
     * Note that it still calls onAdapterChanged callbacks.
     *
     * @param adapter                       The new adapter to set, or null to set no adapter.
     * @param removeAndRecycleExistingViews If set to true, RecyclerView will recycle all existing Views. If adapters have stable ids and/or you want to animate the disappearing views, you may prefer to set this to false.
     */
    public void swapAdapter(FooterViewListAdapter adapter, boolean removeAndRecycleExistingViews) {
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

    /**
     * Set the load more listener of recyclerview
     *
     * @param onLoadMoreListener load listen
     */
    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    @Override
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

    public RecyclerView.Adapter getAdapter() {
        return mRecyclerView.getAdapter();
    }

    public void setAdapter(FooterViewListAdapter adapter) {
        if (adapter.equals(mAdapter))
            return;
        this.mAdapter = adapter;
        mRecyclerView.setAdapter(mAdapter);

        if (mAdapter != null) {
            setContentShown(true);

            if (isLoadMoreEnabled()) {
                enableLoadMore();
            }
        }
        mRecyclerViewHelper = RecyclerViewPositionHelper.createHelper(mRecyclerView);
    }

    private void updateHelperDisplays() {

        if (mAdapter == null)
            return;

        if (!isFirstLoadingOnlineAdapter) {
            if (mAdapter.getItemCount() == 0) {
                mEmptyView.setVisibility(View.GONE);
                setContentShown(false);
            }

        } else {
            isFirstLoadingOnlineAdapter = false;
            setContentShown(true);
            footerLoadMoreChecker();
        }

    }

    private void footerLoadMoreChecker() {
        if (!(getAdapter() instanceof FooterViewListAdapter)) {
            return;
        }
        if (mAdapter.getLoadMoreView() != null) {
            if (mAdapter.enableLoadMore()) {
                mAdapter.getLoadMoreView().setVisibility(View.VISIBLE);
            } else {
                mAdapter.getLoadMoreView().setVisibility(View.GONE);
            }
        }
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
}