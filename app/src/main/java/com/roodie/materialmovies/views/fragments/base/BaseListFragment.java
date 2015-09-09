package com.roodie.materialmovies.views.fragments.base;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.roodie.materialmovies.R;

/**
 * Created by Roodie on 01.07.2015.
 */
public abstract class BaseListFragment<E extends AbsListView> extends BaseFragment implements AbsListView.OnScrollListener {

    public static final String LOG_TAG = BaseListFragment.class.getSimpleName();

    static final String INTERNAL_EMPTY_TAG = "INTERNAL_EMPTY";
    static final String INTERNAL_PROGRESS_TAG = "INTERNAL_PROGRESS";
    static final String INTERNAL_LIST_CONTAINER_TAG = "INTERNAL_LIST_CONTAINER";
    static final String INTERNAL_SECONDARY_PROGRESS_TAG = "INTERNAL_SECONDARY_PROGRESS";

    ListAdapter mAdapter;
    E mListView;
    View mEmptyView;
    TextView mStandardEmptyView;
    View mProgressView;
    View mListContainer;
    View mSecondaryProgressView;
    CharSequence mEmptyText;
    boolean mListShown;


    boolean mTwoPane = false;

    private int mFirstVisiblePosition;
    private int mFirstVisiblePositionTop;

    final private Handler mHandler = new Handler();

    private boolean mLoadMoreIsAtBottom;
    private int mLoadMoreRequestedItemCount;


    public void setTwoPaneLayout(boolean mTwoPane) {
        this.mTwoPane = mTwoPane;
    }

    /**
     * @return true if activity orientation is LANDSCAPE(has two frames)
     * @return true if activity orientation is PORTRAIT(has one frames)
     */
    public boolean isTwoPaneLayout() {
        return mTwoPane;
    }

    final private Runnable mRequestFocus = new Runnable() {
        public void run() {
            mListView.focusableViewAvailable(mListView);
        }
    };

