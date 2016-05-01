
package com.roodie.materialmovies.views.fragments;


import android.annotation.TargetApi;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.google.common.base.Preconditions;
import com.roodie.materialmovies.R;
import com.roodie.materialmovies.mvp.presenters.DetailShowPresenter;
import com.roodie.materialmovies.mvp.views.TvShowDetailView;
import com.roodie.materialmovies.util.StringUtils;
import com.roodie.materialmovies.views.activities.SettingsActivity;
import com.roodie.materialmovies.views.custom_views.CirclePageIndicator;
import com.roodie.materialmovies.views.custom_views.GradientView;
import com.roodie.materialmovies.views.custom_views.MMoviesImageView;
import com.roodie.materialmovies.views.custom_views.MovieDetailCardLayout;
import com.roodie.materialmovies.views.custom_views.MovieDetailInfoLayout;
import com.roodie.materialmovies.views.custom_views.MovieWatchedToggler;
import com.roodie.materialmovies.views.custom_views.RatingBarLayout;
import com.roodie.materialmovies.views.custom_views.ViewRecycler;
import com.roodie.materialmovies.views.custom_views.recyclerview.DetailRecyclerLayout;
import com.roodie.materialmovies.views.fragments.base.BaseAnimationFragment;
import com.roodie.model.Display;
import com.roodie.model.entities.CreditWrapper;
import com.roodie.model.entities.Genre;
import com.roodie.model.entities.PersonWrapper;
import com.roodie.model.entities.SeasonWrapper;
import com.roodie.model.entities.ShowWrapper;
import com.roodie.model.util.MoviesCollections;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.InjectView;
import butterknife.Optional;

/**
 * Created by Roodie on 16.09.2015.
 */

public class TvShowDetailFragment extends BaseAnimationFragment<ShowWrapper, DetailRecyclerLayout> implements TvShowDetailView {

    private static final String LOG_TAG = TvShowDetailFragment.class.getSimpleName();

    private static final String KEY_SHOW_SAVE_STATE = "show_on_save_state";

    @InjectPresenter
    DetailShowPresenter mPresenter;

    private DetailAdapter mAdapter;
    private ShowWrapper mShow;
    private final ArrayList<DetailItemType> mItems = new ArrayList<>();

    private View mHeader;

    @Optional
    @InjectView(R.id.header_viewpager)
    ViewPager mHeaderViewPager;

    @Optional @InjectView(R.id.gradientview_fanart)
    GradientView mFanartGradient;

    @Optional @InjectView(R.id.header_pager_indicator)
    CirclePageIndicator mHeaderPagerIndicator;

    @Optional @InjectView(R.id.title)
    TextView mTitleTextView;

    @Optional  @InjectView(R.id.status)
    TextView mHeaderStatus;

    @Optional @InjectView(R.id.airs_on)
    TextView mHeaderAirsOn;

    @Optional @InjectView(R.id.air_date)
    TextView mHeaderDate;

    @Optional @InjectView(R.id.duration)
    TextView mHeaderDuration;

    @Optional @InjectView(R.id.header_page1)
    View mHeaderPage1;

    @Optional @InjectView(R.id.header_page2)
    View mHeaderPage2;

    @Optional @InjectView(R.id.toggler_watched)
    MovieWatchedToggler mWatchedToggler;


    @InjectView(R.id.fanart_image)
    MMoviesImageView mFanartImageView;

    @Optional @InjectView(R.id.summary)
    TextView mSummary;

    @InjectView(R.id.rating_bar)
    RatingBarLayout mRatingBarLayout;

    private MenuItem mShareItem;
    private MenuItem mWebSearchItem;
    private MenuItem mWatchedItem;
    boolean isEnableShare = false;

    private ShowCastAdapter mShowCastAdapter;
    private ShowCrewAdapter mShowCrewAdapter;
    private ShowSeasonsAdapter mShowSeasonsAdapter;

    private static final Date DATE = new Date();

    protected static final String KEY_QUERY_SHOW_ID = "show_id";

    private static final int HEADER_PAGER_SIZE = 2;

    @Override
    protected void configureEnterTransition() {
        ViewCompat.setTransitionName(mPosterImageView, getActivity().getString(R.string.transition_poster));
        Picasso.with(getActivity().getApplicationContext()).load(getImageUrl()).into(mPosterImageView);
    }

