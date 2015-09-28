package com.roodie.materialmovies.mvp.presenters;

import com.google.common.base.Preconditions;
import com.roodie.materialmovies.mvp.views.BaseSeasonView;
import com.roodie.materialmovies.qualifiers.GeneralPurpose;
import com.roodie.model.entities.SeasonWrapper;
import com.roodie.model.state.ApplicationState;
import com.roodie.model.tasks.BaseMovieRunnable;
import com.roodie.model.util.BackgroundExecutor;
import com.roodie.model.util.Injector;

import javax.inject.Inject;

/**
 * Created by Roodie on 27.09.2015.
 */
class BaseSeasonPresenter<V extends BaseSeasonView> extends BasePresenter<V> {


    private final BackgroundExecutor mExecutor;
    private final Injector mInjector;

    @Inject
    public BaseSeasonPresenter(
            ApplicationState state,
            @GeneralPurpose BackgroundExecutor executor, Injector injector) {
        super(state);
        mExecutor = Preconditions.checkNotNull(executor, "executor can not be null");
        mInjector = Preconditions.checkNotNull(injector, "injector cannot be null");
    }

    @Override
    public void initialize() {

    }

    @Override
    public void attachView(V view) {
        super.attachView(view);
    }

    @Override
    public void detachView(boolean retainInstance) {
        super.detachView(retainInstance);
    }

    public void starSeason(final SeasonWrapper season) {

    }

    protected  <R> void executeTask(BaseMovieRunnable<R> task) {
        mInjector.inject(task);
        mExecutor.execute(task);
    }


}
