package com.roodie.materialmovies.modules;

import com.roodie.materialmovies.MMoviesApp;
import com.roodie.materialmovies.modules.library.InjectorModule;
import com.roodie.materialmovies.modules.library.NetworkProvider;
import com.roodie.materialmovies.modules.library.PersistanceProvider;
import com.roodie.materialmovies.modules.library.StateProvider;
import com.roodie.materialmovies.modules.library.UtilProvider;

import dagger.Module;



@Module (
        injects =  MMoviesApp.class,
        includes = {
                UtilProvider.class,
                NetworkProvider.class,
                StateProvider.class,
                PersistanceProvider.class,
                InjectorModule.class,
                ViewUtilProvider.class
        }

)
public class ApplicationModule {
}
