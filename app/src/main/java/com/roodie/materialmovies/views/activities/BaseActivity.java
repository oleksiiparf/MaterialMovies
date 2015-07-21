package com.roodie.materialmovies.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.NavUtils;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.roodie.materialmovies.R;
import com.roodie.materialmovies.views.MMoviesDisplay;
import com.roodie.model.Display;

/**
 * Created by Roodie on 27.06.2015.
 */
public class BaseActivity extends ActionBarActivity {

    private View mCardContainer;
    private DrawerLayout mDrawerLayout;
    protected Display mDisplay;
    private NavigationView mNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(getContentViewLayoutId());

        mCardContainer = findViewById(R.id.card_container);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        mDisplay = new MMoviesDisplay(this, mDrawerLayout);
        handleIntent(getIntent(), getDisplay());


        mNavigationView = (NavigationView) findViewById(R.id.navigation_view);
        if (mNavigationView != null) {
            setupDrawerContent(mNavigationView);
        }
    }

    protected void handleIntent(Intent intent, Display display) {
    }


    protected Intent getParentIntent() {
        return NavUtils.getParentActivityIntent(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });
    }



    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.mDisplay = null;
    }

    protected int getContentViewLayoutId() {
        return R.layout.activity_main;
    }

    public Display getDisplay() {
        return mDisplay;
    }

    public DrawerLayout getDrawerLayout() {
        return mDrawerLayout;
    }

    @Override
    public final void setSupportActionBar(@Nullable Toolbar toolbar) {
        super.setSupportActionBar(toolbar);
    }

    public void setSupportActionBar(@Nullable Toolbar toolbar, boolean handleBackground) {
        setSupportActionBar(toolbar);
        getDisplay().setSupportActionBar(toolbar, handleBackground);
    }
}
