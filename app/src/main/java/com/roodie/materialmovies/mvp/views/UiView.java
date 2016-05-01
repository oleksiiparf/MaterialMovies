package com.roodie.materialmovies.mvp.views;


import com.arellomobile.mvp.MvpView;

/**
 * Created by Roodie on 25.06.2015.
 */
public interface UiView extends MvpView {

    void updateDisplayTitle(String title);

    void updateDisplaySubtitle(String subtitle);

    enum MMoviesQueryType {
        COMMON_MOVIES,
        POPULAR_MOVIES,
        UPCOMING_MOVIES,
        IN_THEATERS_MOVIES,
        WATCHED,
        COMMON_SHOWS,
        POPULAR_SHOWS,
        ON_THE_AIR_SHOWS,
        WATCHED_SHOWS,
        MOVIES_TAB,
        SHOWS_TAB,
        SHOWS,
        MOVIE_DETAIL,
        RELATED_MOVIES,
        MOVIE_CAST,
        MOVIE_CREW,
        MOVIE_IMAGES,
        PERSON_DETAIL,
        PERSON_CREDITS_CAST,
        PERSON_CREDITS_CREW,
        SEARCH,
        SEARCH_MOVIES,
        SEARCH_SHOWS,
        SEARCH_PEOPLE,
        TV_SHOW_DETAIL,
        TV_SHOW_CAST,
        TV_SHOW_CREW,
        TV_SEASONS_LIST,
        TV_SEASONS,
        TV_SEASON_DETAIL,
        FAVOURITE_MOVIES,
        FAVOURITE_SHOWS,
        NONE;

        public boolean showUpNavigation() {
            switch (this) {
                case COMMON_MOVIES:
                case COMMON_SHOWS:
                case MOVIE_DETAIL:
                case RELATED_MOVIES:
                case MOVIE_CAST:
                case MOVIE_CREW:
                case MOVIE_IMAGES:
                case PERSON_DETAIL:
                case PERSON_CREDITS_CAST:
                case PERSON_CREDITS_CREW:
                case SEARCH:
                case SEARCH_MOVIES:
                case SEARCH_SHOWS:
                case SEARCH_PEOPLE:
                case TV_SHOW_CAST:
                case TV_SHOW_CREW:
                case TV_SHOW_DETAIL:
                case TV_SEASONS_LIST:
                case TV_SEASON_DETAIL:
                case TV_SEASONS:
                    return true;
                default:
                    return false;
            }
        }
    }

    enum MovieTabs {
        POPULAR("Popular"),
        IN_THEATRES("In Theatres"),
        UPCOMING("Upcoming");

        private final String title;

        MovieTabs(String title) {
            this.title = title;
        }

        public String getTitle() {
            return title;
        }

    }

    enum ShowTabs {
        POPULAR, ON_THE_AIR
    }

    enum SearchMediaType {
        MOVIES, SHOWS, PEOPLE
    }

}
