
package com.roodie.materialmovies.views.fragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.google.common.base.Preconditions;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerviewViewHolder;
import com.roodie.materialmovies.R;
import com.roodie.materialmovies.mvp.presenters.SearchPresenter;
import com.roodie.materialmovies.mvp.views.SearchView;
import com.roodie.materialmovies.mvp.views.UiView;
import com.roodie.materialmovies.util.ImeUtils;
import com.roodie.materialmovies.util.UiUtils;
import com.roodie.materialmovies.views.activities.SearchItemsActivity;
import com.roodie.materialmovies.views.custom_views.MMoviesEditText;
import com.roodie.materialmovies.views.custom_views.MMoviesImageView;
import com.roodie.materialmovies.views.custom_views.MovieDetailCardLayout;
import com.roodie.materialmovies.views.custom_views.ViewRecycler;
import com.roodie.materialmovies.views.fragments.base.BaseAnimationFragment;
import com.roodie.materialmovies.views.fragments.base.BaseDetailFragment;
import com.roodie.model.Display;
import com.roodie.model.entities.MovieWrapper;
import com.roodie.model.entities.PersonWrapper;
import com.roodie.model.entities.ShowWrapper;
import com.roodie.model.state.ApplicationState;
import com.roodie.model.state.MoviesState;
import com.roodie.model.util.FileLog;
import com.roodie.model.util.MoviesCollections;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import io.codetail.animation.SupportAnimator;
import io.codetail.animation.ViewAnimationUtils;


public class SearchFragment extends BaseDetailFragment<ApplicationState.SearchResult> implements SearchView {

    @InjectPresenter
    SearchPresenter mPresenter;

    private static final String LOG_TAG = SearchFragment.class.getSimpleName();

    private static final String KEY_SEARCH_RESULT_STATE = "search_result_state";

    static final AccelerateInterpolator ACCELERATE = new AccelerateInterpolator();
    static final DecelerateInterpolator DECELERATE = new DecelerateInterpolator();

    private static final AutoCompleteTextViewReflector HIDDEN_METHOD_INVOKER
            = new AutoCompleteTextViewReflector();

    private ApplicationState.SearchResult mSearchResult;

    @InjectView(R.id.cancel_button)
    View mCancelButton;

    @InjectView(R.id.smooth_progress_bar)
    SmoothProgressBar mProgressBar;

    @InjectView(R.id.search_edit)
    MMoviesEditText mSearchEdit;

    @InjectView(R.id.search_view_container)
    View mSearchViewContainer;

    @InjectView(R.id.up_button)
    View mUpButton;

    private String mSearchQuery;

    private SearchAdapter mAdapter;
    private MovieAdapter mMoviesAdapter;
    private PeopleAdapter mPeopleAdapter;
    private TvShowAdapter mShowsAdapter;

