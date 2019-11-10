package com.manywho.services.sql.suites.mysql.data;

import com.manywho.services.sql.DbConfigurationTest;
import com.manywho.services.sql.ServiceFunctionalTest;
import com.manywho.services.sql.utilities.DefaultApiRequest;
import org.junit.After;
import org.junit.Test;
import org.sql2o.Connection;

public class AutoIncrementTest extends ServiceFunctionalTest {

    @Test
    public void testCreateDataWithAutoIncrement() throws Exception {
        DbConfigurationTest.setPropertiesIfNotInitialized("mysql");

        try (Connection connection = getSql2o().open()) {
            String sql = "CREATE TABLE " + escapeTableName("country") + "(" +
                    "name character varying(255)," +
                    "id INT NOT NULL AUTO_INCREMENT," +
                    "description character varying(1024), " +
                    "CONSTRAINT country_id_pk PRIMARY KEY (id)" +
                    ");";
            connection.createQuery(sql).executeUpdate();
        }

        DefaultApiRequest.saveDataRequestAndAssertion("/data",
                "suites/mysql/autoincrement/create/create-request.json",
                configurationParameters(),
                "suites/mysql/autoincrement/create/create-response.json",
                dispatcher
        );
    }

    @Test
    public void testUpdateAutoincrement() throws Exception {
        DbConfigurationTest.setPropertiesIfNotInitialized("mysql");
        try (Connection connection = getSql2o().open()) {
            String sql = "CREATE TABLE " + escapeTableName("country") + "(" +
                    "name character varying(255)," +
                    "id INT NOT NULL AUTO_INCREMENT," +
                    "description character varying(1024), " +
                    "CONSTRAINT country_id_pk PRIMARY KEY (id)" +
                    ");";
            connection.createQuery(sql).executeUpdate();
        }

        try (Connection connection = getSql2o().open()) {
            String sql = "INSERT INTO " + escapeTableName("country") + "( name, description) VALUES ( 'Uruguay', 'It is a nice country');";
            connection.createQuery(sql).executeUpdate();
        }

        DefaultApiRequest.saveDataRequestAndAssertion("/data",
                "suites/mysql/autoincrement/update/update-request.json",
                configurationParameters(),
                "suites/mysql/autoincrement/update/update-response.json",
                dispatcher
        );
    }

    @After
    public void cleanDatabaseAfterEachTest() {
        try (Connection connection = getSql2o().open()) {
            deleteTableIfExist("country", connection);
        } catch (ClassNotFoundException e) {
        }
    }
}
