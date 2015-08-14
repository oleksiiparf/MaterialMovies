package com.roodie.materialmovies.views.fragments;


import com.roodie.materialmovies.views.fragments.base.MovieGridFragment;


/**
 * Created by Roodie on 09.07.2015.
 */
public class PopularMoviesFragment extends MovieGridFragment  {

    @Override
    public boolean isModal() {
        return false;
    }

    @Override
    public MovieQueryType getQueryType() {
        return MovieQueryType.POPULAR_MOVIES;
    }

}
