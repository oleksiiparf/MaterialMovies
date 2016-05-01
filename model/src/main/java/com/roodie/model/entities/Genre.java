package com.roodie.model.entities;

import com.roodie.model.R;

/**
 * Created by Roodie on 16.03.2016.
 */
public enum Genre {

    ACTION(28, R.string.genre_action, R.drawable.genre_action),
    ANIMATION(16, R.string.genre_animation, R.drawable.genre_anime),
    COMEDY(35, R.string.genre_comedy, R.drawable.genre_comedy),
    CRIME(80, R.string.genre_crime, R.drawable.genre_action),
    DOCUMENTARY(99, R.string.genre_documentary, R.drawable.genre_docu),
    DRAMA(18, R.string.genre_drama, R.drawable.genre_drama),
    DETECTIVE(9648, R.string.genre_detective, R.drawable.genre_action),
    EDUCATION(10761, R.string.genre_scienceFiction, R.drawable.genre_docu),
    FAMILY(10751, R.string.genre_family, R.drawable.genre_tv),
    FANTASY(14, R.string.genre_fantasy, R.drawable.genre_fantasy),
    KIDS(10762, R.string.genre_kids, R.drawable.genre_kids),
    MYSTERY(9648, R.string.genre_mystery, R.drawable.genre_sf),
    NEWS(10763, R.string.genre_news, R.drawable.genre_tv),
    REALITY(10764, R.string.genre_reality, R.drawable.genre_miniseries),
    SOAP(10766, R.string.genre_soap, R.drawable.genre_soap),
    SCIENSEFICTION(878, R.string.genre_scienceFiction, R.drawable.genre_tv),
    SCIFI(10765, R.string.genre_sciFiAndFantasy, R.drawable.genre_fantasy),
    TALK(10767, R.string.genre_talkShow, R.drawable.genre_miniseries),
    WAR(10768, R.string.genre_war, R.drawable.genre_docu),
    WESTERN(37, R.string.genre_western, R.drawable.genre_western);



    private int tmdbId;
    private int imageResId;
    private int resId;

    Genre(int tmdbId, int resId, int imageResId) {
        this.tmdbId = tmdbId;
        this.resId = resId;
        this.imageResId = imageResId;
    }

    public static Genre fromName(int id) {
        for (Genre genre : Genre.values()) {
            if (genre.getId() == id) {
                return genre;
            }
        }
        return null;
    }

    public int getId() {
        return tmdbId;
    }

    public int getResId() {
        return resId;
    }


    public int getImageResId() {
        return imageResId;
    }
}
