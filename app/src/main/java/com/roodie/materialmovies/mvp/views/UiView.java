package com.roodie.materialmovies.mvp.views;


/**
 * Created by Roodie on 25.06.2015.
 */
public interface UiView {

    MovieQueryType getQueryType();

    boolean isModal();

    public enum MovieQueryType {
        POPULAR_MOVIES,
        UPCOMING_MOVIES,
        IN_THEATERS_MOVIES,
        MOVIES_TAB,
        SHOWS_TAB,
        SHOWS,
        MOVIE_DETAIL,
        MOVIE_RELATED,
        MOVIE_CAST,
        MOVIE_CREW,
        MOVIE_IMAGES,
        PERSON_DETAIL,
        PERSON_CREDITS_CAST,
        PERSON_CREDITS_CREW,
        POPULAR_SHOWS,
        ON_THE_AIR_SHOWS,
        SEARCH,
        SEARCH_MOVIES,
        SEARCH_SHOWS,
        SEARCH_PEOPLE,
        NONE;

        public boolean showUpNavigation() {
            switch (this) {
                case MOVIE_DETAIL:
                case MOVIE_RELATED:
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
                    return true;
                default:
                    return false;
            }
        }
    }

    public static enum MovieTabs {
        POPULAR, IN_THEATRES, UPCOMING
    }

    public static enum ShowTabs {
        POPULAR, ON_THE_AIR
    }

    public static enum SearchMediaType {
        MOVIES, SHOWS, PEOPLE
    }

}
