package com.manywho.services.atomsphere.services;

import com.google.common.base.Strings;
import com.manywho.sdk.api.run.elements.type.ListFilter;
import com.manywho.sdk.api.run.elements.type.MObject;
import com.manywho.sdk.api.run.elements.type.ObjectDataTypeProperty;
import com.manywho.services.sql.entities.TableMetadata;
import javax.inject.Inject;
import java.util.*;

public class AliasService {
    private PrimaryKeyService primaryKeyService;

    @Inject
    public AliasService(PrimaryKeyService primaryKeyService) {
        this.primaryKeyService = primaryKeyService;
    }

    public MObject getMObjectWithoutAliases(MObject object, TableMetadata tableMetadata) {
        String externalId = null;

        if (object.getExternalId() != null) {
            HashMap<String, String> idWithAlias = primaryKeyService.deserializePrimaryKey(object.getExternalId());
            HashMap<String, String> idWithColumnNames = getOriginalKeys(idWithAlias, tableMetadata);

            externalId = primaryKeyService.serializePrimaryKey(idWithColumnNames);
        }

        object.getProperties()
                .forEach(p -> p.setDeveloperName(getColumnNameOrAlias(tableMetadata, p.getDeveloperName())));

        object.setExternalId(externalId);

        return object;
    }
    
    public MObject getMObjectWithAliases(MObject object, TableMetadata tableMetadata) {
        HashMap<String, String> idWithColumnNames = primaryKeyService.deserializePrimaryKey(object.getExternalId());

        HashMap<String, String> idWithAlias = new HashMap<>();
        idWithColumnNames.entrySet()
                .forEach(property -> idWithAlias.put(getColumnAliasOrName(tableMetadata, property.getKey()), property.getValue()));

        object.getProperties()
                .forEach(p -> p.setDeveloperName(getColumnAliasOrName(tableMetadata, p.getDeveloperName())));

        object.setExternalId(primaryKeyService.serializePrimaryKey(idWithAlias));

        return object;
    }

    public String getColumnDatabaseType(TableMetadata tableMetadata, String nameOrAlias) {

        if (isAlias(tableMetadata, nameOrAlias)) {
            return tableMetadata.getColumnsDatabaseType().get(getColumnNameOrAlias(tableMetadata, nameOrAlias));
        }

        return tableMetadata.getColumnsDatabaseType().get(nameOrAlias);
    }

    /**
     * it will try to find a column with name nameOrAlias if it doesn't exist it will return the alias
     *
     * @param tableMetadata
     * @param nameOrAlias
     * @return
     */
    public String getColumnNameOrAlias(TableMetadata tableMetadata, String nameOrAlias) {
        if (tableMetadata.getColumns().get(nameOrAlias) == null) {
            //is alias

            Optional<Map.Entry<String, String>> first = tableMetadata.getAliases().entrySet().stream()
                    .filter(c -> c.getValue() != null && Objects.equals(c.getValue(), nameOrAlias))
                    .findFirst();

            if (first.isPresent()) {
                return first.get().getKey();
            }

            throw new RuntimeException("Name of Column not found");
        }

        //is name
        return nameOrAlias;
    }

    /**
     * it will try to find an alias for nameOrAlias if it doesn't exist it will return the name of the column
     * @param tableMetadata
     * @param aliasOrName
     * @return
     */
    public String getColumnAliasOrName(TableMetadata tableMetadata, String aliasOrName) {
        if (tableMetadata.getColumns().get(aliasOrName)== null) {
            // it is not a column
            Optional<Map.Entry<String, String>> alias = tableMetadata.getAliases().entrySet().stream().filter(p -> Objects.equals(p.getValue(), aliasOrName)).findFirst();

            if (alias.isPresent()) {
                return alias.get().getValue();
            }

        } else if (tableMetadata.getColumns().get(aliasOrName)!= null){
            if (tableMetadata.getAliases().get(aliasOrName) != null) {
                // it is a column and exist an alias for it
                return tableMetadata.getAliases().get(aliasOrName);
            }

            // it is a column but there isn't alias for it
            return aliasOrName;
        }

        throw new RuntimeException("Name of Column not found");
    }

    private boolean isAlias(TableMetadata tableMetadata, String columnName) {
        return tableMetadata.getColumns().get(columnName) == null;
    }

    public ListFilter setFiltersOriginalNames(TableMetadata tableMetadata, ListFilter filter) {
        filter.getWhere().forEach(f -> f.setColumnName(getColumnNameOrAlias(tableMetadata, f.getColumnName())));
        if (!Strings.isNullOrEmpty(filter.getOrderByPropertyDeveloperName())) {
            filter.setOrderByPropertyDeveloperName(getColumnNameOrAlias(tableMetadata, filter.getOrderByPropertyDeveloperName()));
        }
        return filter;
    }

    public List<ObjectDataTypeProperty> setPropertiesOriginalName(TableMetadata tableMetadata, List<ObjectDataTypeProperty> properties) {
        properties.forEach(p -> p.setDeveloperName(getColumnNameOrAlias(tableMetadata, p.getDeveloperName())));

        return properties;
    }

    public HashMap<String,String> getOriginalKeys(HashMap<String, String> idWithAlias, TableMetadata tableMetadata) {
        HashMap<String, String> idWithColumnNames = new HashMap<>();

        idWithAlias.entrySet()
                .forEach(property -> idWithColumnNames.put(getColumnNameOrAlias(tableMetadata, property.getKey()), property.getValue()));

        return idWithColumnNames;

    }

    public List<MObject> getMObjectsWithAlias(List<MObject> mObjects, TableMetadata tableMetadata) {
        List<MObject> mObjectsWithAlias = new ArrayList<>();

        mObjects.forEach(original -> mObjectsWithAlias.add(getMObjectWithAliases(original, tableMetadata)));

        return mObjectsWithAlias;
    }
}
