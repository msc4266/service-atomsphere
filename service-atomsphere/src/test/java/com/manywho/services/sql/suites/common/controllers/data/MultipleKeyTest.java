package com.manywho.services.sql.suites.common.controllers.data;

import com.manywho.services.sql.DbConfigurationTest;
import com.manywho.services.sql.ServiceFunctionalTest;
import com.manywho.services.sql.utilities.DefaultApiRequest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sql2o.Connection;

public class MultipleKeyTest extends ServiceFunctionalTest {

    @Before
    public void setupDatabase() throws Exception {
        DbConfigurationTest.setPropertiesIfNotInitialized("sqlserver");

        try (Connection connection = getSql2o().open()) {
            String sql = "CREATE TABLE " + escapeTableName("city") +
                    "(" +
                            "cityname character varying(255)," +
                            "countryname character varying(255)," +
                            "description character varying(1024)," +
                            "CONSTRAINT city_pk PRIMARY KEY (cityname, countryname)" +
                    ");";
            connection.createQuery(sql).executeUpdate();
        }
    }

    @Test
    public void testLoadDataByExternalId() throws Exception {
        DbConfigurationTest.setPropertiesIfNotInitialized("postgresql");
        try (Connection connection = getSql2o().open()) {
            String sql = "INSERT INTO " + escapeTableName("city")+ "(cityname, countryname) VALUES ('Montevideo', 'Uruguay');";
            connection.createQuery(sql).executeUpdate();
        }

        DefaultApiRequest.loadDataRequestAndAssertion("/data",
                "suites/common/data/multiple-primary-key/load/by-primary-key/load-request.json",
                configurationParameters(),
                "suites/common/data/multiple-primary-key/load/by-primary-key/load-response.json",
                dispatcher
        );
    }

    @Test
    public void testCreate() throws Exception {
        
        DefaultApiRequest.saveDataRequestAndAssertion("/data",
                "suites/common/data/multiple-primary-key/create/create-request.json",
                configurationParameters(),
                "suites/common/data/multiple-primary-key/create/create-response.json",
                dispatcher
        );
    }

    @After
    public void cleanDatabaseAfterEachTest() {
        try (Connection connection = getSql2o().open()) {
            deleteTableIfExist("city", connection);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
