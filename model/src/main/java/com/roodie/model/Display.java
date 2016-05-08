package com.roodie.model;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.view.View;

import com.roodie.model.entities.MovieWrapper;
import com.roodie.model.entities.PersonWrapper;
import com.roodie.model.entities.ShowWrapper;

public interface Display {

    String PARAM_ID = "_id";

    String PARAM_LOCATION = "_location";

    String PARAM_IMAGE = "_image";
    String PARAM_IMAGE_BACKGROUND = "_image_background";


    String PARAM_SEARCH_TYPE = "_search_type";

    void setDrawerLayout(DrawerLayout layout);

    void showMovies();

    void showTvShows();

    void showSettings();

    void showWatched();

    void showAbout();

    void showRelatedMovies(String movieId);

    void startWatchlistActivity();

    void startMovieDetailActivityBySharedElements(String movieId, View view, String imageUrl);

    void startMovieDetailActivity(String movieId, Bundle bundle);

    void showMovieDetailFragment(String movieId);

    Fragment showMovieDetailFragmentBySharedElements(String movieId, String imageUrl);

    void startTvDetailActivity(String showId, Bundle bundle);

    void startTvDetailActivityBySharedElements(String tvId, View view, String imageUrl);

    void showTvDetailFragment(String tvId);

    Fragment showTvDetailFragmentBySharedElement(String tvId, String imageUrl);

    void startPersonDetailActivity(String id, Bundle bundle);

    void showPersonDetailFragment(String id);

    void startMovieImagesActivity(String movieId);

    void showMovieImagesFragment(String movieId);

    void startSettingsActivity();

    void playYoutubeVideo(String id);

    void shareMovie(int movieId, String movieTitle);

    void shareTvShow(int showId, String showTitle);

    void startShareIntentChooser(String message, @StringRes int titleResId);

    void openTmdbMovie(MovieWrapper movie);

    void openTmdbPerson(PersonWrapper person);

    void openTmdbTvShow(ShowWrapper show);

    void openTmdbUrl(String url);

    boolean tryStartActivity(Intent intent, boolean displayError);

    void performWebSearch(String query);

    void showSearchFragment();

    void showSearchMoviesFragment();

    void showSearchPeopleFragment();

    void showSearchTvShowsFragment();

    void closeDrawerLayout();

    boolean hasMainFragment();

    void showUpNavigation(boolean show);

    void setActionBarTitle(CharSequence title);

    void setActionBarSubtitle(CharSequence title);

    boolean popEntireFragmentBackStack();

    void finishActivity();

    boolean toggleDrawer();

    void setStatusBarColor(float scroll);

    void setSupportActionBar(Object toolbar, boolean handleBackground);

    boolean isTablet();

    void sendEmail();

}
