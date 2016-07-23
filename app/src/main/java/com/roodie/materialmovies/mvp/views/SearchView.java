package com.roodie.materialmovies.mvp.views;

import android.view.View;

import com.arellomobile.mvp.GenerateViewState;
import com.roodie.model.entities.MovieWrapper;
import com.roodie.model.entities.PersonWrapper;
import com.roodie.model.entities.ShowWrapper;
import com.roodie.model.state.MoviesState;

/**
 * Created by Roodie on 22.03.2016.
 */

@GenerateViewState
public interface SearchView extends MvpLceView<MoviesState.SearchResult> {

    void showMovieDetail(MovieWrapper movie, View item);

    void showTvShowDetail(ShowWrapper show, View item);

    void showPersonDetail(PersonWrapper person, View item);

}
