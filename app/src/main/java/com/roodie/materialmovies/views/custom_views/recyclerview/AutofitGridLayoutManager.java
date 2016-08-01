package com.roodie.materialmovies.views.custom_views.recyclerview;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.marshalchen.ultimaterecyclerview.UltimateViewAdapter;

/**
 * Created by Roodie on 29.07.2016.
 */

public class AutofitGridLayoutManager extends GridLayoutManager {

    private final UltimateViewAdapter mAdapter;
    protected int headerSpan = 1;
    private int minItemWidth;

    protected GridLayoutManager.SpanSizeLookup mSpanSizeLookUp = new GridLayoutManager.SpanSizeLookup() {
        @Override
        public int getSpanSize(int position) {
            if (mAdapter.getItemViewType(position) == UltimateViewAdapter.VIEW_TYPES.FOOTER) {
                return getSpanCount();
            } else if (mAdapter.getItemViewType(position) == UltimateViewAdapter.VIEW_TYPES.HEADER) {
                return getSpanCount();
            } else
                return getNormalSpanCount(position);
        }
    };

    protected int getSpanInterval(int position) {
        int mIntervalHeader = getSpanCount() * 10;
        int h = position % mIntervalHeader == 0 ? getSpanCount() : 1;
        return h;

    }

    protected int getHeaderSpanCount(int n) {
        return headerSpan;
    }

    protected int getNormalSpanCount(int item_position) {
        return 1;
    }

    protected GridLayoutManager.SpanSizeLookup decideSpanSizeCal() {
        return mSpanSizeLookUp;
    }

    public AutofitGridLayoutManager(Context context, int minItemWidth, UltimateViewAdapter mAdapter) {
        super(context, 1);
        this.mAdapter = mAdapter;
        this.minItemWidth = minItemWidth;
        setSpanSizeLookup(decideSpanSizeCal());
    }

    public AutofitGridLayoutManager(Context context, int minItemWidth, int orientation, boolean reverseLayout, UltimateViewAdapter mAdapter) {
        super(context, 1, orientation, reverseLayout);
        this.mAdapter = mAdapter;
        this.minItemWidth = minItemWidth;
        setSpanSizeLookup(decideSpanSizeCal());
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler,
                                 RecyclerView.State state) {
        updateSpanCount();
        super.onLayoutChildren(recycler, state);
    }

    private void updateSpanCount() {
        int spanCount = getWidth() / minItemWidth;
        if (spanCount < 1) {
            spanCount = 1;
        }
        this.setSpanCount(spanCount);
    }
}
