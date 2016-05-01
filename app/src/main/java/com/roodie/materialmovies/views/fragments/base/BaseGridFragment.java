package com.roodie.materialmovies.views.fragments.base;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.roodie.materialmovies.mvp.views.MvpLceView;
import com.roodie.materialmovies.views.custom_views.recyclerview.LoadMoreRecyclerLayout;
import com.roodie.materialmovies.views.custom_views.recyclerview.RecyclerInsetsDecoration;
import com.roodie.model.util.FileLog;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Roodie on 01.07.2015.
 */
public abstract class BaseGridFragment<VH extends RecyclerView.ViewHolder, M extends List<? extends Serializable>, V extends MvpLceView<M>>
        extends RecyclerFragment<VH, M, V>
{

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getRecyclerView().enableLoadMoreView(true);
        getRecyclerView().setOnLoadMoreListener(new LoadMoreRecyclerLayout.OnLoadMoreListener() {
            @Override
            public void loadMore(int itemsCount, int maxLastVisiblePosition) {
                FileLog.d("lce", "Fragment : loadMore()");
                onScrolledToBottom();
            }
        });

        getRecyclerView().addItemDecoration(new RecyclerInsetsDecoration(getActivity()));
    }

}

