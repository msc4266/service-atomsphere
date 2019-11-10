package com.manywho.services.atomsphere.types;

import com.manywho.sdk.api.describe.DescribeServiceRequest;
import com.manywho.sdk.api.draw.elements.type.TypeElement;
import com.manywho.sdk.services.types.TypeProvider;
import com.manywho.services.atomsphere.ServiceConfiguration;
import com.manywho.services.atomsphere.managers.ConnectionManager;
import com.manywho.services.atomsphere.managers.DescribeManager;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class RawTypeProvider implements TypeProvider<ServiceConfiguration> {

    private DescribeManager describeManager;

    @Inject
    public RawTypeProvider(DescribeManager describeManager) {
        this.describeManager = describeManager;
    }

    @Override
    public boolean doesTypeExist(ServiceConfiguration configuration, String s) {
        return true;
    }

    @Override
    public List<TypeElement> describeTypes(ServiceConfiguration configuration, DescribeServiceRequest describeServiceRequest) {
        try {
            if (describeServiceRequest.getConfigurationValues() != null && describeServiceRequest.getConfigurationValues().size() > 0) {
                Sql2o sql2o = ConnectionManager.getSql2Object(configuration);

                try (Connection connection = sql2o.open()) {
                    return describeManager.getListTypeElementFromTableMetadata(connection, configuration);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return new ArrayList<>();
    }
}
