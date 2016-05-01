package com.roodie.materialmovies.mvp.views;

import android.view.View;

import com.arellomobile.mvp.GenerateViewState;
import com.roodie.model.entities.MovieWrapper;
import com.roodie.model.entities.PersonWrapper;

/**
 * Created by Roodie on 10.02.2016.
 */

@GenerateViewState
public interface MovieDetailView extends MvpLceView<MovieWrapper>  {

        void showMovieDetail(MovieWrapper movie, View ui);

        void playTrailer();

        void showPersonDetail(PersonWrapper person, View ui);

        void showMovieImages(MovieWrapper movie);

        void showMovieCreditsDialog(MMoviesQueryType queryType);

}
