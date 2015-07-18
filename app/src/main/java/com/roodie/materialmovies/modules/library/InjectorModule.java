package com.roodie.materialmovies.modules.library;

import com.google.common.base.Preconditions;
import com.roodie.model.util.Injector;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Roodie on 13.07.2015.
 */

@Module(
        library = true
)
public class InjectorModule {

    private final Injector mInjector;

    public InjectorModule(Injector injector) {
        mInjector = Preconditions.checkNotNull(injector, "injector cannot be null");
    }

    @Provides
    public Injector provideMainInjector() {
        return mInjector;
    }
}
