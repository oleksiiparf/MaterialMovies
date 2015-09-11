package com.roodie.materialmovies.views.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.common.base.Preconditions;
import com.roodie.materialmovies.R;

/**
 * Created by Roodie on 07.09.2015.
 */
public class MovieDetailFragmentVertical extends MovieDetailFragment {

    public static MovieDetailFragmentVertical newInstance(String id) {
        Preconditions.checkArgument(id != null, "movieId can not be null");

        Bundle bundle = new Bundle();
        bundle.putString(QUERY_MOVIE_ID, id);
        MovieDetailFragmentVertical fragment = new MovieDetailFragmentVertical();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_movie_detail_list_vertical, container, false);
    }






}
