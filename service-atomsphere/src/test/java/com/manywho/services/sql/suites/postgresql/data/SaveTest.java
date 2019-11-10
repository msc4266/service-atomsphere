package com.manywho.services.sql.suites.postgresql.data;

import com.manywho.services.sql.DbConfigurationTest;
import com.manywho.services.sql.ServiceFunctionalTest;
import com.manywho.services.sql.utilities.DefaultApiRequest;
import org.junit.After;
import org.junit.Test;
import org.sql2o.Connection;

public class SaveTest extends ServiceFunctionalTest {
    @Test
    public void testCreateWithAlias() throws Exception {
        DbConfigurationTest.setPropertiesIfNotInitialized("postgresql");

        try (Connection connection = getSql2o().open()) {
            String sql = "CREATE TABLE " + escapeTableName("country") + "(" +
                    "id integer NOT NULL," +
                    "name character varying(255)," +
                    "description character varying(1024), " +
                    "CONSTRAINT country_id_pk PRIMARY KEY (id)" +
                    ");";
            connection.createQuery(sql).executeUpdate();

            String aliasId = "COMMENT ON COLUMN " + escapeTableName("country") + ".id IS '{{ManyWhoName:The Id}}';";
            connection.createQuery(aliasId).executeUpdate();

            String aliasName = "COMMENT ON COLUMN " + escapeTableName("country") + ".name IS '{{ManyWhoName:The Name}}';";
            connection.createQuery(aliasName).executeUpdate();

            String aliasDescription = "COMMENT ON COLUMN " + escapeTableName("country") + ".description IS '{{ManyWhoName:The Description}}';";
            connection.createQuery(aliasDescription).executeUpdate();
        }

        DefaultApiRequest.saveDataRequestAndAssertion("/data",
                "suites/common/data/save/create-with-alias/create-request.json",
                configurationParameters(),
                "suites/common/data/save/create-with-alias/create-response.json",
                dispatcher
        );
    }

    @Test
    public void testUpdateWithAliases() throws Exception {
        DbConfigurationTest.setPropertiesIfNotInitialized("postgresql");

        try (Connection connection = getSql2o().open()) {
            String sql = "CREATE TABLE " + escapeTableName("country") + "(" +
                    "id integer NOT NULL," +
                    "name character varying(255)," +
                    "description character varying(1024), " +
                    "CONSTRAINT country_id_pk PRIMARY KEY (id)" +
                    ");";
            connection.createQuery(sql).executeUpdate();

            String aliasId = "COMMENT ON COLUMN " + escapeTableName("country") + ".id IS '{{ManyWhoName:The Id}}';";
            connection.createQuery(aliasId).executeUpdate();

            String aliasName = "COMMENT ON COLUMN " + escapeTableName("country") + ".name IS '{{ManyWhoName:The Name}}';";
            connection.createQuery(aliasName).executeUpdate();

            String aliasDescription = "COMMENT ON COLUMN " + escapeTableName("country") + ".description IS '{{ManyWhoName:The Description}}';";
            connection.createQuery(aliasDescription).executeUpdate();
        }

        try (Connection connection = getSql2o().open()) {
            String sql = "INSERT INTO " + escapeTableName("country") + "(id, name, description) VALUES ('1', 'Uruguay', 'It is a nice country');";
            connection.createQuery(sql).executeUpdate();
        }

        DefaultApiRequest.saveDataRequestAndAssertion("/data",
                "suites/common/data/save/update-with-alias/update-request.json",
                configurationParameters(),
                "suites/common/data/save/update-with-alias/update-response.json",
                dispatcher
        );
    }

    @Test
    public void testCreateWithBoolean() throws Exception {
        DbConfigurationTest.setPropertiesIfNotInitialized("postgresql");

        try (Connection connection = getSql2o().open()) {
            String sql = "CREATE TABLE " + escapeTableName("country") + "(" +
                    "id integer NOT NULL," +
                    "name character varying(255)," +
                    "big BOOLEAN, " +
                    "CONSTRAINT country_id2_pk PRIMARY KEY (id)" +
                    ");";
            connection.createQuery(sql).executeUpdate();
        }

        DefaultApiRequest.saveDataRequestAndAssertion("/data",
                "suites/postgresql/boolean/create/create-request.json",
                configurationParameters(),
                "suites/postgresql/boolean/create/create-response.json",
                dispatcher
        );
    }

    @After
    public void cleanDatabaseAfterEachTest() {
        try (Connection connection = getSql2o().open()) {
            deleteTableIfExist("country", connection);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
