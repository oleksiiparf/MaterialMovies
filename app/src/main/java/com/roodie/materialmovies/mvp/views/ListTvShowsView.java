package com.roodie.materialmovies.mvp.views;

import android.view.View;

import com.arellomobile.mvp.GenerateViewState;
import com.roodie.model.entities.ShowWrapper;

import java.util.List;

/**
 * Created by Roodie on 22.03.2016.
 */

@GenerateViewState
public interface ListTvShowsView extends BaseListView<List<ShowWrapper>> {

    void showItemDetail(ShowWrapper item, View ui);
}