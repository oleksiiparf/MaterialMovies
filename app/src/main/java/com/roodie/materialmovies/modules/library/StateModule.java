package com.roodie.materialmovies.modules.library;

import com.roodie.model.state.ApplicationState;
import com.squareup.otto.Bus;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Roodie on 02.07.2015.
 */
@Module (
        library =  true,
        includes = UtilModule.class
)
public class StateModule {

    @Provides @Singleton
    public ApplicationState provideApplicationState(Bus bus) {
        return new ApplicationState(bus);
    }

}
