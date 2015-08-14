package com.roodie.materialmovies.util;

import com.roodie.materialmovies.R;
import com.roodie.materialmovies.mvp.views.UiView;
import com.roodie.model.network.NetworkError;

/**
 * Created by Roodie on 21.07.2015.
 */
public class StringUtils {

    public static int getMoviesStringResId(NetworkError error) {
        switch (error) {
            case NOT_FOUND_TMDB:
                return R.string.error_movie_not_found_tmdb;
            case NETWORK_ERROR:
                return R.string.error_network;
            case UNKNOWN:
                default:
                    return R.string.empty_unknown_error;
        }
    }

    public static int getMoviesStringResId(UiView.MovieTabs tab) {
        switch (tab) {
            case POPULAR:
                return R.string.popular_title;
            case IN_THEATRES:
                return R.string.in_theatres_title;
            case UPCOMING:
                return R.string.upcoming_title;
        }
        return 0;
    }

    public static int getShowsStringResId(UiView.ShowTabs tab) {
        switch (tab) {
            case POPULAR:
                return R.string.popular_title;
            case ON_THE_AIR:
                return R.string.on_the_air_title;
        }
        return 0;
    }

}
