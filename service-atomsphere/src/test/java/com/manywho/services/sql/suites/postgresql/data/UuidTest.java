package com.manywho.services.sql.suites.postgresql.data;

import com.manywho.services.sql.DbConfigurationTest;
import com.manywho.services.sql.ServiceFunctionalTest;
import com.manywho.services.sql.utilities.DefaultApiRequest;
import org.json.JSONException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sql2o.Connection;

import java.io.IOException;
import java.net.URISyntaxException;

public class UuidTest extends ServiceFunctionalTest {
    @Before
    public void setupDatabase() throws Exception {
        DbConfigurationTest.setPropertiesIfNotInitialized("postgresql");
    }

    @Test
    public void testLoad() throws ClassNotFoundException, JSONException, IOException, URISyntaxException {
        try (Connection connection = getSql2o().open()) {
            String sqlCreate = "CREATE TABLE " + escapeTableName("uuidexample") +
                    "(" +
                    "uuidkey uuid PRIMARY KEY," +
                    "payload character(300)" +
                    ");";
            connection.createQuery(sqlCreate).executeUpdate();

            String sql = "INSERT INTO " + escapeTableName("uuidexample") + "(uuidkey, payload) VALUES " +
                    "('155e3620-42bb-11e6-beb8-9e71128cae77', 'payload example');";

            connection.createQuery(sql).executeUpdate();
        }

        DefaultApiRequest.loadDataRequestAndAssertion("/data",
                "suites/postgresql/uuid/load/request-load.json",
                configurationParameters(),
                "suites/postgresql/uuid/load/response-load.json",
                dispatcher);
    }

    @Test
    public void testUpdate() throws ClassNotFoundException, JSONException, IOException, URISyntaxException {
        try (Connection connection = getSql2o().open()) {
            String sqlCreate = "CREATE TABLE " + escapeTableName("uuidexample") +
                    "(" +
                    "uuidkey uuid PRIMARY KEY," +
                    "payload character(300)" +
                    ");";
            connection.createQuery(sqlCreate).executeUpdate();

            String sql = "INSERT INTO " + escapeTableName("uuidexample") + "(uuidkey, payload) VALUES " +
                    "('155e3620-42bb-11e6-beb8-9e71128cae77', 'payload example');";

            connection.createQuery(sql).executeUpdate();
        }

        DefaultApiRequest.saveDataRequestAndAssertion("/data",
                "suites/postgresql/uuid/save/update/update-request.json",
                configurationParameters(),
                "suites/postgresql/uuid/save/update/update-response.json",
                dispatcher);
    }


    @Test
    public void testCreate() throws ClassNotFoundException, JSONException, IOException, URISyntaxException {
        try (Connection connection = getSql2o().open()) {
            String sqlCreate = "CREATE TABLE " + escapeTableName("uuidexample") +
                    "(" +
                    "uuidkey uuid PRIMARY KEY," +
                    "payload character(300)" +
                    ");";
            connection.createQuery(sqlCreate).executeUpdate();
        }

        DefaultApiRequest.saveDataRequestAndAssertion("/data",
                "suites/postgresql/uuid/save/create/create-request.json",
                configurationParameters(),
                "suites/postgresql/uuid/save/create/create-response.json",
                dispatcher);
    }


    @After
    public void cleanDatabaseAfterEachTest() {
        try (Connection connection = getSql2o().open()) {
            deleteTableIfExist("uuidexample", connection);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
