package com.roodie.materialmovies.views.adapters;

import android.view.View;

import com.marshalchen.ultimaterecyclerview.UltimateRecyclerviewViewHolder;
import com.marshalchen.ultimaterecyclerview.quickAdapter.easyRegularAdapter;
import com.roodie.materialmovies.R;
import com.roodie.materialmovies.views.custom_views.MMoviesImageView;
import com.roodie.materialmovies.views.custom_views.MMoviesTextView;
import com.roodie.materialmovies.views.listeners.RecyclerItemClickListener;
import com.roodie.model.entities.PersonWrapper;

import java.util.List;

/**
 * Created by Roodie on 22.03.2016.
 */
public class PeopleListAdapter extends easyRegularAdapter<PersonWrapper, PeopleListAdapter.PeopleListViewHolder> {

    private RecyclerItemClickListener mClickListener;

    public PeopleListAdapter(List<PersonWrapper> list, RecyclerItemClickListener mClickListener) {
        super(list);
        this.mClickListener = mClickListener;
    }

    @Override
    protected int getNormalLayoutResId() {
        return R.layout.item_list_2line;
    }

    @Override
    protected PeopleListViewHolder newViewHolder(View view) {
        return new PeopleListViewHolder(view, true);
    }

    @Override
    public PeopleListViewHolder newHeaderHolder(View view) {
        return new PeopleListViewHolder(view, false);
    }

    @Override
    protected void withBindHolder(PeopleListViewHolder holder, PersonWrapper data, int position) {

        holder.name.setText(data.getName());
        holder.poster.setAvatarMode(true);
        holder.poster.loadProfile(data);
    }

    public class PeopleListViewHolder extends UltimateRecyclerviewViewHolder implements View.OnClickListener {
        View container;
        MMoviesTextView name;
        MMoviesImageView poster;

        public PeopleListViewHolder(View itemView, boolean isItem) {
            super(itemView);

            if (isItem) {
                container = itemView.findViewById(R.id.container);
                container.setOnClickListener(this);
                poster = (MMoviesImageView) itemView.findViewById(R.id.imageview_poster);
                name = (MMoviesTextView) itemView.findViewById(R.id.title);
            }
        }

        @Override
        public void onClick(View v) {
            final int viewId = v.getId();
            switch (viewId) {
                case R.id.container :
                    mClickListener.onClick(poster, getPosition());
                    break;
            }
        }
    }
}
