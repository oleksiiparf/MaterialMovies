package com.roodie.materialmovies.views.fragments;

import com.roodie.materialmovies.views.fragments.base.ShowGridFragment;

/**
 * Created by Roodie on 14.08.2015.
 */

public class OnTheAirShowsFragment extends ShowGridFragment {

    @Override
    public MovieQueryType getQueryType() {
        return MovieQueryType.ON_THE_AIR_SHOWS;
    }

    @Override
    public boolean isModal() {
        return false;
    }
}
