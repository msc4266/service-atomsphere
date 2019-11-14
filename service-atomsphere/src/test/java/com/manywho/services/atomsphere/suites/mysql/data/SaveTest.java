package com.manywho.services.sql.suites.mysql.data;

import com.manywho.services.sql.DbConfigurationTest;
import com.manywho.services.sql.ServiceFunctionalTest;
import com.manywho.services.sql.utilities.DefaultApiRequest;
import org.junit.After;
import org.junit.Test;
import org.sql2o.Connection;

public class SaveTest extends ServiceFunctionalTest {
    @Test
    public void testCreate() throws Exception {
        DbConfigurationTest.setPropertiesIfNotInitialized("mysql");

        try (Connection connection = getSql2o().open()) {
            String sql = "CREATE TABLE " + escapeTableName("country1") + "(" +
                    "id integer NOT NULL," +
                    "name character varying(255)," +
                    "big BOOLEAN, " +
                    "created Date, " +
                    "updated Datetime, " +
                    "CONSTRAINT country_id2_pk PRIMARY KEY (id)" +
                    ");";
            connection.createQuery(sql).executeUpdate();
        }

        DefaultApiRequest.saveDataRequestAndAssertion("/data",
                "suites/mysql/create/create-request.json",
                configurationParameters(),
                "suites/mysql/create/create-response.json",
                dispatcher
        );
    }

    @After
    public void cleanDatabaseAfterEachTest() {
        try (Connection connection = getSql2o().open()) {
            deleteTableIfExist("country1", connection);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
