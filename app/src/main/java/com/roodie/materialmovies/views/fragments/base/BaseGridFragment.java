package com.roodie.materialmovies.views.fragments.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.roodie.materialmovies.R;

/**
 * Created by Roodie on 01.07.2015.
 */
public abstract class BaseGridFragment extends BaseFragment {

    private RecyclerView mRecyclerView;
    private TextView mStandardEmptyView;
    private ProgressBar mProgressView;
    private ProgressBar mSecondaryProgressView;
    private FrameLayout mListContainer;

    private RecyclerView.Adapter mAdapter;
    View mEmptyView;

   // View mSecondaryProgressView;
    boolean mGridShown;

    public BaseGridFragment() {
    }

    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.view_receicler, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mStandardEmptyView = (TextView) view.findViewById(R.id.empty_text_view);
        mProgressView = (ProgressBar) view.findViewById(R.id.progress_bar);
        mSecondaryProgressView = (ProgressBar) view.findViewById(R.id.secondary_progress_bar);
        mListContainer = (FrameLayout) view.findViewById(R.id.conteiner);
        initializeReceicler();
        //ensureList();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public abstract void initializeReceicler();

    /**
     * Detach from listView.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

   // public abstract E createListView(Context context, LayoutInflater inflater);

    /**
     * Provide the cursor for the list view.
     */
    /*public void setGridAdapter(RecyclerView.Adapter mAdapter) {
        boolean hadAdapter = mAdapter != null;
        this.mAdapter = mAdapter;
        if (mRecyclerView != null) {
            mRecyclerView.setAdapter(mAdapter);
            if (!mGridShown && !hadAdapter) {
                setGridShown(true, getView().getWindowToken() != null);

            }
        }
    }
    */


    /**
     * Get the position of the currently selected list item.
     */

   // public int getSelectedItemPosition() {
   //     return mListView.getSelectedItemPosition();
    //}

    /**
     * Get the cursor row ID of the currently selected list item.
     */
   // public long getSelectedItemId() {
   //     return mListView.getSelectedItemId();
   // }

   // public E getListView() {
   //     return mListView;
  //  }


    /**
     * This method will be called when an item in the list is selected.
     * Subclasses should override it.
     */
   //public void onListItemClick(E l, View v, int position, long id) {
   // }

   // @Override
   // public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
    //    mLoadMoreIsAtBottom = totalItemCount > mLoadMoreRequestedItemCount
    //            && firstVisibleItem + visibleItemCount == totalItemCount;

   // }

  //  @Override
  //  public void onScrollStateChanged(AbsListView view, int scrollState) {
  //      if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && mLoadMoreIsAtBottom) {
    //        if (onScrolledToBottom()) {
   //             mLoadMoreRequestedItemCount = view.getCount();
    //            mLoadMoreIsAtBottom = false;
    //        }
    //    }
   // }

    /**
     * The default content for a BaseGridFragment has a TextView that can
     * be shown when the list is empty.
     */
    public void setEmptyText(CharSequence text) {
        //ensureList();
        if (mStandardEmptyView == null) {
            throw new IllegalStateException("Can't be used with a custom content view");
        }
        mStandardEmptyView.setText(text);
        //if (mEmptyText == null) {
        //    mListView.setEmptyView(mStandardEmptyView);
       // }
       // mEmptyText = text;
    }


    /**
     * Control whether the list is being displayed.  You can make it not
     * displayed if you are waiting for the initial data to show in it.  During
     * this time an indeterminant progress indicator will be shown instead.
     */
    public void setGridShown(boolean shown) {
        setGridShown(shown, true);
    }

    /**
     * Control whether the list is being displayed.  You can make it not
     * displayed if you are waiting for the initial data to show in it.  During
     * this time an indeterminant progress indicator will be shown instead.
     */
    private void setGridShown(boolean shown, boolean animate) {
        //ensureList();
        if (mProgressView == null) {
            throw new IllegalStateException("Can't be used with a custom content view");
        }
        if (mGridShown == shown) {
            return;
        }
        mGridShown = shown;
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

    /*private void ensureList() {
        if (mRecyclerView != null) {
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
        mGridShown = true;
        mListView.setOnItemClickListener(mOnClickListener);
        if (mAdapter != null) {
            ListAdapter adapter = mAdapter;
            mAdapter = null;
            setGridAdapter(adapter);
        } else {
            // We are starting without an adapter, so assume we won't
            // have our data right away and start with the progress indicator.
            if (mProgressView != null) {
                setGridShown(false, false);
            }
        }
        mHandler.post(mRequestFocus);
    }
    */

   // protected abstract boolean onScrolledToBottom();

}

