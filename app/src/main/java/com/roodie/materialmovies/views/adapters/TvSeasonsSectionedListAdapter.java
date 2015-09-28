package com.roodie.materialmovies.views.adapters;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.roodie.materialmovies.R;
import com.roodie.materialmovies.views.custom_views.MMoviesStarView;
import com.roodie.model.entities.ListItem;
import com.roodie.model.entities.SeasonWrapper;

/**
 * Created by Roodie on 23.09.2015.
 */
public class TvSeasonsSectionedListAdapter extends BaseSectionedListAdapter<SeasonWrapper> {

    private static final String LOG_TAG = SearchMoviesSectionedListAdapter.class.getSimpleName();

    public interface SeasonStarListener {
        public void onSeasonStarClicked(SeasonWrapper season);
    }

    private SeasonStarListener starListener;

    public TvSeasonsSectionedListAdapter(Activity activity,SeasonStarListener starListener) {
        super(activity, R.layout.item_season, R.layout.item_list_movie_section_header);
        this.starListener = starListener;
    }

    @Override
    protected void bindView(int position, View view, ListItem<SeasonWrapper> item) {
        final SeasonWrapper season = item.getListItem();

        final MMoviesStarView starSeasonView = (MMoviesStarView) view.findViewById(R.id.star_season);
        starSeasonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                starListener.onSeasonStarClicked(season);
            }
        });

        final TextView title = (TextView) view.findViewById(R.id.textview_season_title);
        title.setText(season.getTitle());

        final TextView airDate = (TextView) view.findViewById(R.id.textview_season_airdate);
        airDate.setText(String.valueOf(season.getAirDate()));

        final TextView episodesNumber = (TextView) view.findViewById(R.id.textview_episodes_number);
        episodesNumber.setText(season.getEpisodeCount());

    }



}
