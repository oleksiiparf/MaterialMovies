package com.roodie.materialmovies.mvp.views;

import com.roodie.model.network.NetworkError;

/**
 * Created by Roodie on 11.12.2015.
 */
public interface MvpLceView<M> extends UiView {

    /**
     * Show the error view.
     * <b>The error view must be a TextView with the id = R.id.mErrorView</b>
     *  @param error The Throwable that has caused this error
     *
     */
    void showError(NetworkError error);

    void setData(M data);

    void showLoadingProgress(boolean visible);

    void onRefreshData(boolean visible);
}
