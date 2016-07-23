package com.roodie.model;

import com.roodie.model.entities.ShowWrapper;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Created by Roodie on 12.07.2016.
 */

public class TvShowTest {

    private static final String FAKE_SHOW_ID = "88";

    private ShowWrapper show;

    @Before
    public void setUp() {
        show = new ShowWrapper(FAKE_SHOW_ID);
    }

    @Test
    public void testShowConstructorHappyCase() {
        String tmdbId = String.valueOf(show.getTmdbId());

        assertThat(tmdbId, is(FAKE_SHOW_ID));
    }
}
