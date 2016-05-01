package com.roodie.materialmovies.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.roodie.materialmovies.R;
import com.roodie.materialmovies.views.custom_views.MMoviesImageView;
import com.roodie.materialmovies.views.custom_views.MMoviesTextView;
import com.roodie.materialmovies.views.listeners.RecyclerItemClickListener;
import com.roodie.model.entities.PersonWrapper;

import java.util.List;

/**
 * Created by Roodie on 22.03.2016.
 */
public class PeopleListAdapter extends FooterViewListAdapter<List<PersonWrapper>, PeopleListAdapter.PeopleListViewHolder> {

    private RecyclerItemClickListener mClickListener;

    public PeopleListAdapter(Context context, RecyclerItemClickListener mClickListener) {
        super(context);
        this.mClickListener = mClickListener;
    }

    @Override
    public PeopleListViewHolder onCreateViewHolder(ViewGroup parent) {
        View rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_2line, parent, false);
        return new PeopleListViewHolder(rowView, true);
    }

    @Override
    public PeopleListViewHolder getViewHolder(View view) {
        return new PeopleListViewHolder(view, false);
    }

    @Override
    public void onBindViewHolder(PeopleListViewHolder holder, int position) {
        if (position < getTotalItemsCount() && position < getItemCount() ) {

            final PersonWrapper item = items.get(position);

            holder.name.setText(item.getName());
            holder.poster.setAvatarMode(true);
            holder.poster.loadProfile(item);
        }
    }

    public class PeopleListViewHolder extends ListViewHolder implements View.OnClickListener {
        View container;
        MMoviesTextView name;
        MMoviesImageView poster;

        public PeopleListViewHolder(View itemView, boolean isItem) {
            super(itemView, isItem);

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
