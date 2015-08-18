package com.roodie.materialmovies.views.fragments.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.roodie.materialmovies.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Roodie on 28.06.2015.
 */
public abstract class BaseDetailFragment extends BaseFragment {



    protected RecyclerView mRecyclerView;
    private LinearLayout mLeftContainer;



    private Context mContext;

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity.getApplicationContext();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_detail_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mLeftContainer = (LinearLayout) view.findViewById(R.id.left_container);
    }



    protected RecyclerView getRecyclerView() {
        return mRecyclerView;
    }



    public boolean hasLeftContainer() {
        return mLeftContainer != null;
    }

    protected abstract void setUpVisibility();

    protected abstract void initializePresenter();

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

        private List<BaseViewHolder> mBinderList = new ArrayList<>();

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
