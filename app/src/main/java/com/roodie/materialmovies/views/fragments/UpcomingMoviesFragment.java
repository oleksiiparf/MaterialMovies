package com.roodie.materialmovies.views.fragments;

import com.roodie.materialmovies.views.fragments.base.MovieGridFragment;

/**
 * Created by Roodie on 02.08.2015.
 */
public class UpcomingMoviesFragment extends MovieGridFragment {

    @Override
    public MovieQueryType getQueryType() {
        return MovieQueryType.UPCOMING;
    }

    @Override
    public boolean isModal() {
        return false;
    }
}
