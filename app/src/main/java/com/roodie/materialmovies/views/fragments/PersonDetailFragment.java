package com.roodie.materialmovies.views.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.common.base.Preconditions;
import com.roodie.materialmovies.R;
import com.roodie.materialmovies.mvp.presenters.PersonPresenter;
import com.roodie.materialmovies.views.MMoviesApplication;
import com.roodie.materialmovies.views.custom_views.MMoviesImageView;
import com.roodie.materialmovies.views.custom_views.MovieDetailCardLayout;
import com.roodie.materialmovies.views.custom_views.ViewRecycler;
import com.roodie.materialmovies.views.fragments.base.BaseDetailFragment;
import com.roodie.model.Display;
import com.roodie.model.entities.PersonCreditWrapper;
import com.roodie.model.entities.PersonWrapper;
import com.roodie.model.network.NetworkError;
import com.roodie.model.util.MoviesCollections;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Roodie on 28.06.2015.
 */
public class PersonDetailFragment extends BaseDetailFragment implements PersonPresenter.PersonView {

    private static final String LOG_TAG = PersonDetailFragment.class.getSimpleName();
    private static final String KEY_PERSON_ID = "person_id";

    private PersonPresenter mPresenter;
    private DetailAdapter mAdapter;
    private PersonWrapper mPerson;
    private final ArrayList<PersonItems> mItems = new ArrayList<>();

    private MMoviesImageView personImagePoster;
    private TextView personName;

    private CastCreditsAdapter mCastCreditAdapter;
    private CrewCreditsAdapter mCrewCreditAdapter;

    private Context mContext;

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
        // MMoviesApplication.from(getActivity()).inject(this);
        setHasOptionsMenu(true);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity.getApplicationContext();
        mPresenter = MMoviesApplication.from(activity.getApplicationContext()).getPersonPresenter();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_person, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        personImagePoster = (MMoviesImageView) view.findViewById(R.id.imageview_person);
        personName = (TextView) view.findViewById(R.id.textview_person_name);

        mPresenter.attachView(this);
        mPresenter.initialize();
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
    public void onDetach() {
        super.onDetach();

    }

    public PersonPresenter getPresenter() {
        return mPresenter;
    }

    public final boolean hasPresenter () {
        return mPresenter != null;
    }


    private enum PersonItems  {
        TITLE,                        //(R.layout.item_person_detail_title)
        BIOGRAPHY,                    //(R.layout.item_movie_detail_summary)
        CREDITS_CAST,                 //(R.layout.item_movie_detail_generic_card),
        CREDITS_CREW                  //(R.layout.item_movie_detail_generic_card);
    }


    protected DetailAdapter createRecyclerAdapter(List<PersonItems> items) {
        return new DetailAdapter(items);
    }

    protected DetailAdapter getRecyclerAdapter() {
        return mAdapter;
    }

    /**
     * PersonView
     *
     * @param person
     */
    @Override
    public void setPerson(PersonWrapper person) {
        mPerson = person;
        populateUi();
        getRecyclerView().setAdapter(mAdapter);
    }


    @Override
    public void showMovieDetail(PersonCreditWrapper credit, Bundle bundle) {
        Preconditions.checkNotNull(credit, "credit cannot be null");

        Display display = getDisplay();
        if (display != null) {
            display.startMovieDetailActivity(String.valueOf(credit.getId()), bundle);
        }
    }

    @Override
    public void showPersonCreditsDialog(MovieQueryType queryType) {
        Preconditions.checkNotNull(queryType, "Query type cannot be null");
        Log.d(LOG_TAG, "Show detail dialog list");
        ListView list = new ListView(mContext);
        String mTitle = "";
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
                .show();
    }

    /**
     * MovieView
     *
     * @param error
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
        return getArguments().getString(KEY_PERSON_ID);
    }

    @Override
    public MovieQueryType getQueryType() {
        return MovieQueryType.PERSON_DETAIL;
    }

    @Override
    public boolean isModal() {
        return false;
    }

    private void populateUi() {
        if (mPerson == null) {
            return;
        }

        if (personImagePoster != null) {
            personImagePoster.loadProfile(mPerson);
        }

        personName.setText(mPerson.getName());
        mItems.clear();
        mItems.add(PersonItems.TITLE);

        if (!TextUtils.isEmpty(mPerson.getBiography())) {
            mItems.add(PersonItems.BIOGRAPHY);
        }

        if (!MoviesCollections.isEmpty(mPerson.getCastCredits())) {
            mItems.add(PersonItems.CREDITS_CAST);
        }
        if (!MoviesCollections.isEmpty(mPerson.getCrewCredits())) {
            mItems.add(PersonItems.CREDITS_CREW);
        }

        mAdapter = createRecyclerAdapter(mItems);
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
                                mPerson.getAge())
                );
                holder.subtitle2.setVisibility(View.VISIBLE);
            } else if (mPerson.getDateOfBirth() != null) {
                holder.subtitle2.setText(getString(R.string.person_age, mPerson.getAge()));
                holder.subtitle2.setVisibility(View.VISIBLE);
            } else {
                holder.subtitle2.setVisibility(View.GONE);
            }
        }

        @Override
        public int getItemCount() {
            return 1;
        }

        class ViewHolder extends RecyclerView.ViewHolder {

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

        class ViewHolder extends RecyclerView.ViewHolder {

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
                    showPersonCreditsDialog(MovieQueryType.PERSON_CREDITS_CAST);
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

        class ViewHolder extends RecyclerView.ViewHolder {

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
                    showPersonCreditsDialog(MovieQueryType.PERSON_CREDITS_CREW);
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

        class ViewHolder extends RecyclerView.ViewHolder {

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
                    (MMoviesImageView) view.findViewById(R.id.poster);
            //load poster to imageView
            imageView.loadPoster(credit);

            TextView subTitle = (TextView) view.findViewById(R.id.subtitle_1);
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
                    if (hasPresenter()) {
                        PersonCreditWrapper credit = (PersonCreditWrapper) view.getTag();
                        if (credit != null && credit != null) {
                            showMovieDetail(credit,
                                    null);
                        }
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
                    if (hasPresenter()) {
                        PersonCreditWrapper credit = (PersonCreditWrapper) view.getTag();
                        if (credit != null && credit != null) {
                            showMovieDetail(credit,
                                    null);
                        }
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
