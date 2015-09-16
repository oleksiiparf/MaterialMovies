package com.roodie.materialmovies.views.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.common.base.Preconditions;
import com.roodie.materialmovies.R;
import com.roodie.materialmovies.mvp.presenters.ShowDetailPresenter;
import com.roodie.materialmovies.views.MMoviesApplication;
import com.roodie.materialmovies.views.custom_views.MMoviesImageView;
import com.roodie.materialmovies.views.custom_views.RatingBarLayout;
import com.roodie.materialmovies.views.fragments.base.BaseAnimationFragment;
import com.roodie.model.Display;
import com.roodie.model.entities.ShowWrapper;
import com.roodie.model.network.NetworkError;
import com.roodie.model.util.MoviesCollections;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Roodie on 16.09.2015.
 */
public class TvShowDetailFragment extends BaseAnimationFragment implements ShowDetailPresenter.ShowDetailView {

    private static final String LOG_TAG = TvShowDetailFragment.class.getSimpleName();

    private static final String KEY_SHOW_SAVE_STATE = "show_on_save_state";

    private ShowDetailPresenter mPresenter;
    private DetailAdapter mAdapter;
    private ShowWrapper mShow;
    private final ArrayList<DetailItemType> mItems = new ArrayList<>();

    private RelativeLayout mSummaryContainer;

    private CollapsingToolbarLayout mCollapsingToolbar;
    private MMoviesImageView mFanartImageView;
    private TextView mTitleTextView;
    private TextView mSummary;
    private MMoviesImageView mPosterImageView;
    private RatingBarLayout mRatingBarLayout;
    private Context mContext;

    private MenuItem mShareItem;
    private MenuItem mWebSearchItem;
    boolean isEnableShare = false;

    private static final Date DATE = new Date();

    protected static final String QUERY_SHOW_ID = "show_id";

