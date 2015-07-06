package com.roodie.materialmovies.mvp.presenters;

import com.roodie.materialmovies.mvp.views.MovieView;
import com.uwetrottmann.tmdb.entities.Movie;

import java.util.List;

/**
 * Created by Roodie on 06.07.2015.
 */
public class MovieImagesPresenter extends BasePresenter {

    private MovieImagesView mMoviesImagesView;

    public MovieImagesPresenter() {
    }

    @Override
    public void onResume() {

    }

    @Override
    protected void onInited() {
        super.onInited();
    }

    @Override
    protected void onPaused() {
        super.onPaused();
    }

    public interface MovieImagesView extends MovieView {

        void setItems(List<String> images);

        void showMovieImages(Movie movie);
    }

}
