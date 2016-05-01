package com.roodie.materialmovies.mvp.views;

/**
 * Created by Roodie on 10.02.2016.
 */

public interface BaseListView<M> extends MvpLceView<M> {

    void onScrolledToBottom();
}
