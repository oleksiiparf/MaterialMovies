package com.roodie.materialmovies.mvp.presenters;

/**
 * Created by Roodie on 25.06.2015.
 */

import android.support.annotation.Nullable;

import com.google.common.base.Preconditions;
import com.roodie.materialmovies.mvp.views.UiView;
import com.roodie.model.state.ApplicationState;

import java.lang.ref.WeakReference;

import javax.inject.Inject;

/**
 * Abstract presenter to work as base for every presenter created in the application. This
 * presenter
 * declares some methods to attach the fragment/activity lifecycle.
 */
 class BasePresenter<V extends UiView> {

    protected final ApplicationState mState;

    @Inject
    public BasePresenter(
            ApplicationState state) {
        super();
        mState = Preconditions.checkNotNull(state, "application state cannot be null");
    }

    private WeakReference<V> viewRef;

    /**
     * Called when the presenter is initialized, this method represents the start of the presenter
     * lifecycle.
     */

    public void attachView(V view) {
        Preconditions.checkNotNull(view, "View cannot be null");
        viewRef = new WeakReference<V>(view);
    }

    /**
     * Checks if a view is attached to this presenter. You should always call this method before
     * calling {@link #getView()} to get the view instance.
     */
    public boolean isViewAttached() {
        return viewRef != null && viewRef.get() != null;
    }

    public void detachView(boolean retainInstance) {
        if (viewRef != null) {
            viewRef.clear();
            viewRef = null;
        }
    }

    /**
     * Called when the presenter is initialized, this method represents the start of the presenter
     * lifecycle.
     */
    public  void initialize() {}

    /**
     * Get the attached view. You should always call {@link #isViewAttached()} to check if the view
     * is
     * attached to avoid NullPointerExceptions.
     *
     * @return <code>null</code>, if view is not attached, otherwise the concrete view instance
     */
    @Nullable public V getView() {
        return viewRef == null ? null : viewRef.get();
    }

    /**
     * Called when the presenter is resumed. After the initialization and when the presenter comes
     * from a pause state.
     */
    public void onResume() {
        mState.registerForEvents(this);
    }

    /**
     * Called when the presenter is paused.
     */
    public void onPause() {
        mState.registerForEvents(this);
    }



}

