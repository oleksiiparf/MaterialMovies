package com.roodie.materialmovies.views.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.common.base.Preconditions;
import com.roodie.materialmovies.R;
import com.roodie.materialmovies.mvp.presenters.MovieDetailPresenter;
import com.roodie.materialmovies.settings.TmdbSettings;
import com.roodie.materialmovies.util.MMoviesServiceUtils;
import com.roodie.materialmovies.util.TmdbTools;
import com.roodie.materialmovies.views.MMoviesApplication;
import com.roodie.materialmovies.views.custom_views.ArcProgress;
import com.roodie.materialmovies.views.custom_views.AutofitTextView;
import com.roodie.materialmovies.views.custom_views.MMoviesImageView;
import com.roodie.materialmovies.views.custom_views.MovieDetailCardLayout;
import com.roodie.materialmovies.views.custom_views.MovieDetailInfoLayout;
import com.roodie.materialmovies.views.custom_views.ViewRecycler;
import com.roodie.materialmovies.views.fragments.base.BaseDetailFragment;
import com.roodie.model.Display;
import com.roodie.model.entities.MovieCreditWrapper;
import com.roodie.model.entities.MovieWrapper;
import com.roodie.model.entities.PersonWrapper;
import com.roodie.model.entities.TrailerWrapper;
import com.roodie.model.network.NetworkError;
import com.roodie.model.util.MoviesCollections;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Roodie on 27.06.2015.
 */
public class MovieDetailFragment extends BaseDetailFragment implements MovieDetailPresenter.MovieDetailView, View.OnClickListener {

    private static final String LOG_TAG = MovieDetailFragment.class.getSimpleName();

    private MovieDetailPresenter mPresenter;
    private DetailAdapter mAdapter;
    private MovieWrapper mMovie;
    private final ArrayList<DetailItemType> mItems = new ArrayList<>();

    private CollapsingToolbarLayout mCollapsingToolbar;
    private MMoviesImageView mFanartImageView;
    private TextView mTitleTextView;
    private AutofitTextView mTaglineTextView;
    private MMoviesImageView mPosterImageView;
    private ArcProgress mRatingBar;
    private TextView mVotesTextView;


    private Context mContext;

    private RelatedMoviesAdapter mRelatedMoviesAdapter;
    private MovieCastAdapter mMovieCastAdapter;
    private MovieCrewAdapter mMovieCrewAdapter;
    private MovieTrailersAdapter mMovieTrailersAdapter;

    private static final Date DATE = new Date();

    private static final String QUERY_MOVIE_ID = "movie_id";

