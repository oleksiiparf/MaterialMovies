package com.roodie.materialmovies.views.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.roodie.materialmovies.R;
import com.roodie.materialmovies.mvp.presenters.TvSeasonPresenter;
import com.roodie.materialmovies.views.MMoviesApplication;
import com.roodie.materialmovies.views.custom_views.MMoviesImageView;
import com.roodie.materialmovies.views.custom_views.RatingBarLayout;
import com.roodie.materialmovies.views.fragments.base.BaseFragment;
import com.roodie.model.entities.SeasonWrapper;
import com.roodie.model.network.NetworkError;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * Created by Roodie on 25.09.2015.
 */
public class TvSeasonDetailFragment extends BaseFragment implements TvSeasonPresenter.TvSeasonView {

    private static final String LOG_TAG = TvSeasonDetailFragment.class.getSimpleName();

    private static final String KEY_SEASON_SAVE_STATE = "tv_season_on_save_state";


    private TvSeasonPresenter mPresenter;

    private SeasonWrapper mSeason;

    @Bind(R.id.container_season) View mSeasonContainer;
    @Bind(R.id.container_season_image) View mImageContainer;
    @Bind(R.id.imageview_season) MMoviesImageView mSeasonImage;

    @Bind(R.id.textview_season_title) TextView mTitle;
    @Bind(R.id.textview_season_description) TextView mDescription;
    @Bind(R.id.textview_season_release_time) TextView mReleseTime;
    @Bind(R.id.rating_bar) RatingBarLayout mRatingBar;
    @Bind(R.id.textview_season_regulars) TextView mRegulars;
    @Bind(R.id.textview_season_release_day) TextView mReleaseDay;
    @Bind(R.id.textview_number_of_episodes) TextView mEpisodesCount;


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
         * Integer extra.
         */
        String QUERY_SEASON_NUMBER = "_season_number";

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

    public static TvSeasonDetailFragment newInstance(String showId, String seasonId, boolean isMultiPaneLayout) {
        TvSeasonDetailFragment f = new TvSeasonDetailFragment();

        // Supply index input as an argument.
        Bundle args = new Bundle();
        args.putString(InitBundle.QUERY_SEASON_ID, seasonId);
        args.putString(InitBundle.QUERY_SHOW_ID, showId);
        args.putBoolean(InitBundle.QUERY_IS_IN_MULTIPANE_LAYOUT, isMultiPaneLayout);

        f.setArguments(args);
        return f;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mPresenter = MMoviesApplication.from(activity.getApplicationContext()).getTvSeasonDetailPresenter();
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
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            setSeason((SeasonWrapper) savedInstanceState.getSerializable(KEY_SEASON_SAVE_STATE));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(KEY_SEASON_SAVE_STATE, mSeason);
    }

    public TvSeasonPresenter getPresenter() {
        return mPresenter;
    }

    /**
     * TvSeasonView
     */

    @Override
    public void markSeasonAsStared(String showId, String seasonId) {
        //TODO
    }

    @Override
    public void markSeasonAsUnstared(String showId, String seasonId) {
        //TODO
    }

    @Override
    public String getShowId() {
        return getArguments().getString(InitBundle.QUERY_SHOW_ID);
    }

    @Override
    public void setSeason(SeasonWrapper season) {

        mSeason = season;

        mTitle.setText(mSeason.getTitle());
        mDescription.setText(mSeason.getOverview());
        mSeasonImage.loadPoster(mSeason);
        //TODO
    }

    /**
     * MovieView
     */
    @Override
    public void showError(NetworkError error) {
        //TODO
    }

    @Override
    public void showLoadingProgress(boolean visible) {
        getActivity().setProgressBarIndeterminateVisibility(visible);
    }

    @Override
    public void showSecondaryLoadingProgress(boolean visible) {
        //TODO
    }

    @Override
    public String getRequestParameter() {
        return getArguments().getString(InitBundle.QUERY_SEASON_ID);
    }

    @Override
    public void updateDisplayTitle(String title) {
        //TODO
    }

    @Override
    public MovieQueryType getQueryType() {
        return MovieQueryType.TV_SEASON_DETAIL;
    }

    @Override
    public boolean isModal() {
        return false;
    }
}
