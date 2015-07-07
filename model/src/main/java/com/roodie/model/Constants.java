package com.roodie.model;

/**
 * Created by Roodie on 24.06.2015.
 */
public class Constants {

    public static final String TMDB_API_KEY = "8673a5378a15fbea14bb426f5222f4ed";

    public static final boolean DEBUG_NETWORK = false;

    public static final int CONNECT_TIMEOUT_MILLIS = 15 * 1000; // 15s
    public static final int READ_TIMEOUT_MILLIS = 20 * 1000; // 20s

    private static final long DAY_IN_MILLIS = 24 * 60 * 60 * 1000;

    public static final long STALE_MOVIE_DETAIL_THRESHOLD = 2 * DAY_IN_MILLIS;
    public static final long FULL_MOVIE_DETAIL_ATTEMPT_THRESHOLD = 60 * 60 * 1000; // 60 secs

}
