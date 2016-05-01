package com.roodie.materialmovies.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.roodie.materialmovies.R;
import com.roodie.materialmovies.mvp.presenters.MainPresenter;
import com.roodie.materialmovies.mvp.views.MainView;
import com.roodie.model.Display;

/**
 * Created by Roodie on 27.06.2015.
 */
public class WatchlistActivity extends BaseNavigationActivity implements MainView {

    @InjectPresenter
    MainPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDrawerLayout.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.menu_refresh: {
                //Should be implemented in fragments
                return false;
            }
            case  R.id.menu_search:
                getDisplay().showSearchFragment();
                return true;
        }
        return false;
    }

    @Override
    protected void handleIntent(Intent intent, Display display) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            if (!display.hasMainFragment()) {
                display.showSearchFragment();
            }
        } else  {
            if (!display.hasMainFragment())
             display.showMovies();
        }
    }

    @Override
    public void setData(int[] data) {
        updateHeader(data);
    }
}


