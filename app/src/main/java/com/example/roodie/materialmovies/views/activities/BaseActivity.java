package com.example.roodie.materialmovies.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.example.roodie.materialmovies.R;
import com.example.roodie.model.Display;

/**
 * Created by Roodie on 27.06.2015.
 */
public class BaseActivity extends ActionBarActivity {

    private View mCardContainer;
    private DrawerLayout mDrawerLayout;
    private Display mDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentViewLayoutId());

        mCardContainer = findViewById(R.id.card_container);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);


    }

    @Override
    public final void setSupportActionBar(@Nullable Toolbar toolbar) {
        super.setSupportActionBar(toolbar);
    }

    protected boolean navigateUp() {
        final Intent intent = getParentIntent();
        if (intent != null) {
            NavUtils.navigateUpTo(this, intent);
            return true;
        }
        return false;
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
            case 1: {
                break;
            }

            case 2: {
                break;
            }

            case 3: {
                break;
            }
        }
        return super.onOptionsItemSelected(item);
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

    public void setSupportActionBar(@Nullable Toolbar toolbar, boolean handleBackground) {
        setSupportActionBar(toolbar);
        getDisplay().setSupportActionBar(toolbar, handleBackground);
    }

}
