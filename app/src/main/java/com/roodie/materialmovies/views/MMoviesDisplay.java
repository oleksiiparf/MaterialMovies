package com.roodie.materialmovies.views;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;

import com.google.common.base.Preconditions;
import com.roodie.materialmovies.R;
import com.roodie.materialmovies.views.activities.MovieActivity;
import com.roodie.materialmovies.views.activities.MovieImagesActivity;
import com.roodie.materialmovies.views.activities.PersonActivity;
import com.roodie.materialmovies.views.activities.SettingsActivity;
import com.roodie.materialmovies.views.fragments.MovieDetailFragment;
import com.roodie.materialmovies.views.fragments.MovieImagesFragment;
import com.roodie.materialmovies.views.fragments.PersonDetailFragment;
import com.roodie.materialmovies.views.fragments.PopularMoviesFragment;
import com.roodie.materialmovies.views.fragments.ShowsFragment;
import com.roodie.model.Display;

/**
 * Created by Roodie on 27.06.2015.
 */
public class MMoviesDisplay implements Display {

    private static final String LOG_TAG = MMoviesDisplay.class.getSimpleName();

    private final ActionBarActivity mActivity;
    private final DrawerLayout mDrawerLayout;

    private Toolbar mToolbar;
    private boolean mCanChangeToolbarBackground;

    public MMoviesDisplay(ActionBarActivity mActivity, DrawerLayout mDrawerLayout) {
        this.mActivity = Preconditions.checkNotNull(mActivity, "Activity can not be null");
        this.mDrawerLayout = mDrawerLayout;

    }

    private void showFragment(Fragment fragment) {
        mActivity.getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_main, fragment)
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
        showFragmentFromDrawer(new PopularMoviesFragment());
    }

    @Override
    public void showShows() {
        showFragmentFromDrawer(new ShowsFragment());
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
            ab.setHomeAsUpIndicator(show ? R.drawable.ic_back : R.drawable.ic_menu);
        }
    }

    @Override
    public void startMovieDetailActivity(String movieId, Bundle bundle) {
        Intent intent = new Intent(mActivity, MovieActivity.class);
        intent.putExtra(PARAM_ID, movieId);
        startActivity(intent, bundle);
    }

    @Override
    public void showMovieDetailFragment(String movieId) {
        showFragmentFromDrawer(MovieDetailFragment.newInstance(movieId));
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
    public void startPersonDetailActivity(String id, Bundle bundle) {
        Intent intent = new Intent(mActivity, PersonActivity.class);
        intent.putExtra(PARAM_ID, id);
        startActivity(intent, bundle);
    }

    @Override
    public void playYoutubeVideo(String id) {
        Preconditions.checkNotNull(id, "id cannot be null");

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("http://www.youtube.com/watch?v=" + id));

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
    public void showPersonDetailFragment(String id) {
            showFragmentFromDrawer(PersonDetailFragment.newInstance(id));
    }
}
