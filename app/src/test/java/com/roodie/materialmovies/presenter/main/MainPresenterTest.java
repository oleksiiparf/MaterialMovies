package com.roodie.materialmovies.presenter.main;

import com.google.common.collect.Lists;
import com.roodie.materialmovies.mvp.callbacks.WatchedDbLoadCallback;
import com.roodie.materialmovies.mvp.presenters.MainPresenter;
import com.roodie.materialmovies.mvp.views.MainView;
import com.roodie.model.entities.MovieWrapper;
import com.roodie.model.entities.ShowWrapper;
import com.roodie.model.entities.Watchable;
import com.roodie.model.state.ApplicationState;
import com.roodie.model.state.MoviesState;
import com.roodie.model.tasks.FetchWatchedRunnable;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


/**
 * Created by Roodie on 03.07.2016.
 */

public class MainPresenterTest {

    @Mock
    private MainView mView;

    @Mock
    private MainPresenter classToTest;

    @Mock
    private ApplicationState mState;

    private static final int FAKE_VIEW_ID = 42;

    private final static String WATCHABLE_ID = "123";

    private static List<Watchable> WATHCHED = Lists.newArrayList(new MovieWrapper("111"),
            new ShowWrapper("112"));

    @Mock
    private WatchedDbLoadCallback mWatchedCallback;

    @Before
    public void setupPresenter() {
        MockitoAnnotations.initMocks(this);
        classToTest.attachView(mView);
        when(classToTest.getViewState()).thenReturn(mView);
        when(classToTest.getId(mView)).thenReturn(FAKE_VIEW_ID);
    }

    @Test
    public void testThatViewStateImpelementsMainView(){

        assertThat(classToTest.getViewState(), instanceOf(MainView.class));
    }

    @Test
    public void testLoadWatchedFromDb() {

        when(mState.isPopulatedWatchedFromDb()).thenReturn(false);

        //then presenter is asked to activate the task
        classToTest.populateStateFromDb(classToTest.getId(classToTest.getViewState()));

        // Then request is sent to runnable task
        verify(classToTest).fetchWatched(classToTest.getId(classToTest.getViewState()));
        verify(classToTest).executeBackgroundTask(new FetchWatchedRunnable(classToTest.getId(classToTest.getViewState()), mWatchedCallback));

        // Trigger callback so watchable items are loaded from cache
        mWatchedCallback.onFinished(WATHCHED);

        // Set watched list to the app`s cache
        when(mState.getWatched()).thenReturn(WATHCHED);

        // The UI is updated
        verify(classToTest).populateUi(classToTest.getViewState());
        assertEquals(WATHCHED, mState.getWatched());

    }

    @Test
    public void testLoadWatchedEmpty() throws Exception {

        mState.setWatched(new ArrayList<Watchable>());
        // Trigger watched loaded
        classToTest.onWatchedMoviesChanged(new MoviesState.WatchedChangedEvent());

        List<Watchable> items = mState.getWatched();
        assertEquals(0, items.size());

        // Update attached view
        classToTest.getViewState().setData(new int[] {0, 0});

    }
}


