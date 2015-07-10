package com.roodie.materialmovies.mvp.presenters;

import android.view.View;

import com.google.common.base.Preconditions;
import com.roodie.model.Display;

/**
 * Created by Roodie on 25.06.2015.
 */

/**
 * Abstract presenter to work as base for every presenter created in the application. This
 * presenter
 * declares some methods to attach the fragment/activity lifecycle.
 */
abstract class BasePresenter {

    private boolean mInited;

    /**
     * Called when the presenter is initialized, this method represents the start of the presenter
     * lifecycle.
     */

    public final void init() {
        Preconditions.checkState(mInited == false, "Already inited");
        mInited = true;
        onInited();
    }

    /**
     * Called when the presenter is resumed. After the initialization and when the presenter comes
     * from a pause state.
     */
    public abstract void onResume();

    /**
     * Called when the presenter is paused.
     */
    public final void onPause() {
        Preconditions.checkState(mInited == true, "Not inited");
        onPaused();
        mInited = false;
    }


    protected void onInited() {}

    protected void onPaused() {}

    public abstract boolean hasCallbacks();


}

