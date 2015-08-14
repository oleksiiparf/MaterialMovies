package com.roodie.materialmovies.views.fragments;

import android.os.Bundle;

import com.roodie.materialmovies.views.fragments.base.MovieGridFragment;

/**
 * Created by Roodie on 02.08.2015.
 */
public class InTheatresMoviesFragment extends MovieGridFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public MovieQueryType getQueryType() {
        return MovieQueryType.IN_THEATERS_MOVIES;
    }

    @Override
    public boolean isModal() {
        return false;
    }


}
