package com.manywho.services.atomsphere.database;

import com.manywho.sdk.api.describe.DescribeServiceRequest;
import com.manywho.sdk.api.draw.elements.type.TypeElement;
import com.manywho.sdk.services.types.TypeProvider;
import com.manywho.services.atomsphere.ServiceConfiguration;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class RawTypeProvider implements TypeProvider<ServiceConfiguration> {

    private ServiceMetadata serviceMetadata;

    @Inject
    public RawTypeProvider(ServiceMetadata serviceMetadata) {
        this.serviceMetadata = serviceMetadata;
    }

    @Override
    public boolean doesTypeExist(ServiceConfiguration configuration, String s) {
        return true;
    }

    @Override
    public List<TypeElement> describeTypes(ServiceConfiguration configuration, DescribeServiceRequest describeServiceRequest) {
     	try {
     		return serviceMetadata.getAllTypesMetadata();
         } catch (Exception e) {
            throw new RuntimeException(e);
        }
     }
}
