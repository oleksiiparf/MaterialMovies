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

    public void showPopular();

    public void startMovieDetailActivity(String movieId, Bundle bundle);

    public void showMovieDetailFragment(String movieId);

    public void startMovieImagesActivity(String movieId);

    public void showMovieImagesFragment(String movieId);

    public void startAboutActivity();

    public void showAboutFragment();

    public void closeDrawerLayout();

    public boolean hasMainFragment();

    public void setActionBarTitle(CharSequence title);

    public void setActionBarSubtitle(CharSequence title);

    public boolean popEntireFragmentBackStack();

    public void showUpNavigation(boolean show);

    public void finishActivity();

    public void showRelatedMovies(String movieId);

    public void showCastList(String movieId);

    public void showCrewList(String movieId);

    public void showPersonDetailActivity(String id, Bundle bundle);

    public void showPersonDetail(String id);

    public void showPersonCastCredits(String id);

    public void showPersonCrewCredits(String id);

    public void showCredentialsChanged();

    public void playYoutubeVideo(String id);

    public void setStatusBarColor(float scroll);

    public void setSupportActionBar(Object toolbar, boolean handleBackground);

}
