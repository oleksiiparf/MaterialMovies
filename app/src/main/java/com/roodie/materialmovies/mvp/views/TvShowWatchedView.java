package com.roodie.materialmovies.mvp.views;

import com.arellomobile.mvp.GenerateViewState;
import com.arellomobile.mvp.MvpView;
import com.roodie.model.entities.ShowWrapper;

/**
 * Created by Roodie on 16.04.2016.
 */

@GenerateViewState
public interface TvShowWatchedView extends MvpView {

    void updateShowWatched(ShowWrapper item, int position);

}
