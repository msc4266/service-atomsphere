package com.manywho.services.atomsphere;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.manywho.sdk.services.types.TypeProvider;

public class ApplicationAtomsphereModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(QueryParameterService.class).in(Singleton.class);
        bind(TypeProvider.class).to(RawTypeProvider.class);
    }
}
