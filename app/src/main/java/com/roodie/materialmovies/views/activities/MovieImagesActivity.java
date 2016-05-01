package com.roodie.materialmovies.views.activities;

import android.content.Intent;

import com.roodie.materialmovies.R;
import com.roodie.model.Display;

/**
 * Created by Roodie on 27.06.2015.
 */
public class MovieImagesActivity extends BaseNavigationActivity {

    @Override
    protected Intent getParentIntent() {
        final Intent currentIntent = getIntent();

        final Intent intent = super.getParentIntent();
        intent.putExtra(Display.PARAM_ID, currentIntent.getStringExtra(Display.PARAM_ID));

        return intent;
    }

    @Override
    protected void handleIntent(Intent intent, Display display) {
        if (!display.hasMainFragment()) {
            display.showMovieImagesFragment(intent.getStringExtra(Display.PARAM_ID));
        }
    }

    @Override
    protected int getContentViewLayoutId() {
        return R.layout.fragment_no_drawer;
    }
}
