package com.roodie.model.state;

import android.text.TextUtils;

import com.google.common.base.Preconditions;
import com.roodie.model.entities.MovieCreditWrapper;
import com.roodie.model.entities.MovieWrapper;
import com.roodie.model.entities.PersonWrapper;
import com.roodie.model.entities.ShowWrapper;
import com.uwetrottmann.tmdb.entities.CastMember;
import com.uwetrottmann.tmdb.entities.CrewMember;
import com.uwetrottmann.tmdb.entities.Movie;
import com.uwetrottmann.tmdb.entities.Person;
import com.uwetrottmann.tmdb.entities.TvShowComplete;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by Roodie on 07.07.2015.
 */

@Singleton
public class EntitityMapper {

    final ApplicationState mMoviesState;

    @Inject
    public EntitityMapper(ApplicationState mMoviesState) {
        this.mMoviesState = Preconditions.checkNotNull(mMoviesState, "mMoviesState cannot be null");
    }

    MovieWrapper getMovieEntity(String id) {
        if (mMoviesState.getImdbIdMovies().containsKey(id)) {
            return mMoviesState.getImdbIdMovies().get(id);
        } else if (mMoviesState.getTmdbIdMovies().containsKey(id)) {
            return mMoviesState.getTmdbIdMovies().get(id);
        }
        return null;
    }

    void putMovieEntity(MovieWrapper movie) {
        if (!TextUtils.isEmpty(movie.getImdbId())) {
            mMoviesState.getImdbIdMovies().put(movie.getImdbId(), movie);
        }
        if (movie.getTmdbId() != null) {
            mMoviesState.getTmdbIdMovies().put(String.valueOf(movie.getTmdbId()), movie);
        }
    }

    PersonWrapper getPersonEntity(String id) {
        return mMoviesState.getPeople().get(id);
    }

    void putPersonEntity(PersonWrapper entity) {
        mMoviesState.getPeople().put(String.valueOf(entity.getTmdbId()), entity);
    }

    ShowWrapper getShowEntity(String id) {
        return mMoviesState.getTmdbShows().get(id);
    }

    ShowWrapper getShowEntity(int id) {
        return getShowEntity(String.valueOf(id));
    }


    void putShowEntity(ShowWrapper entity) {
        mMoviesState.getTmdbShows().put(String.valueOf(entity.getTmdbId()), entity);
    }

    /*
    Wrap CrewMember to PersonWrapper
     */
    public PersonWrapper map(CrewMember entity) {
        PersonWrapper item = getPersonEntity(String.valueOf(entity.id));

        if (item == null) {
            // No item, so create one
            item = new PersonWrapper();
        }

        // We already have a movie, so just update it wrapped value
        item.set(entity);
        putPersonEntity(item);

        return item;
    }

    public List<MovieCreditWrapper> mapCrewCredits(List<CrewMember> entities) {
        final ArrayList<MovieCreditWrapper> credits = new ArrayList<>(entities.size());
        for (CrewMember entity : entities) {
            credits.add(new MovieCreditWrapper(map(entity), entity.job, entity.department));
        }
        Collections.sort(credits);
        return credits;
    }


    /*
    Wrap CastMember to PersonWrapper
     */
    public PersonWrapper map(CastMember entity) {
        PersonWrapper item = getPersonEntity(String.valueOf(entity.id));

        if (item == null) {
            // No item, so create one
            item = new PersonWrapper();
        }

        // We already have a movie, so just update it wrapped value
        item.set(entity);
        putPersonEntity(item);

        return item;
    }

    public List<MovieCreditWrapper> mapCastCredits(List<CastMember> entities) {
        final ArrayList<MovieCreditWrapper> credits = new ArrayList<>(entities.size());
        for (CastMember entity : entities) {
            credits.add(new MovieCreditWrapper(map(entity), entity.character, entity.order));
        }
        Collections.sort(credits);
        return credits;
    }


    /*
    Wrap Person to PersonWrapper
     */
    public PersonWrapper map(Person entity) {
        PersonWrapper item = getPersonEntity(String.valueOf(entity.id));

        if (item == null) {
            // No item, so create one
            item = new PersonWrapper();
        }

        // We already have a movie, so just update it wrapped value
        item.set(entity);
        putPersonEntity(item);

        return item;
    }

    /*
    Wrap Movie to MovieWrapper
     */
    public MovieWrapper map(Movie entity) {
        MovieWrapper movie = getMovieEntity(String.valueOf(entity.id));

        if (movie == null && entity.imdb_id != null) {
            movie = getMovieEntity(entity.imdb_id);
        }

        if (movie == null) {
            // No movie, so create one
            movie = new MovieWrapper();
        }
        // We already have a movie, so just update it wrapped value
        movie.setFromMovie(entity);
        putMovieEntity(movie);

        return movie;
    }

    /*
    Wrap TvShowComplete to ShowWrapper
     */
    public ShowWrapper map(TvShowComplete entity) {
        ShowWrapper show = getShowEntity(String.valueOf(entity.id));

        if (show == null && entity.id != null) {
            show = getShowEntity(entity.id);
        }

        if (show == null) {
            // No show, so create one
            show = new ShowWrapper();
        }
        // We already have a movie, so just update it wrapped value
        show.setFromShow(entity);
        putShowEntity(show);

        return show;
    }





}
