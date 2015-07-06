package com.roodie.materialmovies.mvp.views;

import com.roodie.model.controllers.DrawerMenuItem;

/**
 * Created by Roodie on 04.07.2015.
 */
public interface MainView extends UiView  {

        void setSideMenuItems(DrawerMenuItem[] items, DrawerMenuItem selected);

}
