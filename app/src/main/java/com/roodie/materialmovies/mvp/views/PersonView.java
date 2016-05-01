package com.roodie.materialmovies.mvp.views;

import android.view.View;

import com.arellomobile.mvp.GenerateViewState;
import com.roodie.model.entities.PersonCreditWrapper;
import com.roodie.model.entities.PersonWrapper;

/**
 * Created by Roodie on 14.02.2016.
 */

@GenerateViewState
public interface PersonView extends MvpLceView<PersonWrapper>{

    void showMovieDetail(PersonCreditWrapper credit, View item);

    void showPersonCreditsDialog(MMoviesQueryType queryType);
}
