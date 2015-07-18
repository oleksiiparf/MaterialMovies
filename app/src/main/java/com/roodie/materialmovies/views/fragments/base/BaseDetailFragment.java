package com.roodie.materialmovies.views.fragments.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.roodie.materialmovies.R;
import com.roodie.materialmovies.views.custom_views.PinnedSectionListView;

/**
 * Created by Roodie on 28.06.2015.
 */
public abstract class BaseDetailFragment extends BaseFragment implements AdapterView.OnItemClickListener {

    private PinnedSectionListView mListView;
    private ListAdapter mAdapter;

    private TextView mEmptyView;


    @Override
    public void onResume() {
        super.onResume();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_detail_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAdapter = createListAdapter();

        mListView = (PinnedSectionListView) view.findViewById(android.R.id.list);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);

        mEmptyView = (TextView) view.findViewById(android.R.id.empty);
        mListView.setEmptyView(mEmptyView);
    }

    public void setEmptyText(String stringId) {
       if (mEmptyView != null) {
           mEmptyView.setText(stringId);
       }
    }

    protected interface DetailType<E> {

        public String name();

        public int getLayoutId();

        public int getViewType();

        public boolean isEnabled();

    }

    protected abstract ListAdapter createListAdapter();

    protected PinnedSectionListView getListView() {
        return mListView;
    }

    protected ListAdapter getListAdapter() {
        return mAdapter;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }


}
