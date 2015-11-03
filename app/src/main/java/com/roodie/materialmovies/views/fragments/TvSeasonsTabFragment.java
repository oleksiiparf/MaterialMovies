package com.roodie.materialmovies.views.fragments;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.google.common.base.Preconditions;
import com.roodie.materialmovies.mvp.presenters.TvSeasonsTabPresenter;
import com.roodie.materialmovies.views.MMoviesApplication;
import com.roodie.materialmovies.views.fragments.base.BaseTabFragment;
import com.roodie.model.entities.SeasonWrapper;
import com.roodie.model.network.NetworkError;

import java.util.ArrayList;

/**
 * Created by Roodie on 28.10.2015.
 */
public class TvSeasonsTabFragment extends BaseTabFragment<TvSeasonsTabFragment.TvSeasonsPagerAdapter> implements TvSeasonsTabPresenter.TvSeasonsTabView {

    protected static final String LOG_TAG = TvSeasonsTabFragment.class.getSimpleName();

    private TvSeasonsTabPresenter mPresenter;

    private ArrayList<SeasonWrapper> mSeasons;

    private ViewPager mPager;
    private TabLayout mTabs;

    private boolean mDualPane;

    private int mShowId;

    private Context mContext;

    public interface  InitBundle {

        String QUERY_SHOW_ID = "_show_id";

        String QUERY_DUAL_PANE = "_dual_pane";

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity.getApplicationContext();
        mPresenter = MMoviesApplication.from(activity.getApplicationContext()).getTvSeasonsTabPresenter();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPresenter.attachView(this);
        mPresenter.initialize();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mPresenter.detachView(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mPresenter.onPause();
    }

    @Override
    protected TvSeasonsPagerAdapter setupAdapter() {
        return new TvSeasonsPagerAdapter(getChildFragmentManager(), null, false);
    }

    @Override
    public boolean isModal() {
        return false;
    }

    @Override
    public MovieQueryType getQueryType() {
        return null;
    }

    @Override
    public void updateDisplayTitle(String title) {

    }

    @Override
    public String getRequestParameter() {
        return getArguments().getString(InitBundle.QUERY_SHOW_ID);
    }

    @Override
    public void showSecondaryLoadingProgress(boolean visible) {

    }

    @Override
    public void showLoadingProgress(boolean visible) {

    }

    @Override
    public void showError(NetworkError error) {

    }

    @Override
    public void setupTabs(ArrayList<SeasonWrapper> list) {
        Preconditions.checkNotNull(list, "list cannot be null");
        getAdapter().updateSeasonsList(list);
        //TvSeasonsPagerAdapter adapter = new TvSeasonsPagerAdapter(getChildFragmentManager(), list, mContext, false);
    }

    @Override
    public void setCurrentPage(int position) {
        mPager.setCurrentItem(position, true);
    }

    @Override
    public int updateSeasonsList(int initialSeasonId) {
        return 0;
    }

    public void updateSeasonsList() {
       updateSeasonsList(0);
    }

    @Override
    public int getPositionForSeason(int seasonId) {
        // find page index for this episode
        for (int position = 0; position < mSeasons.size(); position++) {
            if (mSeasons.get(position).getId() == seasonId) {
                return position;
            }
        }

        return 0;
    }

    @Override
    public boolean isDualPane() {
        return getArguments().getBoolean(InitBundle.QUERY_DUAL_PANE);
    }

    @Override
    protected String getTabTitle(int position) {
        return null;
    }

    /**
     * TvSeasonsPagerAdapter
     */
    protected class TvSeasonsPagerAdapter extends BaseTabFragment.TabPagerAdapter {

        private ArrayList<SeasonWrapper> mSeasons;

        private final boolean isMultiPane;

        public TvSeasonsPagerAdapter(FragmentManager fm, ArrayList<SeasonWrapper> mSeasons,
                                     boolean isMultiPane) {
            super(fm);
            this.mSeasons = mSeasons;
            this.isMultiPane = isMultiPane;
        }

        @Override
        public Fragment getItem(int position) {
            return TvSeasonDetailFragment.newInstance(1, false);
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return super.getPageTitle(position);
        }

        @Override
        public int getCount() {
            if (mSeasons != null) {
                return mSeasons.size();
            } else {
                return 0;
            }
        }

        public void updateSeasonsList(ArrayList<SeasonWrapper> list) {
            if (list != null)
                mSeasons = list;
            notifyDataSetChanged();
        }




    }
}