    public static TvShowDetailFragment newInstance(String id) {
        Preconditions.checkArgument(id != null, "showId can not be null");

        Bundle bundle = new Bundle();
        bundle.putString(QUERY_SHOW_ID, id);
        TvShowDetailFragment fragment = new TvShowDetailFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity.getApplicationContext();
        mPresenter = MMoviesApplication.from(activity.getApplicationContext()).getDetailShowPresenter();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(KEY_SHOW_SAVE_STATE, mShow);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            setTvShow((ShowWrapper) savedInstanceState.getSerializable(KEY_SHOW_SAVE_STATE));
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FrameLayout wrapper = new FrameLayout(getActivity());
        inflater.inflate(R.layout.fragment_show_detail_list, wrapper, true);
        return wrapper;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        //set actionbar up navigation
        final Display display = getDisplay();
        if (!isModal()) {
            display.showUpNavigation(getQueryType() != null && getQueryType().showUpNavigation());
        }

        mCollapsingToolbar = (CollapsingToolbarLayout) view.findViewById(R.id.backdrop_toolbar);

        mSummaryContainer = (RelativeLayout) view.findViewById(R.id.container_layout);
        mFanartImageView = (MMoviesImageView) view.findViewById(R.id.imageview_fanart);
        mTitleTextView = (TextView) view.findViewById(R.id.textview_title);
        mSummary = (TextView) view.findViewById(R.id.textview_summary);
        mPosterImageView = (MMoviesImageView) view.findViewById(R.id.imageview_poster);
        if (mPosterImageView != null) {

            // check if jelly bean or higher
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                mFanartImageView.setImageAlpha(150);
            } else {
                mFanartImageView.setAlpha(150);
            }
            mPosterImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showTvShowImages(mShow);
                }
            });
        } else {

            mFanartImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showTvShowImages(mShow);
                }
            });
        }

        mRatingBarLayout = (RatingBarLayout) view.findViewById(R.id.rating_bar);

        mPresenter.attachView(this);
        super.onViewCreated(view, savedInstanceState);
        //mPresenter.initialize();
    }

    @Override
    protected void setUpVisibility() {
        if (mSummaryContainer != null) {
            mSummaryContainer.setVisibility(View.GONE);
        }
        mFanartImageView.setVisibility(View.GONE);
    }

    @Override
    protected void configureEnterTransition() {
        //TODO
    }

    @Override
    protected void configureEnterAnimation() {
        super.configureEnterAnimation();
        //TODO
    }

    @Override
    protected void initializePresenter() {
        mPresenter.initialize();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.show_detail, menu);

        mShareItem = menu.findItem(R.id.menu_movie_share);
        mShareItem.setEnabled(isEnableShare);
        mShareItem.setVisible(isEnableShare);

        mWebSearchItem = menu.findItem(R.id.menu_action_movie_websearch);
        mWebSearchItem.setEnabled(isEnableShare);
        mWebSearchItem.setVisible(isEnableShare);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {

        isEnableShare = mShow != null && !TextUtils.isEmpty(
                mShow.getTitle());
        mShareItem.setEnabled(isEnableShare);
        mShareItem.setVisible(isEnableShare);

        mWebSearchItem.setEnabled(isEnableShare);
        mWebSearchItem.setVisible(isEnableShare);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Display display = getDisplay();
        if (display != null) {
            switch (item.getItemId()) {
                case R.id.menu_refresh: {
                    mPresenter.refresh();
                    return true;
                }
                case R.id.menu_show_share: {
                    if (mShow.getTmdbId() != null) {
                        display.shareTvShow(mShow.getTmdbId(), mShow.getTitle());
                    }
                }
                return true;
                case R.id.menu_open_tmdb: {
                    if (mShow.getTmdbId() != null) {
                        display.openTmdbTvShow(mShow);
                    }
                }
                return true;
                case R.id.menu_action_show_websearch: {
                    if (mShow.getTitle() != null) {
                        display.performWebSearch(mShow.getTitle());
                    }
                }
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public ShowDetailPresenter getPresenter() {
        return mPresenter;
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
        //TODO
    }

    @Override
    public void showSecondaryLoadingProgress(boolean visible) {
        //TODO
    }

    @Override
    public String getRequestParameter() {
        return null;
    }

    @Override
    public void updateDisplayTitle(String title) {
        Display display = getDisplay();
        if (display != null) {
            display.setActionBarTitle(title);
        }
    }

    /**
     * UiView
     */
    @Override
    public MovieQueryType getQueryType() {
        return MovieQueryType.SHOW_DETAIL;
    }

    @Override
    public boolean isModal() {
        return false;
    }

    /**
     * ShowDetailView
     */
    @Override
    public void setTvShow(ShowWrapper show) {
        mShow = show;
        mAdapter = populateUi();
        getRecyclerView().setAdapter(mAdapter);
        getActivity().invalidateOptionsMenu();

        getRecyclerView().setVisibility(View.VISIBLE);
        mFanartImageView.setVisibility(View.VISIBLE);

        if (mSummaryContainer != null) {
            mSummaryContainer.setVisibility(View.VISIBLE);
        }


    }

    @Override
    public void showTvShowImages(ShowWrapper movie) {
        //TODO
    }

    @Override
    public void showTvShowCreditsDialog(MovieQueryType queryType) {
        //TODO
    }

    protected DetailAdapter createRecyclerAdapter(List<DetailItemType> items) {
        return new DetailAdapter(items);
    }

    protected DetailAdapter getRecyclerAdapter() {
        return mAdapter;
    }

    private DetailAdapter populateUi() {
        if (mShow == null) {
            return null;
        }
        mItems.clear();

        if (mShow.hasBackdrodUrl()) {
            mFanartImageView.loadBackdrop(mShow);
        }

        if (mCollapsingToolbar != null) {
            mCollapsingToolbar.setTitle(mShow.getTitle());
        }

        if (hasLeftContainer()) {

            mTitleTextView.setText(mShow.getTitle());

            mSummary.setText(mShow.getOverview());

            mPosterImageView.loadPoster(mShow);

            mRatingBarLayout.setRatingVotes(mShow.getRatingVotes());
            mRatingBarLayout.setRatingValue(mShow.getRatingVoteAverage());
            mRatingBarLayout.setRatingRange(10);


        } else {

            mItems.add(DetailItemType.TITLE);

            if (!TextUtils.isEmpty(mShow.getOverview())) {
                mItems.add(DetailItemType.SUMMARY);
            }
        }

        mItems.add(DetailItemType.DETAILS);


        if (!MoviesCollections.isEmpty(mShow.getCast())) {
            mItems.add(DetailItemType.CAST);
        }

        if (!MoviesCollections.isEmpty(mShow.getCrew())) {
            mItems.add(DetailItemType.CREW);
        }

        if (!MoviesCollections.isEmpty(mShow.getSeasons())) {
            mItems.add(DetailItemType.SEASONS);
        }


        return createRecyclerAdapter(mItems);

    }

    enum DetailItemType {
        TITLE,
        SUMMARY,
        DETAILS,
        TRAILERS,
        SEASONS,
        CAST,
        CREW

    }

    private class DetailAdapter extends EnumListDetailAdapter<DetailItemType> {
        List<BaseViewHolder> mItems;

        public DetailAdapter() {
        }

        public DetailAdapter(List<DetailItemType> items) {
            mItems = new ArrayList<>(items.size());
            for (DetailItemType item : items) {
                switch (item) {
                    case TITLE:
                        mItems.add(new ShowTitleBinder(this));
                        break;
                    case SUMMARY:
                        mItems.add(new ShowSummaryBinder(this));
                        break;
                    case DETAILS:
                        mItems.add(new ShowDetailsBinder(this));
                        break;
                    case TRAILERS:
                        mItems.add(new ShowTrailersBinder(this));
                        break;
                    case CAST:
                        mItems.add(new ShowCastBinder(this));
                        break;
                    case CREW:
                        mItems.add(new ShowCrewBinder(this));
                        break;
                    case SEASONS:
                        mItems.add(new ShowSeasonsBinder(this));
                }
            }
            addAllBinder(mItems);
        }

    }


    /**
     * ShowTitleBinder
     */
    public class ShowTitleBinder extends BaseViewHolder<ShowTitleBinder.ViewHolder> {

        public ShowTitleBinder(BaseDetailAdapter dataBindAdapter) {
            super(dataBindAdapter);
        }

        @Override
        public int getItemCount() {
            return 1;
        }

        @Override
        public void bindViewHolder(ViewHolder holder, int position) {
        //TODO
        }

        @Override
        public ViewHolder newViewHolder(ViewGroup parent) {
            //TODO
            return null;
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            public ViewHolder(View view) {
                super(view);
                //TODO

            }
        }
    }

    /**
     * ShowSummaryBinder
     */
    public class ShowSummaryBinder extends BaseViewHolder<ShowSummaryBinder.ViewHolder> {

        public ShowSummaryBinder(BaseDetailAdapter dataBindAdapter) {
            super(dataBindAdapter);
        }

        @Override
        public int getItemCount() {
            return 1;
        }

        @Override
        public void bindViewHolder(ViewHolder holder, int position) {

        }

        @Override
        public ViewHolder newViewHolder(ViewGroup parent) {
            return null;
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            public ViewHolder(View view) {
                super(view);

            }
        }
    }

    /**
     * ShowDetailsBinder
     */
    public class ShowDetailsBinder extends BaseViewHolder<ShowDetailsBinder.ViewHolder> {

        public ShowDetailsBinder(BaseDetailAdapter dataBindAdapter) {
            super(dataBindAdapter);
        }

        @Override
        public int getItemCount() {
            return 1;
        }

        @Override
        public void bindViewHolder(ViewHolder holder, int position) {

        }

        @Override
        public ViewHolder newViewHolder(ViewGroup parent) {
            return null;
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            public ViewHolder(View view) {
                super(view);

            }
        }
    }

    /**
     * ShowTrailersBinder
     */
    public class ShowTrailersBinder extends BaseViewHolder<ShowTrailersBinder.ViewHolder> {

        public ShowTrailersBinder(BaseDetailAdapter dataBindAdapter) {
            super(dataBindAdapter);
        }

        @Override
        public int getItemCount() {
            return 1;
        }

        @Override
        public void bindViewHolder(ViewHolder holder, int position) {

        }

        @Override
        public ViewHolder newViewHolder(ViewGroup parent) {
            return null;
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            public ViewHolder(View view) {
                super(view);

            }
        }
    }

    /**
     * ShowCastBinder
     */
    public class ShowCastBinder extends BaseViewHolder<ShowCastBinder.ViewHolder> {

        public ShowCastBinder(BaseDetailAdapter dataBindAdapter) {
            super(dataBindAdapter);
        }

        @Override
        public int getItemCount() {
            return 1;
        }

        @Override
        public void bindViewHolder(ViewHolder holder, int position) {

        }

        @Override
        public ViewHolder newViewHolder(ViewGroup parent) {
            return null;
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            public ViewHolder(View view) {
                super(view);

            }
        }
    }

    /**
     * ShowCrewBinder
     */
    public class ShowCrewBinder extends BaseViewHolder<ShowCrewBinder.ViewHolder> {

        public ShowCrewBinder(BaseDetailAdapter dataBindAdapter) {
            super(dataBindAdapter);
        }

        @Override
        public int getItemCount() {
            return 1;
        }

        @Override
        public void bindViewHolder(ViewHolder holder, int position) {

        }

        @Override
        public ViewHolder newViewHolder(ViewGroup parent) {
            return null;
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            public ViewHolder(View view) {
                super(view);

            }
        }
    }

    /**
     * ShowSeasonsBinder
     */
    public class ShowSeasonsBinder extends BaseViewHolder<ShowSeasonsBinder.ViewHolder> {

        public ShowSeasonsBinder(BaseDetailAdapter dataBindAdapter) {
            super(dataBindAdapter);
        }

        @Override
        public int getItemCount() {
            return 1;
        }

        @Override
        public void bindViewHolder(ViewHolder holder, int position) {

        }

        @Override
        public ViewHolder newViewHolder(ViewGroup parent) {
            return null;
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            public ViewHolder(View view) {
                super(view);

            }
        }
    }

}
