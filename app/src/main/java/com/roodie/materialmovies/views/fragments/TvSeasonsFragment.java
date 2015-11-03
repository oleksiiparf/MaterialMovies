package com.roodie.materialmovies.views.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

import com.roodie.materialmovies.R;
import com.roodie.materialmovies.mvp.presenters.ShowDetailPresenter;
import com.roodie.materialmovies.views.MMoviesApplication;
import com.roodie.materialmovies.views.adapters.SeasonsAdapter;
import com.roodie.materialmovies.views.adapters.SeasonsAdapter.PopupMenuClickListener;
import com.roodie.materialmovies.views.adapters.SeasonsAdapter.SeasonStarListener;
import com.roodie.materialmovies.views.fragments.base.BaseListFragment;
import com.roodie.model.Display;
import com.roodie.model.entities.ListItem;
import com.roodie.model.entities.SeasonWrapper;
import com.roodie.model.entities.ShowWrapper;
import com.roodie.model.network.NetworkError;

import java.util.List;

/**
 * Created by Roodie on 23.09.2015.
 */
public class TvSeasonsFragment extends BaseListFragment<ListView> implements ShowDetailPresenter.ShowDetailView,
        SeasonStarListener, PopupMenuClickListener {

    private ShowDetailPresenter mPresenter;

    private SeasonsAdapter mAdapter;

    private boolean mDualPane;

    private int mStartingPosition;

    public interface InitBundle {

        String QUERY_SHOW_ID = "show_id";

        String QUERY_SEASON_ID = "season_id";

        String QUERY_SEASON_NUMBER = "season_number";

        String QUERY_STARTING_POSITION = "starting_position";
    }

    public static TvSeasonsFragment newInstance(int showId, int seasonId, int seasonNumber,
                                               int startingPosition) {
        TvSeasonsFragment f = new TvSeasonsFragment();

        Bundle args = new Bundle();
        args.putInt(InitBundle.QUERY_SHOW_ID, showId);
        args.putInt(InitBundle.QUERY_SEASON_ID, seasonId);
        args.putInt(InitBundle.QUERY_SEASON_NUMBER, seasonNumber);
        args.putInt(InitBundle.QUERY_STARTING_POSITION, startingPosition);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mPresenter = MMoviesApplication.from(activity.getApplicationContext()).getDetailShowPresenter();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        // Check to see if we have a frame in which to embed the details
        // fragment directly in the containing UI.
       View pager = getActivity().findViewById(R.id.pager_seasons);
       mDualPane = pager != null && pager.getVisibility() == View.VISIBLE;

       if (mDualPane) {
            getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            mStartingPosition = getArguments().getInt(InitBundle.QUERY_STARTING_POSITION);
        } else {
            mStartingPosition = -1;
        }

        mAdapter = new SeasonsAdapter(getBaseActivity(), null, this, this);
        setListAdapter(mAdapter);
        //mListAdapter = new TvSeasonsSectionedListAdapter(getActivity());
        //setListAdapter(mAdapter);

        setHasOptionsMenu(true);
    }


    @Override
    public ListView createListView(Context context, LayoutInflater inflater) {
        return (ListView) inflater.inflate(R.layout.view_pinned_list, null);
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
        mPresenter.onResume();
        super.onResume();
    }

    @Override
    public void onPause() {
        mPresenter.onPause();
        super.onPause();
    }

    @Override
    protected boolean onScrolledToBottom() {
        return false;
    }

    /**
     * ShowDetailView
     */

    @Override
    public void setTvShow(ShowWrapper show) {
        //NOIMPL (Alessio) Not implemented in this fragment
    }

    @Override
    public void showTvShowImages(ShowWrapper movie) {
        //NOIMPL (Alessio) Not implemented in this fragment
    }

    @Override
    public void showTvShowCreditsDialog(MovieQueryType queryType) {
        //NOIMPL (Alessio) Not implemented in this fragment
    }

    @Override
    public void setTvSeasons(List<ListItem<SeasonWrapper>> items) {
        mAdapter.setItems(items);
        // set an initial checked item
        if (mStartingPosition != -1 ) {
            setItemChecked(mStartingPosition);
            mStartingPosition = -1;
        }
    }

    @Override
    public void showSeasonDetail(Integer seasonId, View view, int position) {
        if (mDualPane) {
            //getActivity
            setItemChecked(position);
        } else {
            Display display = getDisplay();
            if (display != null) {
                display.startTvSeasonDetailActivity(getRequestParameter(), String.valueOf(seasonId), ActivityOptionsCompat
                        .makeScaleUpAnimation(view, 0, 0, view.getWidth(), view.getHeight())
                        .toBundle());
            }
        }

    }

    @Override
    public void updateDisplayTitle(String title) {

    }

    @Override
    public void updateDisplaySubtitle(CharSequence subtitle) {
        Display display = getDisplay();
        if (display != null) {
            display.setActionBarSubtitle(subtitle);
        }
    }

    /**
     * MovieView
     */
    @Override
    public void showError(NetworkError error) {
        setListShown(true);

        switch (error) {
            case NETWORK_ERROR:
                setEmptyText(getString(R.string.empty_network_error, R.string.seasons_title));
                break;
            case UNKNOWN:
                setEmptyText(getString(R.string.empty_unknown_error, R.string.seasons_title));
                break;
        }
    }

    @Override
    public void showLoadingProgress(boolean visible) {
        if (visible) {
            setListShown(false);
        } else {
            setListShown(true);
        }
    }

    @Override
    public void showSecondaryLoadingProgress(boolean visible) {
        setSecondaryProgressShown(visible);
    }

    @Override
    public String getRequestParameter() {
        return getArguments().getString(InitBundle.QUERY_SHOW_ID);
    }

    @Override
    public boolean isModal() {
        return false;
    }

    @Override
    public MovieQueryType getQueryType() {
        return MovieQueryType.TV_SEASONS_LIST;
    }

    /*
    @Override
    public void showSeasonDetail(SeasonWrapper tvSeason, View view, int position) {
            TvSeasonsActivity activity = (TvSeasonsActivity) getActivity();
            //activity.setCurrentPage();
            setItemChecked(position);

            Display display = getDisplay();
            if (display != null) {
                //display.startTvSeasonsActivity();
            }

    }
    */


    public void getSeasonId() {
        getArguments().getInt(InitBundle.QUERY_SEASON_ID);
    }


    public void getSeasonNumber() {
        getArguments().getInt(InitBundle.QUERY_SEASON_NUMBER);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        ListItem<SeasonWrapper> item = (ListItem<SeasonWrapper>) l.getItemAtPosition(position);
        if (item.getListType() == ListItem.TYPE_ITEM) {
            SeasonWrapper season = item.getListItem();
            showSeasonDetail(season.getId(), v, position);
        }
    }

    @Override
    public void onSeasonStarred(SeasonWrapper season) {

    }

    @Override
    public void onPopupMenuClick(View v, SeasonWrapper season) {

    }

    /**
     * Highlight the given episode in the list.
     */
    public void setItemChecked(int position) {
        ListView list = getListView();
        list.setItemChecked(position, true);
        if (position <= list.getFirstVisiblePosition()
                || position >= list.getLastVisiblePosition()) {
            list.smoothScrollToPosition(position);
        }
    }
}
