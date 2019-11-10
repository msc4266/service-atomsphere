package com.manywho.services.sql.utilities;

import java.util.Properties;

public class DbTestConfigurationProperties {
    protected Properties properties;
    protected Boolean usingPropertiesFile = true;

    public DbTestConfigurationProperties() {
        try {
            properties = new Properties();

            if(getClass().getClassLoader().getResourceAsStream("configuration.properties") != null) {
                properties.load(getClass().getClassLoader().getResourceAsStream("configuration.properties"));
            } else {
                usingPropertiesFile = false;
            }

        } catch (Exception exception) {
            properties = new Properties();
        }
    }

    public String get(String key) {
        if (usingPropertiesFile) {
            return properties.getProperty(key);
        } else {
            return System.getenv(key);
        }
    }

    public boolean has(String key) {
        return properties.containsKey(key);
    }
}
