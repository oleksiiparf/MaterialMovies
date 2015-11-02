package com.roodie.materialmovies.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;

import com.roodie.materialmovies.R;
import com.roodie.materialmovies.views.MMoviesDisplay;
import com.roodie.model.Display;

import butterknife.ButterKnife;
//import icepick.Icepick;

/**
 * Created by Roodie on 27.06.2015.
 */
public abstract class BaseActivity extends ActionBarActivity {

    protected Display mDisplay;

    @StyleRes
    private int appliedTheme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.appliedTheme = getThemeResId();
        setTheme(this.appliedTheme);

        super.onCreate(savedInstanceState);
        setContentView(getContentViewLayoutId());
        ButterKnife.bind(this);

        setDisplay();
        handleIntent(getIntent(), getDisplay());
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        //Icepick.saveInstanceState(this,outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
       // Icepick.restoreInstanceState(this, savedInstanceState);
    }

    @StyleRes
    protected abstract int getThemeResId();


    protected abstract void handleIntent(Intent intent, Display display);

    protected void setDisplay() {
        mDisplay = new  MMoviesDisplay(this);
    }


    @Override
    protected void onResume() {
        super.onResume();
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


    @Override
    public final void setSupportActionBar(@Nullable Toolbar toolbar) {
        super.setSupportActionBar(toolbar);
    }

    public void setSupportActionBar(@Nullable Toolbar toolbar, boolean handleBackground) {
        setSupportActionBar(toolbar);
        getDisplay().setSupportActionBar(toolbar, handleBackground);
    }
}