    private final View.OnClickListener onCancelClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            cancelSearch();
        }
    };

    private final View.OnClickListener onBackClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mSearchResult != null && !mSearchResult.query.isEmpty()) {
                setQuery(null);
                if (mPresenter != null) {
                    mPresenter.clearSearch(SearchFragment.this);
                }
            } else {
                cancelSearch();
            }
        }
    };


    public static SearchFragment newInstance() {
        Bundle bundle = new Bundle();
        SearchFragment fragment = new SearchFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(KEY_SEARCH_RESULT_STATE, mSearchResult);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            //restore
        }
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_search;
    }

    @Override
    protected void attachUiToPresenter() {
        mPresenter.onUiAttached(this, getQueryType(), null);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mSearchEdit != null) {
            mSearchEdit.clearFocus();
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        this.mUpButton.setOnClickListener(this.onCancelClicked);
        view.setOnClickListener(this.onCancelClicked);
        this.mCancelButton.setOnClickListener(this.onBackClicked);
        this.mSearchViewContainer.setVisibility(View.INVISIBLE);


        UiUtils.getInstance().attachToastPopup(getBaseActivity(), this.mUpButton);
        UiUtils.getInstance().attachToastPopup(getActivity(), this.mCancelButton);

        getRecyclerView().setVisibility(View.INVISIBLE);

        this.mSearchEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (mPresenter != null) {
                        mPresenter.search(SearchFragment.this, getQueryType(), mSearchEdit.getText().toString());
                    }

                    //Clear focus
                    return true;
                }
                return false;
            }
        });


        this.mSearchEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    //showKeyboard
                    ImeUtils.showIme(mSearchEdit);
                } else {
                    //hideKeyboard
                    ImeUtils.hideIme(mSearchEdit);

                }
            }
        });

        this.mSearchViewContainer.post(new Runnable() {
            @Override
            public void run() {
                float finalRadius = Math.max(mSearchViewContainer.getWidth(), mSearchViewContainer.getHeight()) * 1.5f;

                SupportAnimator animator = ViewAnimationUtils.createCircularReveal(mSearchViewContainer, mSearchViewContainer.getRight(), mSearchViewContainer.getTop(), mCancelButton.getWidth() / 2f,
                        finalRadius);
                animator.setDuration(500);
                animator.setInterpolator(ACCELERATE);
                animator.addListener(new BaseAnimationFragment.SimpleAnimationListener() {
                    @Override
                    public void onAnimationEnd() {
                        FileLog.d("search", "On animation ended");
                        mSearchEdit.setFocusableInTouchMode(true);
                        mSearchEdit.requestFocus();
                        ImeUtils.showIme(mSearchEdit);
                    }

                    @Override
                    public void onAnimationStart() {
                        mSearchViewContainer.setVisibility(View.VISIBLE);
                    }
                });
                animator.start();
            }
        });

    }

    private void setQuery(String query) {
        if (mSearchEdit != null) {
            this.mSearchEdit.setText(query);
            mSearchQuery = null;
        } else {
            mSearchQuery = query;
        }
    }

    public boolean cancelSearch() {
        boolean close = false;
        if (this.mSearchViewContainer.getVisibility() == View.VISIBLE) {
            setQuery(null);
            if (mPresenter != null) {
                mPresenter.clearSearch(this);
            }

            this.mSearchViewContainer.post(new Runnable() {
                @Override
                public void run() {
                    float startRadius = Math.max(mSearchViewContainer.getWidth(), mSearchViewContainer.getHeight()) * 1.5f;

                    SupportAnimator animator = ViewAnimationUtils.createCircularReveal(mSearchViewContainer, mSearchViewContainer.getRight(), mSearchViewContainer.getTop(),
                            startRadius, 10);
                    animator.setDuration(500);
                    animator.addListener(new BaseAnimationFragment.SimpleAnimationListener() {
                        @Override
                        public void onAnimationEnd() {
                            mSearchViewContainer.setVisibility(View.INVISIBLE);
                            finish();
                        }

                        @Override
                        public void onAnimationStart() {
                            mSearchViewContainer.setOnClickListener(null);
                            ImeUtils.hideIme(mSearchEdit);
                        }
                    });
                    animator.setInterpolator(DECELERATE);
                    animator.start();
                }
            });
            close = true;
        }
        return close;
    }

    /**
     * SearchView
     */
    @Override
    public void showLoadingProgress(boolean visible) {
        if (visible) {
            this.mProgressBar.setVisibility(View.VISIBLE);
        } else {
            this.mProgressBar.setVisibility(View.GONE);
        }
    }

    public void onScrolledToBottom() {
        //NTD
    }

    @Override
    public void onRefreshData(boolean visible) {
        //NTD
    }

    @Override
    public void updateDisplayTitle(String title) {
      //NTD
    }

    @Override
    public void updateDisplaySubtitle(String subtitle) {
        //NTD
    }

    @Override
    public void setData(MoviesState.SearchResult data) {

        if (data != null && mSearchEdit != null) {
            mSearchEdit.clearFocus();
        }
        mSearchResult = data;
        setQuery(mSearchResult != null ? mSearchResult.query : null);

        mAdapter = populateUi();
        if (mAdapter != null) {
            getRecyclerView().setAdapter(mAdapter);
            getRecyclerView().setVisibility(View.VISIBLE);
            this.mProgressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public UiView.MMoviesQueryType getQueryType() {
        return UiView.MMoviesQueryType.SEARCH;
    }

    @Override
    public void showMovieDetail(MovieWrapper movie,View view){
        Preconditions.checkNotNull(movie, "movie cannot be null");

        Display display = getDisplay();
        if (display != null) {
            if (movie.getTmdbId() != null) {
                display.startMovieDetailActivity(String.valueOf(movie.getTmdbId()), null);
            }
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
            //TODO
            display.startPersonDetailActivity(String.valueOf(person.getTmdbId()), null);
        }
    }

    @Override
    public void showTvShowDetail(ShowWrapper tvShow, View view) {
        Preconditions.checkNotNull(tvShow, "tv cannot be null");

        Display display = getDisplay();
        if (display != null) {
            if (tvShow.getTmdbId() != null) {
                display.startTvDetailActivity(String.valueOf(tvShow.getTmdbId()), null);
            }
        }
    }

    private void finish() {
        getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
    }

    private SearchAdapter populateUi() {
        if (mSearchResult == null) {
            //set empty text
            Toast.makeText(getActivity().getApplicationContext(), "searchResult == null", Toast.LENGTH_LONG);
            return null;
        }

        final ArrayList<SearchCategoryItems> items = new ArrayList<>();

        if (mSearchResult.movies != null && !MoviesCollections.isEmpty(mSearchResult.movies.items)) {
            items.add(SearchCategoryItems.MOVIES);
        }

        if (mSearchResult.shows != null && !MoviesCollections.isEmpty(mSearchResult.shows.items)) {
            items.add(SearchCategoryItems.SHOWS);
        }

        if (mSearchResult.people != null && !MoviesCollections.isEmpty(mSearchResult.people.items)) {
            items.add(SearchCategoryItems.PEOPLE);
        }

        return createRecyclerAdapter(items);
    }

    private SearchAdapter createRecyclerAdapter(List<SearchCategoryItems> items) {
        return new SearchAdapter(items);
    }

    private static class AutoCompleteTextViewReflector {
        private Method showSoftInputUnchecked;

        AutoCompleteTextViewReflector() {
            try {
                showSoftInputUnchecked = InputMethodManager.class.getMethod(
                        "showSoftInputUnchecked", int.class, ResultReceiver.class);
                showSoftInputUnchecked.setAccessible(true);
            } catch (NoSuchMethodException e) {
                // Ah well.
            }
        }

        void showSoftInputUnchecked(InputMethodManager imm, View view, int flags) {
            if (showSoftInputUnchecked != null) {
                try {
                    showSoftInputUnchecked.invoke(imm, flags, null);
                    return;
                } catch (Exception e) {
                }
            }

            // Hidden method failed, call public version instead
            imm.showSoftInput(view, flags);
        }
    }

    private enum SearchCategoryItems {
        MOVIES,
        PEOPLE,
        SHOWS
    }

    /**
     * SearchAdapter
     */
    public class SearchAdapter extends EnumListDetailAdapter<SearchCategoryItems> {
        List<BaseViewHolder> mItems;

        public SearchAdapter(List<SearchCategoryItems> items) {
            mItems = new ArrayList<>(items.size());
            for(SearchCategoryItems item : items) {
                switch (item) {
                    case MOVIES:
                        mItems.add(new MoviesResultsBinder(this));
                        break;
                    case PEOPLE:
                        mItems.add(new PeopleResultsBinder(this));
                        break;
                    case SHOWS:
                        mItems.add(new ShowsResultsBinder(this));
                        break;
                }
            }
            addAllBinder(mItems);
        }

    }

    /**
     *  MoviesResultsBinder
     */
    public class MoviesResultsBinder extends BaseViewHolder<MoviesResultsBinder.ViewHolder> {

        MovieDetailCardLayout cardLayout;

        public MoviesResultsBinder(BaseDetailAdapter dataBindAdapter) {
            super(dataBindAdapter);
            Log.d(LOG_TAG, "Bind movies");
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

            cardLayout.setTitle(R.string.movies_title);

            final View.OnClickListener seeMoreClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getDisplay() != null) {
                        getDisplay().startSearchListActivity(SearchItemsActivity.MOVIE_TEMS, null);
                    }
                }
            };

            final ViewRecycler viewRecycler = new ViewRecycler(holder.layout);
            viewRecycler.recycleViews();

            if (!getMoviesAdapter().isEmpty()) {
                final int numItems = getResources().getInteger(R.integer.detail_card_max_items);
                final int adapterCount = getMoviesAdapter().getCount();

                for (int i = 0; i < Math.min(numItems, adapterCount); i++) {
                    View view = getMoviesAdapter().getView(i, viewRecycler.getRecycledView(), holder.layout);
                    holder.layout.addView(view);
                }
                final boolean showSeeMore = numItems < getMoviesAdapter().getCount();
                cardLayout.setSeeMoreVisibility(showSeeMore);
                cardLayout.setSeeMoreOnClickListener(showSeeMore ? seeMoreClickListener : null);

            }
            viewRecycler.clearRecycledViews();

        }

        @Override
        public int getItemCount() {
            return 1;
        }

        class ViewHolder extends UltimateRecyclerviewViewHolder {

            ViewGroup layout;

            public ViewHolder(View view) {
                super(view);
                layout = (ViewGroup) view.findViewById(R.id.card_content);
            }
        }
    }

    /**
     *  ResultsBinder
     */
    public class PeopleResultsBinder extends BaseViewHolder<PeopleResultsBinder.ViewHolder> {

        MovieDetailCardLayout cardLayout;

        public PeopleResultsBinder(BaseDetailAdapter dataBindAdapter) {
            super(dataBindAdapter);
            Log.d(LOG_TAG, "Bind people");
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

            cardLayout.setTitle(R.string.people_title);

            final View.OnClickListener seeMoreClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getDisplay() != null) {
                        getDisplay().startSearchListActivity(SearchItemsActivity.PERSON_TEMS, null);
                    }
                }
            };
            final ViewRecycler viewRecycler = new ViewRecycler(holder.layout);
            viewRecycler.recycleViews();

            if (!getPeopleAdapter().isEmpty()) {
                final int numItems = getResources().getInteger(R.integer.detail_card_max_items);
                final int adapterCount = getPeopleAdapter().getCount();

                for (int i = 0; i < Math.min(numItems, adapterCount); i++) {
                    View view = getPeopleAdapter().getView(i, viewRecycler.getRecycledView(), holder.layout);
                    holder.layout.addView(view);
                }
                final boolean showSeeMore = numItems < getPeopleAdapter().getCount();
                cardLayout.setSeeMoreVisibility(showSeeMore);
                cardLayout.setSeeMoreOnClickListener(showSeeMore ? seeMoreClickListener : null);

            }
            viewRecycler.clearRecycledViews();


        }

        @Override
        public int getItemCount() {
            return 1;
        }

        class ViewHolder extends UltimateRecyclerviewViewHolder {

            ViewGroup layout;

            public ViewHolder(View view) {
                super(view);
                layout = (ViewGroup) view.findViewById(R.id.card_content);
            }
        }
    }

    /**
     *  ShowsResultsBinder
     */
    public class ShowsResultsBinder extends BaseViewHolder<ShowsResultsBinder.ViewHolder> {

        MovieDetailCardLayout cardLayout;

        public ShowsResultsBinder(BaseDetailAdapter dataBindAdapter) {
            super(dataBindAdapter);
            Log.d(LOG_TAG, "Bind shows");
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

            cardLayout.setTitle(R.string.shows_title);

            final View.OnClickListener seeMoreClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getDisplay() != null) {
                        getDisplay().startSearchListActivity(SearchItemsActivity.SHOW_TEMS, null);
                    }
                }
            };
            final ViewRecycler viewRecycler = new ViewRecycler(holder.layout);
            viewRecycler.recycleViews();

            if (!getShowsAdapter().isEmpty()) {
                final int numItems = getResources().getInteger(R.integer.detail_card_max_items);
                final int adapterCount = getShowsAdapter().getCount();

                for (int i = 0; i < Math.min(numItems, adapterCount); i++) {
                    View view = getShowsAdapter().getView(i, viewRecycler.getRecycledView(), holder.layout);
                    holder.layout.addView(view);
                }
                final boolean showSeeMore = numItems < getShowsAdapter().getCount();
                cardLayout.setSeeMoreVisibility(showSeeMore);
                cardLayout.setSeeMoreOnClickListener(showSeeMore ? seeMoreClickListener : null);

            }
            viewRecycler.clearRecycledViews();


        }

        @Override
        public int getItemCount() {
            return 1;
        }

        class ViewHolder extends UltimateRecyclerviewViewHolder {

            ViewGroup layout;

            public ViewHolder(View view) {
                super(view);
                layout = (ViewGroup) view.findViewById(R.id.card_content);
            }
        }
    }

    /**
     * MoviesAdapter
     */
    private class MovieAdapter extends BaseAdapter {

        private final LayoutInflater mInflater;
        private String mImageUrl;

        MovieAdapter(LayoutInflater inflater) {
            mInflater = inflater;
        }

        @Override
        public int getCount() {
            if (mSearchResult != null) {
                if (mSearchResult.movies != null) {
                    return MoviesCollections.size(mSearchResult.movies.items);
                }
            }
            return 0;
        }

        @Override
        public MovieWrapper getItem(int position) {
            return mSearchResult.movies.items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = mInflater.inflate(getLayoutId(), viewGroup, false);
            }

            final MovieWrapper item = getItem(position);

            final TextView title = (TextView) view.findViewById(R.id.title);
            final MMoviesImageView imageView =
                    (MMoviesImageView) view.findViewById(R.id.imageview_poster);

            if (item.getYear() > 0) {
                title.setText(getString(R.string.movie_title_year,item.getTitle(), item.getYear()));
            } else {
                title.setText(item.getTitle());
            }


            imageView.loadPoster(item, new MMoviesImageView.OnLoadedListener() {
                @Override
                public void onSuccess(MMoviesImageView imageView, Bitmap bitmap, String imageUrl) {
                    mImageUrl = imageUrl;
                }

                @Override
                public void onError(MMoviesImageView imageView) {

                }
            });

            view.setTag(mImageUrl);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showMovieDetail(item, imageView);
                }
            });

            return view;
        }

        protected int getLayoutId() {
            return R.layout.item_movie_detail_list_1line;
        }
    }

    /**
     * PeopleAdapter
     */
    private class PeopleAdapter extends BaseAdapter {
        private final LayoutInflater mInflater;
        private String mImageUrl;

        PeopleAdapter(LayoutInflater inflater) {
            mInflater = inflater;
        }

        @Override
        public int getCount() {
            if (mSearchResult != null) {
                if (mSearchResult.people != null) {
                    return MoviesCollections.size(mSearchResult.people.items);
                }
            }
            return 0;
        }

        @Override
        public PersonWrapper getItem(int position) {
            return mSearchResult.people.items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = mInflater.inflate(getLayoutId(), viewGroup, false);
            }

            final PersonWrapper item = getItem(position);

            final TextView title = (TextView) view.findViewById(R.id.title);
            final MMoviesImageView imageView =
                    (MMoviesImageView) view.findViewById(R.id.imageview_poster);

            if (!TextUtils.isEmpty(item.getName()) ) {
                title.setText(item.getName());
            }

            imageView.loadProfile(item, new MMoviesImageView.OnLoadedListener() {
                @Override
                public void onSuccess(MMoviesImageView imageView, Bitmap bitmap, String imageUrl) {
                    mImageUrl = imageUrl;
                }

                @Override
                public void onError(MMoviesImageView imageView) {

                }
            });

            view.setTag(mImageUrl);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showPersonDetail(item, imageView);
                }
            });

            return view;
        }

        protected int getLayoutId() {
            return R.layout.item_movie_detail_list_1line;
        }
    }

    /**
     * TvShowAdapter
     */
    private class TvShowAdapter extends BaseAdapter {

        private final LayoutInflater mInflater;

        TvShowAdapter(LayoutInflater inflater) {
            mInflater = inflater;
        }

        @Override
        public int getCount() {
            if (mSearchResult != null) {
                if (mSearchResult.shows != null) {
                    return MoviesCollections.size(mSearchResult.shows.items);
                }
            }
            return 0;
        }

        @Override
        public ShowWrapper getItem(int position) {
            return mSearchResult.shows.items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = mInflater.inflate(getLayoutId(), viewGroup, false);
            }

            final ShowWrapper item = getItem(position);

            final TextView title = (TextView) view.findViewById(R.id.title);
            final MMoviesImageView imageView =
                    (MMoviesImageView) view.findViewById(R.id.imageview_poster);

            if (item.getYear() > 0) {
                title.setText(getString(R.string.movie_title_year,item.getTitle(), item.getYear()));
            } else {
                title.setText(item.getTitle());
            }

            imageView.loadPoster(item);

            view.setTag(item);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   showTvShowDetail(item, imageView);
                }
            });

            return view;
        }

        protected int getLayoutId() {
            return R.layout.item_movie_detail_list_1line;
        }
    }

    public MovieAdapter getMoviesAdapter() {
        if(mMoviesAdapter == null) {
            mMoviesAdapter = new MovieAdapter(LayoutInflater.from(getActivity()));
        }
        return mMoviesAdapter;
    }

    public PeopleAdapter getPeopleAdapter() {
        if(mPeopleAdapter == null) {
            mPeopleAdapter = new PeopleAdapter(LayoutInflater.from(getActivity()));
        }
        return mPeopleAdapter;
    }

    public TvShowAdapter getShowsAdapter() {
        if(mShowsAdapter == null) {
            mShowsAdapter = new TvShowAdapter(LayoutInflater.from(getActivity()));
        }
        return mShowsAdapter;
    }
}