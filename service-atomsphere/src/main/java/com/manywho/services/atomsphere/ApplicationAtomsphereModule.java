package com.manywho.services.atomsphere;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.manywho.sdk.services.types.TypeProvider;
import com.manywho.services.atomsphere.database.RawTypeProvider;

public class ApplicationAtomsphereModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(TypeProvider.class).to(RawTypeProvider.class);
    }
}
