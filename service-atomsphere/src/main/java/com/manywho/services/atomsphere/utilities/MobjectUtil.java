package com.manywho.services.atomsphere.utilities;

import com.manywho.sdk.api.run.elements.type.Property;
import com.manywho.services.sql.services.PrimaryKeyService;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class MobjectUtil {
    private PrimaryKeyService primaryKeyService;

    @Inject
    public MobjectUtil(PrimaryKeyService primaryKeyService) {
        this.primaryKeyService = primaryKeyService;
    }

    public String getPrimaryKeyValue(List<String> primaryKeyNames, List<Property> properties) {

        return primaryKeyService.serializePrimaryKey(getPrimaryKeyProperties(primaryKeyNames, properties));
    }

    public HashMap<String, String> getPrimaryKeyProperties(List<String> primaryKeyNames, List<Property> properties) {
        HashMap<String, String> primaryKeys = new HashMap<>();

        for(String primaryKey: primaryKeyNames) {
            properties.stream()
                    .filter(property -> Objects.equals(property.getDeveloperName(), primaryKey))
                    .forEach(property -> primaryKeys.put(primaryKey, property.getContentValue()));
        }

        return primaryKeys;
    }
}
