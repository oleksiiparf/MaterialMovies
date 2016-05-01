package com.roodie.materialmovies.mvp.views;

import com.arellomobile.mvp.GenerateViewState;
import com.arellomobile.mvp.MvpView;

/**
 * Created by Roodie on 29.04.2016.
 */
@GenerateViewState
public interface SettingsView extends MvpView {

    void onWatchedCleared();

}