    @Override
    public void onFabClicked() {
        if (!mShow.isWatched()) {
            toast(String.format(getResources().getString(R.string.action_item_added_to_watched), mShow.getTitle()));
        }
        mPresenter.toggleShowWatched(this, mShow);

    }

    @Override
    public void onRefreshData(boolean visible) {
    }


    private void updateShowWatched(boolean isWatched) {
        if (hasFAB()) {
            if (isWatched) {
                mFloatingButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.mm_green)));
                mFloatingButton.setImageResource(R.drawable.checkmark_big_thick);
            } else {
                mFloatingButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.mm_luxor_gold)));
                mFloatingButton.setImageResource(R.drawable.mark_watched);
            }
        }

        if (mWatchedToggler != null)
            mWatchedToggler.setStatus(isWatched);

        getActivity().invalidateOptionsMenu();
    }

    private boolean hasHeaderView() {
        return mHeaderViewPager != null;
    }

    public static TvShowDetailFragment newInstance(String id, String imageUrl) {
        Preconditions.checkArgument(id != null, "showId can not be null");

        Bundle bundle = new Bundle();
        bundle.putString(KEY_QUERY_SHOW_ID, id);
        bundle.putString(KEY_IMAGE_URL, imageUrl);
        TvShowDetailFragment fragment = new TvShowDetailFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    public static TvShowDetailFragment newInstance(String id) {
        Preconditions.checkArgument(id != null, "showId can not be null");

        Bundle bundle = new Bundle();
        bundle.putString(KEY_QUERY_SHOW_ID, id);
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
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(KEY_SHOW_SAVE_STATE, mShow);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            setData((ShowWrapper) savedInstanceState.getSerializable(KEY_SHOW_SAVE_STATE));
        }
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_show_detail_list;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAppBar != null) {
            mPrimaryRecyclerView.addOnScrollListener(expandableScrollListener);
        }
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            view.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                @TargetApi(21)
                public boolean onPreDraw() {
                    view.getViewTreeObserver().removeOnPreDrawListener(this);
                    getActivity().startPostponedEnterTransition();
                    return true;
                }
            });
        }

        mHeader = view.findViewById(R.id.header);

        if (mHeader != null) {
            HeaderPagerAdapter headerAdapter = new HeaderPagerAdapter();
            this.mHeaderViewPager.setAdapter(headerAdapter);
            this.mHeaderPagerIndicator.setViewPager(this.mHeaderViewPager);
        }

        mFanartImageView.setBlurred(true);
        mPosterImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTvShowImages(mShow);
            }
        });

        if (mHeader != null)
            mRatingBarLayout.setWhiteTheme();
        if (hasLeftContainer()) {
            mWatchedToggler.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onFabClicked();
                }
            });
        }

        super.onViewCreated(view, savedInstanceState);
    }

    private class HeaderPagerAdapter extends PagerAdapter {

        public HeaderPagerAdapter() {
        }

        @Override
        public int getCount() {
            return HEADER_PAGER_SIZE;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            switch (position) {
                default:
                    return null;
                case 0:
                    return TvShowDetailFragment.this.mHeaderPage1;
                case 1:
                    return TvShowDetailFragment.this.mHeaderPage2;
            }
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }

    @Override
    protected void attachUiToPresenter() {
        mPresenter.attachUiByQuery(this, getRequestParameter(), getQueryType());
        Display display = getDisplay();
        if ( display != null) {
            display.showUpNavigation(getQueryType() != null && getQueryType().showUpNavigation());
            display.setActionBarTitle(mPresenter.getUiTitle(getRequestParameter()));
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.show_detail, menu);

        mShareItem = menu.findItem(R.id.menu_show_share);
        mShareItem.setEnabled(isEnableShare);
        mShareItem.setVisible(isEnableShare);

        mWebSearchItem = menu.findItem(R.id.menu_action_show_websearch);
        mWebSearchItem.setEnabled(isEnableShare);
        mWebSearchItem.setVisible(isEnableShare);

        mWatchedItem = menu.findItem(R.id.menu_item_watched);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {

        isEnableShare = mShow != null && !TextUtils.isEmpty(
                mShow.getTitle());
        mShareItem.setEnabled(isEnableShare);
        mShareItem.setVisible(isEnableShare);

        mWebSearchItem.setEnabled(isEnableShare);
        mWebSearchItem.setVisible(isEnableShare);
        if (mWatchedItem != null) {
            if (mShow != null) {
                if (mShow.isWatched()) {
                    mWatchedItem.setIcon(R.drawable.ic_added_to_watchlist_white_24dp);
                    mWatchedItem.setTitle(R.string.menu_item_show_is_watched);
                } else {
                    mWatchedItem.setIcon(R.drawable.ic_add_to_watched_white_24dp);
                    mWatchedItem.setTitle(R.string.menu_item_watchable_add_to_watched);
                }
            }
        }
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Display display = getDisplay();
        if (display != null) {
            switch (item.getItemId()) {
                case R.id.menu_item_watched: {
                    onFabClicked();
                    return true;
                }
                case R.id.menu_refresh: {
                    mPresenter.refresh(this, getRequestParameter());
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

    public String getRequestParameter() {
        return getArguments().getString(KEY_QUERY_SHOW_ID);
    }

    public String getImageUrl() {
        return getArguments().getString(KEY_IMAGE_URL);
    }

    @Override
    public void updateDisplayTitle(String title) {
        Display display = getDisplay();
        if (display != null) {
            display.setActionBarTitle(title);
        }
    }

    @Override
    public void updateDisplaySubtitle(CharSequence subtitle) {
        Display display = getDisplay();
        if (display != null) {
            display.setActionBarSubtitle(subtitle);
        }
    }

    @Override
    public MMoviesQueryType getQueryType() {
        return MMoviesQueryType.TV_SHOW_DETAIL;
    }


    @Override
    public void setData(ShowWrapper data) {
        mShow = data;
        mToolbarTitle = data.getTitle();
        mAdapter = populateUi();
        getRecyclerView().setAdapter(mAdapter);
        updateShowWatched(mShow.isWatched());

        mFanartImageView.setVisibility(View.VISIBLE);
    }

    @Override
    public void setSeasons(List<SeasonWrapper> data) {
        //TODO
    }

    @Override
    public void setSeasonPoster(Bitmap bitmap) {
        //TODO
    }


    @Override
    public void showSeasonDetail(SeasonWrapper season) {
        //TODO
    }

    @Override
    public void showTvShowImages(ShowWrapper movie) {
        //TODO
    }

    @Override
    public void showTvShowCreditsDialog(MMoviesQueryType queryType) {
        Preconditions.checkNotNull(queryType, "Query type cannot be null");
        ListView list = new ListView(mContext);
        String mTitle = "";
        boolean wrapInScrollView = false;

        switch (queryType) {
            case TV_SHOW_CAST:
                list.setAdapter(getShowCastAdapter());
                mTitle = getResources().getString(R.string.cast_movies);
                break;
            case TV_SHOW_CREW:
                list.setAdapter(getShowCrewAdapter());
                mTitle = getResources().getString(R.string.crew_movies);
                break;
        }
        new MaterialDialog.Builder(getActivity())
                .title(mTitle)
                .customView(list, wrapInScrollView)
                .theme(SettingsActivity.THEME == R.style.Theme_MMovies_Light ? Theme.LIGHT : Theme.DARK)
                .show();
    }

    @Override
    public void updateDisplaySubtitle(String subtitle) {

    }

    public void showSeasonDetail(int seasonId, View view, int position) {
       //TODO
    }

    @Override
    public void showPersonDetail(PersonWrapper person, View view) {
        Preconditions.checkNotNull(person, "person cannot be null");

        Display display = getDisplay();
        if (display != null) {
            display.startPersonDetailActivity(String.valueOf(person.getTmdbId()), null);
        }
    }

    protected DetailAdapter createRecyclerAdapter(List<DetailItemType> items) {
        return new DetailAdapter(items);
    }

    private DetailAdapter populateUi() {
        if (mShow == null) {
            return null;
        }
        mItems.clear();

        if (mShow.hasBackdropUrl()) {
            if (mFanartImageView != null) {
                mFanartImageView.loadBackdrop(mShow);
            }
        }

        if (mCollapsingToolbarLayout != null) {
            mCollapsingToolbarLayout.setTitle(mShow.getTitle());
        }

        if (mTitleTextView != null) {
            mTitleTextView.setText(mShow.getTitle());
            mTitleTextView.setVisibility(View.VISIBLE);
        }

        if (hasHeaderView()) {
            mHeaderStatus.setText(getString(StringUtils.getShowStatusStringId(mShow.getStatus())));
            mHeaderStatus.setVisibility(View.VISIBLE);

            if (mShow.getNetworks() != null) {
                mHeaderAirsOn.setVisibility(View.VISIBLE);
                mHeaderAirsOn.setText(mShow.getNetworks());
            }

            if (mShow.getRuntime() > 0) {
                mHeaderDuration.setText(getString(R.string.movie_details_runtime_content, mShow.getRuntime()));
            }

            if (mShow.getReleasedTime() > 0) {
                DATE.setTime(mShow.getReleasedTime());
                DateFormat dateFormat = DateFormat.getDateInstance();
                mHeaderDate.setText(dateFormat.format(DATE));
            }
        }

        mPosterImageView.loadPoster(mShow);

        mRatingBarLayout.setRatingVotes(mShow.getRatingVotes());
        mRatingBarLayout.setRatingValue(mShow.getRatingVoteAverage());
        mRatingBarLayout.setRatingRange(10);

        if (hasFAB()) {
            if (mShow.isWatched()) {
                mFloatingButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.mm_green)));
                mFloatingButton.setImageResource(R.drawable.checkmark_big_thick);

            } else {
                mFloatingButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.mm_luxor_gold)));
                mFloatingButton.setImageResource(R.drawable.mark_watched);

            }
        }


        if (!MoviesCollections.isEmpty(mShow.getGenres())) {
            mItems.add(DetailItemType.GENRES);
        }

        if (hasLeftContainer()) {
            mSummary.setText(mShow.getOverview());
        } else {
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
        GENRES,
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
                    case GENRES:
                        mItems.add(new ShowGenresBinder(this));
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
     * ShowSummaryBinder
     */

    public class ShowGenresBinder extends BaseViewHolder<ShowGenresBinder.ViewHolder> {

        public ShowGenresBinder(BaseDetailAdapter dataBindAdapter) {
            super(dataBindAdapter);
        }

        @Override
        public int getItemCount() {
            return 1;
        }

        @Override
        public void bindViewHolder(ViewHolder holder, int position) {
            final ViewRecycler viewRecycler = new ViewRecycler(holder.container);
            viewRecycler.recycleViews();

            if (!mShow.getGenres().isEmpty()) {
                for (Genre genre : mShow.getGenres()) {
                    TextView view = new TextView(getBaseActivity());
                    view.setText(getResources().getText(genre.getResId()));
                    view.setTextColor(getResources().getColor(R.color.primary_color));
                    view.setGravity(Gravity.CENTER);
                    int padding  = getResources().getDimensionPixelSize(R.dimen.spacing_minor);
                    view.setPadding(padding, padding, padding, padding);
                    view.setCompoundDrawablesWithIntrinsicBounds(genre.getImageResId(), 0, 0, 0);
                    view.setCompoundDrawablePadding(padding);
                    holder.container.addView(view);
                }
            }
            viewRecycler.clearRecycledViews();
        }

        @Override
        public ViewHolder newViewHolder(ViewGroup parent) {
            View view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.item_horiz_scroll_view, parent, false);
            return new ViewHolder(view);

        }

        class ViewHolder extends RecyclerView.ViewHolder {

            ViewGroup container;

            public ViewHolder(View view) {
                super(view);
                container = (ViewGroup) view.findViewById(R.id.content);

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
            holder.summary.setText(mShow.getOverview());
        }

        @Override
        public ViewHolder newViewHolder(ViewGroup parent) {
            View view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.item_show_detail_summary, parent, false);
            return new ViewHolder(view);
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            View container;
            TextView summary;

            public ViewHolder(View view) {
                super(view);

                container = view.findViewById(R.id.movie_detail_card_details);
                summary = (TextView) view.findViewById(R.id.textview_summary);
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
            if (mShow.getRuntime() > 0) {
                holder.runtimeLayout.setContentText(
                        getString(R.string.movie_details_runtime_content, mShow.getRuntime()));
                holder.runtimeLayout.setVisibility(View.VISIBLE);
            } else {
                holder.runtimeLayout.setVisibility(View.GONE);
            }

            if (!TextUtils.isEmpty(mShow.getContentRating())) {
                holder.contentRatingLayout.setContentText(mShow.getContentRating());
                holder.contentRatingLayout.setVisibility(View.VISIBLE);
            } else {
                holder.contentRatingLayout.setVisibility(View.GONE);
            }

            if (mShow.getReleasedTime() > 0) {
                DATE.setTime(mShow.getReleasedTime());
                DateFormat dateFormat = DateFormat.getDateInstance();
                holder.lastAirInfoLayout.setContentText( dateFormat.format(DATE));
                holder.lastAirInfoLayout.setVisibility(View.VISIBLE);

            } else {
                holder.lastAirInfoLayout.setVisibility(View.GONE);
            }

            if (!TextUtils.isEmpty(mShow.getOriginalLanguage())) {
                holder.languageLayout.setContentText(mShow.getOriginalLanguage());
                holder.languageLayout.setVisibility(View.VISIBLE);
            } else {
                holder.languageLayout.setVisibility(View.GONE);
            }

        }

        @Override
        public ViewHolder newViewHolder(ViewGroup parent) {
            View view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.item_show_detail_details, parent, false);
            return new ViewHolder(view);
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            MovieDetailCardLayout container;
            MovieDetailInfoLayout runtimeLayout;
            MovieDetailInfoLayout contentRatingLayout;
            MovieDetailInfoLayout genreLayout;
            MovieDetailInfoLayout lastAirInfoLayout;
            MovieDetailInfoLayout languageLayout;

            public ViewHolder(View view) {
                super(view);

                container = (MovieDetailCardLayout) view.findViewById(R.id.movie_detail_card_details);
                runtimeLayout = (MovieDetailInfoLayout) view.findViewById(R.id.layout_info_runtime);
                contentRatingLayout = (MovieDetailInfoLayout) view.findViewById(R.id.layout_info_certification);
                genreLayout = (MovieDetailInfoLayout) view.findViewById(R.id.layout_info_genres);
                lastAirInfoLayout = (MovieDetailInfoLayout) view.findViewById(R.id.layout_info_last_air);
                languageLayout = (MovieDetailInfoLayout) view.findViewById(R.id.layout_info_language);
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

        MovieDetailCardLayout cardLayout;

        public ShowCastBinder(BaseDetailAdapter dataBindAdapter) {
            super(dataBindAdapter);
        }

        @Override
        public int getItemCount() {
            return 1;
        }

        @Override
        public void bindViewHolder(ViewHolder holder, int position) {

            cardLayout.setTitle(R.string.cast_movies);

            final View.OnClickListener seeMoreClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showTvShowCreditsDialog(MMoviesQueryType.TV_SHOW_CAST);
                }
            };

            final ViewRecycler viewRecycler = new ViewRecycler(holder.layout);
            viewRecycler.recycleViews();

            if (!getShowCastAdapter().isEmpty()) {
                final int numItems = getResources().getInteger(R.integer.detail_card_max_items);
                final int adapterCount = getShowCastAdapter().getCount();

                for (int i = 0; i < Math.min(numItems, adapterCount); i++) {
                    View view = getShowCastAdapter().getView(i, viewRecycler.getRecycledView(), holder.layout);
                    holder.layout.addView(view);
                }
                final boolean showSeeMore = numItems < getShowCastAdapter().getCount();
                cardLayout.setSeeMoreVisibility(showSeeMore);
                cardLayout.setSeeMoreOnClickListener(showSeeMore ? seeMoreClickListener : null);

            }
            viewRecycler.clearRecycledViews();
        }

        @Override
        public ViewHolder newViewHolder(ViewGroup parent) {
            View view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.item_movie_detail_generic_card, parent, false);
            cardLayout = (MovieDetailCardLayout) view;

            return new ViewHolder(view);
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            ViewGroup layout;

            public ViewHolder(View view) {
                super(view);
                layout = (ViewGroup) view.findViewById(R.id.card_content);
            }
        }
    }


    /**
     * ShowCrewBinder
     */
    public class ShowCrewBinder extends BaseViewHolder<ShowCrewBinder.ViewHolder> {

        MovieDetailCardLayout cardLayout;

        public ShowCrewBinder(BaseDetailAdapter dataBindAdapter) {
            super(dataBindAdapter);
        }

        @Override
        public int getItemCount() {
            return 1;
        }

        @Override
        public void bindViewHolder(ViewHolder holder, int position) {

            cardLayout.setTitle(R.string.crew_movies);

            final View.OnClickListener seeMoreClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showTvShowCreditsDialog(MMoviesQueryType.TV_SHOW_CREW);
                }
            };

            final ViewRecycler viewRecycler = new ViewRecycler(holder.layout);
            viewRecycler.recycleViews();

            if (!getShowCrewAdapter().isEmpty()) {
                final int numItems = getResources().getInteger(R.integer.detail_card_max_items);
                final int adapterCount = getShowCrewAdapter().getCount();

                for (int i = 0; i < Math.min(numItems, adapterCount); i++) {
                    View view = getShowCrewAdapter().getView(i, viewRecycler.getRecycledView(), holder.layout);
                    holder.layout.addView(view);
                }
                final boolean showSeeMore = numItems < getShowCrewAdapter().getCount();
                cardLayout.setSeeMoreVisibility(showSeeMore);
                cardLayout.setSeeMoreOnClickListener(showSeeMore ? seeMoreClickListener : null);

            }
            viewRecycler.clearRecycledViews();
        }

        @Override
        public ViewHolder newViewHolder(ViewGroup parent) {
            View view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.item_movie_detail_generic_card, parent, false);
            cardLayout = (MovieDetailCardLayout) view;

            return new ViewHolder(view);
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            ViewGroup layout;

            public ViewHolder(View view) {
                super(view);
                layout = (ViewGroup) view.findViewById(R.id.card_content);
            }
        }
    }

    /**
     * ShowSeasonsBinder
     */
    public class ShowSeasonsBinder extends BaseViewHolder<ShowSeasonsBinder.ViewHolder> {

        MovieDetailCardLayout cardLayout;

        public ShowSeasonsBinder(BaseDetailAdapter dataBindAdapter) {
            super(dataBindAdapter);
        }

        @Override
        public int getItemCount() {
            return 1;
        }

        @Override
        public void bindViewHolder(ViewHolder holder, int position) {
            cardLayout.setTitle(R.string.show_seasons);

            final View.OnClickListener seeMoreClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showSeasonDetail(0, v, 0);
                }
            };

            final ViewRecycler viewRecycler = new ViewRecycler(holder.layout);
            viewRecycler.recycleViews();

            if (!getShowSeasonsAdapter().isEmpty()) {
                final int numItems = getResources().getInteger(R.integer.detail_card_max_items);
                final int adapterCount = getShowSeasonsAdapter().getCount();

                for (int i = 0; i < Math.min(numItems, adapterCount); i++) {
                    View view = getShowSeasonsAdapter().getView(i, viewRecycler.getRecycledView(), holder.layout);
                    holder.layout.addView(view);
                }
                final boolean showSeeMore = numItems < getShowSeasonsAdapter().getCount();
                cardLayout.setSeeMoreVisibility(showSeeMore);
                cardLayout.setSeeMoreOnClickListener(showSeeMore ? seeMoreClickListener : null);

            }
            viewRecycler.clearRecycledViews();
        }

        @Override
        public ViewHolder newViewHolder(ViewGroup parent) {
            View view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.item_show_detail_seasons, parent, false);
            cardLayout = (MovieDetailCardLayout) view;

            return new ViewHolder(view);
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            ViewGroup layout;

            public ViewHolder(View view) {
                super(view);
                layout = (ViewGroup) view.findViewById(R.id.card_content);
            }
        }
    }

    /**
     * BaseMovieCastAdapter
     */
    private abstract class BaseMovieCastAdapter extends BaseAdapter {
        private final LayoutInflater mInflater;
        private final View.OnClickListener mItemOnClickListener;

        public BaseMovieCastAdapter(LayoutInflater mInflater, View.OnClickListener mItemOnClickListener) {
            this.mInflater = mInflater;
            this.mItemOnClickListener = mItemOnClickListener;
        }


        @Override
        public abstract CreditWrapper getItem(int position) ;

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = mInflater.inflate(getLayoutId(), parent, false);
            }

            CreditWrapper credit = getItem(position);

            final TextView title = (TextView) convertView.findViewById(R.id.title);
            title.setText(credit.getPerson().getName());

            final MMoviesImageView image = (MMoviesImageView) convertView.findViewById(R.id.imageview_poster);
            image.setAvatarMode(true);
            image.loadProfile(credit.getPerson());


            final TextView subTitle = (TextView) convertView.findViewById(R.id.textview_subtitle_1);
            if (subTitle != null) {
                if (!TextUtils.isEmpty(credit.getJob())) {
                    subTitle.setText(credit.getJob());
                    subTitle.setVisibility(View.VISIBLE);
                } else {
                    subTitle.setVisibility(View.GONE);
                }
            }

            convertView.setOnClickListener(mItemOnClickListener);
            convertView.setTag(credit);

            return convertView;
        }

        protected int getLayoutId() {
            return R.layout.item_movie_detail_list_2line;
        }
    }

    /**
     * ShowCastAdapter
     */
    private class ShowCastAdapter extends BaseMovieCastAdapter {

        public ShowCastAdapter(LayoutInflater mInflater) {
            super(mInflater, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CreditWrapper cast = (CreditWrapper) v.getTag();
                    if (cast != null) {
                        showPersonDetail(cast.getPerson(), v);

                    }
                }
            });
        }

        @Override
        public int getCount() {
            return mShow != null ? MoviesCollections.size(mShow.getCast()) : 0;
        }

        @Override
        public CreditWrapper getItem(int position) {
            return mShow.getCast().get(position);
        }
    }

    /**
     * ShowCrewAdapter
     */
    private class ShowCrewAdapter extends BaseMovieCastAdapter {

        public ShowCrewAdapter(LayoutInflater mInflater) {
            super(mInflater, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CreditWrapper cast = (CreditWrapper) v.getTag();
                    if (cast != null) {
                       showPersonDetail(cast.getPerson(), v);
                    }
                }
            });
        }

        @Override
        public int getCount() {
            return mShow != null ? MoviesCollections.size(mShow.getCrew()) : 0;
        }

        @Override
        public CreditWrapper getItem(int position) {
            return mShow.getCrew().get(position);
        }
    }

    /**
     * ShowSeasonsAdapter
     */
    private class ShowSeasonsAdapter extends BaseAdapter {

        private final LayoutInflater mInflater;


        ShowSeasonsAdapter(LayoutInflater inflater) {
            mInflater = inflater;
        }

        @Override
        public int getCount() {
            return mShow != null ? MoviesCollections.size(mShow.getSeasons()) : 0;
        }

        @Override
        public SeasonWrapper getItem(int position) {
            Log.d(LOG_TAG, mShow.getSeasons().get(position).toString());
            return mShow.getSeason(position);
        }

        protected int getLayoutId() {
            return R.layout.item_show_season;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = mInflater.inflate(getLayoutId(), parent, false);
            }

            final SeasonWrapper season = getItem(position);

            final MMoviesImageView imageView = (MMoviesImageView)
                    convertView.findViewById(R.id.imageview_poster);
            imageView.loadPoster(season);

            final TextView title = (TextView) convertView.findViewById(R.id.title);
            title.setText(StringUtils.getSeasonString(mContext, season.getSeasonNumber()));


            if (season.getReleasedTime() > 0) {
                DateFormat seasonReleaseDate = DateFormat.getDateInstance(DateFormat.MEDIUM);
                Date mDate = new Date();
                mDate.setTime(season.getReleasedTime());
                final TextView subtitle = (TextView) convertView.findViewById(R.id.subtitle);
                subtitle.setText(getString(R.string.season_air_date, seasonReleaseDate.format(mDate)));
            }

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showSeasonDetail(season.getTmdbId(), v, position);
                }
            });
            convertView.setTag(season);

            return convertView;
        }
    }



    private ShowCastAdapter getShowCastAdapter() {
        if (mShowCastAdapter == null) {
            mShowCastAdapter = new ShowCastAdapter(LayoutInflater.from(getActivity()));
        }
        return mShowCastAdapter;
    }

    private ShowCrewAdapter getShowCrewAdapter() {
        if (mShowCrewAdapter == null) {
            mShowCrewAdapter = new ShowCrewAdapter(LayoutInflater.from(getActivity()));
        }
        return mShowCrewAdapter;
    }

    private ShowSeasonsAdapter getShowSeasonsAdapter() {
        if (mShowSeasonsAdapter == null) {
            mShowSeasonsAdapter = new ShowSeasonsAdapter(LayoutInflater.from(getActivity()));
        }
        return mShowSeasonsAdapter;
    }


    @Override
    protected void setSupportActionBar(Toolbar toolbar) {
        setSupportActionBar(toolbar, false);
    }

}

