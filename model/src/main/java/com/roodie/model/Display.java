/*
 * Copyright 2014 Chris Banes
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.roodie.model;

import android.os.Bundle;

public interface Display {

    public static final String PARAM_ID = "_id";

    public void showMovies();

    public void showShows();

    public void showSettings();

    public void showAbout();

    public void startMovieDetailActivity(String movieId, Bundle bundle);

    public void showMovieDetailFragment(String movieId);

    public void startMovieImagesActivity(String movieId);

    public void showMovieImagesFragment(String movieId);

    public void startSettingsActivity();

    public void closeDrawerLayout();

    public boolean hasMainFragment();

    public void showUpNavigation(boolean show);

    public void setActionBarTitle(CharSequence title);

    public void setActionBarSubtitle(CharSequence title);

    public boolean popEntireFragmentBackStack();

    public void finishActivity();

    public void startPersonDetailActivity(String id, Bundle bundle);

    public void showPersonDetailFragment(String id);

    public void playYoutubeVideo(String id);

    public boolean toggleDrawer();

    public void setStatusBarColor(float scroll);

    public void setSupportActionBar(Object toolbar, boolean handleBackground);


}
