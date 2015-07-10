package com.roodie.materialmovies.views.fragments.base;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.roodie.materialmovies.R;
import com.roodie.materialmovies.views.activities.BaseActivity;
import com.roodie.model.Display;

/**
 * Created by Roodie on 01.07.2015.
 */
public class BaseFragment extends Fragment {

    private Toolbar mToolbar;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
      mToolbar = (Toolbar) view.findViewById(R.id.toolbar);

        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
        }
    }

    protected void setSupportActionBar(Toolbar toolbar) {
        ((BaseActivity) getActivity()).setSupportActionBar(toolbar, true);
    }

    public Toolbar getToolbar() {
        return mToolbar;
    }

    protected Display getDisplay() {
        return ((BaseActivity) getActivity()).getDisplay();
    }
}
