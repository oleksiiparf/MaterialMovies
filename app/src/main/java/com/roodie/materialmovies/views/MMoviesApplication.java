package com.roodie.materialmovies.views;

import android.app.Application;
import android.content.Context;

import com.roodie.materialmovies.modules.ApplicationModule;
import com.roodie.materialmovies.modules.TaskProvider;
import com.roodie.materialmovies.modules.ViewUtilProvider;
import com.roodie.materialmovies.modules.library.ContextProvider;
import com.roodie.materialmovies.modules.library.InjectorModule;
import com.roodie.materialmovies.mvp.presenters.MovieDetailPresenter;
import com.roodie.materialmovies.mvp.presenters.MovieGridPresenter;
import com.roodie.materialmovies.mvp.presenters.MovieImagesPresenter;
import com.roodie.materialmovies.mvp.presenters.MovieTabPresenter;
import com.roodie.materialmovies.mvp.presenters.PersonPresenter;
import com.roodie.materialmovies.mvp.presenters.SearchPresenter;
import com.roodie.materialmovies.mvp.presenters.SeasonDetailPresenter;
import com.roodie.materialmovies.mvp.presenters.SeasonsPresenter;
import com.roodie.materialmovies.mvp.presenters.ShowDetailPresenter;
import com.roodie.materialmovies.mvp.presenters.ShowGridPresenter;
import com.roodie.materialmovies.mvp.presenters.ShowTabPresenter;
import com.roodie.model.util.Injector;

import javax.inject.Inject;

import dagger.ObjectGraph;

/**
 * Created by Roodie on 02.07.2015.
 */
public class MMoviesApplication extends Application implements Injector {

    public static MMoviesApplication from(Context context) {
        return (MMoviesApplication) context.getApplicationContext();
    }

    @Inject
    MovieGridPresenter mMovieGridPresenter;

    @Inject
    ShowGridPresenter mShowGridPresenter;

    @Inject
    MovieDetailPresenter mDetailMoviePresenter;

    @Inject
    ShowDetailPresenter mDetailShowPresenter;

    @Inject
    PersonPresenter mPersonPresenter;

    @Inject
    MovieImagesPresenter mMovieImagesPresenter;

    @Inject
    MovieTabPresenter mMovieTabsPresenter;

    @Inject
    ShowTabPresenter mShowTabsPresenter;

    @Inject
    SearchPresenter mSearchPresenter;

    @Inject
    SeasonsPresenter mTvSeasonsListPresenter;

    @Inject
    SeasonDetailPresenter mTvSeasonDetailPresenter;


    private ObjectGraph mObjectGraph;

    @Override
    public void onCreate() {
        super.onCreate();

        mObjectGraph = ObjectGraph.create(
                new ContextProvider(this),
                new ApplicationModule(),
                new TaskProvider(),
                new ViewUtilProvider(),
                new InjectorModule(this)
        );
        mObjectGraph.inject(this);
    }

    public ObjectGraph getObjectGraph() {
        return mObjectGraph;
    }


    public void inject(Object object) {
        mObjectGraph.inject(object);
    }

    public MovieGridPresenter getMovieGridPresenter() {
        return mMovieGridPresenter;
    }

    public MovieDetailPresenter getDetailMoviePresenter() {
        return mDetailMoviePresenter;
    }

    public ShowDetailPresenter getDetailShowPresenter() {
        return mDetailShowPresenter;
    }

    public PersonPresenter getPersonPresenter() {
        return mPersonPresenter;
    }

    public MovieImagesPresenter getMovieImagesPresenter() {
        return mMovieImagesPresenter;
    }

    public MovieTabPresenter getMovieTabsPresenter() {
        return mMovieTabsPresenter;
    }

    public ShowTabPresenter getShowTabsPresenter() {
        return mShowTabsPresenter;
    }

    public ShowGridPresenter getShowGridPresenter() {
        return mShowGridPresenter;
    }

    public SeasonsPresenter getTvSeasonsListPresenter() {
        return mTvSeasonsListPresenter;
    }

    public SeasonDetailPresenter getTvSeasonDetailPresenter() {
        return mTvSeasonDetailPresenter;
    }

    public SearchPresenter getSearchPresenter() {
        return mSearchPresenter;
    }
}
