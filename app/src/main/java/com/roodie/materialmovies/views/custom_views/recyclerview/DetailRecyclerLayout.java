package com.roodie.materialmovies.views.custom_views.recyclerview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import com.roodie.materialmovies.views.fragments.base.BaseDetailFragment;

/**
 * Created by Roodie on 27.02.2016.
 */
public class DetailRecyclerLayout extends BaseRecyclerLayout {

    public BaseDetailFragment.EnumListDetailAdapter mAdapter;

    public DetailRecyclerLayout(Context context) {
        super(context);
        initViews();
    }

    public DetailRecyclerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews();
    }

    public DetailRecyclerLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews();
    }

    @Override
    protected void setRecyclerType() {

    }

    @Override
    protected void initializeEmptyObserver() {
        emptyObserver = new RecyclerView.AdapterDataObserver() {

            @Override
            public void onChanged() {
                if(mAdapter != null && mEmptyView != null) {
                    if(mAdapter.getItemCount() == 0) {
                        mEmptyView.setVisibility(View.VISIBLE);
                        mRecyclerView.setVisibility(View.GONE);
                    }
                    else {
                        mEmptyView.setVisibility(View.GONE);
                        mRecyclerView.setVisibility(View.VISIBLE);
                    }
                }

            }
        };
    }

    public BaseDetailFragment.EnumListDetailAdapter getAdapter() {
        return mAdapter;
    }

    public void setAdapter(BaseDetailFragment.EnumListDetailAdapter adapter) {
       /* if (adapter.equals(mAdapter))
            return;*/
        this.mAdapter = adapter;
        mRecyclerView.setAdapter(mAdapter);
        if (mAdapter != null) {
            if (mAdapter.getItemCount() == 0)
                setContentShown(false);
            else
                setContentShown(true);

        }
    }
}
