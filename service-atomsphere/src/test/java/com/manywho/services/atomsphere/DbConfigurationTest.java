package com.manywho.services.sql;

import com.manywho.services.sql.utilities.DbTestConfigurationProperties;

public class DbConfigurationTest {
    static public boolean initialized = false;
    static public String portForTest;
    static public String databaseTypeForTest;
    static public String databaseNameForTest;
    static public String schemaForTest ;
    static public String hostForTest;

    static public String userName;
    static public String password;

    static public void setPorperties(String databaseType) {
        DbConfigurationTest.initialized = true;
        DbTestConfigurationProperties serviceConfigurationProperties = new DbTestConfigurationProperties();

        DbConfigurationTest.portForTest = serviceConfigurationProperties.get(databaseType + "_port");
        DbConfigurationTest.databaseTypeForTest = serviceConfigurationProperties.get(databaseType + "_databaseType");
        DbConfigurationTest.schemaForTest = serviceConfigurationProperties.get(databaseType + "_schema");
        DbConfigurationTest.hostForTest = serviceConfigurationProperties.get(databaseType + "_host");
        DbConfigurationTest.databaseNameForTest = serviceConfigurationProperties.get(databaseType + "_databaseName");

        DbConfigurationTest.userName = serviceConfigurationProperties.get(databaseType + "_userName");
        DbConfigurationTest.password = serviceConfigurationProperties.get(databaseType + "_password");
    }

    static public void setPropertiesIfNotInitialized(String databaseType) {
        if(!DbConfigurationTest.initialized) {
            initialized = true;

            DbTestConfigurationProperties serviceConfigurationProperties = new DbTestConfigurationProperties();

            DbConfigurationTest.portForTest = serviceConfigurationProperties.get(databaseType + "_port");
            DbConfigurationTest.databaseTypeForTest = serviceConfigurationProperties.get(databaseType + "_databaseType");
            DbConfigurationTest.schemaForTest = serviceConfigurationProperties.get(databaseType + "_schema");
            DbConfigurationTest.hostForTest = serviceConfigurationProperties.get(databaseType + "_host");
            DbConfigurationTest.databaseNameForTest = serviceConfigurationProperties.get(databaseType + "_databaseName");

            DbConfigurationTest.userName = serviceConfigurationProperties.get(databaseType + "_userName");
            DbConfigurationTest.password = serviceConfigurationProperties.get(databaseType + "_password");
        }
    }
}
