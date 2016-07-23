package com.roodie.model.entity.mapper;

import com.roodie.model.entities.MovieWrapper;
import com.roodie.model.state.ApplicationState;
import com.roodie.model.state.EntityMapper;
import com.squareup.otto.Bus;
import com.uwetrottmann.tmdb.entities.Genre;
import com.uwetrottmann.tmdb.entities.Movie;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


/**
 * Created by Roodie on 11.07.2016.
 */

public class EntityMapperTest {

    private static final int FAKE_ID = 123;
    private static final String FAKE_TITLE = "Title";
    private static final String FAKE_OVERVIEW = "Overview";

    private EntityMapper mMapper;

    private ApplicationState mState;

    @Before
    public void setUp() throws Exception {
        Bus bus = new Bus();
        mState = new ApplicationState(bus);
        mMapper = new EntityMapper(mState);
    }

    @Test
    public void testTransformMovieEntity() {
        Movie movieEntity = createFakeMovieEntity();
        MovieWrapper movie = mMapper.map(movieEntity);

        //assertThat(movie, is(instanceOf(User.class)));
        assertThat(movie.getTmdbId(), is(FAKE_ID));
        assertThat(movie.getTitle(), is(FAKE_TITLE));
        assertThat(movie.getOverview(), is(FAKE_OVERVIEW));
    }

    @Test
    public void testTransformGenreEntityCollection() {
        Genre mockGenreEntityOne = createGenreEntityFromId(28);
        Genre mockGenreEntityTwo = createGenreEntityFromId(14);

        List<Genre> genreEntityList = new ArrayList<Genre>(5);
        genreEntityList.add(mockGenreEntityOne);
        genreEntityList.add(mockGenreEntityTwo);


        Collection<com.roodie.model.entities.Genre> genreCollection = mMapper.mapGenres(genreEntityList);
        assertThat(genreCollection.toArray()[0], is(instanceOf(com.roodie.model.entities.Genre.class)));
        assertThat(genreCollection.toArray()[1], is(instanceOf(com.roodie.model.entities.Genre.class)));
        assertThat(genreCollection.size(), is(2));
    }

    private Movie createFakeMovieEntity() {
        Movie movie = new Movie();
        movie.id = FAKE_ID;
        movie.title = FAKE_TITLE;
        movie.overview = FAKE_OVERVIEW;
        return movie;
    }

    private Genre createGenreEntityFromId(int id) {
        Genre genre = new Genre();
        genre.id = id;
        genre.name = FAKE_TITLE;
        return genre;
    }

}
