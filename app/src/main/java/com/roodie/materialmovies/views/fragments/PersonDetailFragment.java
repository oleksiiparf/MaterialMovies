package com.roodie.materialmovies.views.fragments;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
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
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerviewViewHolder;
import com.roodie.materialmovies.R;
import com.roodie.materialmovies.mvp.presenters.PersonPresenter;
import com.roodie.materialmovies.mvp.views.PersonView;
import com.roodie.materialmovies.views.activities.SettingsActivity;
import com.roodie.materialmovies.views.custom_views.MMoviesImageView;
import com.roodie.materialmovies.views.custom_views.MovieDetailCardLayout;
import com.roodie.materialmovies.views.custom_views.ViewRecycler;
import com.roodie.materialmovies.views.fragments.base.BaseAnimationFragment;
import com.roodie.model.Display;
import com.roodie.model.entities.PersonCreditWrapper;
import com.roodie.model.entities.PersonWrapper;
import com.roodie.model.util.MoviesCollections;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Roodie on 28.06.2015.
 */

public class PersonDetailFragment extends BaseAnimationFragment<PersonWrapper> implements PersonView {

    private static final String LOG_TAG = PersonDetailFragment.class.getSimpleName();
    private static final String KEY_PERSON_ID = "person_id";
    private static final String KEY_PERSON_SAVE_STATE = "person_on_save_state";

    @InjectPresenter
    PersonPresenter mPresenter;

    private MenuItem mTmdbPersonItem;
    private MenuItem mSearchItem;

    private boolean isEnableSearch = false;

    private PersonWrapper mPerson;
    private final ArrayList<PersonItems> mItems = new ArrayList<>();

    private DetailAdapter mAdapter;
    private CastCreditsAdapter mCastCreditAdapter;
    private CrewCreditsAdapter mCrewCreditAdapter;

    @Override
    protected void configureEnterTransition() {
    }

