package com.roodie.materialmovies.views;

import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ShareCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.google.common.base.Preconditions;
import com.roodie.materialmovies.R;
import com.roodie.materialmovies.util.TmdbUtils;
import com.roodie.materialmovies.views.activities.MovieActivity;
import com.roodie.materialmovies.views.activities.MovieImagesActivity;
import com.roodie.materialmovies.views.activities.PersonActivity;
import com.roodie.materialmovies.views.activities.SearchDetailActivity;
import com.roodie.materialmovies.views.activities.SettingsActivity;
import com.roodie.materialmovies.views.activities.WatchlistActivity;
import com.roodie.materialmovies.views.fragments.MovieDetailFragment;
import com.roodie.materialmovies.views.fragments.MovieImagesFragment;
import com.roodie.materialmovies.views.fragments.MoviesTabFragment;
import com.roodie.materialmovies.views.fragments.PersonDetailFragment;
import com.roodie.materialmovies.views.fragments.SearchFragment;
import com.roodie.materialmovies.views.fragments.SearchMoviesListFragment;
import com.roodie.materialmovies.views.fragments.SearchPeopleListFragment;
import com.roodie.materialmovies.views.fragments.SearchShowsListFragment;
import com.roodie.materialmovies.views.fragments.ShowsTabFragment;
import com.roodie.model.Display;
import com.roodie.model.entities.MovieWrapper;
import com.roodie.model.entities.PersonWrapper;
import com.roodie.model.entities.ShowWrapper;

/**
 * Created by Roodie on 27.06.2015.
 */
public class MMoviesDisplay implements Display {

    private static final String LOG_TAG = MMoviesDisplay.class.getSimpleName();

    private final ActionBarActivity mActivity;
    private  DrawerLayout mDrawerLayout;

    private Toolbar mToolbar;
    private boolean mCanChangeToolbarBackground;

    public MMoviesDisplay(ActionBarActivity mActivity) {
        this.mActivity = mActivity;
    }

    public MMoviesDisplay(ActionBarActivity mActivity, DrawerLayout mDrawerLayout) {
        this.mActivity = Preconditions.checkNotNull(mActivity, "Activity can not be null");
        this.mDrawerLayout = mDrawerLayout;
    }

    public void setDrawerLayout(DrawerLayout drawerLayout) {
        this.mDrawerLayout = drawerLayout;
    }

