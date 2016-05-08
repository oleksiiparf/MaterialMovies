package com.roodie.materialmovies.modules;

import android.content.Context;

import com.roodie.materialmovies.MMoviesApp;
import com.roodie.materialmovies.modules.library.InjectorModule;
import com.roodie.materialmovies.modules.library.NetworkModule;
import com.roodie.materialmovies.modules.library.PersistanceModule;
import com.roodie.materialmovies.modules.library.StateModule;
import com.roodie.materialmovies.modules.library.UtilModule;
import com.roodie.materialmovies.qualifiers.AppContext;
import com.roodie.materialmovies.util.MMoviesVisitManager;
import com.roodie.materialmovies.views.activities.SettingsActivity;
import com.roodie.materialmovies.views.activities.WelcomeActivity;
import com.roodie.model.util.VisitManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;


@Module(
        injects = {
                MMoviesApp.class,
                WelcomeActivity.class,
                SettingsActivity.class
        },
        includes = {
                UtilModule.class,
                NetworkModule.class,
                StateModule.class,
                PersistanceModule.class,
                InjectorModule.class,
                ViewUtilComponent.class

        }

)
public class ApplicationComponent {

        @Provides @Singleton
        public VisitManager getVisitManager(@AppContext Context context) {
                return new MMoviesVisitManager(context);
        }

}
