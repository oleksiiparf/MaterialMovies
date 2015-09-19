package com.roodie.materialmovies.views.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.google.common.base.Preconditions;
import com.roodie.materialmovies.R;
import com.roodie.materialmovies.mvp.presenters.SearchPresenter;
import com.roodie.materialmovies.util.AnimationUtils;
import com.roodie.materialmovies.util.UiUtils;
import com.roodie.materialmovies.views.MMoviesApplication;
import com.roodie.materialmovies.views.activities.SettingsActivity;
import com.roodie.materialmovies.views.custom_views.MMoviesImageView;
import com.roodie.materialmovies.views.custom_views.MovieDetailCardLayout;
import com.roodie.materialmovies.views.custom_views.TvShowDialogView;
import com.roodie.materialmovies.views.custom_views.ViewRecycler;
import com.roodie.materialmovies.views.fragments.base.BaseDetailFragment;
import com.roodie.model.Display;
import com.roodie.model.entities.MovieWrapper;
import com.roodie.model.entities.PersonWrapper;
import com.roodie.model.entities.ShowWrapper;
import com.roodie.model.network.NetworkError;
import com.roodie.model.state.ApplicationState;
import com.roodie.model.state.MoviesState;
import com.roodie.model.util.MoviesCollections;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Roodie on 20.08.2015.
 */
public class SearchFragment extends BaseDetailFragment implements SearchPresenter.SearchView {

    private static final String LOG_TAG = SearchFragment.class.getSimpleName();

    private static final String KEY_SEARCH_RESULT_STATE = "search_result_state";

    private static final AutoCompleteTextViewReflector HIDDEN_METHOD_INVOKER
            = new AutoCompleteTextViewReflector();

    private SearchPresenter mPresenter;

    private boolean hasPresenter() {
        return mPresenter != null;
    }


    private ApplicationState.SearchResult mSearchResult;

    private EditText mEditText;
    private ImageButton mUpButton;
    private ImageButton mCancelButton;

    private InputMethodManager mInputMethodManager;
    private View mSearchView;
    private String mSearchQuery;

    private SearchAdapter mAdapter;
    private MovieAdapter mMoviesAdapter;
    private PeopleAdapter mPeopleAdapter;
    private TvShowAdapter mShowsAdapter;

    private final Handler mHandler = new Handler();

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mPresenter = MMoviesApplication.from(activity.getApplicationContext()).getSearchPresenter();
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(LOG_TAG, "On create view");
        return inflater.inflate(R.layout.fragment_search_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Log.d(LOG_TAG, "On view created");
        super.onViewCreated(view, savedInstanceState);
        mPresenter.attachView(this);
        mPresenter.initialize();
    }

    @Override
    public void onResume() {
        Log.d(LOG_TAG, "On resume");

        Toolbar toolbar = getToolbar();
        if (toolbar != null) {
            configureToolbar(toolbar);
        }
        mPresenter.onResume();
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.d(LOG_TAG, "On pause");

        getToolbar().setVisibility(View.GONE);
        ((ViewGroup)mSearchView.getParent()).removeView(mSearchView);
        mPresenter.onPause();

        super.onPause();
    }

    @Override
    public void onUiAttached() {
    }

