package com.manywho.services.atomsphere.database;

import com.manywho.sdk.api.describe.DescribeServiceRequest;
import com.manywho.sdk.api.draw.elements.type.TypeElement;
import com.manywho.sdk.services.types.TypeProvider;
import com.manywho.services.atomsphere.ServiceConfiguration;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class RawTypeProvider implements TypeProvider<ServiceConfiguration> {

    private ServiceMetadata serviceMetadata;
    Logger logger;

    @Inject
    public RawTypeProvider(ServiceMetadata serviceMetadata) {
    	logger = Logger.getLogger(this.getClass().getName());
    	logger.info("RawTypeProvider inits serviceMetadata TODO cache this for Database operations");
        this.serviceMetadata = serviceMetadata;
    }

    @Override
    public boolean doesTypeExist(ServiceConfiguration configuration, String s) {
        return serviceMetadata.findTypeElement(s)!=null;
    }

    @Override
    public List<TypeElement> describeTypes(ServiceConfiguration configuration, DescribeServiceRequest describeServiceRequest) {
     	try {
     		return serviceMetadata.getAllTypeElements();
         } catch (Exception e) {
            throw new RuntimeException(e);
        }
     }
}
