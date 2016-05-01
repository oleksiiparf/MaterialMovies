package com.roodie.materialmovies.modules;

import com.roodie.materialmovies.modules.library.InjectorModule;
import com.roodie.materialmovies.modules.library.StateProvider;
import com.roodie.materialmovies.modules.library.UtilProvider;
import com.roodie.materialmovies.mvp.presenters.ListMoviesPresenter;

import dagger.Module;

/**
 * Created by Roodie on 16.02.2016.
 */

@Module(
        injects = {
                ListMoviesPresenter.class


        },
        includes = {
                StateProvider.class,
                UtilProvider.class,
                InjectorModule.class

        }
)
public class PresenterProvider {
}
