package com.roodie.materialmovies.views.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.common.base.Preconditions;
import com.roodie.materialmovies.R;
import com.roodie.materialmovies.mvp.presenters.ShowDetailPresenter;
import com.roodie.materialmovies.util.StringUtils;
import com.roodie.materialmovies.views.MMoviesApplication;
import com.roodie.materialmovies.views.custom_views.MMoviesImageView;
import com.roodie.materialmovies.views.custom_views.MovieDetailCardLayout;
import com.roodie.materialmovies.views.custom_views.MovieDetailInfoLayout;
import com.roodie.materialmovies.views.custom_views.RatingBarLayout;
import com.roodie.materialmovies.views.custom_views.ViewRecycler;
import com.roodie.materialmovies.views.fragments.base.BaseAnimationFragment;
import com.roodie.model.Display;
import com.roodie.model.entities.CreditWrapper;
import com.roodie.model.entities.ListItem;
import com.roodie.model.entities.SeasonWrapper;
import com.roodie.model.entities.ShowWrapper;
import com.roodie.model.network.NetworkError;
import com.roodie.model.util.MoviesCollections;

import java.text.DateFormat;
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

    private View mHeaderContainer;
    private View mHeader;

    private CollapsingToolbarLayout mCollapsingToolbar;
    private MMoviesImageView mFanartImageView;
    private MMoviesImageView mPosterImageView;
    private TextView mTitleTextView;
    private TextView mSummary;
    private TextView mStatus;
    private TextView mAirsOn;
    private RatingBarLayout mRatingBarLayout;
    private Context mContext;

    private MenuItem mShareItem;
    private MenuItem mWebSearchItem;
    boolean isEnableShare = false;

    private ShowCastAdapter mShowCastAdapter;
    private ShowCrewAdapter mShowCrewAdapter;
    private ShowSeasonsAdapter mShowSeasonsAdapter;

    private static final Date DATE = new Date();

    protected static final String KEY_QUERY_SHOW_ID = "show_id";

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

        mHeaderContainer = view.findViewById(R.id.container_layout);
        mHeader = view.findViewById(R.id.header);
        mStatus = (TextView) view.findViewById(R.id.textview_status);
        mAirsOn = (TextView) view.findViewById(R.id.textview_airs_on);
        mPosterImageView = (MMoviesImageView) view.findViewById(R.id.imageview_poster);

        mFanartImageView = (MMoviesImageView) view.findViewById(R.id.imageview_fanart);
        mTitleTextView = (TextView) view.findViewById(R.id.textview_title);
        mSummary = (TextView) view.findViewById(R.id.textview_summary);
        mRatingBarLayout = (RatingBarLayout) view.findViewById(R.id.rating_bar);

        if (mHeader != null) {

            mFanartImageView.setBlurred(true);
            mRatingBarLayout.setWhiteTheme();
        } else {

            // check if jelly bean or higher
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                mFanartImageView.setImageAlpha(150);
            } else {
                mFanartImageView.setAlpha(150);
            }
        }

        mPosterImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTvShowImages(mShow);
            }
        });

        mPresenter.attachView(this);
        super.onViewCreated(view, savedInstanceState);
        //mPresenter.initialize();
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
    protected void setUpVisibility() {
        if (mHeaderContainer != null) {
            mHeaderContainer.setVisibility(View.GONE);
        }
        mFanartImageView.setVisibility(View.GONE);
    }

    @Override
    protected void configureEnterTransition() {
        //TODO
        initializePresenter();
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

        mShareItem = menu.findItem(R.id.menu_show_share);
        mShareItem.setEnabled(isEnableShare);
        mShareItem.setVisible(isEnableShare);

        mWebSearchItem = menu.findItem(R.id.menu_action_show_websearch);
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
        return getArguments().getString(KEY_QUERY_SHOW_ID);
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

    /**
     * UiView
     */
    @Override
    public MovieQueryType getQueryType() {
        return MovieQueryType.TV_SHOW_DETAIL;
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
        Log.d(LOG_TAG, mShow.toString());
        mAdapter = populateUi();
        getRecyclerView().setAdapter(mAdapter);
        getActivity().invalidateOptionsMenu();

        getRecyclerView().setVisibility(View.VISIBLE);
        mFanartImageView.setVisibility(View.VISIBLE);

        if (mHeaderContainer != null) {
            mHeaderContainer.setVisibility(View.VISIBLE);
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

    @Override
    public void setTvSeasons(List<ListItem<SeasonWrapper>> items) {
        //NOIMPL (Alessio) Not implemented in this fragment
    }

    @Override
    public void showSeasonDetail(Integer seasonId, View view, int position) {
        Display display = getDisplay();
        if (display != null) {
            display.startTvSeasonsActivity(getRequestParameter(), String.valueOf(seasonId), ActivityOptionsCompat
                    .makeScaleUpAnimation(view, 0, 0, view.getWidth(), view.getHeight())
                    .toBundle());
        }
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

        mStatus.setText(getString(StringUtils.getShowStatusStringId(mShow.getStatus())));
        mAirsOn.setText(mShow.getNetworks());
        mPosterImageView.loadPoster(mShow);

        mRatingBarLayout.setRatingVotes(mShow.getRatingVotes());
        mRatingBarLayout.setRatingValue(mShow.getRatingVoteAverage());
        mRatingBarLayout.setRatingRange(10);

        if (hasLeftContainer()) {

            mTitleTextView.setText(mShow.getTitle());

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

            if (!TextUtils.isEmpty(mShow.getGenres())) {
                holder.genreLayout.setContentText(mShow.getGenres());
                holder.genreLayout.setVisibility(View.VISIBLE);
            } else {
                holder.genreLayout.setVisibility(View.GONE);
            }

            if (mShow.getLastAirTime() > 0) {
                DATE.setTime(mShow.getLastAirTime());
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
                    showTvShowCreditsDialog(MovieQueryType.TV_SHOW_CAST);
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
                    showTvShowCreditsDialog(MovieQueryType.TV_SHOW_CREW);
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
                    showSeasonDetail(null, v, 0);
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

            final MMoviesImageView image = (MMoviesImageView) convertView.findViewById(R.id.poster);
            image.setAvatarMode(true);
            //Load with Picasso
            image.loadProfile(credit.getPerson());


            final TextView subTitle = (TextView) convertView.findViewById(R.id.subtitle_1);
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
                       // showPersonDetail(cast.getPerson(), v);
                        //TODO
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
                       // showPersonDetail(cast.getPerson(), v);
                        //TODO
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
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = mInflater.inflate(getLayoutId(), parent, false);
            }

            final SeasonWrapper season = getItem(position);

            final MMoviesImageView imageView = (MMoviesImageView)
                    convertView.findViewById(R.id.poster);
            imageView.loadPoster(season);

            final TextView title = (TextView) convertView.findViewById(R.id.title);
            title.setText(StringUtils.getSeasonString(mContext, season.getSeasonNumber()));

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showSeasonDetail(season.getId(), v, 0);
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
