package com.roodie.materialmovies.views.activities;


import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.roodie.materialmovies.R;
import com.roodie.materialmovies.views.fragments.MovieDetailFragmentVertical;
import com.roodie.materialmovies.views.fragments.PersonDetailFragment;
import com.roodie.materialmovies.views.fragments.SearchMoviesListFragment;
import com.roodie.materialmovies.views.fragments.SearchPeopleListFragment;
import com.roodie.materialmovies.views.fragments.SearchShowsListFragment;
import com.roodie.materialmovies.views.fragments.base.BaseDetailFragment;
import com.roodie.materialmovies.views.fragments.base.BaseSearchListFragment;
import com.roodie.model.Display;

/**
 * Created by Roodie on 06.09.2015.
 */
public class SearchDetailActivity extends BaseNavigationActivity implements SearchMoviesListFragment.OnShowMovieListener, SearchShowsListFragment.OnShowTvShowListener, SearchPeopleListFragment.OnShowPersonListener {

    private boolean mTwoPane;
    View mContentPane;

    public static final String LOG_TAG = SearchDetailActivity.class.getSimpleName();

    Display.SearchMediaType queryType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.d(LOG_TAG, "OnCreate()");

        super.onCreate(savedInstanceState);

        mContentPane = findViewById(R.id.detail_frame);

        //check if has second frame
        if (mContentPane != null) {
            mTwoPane = true;
        }

        if (savedInstanceState == null) {
            // check if we should directly show a person
            int tmdbId = getIntent().getIntExtra(Display.PARAM_ID, -1);
            queryType = (Display.SearchMediaType) getIntent().getExtras().get(Display.PARAM_SEARCH_TYPE);
            if (tmdbId != -1) {
                switch (queryType) {
                    case MOVIES:
                        showMovieDetail  (String.valueOf(tmdbId), null);
                        break;
                    case SHOWS:
                        showTvShowDetail(String.valueOf(tmdbId), null);
                        break;
                    case PEOPLE:
                        showPersonDetail(String.valueOf(tmdbId), null);
                        break;
                }
                // if this is not a dual pane layout, remove ourselves from back stack
                if (!mTwoPane) {
                    finish();
                    return;
                }
            }

            BaseSearchListFragment fragment = null;

            switch (queryType) {
                case MOVIES:
                    fragment = new SearchMoviesListFragment();
                    break;
                case SHOWS:
                    fragment = new SearchShowsListFragment();
                    break;
                case PEOPLE:
                    fragment = new SearchPeopleListFragment();
                    break;
            }
            //transmit activities orientation to listFragment
            fragment.setTwoPaneLayout(mTwoPane);

            fragment.setArguments(getIntent().getExtras());

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.content_frame, fragment, "list")
                    .commit();

        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //restart the activity when orientation changes
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    @Override
    protected void handleIntent(Intent intent, Display display) {
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected int getContentViewLayoutId() {
        return R.layout.activity_search_detail_list;
    }


    @Override
    public void showMovieDetail(String movieId, View view) {

        if (!TextUtils.isEmpty(movieId)) {
            if (mTwoPane) {
                BaseDetailFragment fragment = MovieDetailFragmentVertical.newInstance(movieId);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.detail_frame, fragment)
                        .commit();
            } else {
                //start new activity
                Display display = getDisplay();
                if (display != null) {

                    if (view != null) {

                        int[] startingLocation = new int[2];
                        view.getLocationOnScreen(startingLocation);
                        startingLocation[0] += view.getWidth() / 2;
                        startingLocation[1] += view.getHeight() / 2;

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            System.out.println("Start by shared element");
                            display.startMovieDetailActivityBySharedElements(movieId, view, (String) view.getTag());
                        } else {
                            System.out.println("Start by animation");
                            display.startMovieDetailActivityByAnimation(movieId, startingLocation);
                        }
                    } else {
                        System.out.println("Start by default");
                        display.startMovieDetailActivity(movieId, null);
                    }
                }
            }
        }
    }

    @Override
    public void showTvShowDetail(String showId, View view) {
        //TODO
    }

    @Override
    public void showPersonDetail(String personId, View view) {
        int[] startingLocation = new int[2];
        view.getLocationOnScreen(startingLocation);
        startingLocation[0] += view.getWidth() / 2;

        if (!TextUtils.isEmpty(personId)) {
            if (mTwoPane) {
                PersonDetailFragment fragment = PersonDetailFragment.newInstance(personId, startingLocation);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.detail_frame, fragment)
                        .commit();
            } else {
                //start new activity
                Display display = getDisplay();
                if (display != null) {
                    display.startPersonDetailActivity(personId, startingLocation);
                }
            }
        }

    }
}
