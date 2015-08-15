package com.roodie.materialmovies.views.fragments;

import com.roodie.materialmovies.views.fragments.base.ShowGridFragment;

/**
 * Created by Roodie on 14.08.2015.
 */
public class PopularShowsFragment extends ShowGridFragment {

    @Override
    public boolean isModal() {
        return false;
    }

    @Override
    public MovieQueryType getQueryType() {
        return MovieQueryType.POPULAR_SHOWS;
    }
}
