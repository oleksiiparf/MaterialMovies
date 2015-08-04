package com.roodie.materialmovies.mvp.views;


/**
 * Created by Roodie on 25.06.2015.
 */
public interface UiView {

    MovieQueryType getQueryType();

    boolean isModal();

    public enum MovieQueryType {
        POPULAR,
        UPCOMING,
        IN_THEATERS,
        MOVIES_TAB,
        SHOWS,
        MOVIE_DETAIL,
        MOVIE_RELATED,
        MOVIE_CAST,
        MOVIE_CREW,
        MOVIE_IMAGES,
        PERSON_DETAIL,
        PERSON_CREDITS_CAST,
        PERSON_CREDITS_CREW,
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
                    return true;
                default:
                    return false;
            }
        }
    }

    public static enum MovieTab {
        POPULAR, IN_THEATRES, UPCOMING
    }

}
