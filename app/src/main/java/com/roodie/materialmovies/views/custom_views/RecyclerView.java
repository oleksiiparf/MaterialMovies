package com.roodie.materialmovies.views.custom_views;

import android.view.View;
import android.view.ViewGroup;

import com.google.common.base.Preconditions;

import java.util.ArrayList;

/**
 * Created by Roodie on 20.07.2015.
 */
public class RecyclerView {

    private final ViewGroup mViewGroup;
    private final ArrayList<View> mRecycledViews;

    public RecyclerView(ViewGroup mViewGroup) {
        this.mViewGroup = Preconditions.checkNotNull(mViewGroup, "mViewGroup cannot be null");
        mRecycledViews = new ArrayList<>();
    }

    public void recycleViews() {
        if (mViewGroup.getChildCount() > 0) {
            for (int i = 0, z = mViewGroup.getChildCount(); i < z; i++) {
                mRecycledViews.add(mViewGroup.getChildAt(i));
            }
            mViewGroup.removeAllViews();
        }
    }

    public View getRecycledView() {
        if (!mRecycledViews.isEmpty()) {

            View view = mRecycledViews.get(mRecycledViews.size() - 1);
            mRecycledViews.remove(mRecycledViews.size() - 1);

            return view;
        }
        return null;
    }
    
    public void clearRecycledViews() {
        mRecycledViews.clear();
    }
}
