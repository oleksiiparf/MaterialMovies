/*
 * Copyright 2014 Chris Banes
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.roodie.model.util;

import android.text.TextUtils;

import com.google.common.base.Preconditions;
import com.google.common.base.Verify;
import com.roodie.model.entities.MovieWrapper;
import com.roodie.model.entities.PersonCreditWrapper;
import com.roodie.model.entities.PersonWrapper;
import com.roodie.model.entities.ShowWrapper;

import java.net.URLEncoder;


public class ImageHelper {

    private static final boolean RESIZE_ALL = false;

    private  static final String mTmdbBaseUrl = "http://image.tmdb.org/t/p/";
    private  static  final int[] mTmdbPosterSizes = {92, 154, 185, 342, 500, 780};
    private  static  final int[] mTmdbBackdropSizes = {300, 780, 1280};
    private  static  final int[] mTmdbProfileSizes = {45, 185};


    public static String getPosterUrl(final PersonCreditWrapper credit, final int width, final int height) {
        final String imageUrl = credit.getPosterPath();
        Preconditions.checkNotNull(imageUrl, "movie must have poster url");
        String url = buildTmdbPosterUrl(imageUrl, width, RESIZE_ALL);
        return RESIZE_ALL ? getResizedUrl(url, width, height) : url;
    }

    public static String getPosterUrl(final MovieWrapper movie, final int width, final int height) {
        String url = null;

        if (!TextUtils.isEmpty(movie.getPosterUrl())) {
            url = buildTmdbPosterUrl(movie.getPosterUrl(),
                    width, RESIZE_ALL);
        }

        Verify.verifyNotNull(url);

        return RESIZE_ALL ? getResizedUrl(url, width, height) : url;
    }

    public static String getPosterUrl(final ShowWrapper movie, final int width, final int height) {
        String url = null;

        if (!TextUtils.isEmpty(movie.getPosterUrl())) {
            url = buildTmdbPosterUrl(movie.getPosterUrl(),
                    width, RESIZE_ALL);
        }

        Verify.verifyNotNull(url);

        return RESIZE_ALL ? getResizedUrl(url, width, height) : url;
    }

    public static String getFanartUrl(final MovieWrapper movie, final int width, final int height) {
        String url = null;

        if (!TextUtils.isEmpty(movie.getTmdbBackdropUrl())) {
            url = buildTmdbBackdropUrl(movie.getTmdbBackdropUrl(), width, RESIZE_ALL);
        }

        Verify.verifyNotNull(url);

        return RESIZE_ALL ? getResizedUrl(url, width, height) : url;
    }

    public static String getFanartUrl(final MovieWrapper.BackdropImage image,
                               final int width, final int height) {

        final String imageUrl = image.url;
        Preconditions.checkNotNull(imageUrl, "image must have backdrop url");

        String url = null;
        url = buildTmdbBackdropUrl(imageUrl, width, RESIZE_ALL);

        return RESIZE_ALL ? getResizedUrl(url, width, height) : url;
    }


    public static String getProfileUrl(final PersonWrapper person, final int width, final int height) {
        final String imageUrl = person.getPictureUrl();
        Preconditions.checkNotNull(imageUrl, "movie must have picture url");
        String url = null;
        url = buildTmdbBackdropUrl(imageUrl, width, RESIZE_ALL);
        return RESIZE_ALL ? getResizedUrl(url, width, height) : url;
    }

    public static String getResizedUrl(String url, int width, int height) {
        StringBuffer sb = new StringBuffer("https://images1-focus-opensocial.googleusercontent.com/gadgets/proxy");
        sb.append("?container=focus");
        sb.append("&resize_w=").append(width);
        sb.append("&resize_h=").append(height);
        sb.append("&url=").append(URLEncoder.encode(url));
        sb.append("&refresh=31536000");
        return sb.toString();
    }

    private static String buildTmdbPosterUrl(String imageUrl, int width, boolean forceLarger) {
        if (mTmdbBaseUrl != null && mTmdbPosterSizes != null) {
            return buildTmdbUrl(mTmdbBaseUrl, imageUrl,
                    selectSize(width, mTmdbPosterSizes, forceLarger));
        } else {
            return null;
        }
    }

    private static String buildTmdbBackdropUrl(String imageUrl, int width, boolean forceLarger) {
        if (mTmdbBaseUrl != null && mTmdbBackdropSizes != null) {
            return buildTmdbUrl(mTmdbBaseUrl, imageUrl,
                    selectSize(width, mTmdbBackdropSizes, forceLarger));
        } else {
            return null;
        }
    }

    private  String buildTmdbProfileUrl(String imageUrl, int width, boolean forceLarger) {
        if (mTmdbBaseUrl != null && mTmdbProfileSizes != null) {
            return buildTmdbUrl(mTmdbBaseUrl, imageUrl,
                    selectSize(width, mTmdbProfileSizes, forceLarger));
        } else {
            return null;
        }
    }

    private static int selectSize(final int width, final int[] widths, final boolean forceLarger) {
        int previousBucketWidth = 0;

        for (int i = 0; i < widths.length; i++) {
            final int currentBucketWidth = widths[i];

            if (width < currentBucketWidth) {
                if (forceLarger || previousBucketWidth != 0) {
                    // We're in between this and the previous bucket
                    final int bucketDiff = currentBucketWidth - previousBucketWidth;
                    if (width < previousBucketWidth + (bucketDiff / 2)) {
                        return previousBucketWidth;
                    } else {
                        return currentBucketWidth;
                    }
                } else {
                    return currentBucketWidth;
                }
            } else if (i == widths.length - 1) {
                // If we get here then we're larger than a bucket
                if (width < currentBucketWidth * 2) {
                    return currentBucketWidth;
                }
            }

            previousBucketWidth = currentBucketWidth;
        }
        return Integer.MAX_VALUE;
    }


    private static String buildTmdbUrl(String baseUrl, String imagePath, int width) {
        StringBuilder url = new StringBuilder(baseUrl);
        if (width == Integer.MAX_VALUE) {
            url.append("original");
        } else {
            url.append('w').append(width);
        }
        url.append(imagePath);
        return url.toString();
    }
}
