package com.roodie.materialmovies.util;

import android.content.Context;

import com.roodie.materialmovies.R;
import com.roodie.materialmovies.mvp.views.UiView;
import com.roodie.model.entities.ShowWrapper;
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

    /**
     * Decodes the show status and returns integet representation. May be {@code null} if
     * status is unknown.
     *
     */
    public static int getShowStatusStringId(int encodedStatus) {
        if (encodedStatus == ShowWrapper.Status.CONTINUING) {
            return R.string.show_isalive;
        } else if (encodedStatus == ShowWrapper.Status.ENDED) {
            return R.string.show_isnotalive;
        } else {
            // status unknown, display nothing
            return R.string.show_unknown;
        }
    }

    /**
     * Builds a localized string like "Season 5" or if the number is 0 "Special Episodes".
     */
    public static String getSeasonString(Context context, int seasonNumber) {
        if (seasonNumber == 0) {
            return context.getString(R.string.tv_season_specials);
        } else {
            return context.getString(R.string.tv_season_number, seasonNumber);
        }
    }


}
