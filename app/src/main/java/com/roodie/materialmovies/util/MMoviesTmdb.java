package com.roodie.materialmovies.util;

import android.content.Context;

import com.uwetrottmann.tmdb.Tmdb;

import retrofit.RestAdapter;
import retrofit.client.OkClient;

/**
 * Created by Roodie on 14.07.2015.
 */
public class MMoviesTmdb extends Tmdb {

    private final Context context;

    private static final String LOG_TAG = "MMoviesTmdb";


    public MMoviesTmdb(Context context) {
        this.context = context.getApplicationContext();
    }

    @Override
    protected RestAdapter.Builder newRestAdapterBuilder() {
        return new RestAdapter.Builder().setClient(
                new OkClient(MMoviesServiceUtils.getCachingOkHttpClient(context)));
    }


}