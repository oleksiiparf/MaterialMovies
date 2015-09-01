package com.roodie.model.state;

import android.support.v4.util.ArrayMap;
import android.text.TextUtils;

import com.google.common.base.Preconditions;
import com.roodie.model.controllers.DrawerMenuItem;
import com.roodie.model.entities.MovieWrapper;
import com.roodie.model.entities.PersonWrapper;
import com.roodie.model.entities.ShowWrapper;
import com.roodie.model.entities.TmdbConfiguration;
import com.squareup.otto.Bus;

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
    private Map<String, ShowWrapper> mShows;

    private String popularString = "Popular";

    private MoviePaginatedResult mPopularMovies;
    private MoviePaginatedResult mNowPlayingMovies;
    private MoviePaginatedResult mUpcomingMovies;

    private ShowPaginatedResult mPopularShows;
    private ShowPaginatedResult mOnTheAirShows;

    private SearchResult mSearchResult;

    private TmdbConfiguration mConfiguration;

    private DrawerMenuItem mSelectedSideMenuItem;

    public ApplicationState(Bus mEventBus) {
        this.mEventBus = Preconditions.checkNotNull(mEventBus, "eventBus cannot be null");
        mTmdbIdMovies = new ArrayMap<>(INITIAL_MOVIE_MAP_CAPACITY);
        mImdbIdMovies = new ArrayMap<>(INITIAL_MOVIE_MAP_CAPACITY);
        mShows = new ArrayMap<>(INITIAL_MOVIE_MAP_CAPACITY);
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
    public MoviePaginatedResult getPopularMovies() {
        return mPopularMovies;
    }

    @Override
    public void setPopularMovies(MoviePaginatedResult popular) {
        mPopularMovies = popular;
        mEventBus.post(new PopularMoviesChangedEvent());
    }

    public MoviePaginatedResult getNowPlaying() {
        return mNowPlayingMovies;
    }

    public void setNowPlaying(MoviePaginatedResult mNowPlaying) {
        this.mNowPlayingMovies = mNowPlaying;
        mEventBus.post(new InTheatresMoviesChangedEvent());
    }

    public MoviePaginatedResult getUpcoming() {
        return mUpcomingMovies;
    }

    public void setUpcoming(MoviePaginatedResult mUpcoming) {
        this.mUpcomingMovies = mUpcoming;
        mEventBus.post(new UpcomingMoviesChangedEvent());
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

    @Override
    public Map<String, ShowWrapper> getTmdbShows() {
        return mShows;
    }

    @Override
    public ShowWrapper getShow(String id) {
        return mShows.get(id);
    }

    @Override
    public ShowWrapper getShow(int id) {
        return getShow(String.valueOf(id));
    }

    @Override
    public ShowPaginatedResult getPopularShows() {
        return mPopularShows;
    }

    @Override
    public void setPopularShows(ShowPaginatedResult popular) {
        this.mPopularShows = popular;
        mEventBus.post(new PopularShowsChangedEvent());
    }

    @Override
    public ShowPaginatedResult getOnTheAirShows() {
        return mOnTheAirShows;
    }

    @Override
    public void setOnTheAirShows(ShowPaginatedResult onTheAir) {
        this.mOnTheAirShows = onTheAir;
        mEventBus.post(new OnTheAirShowsChangedEvent());
    }

    @Override
    public SearchResult getSearchResult() {
        return mSearchResult;
    }

    @Override
    public void setSearchResult(SearchResult result) {
        mSearchResult = result;
        mEventBus.post(new SearchResultChangedEvent());
    }

}


