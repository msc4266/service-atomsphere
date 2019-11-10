package com.manywho.services.atomsphere.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.manywho.sdk.api.run.elements.type.ListFilter;
import com.manywho.sdk.api.run.elements.type.MObject;
import com.manywho.sdk.api.run.elements.type.ObjectDataType;
import com.manywho.sdk.services.database.RawDatabase;
import com.manywho.services.atomsphere.ServiceConfiguration;
import com.manywho.services.atomsphere.entities.TableMetadata;
import com.manywho.services.atomsphere.exceptions.RecordNotFoundException;
import com.manywho.services.atomsphere.managers.ConnectionManager;
import com.manywho.services.atomsphere.managers.DataManager;
import com.manywho.services.atomsphere.managers.MetadataManager;
import com.manywho.services.atomsphere.services.AliasService;
import com.manywho.services.atomsphere.services.PrimaryKeyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;

public class Database implements RawDatabase<ServiceConfiguration> {
    private DataManager dataManager;
    private PrimaryKeyService primaryKeyService;
    private MetadataManager metadataManager;
    private AliasService aliasService;
    private ObjectMapper objectMapper;

    private static final Logger LOGGER = LoggerFactory.getLogger(Database.class);


    @Inject
    public Database(DataManager dataManager, PrimaryKeyService primaryKeyService, MetadataManager metadataManager,
                    AliasService aliasService) {
        this.dataManager = dataManager;
        this.primaryKeyService = primaryKeyService;
        this.metadataManager = metadataManager;
        this.aliasService = aliasService;
        objectMapper = new ObjectMapper();
    }

    @Override
    public MObject create(ServiceConfiguration configuration, MObject object) {

        Sql2o sql2o = ConnectionManager.getSql2Object(configuration);

        try (Connection connection = sql2o.open()) {
            TableMetadata tableMetadata = metadataManager.getMetadataTable(connection, configuration, object.getDeveloperName());

            MObject objectWithColumnName = this.aliasService.getMObjectWithoutAliases(object, tableMetadata);
            MObject objectInserted = dataManager.create(connection, configuration, tableMetadata, objectWithColumnName);
            HashMap<String, String> primaryKey = primaryKeyService.deserializePrimaryKey(objectInserted.getExternalId());
            List<MObject> mObjectList = this.dataManager.load(connection, configuration, tableMetadata, primaryKey);

            if (mObjectList.size() > 0) {
                return this.aliasService.getMObjectWithAliases(mObjectList.get(0), tableMetadata);
            }

        } catch (Exception e) {
            try {
                LOGGER.error("create MObject: " + objectMapper.writeValueAsString(object), e);
            } catch (Exception ignored) {}

            throw new RuntimeException("problem creating object" + e.getMessage());
        }

        throw new RuntimeException("Error creating object");
    }

    @Override
    public List<MObject> create(ServiceConfiguration configuration, List<MObject> objects) {
        return null;
    }

    @Override
    public void delete(ServiceConfiguration configuration, MObject object) {
        Sql2o sql2o = ConnectionManager.getSql2Object(configuration);
        try (Connection connection = sql2o.open()) {

            TableMetadata tableMetadata = metadataManager.getMetadataTable(connection, configuration, object.getDeveloperName());
            MObject objectWithOriginalNames = this.aliasService.getMObjectWithoutAliases(object, tableMetadata);

            this.dataManager.delete(sql2o, configuration, tableMetadata,
                    primaryKeyService.deserializePrimaryKey(objectWithOriginalNames.getExternalId()));

        } catch (Exception e) {
            try {
                LOGGER.error("delete MObject: " + objectMapper.writeValueAsString(object), e);
            } catch (Exception ignored) {}

            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(ServiceConfiguration configuration, List<MObject> objects) {
        //todo delete list of object;

        return;
    }

    @Override
    public MObject find(ServiceConfiguration configuration, ObjectDataType objectDataType, String id) {
        Sql2o sql2o = ConnectionManager.getSql2Object(configuration);

        try (Connection connection = sql2o.open()) {
            TableMetadata tableMetadata = metadataManager.getMetadataTable(connection, configuration,
                    objectDataType.getDeveloperName());

            List<MObject> mObjectList = this.dataManager.load(connection, configuration, tableMetadata,
                    aliasService.getOriginalKeys(primaryKeyService.deserializePrimaryKey(id), tableMetadata));

            if (mObjectList.size() > 0) {
                return this.aliasService.getMObjectWithAliases(mObjectList.get(0), tableMetadata);
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        throw new RecordNotFoundException();
    }

    @Override
    public List<MObject> findAll(ServiceConfiguration configuration, ObjectDataType objectDataType, ListFilter filter) {
        Sql2o sql2o = ConnectionManager.getSql2Object(configuration);

        try (Connection connection = sql2o.open()) {
            TableMetadata tableMetadata = metadataManager.getMetadataTable(connection, configuration, objectDataType.getDeveloperName());
            List<MObject> mObjects = this.dataManager.loadBySearch(sql2o, configuration, tableMetadata, objectDataType, filter);
            return this.aliasService.getMObjectsWithAlias(mObjects, tableMetadata);


        } catch (Exception e) {
            try {
                LOGGER.error("findAll filter: " + objectMapper.writeValueAsString(filter), e);
            } catch (Exception ignored) {}

            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public MObject update(ServiceConfiguration configuration, MObject object) {
        Sql2o sql2o = ConnectionManager.getSql2Object(configuration);

        try (Connection connection = sql2o.open()) {
            TableMetadata tableMetadata = metadataManager.getMetadataTable(connection, configuration,
                    object.getDeveloperName());

            object = aliasService.getMObjectWithoutAliases(object, tableMetadata);
            this.dataManager.update(connection, configuration, tableMetadata, object);
            List<MObject> mObjectList = this.dataManager.load(connection, configuration, tableMetadata,
                    primaryKeyService.deserializePrimaryKey(object.getExternalId()));

            if (mObjectList.size() > 0) {
                MObject mObject = mObjectList.get(0);
                return this.aliasService.getMObjectWithAliases(mObject, tableMetadata);
            }

        } catch (Exception e) {
            try {
                LOGGER.error("update MObject: " + objectMapper.writeValueAsString(object), e);
            } catch (Exception ignored) {}

            throw new RuntimeException(e);
        }

        throw new RecordNotFoundException();
    }

    @Override
    public List<MObject> update(ServiceConfiguration configuration, List<MObject> objects) {
        return null;
    }
}