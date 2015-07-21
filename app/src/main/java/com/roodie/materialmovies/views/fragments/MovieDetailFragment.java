package com.roodie.materialmovies.views.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.common.base.Preconditions;
import com.roodie.materialmovies.R;
import com.roodie.materialmovies.mvp.presenters.MovieDetailPresenter;
import com.roodie.materialmovies.views.MMoviesApplication;
import com.roodie.materialmovies.views.custom_views.MovieDetailCardLayout;
import com.roodie.materialmovies.views.fragments.base.BaseDetailFragment;
import com.roodie.model.entities.MovieCreditWrapper;
import com.roodie.model.entities.MovieWrapper;
import com.roodie.model.network.NetworkError;
import com.roodie.model.util.MoviesCollections;

import java.util.ArrayList;

/**
 * Created by Roodie on 27.06.2015.
 */
public class MovieDetailFragment extends BaseDetailFragment implements MovieDetailPresenter.MovieDetailView, View.OnClickListener, AbsListView.OnScrollListener {

    private static final String LOG_TAG = MovieDetailFragment.class.getSimpleName();

    private MovieDetailPresenter mPresenter;


    private MovieWrapper mMovie;

    private final ArrayList<DetailItemType> mItems = new ArrayList<>();

    private static final String QUERY_MOVIE_ID = "movie_id";

    public static MovieDetailFragment newInstance(String movieId) {
        Preconditions.checkArgument(!TextUtils.isEmpty(movieId), "MovieId can not be null");

        Bundle bundle = new Bundle();
        bundle.putString(QUERY_MOVIE_ID, movieId);
        MovieDetailFragment fragment = new MovieDetailFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MMoviesApplication.from(getActivity()).inject(this);
        setHasOptionsMenu(true);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
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
        mPresenter.attachView(this);
        mPresenter.initialize();

        getListView().setOnScrollListener(this);
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

    /**
     * MovieDetailView
     */
    @Override
    public void setMovie(MovieWrapper movie) {
        mMovie = movie;
    }


    /**
     *
     * OnScrollListener
     */
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }

    @Override
    public boolean isModal() {
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageview_poster: {
                getPresenter().showMovieImages(mMovie);
            }
            break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d(LOG_TAG, "OnItemPositionClicked: " + position);
        if ( getListAdapter().getItem(position) == DetailItemType.BACKDROP_IMAGES) {
            getPresenter().showMovieImages(mMovie);
        }
    }

    @Override
    protected DetailAdapter createListAdapter() {
        return new DetailAdapter();
    }

    @Override
    protected DetailAdapter getListAdapter() {
        return (DetailAdapter)super.getListAdapter();
    }