    private void configureToolbar(@NonNull Toolbar toolbar) {

        toolbar.setVisibility(View.GONE);
        ViewGroup localViewGroup = (ViewGroup) toolbar.getParent();
        int index = localViewGroup.indexOfChild(toolbar);
        if (mSearchView == null) {
            mSearchView = LayoutInflater.from(getBaseActivity()).inflate(R.layout.include_searchview, localViewGroup, false);
        }
        localViewGroup.addView(mSearchView, index);

        mEditText = (EditText) mSearchView.findViewById(R.id.search_edt);
        mUpButton = (ImageButton) mSearchView.findViewById(R.id.up_button);
        mCancelButton = (ImageButton) mSearchView.findViewById(R.id.cancel_btn);

        UiUtils.attachToastPopup(getBaseActivity(), mUpButton);
        UiUtils.attachToastPopup(getActivity(), mCancelButton);


        mEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity().getApplicationContext(), "edit text clicked", Toast.LENGTH_LONG).show();
            }
        });

        //mInputMethodManager = ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE));
        mEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    Toast.makeText(getActivity().getApplicationContext(), "got the focus", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "lost the focus", Toast.LENGTH_LONG).show();

                }
            }
        });

        mEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (hasPresenter()) {
                        mPresenter.search(mEditText.getText().toString());
                    }
                }
                return false;
            }
        });


        mUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity().getApplicationContext(), "UpButton", Toast.LENGTH_LONG).show();
                //finish();

            }
        });
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "CancelButton", Toast.LENGTH_LONG).show();

                 setQuery(null);
                  if (hasPresenter()) {
                    mPresenter.clearSearch();
                }
                if (mSearchQuery == null) {
                  //  finish();
                }
            }
        });
        mHandler.post(mShowImeRunnable);
    }

    private void finish() {
        getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
    }


    private void setQuery(String query) {
        if (mSearchView != null) {
            mEditText.setText(query);
            mSearchQuery = null;
        } else {
            mSearchQuery = query;
        }
    }

    private  SearchAdapter populateUi() {
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

        if (MoviesCollections.isEmpty(items)) {
           // setEmptyText(R.string.search_empty_no_results);
        }

        Toast.makeText(getActivity().getApplicationContext(), "searchResult != null", Toast.LENGTH_LONG);

        return createRecyclerAdapter(items);
    }

    private SearchAdapter createRecyclerAdapter(List<SearchCategoryItems> items) {
        return new SearchAdapter(items);
    }

    @Override
    protected void setUpVisibility() {

    }

    @Override
    protected void initializePresenter() {

    }

    @Override
    public MovieQueryType getQueryType() {
        return MovieQueryType.SEARCH;
    }

    @Override
    public boolean isModal() {
        return false;
    }

    /**
     * SearchView
     *
     */
    @Override
    public void setSearchResult(MoviesState.SearchResult result) {
        mSearchResult = result;
        setQuery(mSearchResult != null ? mSearchResult.query : null);

        mAdapter = populateUi();
        getRecyclerView().setAdapter(mAdapter);
        getRecyclerView().setVisibility(View.VISIBLE);
    }

    @Override
    public void setItems(List list) {
        //TODO
    }

    /**
     * MovieView
     *
     */
    @Override
    public void showError(NetworkError error) {

    }

    @Override
    public void showLoadingProgress(boolean visible) {

    }

    @Override
    public void showSecondaryLoadingProgress(boolean visible) {

    }

    @Override
    public String getRequestParameter() {
        return null;
    }

    @Override
    public void updateDisplayTitle(String title) {

    }

    public SearchPresenter getPresenter() {
        return mPresenter;
    }

    @Override
    public String getSubtitle() {
        if (hasPresenter()) {
            return getPresenter().getUiSubTitle();
        }
        return  null;
    }

    @Override
    public String getTitle() {
        if (hasPresenter()) {
            return getPresenter().getUiTitle();
        }
        return  null;
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

    private final Runnable mShowImeRunnable = new Runnable() {
        public void run() {
            InputMethodManager imm = (InputMethodManager)
                    getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

            if (mSearchResult == null && imm != null) {
                mEditText.requestFocus();
                HIDDEN_METHOD_INVOKER.showSoftInputUnchecked(imm, mSearchView, 0);
            }
        }
    };


    @Override
    public void showMovieDetail(MovieWrapper movie,View view){
        Preconditions.checkNotNull(movie, "movie cannot be null");
        int[] startingLocation = new int[2];
        view.getLocationOnScreen(startingLocation);
        startingLocation[0] += view.getWidth() / 2;
        startingLocation[1] += view.getHeight() / 2;

        Display display = getDisplay();
        if (display != null) {
            if (movie.getTmdbId() != null) {
                display.startSearchDetailActivity(String.valueOf(movie.getTmdbId()), Display.SearchMediaType.MOVIES);
               // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                //    System.out.println("Start by shared element");
                //    display.startMovieDetailActivityBySharedElements(String.valueOf(movie.getTmdbId()), view, (String) view.getTag());
               // } else {
                //    System.out.println("Start by animation");
                //    display.startMovieDetailActivityByAnimation(String.valueOf(movie.getTmdbId()), startingLocation);
              //  }
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
           // display.startPersonDetailActivity(String.valueOf(person.getTmdbId()), startingLocation);
            display.startSearchDetailActivity(String.valueOf(person.getTmdbId()), Display.SearchMediaType.PEOPLE);

        }

    }

    @Override
    public void showTvShowDetail(ShowWrapper show, View view) {
        Preconditions.checkNotNull(show, "show cannot be null");
        Preconditions.checkNotNull(show.getTmdbId(), "show id cannot be null");

        int[] startingLocation = new int[2];
        view.getLocationOnScreen(startingLocation);
        startingLocation[0] += view.getWidth() / 2;

        Display display = getDisplay();
        if (display != null) {
            // display.startPersonDetailActivity(String.valueOf(person.getTmdbId()), startingLocation);
            display.startSearchDetailActivity(String.valueOf(show.getTmdbId()), Display.SearchMediaType.SHOWS);
        }
    }

    @Override
    public void showTvShowDialog(final ShowWrapper tvShow) {

        // View localView = View.inflate(getActivity())
        final TvShowDialogView dialogView = new TvShowDialogView(getActivity());


        MaterialDialog localMaterialDialog = new MaterialDialog.Builder(getActivity())
                .title(tvShow.getTitle())
                .autoDismiss(false)
                .customView(dialogView, true)
                .theme(SettingsActivity.THEME == R.style.Theme_MMovies_Light ? Theme.LIGHT : Theme.DARK)
                .negativeText(getActivity().getString(R.string.close_dialog_window)).callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        super.onNegative(dialog);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            AnimationUtils.hideImageCircular(dialogView, dialog);
                        }
                    }
                })
                .build();

        localMaterialDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            public void onShow(DialogInterface paramDialogInterface) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    AnimationUtils.revealImageCircular(dialogView);
                    return;
                }
                dialogView.setVisibility(View.VISIBLE);
            }
        });
        localMaterialDialog.show();

        dialogView.setSummary(tvShow.getOverview());
        dialogView.getCoverImageView().loadPoster(tvShow);
        dialogView.setYear(String.valueOf(tvShow.getFirstAirDate()));
        dialogView.setRating(tvShow.getAverageRatingPercent() + "%");
        dialogView.getLikeButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        dialogView.getShareButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getDisplay() != null) {
                    getDisplay().shareTvShow(tvShow.getTmdbId(), tvShow.getTitle());
                }
            }
        });


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

            final View.OnClickListener seeMoreClickListener = new OnSearchItemClickListener(Display.SearchMediaType.MOVIES);

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

        class ViewHolder extends RecyclerView.ViewHolder {

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

            final View.OnClickListener seeMoreClickListener = new OnSearchItemClickListener(Display.SearchMediaType.PEOPLE);

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

        class ViewHolder extends RecyclerView.ViewHolder {

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

            final View.OnClickListener seeMoreClickListener = new OnSearchItemClickListener(Display.SearchMediaType.SHOWS);

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

        class ViewHolder extends RecyclerView.ViewHolder {

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
                    (MMoviesImageView) view.findViewById(R.id.poster);

            if (item.getYear() > 0) {
                title.setText(getString(R.string.movie_title_year,item.getTitle(), item.getYear()));
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
            view.setOnClickListener(new OnSearchItemClickListener(item.getTmdbId(), Display.SearchMediaType.MOVIES, imageView));

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
                    (MMoviesImageView) view.findViewById(R.id.poster);

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
            view.setOnClickListener(new OnSearchItemClickListener(item.getTmdbId(), Display.SearchMediaType.PEOPLE, imageView));



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
                    (MMoviesImageView) view.findViewById(R.id.poster);

            if (!TextUtils.isEmpty(item.getTitle()) ) {
                title.setText(getString(R.string.movie_title_year, item.getTitle(), item.getFirstAirDate()));
            }

            imageView.loadPoster(item);

            view.setTag(item);
            view.setOnClickListener(new OnSearchItemClickListener(item.getTmdbId(), Display.SearchMediaType.PEOPLE, imageView));

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

    public  class OnSearchItemClickListener implements View.OnClickListener {

        private final int mItemTmdbId;
        private final Display.SearchMediaType queryType;
        private final View view;

        public OnSearchItemClickListener(int tmdbId, Display.SearchMediaType queryType, View view) {
            this.mItemTmdbId = tmdbId;
            this.queryType = queryType;
            this.view = view;
        }

        public OnSearchItemClickListener(Display.SearchMediaType queryType) {
            this(-1, queryType, null);
        }

        @Override
        public void onClick(View v) {
           // int[] startingLocation = new int[2];
           // view.getLocationOnScreen(startingLocation);
           // startingLocation[0] += view.getWidth() / 2;
           // startingLocation[1] += view.getHeight() / 2;

            Display display = getDisplay();
            if (display != null) {
                    display.startSearchDetailActivity(String.valueOf(mItemTmdbId), queryType);

            }
        }
    }
}
