package com.manywho.services.atomsphere.managers;

import com.manywho.services.atomsphere.ServiceConfiguration;
import com.manywho.services.atomsphere.entities.TableMetadata;
import com.manywho.services.atomsphere.services.MetadataService;
import org.sql2o.Connection;

import javax.inject.Inject;
import java.sql.DatabaseMetaData;
import java.util.List;

public class MetadataManager {
    private MetadataService metadataService;

    @Inject
    public MetadataManager(MetadataService metadataService){
        this.metadataService = metadataService;
    }

    public List<TableMetadata> getMetadataTables(Connection connection, ServiceConfiguration serviceConfiguration) throws Exception {

        DatabaseMetaData metaData = connection.getJdbcConnection().getMetaData();

        return metadataService.getTablesMetadata(
                serviceConfiguration.getDatabaseName(),
                serviceConfiguration.getDatabaseSchema(),
                metaData
        );
    }

    public TableMetadata getMetadataTable(Connection connection, ServiceConfiguration serviceConfiguration, String tableName) throws Exception {

        DatabaseMetaData metaData = connection.getJdbcConnection().getMetaData();

        List<TableMetadata> tableMetadataLis = metadataService.getTablesMetadata(
                serviceConfiguration.getDatabaseName(),
                serviceConfiguration.getDatabaseSchema(),
                metaData,
                tableName
        );

        if(tableMetadataLis.size() == 1) {

            return tableMetadataLis.get(0);
        } else {

            throw new RuntimeException ("table" + tableName + "not found");
        }
    }
}
