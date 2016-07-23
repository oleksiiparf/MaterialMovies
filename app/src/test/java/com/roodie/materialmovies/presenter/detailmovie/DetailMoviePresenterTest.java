package com.roodie.materialmovies.presenter.detailmovie;

import com.roodie.materialmovies.mvp.presenters.DetailMoviePresenter;
import com.roodie.materialmovies.mvp.views.MovieDetailView;
import com.roodie.materialmovies.mvp.views.UiView;
import com.roodie.model.entities.MovieWrapper;
import com.roodie.model.state.ApplicationState;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Roodie on 29.06.2016.
 */

public class DetailMoviePresenterTest {

    public static final String FAKE_PARAMETER = "12345";

    public static final String TITLE_TEST = "title";

    private static final int FAKE_VIEW_ID = 42;

    private static MovieWrapper MOVIE = new MovieWrapper(FAKE_PARAMETER);

    @Mock
    private MovieDetailView mDetailView;

    @Mock
    private DetailMoviePresenter classToTest;

    @Mock
    private ApplicationState mState;


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        // Get a reference to the View
        classToTest.attachView(mDetailView);

        when(classToTest.getViewState()).thenReturn(mDetailView);
        when(classToTest.getId(classToTest.getViewState())).thenReturn(FAKE_VIEW_ID);
    }

    @Test
    public void testThatViewStateImpelementsMovieView(){

        assertThat(classToTest.getViewState(), instanceOf(MovieDetailView.class));
    }


    @Test
    public void testInitializePreseter_UpdateView(){

        //when the view is attached to presenter
        classToTest.attachUiByParameter(mDetailView, FAKE_PARAMETER);

        verify(classToTest).populateUi(classToTest.getViewState(), FAKE_PARAMETER);
    }

    @Test
    public void testUpdateMovieWatched(){

        MovieWrapper movie = new MovieWrapper(FAKE_PARAMETER);
        movie.setTitle(TITLE_TEST);
        //Set movie status watched to false
        movie.setWatched(false);

        classToTest.toggleMovieWatched(mDetailView, movie);
        //verify if mark watched function is performed
        verify(classToTest).markMovieSeen(classToTest.getId(classToTest.getViewState()), movie);


        //populate ui after task has been performed
        verify(classToTest).populateUi(classToTest.getViewState(), movie.getId());
    }

    @Test
    public void testClickOnCard_ShowCastDialog() {

        classToTest.attachUiByParameter(mDetailView, FAKE_PARAMETER);

        verify(classToTest, atLeastOnce()).populateUi(classToTest.getViewState(), FAKE_PARAMETER);

        //Simulate click to show dialog
        mDetailView.showMovieCreditsDialog(UiView.MMoviesQueryType.MOVIE_CAST);
        verify(mDetailView).showMovieCreditsDialog(any(UiView.MMoviesQueryType.class));
    }

    @Test
    public void testGetMovieDetailRequestExecution(){

        MovieWrapper movie = new MovieWrapper(FAKE_PARAMETER);
        movie.setTitle(TITLE_TEST);

        //Set movie cached
        when(mState.getMovie(FAKE_PARAMETER)).thenReturn(movie);
        when(mState.getMovie(Integer.valueOf(FAKE_PARAMETER))).thenReturn(movie);

        //Execute fetching
        classToTest.fetchDetailMovieIfNeeded(classToTest.getId(mDetailView), FAKE_PARAMETER);
        // verify if fetching task has been executed
        verify(classToTest, times(1)).fetchDetailMovieFromTmdb(classToTest.getId(classToTest.getViewState()), movie.getTmdbId());
    }

    @Test
    public void testRequestMovieInstanceFromCache(){

        MovieWrapper movie = mState.getMovie(FAKE_PARAMETER);
        // There is no movie instance in the cache
        assertNull(movie);

        // Than add movie to the cache of the app
        when(mState.getMovie(FAKE_PARAMETER)).thenReturn(MOVIE);
        assertNotNull(mState.getMovie(FAKE_PARAMETER));
    }



}
