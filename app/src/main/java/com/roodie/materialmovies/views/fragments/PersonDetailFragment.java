package com.roodie.materialmovies.views.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.common.base.Preconditions;
import com.roodie.materialmovies.R;
import com.roodie.materialmovies.mvp.presenters.PersonPresenter;
import com.roodie.materialmovies.views.MMoviesApplication;
import com.roodie.materialmovies.views.activities.MovieActivity;
import com.roodie.materialmovies.views.fragments.base.BaseDetailFragment;
import com.roodie.model.Display;
import com.roodie.model.entities.PersonCreditWrapper;
import com.roodie.model.entities.PersonWrapper;
import com.roodie.model.network.NetworkError;
import com.roodie.model.util.MoviesCollections;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Roodie on 28.06.2015.
 */
public class PersonDetailFragment extends BaseDetailFragment implements PersonPresenter.PersonView {

    private static final String LOG_TAG = MovieDetailFragment.class.getSimpleName();
    private static final String PERSON_ID = "person_id";

    private PersonPresenter mPresenter;
    private PersonWrapper mPerson;
    private final ArrayList<PersonItems> mItems = new ArrayList<>();

    private Context mContext;
    private Display mDisplay;

    private ImageView personImagePoster;
    private TextView personName;

    public static PersonDetailFragment newInstance(String personId) {
        Preconditions.checkArgument(!TextUtils.isEmpty(personId), "personId cannot be empty");

        Bundle bundle = new Bundle();
        bundle.putString(PERSON_ID, personId);

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
        mDisplay = ((MovieActivity)this.getActivity()).getDisplay();
        mPresenter.attachDisplay(mDisplay);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_person, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        personImagePoster = (ImageView) view.findViewById(R.id.imageview_person);
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
        mPresenter.detachDisplay(mDisplay);
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


    @Override
    protected BaseDetailAdapter createRecyclerAdapter() {
        return null;
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
    }

    @Override
    public void showPersonDetail(PersonWrapper person, Bundle bundle) {

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
        return null;
    }

    @Override
    public boolean isModal() {
        return false;
    }

    private void populateUi() {
        if (mPerson == null) {
            return;
        }

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
    }

    /**
     * DetailAdapter
     */
    public class DetailAdapter extends  EnumListDetailAdapter<PersonItems> {
        List<BaseViewHolder> mItems;

        public DetailAdapter() {
        }

        public void addBinders(List<PersonItems> items) {
            mItems = new ArrayList<>(items.size());
            for (PersonItems item : items) {
                switch (item) {
                    case TITLE:
                       // mItems.add(new PersonTitleBinder(this));
                        break;
                    case BIOGRAPHY:
                       // mItems.add(new PersonBiographyBinder(this));
                        break;
                    case CREDITS_CAST:
                       // mItems.add(new PersonCastBinder(this));
                        break;
                    case CREDITS_CREW:
                       // mItems.add(new PersonCrewBinder(this));
                        break;
                }
            }
            addAllBinder(mItems);
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

            final TextView title = (TextView) view.findViewById(R.id.textview_title);
            title.setText(credit.getTitle());

            final ImageView imageView =
                    (ImageView) view.findViewById(R.id.imageview_poster);
            //load poster to imageView

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
                    if (hasPresenter()) {
                        PersonCreditWrapper credit = (PersonCreditWrapper) view.getTag();
                        if (credit != null && credit != null) {
                            getPresenter().showMovieDetail(credit,
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
                            getPresenter().showMovieDetail(credit,
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






}
