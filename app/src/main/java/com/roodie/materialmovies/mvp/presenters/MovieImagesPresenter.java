package com.roodie.materialmovies.mvp.presenters;

import com.google.common.base.Preconditions;
import com.roodie.materialmovies.mvp.views.MovieView;
import com.roodie.materialmovies.qualifiers.GeneralPurpose;
import com.roodie.model.entities.MovieWrapper;
import com.roodie.model.state.ApplicationState;
import com.roodie.model.util.BackgroundExecutor;
import com.roodie.model.util.Injector;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by Roodie on 06.07.2015.
 */
public class MovieImagesPresenter extends BasePresenter {

    private MovieImagesView mView;

    private final ApplicationState mState;
    private final BackgroundExecutor mExecutor;
    private final Injector mInjector;

    private boolean attached = false;

    @Inject
    public MovieImagesPresenter(ApplicationState applicationState,
                                @GeneralPurpose BackgroundExecutor executor,
                                Injector injector) {
        mState = Preconditions.checkNotNull(applicationState, "mState can not be null");
        mExecutor = Preconditions.checkNotNull(executor, "executor cannot be null");
        mInjector = Preconditions.checkNotNull(injector, "injector cannot be null");
    }

    @Override
    public void onResume() {

    }

    @Override
    public void initialize() {

        checkViewAlreadySetted();
    }

    @Override
    public void onPause() {

    }

    public void attachView (MovieImagesView view) {
        Preconditions.checkNotNull(view, "View cannot be null");
        this.mView = view;
        attached = true;

    }

    private void checkViewAlreadySetted() {
        Preconditions.checkState(attached = true, "View not attached");
    }


    public interface MovieImagesView extends MovieView {
        void setItems(List<MovieWrapper.BackgroundImage> images);
    }

}
