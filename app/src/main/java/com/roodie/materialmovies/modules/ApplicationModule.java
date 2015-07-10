package com.roodie.materialmovies.modules;

import com.roodie.materialmovies.modules.library.NetworkProvider;
import com.roodie.materialmovies.modules.library.PersistanceProvider;
import com.roodie.materialmovies.modules.library.StateProvider;
import com.roodie.materialmovies.modules.library.UtilProvider;
import com.roodie.materialmovies.views.MMoviesApplication;

import dagger.Module;



@Module (
        injects = MMoviesApplication.class,
        includes = {
                UtilProvider.class,
                NetworkProvider.class,
                StateProvider.class,
                PersistanceProvider.class
        }

)
public class ApplicationModule {
}