    private void showFragment(Fragment fragment) {
        mActivity.getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_main, fragment)
                .addToBackStack(null)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }

    private void showContentFragment(Fragment fragment) {
        mActivity.getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, fragment)
                .addToBackStack(null)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }

    private void showFragmentFromDrawer(Fragment fragment) {
        popEntireFragmentBackStack();

        mActivity.getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_main, fragment)
                .commit();
    }

    private void startActivity(Intent intent, Bundle options) {
        ActivityCompat.startActivity(mActivity, intent, options);
    }

    @Override
    public void showMovies() {
        showFragmentFromDrawer(new MoviesTabFragment());
    }

    @Override
    public void showTvShows() {
        showFragmentFromDrawer(new ShowsTabFragment());
    }

    @Override
    public void showSettings() {
        startSettingsActivity();
    }

    @Override
    public void showAbout() {
    }

    @Override
    public void showUpNavigation(boolean show) {
        final ActionBar ab = mActivity.getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setHomeButtonEnabled(true);
            ab.setHomeAsUpIndicator(show ? R.drawable.ic_clear_white_24dp : R.drawable.ic_menu);
        }
    }

    @Override
    public void startWatchlistActivity() {
        Intent intent = new Intent(mActivity, WatchlistActivity.class);
        startActivity(intent, null);
    }

    @Override
    public void startMovieDetailActivityByAnimation(String movieId, int[] startingLocation) {
        Intent intent = new Intent(mActivity, MovieActivity.class);
        intent.putExtra(PARAM_ID, movieId);
        intent.putExtra(PARAM_LOCATION, startingLocation);
        startActivity(intent, null);
    }

    @Override
    public void showMovieDetailFragmentByAnimation(String movieId, int[] startingLocation) {
        showFragmentFromDrawer(MovieDetailFragment.newInstance(movieId, startingLocation));
    }

    @Override
    public void showMovieDetailFragmentBySharedElements(String movieId, String imageUrl) {
        showFragmentFromDrawer(MovieDetailFragment.newInstance(movieId, imageUrl));
    }

    @Override
    public void showMovieDetailFragment(String movieId) {
        showFragmentFromDrawer(MovieDetailFragment.newInstance(movieId));
    }

    @Override
    public void startMovieDetailActivityBySharedElements(String movieId, View view, String imageUrl) {
        ActivityOptionsCompat options =
                ActivityOptionsCompat.makeSceneTransitionAnimation(
                        mActivity, view, PARAM_IMAGE);
        Intent intent = new Intent(mActivity, MovieActivity.class);
        intent.putExtra(PARAM_ID, movieId);
        intent.putExtra(PARAM_IMAGE, imageUrl);
        startActivity(intent, options.toBundle());
    }

    @Override
    public void startMovieDetailActivity(String movieId, Bundle bundle) {
        Intent intent = new Intent(mActivity, MovieActivity.class);
        intent.putExtra(PARAM_ID, movieId);
        startActivity(intent, bundle);
    }


    @Override
    public void startPersonDetailActivity(String id, Bundle bundle) {
        Intent intent = new Intent(mActivity, PersonActivity.class);
        intent.putExtra(PARAM_ID, id);
        startActivity(intent, bundle);
    }


    @Override
    public void startPersonDetailActivity(String id, int[] startingLocation) {
        Intent intent = new Intent(mActivity, PersonActivity.class);
        intent.putExtra(PARAM_ID, id);
        intent.putExtra(PARAM_LOCATION, startingLocation);
        startActivity(intent, null);
    }

    @Override
    public void showPersonDetailFragment(String id, int[] startingLocation) {
        showFragmentFromDrawer(PersonDetailFragment.newInstance(id, startingLocation));
    }

    @Override
    public void startMovieImagesActivity(String movieId) {
        Intent intent = new Intent(mActivity, MovieImagesActivity.class);
        intent.putExtra(PARAM_ID, movieId);
        mActivity.startActivity(intent);

    }

    @Override
    public void showMovieImagesFragment(String movieId) {
        showFragmentFromDrawer(MovieImagesFragment.newInstance(movieId));
    }

    @Override
    public void startSettingsActivity() {
        Intent intent = new Intent(mActivity, SettingsActivity.class);
        mActivity.startActivity(intent);
    }

    @Override
    public void closeDrawerLayout() {
        if (mDrawerLayout != null && mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawers();
        }
    }

    @Override
    public boolean toggleDrawer() {
        if (mDrawerLayout != null) {
            if (mDrawerLayout.isDrawerVisible(GravityCompat.START)) {
                mDrawerLayout.closeDrawer(GravityCompat.START);
            } else {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean hasMainFragment() {
        return mActivity.getSupportFragmentManager().findFragmentById(R.id.fragment_main) != null;
    }

    @Override
    public void setActionBarTitle(CharSequence title) {
        ActionBar ab = mActivity.getSupportActionBar();
        if (ab != null) {
            ab.setTitle(title);
        }
    }

    @Override
    public void setActionBarSubtitle(CharSequence title) {
        ActionBar ab = mActivity.getSupportActionBar();
        if (ab != null) {
            ab.setSubtitle(title);
        }
    }

    @Override
    public boolean popEntireFragmentBackStack() {
        final FragmentManager fm = mActivity.getSupportFragmentManager();
        final int backStackCount = fm.getBackStackEntryCount();
        // Clear Back Stack
        for (int i = 0; i < backStackCount; i++) {
            fm.popBackStack();
        }
        return backStackCount > 0;
    }

    @Override
    public void finishActivity() {
        mActivity.finish();
    }

    @Override
    public void playYoutubeVideo(String id) {
        Preconditions.checkNotNull(id, "id cannot be null");

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("http://www.youtube.com/watch?v=" + id));

        mActivity.startActivity(intent);

    }

    @Override
    public void setStatusBarColor(float scroll) {

        //TODO
    }

    @Override
    public void setSupportActionBar(Object toolbar, boolean handleBackground) {
        mToolbar = (Toolbar) toolbar;
        mCanChangeToolbarBackground = handleBackground;


        if (mDrawerLayout != null && mToolbar != null) {
            final ActionBar ab = mActivity.getSupportActionBar();
            if (ab != null) {
                ab.setDisplayHomeAsUpEnabled(true);
                ab.setHomeButtonEnabled(true);
            }
        }
    }

    private void setToolbarBackground(int color) {
        if (mCanChangeToolbarBackground && mToolbar != null) {
            mToolbar.setBackgroundColor(color);
        }
    }

    @Override
    public void shareMovie(int movieId, String movieTitle) {
        String message = mActivity.getString(R.string.share_checkout, movieTitle) + " "
                + TmdbUtils.buildMovieUrl(movieId);
        startShareIntentChooser(message, R.string.share_movie);
    }

    @Override
    public void shareTvShow(int showId, String showTitle) {
        String message = mActivity.getString(R.string.share_checkout, showTitle) + " "
                + TmdbUtils.buildTvShowUrl(showId);
        startShareIntentChooser(message, R.string.share_tv_show);
    }

    @Override
    public void startShareIntentChooser(String message, @StringRes int titleResId) {
        ShareCompat.IntentBuilder ib = ShareCompat.IntentBuilder.from(mActivity);
        ib.setText(message);
        ib.setChooserTitle(titleResId);
        ib.setType("text/plain");
        try {
            ib.startChooser();
        } catch (ActivityNotFoundException e) {
            // no activity available to handle the intent
            Toast.makeText(mActivity, R.string.app_not_available, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void openTmdbMovie(MovieWrapper movie) {
        openTmdbUrl(TmdbUtils.buildMovieUrl(movie.getTmdbId()));
    }

    @Override
    public void openTmdbPerson(PersonWrapper person) {
        openTmdbUrl(TmdbUtils.buildPersonUrl(person.getTmdbId()));
    }

    @Override
    public void openTmdbTvShow(ShowWrapper show) {
        openTmdbUrl(TmdbUtils.buildTvShowUrl(show.getTmdbId()));
    }

    @Override
    public void openTmdbUrl(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        tryStartActivity(intent, true);
    }

    @Override
    public boolean tryStartActivity(Intent intent, boolean displayError) {
        boolean handled = false;

        if (intent.resolveActivity(mActivity.getPackageManager()) != null) {
            try {
                mActivity.startActivity(intent);
                handled = true;
            } catch (ActivityNotFoundException exception) {

            }
        }

        if (displayError && !handled) {
            Toast.makeText(mActivity, R.string.app_not_available, Toast.LENGTH_LONG).show();
        }
        return handled;
    }

    @Override
    public void performWebSearch(String query) {
        Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
        intent.putExtra(SearchManager.QUERY, query);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        tryStartActivity(intent, true);
    }

    @Override
    public void showSearchFragment() {
        showFragment(new SearchFragment());
    }

    @Override
    public void showSearchMoviesFragment() {
        showFragment(new SearchMoviesListFragment());
    }

    @Override
    public void showSearchPeopleFragment() {
        showFragment(new SearchPeopleListFragment());
    }

    @Override
    public void showSearchTvShowsFragment() {
        showFragment(new SearchShowsListFragment());
    }

    @Override
    public void startSearchDetailActivity(String id, SearchMediaType queryType) {
        Intent intent = new Intent(mActivity, SearchDetailActivity.class);
        intent.putExtra(PARAM_ID, Integer.valueOf(id));
        intent.putExtra(PARAM_SEARCH_TYPE, queryType);
        startActivity(intent, null);
    }
}
