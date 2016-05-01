package com.roodie.materialmovies.views.adapters;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.roodie.model.util.FileLog;

import java.util.List;

/**
 * Created by Roodie on 11.02.2016.
 */
public abstract class FooterViewListAdapter<T extends List, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    protected View mLoadMoreView = null;
    protected T items;

    private int loadmoresetingswatch = 0;

    private boolean customHeader = false;
    public boolean enabledLoadMoreView = false;

    protected LayoutInflater inflater;

    public FooterViewListAdapter(Context context) {
        inflater = LayoutInflater.from(context);
    }

    public LayoutInflater getInflater() {
        return inflater;
    }

    public T getItems() {
        return items;
    }

    public void setItems(T items) {
        this.items = items;
        FileLog.d("lce", "Adapter : set items count =" + items.size());
    }

    public int getTotalItemsCount() {
        int offset = 0;
        if (enableLoadMore()) offset++;
        return getItemCount() + offset;
    }

    public T getItem(int position) {
        if (position < items.size())
            return (T) items.get(position);
        else return null;
    }



    /**
     * Returns the number of items in the adapter bound to the parent RecyclerView.
     *
     * @return The number of items in the bound adapter
     */
    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    /**
     * Using a custom LoadMoreView
     *
     * @param customview the inflated view
     */
    public void setLoadMoreView(@Nullable View customview) {
        mLoadMoreView = customview;
    }

    public View getLoadMoreView() {
        return mLoadMoreView;
    }

    /**
     * the get function to get load more
     *
     * @return determine this is a get function
     */
    public boolean enableLoadMore() {
        return enabledLoadMoreView;
    }

    /**
     * as the set function to switching load more feature
     *
     * @param b bool
     */
    public void enableLoadMore(boolean b) {
        enabledLoadMoreView = b;
        if (loadmoresetingswatch > 0 && !b && mLoadMoreView != null) {
            notifyItemRemoved(getTotalItemsCount() - 1);
        }
        loadmoresetingswatch++;
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPES.FOOTER) {
            VH viewHolder = getViewHolder(mLoadMoreView);
            if (getItemCount() == 0)
                viewHolder.itemView.setVisibility(View.INVISIBLE);
            return viewHolder;
        }
        return onCreateViewHolder(parent);
    }

    public abstract VH getViewHolder(View view);

    public abstract VH onCreateViewHolder(ViewGroup parent);

    @Override
    public int getItemViewType(int position) {
        if (getItemCount() == 0) {
            if (enableLoadMore() && position == 1) {
                return VIEW_TYPES.FOOTER;
            } else {
                return VIEW_TYPES.NOVIEW;
            }
        } else if (getItemCount() > 0) {
            int last_item = getTotalItemsCount() - 1;
            if (position == last_item && enableLoadMore()) {
                return VIEW_TYPES.FOOTER;
            } else {
                return VIEW_TYPES.NORMAL;
            }
        } else {
            return VIEW_TYPES.NORMAL;
        }
    }

    public static class VIEW_TYPES {
        public static final int NORMAL = 0;
        //this is the default loading footer
        public static final int FOOTER = 2;
        //this is the customized footer
        public static final int NOVIEW = 3;
    }
}
