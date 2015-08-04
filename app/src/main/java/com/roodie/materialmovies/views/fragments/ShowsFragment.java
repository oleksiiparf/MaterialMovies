package com.roodie.materialmovies.views.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.roodie.materialmovies.R;
import com.roodie.materialmovies.mvp.views.MovieView;
import com.roodie.materialmovies.views.fragments.base.BaseFragment;
import com.roodie.model.Display;
import com.roodie.model.network.NetworkError;

/**
 * Created by Roodie on 01.08.2015.
 */
public class ShowsFragment extends BaseFragment implements MovieView {

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //set actionbar up navigation
        final Display display = getDisplay();
        if (!isModal()) {
            display.showUpNavigation(getQueryType() != null && getQueryType().showUpNavigation());
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_shows, container, false);
    }


    @Override
    public void showError(NetworkError error) {

    }

    @Override
    public void showLoadingProgress(boolean visible) {

    }

    @Override
    public void showSecondaryLoadingProgress(boolean visible) {

    }

    @Override
    public String getRequestParameter() {
        return null;
    }

    @Override
    public MovieQueryType getQueryType() {
        return MovieQueryType.SHOWS;
    }

    @Override
    public boolean isModal() {
        return false;
    }

}