    public static PersonDetailFragment newInstance(String personId) {
        Preconditions.checkArgument(!TextUtils.isEmpty(personId), "personId cannot be empty");

        Bundle bundle = new Bundle();
        bundle.putString(KEY_PERSON_ID, personId);

        PersonDetailFragment fragment = new PersonDetailFragment();
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
        outState.putSerializable(KEY_PERSON_SAVE_STATE, mPerson);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null){
           setData((PersonWrapper) savedInstanceState.getSerializable(KEY_PERSON_SAVE_STATE));
        }
    }

    @Override
    public void onFabClicked() {
        //NTD
    }

    @Override
    public void onRefreshData(boolean visible) {
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_person;
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        //set actionbar up navigation
        final Display display = getDisplay();
        if (display != null) {
            display.showUpNavigation(getQueryType() != null && getQueryType().showUpNavigation());
        }

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

        super.onViewCreated(view, savedInstanceState);
    }

    public String getImagePosition() {
        return getArguments().getString(KEY_IMAGE_POSITION);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.person_menu, menu);
        mSearchItem =  menu.findItem(R.id.menu_action_person_web_search);
        mSearchItem.setEnabled(isEnableSearch);
        mSearchItem.setVisible(isEnableSearch);
        mTmdbPersonItem = menu.findItem(R.id.menu_action_person_tmdb);
        mTmdbPersonItem.setEnabled(isEnableSearch);
        mTmdbPersonItem.setVisible(isEnableSearch);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        isEnableSearch = mPerson != null && !TextUtils.isEmpty(
                mPerson.getName());
        mSearchItem.setEnabled(isEnableSearch);
        mSearchItem.setVisible(isEnableSearch);

        mTmdbPersonItem.setEnabled(isEnableSearch);
        mTmdbPersonItem.setVisible(isEnableSearch);
        super.onPrepareOptionsMenu(menu);
    }

    private void animatePoster() {
        mPosterImageView.setScaleY(0);
        mPosterImageView.setScaleX(0);
        mPosterImageView.animate()
                .scaleY(1).scaleX(1)
                .setDuration(200)
                .setStartDelay(SCALE_DELAY)
                .start();
        //mPosterImageView.setTranslationY(-mPosterImageView.getHeight());
        //mPosterImageView.animate().translationY(0).setDuration(300).setStartDelay(100).setInterpolator(getInterpolator());
    }

    protected DetailAdapter createRecyclerAdapter(List<PersonItems> items) {
        return new DetailAdapter(items);
    }

    /**
     * PersonDetailView
     */
    @Override
    protected void attachUiToPresenter() {
        mPresenter.attachUiByParameter(this, getRequestParameter());
        Display display = getDisplay();
        if ( display != null) {
            display.showUpNavigation(getQueryType() != null && getQueryType().showUpNavigation());
            display.setActionBarTitle(mPresenter.getUiTitle(getRequestParameter()));
        }
    }

    @Override
    public void updateDisplaySubtitle(String subtitle) {

    }

    @Override
    public void setData(PersonWrapper data) {
        mPerson = data;
        getActivity().invalidateOptionsMenu();
        mAdapter = populateUi();
        getRecyclerView().setAdapter(mAdapter);
        if (mPosterImageView != null) {
            mPosterImageView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void updateDisplayTitle(String title) {
        Display display = getDisplay();
        if (display != null) {
            display.setActionBarTitle(title);
        }
    }

    @Override
    public void showMovieDetail(PersonCreditWrapper credit, View view) {
        Preconditions.checkNotNull(credit, "credit cannot be null");

        int[] startingLocation = new int[2];
        view.getLocationOnScreen(startingLocation);
        startingLocation[0] += view.getWidth() / 2;

        Display display = getDisplay();
        if (display != null) {
            display.startMovieDetailActivity(String.valueOf(credit.getId()), null);
        }
    }

    @Override
    public void showPersonCreditsDialog(MMoviesQueryType queryType) {
        Preconditions.checkNotNull(queryType, "Query type cannot be null");
        ListView list = new ListView(getActivity());
        String mTitle = null;
        boolean wrapInScrollView = false;

        switch (queryType) {
            case PERSON_CREDITS_CAST:
                list.setAdapter(getCastCreditAdapter());
                mTitle = getResources().getString(R.string.cast_movies);
                break;
            case PERSON_CREDITS_CREW:
                list.setAdapter(getCrewCreditAdapter());
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
    public void showLoadingProgress(boolean visible) {
    }

    @Override
    public MMoviesQueryType getQueryType() {
        return MMoviesQueryType.PERSON_DETAIL;
    }

    public String getRequestParameter() {
        return getArguments().getString(KEY_PERSON_ID);
    }

    private DetailAdapter populateUi() {
        if (mPerson == null) {
            return null;
        }

        if (mPosterImageView != null) {
            mPosterImageView.loadProfile(mPerson, new MMoviesImageView.OnLoadedListener() {
                @Override
                public void onSuccess(MMoviesImageView imageView, Bitmap bitmap, String imageUrl) {
                  animatePoster();
                }

                @Override
                public void onError(MMoviesImageView imageView) {

                }
            });
        }
        mItems.clear();

        if (mPosterImageView == null) {
            mItems.add(PersonItems.HEADER);
        }

        if (mPerson.getAge() != null && mPerson.getDateOfBirth() != null && !TextUtils.isEmpty(mPerson.getPlaceOfBirth())) {
            mItems.add(PersonItems.TITLE);
        }

        if (!TextUtils.isEmpty(mPerson.getBiography())) {
            mItems.add(PersonItems.BIOGRAPHY);
        }

        if (!MoviesCollections.isEmpty(mPerson.getCastCredits())) {
            mItems.add(PersonItems.CREDITS_CAST);
        }
        if (!MoviesCollections.isEmpty(mPerson.getCrewCredits())) {
            mItems.add(PersonItems.CREDITS_CREW);
        }

        return createRecyclerAdapter(mItems);
    }

    private enum PersonItems  {
        HEADER,
        TITLE,                        //(R.layout.item_person_detail_title)
        BIOGRAPHY,                    //(R.layout.item_movie_detail_summary)
        CREDITS_CAST,                 //(R.layout.item_movie_detail_generic_card),
        CREDITS_CREW                  //(R.layout.item_movie_detail_generic_card);
    }

    /**
     * DetailAdapter
     */
    public class DetailAdapter extends  EnumListDetailAdapter<PersonItems> {
        private List<BaseViewHolder> mItems;

        public DetailAdapter() {
        }

        public DetailAdapter(List<PersonItems> items) {
            mItems = new ArrayList<>(items.size());
            for (PersonItems item : items) {
                switch (item) {
                    case HEADER:
                        mItems.add(new PersonHeaderBinder(this));
                        break;
                    case TITLE:
                        mItems.add(new PersonTitleBinder(this));
                        break;
                    case BIOGRAPHY:
                        mItems.add(new PersonBiographyBinder(this));
                        break;
                    case CREDITS_CAST:
                        mItems.add(new PersonCastBinder(this));
                        break;
                    case CREDITS_CREW:
                        mItems.add(new PersonCrewBinder(this));
                        break;
                }
            }
            addAllBinder(mItems);
        }
    }

    /**
     * PersonHeaderBinder
     */
    public class PersonHeaderBinder extends BaseViewHolder<PersonHeaderBinder.ViewHolder> {

        public PersonHeaderBinder(BaseDetailAdapter dataBindAdapter) {
            super(dataBindAdapter);
            Log.d(LOG_TAG, "Bind header");
        }

        @Override
        public ViewHolder newViewHolder(ViewGroup parent) {
            View view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.item_person_header, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void bindViewHolder(ViewHolder holder, int position) {
            holder.personImage.loadProfile(mPerson);
        }

        @Override
        public int getItemCount() {
            return 1;
        }

        class ViewHolder extends UltimateRecyclerviewViewHolder {

            MMoviesImageView personImage;

            public ViewHolder(View view) {
                super(view);
                personImage = (MMoviesImageView) view.findViewById(R.id.poster_image);
                }
        }
    }

    /**
     * PersonTitleBinder
     */
    public class PersonTitleBinder extends BaseViewHolder<PersonTitleBinder.ViewHolder> {

        public PersonTitleBinder(BaseDetailAdapter dataBindAdapter) {
            super(dataBindAdapter);
            Log.d(LOG_TAG, "Bind title");
        }

        @Override
        public ViewHolder newViewHolder(ViewGroup parent) {
            View view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.item_person_detail_title, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void bindViewHolder(ViewHolder holder, int position) {

            DateFormat mMediumDateFormatter = DateFormat.getDateInstance();

            if (mPerson.getDateOfBirth() != null) {
                if (!TextUtils.isEmpty(mPerson.getPlaceOfBirth())) {
                    holder.subtitle1.setText(
                            getString(R.string.person_born_date_with_loc,
                                    mMediumDateFormatter.format(mPerson.getDateOfBirth()),
                                    mPerson.getPlaceOfBirth())
                    );
                } else {
                    holder.subtitle1.setText(
                            getString(R.string.person_born_date,
                                    mMediumDateFormatter.format(mPerson.getDateOfBirth()))
                    );
                }
                holder.subtitle1.setVisibility(View.VISIBLE);
            } else {
                holder.subtitle1.setVisibility(View.GONE);
            }

            if (mPerson.getDateOfDeath() != null) {
                holder.subtitle2.setText(
                        getString(R.string.person_death_date,
                                mMediumDateFormatter.format(mPerson.getDateOfDeath()),
                                mPerson.getAge().toString())
                );
                holder.subtitle2.setVisibility(View.VISIBLE);
            } else if (mPerson.getDateOfBirth() != null) {
                holder.subtitle2.setText(getString(R.string.person_age, mPerson.getAge().toString()));
                holder.subtitle2.setVisibility(View.VISIBLE);
            } else {
                holder.subtitle2.setVisibility(View.GONE);
            }
        }

        @Override
        public int getItemCount() {
            return 1;
        }

        class ViewHolder extends UltimateRecyclerviewViewHolder {



            TextView subtitle1;
            TextView subtitle2;

            public ViewHolder(View view) {
                super(view);

                subtitle1 = (TextView) view.findViewById(R.id.textview_subtitle_1);
                subtitle2 = (TextView) view.findViewById(R.id.textview_subtitle_2);
            }
        }
    }

    /**
     * PersonBiographyBinder
     */
    public class PersonBiographyBinder extends BaseViewHolder<PersonBiographyBinder.ViewHolder> {

        public PersonBiographyBinder(BaseDetailAdapter dataBindAdapter) {
            super(dataBindAdapter);
            Log.d(LOG_TAG, "Bind Biography");
        }

        @Override
        public ViewHolder newViewHolder(ViewGroup parent) {
            View view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.item_person_detail_biography, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void bindViewHolder(ViewHolder holder, int position) {
            holder.biography.setText(mPerson.getBiography());
            Log.d(LOG_TAG, mPerson.getBiography());
        }

        @Override
        public int getItemCount() {
            return 1;
        }

        class ViewHolder extends UltimateRecyclerviewViewHolder {

            TextView biography;

            public ViewHolder(View view) {
                super(view);
                biography = (TextView) view.findViewById(R.id.textview_biography);
            }
        }
    }

    /**
     * PersonCastBinder
     */
    public class PersonCastBinder extends BaseViewHolder<PersonCastBinder.ViewHolder> {

        MovieDetailCardLayout cardLayout;

        public PersonCastBinder(BaseDetailAdapter dataBindAdapter) {
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
                    showPersonCreditsDialog(MMoviesQueryType.PERSON_CREDITS_CAST);
                }
            };

            final ViewRecycler viewRecycler = new ViewRecycler(holder.layout);
            viewRecycler.recycleViews();

            if (!getCastCreditAdapter().isEmpty()) {
                final int numItems = getResources().getInteger(R.integer.detail_card_max_items);
                final int adapterCount = getCastCreditAdapter().getCount();

                for (int i = 0; i < Math.min(numItems, adapterCount); i++) {
                    View view = getCastCreditAdapter().getView(i, viewRecycler.getRecycledView(), holder.layout);
                    holder.layout.addView(view);
                }
                final boolean showSeeMore = numItems < getCastCreditAdapter().getCount();
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
     * PersonCrewBinder
     */
    public class PersonCrewBinder extends BaseViewHolder<PersonCrewBinder.ViewHolder> {

        MovieDetailCardLayout cardLayout;

        public PersonCrewBinder(BaseDetailAdapter dataBindAdapter) {
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
                    showPersonCreditsDialog(MMoviesQueryType.PERSON_CREDITS_CREW);
                }
            };

            final ViewRecycler viewRecycler = new ViewRecycler(holder.layout);
            viewRecycler.recycleViews();

            if (!getCrewCreditAdapter().isEmpty()) {
                final int numItems = getResources().getInteger(R.integer.detail_card_max_items);
                final int adapterCount = getCrewCreditAdapter().getCount();

                for (int i = 0; i < Math.min(numItems, adapterCount); i++) {
                    View view = getCrewCreditAdapter().getView(i, viewRecycler.getRecycledView(), holder.layout);
                    holder.layout.addView(view);
                }
                final boolean showSeeMore = numItems < getCrewCreditAdapter().getCount();
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
     * BaseCreditAdapter
     */
    private abstract class BaseCreditAdapter extends BaseAdapter {
        private final View.OnClickListener mItemOnClickListener;
        private final LayoutInflater mInflater;

        BaseCreditAdapter(LayoutInflater inflater, View.OnClickListener itemOnClickListener) {
            mInflater = inflater;
            mItemOnClickListener = itemOnClickListener;
        }

        @Override
        public abstract PersonCreditWrapper getItem(int position);

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = mInflater.inflate(getLayoutId(), viewGroup, false);
            }

            final PersonCreditWrapper credit = getItem(position);

            final TextView title = (TextView) view.findViewById(R.id.title);
            Preconditions.checkState(title != null, "title == null");
           // Preconditions.checkState(credit.getTitle() == null, "credit.getTitle() != null  " + credit.getTitle());
            title.setText(credit.getTitle());

            final MMoviesImageView imageView =
                    (MMoviesImageView) view.findViewById(R.id.imageview_poster);
            //load poster to imageView
            imageView.loadPoster(credit);

            TextView subTitle = (TextView) view.findViewById(R.id.textview_subtitle_1);
            if (!TextUtils.isEmpty(credit.getJob())) {
                subTitle.setText(credit.getJob());
                subTitle.setVisibility(View.VISIBLE);
            } else {
                subTitle.setVisibility(View.GONE);
            }

            view.setOnClickListener(mItemOnClickListener);
            view.setTag(credit);

            return view;
        }

        protected int getLayoutId() {
            return R.layout.item_movie_detail_list_2line;
        }
    }

    /**
     * CrewCreditAdapter
     */
    private class CrewCreditsAdapter extends BaseCreditAdapter {
        CrewCreditsAdapter(LayoutInflater inflater) {
            super(inflater, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PersonCreditWrapper credit = (PersonCreditWrapper) view.getTag();
                    if (credit != null) {
                        showMovieDetail(credit, view);
                    }
                }
            });
        }

        @Override
        public int getCount() {
            return mPerson != null ? MoviesCollections.size(mPerson.getCrewCredits()) : 0;
        }

        @Override
        public PersonCreditWrapper getItem(int position) {
            return mPerson.getCrewCredits().get(position);
        }
    }

    /**
     * CastCreditAdapter
     */
    private class CastCreditsAdapter extends BaseCreditAdapter {
        CastCreditsAdapter(LayoutInflater inflater) {
            super(inflater, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PersonCreditWrapper credit = (PersonCreditWrapper) view.getTag();
                    if (credit != null) {
                        showMovieDetail(credit, view);
                    }

                }
            });
        }

        @Override
        public int getCount() {
            return mPerson != null ? MoviesCollections.size(mPerson.getCastCredits()) : 0;
        }

        @Override
        public PersonCreditWrapper getItem(int position) {
            return mPerson.getCastCredits().get(position);
        }
    }

    private CastCreditsAdapter getCastCreditAdapter() {
        if (mCastCreditAdapter == null) {
            mCastCreditAdapter = new CastCreditsAdapter(LayoutInflater.from(getActivity()));
        }
        return mCastCreditAdapter;
    }

    private CrewCreditsAdapter getCrewCreditAdapter() {
        if (mCrewCreditAdapter == null) {
            mCrewCreditAdapter = new CrewCreditsAdapter(LayoutInflater.from(getActivity()));
        }
        return mCrewCreditAdapter;
    }

}
