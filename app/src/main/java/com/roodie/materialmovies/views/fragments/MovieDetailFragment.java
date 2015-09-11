package com.roodie.materialmovies.views.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.view.ViewCompat;
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
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.google.common.base.Preconditions;
import com.roodie.materialmovies.R;
import com.roodie.materialmovies.mvp.presenters.MovieDetailPresenter;
import com.roodie.materialmovies.settings.TmdbSettings;
import com.roodie.materialmovies.views.MMoviesApplication;
import com.roodie.materialmovies.views.activities.SettingsActivity;
import com.roodie.materialmovies.views.custom_views.MMoviesImageView;
import com.roodie.materialmovies.views.custom_views.MovieDetailCardLayout;
import com.roodie.materialmovies.views.custom_views.MovieDetailInfoLayout;
import com.roodie.materialmovies.views.custom_views.RatingBarLayout;
import com.roodie.materialmovies.views.custom_views.ViewRecycler;
import com.roodie.materialmovies.views.fragments.base.BaseAnimationFragment;
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
public class MovieDetailFragment extends BaseAnimationFragment implements MovieDetailPresenter.MovieDetailView {

    private static final String LOG_TAG = MovieDetailFragment.class.getSimpleName();

    private static final String KEY_MOVIE_SAVE_STATE = "movie_on_save_state";


    private static final int ANIMATION_DELAY = 150;
    long animationDelay = ANIMATION_DELAY + 2 * 30;

    private MovieDetailPresenter mPresenter;
    private DetailAdapter mAdapter;
    private MovieWrapper mMovie;
    private final ArrayList<DetailItemType> mItems = new ArrayList<>();



    private MenuItem mYoutubeItem;
    private MenuItem mShareItem;
    private MenuItem mWebSearchItem;
    boolean isEnableYoutube = false;
    boolean isEnableShare = false;

    private RelativeLayout mSummaryContainer;

    private CollapsingToolbarLayout mCollapsingToolbar;
    private MMoviesImageView mFanartImageView;
    private TextView mTitleTextView;
    private TextView mSummary;
    private TextView mTagline;
    private MMoviesImageView mPosterImageView;
    private Button mTrailerButton;
    private RatingBarLayout mRatingBarLayout;
    private Context mContext;

    private RelatedMoviesAdapter mRelatedMoviesAdapter;
    private MovieCastAdapter mMovieCastAdapter;
    private MovieCrewAdapter mMovieCrewAdapter;
    private MovieTrailersAdapter mMovieTrailersAdapter;

    private static final Date DATE = new Date();

    protected static final String QUERY_MOVIE_ID = "movie_id";

