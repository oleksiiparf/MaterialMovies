package com.roodie.materialmovies.mvp.presenters;

import android.util.Log;
import android.view.View;

import com.google.common.base.Preconditions;
import com.roodie.materialmovies.mvp.views.MovieView;
import com.roodie.materialmovies.qualifiers.GeneralPurpose;
import com.roodie.model.entities.PersonCreditWrapper;
import com.roodie.model.entities.PersonWrapper;
import com.roodie.model.state.ApplicationState;
import com.roodie.model.state.BaseState;
import com.roodie.model.state.MoviesState;
import com.roodie.model.tasks.BaseMovieRunnable;
import com.roodie.model.tasks.FetchPersonRunnable;
import com.roodie.model.util.BackgroundExecutor;
import com.roodie.model.util.Injector;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

/**
 * Created by Roodie on 25.06.2015.
 */
public class PersonPresenter extends BasePresenter<PersonPresenter.PersonView> {

    public static final String LOG_TAG = PersonPresenter.class.getSimpleName();

    private final BackgroundExecutor mExecutor;
    private final Injector mInjector;

    private boolean attached = false;


    @Inject
    public PersonPresenter(
            ApplicationState state,
            @GeneralPurpose BackgroundExecutor executor, Injector injector) {
        super(state);
        mExecutor = Preconditions.checkNotNull(executor, "executor can not be null");
        mInjector = Preconditions.checkNotNull(injector, "injector cannot be null");
    }

    @Subscribe
    public void onPersonInfoChanged(MoviesState.PersonChangedEvent event) {
        Log.d(LOG_TAG, "On Person Changed info");
        populateUi();
    }


    @Subscribe
    public void onNetworkError(BaseState.OnErrorEvent event) {
        if (attached && null != event.error) {
            getView().showError(event.error);
        }
    }

    @Subscribe
    public void onLoadingProgressVisibilityChanged(BaseState.ShowLoadingProgressEvent event) {
        if (attached) {
            if (event.secondary) {
                getView().showSecondaryLoadingProgress(event.show);
            } else {
                getView().showLoadingProgress(event.show);
            }
        }
    }

    @Override
    public void initialize() {

        fetchPersonIfNeeded(3, getView().getRequestParameter());
    }

    /**
     * Fetch person information
     */
    private void fetchPersonIfNeeded(final int callingId, String id) {
        Preconditions.checkNotNull(id, "id cannot be null");

        PersonWrapper person = mState.getPerson(id);
        if (person == null || !person.isFetchedCredits()) {
            fetchPerson(callingId, Integer.parseInt(id));
        } else {
            populateUi();
        }
    }

    private void fetchPerson(final int callingId, int id) {
        executeTask(new FetchPersonRunnable(callingId, id));
    }

    private <R> void executeTask(BaseMovieRunnable<R> task) {
        mInjector.inject(task);
        mExecutor.execute(task);
    }

    public void populateUi() {

        final PersonWrapper person = mState.getPerson(getView().getRequestParameter());
        Log.d(LOG_TAG, "Populate ui: " + getView().getQueryType().toString());
        switch (getView().getQueryType()) {
            case PERSON_DETAIL:
                if (person != null) {
                    getView().updateDisplayTitle(person.getName());
                    getView().setPerson(person);
                }
                break;
        }

    }

    public interface PersonView extends MovieView {

        void setPerson(PersonWrapper person);

        void showMovieDetail(PersonCreditWrapper credit, View view);

        void showPersonCreditsDialog(MovieQueryType queryType);
    }

}
