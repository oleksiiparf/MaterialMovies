package com.roodie.materialmovies.mvp.views;

import android.view.View;

import com.arellomobile.mvp.GenerateViewState;
import com.roodie.model.entities.ShowWrapper;

import java.util.List;

/**
 * Created by Roodie on 13.02.2016.
 */

@GenerateViewState
public interface TvShowsGridView extends BaseListView<List<ShowWrapper>> {

    void showTvShowDialog(ShowWrapper tvShow);

    void showTvDetail(ShowWrapper tvShow, View ui);


}
