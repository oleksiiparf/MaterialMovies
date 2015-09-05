package com.roodie.materialmovies.views.activities;

import android.content.Intent;
import android.os.Bundle;

import com.roodie.model.Display;

/**
 * Created by Roodie on 27.06.2015.
 */
public class MovieActivity extends BaseNavigationActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTransationName(getDisplay().PARAM_IMAGE);
    }

    @Override
    protected void handleIntent(Intent intent, Display display) {
      if (!display.hasMainFragment()) {
          if (intent.getIntArrayExtra(Display.PARAM_LOCATION) != null) {
              display.showMovieDetailFragmentByAnimation(intent.getStringExtra(Display.PARAM_ID), intent.getIntArrayExtra(Display.PARAM_LOCATION));
          } else {
              display.showMovieDetailFragmentBySharedElements(intent.getStringExtra(Display.PARAM_ID), intent.getStringExtra(Display.PARAM_IMAGE));
          }
          }
    }
}
