package com.roodie.materialmovies.views.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.roodie.materialmovies.R;
import com.roodie.materialmovies.mvp.views.SeasonDetailView;
import com.roodie.materialmovies.views.custom_views.MMoviesImageView;
import com.roodie.materialmovies.views.custom_views.RatingBarLayout;
import com.roodie.model.entities.SeasonWrapper;
import com.roodie.model.network.NetworkError;

import butterknife.ButterKnife;


/**
 * Created by Roodie on 25.09.2015.
 */
public class TvSeasonDetailFragment extends Fragment implements SeasonDetailView {

    private static final String LOG_TAG = TvSeasonDetailFragment.class.getSimpleName();

    private View mSeasonContainer;
    private View mImageContainer;
    private MMoviesImageView mSeasonImage;

    private TextView mTitle;
    private TextView mDescription;
    private TextView mReleseTime;
    private RatingBarLayout mRatingBar;
    private TextView mRegulars;
    private TextView mReleaseDay;
    private TextView mEpisodesCount;


    public interface InitBundle {

        /**
         * Integer extra.
         */
        String QUERY_SHOW_ID = "_show_id";

        /**
         * Integer extra.
         */
        String QUERY_SEASON_ID = "_season_id";

        /**
         * Boolean extra.
         */
        String QUERY_IS_IN_MULTIPANE_LAYOUT = "_multipane";
    }


    public static TvSeasonDetailFragment newInstance(int seasonId, boolean isInMultiPaneLayout) {
        TvSeasonDetailFragment f = new TvSeasonDetailFragment();

        // Supply index input as an argument.
        Bundle args = new Bundle();
        args.putInt(InitBundle.QUERY_SEASON_ID, seasonId);
        args.putBoolean(InitBundle.QUERY_IS_IN_MULTIPANE_LAYOUT, isInMultiPaneLayout);
        f.setArguments(args);
        return f;
    }

    public static TvSeasonDetailFragment newInstance(String showId, String seasonId) {
        TvSeasonDetailFragment f = new TvSeasonDetailFragment();

        // Supply index input as an argument.
        Bundle args = new Bundle();
        args.putString(InitBundle.QUERY_SEASON_ID, seasonId);
        args.putString(InitBundle.QUERY_SHOW_ID, showId);
        f.setArguments(args);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_season_detail, container, false);
        ButterKnife.bind(this, v);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mSeasonContainer = view.findViewById(R.id.container_season);
        mImageContainer = view.findViewById(R.id.container_season_image);
        mSeasonImage = (MMoviesImageView) view.findViewById(R.id.imageview_season);

        mTitle = (TextView) view.findViewById(R.id.textview_season_title);
        mDescription = (TextView) view.findViewById(R.id.textview_season_description);
        mReleseTime = (TextView) view.findViewById(R.id.textview_season_release_time);
        RatingBarLayout mRatingBar = (RatingBarLayout) view.findViewById(R.id.rating_bar);
        TextView mRegulars = (TextView) view.findViewById(R.id.textview_season_regulars);
        TextView mReleaseDay = (TextView) view.findViewById(R.id.textview_season_release_day);
        TextView mEpisodesCount = (TextView) view.findViewById(R.id.textview_number_of_episodes);

    }

    /**
     * SeasonDetailView
     */
    @Override
    public void setTvSeason(SeasonWrapper season) {

    }

    @Override
    public void markSeasonAsStared(String showId, String seasonId) {

    }

    @Override
    public void markSeasonAsUnstared(String showId, String seasonId) {

    }

    @Override
    public String getRequestTvShow() {
        return null;
    }

    /**
     * MovieView
     */
    @Override
    public void showError(NetworkError error) {

    }

    @Override
    public void showLoadingProgress(boolean visible) {

    }

    @Override
    public void showSecondaryLoadingProgress(boolean visible) {

    }

    @Override
    public String getRequestParameter() {
        return null;
    }

    @Override
    public void updateDisplayTitle(String title) {

    }

    @Override
    public MovieQueryType getQueryType() {
        return null;
    }

    @Override
    public boolean isModal() {
        return false;
    }
}
