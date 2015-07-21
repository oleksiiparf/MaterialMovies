package com.roodie.materialmovies.views.fragments.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.roodie.materialmovies.R;
import com.roodie.materialmovies.views.custom_views.MovieDetailCardLayout;
import com.roodie.materialmovies.views.custom_views.PinnedSectionListView;
import com.roodie.materialmovies.views.custom_views.RecyclerView;

import java.util.List;

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

        public int ordinal();

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

    protected abstract  class BaseDetailAdapter<E extends DetailType> extends BaseAdapter
    implements PinnedSectionListView.PinnedSectionListAdapter {
        private List<E> mItems;

        public void setItems(List<E> mItems) {
            this.mItems = mItems;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mItems != null ? mItems.size() : 0;
        }

        @Override
        public E getItem(int position) {
            return mItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return mItems.get(position).ordinal();
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public int getItemViewType(int position) {
            return getItem(position).getViewType();
        }

        @Override
        public boolean isEnabled(int position) {
            return getItem(position).isEnabled();
        }

        @Override
        public boolean areAllItemsEnabled() {
            return false;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final E item = getItem(position);

            if (convertView == null) {
                final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                convertView = inflater.inflate(item.getLayoutId(), parent, false);
            }
            // Bind to the view
            bindView(item, convertView);
            return convertView;
        }


        protected abstract void bindView(final E item, final View view);

        protected void populateDetailGrid(
                ViewGroup layout,
                MovieDetailCardLayout cardLayout,
                View.OnClickListener seeMoreClickListener,
                BaseAdapter adapter) {

            final RecyclerView recyclerView = new RecyclerView(layout);
            recyclerView.recycleViews();

            if (!adapter.isEmpty()) {
                final int numItems = getResources().getInteger(R.integer.detail_card_max_items);
                final int adpterCount = adapter.getCount();

                for (int i = 0; i < Math.min(numItems, adpterCount); i++) {
                    View view = adapter.getView(i, recyclerView.getRecycledView(), layout);
                    layout.addView(view);
                }

                final boolean showMore = numItems < adapter.getCount();
                cardLayout.setSeeMoreVisibility(showMore);
                cardLayout.setSeeMoreOnClickListener(showMore ? seeMoreClickListener : null) ;
            }

            recyclerView.clearRecycledViews();

        }


        protected void rebindView(final E item) {
            ListView listView = getListView();

            for (int i = 0, m = listView.getChildCount(); i < m; i++) {
                View child = listView.getChildAt(i);
                if (child != null && child.getTag() == item) {
                    bindView(item, child);
                    return;
                }

            }
        }

        @Override
        public boolean isItemViewTypePinned(int viewType) {
            return false;
        }
    }



}
