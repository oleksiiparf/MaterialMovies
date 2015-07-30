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
import com.roodie.materialmovies.mvp.presenters.PersonPresenter;
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

    @Inject MovieGridPresenter mGridPresenter;

    @Inject
    MovieDetailPresenter mDetailMoviePresenter;

    @Inject
    PersonPresenter mPersonPresenter;

    @Inject
    MovieImagesPresenter mMovieImagesPresenter;

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

    public MovieGridPresenter getGridPresenter() {
        return mGridPresenter;
    }

    public MovieDetailPresenter getDetailMoviePresenter() {
        return mDetailMoviePresenter;
    }

    public PersonPresenter getPersonPresenter() {
        return mPersonPresenter;
    }

    public MovieImagesPresenter getMovieImagesPresenter() {
        return mMovieImagesPresenter;
    }
}
