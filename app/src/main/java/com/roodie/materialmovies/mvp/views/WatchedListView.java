package com.roodie.materialmovies.mvp.views;

import android.view.View;

import com.arellomobile.mvp.GenerateViewState;
import com.roodie.model.entities.Watchable;

/**
 * Created by Roodie on 06.03.2016.
 */
@GenerateViewState
public interface WatchedListView extends BaseListView<Watchable> {

    void showWatchableDetail(Watchable item, View container);

}
