package com.roodie.materialmovies.views.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.roodie.materialmovies.R;
import com.roodie.materialmovies.views.fragments.base.BaseFragment;

/**
 * Created by Roodie on 22.08.2015.
 */
public class SearchPeopleGridFragment extends BaseFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.movie_view_recycler, container, false);
    }
}
