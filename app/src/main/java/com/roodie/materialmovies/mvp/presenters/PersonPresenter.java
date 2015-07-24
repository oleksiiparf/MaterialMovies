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
import com.roodie.model.tasks.BaseMovieRunnable;
import com.roodie.model.util.BackgroundExecutor;

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

    @Override
    public void initialize() {

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


    private <R> void executeTask(BaseMovieRunnable<R> task) {
        mExecutor.execute(task);
    }


    public void showMovieDetail(PersonCreditWrapper credit, Bundle bundle) {
        Preconditions.checkNotNull(credit, "credit cannot be null");

        Display display = getDisplay();
        if (display != null) {
            display.startMovieDetailActivity(String.valueOf(credit.getId()), bundle);
        }
    }



    public interface PersonView extends MovieView {

        void setPerson(PersonWrapper person);

        void showPersonDetail(PersonWrapper person, Bundle bundle);

    }

}
