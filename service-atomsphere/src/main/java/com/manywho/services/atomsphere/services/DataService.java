package com.manywho.services.atomsphere.services;

import com.google.common.base.Strings;
import com.manywho.sdk.api.run.elements.type.MObject;
import com.manywho.sdk.api.run.elements.type.Property;
import com.manywho.services.sql.ServiceConfiguration;
import com.manywho.services.sql.entities.DatabaseType;
import com.manywho.services.sql.entities.TableMetadata;
import com.manywho.services.sql.exceptions.DataBaseTypeNotSupported;
import com.manywho.services.sql.factories.MObjectFactory;
import com.manywho.services.sql.utilities.MobjectUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sql2o.Connection;
import org.sql2o.Query;
import org.sql2o.Sql2o;

import javax.inject.Inject;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataService {
    private MObjectFactory mObjectFactory;
    private QueryStrService queryStrService;
    private QueryParameterService parameterSanitaizerService;
    private MobjectUtil mobjectUtil;
    private static final Logger LOGGER = LoggerFactory.getLogger(DataService.class);

    @Inject
    public DataService(MObjectFactory mObjectFactory, QueryStrService queryStrService,
                       QueryParameterService parameterSanitaizerService, MobjectUtil mobjectUtil) {
        this.mObjectFactory = mObjectFactory;
        this.queryStrService = queryStrService;
        this.parameterSanitaizerService = parameterSanitaizerService;
        this.mobjectUtil = mobjectUtil;
    }

    public List<MObject> fetchByPrimaryKey(TableMetadata tableMetadata, Connection connection, HashMap<String, String> externalId, ServiceConfiguration configuration) throws SQLException, ParseException {
        String queryString = "";
        try {
            queryString = queryStrService.createQueryWithParametersForSelectByPrimaryKey(tableMetadata, externalId.keySet(), configuration);
            Query query = connection.createQuery(queryString);

            for (String key : externalId.keySet()) {
                String paramType = tableMetadata.getColumnsDatabaseType().get(key);
                parameterSanitaizerService.addParameterValueToTheQuery(key, externalId.get(key), paramType, query);
            }

            return mObjectFactory.createFromTable(query.executeAndFetchTable(), tableMetadata);
        } catch (DataBaseTypeNotSupported ex) {
            LOGGER.error("query: " + queryString, ex);
            throw new RuntimeException(ex.getMessage());
        } catch (RuntimeException ex) {
            LOGGER.error("query: " + queryString, ex);
            throw ex;
        }
    }

    public List<MObject> fetchBySearch(TableMetadata tableMetadata, Sql2o sql2o, String sqlSearch) throws SQLException {
        try (Connection con = sql2o.open()) {
            Query query = con.createQuery(sqlSearch).setCaseSensitive(true);

            return mObjectFactory.createFromTable(query.executeAndFetchTable(), tableMetadata);
        } catch (RuntimeException ex) {
            LOGGER.error("query: " + sqlSearch, ex);
            throw ex;
        }
    }

    public MObject update(MObject mObject, Connection connection, TableMetadata metadataTable, HashMap<String, String> primaryKeyHashMap, ServiceConfiguration configuration) throws DataBaseTypeNotSupported, ParseException {
        String queryString = queryStrService.createQueryWithParametersForUpdate(mObject, metadataTable, primaryKeyHashMap.keySet(), configuration);

        Query query = connection.createQuery(queryString);

        for (Property p : mObject.getProperties()) {
            parameterSanitaizerService.addParameterValueToTheQuery(p.getDeveloperName(),
                    p.getContentValue(),
                    metadataTable.getColumnsDatabaseType().get(p.getDeveloperName()),
                    query);
        }

        try {
            query.setCaseSensitive(true).executeUpdate();
        } catch (RuntimeException ex) {
            LOGGER.error("query: " + queryString, ex);
            throw ex;
        }

        return mObject;
    }

    public MObject insert(MObject mObject, Connection connection, TableMetadata tableMetadata, ServiceConfiguration configuration) throws DataBaseTypeNotSupported, ParseException {
        boolean isPostgres = configuration.getDatabaseType().equals(DatabaseType.Postgresql);

        String queryString = queryStrService.createQueryWithParametersForInsert(mObject, tableMetadata, configuration);
        Query query = createInsertQuery(connection, queryString, isPostgres);

        //todo we should support more than one autoincrement
        String autoIncrement = "";

        for (Property p : mObject.getProperties()) {
            if (tableMetadata.isColumnAutoincrement(p.getDeveloperName())) {
                autoIncrement = p.getDeveloperName();
            } else {
                parameterSanitaizerService.addParameterValueToTheQuery(p.getDeveloperName(), p.getContentValue(),
                        tableMetadata.getColumnsDatabaseType().get(p.getDeveloperName()), query);
            }
        }

        mObject = executeInsertQuery(query, autoIncrement, mObject, isPostgres);
        mObject.setExternalId(mobjectUtil.getPrimaryKeyValue(tableMetadata.getPrimaryKeyNames(), mObject.getProperties()));

        return mObject;
    }

    /**
     * because a bug in sql2o library the getKeys doesn't work for postgres. The solution have been to modify the query
     * a bit for postgres. We are hardcoding the "RETURNING *" that should work out of the box with just adding
     * returnGeneratedKeys = true, but is not currently working.
     *
     */
    private Query createInsertQuery(Connection connection, String queryString, boolean isPostgres) {
        if (isPostgres == true) {
            // we do this because a bug in the library sql2o with postgres, it should be removed when this problem is fixed
            // in this way we can use the last version of sql2o even if the bug is not fixed.
            queryString += " RETURNING *";
            return connection.createQuery(queryString, false);
        } else {
            return connection.createQuery(queryString, true);
        }
    }

    /**
     * because a bug in sql2o library the getKeys doesn't work for postgres. The solution have been to modify the query
     * a bit for postgres. Once this bug is fixed we should be able to use the same call to getKeys for all databases
     *
     */
    private MObject executeInsertQuery(Query query, String autoIncrement, MObject mObject, boolean isPostgres) {
        if (Strings.isNullOrEmpty(autoIncrement)) {
            if (isPostgres == true) {
                // we need to fetch table because we are hardcoding "RETURNING *" the library would throw an exception if
                // we don't execute it and then fetch the data, even if we don't need it
                query.executeAndFetchTable();
            } else {
                query.executeUpdate();
            }
        } else {
            if (isPostgres == true) {
                List<Map<String, Object>> allKeys = query.executeAndFetchTable().asList();

                mObject.getProperties().forEach(p -> {
                    if (p.getDeveloperName().equals(autoIncrement) == true) {
                        p.setContentValue(allKeys.get(0).get(autoIncrement).toString());
                    }
                });
            } else {
                Object objects[] = query.executeUpdate().getKeys();

                mObject.getProperties().forEach(p -> {
                    if (p.getDeveloperName().equals(autoIncrement) == true) {
                        p.setContentValue(String.valueOf(objects[0]));
                    }
                });
            }
        }

        return mObject;
    }

    public void deleteByPrimaryKey(TableMetadata tableMetadata, Sql2o sql2o, HashMap<String, String> externalId, ServiceConfiguration configuration) throws ParseException {
        String queryString = "";

        try (Connection con = sql2o.open()) {
            queryString = queryStrService.createQueryWithParametersForDeleteByPrimaryKey(tableMetadata, externalId.keySet(), configuration);
            Query query = con.createQuery(queryString);

            for (String key : externalId.keySet()) {
                String paramType = tableMetadata.getColumnsDatabaseType().get(key);
                parameterSanitaizerService.addParameterValueToTheQuery(key, externalId.get(key), paramType, query);
            }

            query.executeUpdate();
        } catch (DataBaseTypeNotSupported dataBaseTypeNotSupported) {
            throw new RuntimeException(dataBaseTypeNotSupported);
        } catch (RuntimeException ex) {
            LOGGER.error("query: " + queryString, ex);
        }
    }
}
