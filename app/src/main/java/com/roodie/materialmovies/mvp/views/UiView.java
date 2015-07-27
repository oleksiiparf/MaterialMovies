package com.roodie.materialmovies.mvp.views;


/**
 * Created by Roodie on 25.06.2015.
 */
public interface UiView {

    MovieQueryType getQueryType();

    boolean isModal();

    public enum MovieQueryType {
        POPULAR,
        MOVIE_DETAIL,
        MOVIE_RELATED,
        MOVIE_CAST,
        MOVIE_CREW,
        MOVIE_IMAGES,
        PERSON_DETAIL,
        PERSON_CREDITS_CAST,
        PERSON_CREDITS_CREW,
        NONE
    }

}
