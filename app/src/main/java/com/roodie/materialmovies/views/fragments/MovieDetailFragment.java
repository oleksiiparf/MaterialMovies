package com.roodie.materialmovies.views.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.common.base.Preconditions;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ObjectAnimator;
import com.roodie.materialmovies.R;
import com.roodie.materialmovies.mvp.presenters.MovieDetailPresenter;
import com.roodie.materialmovies.settings.TmdbSettings;
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

import io.codetail.animation.SupportAnimator;
import io.codetail.animation.ViewAnimationUtils;

/**
 * Created by Roodie on 27.06.2015.
 */
public class MovieDetailFragment extends BaseDetailFragment implements MovieDetailPresenter.MovieDetailView {

    private static final String LOG_TAG = MovieDetailFragment.class.getSimpleName();

    private static final String KEY_REVEAL_START_LOCATION = "reveal_start_location";
    private static final String KEY_MOVIE_SAVE_STATE = "movie_on_save_state";

    final static AccelerateInterpolator ACCELERATE = new AccelerateInterpolator();
    final static Interpolator INTERPOLATOR = new DecelerateInterpolator();
    final static DecelerateInterpolator DECELERATE = new DecelerateInterpolator();
    final static AccelerateDecelerateInterpolator ACCELERATE_DECELERATE = new AccelerateDecelerateInterpolator();

    private static final int ANIMATION_DELAY = 150;
    long animationDelay = ANIMATION_DELAY + 2 * 30;

    private MovieDetailPresenter mPresenter;
    private DetailAdapter mAdapter;
    private MovieWrapper mMovie;
    private final ArrayList<DetailItemType> mItems = new ArrayList<>();

    private int endAnimationX, endAnimationY;
    private   int startAnimationPairBottom;

    private MenuItem mYoutubeItem;
    private MenuItem mShareItem;
    private MenuItem mWebSearchItem;
    boolean isEnableYoutube = false;
    boolean isEnableShare = false;

    private FrameLayout mAnimationLayout;
    private LinearLayout mSummaryContainer;

    private LinearLayout mSummaryRoot;
    private LinearLayout mRatingBarContainer;

    private CollapsingToolbarLayout mCollapsingToolbar;
    private MMoviesImageView mFanartImageView;
    private TextView mTitleTextView;
    private TextView mGenresTextView;
    private AutofitTextView mTaglineTextView;
    private MMoviesImageView mPosterImageView;
    private LinearLayout mTrailerButton;
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

