package com.manywho.services.atomsphere.factories;

import com.manywho.sdk.api.run.elements.type.MObject;
import com.manywho.sdk.api.run.elements.type.Property;
import com.manywho.services.atomsphere.date.DateSerializer;
import com.manywho.services.atomsphere.entities.TableMetadata;
import com.manywho.services.atomsphere.services.AliasService;
import com.manywho.services.atomsphere.services.DescribeService;
import com.manywho.services.atomsphere.services.PrimaryKeyService;
import com.manywho.services.atomsphere.utilities.MobjectUtil;
import org.sql2o.data.Row;
import org.sql2o.data.Table;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class MObjectFactory {
    private DescribeService describeService;
    private MobjectUtil mobjectUtil;
    private PrimaryKeyService primaryKeyService;
    private AliasService aliasService;

    @Inject
    public MObjectFactory(DescribeService describeService, MobjectUtil mobjectUtil, PrimaryKeyService primaryKeyService,
                          AliasService aliasService){
        this.describeService = describeService;
        this.mobjectUtil = mobjectUtil;
        this.primaryKeyService = primaryKeyService;
        this.aliasService = aliasService;
    }

    public List<MObject> createFromTable(Table table, TableMetadata tableMetadata) {
        List<MObject> mObjects = new ArrayList<>();

        for (Row row: table.rows()) {
            List<Property> properties = describeService.createProperties(table, tableMetadata);

            for (Property property : properties) {
                if (property.getContentType() == com.manywho.sdk.api.ContentType.DateTime) {
                    populatePropertyDate(property.getDeveloperName(), row.getObject(property.getDeveloperName()), properties);
                } else {
                    populateProperty(property.getDeveloperName(), row.getString(property.getDeveloperName()), properties);
                }
            }

            HashMap<String, String> primaryKeyAlias = new HashMap<>();
            mobjectUtil.getPrimaryKeyProperties(tableMetadata.getPrimaryKeyNames(), properties)
                    .entrySet()
                    .forEach(p -> primaryKeyAlias.put(aliasService.getColumnAliasOrName(tableMetadata, p.getKey()), p.getValue()));

            mObjects.add(new MObject(tableMetadata.getTableName(), primaryKeyService.serializePrimaryKey(primaryKeyAlias), properties));
            renamePropertiesUsingAliases(tableMetadata, properties);
        }

        return mObjects;
    }

    private void renamePropertiesUsingAliases(TableMetadata tableMetadata, List<Property> originalProperties) {
        originalProperties.forEach(p -> p.setDeveloperName(aliasService.getColumnAliasOrName(tableMetadata, p.getDeveloperName())));
    }

    private void populateProperty(String propertyName, String propertyValue, List<Property> propertyList) {
        propertyList.stream()
                .filter(p -> Objects.equals(p.getDeveloperName(), propertyName))
                .forEach(p-> p.setContentValue(propertyValue));
    }

    private void populatePropertyDate(String propertyName, Object propertyValue, List<Property> propertyList) {
        propertyList.stream()
                .filter(p -> Objects.equals(p.getDeveloperName(), propertyName))
                .forEach(p-> p.setContentValue(DateSerializer.serializeDate(p.getDeveloperName(), propertyValue)));
    }
}