    public static  MovieDetailFragment newInstance(String id) {
        Preconditions.checkArgument(id != null, "movieId can not be null");

        Bundle bundle = new Bundle();
        bundle.putString(QUERY_MOVIE_ID, id);
        MovieDetailFragment fragment = new MovieDetailFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    public static MovieDetailFragment newInstance(String movieId, String imageUrl) {
        Preconditions.checkArgument(movieId != null, "movieId can not be null");
        Preconditions.checkArgument(imageUrl != null, "ImageUrl can not be null");

        Bundle bundle = new Bundle();
        bundle.putString(QUERY_MOVIE_ID, movieId);
        bundle.putString(KEY_IMAGE_URL, imageUrl);
        MovieDetailFragment fragment = new MovieDetailFragment();
        fragment.setArguments(bundle);

        return fragment;
    }


    public static MovieDetailFragment newInstance(String movieId, int[] startingLocation) {
        Preconditions.checkArgument(movieId != null, "MovieId can not be null");

        Bundle bundle = new Bundle();
        bundle.putString(QUERY_MOVIE_ID, movieId);
        bundle.putIntArray(KEY_REVEAL_START_LOCATION, startingLocation);
        MovieDetailFragment fragment = new MovieDetailFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    public static MovieDetailFragment newInstance(String movieId, int[] startingLocation, String imageUrl) {
        Preconditions.checkArgument(movieId != null, "MovieId can not be null");

        Bundle bundle = new Bundle();
        bundle.putString(QUERY_MOVIE_ID, movieId);
        bundle.putIntArray(KEY_REVEAL_START_LOCATION, startingLocation);
        bundle.putString(KEY_IMAGE_URL, imageUrl);
        MovieDetailFragment fragment = new MovieDetailFragment();
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
        mPresenter = MMoviesApplication.from(activity.getApplicationContext()).getDetailMoviePresenter();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(KEY_MOVIE_SAVE_STATE, mMovie);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            setMovie((MovieWrapper) savedInstanceState.getSerializable(KEY_MOVIE_SAVE_STATE));
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FrameLayout wrapper = new FrameLayout(getActivity());
        inflater.inflate(R.layout.fragment_movie_detail_list, wrapper, true);
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
        mTagline = (TextView) view.findViewById(R.id.textview_tagline);
        mPosterImageView = (MMoviesImageView) view.findViewById(R.id.imageview_poster);
        if (mPosterImageView != null) {

            // check if jelly bean or higher
            if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                mFanartImageView.setImageAlpha(150);
            } else {
                mFanartImageView.setAlpha(150);
            }
            mPosterImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showMovieImages(mMovie);
                }
            });
        } else {

            mFanartImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showMovieImages(mMovie);
                }
            });
        }

        mRatingBarLayout = (RatingBarLayout)view.findViewById(R.id.rating_bar);

        mTrailerButton = (Button) view.findViewById(R.id.trailer_button);
        if (mTrailerButton != null) {
            onPrepareTrailerButton(mTrailerButton);
            mTrailerButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mMovie != null && !MoviesCollections.isEmpty(mMovie.getTrailers())) {
                        playTrailer();
                    }
                }
            });
        }
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
        System.out.println("Configure enter transition");
        if (mPosterImageView != null) {
            ViewCompat.setTransitionName(mPosterImageView, KEY_IMAGE_URL);
            Picasso.with(getActivity().getApplicationContext()).load(getImageUrl()).into(mPosterImageView);
        }
        initializePresenter();
    }

    @Override
    protected void configureEnterAnimation() {
        System.out.println("Configure enter animation");
        final int[] startingLocation = getStartingLocation();

        setEndAnimationX(startingLocation[0]);
        setEndAnimationY(startingLocation[1]);
        super.configureEnterAnimation();
    }

    @Override
    protected void initializePresenter() {
        mPresenter.initialize();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.movie_detail, menu);
        mYoutubeItem = menu.findItem(R.id.menu_open_youtube);
        mYoutubeItem.setEnabled(isEnableYoutube);
        mYoutubeItem.setVisible(isEnableYoutube);

        mShareItem = menu.findItem(R.id.menu_movie_share);
        mShareItem.setEnabled(isEnableShare);
        mShareItem.setVisible(isEnableShare);

        mWebSearchItem = menu.findItem(R.id.menu_action_movie_websearch);
        mWebSearchItem.setEnabled(isEnableShare);
        mWebSearchItem.setVisible(isEnableShare);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        isEnableYoutube = (mMovie != null && !MoviesCollections.isEmpty(mMovie.getTrailers()));
        mYoutubeItem.setEnabled(isEnableYoutube);
        mYoutubeItem.setVisible(isEnableYoutube);

        isEnableShare = mMovie != null && !TextUtils.isEmpty(
                mMovie.getTitle());
        mShareItem.setEnabled(isEnableShare);
        mShareItem.setVisible(isEnableShare);

        mWebSearchItem.setEnabled(isEnableShare);
        mWebSearchItem.setVisible(isEnableShare);
        super.onPrepareOptionsMenu(menu);
    }

    public void onPrepareTrailerButton(Button button) {
        isEnableYoutube = (mMovie != null && !MoviesCollections.isEmpty(mMovie.getTrailers()));
        //button.setEnabled(isEnableYoutube);
    }

    /**
     * Animations
     */

    private void animateSummary() {
     mSummaryContainer.setTranslationY(-mSummaryContainer.getHeight());
     mSummaryContainer.animate().translationY(0).setDuration(300).setStartDelay(100).setInterpolator(getInterpolator());
  }

    private void animateFanart() {
        mFanartImageView.setTranslationY(-mFanartImageView.getHeight());
        mFanartImageView.animate().translationY(0).setDuration(300).setStartDelay(100).setInterpolator(getInterpolator());
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
        Display display = getDisplay();
        if (display != null) {
            switch (item.getItemId()) {
                case R.id.menu_refresh: {
                    mPresenter.refresh();
                    return true;
                }
                case R.id.menu_movie_share: {
                    if (mMovie.getTmdbId() != null) {
                        display.shareMovie(mMovie.getTmdbId(), mMovie.getTitle());
                    }
                }
                return true;
                case R.id.menu_open_youtube: {
                    if (mMovie != null && !MoviesCollections.isEmpty(mMovie.getTrailers())) {
                      playTrailer();
                    }
                }
                return true;
                case R.id.menu_open_tmdb: {
                    if (mMovie.getTmdbId() != null) {
                        display.openTmdbMovie(mMovie);
                    }
                }
                return true;
                case R.id.menu_action_movie_websearch: {
                    if (mMovie.getTitle() != null) {
                        display.performWebSearch(mMovie.getTitle());
                    }
                }
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }


    public MovieDetailPresenter getPresenter() {
        return mPresenter;
    }

    /**
     * MovieView
     */
    @Override
    public void updateDisplayTitle(String title) {
        Display display = getDisplay();
        if (display != null) {
            display.setActionBarTitle(title);
        }
    }

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

    public int[] getStartingLocation() {
        return getArguments().getIntArray(KEY_REVEAL_START_LOCATION);
    }

    public String getImageUrl() {
        return getArguments().getString(KEY_IMAGE_URL);
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
        mAdapter = populateUi();
        getRecyclerView().setAdapter(mAdapter);
        getActivity().invalidateOptionsMenu();
        onPrepareTrailerButton(mTrailerButton);

        getRecyclerView().setVisibility(View.VISIBLE);
        mFanartImageView.setVisibility(View.VISIBLE);
        //animateFanart();

        if (mSummaryContainer != null) {
            mSummaryContainer.setVisibility(View.VISIBLE);
            //animateSummary();
            }
    }

    @Override
    public void showMovieDetail(MovieWrapper movie, View view) {
        Preconditions.checkNotNull(movie, "movie cannot be null");
        int[] startingLocation = new int[2];
        view.getLocationOnScreen(startingLocation);
        startingLocation[0] += view.getWidth() / 2;

        Display display = getDisplay();
        if (display != null) {
            if (movie.getTmdbId() != null) {
                display.startMovieDetailActivityByAnimation(String.valueOf(movie.getTmdbId()), startingLocation);
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
    public void showPersonDetail(PersonWrapper person, View view) {
        Preconditions.checkNotNull(person, "person cannot be null");
        Preconditions.checkNotNull(person.getTmdbId(), "person id cannot be null");

        int[] startingLocation = new int[2];
        view.getLocationOnScreen(startingLocation);
        startingLocation[0] += view.getWidth() / 2;

        Display display = getDisplay();
        if (display != null) {
            display.startPersonDetailActivity(String.valueOf(person.getTmdbId()), startingLocation);
        }

    }


    @Override
    public void playTrailer() {
        final Display display = getDisplay();
        if (display != null) {
            for (TrailerWrapper trailer : mMovie.getTrailers()) {
                if (trailer.getSource().equals(TrailerWrapper.Source.YOUTUBE)) {
                    display.playYoutubeVideo(trailer.getId());
                    return;
                }
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
                .theme(SettingsActivity.THEME == R.style.Theme_MMovies_Light ? Theme.LIGHT : Theme.DARK)
                .show();
    }


    @Override
    public boolean isModal() {
        return false;
    }


    protected DetailAdapter createRecyclerAdapter(List<DetailItemType> items) {
        return new DetailAdapter(items);
    }

    protected DetailAdapter getRecyclerAdapter() {
        return mAdapter;
    }

    private DetailAdapter populateUi() {
        if (mMovie == null) {
            return null;
        }
        mItems.clear();

        if (mMovie.hasBackdropUrl()) {
            mFanartImageView.loadBackdrop(mMovie);
        }

        if (mCollapsingToolbar != null) {
            mCollapsingToolbar.setTitle(mMovie.getTitle());
        }

        if (hasLeftContainer()) {
            if (mMovie.getYear() > 0) {
                mTitleTextView.setText(mMovie.getTitle() + " (" + mMovie.getYear() + ")");
            } else {
                mTitleTextView.setText(mMovie.getTitle());
            }

            if (!TextUtils.isEmpty(mMovie.getTagline())) {
                mTagline.setText(mMovie.getTagline());
            } else {
                mTagline.setHeight(0);
            }
            mSummary.setText(mMovie.getOverview());

            mPosterImageView.loadPoster(mMovie);

            mRatingBarLayout.setRatingVotes(mMovie.getRatingVotes());
            mRatingBarLayout.setRatingValue(mMovie.getRatingVoteAverage());
            mRatingBarLayout.setRatingRange(10);


        } else {

            mItems.add(DetailItemType.TITLE);

            if (!TextUtils.isEmpty(mMovie.getOverview())) {
                mItems.add(DetailItemType.DESCRIPTION);
            }
        }

        mItems.add(DetailItemType.DETAILS);

        /**
         *  if (!MoviesCollections.isEmpty(mMovie.getTrailers())){
         *  mItems.add(DetailItemType.TRAILERS);
         *  }
         */

        if (!MoviesCollections.isEmpty(mMovie.getCast())) {
            mItems.add(DetailItemType.CAST);
        }

        if (!MoviesCollections.isEmpty(mMovie.getCrew())) {
            mItems.add(DetailItemType.CREW);
        }

        if (!MoviesCollections.isEmpty(mMovie.getRelated())) {
            mItems.add(DetailItemType.RELATED);
        }


        return createRecyclerAdapter(mItems);

    }

     enum DetailItemType  {
        TITLE,          //(R.layout.item_movie_detail_title), includes poster, tagline and rating
        TAGLINE,        //(R.layout.iutem_movie_detail_tagline)
        DESCRIPTION,    // R.lay.item_movie_detail_description
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
            for(DetailItemType item : items) {
                switch (item) {
                    case TITLE:
                        mItems.add(new MovieTitleBinder(this));
                        break;
                    case DESCRIPTION:
                        mItems.add(new MovieDescriptionBinder(this));
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
     * MovieDescriptionBinder
     */
    public class MovieDescriptionBinder extends BaseViewHolder<MovieDescriptionBinder.ViewHolder> {

        public MovieDescriptionBinder(BaseDetailAdapter dataBindAdapter) {
            super(dataBindAdapter);
        }

        @Override
        public ViewHolder newViewHolder(ViewGroup parent) {
            View view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.item_movie_detail_description, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void bindViewHolder(ViewHolder holder, int position) {

            holder.tagline.setText(mMovie.getTagline());
            holder.summary.setText(mMovie.getOverview());


            holder.container.setScaleY(0);
            holder.container.setScaleX(0);

            holder.container.animate()
                    .scaleY(1)
                    .scaleX(1)
                    .setDuration(200)
                    .setInterpolator(getInterpolator())
                    .setStartDelay(animationDelay)
                    .start();

        }

        @Override
        public int getItemCount() {
            return 1;
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            View container;
            TextView tagline;
            TextView summary;

            public ViewHolder(View view) {
                super(view);

                container = view.findViewById(R.id.movie_detail_card_details);
                tagline = (TextView) view.findViewById(R.id.textview_tagline);
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
            holder.title.setText(mMovie.getTitle() + " (" + mMovie.getYear() + ")");
            holder.genres.setText(mMovie.getGenres());
            String mImageBaseUrl = TmdbSettings.getImageBaseUrl(mContext)
                    + TmdbSettings.POSTER_SIZE_SPEC_W154;

            Picasso.with(mContext)
                    .load(mImageBaseUrl + mMovie.getPosterUrl())
                    .fit().
                    centerCrop().
                    into(holder.posterImageView);

            holder.ratingBarLayout.setRatingVotes(mMovie.getRatingVotes());
            holder.ratingBarLayout.setRatingValue(mMovie.getRatingVoteAverage());
            holder.ratingBarLayout.setRatingRange(10);

            onPrepareTrailerButton(holder.trailerButton);
            holder.trailerButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mMovie != null && !MoviesCollections.isEmpty(mMovie.getTrailers())) {
                        playTrailer();
                    }
                }
            });

            holder.container.setScaleY(0);
            holder.container.setScaleX(0);

            holder.container.animate()
                    .scaleY(1)
                    .scaleX(1)
                    .setDuration(200)
                    .setInterpolator(getInterpolator())
                    .setStartDelay(animationDelay)
                    .start();
        }


        @Override
        public int getItemCount() {
            return 1;
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            LinearLayout container;
            TextView title;
            TextView genres;
            ImageView posterImageView;
            RatingBarLayout ratingBarLayout;
            Button trailerButton;


            public ViewHolder(View view) {
                super(view);

                container = (LinearLayout) view.findViewById(R.id.container_layout);
                title = (TextView) view.findViewById(R.id.textview_title);
                genres = (TextView) view.findViewById(R.id.textview_genres);
                posterImageView = (ImageView)view.findViewById(R.id.imageview_poster);
                ratingBarLayout = (RatingBarLayout)view.findViewById(R.id.rating_bar);
                trailerButton = (Button) view.findViewById(R.id.trailer_button);

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

            holder.container.setScaleY(0);
            holder.container.setScaleX(0);

            holder.container.animate()
                    .scaleY(1)
                    .scaleX(1)
                    .setDuration(200)
                    .setInterpolator(getInterpolator())
                    .setStartDelay(animationDelay)
                    .start();

        }

        @Override
        public int getItemCount() {
            return 1;
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            MovieDetailCardLayout container;
            MovieDetailInfoLayout runtimeLayout;
            MovieDetailInfoLayout certificationLayout;
            MovieDetailInfoLayout genreLayout;
            MovieDetailInfoLayout releasedInfoLayout;
            MovieDetailInfoLayout budgetInfoLayout;
            MovieDetailInfoLayout languageLayout;

            public ViewHolder(View view) {
                super(view);

                container = (MovieDetailCardLayout) view.findViewById(R.id.movie_detail_card_details);
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


            cardLayout.setScaleY(0);
            cardLayout.setScaleX(0);

            cardLayout.animate()
                    .scaleY(1)
                    .scaleX(1)
                    .setDuration(200)
                    .setInterpolator(getInterpolator())
                    .setStartDelay(animationDelay)
                    .start();
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

            cardLayout.setScaleY(0);
            cardLayout.setScaleX(0);

            cardLayout.animate()
                    .scaleY(1)
                    .scaleX(1)
                    .setDuration(200)
                    .setInterpolator(getInterpolator())
                    .setStartDelay(animationDelay)
                    .start();
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

            cardLayout.setScaleY(0);
            cardLayout.setScaleX(0);

            cardLayout.animate()
                    .scaleY(1)
                    .scaleX(1)
                    .setDuration(200)
                    .setInterpolator(getInterpolator())
                    .setStartDelay(animationDelay)
                    .start();
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
                       showPersonDetail(cast.getPerson(), v);
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
                        showPersonDetail(cast.getPerson(), v);
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
                   showMovieDetail((MovieWrapper) v.getTag(), v);
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
                        playTrailer();
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
