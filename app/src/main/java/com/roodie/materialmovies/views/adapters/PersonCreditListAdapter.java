package com.roodie.materialmovies.views.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.roodie.materialmovies.R;
import com.roodie.model.entities.PersonCreditWrapper;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Roodie on 25.07.2015.
 */
public class PersonCreditListAdapter extends RecyclerView.Adapter<PersonCreditListAdapter.ViewHolder> {

    List<PersonCreditWrapper> mItems;

    private final Date mDate;
    DateFormat mMediumDateFormatter = DateFormat.getDateInstance();
    private Context mContext;

    private static final String LOG_TAG = MovieCreditListAdapter.class.getSimpleName();

    public PersonCreditListAdapter(List<PersonCreditWrapper> items, Context context) {
        mItems = items;
        mDate = new Date();
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_list_3line, viewGroup, false);
        ViewHolder holder = new ViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        PersonCreditWrapper credit = mItems.get(i);

        viewHolder.nameTextView.setText(credit.getTitle());

        if (TextUtils.isEmpty(credit.getJob())) {
            viewHolder.characterTextView.setVisibility(View.GONE);
        } else {
            viewHolder.characterTextView.setVisibility(View.VISIBLE);
            viewHolder.characterTextView.setText(credit.getJob());
        }

        mDate.setTime(credit.getReleaseDate());
        viewHolder.release.setText(mContext.getString(R.string.movie_release_date,
                mMediumDateFormatter.format(mDate)));
        //viewHolder.imageView. loadProfile()
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView nameTextView;
        TextView characterTextView;
        TextView release;
        ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            nameTextView = (TextView) itemView.findViewById(R.id.title);
            characterTextView = (TextView) itemView.findViewById(R.id.subtitle_1);
            release = (TextView) itemView.findViewById(R.id.textview_subtitle_2);
            imageView = (ImageView) itemView.findViewById(R.id.poster);

        }
    }
}
