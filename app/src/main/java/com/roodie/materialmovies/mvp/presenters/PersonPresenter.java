package com.roodie.materialmovies.mvp.presenters;

import android.os.Bundle;

import com.google.common.base.Preconditions;
import com.roodie.materialmovies.mvp.views.MovieView;
import com.roodie.materialmovies.qualifiers.GeneralPurpose;
import com.roodie.model.Display;
import com.roodie.model.entities.PersonCreditWrapper;
import com.roodie.model.entities.PersonWrapper;
import com.roodie.model.state.ApplicationState;
import com.roodie.model.state.AsyncDatabaseHelper;
import com.roodie.model.state.BaseState;
import com.roodie.model.state.MoviesState;
import com.roodie.model.tasks.BaseMovieRunnable;
import com.roodie.model.tasks.FetchPersonRunnable;
import com.roodie.model.util.BackgroundExecutor;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

/**
 * Created by Roodie on 25.06.2015.
 */
public class PersonPresenter extends BasePresenter {

    private PersonView mPersonView;

    private final BackgroundExecutor mExecutor;
    private final AsyncDatabaseHelper mDbHelper;
    private final ApplicationState mState;

    private boolean attached = false;


    @Inject
    public PersonPresenter(
            @GeneralPurpose BackgroundExecutor executor,
            AsyncDatabaseHelper dbHelper, ApplicationState state) {
        super();
        mState = Preconditions.checkNotNull(state, "moviesState cannot be null");
        mExecutor = Preconditions.checkNotNull(executor, "executor can not be null");
        mDbHelper = Preconditions.checkNotNull(dbHelper, "executor can not be null");
    }

    @Subscribe
    public void onPersonCreditsChanged(MoviesState.PersonChangedEvent event) {
        populateUi(event);
    }

    @Subscribe
    public void onNetworkError(BaseState.OnErrorEvent event) {
        if (attached && null != event.error) {
            mPersonView.showError(event.error);
        }
    }

    @Subscribe
    public void onLoadingProgressVisibilityChanged(BaseState.ShowLoadingProgressEvent event) {
        if (attached) {
            if (event.secondary) {
                mPersonView.showSecondaryLoadingProgress(event.show);
            } else {
                mPersonView.showLoadingProgress(event.show);
            }
        }
    }

    @Override
    public void initialize() {
        fetchPersonIfNeeded(3, mPersonView.getRequestParameter());
    }

    public void attachView (PersonView view) {
        Preconditions.checkNotNull(view, "View cannot be null");
        this.mPersonView = view;
        attached = true;
    }

    @Override
    public void onResume() {
        mState.registerForEvents(this);
    }

    @Override
    public void onPause() {
        mState.unregisterForEvents(this);
    }

    private void fetchPersonIfNeeded(final int callingId, String id) {
        Preconditions.checkNotNull(id, "id cannot be null");

        PersonWrapper person = mState.getPerson(id);
        if (person == null || !person.isFetchedCredits()) {
            fetchPerson(callingId, Integer.parseInt(id));
        }
    }

    private void fetchPerson(final int callingId, int id) {
        executeTask(new FetchPersonRunnable(callingId, id));
    }

    private <R> void executeTask(BaseMovieRunnable<R> task) {
        mExecutor.execute(task);
    }

    public void populateUi(MoviesState.PersonChangedEvent event) {
        Preconditions.checkNotNull(event, "event cannot be null");

        final PersonWrapper person = mState.getPerson(mPersonView.getRequestParameter());
        if (person != null) {
            mPersonView.setPerson(person);
        }
    }

    public void showMovieDetail(PersonCreditWrapper credit, Bundle bundle) {
        Preconditions.checkNotNull(credit, "credit cannot be null");

        Display display = getDisplay();
        if (display != null) {
            display.startMovieDetailActivity(String.valueOf(credit.getId()), bundle);
        }
    }



    public void showPersonCastCredits(PersonWrapper person) {
        Preconditions.checkNotNull(person, "person cannot be null");
        Preconditions.checkNotNull(person.getTmdbId(), "person id cannot be null");

        Display display = getDisplay();
        if (display != null) {
            display.showPersonCastCreditsFragment(String.valueOf(person.getTmdbId()));
        }
    }

    public void showPersonCrewCredits(PersonWrapper person) {
        Preconditions.checkNotNull(person, "person cannot be null");
        Preconditions.checkNotNull(person.getTmdbId(), "person id cannot be null");

        Display display = getDisplay();
        if (display != null) {
            display.showPersonCrewCreditsFragment(String.valueOf(person.getTmdbId()));
        }
    }



    public interface PersonView extends MovieView {

        void setPerson(PersonWrapper person);

        void showPersonDetail(PersonWrapper person, Bundle bundle);

    }

}
