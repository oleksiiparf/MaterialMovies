package com.roodie.materialmovies.mvp.views;

import android.view.View;

import com.roodie.model.entities.ListItem;
import com.roodie.model.entities.SeasonWrapper;

import java.util.List;

/**
 * Created by Roodie on 27.09.2015.
 */
public interface SeasonsListView extends BaseSeasonView {

    void setItems(List<ListItem<SeasonWrapper>> items);

    String getTitle();

    String getSubtitle();

    void showSeasonDetail(SeasonWrapper tvSeason, View view, int position);
}