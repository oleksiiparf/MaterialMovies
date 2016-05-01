package com.roodie.materialmovies.views.activities;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.roodie.materialmovies.R;
import com.roodie.model.Display;

/**
 * Created by Roodie on 03.09.2015.
 */


public abstract class BaseNavigationActivity extends BaseActivity  {

   // @Bind({R.id.drawer_layout})
    protected DrawerLayout mDrawerLayout;

    //@Bind({R.id.navigation_view})
    private NavigationView mNavigationView;

    private int checkedMenuItem;





    public BaseNavigationActivity() {
        checkedMenuItem = R.id.menu_movies;
    }

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
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            this.checkedMenuItem = savedInstanceState.getInt("checked_menu_item", R.id.menu_movies);
        }

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        getDisplay().setDrawerLayout(mDrawerLayout);

       mNavigationView = (NavigationView) findViewById(R.id.navigation_view);

        if (mNavigationView != null) {
            setupDrawerContent();
            setMenuCheckedState(mNavigationView.getMenu(), checkedMenuItem);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putInt("checked_menu_item", this.checkedMenuItem);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        this.checkedMenuItem = savedInstanceState.getInt("checked_menu_item", R.id.menu_movies);
    }

    private void setupDrawerContent() {
        this.mNavigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        if (menuItem.getItemId() != checkedMenuItem) {
                            if (getDisplay() != null) {
                                switch (menuItem.getItemId()) {
                                    case R.id.menu_watched:
                                        getDisplay().showWatched();
                                        checkedMenuItem = R.id.menu_watched;
                                        break;
                                    case R.id.menu_movies:
                                        getDisplay().showMovies();
                                        checkedMenuItem = R.id.menu_movies;
                                        break;
                                    case R.id.menu_shows:
                                        getDisplay().showTvShows();
                                        checkedMenuItem = R.id.menu_shows;
                                        break;
                                    case R.id.menu_settings:
                                        getDisplay().showSettings();
                                        break;
                                    case R.id.menu_mail:
                                        getDisplay().sendEmail();
                                        break;
                                    default:
                                        getDisplay().showWatched();
                                        checkedMenuItem = R.id.menu_watched;
                                        break;
                                }
                            }
                        }
                        getDisplay().closeDrawerLayout();

                        setMenuCheckedState(mNavigationView.getMenu(), checkedMenuItem);
                        return true;
                    }
                });

        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open_content_desc, R.string.drawer_closed_content_desc) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                BaseNavigationActivity.this.supportInvalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                BaseNavigationActivity.this.supportInvalidateOptionsMenu();
            }
        };

        //Setting the actionbarToggle to drawer layout
        mDrawerLayout.setDrawerListener(drawerToggle);

        drawerToggle.syncState();
    }

    private static void setMenuCheckedState(final Menu menu, final int itemId) {
        for (int i = 0; i < menu.size(); ++i) {
            final MenuItem item = menu.getItem(i);
            item.setChecked(item.getItemId() == itemId);
            if (item.hasSubMenu()) {
                setMenuCheckedState(item.getSubMenu(), itemId);
            }
        }
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

    public boolean onHomeButtonPressed() {
        Display display = getDisplay();
        return display != null && (display.toggleDrawer() || display.popEntireFragmentBackStack());
    }

    public DrawerLayout getDrawerLayout() {
        return mDrawerLayout;
    }

    public void updateHeader(int[] data) {
        if (mNavigationView != null) {
            View headView = mNavigationView.getHeaderView(0);

            ((TextView) headView.findViewById(R.id.watched_movies_txt)).setText(String.valueOf(data[0]));
            ((TextView) headView.findViewById(R.id.watched_shows_txt)).setText(String.valueOf(data[1]));
        }

    }
}
