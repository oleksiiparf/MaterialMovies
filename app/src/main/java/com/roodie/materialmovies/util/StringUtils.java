package com.roodie.materialmovies.util;

import com.roodie.materialmovies.R;
import com.roodie.model.network.NetworkError;

/**
 * Created by Roodie on 21.07.2015.
 */
public class StringUtils {

    public static int getStringResId(NetworkError error) {
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
}
