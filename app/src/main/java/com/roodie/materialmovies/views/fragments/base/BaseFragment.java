package com.roodie.materialmovies.views.fragments.base;

import android.app.Activity;
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
       /** if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
         *((BaseActivity) getActivity()).getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
         *   ((BaseActivity) getActivity()).getWindow().setStatusBarColor(getResources().getColor(R.color.color_primary_dark_default));
         * }
         */
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    protected  void setSupportActionBar(Toolbar toolbar) {
        setSupportActionBar(toolbar, true);
    }

    protected void setSupportActionBar(Toolbar toolbar, boolean handle) {
        ((BaseActivity) getActivity()).setSupportActionBar(toolbar, handle);
    }

    public Toolbar getToolbar() {
        return mToolbar;
    }

    protected Display getDisplay() {
        return ((BaseActivity) getActivity()).getDisplay();
    }

    protected BaseActivity getBaseActivity() {
        return (BaseActivity)getActivity();
    }
}
