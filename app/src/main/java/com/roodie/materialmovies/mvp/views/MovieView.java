package com.roodie.materialmovies.mvp.views;

import com.roodie.model.network.NetworkError;


/**
 * Created by Roodie on 02.07.2015.
 */
public interface MovieView extends UiView {

    void showError(NetworkError error);

    void showLoadingProgress(boolean visible);

    void showSecondaryLoadingProgress(boolean visible);

    String getRequestParameter();
}