    public static MovieDetailFragment newInstance(String movieId) {
        Preconditions.checkArgument(movieId != null, "MovieId can not be null");

        Bundle bundle = new Bundle();
        bundle.putString(QUERY_MOVIE_ID, movieId);
        MovieDetailFragment fragment = new MovieDetailFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // MMoviesApplication.from(getActivity()).inject(this);
        setHasOptionsMenu(true);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity.getApplicationContext();
        mPresenter = MMoviesApplication.from(activity.getApplicationContext()).getDetailMoviePresenter();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_movie_detail_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mCollapsingToolbar = (CollapsingToolbarLayout) view.findViewById(R.id.backdrop_toolbar);
        mFanartImageView = (MMoviesImageView) view.findViewById(R.id.imageview_fanart);
        if (mFanartImageView != null) {
            mFanartImageView.setOnClickListener(this);
        }

        mTitleTextView = (TextView) view.findViewById(R.id.textview_title);
        mTaglineTextView = (AutofitTextView) view.findViewById(R.id.textview_tagline);
        mPosterImageView = (MMoviesImageView) view.findViewById(R.id.imageview_poster);
        mRatingBar = (ArcProgress) view.findViewById(R.id.rating_bar);
        mVotesTextView = (TextView) view.findViewById(R.id.textview_votes);
        mPresenter.attachView(this);
        mPresenter.initialize();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.movie_detail, menu);
    }

    @Override
    public void onPause() {
        super.onPause();
        mPresenter.onPause();

    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.onResume();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_refresh: {
                mPresenter.refresh();
                return true;
            }
        }
        return false;
    }

    public MovieDetailPresenter getPresenter() {
        return mPresenter;
    }

    /**
     * MovieView
     */
    @Override
    public void showError(NetworkError error) {

    }

    @Override
    public void showLoadingProgress(boolean visible) {
        getActivity().setProgressBarIndeterminateVisibility(visible);
    }

    @Override
    public void showSecondaryLoadingProgress(boolean visible) {

    }

    @Override
    public String getRequestParameter() {
        return getArguments().getString(QUERY_MOVIE_ID);
    }

    @Override
    public MovieQueryType getQueryType() {
        return MovieQueryType.MOVIE_DETAIL;
    }


    /**
     * MovieDetailView
     */

    @Override
    public void setMovie(MovieWrapper movie) {
        mMovie = movie;
        populateUi();
        getRecyclerView().setAdapter(mAdapter);
    }

    @Override
    public void showMovieDetail(MovieWrapper movie, Bundle bundle) {
        Preconditions.checkNotNull(movie, "movie cannot be null");

        Display display = getDisplay();
        if (display != null) {
            if (movie.getTmdbId() != null) {
                display.startMovieDetailActivity(String.valueOf(movie.getTmdbId()), bundle);
            }
        }
    }


    @Override
    public void showMovieImages(MovieWrapper movie) {
        Preconditions.checkNotNull(movie, "movie cannot be null");

        final Display display = getDisplay();
        if (display != null) {
            display.startMovieImagesActivity(String.valueOf(movie.getTmdbId()));
        }
    }

    @Override
    public void showPersonDetail(PersonWrapper person, Bundle bundle) {
        Preconditions.checkNotNull(person, "person cannot be null");
        Preconditions.checkNotNull(person.getTmdbId(), "person id cannot be null");

        Display display = getDisplay();
        if (display != null) {
            display.startPersonDetailActivity(String.valueOf(person.getTmdbId()), bundle);
        }
    }

    @Override
    public void playTrailer(TrailerWrapper trailer) {
        Preconditions.checkNotNull(trailer, "trailer cannot be null");
        Preconditions.checkNotNull(trailer.getId(), "trailer id cannot be null");

        final Display display = getDisplay();
        if (display != null) {
            switch (trailer.getSource()) {
                case YOUTUBE:
                    display.playYoutubeVideo(trailer.getId());
                    break;
            }
        }
    }

    @Override
    public void showMovieCreditsDialog(MovieQueryType queryType) {
        Preconditions.checkNotNull(queryType, "Query type cannot be null");
        Log.d(LOG_TAG, "Show detail dialog list");
        ListView list = new ListView(mContext);
        String mTitle = "";
        boolean wrapInScrollView = false;

        switch (queryType) {
            case MOVIE_CAST:
                list.setAdapter(getMovieCastAdapter());
                mTitle = getResources().getString(R.string.cast_movies);
                break;
            case MOVIE_CREW:
                list.setAdapter(getMovieCrewAdapter());
                mTitle = getResources().getString(R.string.crew_movies);
                break;
            case MOVIE_RELATED:
                list.setAdapter(getRelatedMoviesAdapter());
                mTitle = getResources().getString(R.string.related_movies);
        }
        new MaterialDialog.Builder(getActivity())
                .title(mTitle)
                .customView(list, wrapInScrollView)
                .show();
    }


    @Override
    public boolean isModal() {
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageview_poster: {
                showMovieImages(mMovie);
            }
            break;
        }
    }

    protected DetailAdapter createRecyclerAdapter(List<DetailItemType> items) {
        return new DetailAdapter(items);
    }

    protected DetailAdapter getRecyclerAdapter() {
        return mAdapter;
    }

    private void populateUi() {
        if (mMovie == null) {
            return;
        }
        mItems.clear();

        if (mMovie.hasBackdropUrl()) {
            mFanartImageView.loadBackdrop(mMovie);
        }

        if (mCollapsingToolbar != null) {
            mCollapsingToolbar.setTitle(mMovie.getTitle());
        }

        if (hasTitleContainer() && mTitleTextView != null) {
            mTitleTextView.setText(mMovie.getTitle());
            mTaglineTextView.setText(mMovie.getTagline());
            mPosterImageView.loadPoster(mMovie);

            mRatingBar.setMax(100);
            mRatingBar.setProgress(mMovie.getAverageRatingPercent());
            mVotesTextView.setText(String.valueOf(mMovie.getRatingVotes()));
        } else {
            mItems.add(DetailItemType.TITLE);
        }

        if (!TextUtils.isEmpty(mMovie.getOverview())) {
            mItems.add(DetailItemType.SUMMARY);
        }


        mItems.add(DetailItemType.DETAILS);

        if (!MoviesCollections.isEmpty(mMovie.getTrailers())){
            mItems.add(DetailItemType.TRAILERS);
        }


        if (!MoviesCollections.isEmpty(mMovie.getCast())) {
            mItems.add(DetailItemType.CAST);
        }

        if (!MoviesCollections.isEmpty(mMovie.getCrew())) {
            mItems.add(DetailItemType.CREW);
        }

        if (!MoviesCollections.isEmpty(mMovie.getRelated())) {
            mItems.add(DetailItemType.RELATED);
        }

        mAdapter = createRecyclerAdapter(mItems);

    }

     enum DetailItemType  {
        TITLE,          //(R.layout.item_movie_detail_title), includes poster, tagline and rating
        DETAILS,        //(R.layout.item_movie_detail_details), include details
        SUMMARY,        //(R.layout.item_movie_detail_summary), includes description text, maybe
        TRAILERS,       //(R.layout.item_movie_detail_trailers), includes trailers
        RELATED,        //(R.layout.item_movie_detail_generic_card), includes related movies list
        CAST,           //(R.layout.item_movie_detail_generic_card), includes cast list
        CREW            //(R.layout.item_movie_detail_generic_card), includes crew list

    }

    /**
     * DetailAdapter
     */
    public class DetailAdapter extends  EnumListDetailAdapter<DetailItemType> {
        List<BaseViewHolder> mItems;

        public DetailAdapter() {
        }

        public DetailAdapter(List<DetailItemType> items) {
            mItems = new ArrayList<>(items.size());
            for (DetailItemType item : items) {
                switch (item) {
                    case TITLE:
                        mItems.add(new MovieTitleBinder(this));
                        break;
                    case SUMMARY:
                        mItems.add(new MovieSummaryBinder(this));
                        break;
                    case DETAILS:
                        mItems.add(new MovieDetailsBinder(this));
                        break;
                    case TRAILERS:
                        mItems.add(new MovieTrailersBinder(this));
                        break;
                    case CAST:
                        mItems.add(new MovieCastBinder(this));
                        break;
                    case CREW:
                        mItems.add(new MovieCrewBinder(this));
                        break;
                    case RELATED:
                        mItems.add(new MovieRelatedBinder(this));
                }
            }
            addAllBinder(mItems);
        }

    }

    /**
     * MovieSummaryBinder
     */
    public class MovieSummaryBinder extends BaseViewHolder<MovieSummaryBinder.ViewHolder> {

        public MovieSummaryBinder(BaseDetailAdapter dataBindAdapter) {
            super(dataBindAdapter);
        }

        @Override
        public ViewHolder newViewHolder(ViewGroup parent) {
            View view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.item_movie_detail_summary, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void bindViewHolder(ViewHolder holder, int position) {
             holder.summary.setText(mMovie.getOverview());
        }

        @Override
        public int getItemCount() {
            return 1;
        }

         class ViewHolder extends RecyclerView.ViewHolder {

            TextView summary;

            public ViewHolder(View view) {
                super(view);
                summary = (TextView) view.findViewById(R.id.textview_summary);
            }
        }
    }

    /**
     * MovieTitleBinder
     */
    public class MovieTitleBinder extends BaseViewHolder<MovieTitleBinder.ViewHolder> {

        public MovieTitleBinder(BaseDetailAdapter dataBindAdapter) {
            super(dataBindAdapter);
        }

        @Override
        public ViewHolder newViewHolder(ViewGroup parent) {
            View view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.item_movie_detail_title, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void bindViewHolder(ViewHolder holder, int position) {
            holder.taglineTextView.setText(mMovie.getTagline());
            String mImageBaseUrl = TmdbSettings.getImageBaseUrl(mContext)
                    + TmdbSettings.POSTER_SIZE_SPEC_W154;
            Picasso.with(mContext)
                    .load(mImageBaseUrl + mMovie.getPosterUrl())
                    .fit().
                    centerCrop().
                    into(holder.posterImageView);

            holder.ratingBar.setMax(100);
            holder.ratingBar.setProgress(mMovie.getAverageRatingPercent());
            //holder.votes.setText(mMovie.getRatingVotes());
        }

        @Override
        public int getItemCount() {
            return 1;
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            TextView taglineTextView;
            ImageView posterImageView;
            ArcProgress ratingBar;
            TextView votes;

            public ViewHolder(View view) {
                super(view);

                taglineTextView = (TextView) view.findViewById(R.id.textview_tagline);
                posterImageView = (ImageView)view.findViewById(R.id.imageview_poster);
                ratingBar = (ArcProgress) view.findViewById(R.id.rating_bar);
                votes = (TextView) view.findViewById(R.id.textview_votes);
            }
        }
    }

    /**
     * MovieDetailsBinder
     */
    public class MovieDetailsBinder extends BaseViewHolder<MovieDetailsBinder.ViewHolder> {

        public MovieDetailsBinder(BaseDetailAdapter dataBindAdapter) {
            super(dataBindAdapter);
        }

        @Override
        public ViewHolder newViewHolder(ViewGroup parent) {
            View view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.item_movie_detail_details, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void bindViewHolder(ViewHolder holder, int position) {
            if (mMovie.getRuntime() > 0) {
                holder.runtimeLayout.setContentText(
                        getString(R.string.movie_details_runtime_content, mMovie.getRuntime()));
                holder.runtimeLayout.setVisibility(View.VISIBLE);
            } else {
                holder.runtimeLayout.setVisibility(View.GONE);
            }

            if (!TextUtils.isEmpty(mMovie.getCertification())) {
                holder.certificationLayout.setContentText(mMovie.getCertification());
                holder.certificationLayout.setVisibility(View.VISIBLE);
            } else {
                holder.certificationLayout.setVisibility(View.GONE);
            }

            if (!TextUtils.isEmpty(mMovie.getGenres())) {
                holder.genreLayout.setContentText(mMovie.getGenres());
                holder.genreLayout.setVisibility(View.VISIBLE);
            } else {
                holder.genreLayout.setVisibility(View.GONE);
            }

            if (mMovie.getReleasedTime() > 0) {
                DATE.setTime(mMovie.getReleasedTime());
                DateFormat dateFormat = DateFormat.getDateInstance();
                holder.releasedInfoLayout.setContentText( dateFormat.format(DATE));
                holder.releasedInfoLayout.setVisibility(View.VISIBLE);

            } else {
                holder.releasedInfoLayout.setVisibility(View.GONE);
            }

            if (mMovie.getBudget() > 0) {
                holder.budgetInfoLayout.setContentText(
                        getString(R.string.movie_details_budget_content, mMovie.getBudget()));
                holder.budgetInfoLayout.setVisibility(View.VISIBLE);
            } else {
                holder.budgetInfoLayout.setVisibility(View.GONE);
            }

            if (!TextUtils.isEmpty(mMovie.getMainLanguageTitle())) {
                holder.languageLayout.setContentText(mMovie.getMainLanguageTitle());
                holder.languageLayout.setVisibility(View.VISIBLE);
            } else {
                holder.languageLayout.setVisibility(View.GONE);
            }
        }

        @Override
        public int getItemCount() {
            return 1;
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            MovieDetailInfoLayout runtimeLayout;
            MovieDetailInfoLayout certificationLayout;
            MovieDetailInfoLayout genreLayout;
            MovieDetailInfoLayout releasedInfoLayout;
            MovieDetailInfoLayout budgetInfoLayout;
            MovieDetailInfoLayout languageLayout;

            public ViewHolder(View view) {
                super(view);

                runtimeLayout = (MovieDetailInfoLayout) view.findViewById(R.id.layout_info_runtime);
                certificationLayout = (MovieDetailInfoLayout) view.findViewById(R.id.layout_info_certification);
                genreLayout = (MovieDetailInfoLayout) view.findViewById(R.id.layout_info_genres);
                releasedInfoLayout = (MovieDetailInfoLayout) view.findViewById(R.id.layout_info_released);
                budgetInfoLayout = (MovieDetailInfoLayout) view.findViewById(R.id.layout_info_budget);
                languageLayout = (MovieDetailInfoLayout) view.findViewById(R.id.layout_info_language);
            }
        }
    }

    /**
     * MovieCastBinder
     */
    public class MovieCastBinder extends BaseViewHolder<MovieCastBinder.ViewHolder> {

        MovieDetailCardLayout cardLayout;

        public MovieCastBinder(BaseDetailAdapter dataBindAdapter) {
            super(dataBindAdapter);
            Log.d(LOG_TAG, "Bind cast");
        }

        @Override
        public ViewHolder newViewHolder(ViewGroup parent) {
            View view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.item_movie_detail_generic_card, parent, false);
            cardLayout = (MovieDetailCardLayout) view;

            return new ViewHolder(view);
        }

        @Override
        public void bindViewHolder(ViewHolder holder, int position) {

            cardLayout.setTitle(R.string.cast_movies);

            final View.OnClickListener seeMoreClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showMovieCreditsDialog(MovieQueryType.MOVIE_CAST);
                }
            };

            final ViewRecycler viewRecycler = new ViewRecycler(holder.layout);
            viewRecycler.recycleViews();

            if (!getMovieCastAdapter().isEmpty()) {
                final int numItems = getResources().getInteger(R.integer.detail_card_max_items);
                final int adapterCount = getMovieCastAdapter().getCount();

                for (int i = 0; i < Math.min(numItems, adapterCount); i++) {
                    View view = getMovieCastAdapter().getView(i, viewRecycler.getRecycledView(), holder.layout);
                    holder.layout.addView(view);
                }
                final boolean showSeeMore = numItems < getMovieCastAdapter().getCount();
                cardLayout.setSeeMoreVisibility(showSeeMore);
                cardLayout.setSeeMoreOnClickListener(showSeeMore ? seeMoreClickListener : null);

            }
            viewRecycler.clearRecycledViews();
        }

        @Override
        public int getItemCount() {
            return 1;
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
     * MovieTitleBinder
     */
    public class MovieCrewBinder extends BaseViewHolder<MovieCrewBinder.ViewHolder> {

        MovieDetailCardLayout cardLayout;

        public MovieCrewBinder(BaseDetailAdapter dataBindAdapter) {
            super(dataBindAdapter);
            Log.d(LOG_TAG, "Bind crew");
        }

        @Override
        public ViewHolder newViewHolder(ViewGroup parent) {

            View view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.item_movie_detail_generic_card, parent, false);
            cardLayout = (MovieDetailCardLayout) view;
            return new ViewHolder(view);
        }

        @Override
        public void bindViewHolder(ViewHolder holder, int position) {

            cardLayout.setTitle(R.string.crew_movies);

            final View.OnClickListener seeMoreClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showMovieCreditsDialog(MovieQueryType.MOVIE_CREW);
                }
            };

            final ViewRecycler viewRecycler = new ViewRecycler(holder.layout);
            viewRecycler.recycleViews();

            if (!getMovieCrewAdapter().isEmpty()) {
                final int numItems = getResources().getInteger(R.integer.detail_card_max_items);
                final int adapterCount = getMovieCrewAdapter().getCount();

                for (int i = 0; i < Math.min(numItems, adapterCount); i++) {
                    View view = getMovieCrewAdapter().getView(i, viewRecycler.getRecycledView(), holder.layout);
                    holder.layout.addView(view);
                }
                final boolean showSeeMore = numItems < getMovieCrewAdapter().getCount();
                cardLayout.setSeeMoreVisibility(showSeeMore);
                cardLayout.setSeeMoreOnClickListener(showSeeMore ? seeMoreClickListener : null);

            }
            viewRecycler.clearRecycledViews();
        }

        @Override
        public int getItemCount() {
            return 1;
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
     * MovieRelated
     */
    public class MovieRelatedBinder extends BaseViewHolder<MovieRelatedBinder.ViewHolder> {

        private MovieDetailCardLayout cardLayout;

        public MovieRelatedBinder(BaseDetailAdapter dataBindAdapter) {
            super(dataBindAdapter);
            Log.d(LOG_TAG, "Bind related movies");
        }

        @Override
        public ViewHolder newViewHolder(ViewGroup parent) {

            View view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.item_movie_detail_generic_card, parent, false);
            cardLayout = (MovieDetailCardLayout) view;
            return new ViewHolder(view);
        }

        @Override
        public void bindViewHolder(ViewHolder holder, int position) {
            cardLayout.setTitle(R.string.related_movies);

            final View.OnClickListener seeMoreClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showMovieCreditsDialog(MovieQueryType.MOVIE_RELATED);
                }
            };

            final ViewRecycler viewRecycler = new ViewRecycler(holder.layout);
            viewRecycler.recycleViews();

            if (!getRelatedMoviesAdapter().isEmpty()) {
                final int numItems = getResources().getInteger(R.integer.detail_card_max_items);
                final int adapterCount = getRelatedMoviesAdapter().getCount();

                for (int i = 0; i < Math.min(numItems, adapterCount); i++) {
                    View view = getRelatedMoviesAdapter().getView(i, viewRecycler.getRecycledView(), holder.layout);
                    holder.layout.addView(view);
                }
                final boolean showSeeMore = numItems < getRelatedMoviesAdapter().getCount();
                cardLayout.setSeeMoreVisibility(showSeeMore);
                cardLayout.setSeeMoreOnClickListener(showSeeMore ? seeMoreClickListener : null);

            }
            viewRecycler.clearRecycledViews();
        }

        @Override
        public int getItemCount() {
            return 1;
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
     * MovieTrailersBinder
     */
    public class MovieTrailersBinder extends BaseViewHolder<MovieTrailersBinder.ViewHolder> {

        public MovieTrailersBinder(BaseDetailAdapter dataBindAdapter) {
            super(dataBindAdapter);
            Log.d(LOG_TAG, "bindTrailers");
        }

        @Override
        public ViewHolder newViewHolder(ViewGroup parent) {
            View view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.item_movie_detail_trailers, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void bindViewHolder(ViewHolder holder, int position) {

            final ViewRecycler viewRecycler = new ViewRecycler(holder.layout);
            viewRecycler.recycleViews();

            if (!getMovieTrailersAdapter().isEmpty()) {
                final int numItems = getResources().getInteger(R.integer.detail_card_max_items);
                final int adapterCount = getMovieTrailersAdapter().getCount();

                for (int i = 0; i < Math.min(numItems, adapterCount); i++) {
                    View view = getMovieTrailersAdapter().getView(i, viewRecycler.getRecycledView(), holder.layout);
                    holder.layout.addView(view);
                }
            }
            viewRecycler.clearRecycledViews();
        }

        @Override
        public int getItemCount() {
            return 1;
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
        public abstract MovieCreditWrapper getItem(int position) ;

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = mInflater.inflate(getLayoutId(), parent, false);
            }

            MovieCreditWrapper credit = getItem(position);

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
     * MovieCastAdapter
     */
    private class MovieCastAdapter extends BaseMovieCastAdapter {

        public MovieCastAdapter(LayoutInflater mInflater) {
            super(mInflater, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MovieCreditWrapper cast = (MovieCreditWrapper) v.getTag();
                    if (cast != null) {
                       showPersonDetail(cast.getPerson(), null);
                    }
                }
            });
        }

        @Override
        public int getCount() {
            return mMovie != null ? MoviesCollections.size(mMovie.getCast()) : 0;
        }

        @Override
        public MovieCreditWrapper getItem(int position) {
            return mMovie.getCast().get(position);
        }
    }

    /**
     * MovieCrewAdapter
     */
    private class MovieCrewAdapter extends BaseMovieCastAdapter {

        public MovieCrewAdapter(LayoutInflater mInflater) {
            super(mInflater, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MovieCreditWrapper cast = (MovieCreditWrapper) v.getTag();
                    if (cast != null) {
                        showPersonDetail(cast.getPerson(), null);
                    }
                }
            });
        }

        @Override
        public int getCount() {
            return mMovie != null ? MoviesCollections.size(mMovie.getCrew()) : 0;
        }

        @Override
        public MovieCreditWrapper getItem(int position) {
            return mMovie.getCrew().get(position);
        }
    }

    /**
     * RelatedMoviesAdapter
     */
    private class RelatedMoviesAdapter extends BaseAdapter {

        private final View.OnClickListener mItemOnClickListener;
        private final LayoutInflater mInflater;

        public RelatedMoviesAdapter( LayoutInflater mInflater) {
            this.mInflater = mInflater;

            this.mItemOnClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   showMovieDetail((MovieWrapper) v.getTag(),
                           null);
                }
            };

        }

        @Override
        public int getCount() {
            if (mMovie != null & !MoviesCollections.isEmpty(mMovie.getRelated())) {
                return mMovie.getRelated().size();
            } else
            return 0;
        }

        @Override
        public MovieWrapper getItem(int position) {
            return mMovie.getRelated().get(position);
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

            final MovieWrapper movie = getItem(position);

            final TextView title = (TextView) convertView.findViewById(R.id.title);
            if (movie.getYear() > 0) {
                title.setText(getString(R.string.movie_title_year,
                        movie.getTitle(), movie.getYear()));
            } else {
                title.setText(movie.getTitle());
            }

            final MMoviesImageView imageView =
                    (MMoviesImageView) convertView.findViewById(R.id.poster);
            imageView.setAvatarMode(false);
            imageView.loadPoster(movie);

            MMoviesServiceUtils.loadWithPicasso(mContext, TmdbTools.buildProfileImageUrl(mContext, movie.getPosterUrl(),
                    TmdbTools.ProfileImageSize.W185))
                    .resizeDimen(R.dimen.person_headshot_size, R.dimen.person_headshot_size)
                    .centerCrop()
                    .error(R.color.protection_dark)
                    .into(imageView);

            convertView.setOnClickListener(mItemOnClickListener);
            convertView.setTag(movie);

            return convertView;
        }

        protected int getLayoutId() {
            return R.layout.item_movie_detail_list_1line;
        }
    }

    /**
     * MovieTrailersAdapter
     */
    private class MovieTrailersAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        private View.OnClickListener mOnClickListener;

        public MovieTrailersAdapter(LayoutInflater mInflater) {
            this.mInflater = mInflater;

            this.mOnClickListener = new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    TrailerWrapper trailer = (TrailerWrapper) v.getTag();
                    if (trailer != null) {
                        playTrailer(trailer);
                    }
                }
            };
        }

        @Override
        public int getCount() {
            if (mMovie != null && !MoviesCollections.isEmpty(mMovie.getTrailers())) {
                return mMovie.getTrailers().size();
            } else {
                return 0;
            }
        }

        @Override
        public TrailerWrapper getItem(int position) {
            return mMovie.getTrailers().get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
           if (convertView == null) {
               convertView = mInflater.inflate(R.layout.item_movie_trailer, parent, false);
           }
            final TrailerWrapper trailer = getItem(position);

            ImageView imageView = (ImageView) convertView.findViewById(R.id.thumbnail);
            // imageView set trailer


            TextView title = (TextView) convertView.findViewById(R.id.title);
            title.setText(trailer.getName());

            convertView.setOnClickListener(mOnClickListener);
            convertView.setTag(trailer);

            return convertView;
        }
    }



    private RelatedMoviesAdapter getRelatedMoviesAdapter() {
        if (mRelatedMoviesAdapter == null) {
            mRelatedMoviesAdapter = new RelatedMoviesAdapter(LayoutInflater.from(getActivity()));
        }
        return mRelatedMoviesAdapter;
    }

    private MovieCastAdapter getMovieCastAdapter() {
        if (mMovieCastAdapter == null) {
            mMovieCastAdapter = new MovieCastAdapter(LayoutInflater.from(getActivity()));
        }
        return mMovieCastAdapter;
    }

    private MovieCrewAdapter getMovieCrewAdapter() {
        if (mMovieCrewAdapter == null) {
            mMovieCrewAdapter = new MovieCrewAdapter(LayoutInflater.from(getActivity()));
        }
        return mMovieCrewAdapter;
    }

    private MovieTrailersAdapter getMovieTrailersAdapter() {
        if (mMovieTrailersAdapter == null) {
            mMovieTrailersAdapter = new MovieTrailersAdapter(LayoutInflater.from(getActivity()));
        }
        return mMovieTrailersAdapter;
    }


    @Override
    protected void setSupportActionBar(Toolbar toolbar) {
       setSupportActionBar(toolbar, false);
    }
}
