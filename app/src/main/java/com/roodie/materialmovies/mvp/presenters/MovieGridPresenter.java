package com.roodie.materialmovies.mvp.presenters;

import android.os.Bundle;
import android.text.TextUtils;

import com.google.common.base.Preconditions;
import com.roodie.materialmovies.mvp.views.BaseMovieListView;
import com.roodie.model.Display;
import com.roodie.model.entities.MovieWrapper;


/**
 * Created by Roodie on 10.07.2015.
 */
public class MovieGridPresenter extends BasePresenter {

    private MovieGridView mMovieGridView;

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


    protected void attachView(MovieGridView view) {
        this.mMovieGridView = view;
    }

    public void showMovieDetail(MovieWrapper movie, Bundle bundle){
        Preconditions.checkNotNull(movie, "movie cannot be null");
        Display display = getDisplay();
        if (display != null) {
            if (!TextUtils.isEmpty(movie.getImdbId())) {
                display.startMovieDetailActivity(movie.getImdbId(), bundle);
            }
        }
    }


    public void onScrolledToBottom(){


    }

    public interface MovieGridView extends BaseMovieListView<MovieWrapper> {

    }
}
