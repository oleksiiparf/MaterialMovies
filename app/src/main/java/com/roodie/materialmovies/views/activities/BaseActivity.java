package com.roodie.materialmovies.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.NavUtils;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.roodie.materialmovies.R;
import com.roodie.materialmovies.views.MMoviesDisplay;
import com.roodie.materialmovies.views.custom_views.MMoviesImageView;
import com.roodie.model.Display;

/**
 * Created by Roodie on 27.06.2015.
 */
public class BaseActivity extends ActionBarActivity {

    private View mCardContainer;
    private DrawerLayout mDrawerLayout;
    protected Display mDisplay;
    private NavigationView mNavigationView;
    private MMoviesImageView mUserProfilePhoto;
    protected String mTransationName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(getContentViewLayoutId());

        mCardContainer = findViewById(R.id.card_container);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        mDisplay = new  MMoviesDisplay(this, mDrawerLayout);
        handleIntent(getIntent(), getDisplay());


        mNavigationView = (NavigationView) findViewById(R.id.navigation_view);

        mUserProfilePhoto = (MMoviesImageView) findViewById(R.id.profile_image);
        if (mNavigationView != null) {
            setupDrawerContent(mNavigationView);
            setupHeader();
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (onHomeButtonPressed()) {
                    return true;
                }
                if (navigateUp()) {
                    return true;
                }
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {

                        //Checking if the item is in checked state or not, if not make it in checked state
                        // if (menuItem.isChecked()) {
                        //     menuItem.setChecked(false);
                        //  } else {
                        if (getDisplay() != null) {

                            //}
                            //Closing drawer on item click
                            mDrawerLayout.closeDrawers();
                            switch (menuItem.getItemId()) {
                                case R.id.menu_movies:
                                    getDisplay().showMovies();
                                    break;
                                case R.id.menu_shows:
                                    getDisplay().showTvShows();
                                    break;
                                case R.id.menu_settings:
                                    getDisplay().showSettings();
                                    break;
                                case R.id.menu_about:
                                    break;
                            }
                            menuItem.setChecked(true);
                            mDisplay.closeDrawerLayout();
                        }
                        return true;
                    }
                });

        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open_content_desc, R.string.drawer_closed_content_desc) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        mDrawerLayout.setDrawerListener(drawerToggle);

        drawerToggle.syncState();
    }

    private void setupHeader() {

    }

    public boolean onHomeButtonPressed() {
        if (mDisplay != null && (mDisplay.toggleDrawer() || mDisplay.popEntireFragmentBackStack())) {
            return true;
        }
        return false;
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

    protected boolean navigateUp() {
        final Intent intent = getParentIntent();
        if (intent != null) {
            NavUtils.navigateUpTo(this, intent);
            return true;
        }
        return false;
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

    public String getTransationName() {
        return mTransationName;
    }

    public void setTransationName(String mTransationName) {
        this.mTransationName = mTransationName;
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
