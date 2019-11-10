package com.manywho.services.atomsphere.services;

import com.google.common.collect.Lists;
import com.manywho.sdk.api.ContentType;
import com.manywho.sdk.api.draw.elements.type.TypeElement;
import com.manywho.sdk.api.draw.elements.type.TypeElementBinding;
import com.manywho.sdk.api.draw.elements.type.TypeElementProperty;
import com.manywho.sdk.api.draw.elements.type.TypeElementPropertyBinding;
import com.manywho.sdk.api.run.elements.type.Property;
import com.manywho.services.atomsphere.entities.TableMetadata;
import org.sql2o.data.Table;

import javax.inject.Inject;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DescribeService {
    private AliasService aliasService;

    @Inject
    public DescribeService(AliasService aliasService){
        this.aliasService = aliasService;
    }

    public TypeElement createTypeElementFromTableMetadata(TableMetadata tableMetadata) throws SQLException {
        HashMap<String, ContentType> metadataProperties = tableMetadata.getColumnsAndContentTypeWithAlias();

        List<TypeElementProperty> properties = Lists.newArrayList();
        List<TypeElementPropertyBinding> propertyBindings = Lists.newArrayList();

        for(Map.Entry<String, ContentType> property: metadataProperties.entrySet()) {
            properties.add(new TypeElementProperty(property.getKey(), property.getValue()));
            propertyBindings.add(new TypeElementPropertyBinding(property.getKey(), property.getKey(),
                    aliasService.getColumnDatabaseType(tableMetadata, property.getKey())));
        }

        List<TypeElementBinding> bindings = Lists.newArrayList();
        bindings.add(new TypeElementBinding(tableMetadata.getTableName(), "The binding for " + tableMetadata.getTableName(), tableMetadata.getTableName(), propertyBindings));

        return new TypeElement(tableMetadata.getTableName(), properties, bindings);
    }

    public List<Property> createProperties(Table table, TableMetadata tableMetadata) {
        HashMap<String, ContentType> columnsContentType = tableMetadata.getColumns();
        List<Property> properties = new ArrayList<>();

        properties.addAll(
                table.columns().stream()
                        .filter(column1 -> columnsContentType.get(column1.getName()) != null)
                        .map(column -> new Property(column.getName(), "", columnsContentType.get(column.getName())))
                        .collect(Collectors.toList()));

        return properties;
    }
}
