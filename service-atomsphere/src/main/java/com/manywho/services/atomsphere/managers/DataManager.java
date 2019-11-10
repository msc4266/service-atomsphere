package com.manywho.services.sql.managers;

import com.manywho.sdk.api.run.elements.type.ListFilter;
import com.manywho.sdk.api.run.elements.type.MObject;
import com.manywho.sdk.api.run.elements.type.ObjectDataType;
import com.manywho.services.sql.ServiceConfiguration;
import com.manywho.services.sql.entities.TableMetadata;
import com.manywho.services.sql.services.DataService;
import com.manywho.services.sql.services.PrimaryKeyService;
import com.manywho.services.sql.services.QueryStrService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.sql2o.Connection;
import org.sql2o.Sql2o;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;

public class DataManager {

    private DataService dataService;
    private QueryStrService queryStrService;
    private PrimaryKeyService primaryKeyService;
    private static final Logger LOGGER = LoggerFactory.getLogger(DataService.class);

    @Inject
    public DataManager(DataService dataService, QueryStrService queryStrService, PrimaryKeyService primaryKeyService){
        this.dataService = dataService;
        this.queryStrService = queryStrService;
        this.primaryKeyService = primaryKeyService;
    }

    public List<MObject> load(Connection connection, ServiceConfiguration configuration, TableMetadata tableMetadata,
                              HashMap<String, String> id) throws Exception {

        return dataService.fetchByPrimaryKey(tableMetadata, connection, id, configuration);
    }

    public List<MObject> loadBySearch(Sql2o sql2o,ServiceConfiguration configuration, TableMetadata tableMetadata,
                                      ObjectDataType objectDataType, ListFilter filters) throws Exception {

        String queryString = "";

        try {
            queryString = queryStrService.getSqlFromFilter(configuration, objectDataType, filters, tableMetadata);

            return dataService.fetchBySearch(tableMetadata, sql2o, queryString);
        } catch (Exception ex) {
            LOGGER.error("query: " + queryString, ex);
            throw ex;
        }
    }

    public MObject update(Connection connection, ServiceConfiguration configuration, TableMetadata tableMetadata, MObject mObject) throws Exception {

        HashMap<String, String> primaryKeyHashMap = primaryKeyService.deserializePrimaryKey(mObject.getExternalId());
        dataService.update(mObject, connection, tableMetadata, primaryKeyHashMap, configuration);

        return dataService.fetchByPrimaryKey(tableMetadata, connection, primaryKeyHashMap, configuration).get(0);
    }

    public MObject create(Connection connection, ServiceConfiguration configuration, TableMetadata tableMetadata, MObject mObject) throws Exception {

        dataService.insert(mObject, connection, tableMetadata, configuration);

        return mObject;
    }

    public void delete(Sql2o sql2o, ServiceConfiguration configuration, TableMetadata tableMetadata,
                       HashMap<String, String> id) throws Exception {

        dataService.deleteByPrimaryKey(tableMetadata, sql2o, id, configuration);
    }
}
