package com.roodie.materialmovies.mvp.presenters;

import com.arellomobile.mvp.MvpView;
import com.roodie.model.tasks.BaseRunnable;

/**
 * Created by Roodie on 14.08.2015.
 */
 public interface BasePresenter<M extends MvpView>{

    int TMDB_FIRST_PAGE = 1;

    int getId(M view);

    <BR> void executeNetworkTask(BaseRunnable<BR> task);

}
