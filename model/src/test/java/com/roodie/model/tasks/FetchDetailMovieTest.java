package com.roodie.model.tasks;

import com.roodie.model.util.BackgroundExecutor;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verifyZeroInteractions;

/**
 * Created by Roodie on 02.07.2016.
 */

public class FetchDetailMovieTest {

    private FetchDetailMovieRunnable mRunnable;

    @Mock
    BackgroundExecutor mExecutor;

    public static final Integer FAKE_MOVIE_ID = 13456;

    public static final Integer FAKE_CALLING_ID = 1;

    @Mock private FetchDetailMovieRunnable mTask;

    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);

        mRunnable = new FetchDetailMovieRunnable(FAKE_CALLING_ID, FAKE_MOVIE_ID);

    }

    @Test
    public void testGetMovieDetail() {

        FetchDetailMovieRunnable spyRunnable = spy(mRunnable);
        mExecutor.execute(spyRunnable);

        verifyZeroInteractions(spyRunnable);
        assertEquals(FAKE_CALLING_ID, (Integer)spyRunnable.getCallingId());
    }



}
