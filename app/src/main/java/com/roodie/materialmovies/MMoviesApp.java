package com.roodie.materialmovies;

import android.app.Application;
import android.content.Context;

import com.arellomobile.mvp.MvpFacade;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.roodie.materialmovies.modules.ApplicationComponent;
import com.roodie.materialmovies.modules.TaskComponent;
import com.roodie.materialmovies.modules.ViewUtilComponent;
import com.roodie.materialmovies.modules.library.ContextModule;
import com.roodie.materialmovies.modules.library.InjectorModule;
import com.roodie.materialmovies.qualifiers.GeneralPurpose;
import com.roodie.materialmovies.util.FontManager;
import com.roodie.model.entities.Entity;
import com.roodie.model.entities.MovieWrapper;
import com.roodie.model.entities.ShowWrapper;
import com.roodie.model.repository.MovieRepository;
import com.roodie.model.repository.Repository;
import com.roodie.model.repository.ShowRepository;
import com.roodie.model.sqlite.SQLiteUpgradeStep;
import com.roodie.model.state.ApplicationState;
import com.roodie.model.util.BackgroundExecutor;
import com.roodie.model.util.Injector;
import com.roodie.model.util.Lists;
import com.roodie.model.util.StringFetcher;
import com.squareup.otto.Bus;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import dagger.ObjectGraph;
import io.fabric.sdk.android.Fabric;

/**
 * Created by Roodie on 02.07.2015.
 */
public class MMoviesApp extends Application implements Injector{

    private static MMoviesApp sInstance;

    @Inject ApplicationState mState;

    @Inject @GeneralPurpose
    BackgroundExecutor executor;

    @Inject
    StringFetcher mStringFetcher;

    @Inject
    FontManager mFontManager;

    @Inject MovieRepository mMovieRepositiry;

    @Inject ShowRepository mShowRepository;


    private Bus mBus;

    public MMoviesApp() {
        sInstance = this;
    }

    public ApplicationState getState() {
        return mState;
    }

    public  Context getAppContext() {
        return this;
    }

    public BackgroundExecutor getBackgroundExecutor() {
        return executor;
    }

    public StringFetcher getStringFetcher() {
        return mStringFetcher;
    }

    public FontManager getFontManager() {
        return mFontManager;
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

        //Create Crashlytics,enabled for debug assembly
        Crashlytics crashlyticsKit = new Crashlytics.Builder()
                .core(new CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build())
                .build();

        //Initialize Fabric with disabled crashlytics.
        Fabric.with(this, crashlyticsKit);

        MvpFacade.init();

        mBus = new Bus();

        mObjectGraph = ObjectGraph.create(
                new ContextModule(this),
                new ApplicationComponent(),
                new TaskComponent(),
                new ViewUtilComponent(),
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

            initDatabaseRepositories(getState().getRepositories());
        }
    }

    private void initDatabaseRepositories(Map<Class<? extends Entity>, Repository<? extends Entity>> reposMap)
    {
        reposMap.put(MovieWrapper.class, mMovieRepositiry);
        reposMap.put(ShowWrapper.class, mShowRepository);
    }

    protected List<SQLiteUpgradeStep> getSQLiteUpgradeSteps()
    {
        return Lists.newArrayList();
    }


}
