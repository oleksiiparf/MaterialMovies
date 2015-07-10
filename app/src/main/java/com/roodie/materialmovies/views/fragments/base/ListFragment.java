package com.roodie.materialmovies.views.fragments.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListAdapter;

/**
 * Created by Roodie on 01.07.2015.
 */
public abstract class ListFragment<E extends AbsListView> extends BaseFragment implements AbsListView.OnScrollListener {

    ListAdapter mAdapter;
    E mListView;

    private int mFirstVisiblePosition;
    private int mFirstVisiblePositionTop;

    private boolean mLoadMoreIsAtBottom;
    private int mLoadMoreRequestedItemCount;

    public ListFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final Context context = getActivity();


        return super.onCreateView(inflater, container, savedInstanceState);
    }


    /**
     * Attach to listView once when it was created.
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getListView().setOnScrollListener(this);
    }

    @Override
    public void onPause() {
        saveListViewPosition();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    /**
     * Detach from listView.
     */
    @Override
    public void onDestroyView() {
        mListView = null;
        super.onDestroyView();
    }

    public abstract E createListView(Context context, LayoutInflater inflater);

    public ListAdapter getListAdapter() {
        return mAdapter;
    }

    public void setListAdapter(ListAdapter mAdapter) {
        this.mAdapter = mAdapter;
        if (mListView != null) {
            mListView.setAdapter(mAdapter);
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

    protected abstract boolean onScrolledToBottom();

    final private AdapterView.OnItemClickListener mOnClickListener =
            new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    onListItemClick((E)parent, view, position, id);
                }
            };



}

