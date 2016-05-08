package com.roodie.materialmovies.views.fragments.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Toast;

import com.roodie.materialmovies.R;
import com.roodie.materialmovies.mvp.views.MvpLceView;
import com.roodie.materialmovies.util.StringUtils;
import com.roodie.materialmovies.views.custom_views.recyclerview.BaseRecyclerLayout;
import com.roodie.materialmovies.views.listeners.AppBarStateChangeListener;
import com.roodie.model.network.NetworkError;
import com.roodie.model.util.FileLog;
import com.roodie.model.util.TextUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.InjectView;
import butterknife.Optional;

/**
 * Created by Roodie on 28.06.2015.
 */
public abstract class BaseDetailFragment<M extends Serializable, RV extends BaseRecyclerLayout> extends BaseMvpFragment implements MvpLceView<M>{


    protected RV mPrimaryRecyclerView;

    @Optional @InjectView(R.id.appbar)
    protected AppBarLayout mAppBar;

    @Optional @InjectView(R.id.left_container)
    ViewGroup mLeftContainer;

    protected String mToolbarTitle;

    private int mLastFirstVisiblePosition;

    @Optional
    @InjectView(R.id.backdrop_toolbar)
    protected CollapsingToolbarLayout mCollapsingToolbarLayout;

    protected RecyclerView.OnScrollListener expandableScrollListener = new RecyclerView.OnScrollListener() {
        int scrollDy = 0;
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if (scrollDy == 0 && (newState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE)) {
                mAppBar.setExpanded(true);
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            scrollDy += dy;
        }
    };

    protected AppBarLayout.OnOffsetChangedListener offsetListener = new AppBarStateChangeListener() {
        @Override
        public void onStateChanged(AppBarLayout appBarLayout, AppBarStateChangeListener.State
                state) {
            if (state == AppBarStateChangeListener.State.EXPANDED || state == AppBarStateChangeListener.State.IDLE) {
                mCollapsingToolbarLayout.setTitle("");
            } else {
                mCollapsingToolbarLayout.setTitle(mToolbarTitle);
            }
        }
    };

