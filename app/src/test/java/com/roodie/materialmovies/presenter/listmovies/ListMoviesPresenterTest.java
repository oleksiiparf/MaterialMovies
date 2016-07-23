package com.roodie.materialmovies.presenter.listmovies;

import com.google.common.collect.Lists;
import com.roodie.materialmovies.mvp.presenters.ListMoviesPresenter;
import com.roodie.materialmovies.mvp.views.ListMoviesView;
import com.roodie.model.entities.MovieWrapper;
import com.roodie.model.state.ApplicationState;
import com.roodie.model.state.MoviesState;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static com.roodie.materialmovies.mvp.views.UiView.MMoviesQueryType.POPULAR_MOVIES;
import static com.roodie.materialmovies.mvp.views.UiView.MMoviesQueryType.UPCOMING_MOVIES;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


/**
 * Created by Roodie on 03.07.2016.
 */

public class ListMoviesPresenterTest {

    public static final String FAKE_PARAMETER = "12345";

    private static final int FAKE_VIEW_ID = 42;


    @Mock
    private ListMoviesView mView;

    @Mock
    private ListMoviesPresenter classToTest;

    @Mock
    ApplicationState mState;

    private static List<MovieWrapper> UPCOMING = Lists.newArrayList(new MovieWrapper("111"),
            new MovieWrapper("112"));

    @Before
    public void setupPresenter() {
        MockitoAnnotations.initMocks(this);
        //Attach view to the presenter class
        classToTest.attachView(mView);

        //Replace the presenter`s viewState by assigned view
        when(classToTest.getViewState()).thenReturn(mView);
        when(classToTest.getId(classToTest.getViewState())).thenReturn(FAKE_VIEW_ID);
    }

    @Test
    public void initializePreseter_UpdatePopularView(){
        //Attach view to the presenter class
        classToTest.onUiAttached(mView, POPULAR_MOVIES, FAKE_PARAMETER);

        verify(classToTest).fetchPopularIfNeeded(classToTest.getId(classToTest.getViewState()));

        verify(classToTest, atLeastOnce()).populateUi(classToTest.getViewState(), POPULAR_MOVIES);
    }

    @Test
    public void testPerformScrollToBottom(){

        final ApplicationState.MoviePaginatedResult result = new MoviesState.MoviePaginatedResult();
        when(classToTest.canFetchNextPage(result)).thenReturn(true);

        //Set movie cached
        when(mState.getPopularMovies()).thenReturn(result);

        //when
        classToTest.fetchPopularIfNeeded(classToTest.getId(classToTest.getViewState()));

        //then
        verify(classToTest, atLeastOnce()).populateUi(classToTest.getViewState(), POPULAR_MOVIES);

    }

    @Test
    public void testPerformRefreshUpcomingMovies_UpdateUi() {
        int callingId = classToTest.getId(classToTest.getViewState());
        //Set nul to cache
        mState.setUpcoming(callingId, null);

        //Execute fetching the first page of upcoming movies
        classToTest.fetchUpcoming(callingId, 1);

        //Update upcoming movies list
        MoviesState.MoviePaginatedResult result = new MoviesState.MoviePaginatedResult();
        result.items = UPCOMING;
        mState.setUpcoming(callingId, result);

        // Update the Ui
        verify(classToTest, atLeastOnce()).populateUi(classToTest.getViewState(), UPCOMING_MOVIES);


    }


}
