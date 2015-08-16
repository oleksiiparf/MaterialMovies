package com.roodie.materialmovies.views.activities;

import android.content.Intent;
import android.os.Bundle;

import com.roodie.materialmovies.R;
import com.roodie.model.Display;

/**
 * Created by Roodie on 27.06.2015.
 */
public class MovieActivity extends BaseActivity {


    @Override
    protected int getContentViewLayoutId() {
        return R.layout.activity_no_drawer;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTransationName(getDisplay().PARAM_IMAGE);
    }

    @Override
    protected void handleIntent(Intent intent, Display display) {
      if (!display.hasMainFragment()) {
          display.showMovieDetailFragment(intent.getStringExtra(Display.PARAM_ID), intent.getIntArrayExtra(Display.PARAM_LOCATION), getTransationName());
      }
    }
}
