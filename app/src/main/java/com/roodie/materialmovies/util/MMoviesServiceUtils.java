package com.roodie.materialmovies.util;

import android.content.Context;
import android.os.Build;
import android.os.StatFs;

import com.google.common.base.Preconditions;
import com.roodie.model.Constants;
import com.roodie.model.util.MMoviesPreferences;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.OkUrlFactory;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Created by Roodie on 11.07.2015.
 */
public  class MMoviesServiceUtils {

    private  final MMoviesPreferences mPreferences;

    private static OkHttpClient cachingHttpClient;
    private static OkUrlFactory cachingUrlFactory;

    private static final String API_CACHE = "api-cache";
    private static final int MIN_DISK_API_CACHE_SIZE = 2 * 1024 * 1024; // 2MB
    private static final int MAX_DISK_API_CACHE_SIZE = 10 * 1024 * 1024; // 10MB

    private static Picasso mPicasso;

    public MMoviesServiceUtils(MMoviesPreferences preferences) {
        mPreferences = Preconditions.checkNotNull(preferences, "preferences cannot be null");
    }


    public static synchronized OkHttpClient getCachingOkHttpClient(Context context) {
        if (cachingHttpClient == null) {
            cachingHttpClient = new OkHttpClient();
            cachingHttpClient.setConnectTimeout(Constants.CONNECT_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
            cachingHttpClient.setReadTimeout(Constants.READ_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
            try {
                File cacheDir = createApiCacheDir(context);
                cachingHttpClient.setCache(new Cache(cacheDir, calculateApiDiskCacheSize(cacheDir)));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return cachingHttpClient;
    }

    static File createApiCacheDir(Context context) {
        File cache = new File(context.getApplicationContext().getCacheDir(), API_CACHE);
        if (!cache.exists()) {
            cache.mkdirs();
        }
        return cache;
    }

    static long calculateApiDiskCacheSize(File dir) {
        long size = MIN_DISK_API_CACHE_SIZE;

        try {
            StatFs statFs = new StatFs(dir.getAbsolutePath());
            long available;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                available = statFs.getBlockCountLong() * statFs.getBlockSizeLong();
            } else {
                available = ((long) statFs.getBlockCount()) * statFs.getBlockSize();
            }
            // Target 2% of the total space.
            size = available / 50;
        } catch (IllegalArgumentException ignored) {
        }

        // Bound inside min/max size for disk cache.
        return Math.max(Math.min(size, MAX_DISK_API_CACHE_SIZE), MIN_DISK_API_CACHE_SIZE);
    }




}
