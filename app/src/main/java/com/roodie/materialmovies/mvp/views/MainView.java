package com.roodie.materialmovies.mvp.views;

import com.arellomobile.mvp.GenerateViewState;
import com.arellomobile.mvp.MvpView;

/**
 * Created by Roodie on 23.02.2016.
 */

@GenerateViewState
public interface MainView extends MvpView {

    void setData(int[] data);

}
