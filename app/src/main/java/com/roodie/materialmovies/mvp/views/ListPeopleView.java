package com.roodie.materialmovies.mvp.views;

import android.view.View;

import com.arellomobile.mvp.GenerateViewState;
import com.roodie.model.entities.PersonWrapper;

import java.util.List;

/**
 * Created by Roodie on 22.03.2016.
 */
@GenerateViewState
public interface ListPeopleView extends BaseListView<List<PersonWrapper>> {

    void showItemDetail(PersonWrapper item, View ui);
}
