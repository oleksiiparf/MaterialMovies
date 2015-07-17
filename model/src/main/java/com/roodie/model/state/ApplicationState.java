package com.roodie.model.state;

import android.support.v4.util.ArrayMap;
import android.text.TextUtils;

import com.google.common.base.Preconditions;
import com.roodie.model.controllers.DrawerMenuItem;
import com.roodie.model.entities.MovieWrapper;
import com.roodie.model.entities.PersonWrapper;
import com.roodie.model.entities.TmdbConfiguration;
import com.squareup.otto.Bus;

import java.util.List;
import java.util.Map;

/**
 * Created by Roodie on 02.07.2015.
 */

public  class ApplicationState implements BaseState, MoviesState {

    private static final int INITIAL_MOVIE_MAP_CAPACITY = 200;

    private final Bus mEventBus;

    private Map<String, MovieWrapper> mTmdbIdMovies;
    private Map<String, MovieWrapper> mImdbIdMovies;
    private Map<String, PersonWrapper> mPeople;

    private String popularString = "Popular";

    private MoviePaginatedResult mPopular;

    private TmdbConfiguration mConfiguration;

    private DrawerMenuItem mSelectedSideMenuItem;

    public ApplicationState(Bus mEventBus) {
        this.mEventBus = Preconditions.checkNotNull(mEventBus, "eventBus cannot be null");
        mTmdbIdMovies = new ArrayMap<>(INITIAL_MOVIE_MAP_CAPACITY);
        mImdbIdMovies = new ArrayMap<>(INITIAL_MOVIE_MAP_CAPACITY);
        mPeople = new ArrayMap<>();
    }

    @Override
    public void registerForEvents(Object receiver) {
        mEventBus.register(receiver);
    }

    @Override
    public void unregisterForEvents(Object receiver) {
        mEventBus.unregister(receiver);
    }

    @Override
    public DrawerMenuItem getSelectedSideMenuItem() {
        return mSelectedSideMenuItem;
    }

    @Override
    public void setSelectedSideMenuItem(DrawerMenuItem item) {
        this.mSelectedSideMenuItem = item;
    }

    /*
    MovieState implementation
     */

    @Override
    public Map<String, MovieWrapper> getTmdbIdMovies() {
        return mTmdbIdMovies;
    }

    @Override
    public Map<String, MovieWrapper> getImdbIdMovies() {
        return mImdbIdMovies;
    }

    @Override
    public void putMovie(MovieWrapper movie) {
        if (!TextUtils.isEmpty(movie.getImdbId())) {
            mImdbIdMovies.put(movie.getImdbId(), movie);
        }
        if (movie.getTmdbId() != null) {
            mTmdbIdMovies.put(String.valueOf(movie.getTmdbId()), movie);
        }
    }

    @Override
    public MoviePaginatedResult getPopular() {
        return mPopular;
    }

    public String getPopularString() {
        return popularString;
    }

    @Override
    public void setPopular(MoviePaginatedResult popular) {
        mPopular = popular;
        List<MovieWrapper> items = mPopular.items;
        if (items != null) {
            System.out.println("ApplicationState: items != null");
        }
        System.out.println("ApplicationState: Popular: " + items);
        mEventBus.post(new PopularChangedEvent());
    }

    @Override
    public TmdbConfiguration getTmdbConfiguration() {
        return mConfiguration;
    }

    @Override
    public void setTmdbConfiguration(TmdbConfiguration configuration) {
        mConfiguration = configuration;
        mEventBus.post(new TmdbConfigurationChangedEvent());

    }

    @Override
    public Map<String, PersonWrapper> getPeople() {
        return mPeople;
    }

    @Override
    public PersonWrapper getPerson(int id) {
        return getPerson(String.valueOf(id));
    }

    @Override
    public PersonWrapper getPerson(String id) {
        return mPeople.get(id);
    }

    @Override
    public MovieWrapper getMovie(int id) {
        return getMovie(String.valueOf(id));
    }

    @Override
    public MovieWrapper getMovie(String id) {
        MovieWrapper movie = mTmdbIdMovies.get(id);

        if (movie == null) {
            movie = mImdbIdMovies.get(id);
        }
        return movie;
    }
}


