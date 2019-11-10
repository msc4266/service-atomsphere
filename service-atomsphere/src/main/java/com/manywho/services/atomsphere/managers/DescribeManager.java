package com.manywho.services.sql.managers;

import com.manywho.sdk.api.draw.elements.type.TypeElement;
import com.manywho.services.sql.ServiceConfiguration;
import com.manywho.services.sql.entities.TableMetadata;
import com.manywho.services.sql.services.DescribeService;
import org.sql2o.Connection;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class DescribeManager {
    private DescribeService describeService;
    private MetadataManager metadataManager;

    @Inject
    public DescribeManager(DescribeService describeService, MetadataManager metadataManager) {
        this.describeService = describeService;
        this.metadataManager = metadataManager;
    }

    public List<TypeElement> getListTypeElementFromTableMetadata(Connection connection, ServiceConfiguration serviceConfiguration) throws Exception {
        List<TypeElement> listOfTypeElements = new ArrayList<>();
        for (TableMetadata table : metadataManager.getMetadataTables(connection, serviceConfiguration)) {
            TypeElement typeElement = describeService.createTypeElementFromTableMetadata(table);
            if (typeElement.getProperties().size() > 0) {
                listOfTypeElements.add(typeElement);
            }
        }
        
        return listOfTypeElements;
    }
}
