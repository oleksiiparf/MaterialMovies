package com.roodie.materialmovies.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.google.common.base.Objects;
import com.roodie.materialmovies.R;
import com.roodie.materialmovies.views.custom_views.MMoviesStarView;
import com.roodie.materialmovies.views.custom_views.MMoviesTextView;
import com.roodie.model.entities.ListItem;
import com.roodie.model.entities.SeasonWrapper;

import java.text.DateFormat;
import java.util.List;

/**
 * Created by Roodie on 01.11.2015.
 */
public class SeasonsAdapter extends BaseAdapter {

    private DateFormat seasonAirDate = DateFormat.getDateInstance(DateFormat.MEDIUM);

    public interface SeasonClickListener {
        public void onSeasonClicked(ViewHolder holder, SeasonWrapper season);
    }

    public interface SeasonStarListener {
        public void onSeasonStarred(SeasonWrapper season);
    }

    public interface PopupMenuClickListener {
        public void onPopupMenuClick(View v, SeasonWrapper season);
    }

    private final Context context;
    private List<ListItem<SeasonWrapper>> mItems;

    //private SeasonClickListener clickListener;
    private SeasonStarListener starListener;
    private PopupMenuClickListener mPopupMenuClickListener;

    public SeasonsAdapter(Context context, List<ListItem<SeasonWrapper>> mItems,
                          SeasonStarListener starListener, PopupMenuClickListener listener) {
        this.context = context;
        this.mItems = mItems;
        this.starListener = starListener;
        this.mPopupMenuClickListener = listener;
    }

    public void setItems(List<ListItem<SeasonWrapper>> items) {
        if (!Objects.equal(items, mItems)) {
            mItems = items;
            notifyDataSetChanged();
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = null;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.item_season, null, true);

            final ViewHolder holder = new ViewHolder();
            holder.episodesAmount = (MMoviesTextView) rowView.findViewById(R.id.textview_episodes_amount);
            holder.seasonAirDate = (MMoviesTextView) rowView.findViewById(R.id.textview_season_airdate);
            holder.seasonTitle = (MMoviesTextView) rowView.findViewById(R.id.textview_season_title);
            holder.starred =  (MMoviesStarView) rowView.findViewById(R.id.star_season);
            holder.contextMenu =  (ImageView) rowView.findViewById(R.id.imageView_context_menu);

            rowView.setTag(holder);
        } else {
            rowView = convertView;
        }

        final ViewHolder holder = (ViewHolder) rowView.getTag();

        final SeasonWrapper season = getItem(position).getListItem();

        holder.seasonTitle.setText(context.getResources().getString(R.string.tv_season_number, season.getSeasonNumber()));

        if (season.getAirDate() != null) {
            holder.seasonAirDate.setText(seasonAirDate.format(season.getAirDate()));
        } else {
            holder.seasonAirDate.setText("");
        }

        if (season.getEpisodeCount() != null ) {
            holder.episodesAmount.setText(context.getResources().getString(R.string.season_episodes_amount, season.getEpisodeCount()));
        } else {
            holder.episodesAmount.setText("");
        }

        holder.starred.setStarred(season.isStarred());
        holder.starred.clearAnimation();


        holder.starred.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                starListener.onSeasonStarred(season);
            }
        });

        holder.contextMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPopupMenuClickListener != null) {
                    mPopupMenuClickListener.onPopupMenuClick(v, season);
                }
            }
        });

        return rowView;
    }

    @Override
    public ListItem<SeasonWrapper> getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getCount() {
        return mItems != null ? mItems.size() : 0;
    }

    static class ViewHolder {
        MMoviesStarView starred;
        MMoviesTextView seasonTitle;
        MMoviesTextView episodesAmount;
        MMoviesTextView seasonAirDate;
        ImageView contextMenu;
    }


}
