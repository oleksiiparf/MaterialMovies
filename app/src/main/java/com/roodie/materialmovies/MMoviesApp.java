package com.roodie.materialmovies;

import android.content.Context;

import com.activeandroid.app.Application;
import com.arellomobile.mvp.MvpFacade;
import com.roodie.materialmovies.modules.ApplicationModule;
import com.roodie.materialmovies.modules.TaskProvider;
import com.roodie.materialmovies.modules.ViewUtilProvider;
import com.roodie.materialmovies.modules.library.ContextProvider;
import com.roodie.materialmovies.modules.library.InjectorModule;
import com.roodie.materialmovies.qualifiers.GeneralPurpose;
import com.roodie.materialmovies.util.AndroidStringFetcher;
import com.roodie.model.entities.Entity;
import com.roodie.model.entities.MovieWrapper;
import com.roodie.model.entities.ShowWrapper;
import com.roodie.model.repository.MovieRepository;
import com.roodie.model.repository.Repository;
import com.roodie.model.repository.ShowRepository;
import com.roodie.model.sqlite.SQLiteHelper;
import com.roodie.model.sqlite.SQLiteUpgradeStep;
import com.roodie.model.state.ApplicationState;
import com.roodie.model.util.BackgroundExecutor;
import com.roodie.model.util.Injector;
import com.roodie.model.util.Lists;
import com.squareup.otto.Bus;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.ObjectGraph;

/**
 * Created by Roodie on 02.07.2015.
 */
public class MMoviesApp extends Application implements Injector {

    private static volatile Context applicationContext;

    private static MMoviesApp sInstance;

    private static ApplicationState mState;

    private Bus mBus;

    @Inject @GeneralPurpose
    BackgroundExecutor executor;

    public MMoviesApp() {
        sInstance = this;
    }

    @Singleton
    public ApplicationState getState() {
        return mState;
    }


    public  Context getAppContext() {
        return this;
    }

    public BackgroundExecutor getBackgroundExecutor() {
        return executor;
    }

    @Singleton
    public AndroidStringFetcher getStringFetcher() {
        return new AndroidStringFetcher(applicationContext);
    }

    public Bus getBus() {
        return mBus;
    }

    public static MMoviesApp from(Context context) {
        return (MMoviesApp) context.getApplicationContext();
    }

    public static MMoviesApp get() {
        return sInstance;
    }

    public boolean isAuthentificatedFeatureEnabled() {
        return true;
    }

    public boolean isDaatabaseEnabled() {
        return true;
    }

    private ObjectGraph mObjectGraph;

    @Override
    public void onCreate() {
        super.onCreate();

        applicationContext = getApplicationContext();

        MvpFacade.init();



        mBus = new Bus();
        mState = new ApplicationState(getBus());

        mObjectGraph = ObjectGraph.create(
                new ContextProvider(this),
                new ApplicationModule(),
                new TaskProvider(),
                new ViewUtilProvider(),
                new InjectorModule(this)
        );
        mObjectGraph.inject(this);
        initRepositories();
    }

    public ObjectGraph getObjectGraph() {
        return mObjectGraph;
    }


    public void inject(Object object) {
        mObjectGraph.inject(object);
    }


    private void initRepositories() {
        if (sInstance.isDaatabaseEnabled()) {
            SQLiteHelper helper = new SQLiteHelper(this);
            initDatabaseRepositories(getState().getRepositories(), helper);
            helper.addUpgradeSteps(getSQLiteUpgradeSteps());
        }
    }

    private void initDatabaseRepositories(Map<Class<? extends Entity>, Repository<? extends Entity>> reposMap, SQLiteHelper sQLiteHelper)
    {
        reposMap.put(MovieWrapper.class, new MovieRepository(sQLiteHelper));
        reposMap.put(ShowWrapper.class, new ShowRepository(sQLiteHelper));
    }

    protected List<SQLiteUpgradeStep> getSQLiteUpgradeSteps()
    {
        return Lists.newArrayList();
    }


}
