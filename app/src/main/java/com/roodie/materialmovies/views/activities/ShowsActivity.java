package com.roodie.materialmovies.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;

import com.roodie.materialmovies.R;
import com.roodie.model.Display;

/**
 * Created by Roodie on 14.08.2015.
 */
public class ShowsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void handleIntent(Intent intent, Display display) {
       if (!display.hasMainFragment()) {
           display.showTvShows();
       }
    }
}
