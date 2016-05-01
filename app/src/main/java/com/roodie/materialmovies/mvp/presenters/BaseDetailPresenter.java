package com.roodie.materialmovies.mvp.presenters;

import com.roodie.materialmovies.mvp.views.UiView;

/**
 * Created by Roodie on 20.02.2016.
 */
public interface BaseDetailPresenter<M extends UiView> extends BasePresenter<M> {

    void attachUiByParameter(M view, String requestedParameter);

    void populateUi(M view, String parameter);

    void refresh(M view, String parameter);

    String getUiTitle(String parameter);
}
