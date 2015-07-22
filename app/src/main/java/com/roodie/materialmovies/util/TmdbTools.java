package com.roodie.materialmovies.util;

import android.content.Context;

import com.roodie.materialmovies.settings.TmdbSettings;

/**
 * Created by Roodie on 22.07.2015.
 */
public class TmdbTools {

    public enum ProfileImageSize {

        W45("w45"),
        W185("w185"),
        H632("h632"),
        ORIGINAL("original");

        private final String value;

        private ProfileImageSize(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    /**
     * Build url to a profile image using the given size spec and current TMDb image url.
     */
    public static String buildProfileImageUrl(Context context, String path, ProfileImageSize size) {
        return TmdbSettings.getImageBaseUrl(context) + size + path;
    }

}
