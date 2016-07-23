package com.roodie.materialmovies.mvp.views;

import android.view.View;

import com.arellomobile.mvp.GenerateViewState;
import com.roodie.model.entities.ShowWrapper;

/**
 * Created by Roodie on 22.03.2016.
 */

@GenerateViewState
public interface ListTvShowsView extends BaseListView<ShowWrapper> {

    void showItemDetail(ShowWrapper item, View ui);

    void showContextMenu(ShowWrapper tvShow);
}