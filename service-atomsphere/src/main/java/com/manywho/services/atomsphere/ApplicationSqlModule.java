package com.manywho.services.atomsphere;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.manywho.sdk.services.types.TypeProvider;
import com.manywho.services.atomsphere.services.QueryParameterService;
import com.manywho.services.atomsphere.types.RawTypeProvider;

public class ApplicationSqlModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(QueryParameterService.class).in(Singleton.class);
        bind(TypeProvider.class).to(RawTypeProvider.class);
    }
}
