package com.roodie.materialmovies.views.adapters;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.roodie.materialmovies.R;
import com.roodie.materialmovies.views.MMoviesApplication;
import com.roodie.materialmovies.views.custom_views.MMoviesImageView;
import com.roodie.model.entities.ListItem;
import com.roodie.model.entities.ShowWrapper;

import java.text.DateFormat;

/**
 * Created by Roodie on 06.09.2015.
 */
public class SearchShowsSectionedListAdapter  extends BaseSectionedListAdapter<ShowWrapper> {

    private static final String LOG_TAG = SearchShowsSectionedListAdapter.class.getSimpleName();

    private DateFormat movieReleaseDate = DateFormat.getDateInstance(DateFormat.MEDIUM);

    public SearchShowsSectionedListAdapter(Activity activity) {
        super(activity, R.layout.item_list_3line, R.layout.item_list_movie_section_header);
        MMoviesApplication.from(activity).inject(this);
    }

    @Override
    protected void bindView(int position, View view, ListItem<ShowWrapper> item) {
        ShowWrapper show = item.getListItem();

        final TextView title = (TextView) view.findViewById(R.id.textview_title);
            title.setText(show.getTitle());


        final TextView ratingTextView = (TextView) view.findViewById(R.id.textview_subtitle_1);
        ratingTextView.setText(mActivity.getString(R.string.movie_rating_votes,
                show.getAverageRatingPercent(), show.getRatingVotes()));

        final TextView release = (TextView) view.findViewById(R.id.textview_subtitle_2);
       // release.setText((movieReleaseDate.format(show.getReleaseDate())));

        final MMoviesImageView imageView = (MMoviesImageView) view.findViewById(R.id.imageview_poster);
        imageView.loadPoster(show);
    }

}