    final private AdapterView.OnItemClickListener mOnClickListener =
            new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    onListItemClick((E) parent, view, position, id);
                }
            };

    public BaseListFragment() {
    }

    /**
     * Provide default implementation of a simple list view.  Subclasses
     * can override to replace with their own layout.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(LOG_TAG, "OnCreateView()");
        final Context context = getActivity();

        FrameLayout contentRoot = new FrameLayout(context);

        ProgressBar progress = new ProgressBar(context, null,
                android.R.attr.progressBarStyleLarge);
        progress.setTag(INTERNAL_PROGRESS_TAG);
        progress.setVisibility(View.GONE);

        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.CENTER;
        contentRoot.addView(progress, lp);

        FrameLayout lframe = new FrameLayout(context);
        lframe.setTag(INTERNAL_LIST_CONTAINER_TAG);

        TextView tv = new TextView(getActivity());
        tv.setTag(INTERNAL_EMPTY_TAG);
        tv.setGravity(Gravity.CENTER);
        //tv.setFont();
        final int p = getResources().getDimensionPixelSize(R.dimen.spacing_major);
        tv.setPadding(p, p, p, p);
        lframe.addView(tv, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));


        E lv = createListView(context, inflater);
        lv.setId(android.R.id.list);
        lframe.addView(lv, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        contentRoot.addView(lframe, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.FILL_PARENT));

        // ------------------------------------------------------------------

        ProgressBar secondaryProgress = new ProgressBar(context, null,
                android.R.attr.progressBarStyleHorizontal);
        secondaryProgress.setTag(INTERNAL_SECONDARY_PROGRESS_TAG);
        secondaryProgress.setVisibility(View.GONE);
        secondaryProgress.setIndeterminate(true);
        contentRoot.addView(secondaryProgress,
                new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.BOTTOM));

        View root;

        // if PORTRAIT orientation add toolbar to contentRoot
        if (!isTwoPaneLayout()) {
            final LinearLayout toolbarRoot = new LinearLayout(context);
            toolbarRoot.setOrientation(LinearLayout.VERTICAL);
            inflater.inflate(R.layout.include_toolbar, toolbarRoot, true);

            toolbarRoot.addView(contentRoot, new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f));
            root = toolbarRoot;
        } else {
            root = contentRoot;
        }

        root.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        return root;  }


    @Override
    public void onStop() {
        Log.d(LOG_TAG, "OnStop()");
        super.onStop();
    }

    @Override
    public void onDestroy() {
        Log.d(LOG_TAG, "OnDestroy()");
        super.onDestroy();
    }


    /*
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Log.d(LOG_TAG, "OnConfigChanged()");
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setTwoPaneLayout(true);
        } else
            setTwoPaneLayout(false);
    }
    */

    /**
     * Attach to listView once when it was created.
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Log.d(LOG_TAG, "OnViewCreated()");
        super.onViewCreated(view, savedInstanceState);
        ensureList();
        getListView().setOnScrollListener(this);
    }

    @Override
    public void onPause() {
        Log.d(LOG_TAG, "OnPause()");
        saveListViewPosition();
        super.onPause();
    }

    @Override
    public void onResume() {
        Log.d(LOG_TAG, "OnResume()");
        super.onResume();
    }

    /**
     * Detach from listView.
     */
    @Override
    public void onDestroyView() {
        mHandler.removeCallbacks(mRequestFocus);
        mListView = null;
        mListShown = false;
        mEmptyView = mProgressView = mListContainer = null;
        mStandardEmptyView = null;
        super.onDestroyView();
    }

    public abstract E createListView(Context context, LayoutInflater inflater);

    public ListAdapter getListAdapter() {
        return mAdapter;
    }

    /**
     * Provide the cursor for the list view.
     */
    public void setListAdapter(ListAdapter mAdapter) {
        boolean hadAdapter = mAdapter != null;
        this.mAdapter = mAdapter;
        if (mListView != null) {
            mListView.setAdapter(mAdapter);
            if (!mListShown && !hadAdapter) {
                setListShown(true, getView().getWindowToken() != null);

            }
        }
    }

    private void saveListViewPosition() {
        E listView = getListView();

        mFirstVisiblePosition = listView.getFirstVisiblePosition();

        if (mFirstVisiblePosition != AdapterView.INVALID_POSITION && listView.getChildCount() > 0) {
            mFirstVisiblePositionTop = listView.getChildAt(0).getTop();
        }
    }

    protected void moveListViewToSavedPositions() {
        final E list = getListView();
        if (mFirstVisiblePosition != AdapterView.INVALID_POSITION
                && list.getFirstVisiblePosition() <= 0) {
            list.post(new Runnable() {
                @Override
                public void run() {
                    list.setSelection(mFirstVisiblePosition);
                }
            });
        }
    }

    /**
     * Get the position of the currently selected list item.
     */
    public int getSelectedItemPosition() {
        return mListView.getSelectedItemPosition();
    }

    /**
     * Get the cursor row ID of the currently selected list item.
     */
    public long getSelectedItemId() {
        return mListView.getSelectedItemId();
    }

    public E getListView() {
        return mListView;
    }

    /**
     * This method will be called when an item in the list is selected.
     * Subclasses should override it.
     */
    public void onListItemClick(E l, View v, int position, long id) {
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        mLoadMoreIsAtBottom = totalItemCount > mLoadMoreRequestedItemCount
                && firstVisibleItem + visibleItemCount == totalItemCount;

    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && mLoadMoreIsAtBottom) {
            if (onScrolledToBottom()) {
                mLoadMoreRequestedItemCount = view.getCount();
                mLoadMoreIsAtBottom = false;
            }
        }
    }

    /**
     * The default content for a BaseListFragment has a TextView that can
     * be shown when the list is empty.
     */
    public void setEmptyText(CharSequence text) {
        ensureList();
        if (mStandardEmptyView == null) {
            throw new IllegalStateException("Can't be used with a custom content view");
        }
        mStandardEmptyView.setText(text);
        if (mEmptyText == null) {
            mListView.setEmptyView(mStandardEmptyView);
        }
        mEmptyText = text;
    }


    /**
     * Control whether the list is being displayed.  You can make it not
     * displayed if you are waiting for the initial data to show in it.  During
     * this time an indeterminant progress indicator will be shown instead.
     */
    public void setListShown(boolean shown) {
        setListShown(shown, true);
    }


    /**
     * Control whether the list is being displayed.  You can make it not
     * displayed if you are waiting for the initial data to show in it.  During
     * this time an indeterminant progress indicator will be shown instead.
     */
    private void setListShown(boolean shown, boolean animate) {
        ensureList();
        if (mProgressView == null) {
            throw new IllegalStateException("Can't be used with a custom content view");
        }
        if (mListShown == shown) {
            return;
        }
        mListShown = shown;
        if (shown) {
            if (animate) {
                mProgressView.startAnimation(AnimationUtils.loadAnimation(
                        getActivity(), android.R.anim.fade_out));
                mListContainer.startAnimation(AnimationUtils.loadAnimation(
                        getActivity(), android.R.anim.fade_in));
            } else {
                mProgressView.clearAnimation();
                mListContainer.clearAnimation();
            }

            mProgressView.setVisibility(View.GONE);
            mListContainer.setVisibility(View.VISIBLE);
        } else {
            if (animate) {
                mProgressView.startAnimation(AnimationUtils.loadAnimation(
                        getActivity(), android.R.anim.fade_in));
                mListContainer.startAnimation(AnimationUtils.loadAnimation(
                        getActivity(), android.R.anim.fade_out));
            } else {
                mProgressView.clearAnimation();
                mListContainer.clearAnimation();
            }
            mProgressView.setVisibility(View.VISIBLE);
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

    public void smoothScrollTo(int position) {
        if (mListView != null) {
            mListView.smoothScrollToPosition(position);
        }
    }


    private void ensureList() {
        if (mListView != null) {
            return;
        }
        View root = getView();
        if (root == null) {
            throw new IllegalStateException("Content view not yet created");
        }
        if (root instanceof AbsListView) {
            mListView = (E)root;
        } else {
            mStandardEmptyView = (TextView)root.findViewWithTag(INTERNAL_EMPTY_TAG);
            if (mStandardEmptyView == null) {
                mEmptyView = root.findViewById(android.R.id.empty);
            } else {
                mStandardEmptyView.setVisibility(View.GONE);
            }
            mProgressView = root.findViewWithTag(INTERNAL_PROGRESS_TAG);
            mSecondaryProgressView = root.findViewWithTag(INTERNAL_SECONDARY_PROGRESS_TAG);
            mListContainer = root.findViewWithTag(INTERNAL_LIST_CONTAINER_TAG);
            View rawListView = root.findViewById(android.R.id.list);
            if (!(rawListView instanceof AbsListView)) {
                if (rawListView == null) {
                    throw new RuntimeException(
                            "Your content must have a ListView whose id attribute is " +
                                    "'android.R.id.list'");
                }
                throw new RuntimeException(
                        "Content has view with id attribute 'android.R.id.list' "
                                + "that is not a ListView class");
            }
            mListView = (E)rawListView;
            if (mEmptyView != null) {
                mListView.setEmptyView(mEmptyView);
            } else if (mEmptyText != null) {
                mStandardEmptyView.setText(mEmptyText);
                mListView.setEmptyView(mStandardEmptyView);
            }
        }
        mListShown = true;
        mListView.setOnItemClickListener(mOnClickListener);
        if (mAdapter != null) {
            ListAdapter adapter = mAdapter;
            mAdapter = null;
            setListAdapter(adapter);
        } else {
            // We are starting without an adapter, so assume we won't
            // have our data right away and start with the progress indicator.
            if (mProgressView != null) {
                setListShown(false, false);
            }
        }
        mHandler.post(mRequestFocus);
    }

    protected abstract boolean onScrolledToBottom();





}
