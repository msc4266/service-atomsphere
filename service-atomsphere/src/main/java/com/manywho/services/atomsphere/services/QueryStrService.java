package com.manywho.services.atomsphere.services;

import com.healthmarketscience.sqlbuilder.*;
import com.manywho.sdk.api.run.elements.type.ListFilter;
import com.manywho.sdk.api.run.elements.type.MObject;
import com.manywho.sdk.api.run.elements.type.ObjectDataType;
import com.manywho.sdk.api.run.elements.type.Property;
import com.manywho.services.sql.ServiceConfiguration;
import com.manywho.services.sql.entities.TableMetadata;
import com.manywho.services.sql.services.filter.QueryFilterConditions;
import com.manywho.services.sql.utilities.ScapeForTablesUtil;

import javax.inject.Inject;
import java.util.Set;

public class QueryStrService {
    private QueryFilterConditions queryFilterConditions;
    private ScapeForTablesUtil scapeForTablesUtil;
    private AliasService aliasService;

    @Inject
    public QueryStrService(QueryFilterConditions queryFilterConditions, ScapeForTablesUtil scapeForTablesUtil,
                           AliasService aliasService) {
        this.queryFilterConditions = queryFilterConditions;
        this.scapeForTablesUtil = scapeForTablesUtil;
        this.aliasService = aliasService;
    }

    public String createQueryWithParametersForSelectByPrimaryKey(TableMetadata tableMetadata, Set<String> primaryKeyNames, ServiceConfiguration configuration) {

        SelectQuery selectQuery = new SelectQuery().addAllColumns()
                .addCustomFromTable(scapeForTablesUtil.scapeTableName(configuration.getDatabaseType(),tableMetadata.getSchemaName(), tableMetadata.getTableName()));

        for (String key: primaryKeyNames) {
            selectQuery.addCondition(BinaryCondition.equalTo(new CustomSql(ScapeForTablesUtil.scapeCollumnName(configuration.getDatabaseType(),key)), new CustomSql(":" + key)));
        }

        return selectQuery.validate().toString();
    }

    public String createQueryWithParametersForDeleteByPrimaryKey(TableMetadata tableMetadata, Set<String> primaryKeyNames, ServiceConfiguration configuration) {

        DeleteQuery selectQuery = new DeleteQuery(scapeForTablesUtil.scapeTableName(configuration.getDatabaseType(), tableMetadata.getSchemaName(), tableMetadata.getTableName()));

        for (String key: primaryKeyNames) {
            selectQuery.addCondition(BinaryCondition.equalTo(new CustomSql(ScapeForTablesUtil.scapeCollumnName(configuration.getDatabaseType(),key)), new CustomSql(":" + key)));
        }

        return selectQuery.validate().toString();
    }

    public String createQueryWithParametersForUpdate(MObject mObject, TableMetadata tableMetadata, Set<String> primaryKeyNames, ServiceConfiguration configuration){

        UpdateQuery updateQuery = new UpdateQuery(
                scapeForTablesUtil.scapeTableName(configuration.getDatabaseType(), tableMetadata.getSchemaName(), tableMetadata.getTableName()));

        for(Property p : mObject.getProperties()) {
           if(!tableMetadata.isColumnAutoincrement(p.getDeveloperName())) {
               updateQuery.addCustomSetClause(new CustomSql(ScapeForTablesUtil.scapeCollumnName(configuration.getDatabaseType(), p.getDeveloperName())), new CustomSql(":" + p.getDeveloperName()));
           }
        }

        for (String key: primaryKeyNames) {
            updateQuery.addCondition(BinaryCondition.equalTo(new CustomSql(ScapeForTablesUtil.scapeCollumnName(configuration.getDatabaseType(),key)), new CustomSql(":" + key)));
        }

        return updateQuery.validate().toString();
    }

    public String createQueryWithParametersForInsert(MObject mObject, TableMetadata tableMetadata, ServiceConfiguration configuration) {
        InsertQuery insertQuery = new InsertQuery(
                scapeForTablesUtil.scapeTableName(configuration.getDatabaseType(), tableMetadata.getSchemaName(), tableMetadata.getTableName()));

        for(Property p : mObject.getProperties()) {
            if (!tableMetadata.isColumnAutoincrement(p.getDeveloperName())) {
                insertQuery.addCustomColumn(new CustomSql(ScapeForTablesUtil.scapeCollumnName(configuration.getDatabaseType(), p.getDeveloperName())), new CustomSql(":" + p.getDeveloperName()));
            }
        }

        return  insertQuery.validate().toString();
    }

    public String getSqlFromFilter(ServiceConfiguration configuration, ObjectDataType objectDataType, ListFilter filter, TableMetadata tableMetadata) {

        SelectQuery selectQuery = new SelectQuery().addAllColumns()
                .addCustomFromTable(scapeForTablesUtil.scapeTableName(configuration.getDatabaseType(),
                        configuration.getDatabaseSchema(), objectDataType.getDeveloperName()));

        aliasService.setFiltersOriginalNames(tableMetadata, filter);
        objectDataType.setProperties(aliasService.setPropertiesOriginalName(tableMetadata, objectDataType.getProperties()));

        queryFilterConditions.addSearch(selectQuery, filter.getSearch(), objectDataType.getProperties(), tableMetadata.getColumnsDatabaseType(), configuration.getDatabaseType());
        queryFilterConditions.addWhere(selectQuery, filter.getWhere(), filter.getComparisonType(), configuration.getDatabaseType(), tableMetadata);
        queryFilterConditions.addOffset(selectQuery, configuration.getDatabaseType(), filter.getOffset(), filter.getLimit());
        queryFilterConditions.addOrderBy(selectQuery, filter.getOrderByPropertyDeveloperName(),
                filter.getOrderByDirectionType(), tableMetadata, configuration.getDatabaseType());

        return selectQuery.validate().toString();
    }
}
