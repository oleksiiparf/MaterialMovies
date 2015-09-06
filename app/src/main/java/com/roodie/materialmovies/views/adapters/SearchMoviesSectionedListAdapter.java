package com.roodie.materialmovies.views.adapters;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.roodie.materialmovies.R;
import com.roodie.materialmovies.views.MMoviesApplication;
import com.roodie.materialmovies.views.custom_views.MMoviesImageView;
import com.roodie.model.entities.ListItem;
import com.roodie.model.entities.MovieWrapper;

import java.text.DateFormat;

/**
 * Created by Roodie on 06.09.2015.
 */
public class SearchMoviesSectionedListAdapter extends BaseSectionedListAdapter<MovieWrapper> {

    private static final String LOG_TAG = SearchMoviesSectionedListAdapter.class.getSimpleName();

    private DateFormat movieReleaseDate = DateFormat.getDateInstance(DateFormat.MEDIUM);

    public SearchMoviesSectionedListAdapter(Activity activity) {
        super(activity, R.layout.item_list_3line, R.layout.item_list_movie_section_header);
        MMoviesApplication.from(activity).inject(this);
    }

    @Override
    protected void bindView(int position, View view, ListItem<MovieWrapper> item) {
        MovieWrapper movie = item.getListItem();

        final TextView title = (TextView) view.findViewById(R.id.textview_title);
        if (movie.getYear() > 0) {
            title.setText(mActivity.getString(R.string.movie_title_year,
                    movie.getTitle(), movie.getYear()));
        } else {
            title.setText(movie.getTitle());
        }

        final TextView ratingTextView = (TextView) view.findViewById(R.id.textview_subtitle_1);
        ratingTextView.setText(mActivity.getString(R.string.movie_rating_votes,
                movie.getAverageRatingPercent(), movie.getRatingVotes()));

        final TextView release = (TextView) view.findViewById(R.id.textview_subtitle_2);
        release.setText((movieReleaseDate.format(movie.getReleaseDate())));

        final MMoviesImageView imageView = (MMoviesImageView) view.findViewById(R.id.imageview_poster);
        imageView.loadPoster(movie);
    }

}
