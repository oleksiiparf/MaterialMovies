package com.roodie.materialmovies.mvp.views;

import com.roodie.model.network.NetworkError;

import java.util.List;

/**
 * Created by Roodie on 17.07.2016.
 */

public interface MvpLceListView<M> extends UiView {

    void showError(NetworkError error);

    void setData(List<M> data);

    void showLoadingProgress(boolean visible);

    void onRefreshData(boolean visible);
}