    private void populateUi() {
        if (mMovie == null) {
            return;
        }
        mItems.clear();

        mItems.add(DetailItemType.TITLE);

        if (!TextUtils.isEmpty(mMovie.getOverview())) {
            mItems.add(DetailItemType.SUMMARY);
        }

        mItems.add(DetailItemType.DETAILS);

        if (!MoviesCollections.isEmpty(mMovie.getTrailers())) {
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

      //  if (hasBigPosterView()) {
      //      getBigPosterView().loadPoster(mMovie, mPosterListener);
      //  }

        getListAdapter().setItems(mItems);
    }

    private enum DetailItemType implements DetailType {
        BACKDROP_IMAGES(R.layout.item_movie_backdrop_spacing),
        TITLE(R.layout.item_movie_detail_title), //includes poster, tagline and rating
        DETAILS(R.layout.item_movie_detail_details) ,//include details
        SUMMARY(R.layout.item_movie_detail_summary), //includes description text, maybe
        TRAILERS(R.layout.item_movie_detail_trailers),// includes trailers
        RELATED(R.layout.item_movie_detail_generic_card),// includes related movies list
        CAST(R.layout.item_movie_detail_generic_card), /// includes cast list
        CREW(R.layout.item_movie_detail_generic_card); // includes crew list

        private final int mLayoutId;

        DetailItemType(int mLayoutId) {
            this.mLayoutId = mLayoutId;
        }


        @Override
        public int getLayoutId() {
            return mLayoutId;
        }

        @Override
        public int getViewType() {
            return RELATED.ordinalId();
        }

        @Override
        public boolean isEnabled() {
            return false;
        }
    }


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

            final ImageView image = (ImageView) convertView.findViewById(R.id.poster);
            //Load with Picasso

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

    private class MovieCastAdapter extends BaseMovieCastAdapter {

        public MovieCastAdapter(LayoutInflater mInflater) {
            super(mInflater, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MovieCreditWrapper cast = (MovieCreditWrapper) v.getTag();
                    if (cast != null) {
                       getPresenter().showPersonDetail(cast.getPerson(), null);
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

    private class MovieCrewAdapter extends BaseMovieCastAdapter {

        public MovieCrewAdapter(LayoutInflater mInflater) {
            super(mInflater, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MovieCreditWrapper cast = (MovieCreditWrapper) v.getTag();
                    if (cast != null) {
                        getPresenter().showPersonDetail(cast.getPerson(), null);
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



    private class RelatedMoviesAdapter extends BaseAdapter {

        private final View.OnClickListener mItemOnClickListener;
        private final LayoutInflater mInflater;

        public RelatedMoviesAdapter( LayoutInflater mInflater) {
            this.mInflater = mInflater;

            this.mItemOnClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   getPresenter().showMovieDetail((MovieWrapper) v.getTag(),
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

            final TextView title = (TextView) convertView.findViewById(R.id.textview_title);
            if (movie.getYear() > 0) {
                title.setText(getString(R.string.movie_title_year,
                        movie.getTmdbTitle(), movie.getYear()));
            } else {
                title.setText(movie.getTmdbTitle());
            }

            final ImageView imageView =
                    (ImageView) convertView.findViewById(R.id.imageview_poster);

            convertView.setOnClickListener(mItemOnClickListener);
            convertView.setTag(movie);

            return convertView;
        }

        protected int getLayoutId() {
            return R.layout.item_movie_detail_list_1line;
        }
    }




    private class DetailAdapter extends BaseDetailAdapter<DetailItemType> {

        private RelatedMoviesAdapter mRelatedMoviesAdapter;
        private MovieCastAdapter mMovieCastAdapter;
        private MovieCrewAdapter mMovieCrewAdapter;

        @Override
        protected void bindView(DetailItemType item, View view) {
            Log.d(LOG_TAG, "Bind view: " + item.name());

            switch(item) {
                case TITLE:
                    break;
                case RELATED:
                    bindRelated(view);
                    break;
                case CAST:
                    bindCast(view);
                    break;
                case CREW:
                    bindCrew(view);
                    break;
            }

            view.setTag(item);
        }

        private void bindRelated(View view) {
            Log.d(LOG_TAG, "Bind related.");

            final View.OnClickListener seeMoreClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   getPresenter().showRelatedMovies(mMovie);
                }
            };

            MovieDetailCardLayout cardLayout = (MovieDetailCardLayout) view;
            cardLayout.setTitle(R.string.related_movies);
            populateDetailGrid((ViewGroup) view.findViewById(R.id.card_content),
                    cardLayout,
                    seeMoreClickListener,
                    getRelatedMoviesAdapter()
            );

        }

        private void bindCast(View view) {
            Log.d(LOG_TAG, "Bind cast");

            final View.OnClickListener seeMoreClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getPresenter().showCastList(mMovie);
                }
            };

            MovieDetailCardLayout cardLayout = (MovieDetailCardLayout) view;
            cardLayout.setTitle(R.string.cast_movies);

            populateDetailGrid((ViewGroup) view.findViewById(R.id.card_content),
                    cardLayout,
                    seeMoreClickListener,
                    getMovieCastAdapter());
        }


        private void bindCrew(View view) {
            Log.d(LOG_TAG, "Bind crew");

            final View.OnClickListener seeMoreClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   getPresenter().showCrewList(mMovie);
                }
            };

            MovieDetailCardLayout cardLayout = (MovieDetailCardLayout) view;
            cardLayout.setTitle(R.string.crew_movies);

            populateDetailGrid((ViewGroup) view.findViewById(R.id.card_content),
                    cardLayout,
                    seeMoreClickListener,
                    getMovieCrewAdapter());
        }



        private RelatedMoviesAdapter getRelatedMoviesAdapter() {
            if (mRelatedMoviesAdapter == null) {
                mRelatedMoviesAdapter = new RelatedMoviesAdapter(LayoutInflater.from(getActivity()));
        }
        return  mRelatedMoviesAdapter;
        }

        private MovieCastAdapter getMovieCastAdapter() {
            if (mMovieCastAdapter == null) {
                mMovieCastAdapter = new MovieCastAdapter(LayoutInflater.from(getActivity()));
            }
            return  mMovieCastAdapter;
        }

        private MovieCrewAdapter getMovieCrewAdapter() {
            if (mMovieCrewAdapter == null) {
                mMovieCrewAdapter = new MovieCrewAdapter(LayoutInflater.from(getActivity()));
            }
            return  mMovieCrewAdapter;
        }


    }

    @Override
    protected void setSupportActionBar(Toolbar toolbar) {
       setSupportActionBar(toolbar, false);
    }
}