    protected Context mContext;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity.getApplicationContext();
    }

    @Override
    public void onResume() {
        super.onResume();
        ((LinearLayoutManager) mPrimaryRecyclerView.getLayoutManager()).scrollToPosition(mLastFirstVisiblePosition);

        if (mAppBar != null) {
            mAppBar.addOnOffsetChangedListener(offsetListener);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mLastFirstVisiblePosition = ((LinearLayoutManager)mPrimaryRecyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();

        if (mAppBar != null)
            mAppBar.removeOnOffsetChangedListener(offsetListener);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("FIRST_VISIBLE_POSITION", mLastFirstVisiblePosition);
        FileLog.d("Base", "on save state");
        super.onSaveInstanceState(outState);
    }



    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_detail_list;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        FileLog.d("Base", "on view created");
        super.onViewCreated(view, savedInstanceState);

        if (savedInstanceState != null) {
            mLastFirstVisiblePosition = savedInstanceState.getInt("FIRST_VISIBLE_POSITION");
        }

        mPrimaryRecyclerView = (RV) view.findViewById(R.id.primary_recycler_view);
        mPrimaryRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        if (mCollapsingToolbarLayout != null) {
            mCollapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));
        }
    }

    public RV getRecyclerView() {
        return mPrimaryRecyclerView;
    }

    public boolean hasLeftContainer() {
        return mLeftContainer != null;
    }

    @Override
    public void showError(NetworkError error) {
        toast(getText(StringUtils.getMessageByError(error)).toString());
    }

    @Override
    public void showLoadingProgress(boolean visible) {
        getActivity().setProgressBarIndeterminateVisibility(visible);
    }

    public void toast(final String message) {
        if (!this.isResumed()) {
            return;
        }
        Toast.makeText(getBaseActivity(), TextUtils.toSpanned(getBaseActivity(), message, R.color.mm_green), Toast.LENGTH_SHORT).show();

    }

    /**
     * BaseViewHolder
     *
     * @param <T>
     */
    abstract public class BaseViewHolder<T extends RecyclerView.ViewHolder> {

        private BaseDetailAdapter mDataBindAdapter;

        public BaseViewHolder(BaseDetailAdapter dataBindAdapter) {
            mDataBindAdapter = dataBindAdapter;
        }

        abstract public T newViewHolder(ViewGroup parent);

        abstract public void bindViewHolder(T holder, int position);

        abstract public int getItemCount();

        public final void notifyDataSetChanged() {
            mDataBindAdapter.notifyDataSetChanged();
        }

        public final void notifyBinderDataSetChanged() {
            notifyBinderItemRangeChanged(0, getItemCount());
        }

        public final void notifyBinderItemChanged(int position) {
            mDataBindAdapter.notifyBinderItemChanged(this, position);
        }

        public final void notifyBinderItemRangeChanged(int positionStart, int itemCount) {
            mDataBindAdapter.notifyBinderItemRangeChanged(this, positionStart, itemCount);
        }

        public final void notifyBinderItemInserted(int position) {
            mDataBindAdapter.notifyBinderItemInserted(this, position);
        }

        public final void notifyBinderItemMoved(int fromPosition, int toPosition) {
            mDataBindAdapter.notifyBinderItemMoved(this, fromPosition, toPosition);
        }

        public final void notifyBinderItemRangeInserted(int positionStart, int itemCount) {
            mDataBindAdapter.notifyBinderItemRangeInserted(this, positionStart, itemCount);
        }

        public final void notifyBinderItemRemoved(int position) {
            mDataBindAdapter.notifyBinderItemRemoved(this, position);
        }

        public final void notifyBinderItemRangeRemoved(int positionStart, int itemCount) {
            mDataBindAdapter.notifyBinderItemRangeRemoved(this, positionStart, itemCount);
        }
    }

    /**
     * BaseDetailAdapter
     */
    abstract public class BaseDetailAdapter extends android.support.v7.widget.RecyclerView.Adapter<android.support.v7.widget.RecyclerView.ViewHolder> {

        @Override
        public android.support.v7.widget.RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return getDataBinder(viewType).newViewHolder(parent);
        }

        @Override
        public void onBindViewHolder(android.support.v7.widget.RecyclerView.ViewHolder viewHolder, int position) {
            int binderPosition = getBinderPosition(position);
            getDataBinder(viewHolder.getItemViewType()).bindViewHolder(viewHolder, binderPosition);
        }

        @Override
        public abstract int getItemCount();

        @Override
        public abstract int getItemViewType(int position);

        public abstract <T extends BaseViewHolder> T getDataBinder(int viewType);

        public abstract int getPosition(BaseViewHolder binder, int binderPosition);

        public abstract int getBinderPosition(int position);

        public void notifyBinderItemChanged(BaseViewHolder binder, int binderPosition) {
            notifyItemChanged(getPosition(binder, binderPosition));
        }

        public abstract void notifyBinderItemRangeChanged(BaseViewHolder binder, int positionStart,
                                                          int itemCount);

        public void notifyBinderItemInserted(BaseViewHolder binder, int binderPosition) {
            notifyItemInserted(getPosition(binder, binderPosition));
        }

        public void notifyBinderItemMoved(BaseViewHolder binder, int fromPosition, int toPosition) {
            notifyItemMoved(getPosition(binder, fromPosition), getPosition(binder, toPosition));
        }

        public abstract void notifyBinderItemRangeInserted(BaseViewHolder binder, int positionStart,
                                                           int itemCount);

        public void notifyBinderItemRemoved(BaseViewHolder binder, int binderPosition) {
            notifyItemRemoved(getPosition(binder, binderPosition));
        }

        public abstract void notifyBinderItemRangeRemoved(BaseViewHolder binder, int positionStart,
                                                          int itemCount);
    }

    /**
     * ListDetailAdapter
     */
    public class ListDetailAdapter extends BaseDetailAdapter {

        protected List<BaseViewHolder> mBinderList = new ArrayList<>();

        @Override
        public int getItemCount() {
            int itemCount = 0;
            for (BaseViewHolder binder : mBinderList) {
                itemCount += binder.getItemCount();
            }
            return itemCount;
        }

        @Override
        public int getItemViewType(int position) {
            int itemCount = 0;
            for (int viewType = 0; viewType < mBinderList.size(); viewType++) {
                itemCount += mBinderList.get(viewType).getItemCount();
                if (position < itemCount) {
                    return viewType;
                }
            }
            throw new IllegalArgumentException("arg position is invalid");
        }

        @Override
        public <T extends BaseViewHolder> T getDataBinder(int viewType) {
            return (T) mBinderList.get(viewType);
        }

        @Override
        public int getPosition(BaseViewHolder binder, int binderPosition) {
            int viewType = mBinderList.indexOf(binder);
            if (viewType < 0) {
                throw new IllegalStateException("binder does not exist in adapter");
            }

            int position = binderPosition;
            for (int i = 0; i < viewType; i++) {
                position += mBinderList.get(i).getItemCount();
            }

            return position;
        }

        @Override
        public int getBinderPosition(int position) {
            int binderItemCount;
            for (int i = 0; i < mBinderList.size(); i++) {
                binderItemCount = mBinderList.get(i).getItemCount();
                if (position - binderItemCount < 0) {
                    break;
                }
                position -= binderItemCount;
            }
            return position;
        }

        @Override
        public void notifyBinderItemRangeChanged(BaseViewHolder binder, int positionStart, int itemCount) {
            notifyItemRangeChanged(getPosition(binder, positionStart), itemCount);
        }

        @Override
        public void notifyBinderItemRangeInserted(BaseViewHolder binder, int positionStart, int itemCount) {
            notifyItemRangeInserted(getPosition(binder, positionStart), itemCount);
        }

        @Override
        public void notifyBinderItemRangeRemoved(BaseViewHolder binder, int positionStart, int itemCount) {
            notifyItemRangeRemoved(getPosition(binder, positionStart), itemCount);
        }

        public List<BaseViewHolder> getBinderList() {
            return mBinderList;
        }

        public void addHeaderBinder(BaseViewHolder binder) {
            mBinderList.add(binder);
        }

        public void addBinder(BaseViewHolder binder) {
            mBinderList.add(binder);
        }

        public void addAllBinder(List<BaseViewHolder> dataSet) {
            mBinderList.addAll(dataSet);
        }

        public void addAllBinder(BaseViewHolder... dataSet) {
            mBinderList.addAll(Arrays.asList(dataSet));
        }

        public void setBinder(int index, BaseViewHolder binder) {
            mBinderList.set(index, binder);
        }

        public void removeBinder(int index) {
            mBinderList.remove(index);
        }

        public void removeBinder(BaseViewHolder binder) {
            mBinderList.remove(binder);
        }

        public void clearBinderList() {
            mBinderList.clear();
        }
    }

    /**
     * EnumListDetailAdapter
     *
     * @param <E>
     */
    public abstract class EnumListDetailAdapter<E extends Enum<E>> extends ListDetailAdapter {

        public <T extends BaseViewHolder> T getDataBinder(E e) {
            return getDataBinder(e.ordinal());
        }
    }

}
