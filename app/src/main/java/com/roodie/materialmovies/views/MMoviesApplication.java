package com.roodie.materialmovies.views;

import android.app.Application;
import android.content.Context;

import com.roodie.materialmovies.modules.ApplicationModule;
import com.roodie.materialmovies.modules.TaskProvider;
import com.roodie.materialmovies.modules.library.ContextProvider;
import com.roodie.materialmovies.util.MMoviesServiceUtils;

import javax.inject.Inject;

import dagger.ObjectGraph;

/**
 * Created by Roodie on 02.07.2015.
 */
public class MMoviesApplication extends Application  {

    public static MMoviesApplication from(Context context) {
        return (MMoviesApplication) context.getApplicationContext();
    }

    @Inject
    MMoviesServiceUtils mMMoviesServiceUtils;
    private ObjectGraph mObjectGraph;

    @Override
    public void onCreate() {
        super.onCreate();

        mObjectGraph = ObjectGraph.create(
                new ContextProvider(this),
                new ApplicationModule(),
                new TaskProvider()
        );
        mObjectGraph.inject(this);
    }

    public ObjectGraph getObjectGraph() {
        return mObjectGraph;
    }

    public MMoviesServiceUtils getMMoviesServiceUtils() {
        return mMMoviesServiceUtils;
    }

    public void inject(Object object) {
        mObjectGraph.inject(object);
    }
}
