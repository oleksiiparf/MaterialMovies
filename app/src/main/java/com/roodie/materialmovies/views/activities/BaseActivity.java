package com.roodie.materialmovies.views.activities;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;

import com.arellomobile.mvp.MvpDelegate;
import com.roodie.materialmovies.MMoviesDisplay;
import com.roodie.materialmovies.R;
import com.roodie.materialmovies.views.custom_views.MMoviesToolbar;
import com.roodie.model.Display;

import butterknife.ButterKnife;

/**
 * Created by Roodie on 27.06.2015.
 */
public abstract class BaseActivity extends ActionBarActivity {

    protected Display mDisplay;

    @StyleRes
    private int appliedTheme;

    private boolean savedState;

    private MvpDelegate<? extends BaseActivity> mMvpDelegate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.appliedTheme = getThemeResId();
        setTheme(this.appliedTheme);

        super.onCreate(savedInstanceState);
        getMvpDelegate().onCreate(savedInstanceState);
        setContentView(getContentViewLayoutId());
        ButterKnife.inject(this);

        setDisplay();
        handleIntent(getIntent(), getDisplay());
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        this.mDisplay = null;
        getMvpDelegate().onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
        getMvpDelegate().onStart();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        getMvpDelegate().onStop();
    }

    /**
     * @return The {@link MvpDelegate} being used by this Activity.
     */
    public MvpDelegate getMvpDelegate()
    {
        if (mMvpDelegate == null)
        {
            mMvpDelegate = new MvpDelegate<>(this);
        }
        return mMvpDelegate;
    }


    /**
     * Implementers must call this in {@link #onCreate} after {@link #setContentView} if they want
     * to use the action bar.
     */
    protected void setupActionBar() {
        MMoviesToolbar toolbar = (MMoviesToolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar, true);
            toolbar.setToolbarTitleTypeface();

        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        this.savedState = true;

        super.onSaveInstanceState(outState, outPersistentState);
        getMvpDelegate().onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @StyleRes
    protected abstract int getThemeResId();

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent, getDisplay());
    }

    protected abstract void handleIntent(Intent intent, Display display);

    protected void setDisplay() {
        mDisplay = new  MMoviesDisplay(this);
    }


    @Override
    protected void onResume() {
        super.onResume();
        this.savedState = false;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onPause() {
        super.onPause();
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


    protected boolean popLastFragment() {
        if ((!isFinishing()) && (!this.savedState))
        {
            FragmentManager localFragmentManager = getFragmentManager();
            if (localFragmentManager.getBackStackEntryCount() > 0)
                return localFragmentManager.popBackStackImmediate();
        }
        return false;
    }
}
