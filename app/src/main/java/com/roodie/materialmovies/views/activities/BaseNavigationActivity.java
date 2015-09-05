package com.roodie.materialmovies.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.NavUtils;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.view.View;

import com.roodie.materialmovies.R;
import com.roodie.materialmovies.views.custom_views.MMoviesImageView;

/**
 * Created by Roodie on 03.09.2015.
 */
public abstract class BaseNavigationActivity extends BaseActivity {

    //@Bind({R.id.card_container})
    private View mCardContainer;

   // @Bind({R.id.drawer_layout})
    private DrawerLayout mDrawerLayout;

    //@Bind({R.id.navigation_view})
    private NavigationView mNavigationView;

    //@Bind({R.id.profile_image})
    private MMoviesImageView mUserProfilePhoto;
    protected String mTransationName;

    @Override
    protected int getThemeResId() {
        if (!SettingsActivity.hasTheme()) {
            SettingsActivity.setTheme(this);
        }
        // set a theme based on user preference
        if (SettingsActivity.THEME == R.style.Theme_MMovies_Light) {
            return R.style.Theme_MMovies_Light;
        } else if (SettingsActivity.THEME ==  R.style.Theme_MMovies_Dark) {
            return R.style.Theme_MMovies_Dark;
        } else {
            return R.style.Theme_MMovies_Green;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        mCardContainer = findViewById(R.id.card_container);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        mNavigationView = (NavigationView) findViewById(R.id.navigation_view);

        mUserProfilePhoto = (MMoviesImageView) findViewById(R.id.profile_image);
        if (mNavigationView != null) {
            setupDrawerContent(mNavigationView);
            setupHeader();
        }
        super.onCreate(savedInstanceState);
    }

    private void setupHeader() {

    }

    public boolean onHomeButtonPressed() {
        if (mDisplay != null && (mDisplay.toggleDrawer() || mDisplay.popEntireFragmentBackStack())) {
            return true;
        }
        return false;
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
                        } else {
                            System.out.println("Display == null");
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

    protected boolean navigateUp() {
        final Intent intent = getParentIntent();
        if (intent != null) {
            NavUtils.navigateUpTo(this, intent);
            return true;
        }
        return false;
    }


    @Override
    protected void setDisplay() {
        super.setDisplay();
        getDisplay().setDrawerLayout(this.mDrawerLayout);
    }

    protected Intent getParentIntent() {
        return NavUtils.getParentActivityIntent(this);
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

}
