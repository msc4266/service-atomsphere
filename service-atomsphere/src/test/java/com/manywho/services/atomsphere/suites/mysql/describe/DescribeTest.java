package com.manywho.services.sql.suites.mysql.describe;

import com.manywho.services.sql.DbConfigurationTest;
import com.manywho.services.sql.ServiceFunctionalTest;
import com.manywho.services.sql.utilities.DefaultApiRequest;
import org.junit.After;
import org.junit.Test;
import org.sql2o.Connection;

public class DescribeTest extends ServiceFunctionalTest {

    @Test
    public void testDescribeWithTypes() throws Exception {
        DbConfigurationTest.setPropertiesIfNotInitialized("mysql");
        String sql = "CREATE TABLE " + escapeTableName("country") + "(" +
                "id integer NOT NULL," +
                "big BOOLEAN," +
                "CONSTRAINT country_id_pk PRIMARY KEY (id)" +
                ");";

        try (Connection connection = getSql2o().open()) {
            connection.createQuery(sql).executeUpdate();
        }

        DefaultApiRequest.describeServiceRequestAndAssertion("/metadata",
                "suites/mysql/describe/with-types/metadata1-request.json",
                configurationParameters(),
                "suites/mysql/describe/with-types/metadata1-response.json",
                dispatcher
        );
    }

    @Test
    public void testViewWithNoColumns() throws Exception {
        DbConfigurationTest.setPropertiesIfNotInitialized("mysql");
        try (Connection connection = getSql2o().open()) {
            String sql = "CREATE TABLE " + escapeTableName("deletetable") + " (id integer PRIMARY KEY);";
            connection.createQuery(sql).executeUpdate();
            String sql2 = "CREATE VIEW " + escapeTableName("emptyview") + " AS SELECT * FROM deletetable;";
            connection.createQuery(sql2).executeUpdate();
            String sql3 = "DROP TABLE " + escapeTableName("deletetable") + ";";
            connection.createQuery(sql3).executeUpdate();
            String sql4 = "CREATE TABLE " + escapeTableName("notemptytable") + " (id integer PRIMARY KEY, data text);";
            connection.createQuery(sql4).executeUpdate();
            String sql5 = "CREATE VIEW " + escapeTableName("notemptyview") + " AS SELECT * FROM notemptytable;";
            connection.createQuery(sql5).executeUpdate();
        }

        DefaultApiRequest.describeServiceRequestAndAssertion("/metadata",
                "suites/mysql/describe/with-types/metadata-empty-view-request.json",
                configurationParameters(),
                "suites/mysql/describe/with-types/metadata-empty-view-response.json",
                dispatcher
        );
    }

    @After
    public void cleanDatabaseAfterEachTest() {
        try (Connection connection = getSql2o().open()) {
            String sql = "DROP VIEW IF EXISTS " + escapeTableName("emptyview") + ";";
            connection.createQuery(sql).executeUpdate();
            deleteTableIfExist("notemptytable", connection);
            String sql2 = "DROP VIEW IF EXISTS " + escapeTableName("notemptyview") + ";";
            connection.createQuery(sql2).executeUpdate();
            deleteTableIfExist("country", connection);
        } catch (ClassNotFoundException e) {
        }
    }
}