    public static MovieDetailFragment newInstance(String movieId, int[] startingLocation) {
        Preconditions.checkArgument(movieId != null, "MovieId can not be null");

        Bundle bundle = new Bundle();
        bundle.putString(QUERY_MOVIE_ID, movieId);
        bundle.putIntArray(KEY_REVEAL_START_LOCATION, startingLocation);
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
        return inflater.inflate(R.layout.fragment_movie_detail_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final int[] startingLocation = getStartingLocation();

        endAnimationX = startingLocation[0];
        endAnimationY = startingLocation[1];

        //set actionbar up navigation
        final Display display = getDisplay();
        if (!isModal()) {
            display.showUpNavigation(getQueryType() != null && getQueryType().showUpNavigation());
        }

        mCollapsingToolbar = (CollapsingToolbarLayout) view.findViewById(R.id.backdrop_toolbar);
        mFanartImageView = (MMoviesImageView) view.findViewById(R.id.imageview_fanart);
        if (mFanartImageView != null) {
            mFanartImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showMovieImages(mMovie);
                }
            });
        }

        mAnimationLayout = (FrameLayout) view.findViewById(R.id.transaction_container);
        mSummaryContainer = (LinearLayout) view.findViewById(R.id.container_layout);

        mSummaryRoot = (LinearLayout) view.findViewById(R.id.summary_root);
        mRatingBarContainer = (LinearLayout) view.findViewById(R.id.rating_bar_container);


        mTitleTextView = (TextView) view.findViewById(R.id.textview_title);
        mGenresTextView = (TextView) view.findViewById(R.id.textview_genres);
        mTaglineTextView = (AutofitTextView) view.findViewById(R.id.textview_tagline);
        mPosterImageView = (MMoviesImageView) view.findViewById(R.id.imageview_poster);

        mRatingBar = (ArcProgress) view.findViewById(R.id.rating_bar);
        mVotesTextView = (TextView) view.findViewById(R.id.textview_votes);
        mTrailerButton = (LinearLayout) view.findViewById(R.id.trailer_container);
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
        //mPresenter.initialize();

        mAnimationLayout.post(new Runnable() {
            @Override
            public void run() {
                if (mSummaryContainer != null) {
                    mSummaryContainer.setVisibility(View.GONE);
                }
                mFanartImageView.setVisibility(View.GONE);
                startSircleAnimation();
            }
        });
    }

    private void startSircleAnimation() {
        mAnimationLayout.setVisibility(View.VISIBLE);

        float finalRadius = Math.max(mAnimationLayout.getWidth(), mAnimationLayout.getHeight()) * 1.5f;

        SupportAnimator animator = ViewAnimationUtils.createCircularReveal(mAnimationLayout, endAnimationX, endAnimationY, 20 / 2f,
                finalRadius);
        animator.setDuration(500);
        animator.setInterpolator(ACCELERATE);
        animator.addListener(new SimpleListener() {
            @Override
            public void onAnimationEnd() {
                raiseUpAnimation();
            }
        });
        animator.start();
    }


    private void raiseUpAnimation(){
        startAnimationPairBottom = mAnimationLayout.getBottom();
        ObjectAnimator objectAnimator = ObjectAnimator.ofInt(mAnimationLayout, "bottom", mAnimationLayout.getBottom(), mAnimationLayout.getTop() + dpToPx(100));
        objectAnimator.addListener(new SimpleListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                //  appearRed();
                mAnimationLayout.setVisibility(View.GONE);
                mPresenter.initialize();

            }
        });
        objectAnimator.setInterpolator(ACCELERATE_DECELERATE);
        objectAnimator.start();
    }

    private void comeDownAnimation(){
        ObjectAnimator objectAnimator = ObjectAnimator.ofInt(mAnimationLayout, "bottom", mAnimationLayout.getBottom(), startAnimationPairBottom);
        objectAnimator.addListener(new SimpleListener() {
            @Override
            public void onAnimationEnd(Animator animator) {
                disappearBackgroundAnimation();
            }
        });
        objectAnimator.setInterpolator(ACCELERATE_DECELERATE);
        objectAnimator.start();
    }

    private void disappearBackgroundAnimation(){
        float finalRadius = Math.max(mAnimationLayout.getWidth(), mAnimationLayout.getHeight()) * 1.5f;

        SupportAnimator animator = ViewAnimationUtils.createCircularReveal(mAnimationLayout, endAnimationX, endAnimationY,
                finalRadius, 10);
        animator.setDuration(500);
        animator.addListener(new SimpleListener() {
            @Override
            public void onAnimationEnd() {
                mAnimationLayout.setVisibility(View.INVISIBLE);
            }
        });
        animator.setInterpolator(DECELERATE);
        animator.start();
    }




    public int dpToPx(int dp){
        DisplayMetrics displayMetrics = getActivity().getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
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

    public void onPrepareTrailerButton(LinearLayout layout) {
        isEnableYoutube = (mMovie != null && !MoviesCollections.isEmpty(mMovie.getTrailers()));
        //for (int i = 0; i < mTrailerButton.getChildCount(); i++) {
           // View view = mTrailerButton.getChildAt(i);
          //  layout.setEnabled(isEnableYoutube);
       // }
    }

    /**
     * Animations
     */

    private void animateSummary() {
       // mSummaryRoot.setTranslationY(-mSummaryRoot.getHeight());
       // mPosterImageView.setTranslationY(-mPosterImageView.getHeight());
        mSummaryContainer.setTranslationY(-mSummaryContainer.getHeight());
        //mFanartImageView.setTranslationY(-mFanartImageView.getHeight());
        //mRatingBarContainer.setAlpha(0);

        //mSummaryRoot.animate().translationY(0).setDuration(300).setStartDelay(100).setInterpolator(INTERPOLATOR);
        mSummaryContainer.animate().translationY(0).setDuration(300).setStartDelay(100).setInterpolator(INTERPOLATOR);
        //mPosterImageView.animate().translationY(0).setDuration(300).setStartDelay(100).setInterpolator(INTERPOLATOR);
        //mRatingBarContainer.animate().alpha(1).setDuration(200).setStartDelay(400).setInterpolator(INTERPOLATOR).start();
    }

    private void animateFanart() {
        mFanartImageView.setTranslationY(-mFanartImageView.getHeight());
        mFanartImageView.animate().translationY(0).setDuration(300).setStartDelay(100).setInterpolator(INTERPOLATOR);
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
        animateFanart();
        if (mSummaryContainer != null) {
            mSummaryContainer.setVisibility(View.VISIBLE);
            animateSummary();
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
                display.startMovieDetailActivity(String.valueOf(movie.getTmdbId()), startingLocation);
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

        if (hasTitleContainer() && mTitleTextView != null) {
            mTitleTextView.setText(mMovie.getTitle() + " (" + mMovie.getYear() + ")");
            mGenresTextView.setText(mMovie.getGenres());
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

            holder.summary.setScaleY(0);
            holder.summary.setScaleX(0);

            holder.summary.animate()
                    .scaleY(1)
                    .scaleX(1)
                    .setDuration(200)
                    .setInterpolator(INTERPOLATOR)
                    .setStartDelay(animationDelay)
                    .start();
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
            holder.title.setText(mMovie.getTitle() + " (" + mMovie.getYear() + ")");
            holder.genres.setText(mMovie.getGenres());
            String mImageBaseUrl = TmdbSettings.getImageBaseUrl(mContext)
                    + TmdbSettings.POSTER_SIZE_SPEC_W154;

            Picasso.with(mContext)
                    .load(mImageBaseUrl + mMovie.getPosterUrl())
                    .fit().
                    centerCrop().
                    into(holder.posterImageView);

            holder.ratingBar.setMax(100);
            holder.ratingBar.setProgress(mMovie.getAverageRatingPercent());
            holder.votes.setText(String.valueOf(mMovie.getRatingVotes()));
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
                    .setInterpolator(INTERPOLATOR)
                    .setStartDelay(animationDelay)
                    .start();
        }


        @Override
        public int getItemCount() {
            return 1;
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            LinearLayout container;
            AutofitTextView taglineTextView;
            TextView title;
            TextView genres;
            ImageView posterImageView;
            ArcProgress ratingBar;
            TextView votes;
            LinearLayout trailerButton;


            public ViewHolder(View view) {
                super(view);

                container = (LinearLayout) view.findViewById(R.id.container_layout);
                title = (TextView) view.findViewById(R.id.textview_title);
                genres = (TextView) view.findViewById(R.id.textview_genres);
                taglineTextView = (AutofitTextView) view.findViewById(R.id.textview_tagline);
                posterImageView = (ImageView)view.findViewById(R.id.imageview_poster);
                ratingBar = (ArcProgress) view.findViewById(R.id.rating_bar);
                votes = (TextView) view.findViewById(R.id.textview_votes);
                trailerButton = (LinearLayout) view.findViewById(R.id.trailer_container);

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
                    .setInterpolator(INTERPOLATOR)
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
                    .setInterpolator(INTERPOLATOR)
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
                    .setInterpolator(INTERPOLATOR)
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
                    .setInterpolator(INTERPOLATOR)
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

    private static class SimpleListener implements SupportAnimator.AnimatorListener, ObjectAnimator.AnimatorListener{

        @Override
        public void onAnimationStart() {
        }

        @Override
        public void onAnimationEnd() {
        }

        @Override
        public void onAnimationCancel() {
        }

        @Override
        public void onAnimationRepeat() {
        }

        @Override
        public void onAnimationStart(Animator animation) {
        }

        @Override
        public void onAnimationEnd(Animator animation) {
        }

        @Override
        public void onAnimationCancel(Animator animation) {
        }

        @Override
        public void onAnimationRepeat(Animator animation) {
        }
    }
}
