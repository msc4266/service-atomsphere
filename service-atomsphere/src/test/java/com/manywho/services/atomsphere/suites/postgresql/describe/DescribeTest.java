package com.manywho.services.sql.suites.postgresql.describe;

import com.manywho.services.sql.DbConfigurationTest;
import com.manywho.services.sql.ServiceFunctionalTest;
import com.manywho.services.sql.utilities.DefaultApiRequest;
import org.junit.After;
import org.junit.Test;
import org.sql2o.Connection;

public class DescribeTest extends ServiceFunctionalTest {

    @Test
    public void testDescribeWithTypes() throws Exception {
        DbConfigurationTest.setPropertiesIfNotInitialized("postgresql");
        String sql = "CREATE TABLE " + escapeTableName("country2") + "(" +
                "id integer NOT NULL," +
                "name character varying(255)," +
                "description character varying(1024)," +
                "available BOOLEAN," +
                "CONSTRAINT country_id_pk PRIMARY KEY (id)" +
                ");";

        try (Connection connection = getSql2o().open()) {
            connection.createQuery(sql).executeUpdate();
        }

        DefaultApiRequest.describeServiceRequestAndAssertion("/metadata",
                "suites/postgresql/describe/with-types/metadata1-request.json",
                configurationParameters(),
                "suites/postgresql/describe/with-types/metadata1-response.json",
                dispatcher
        );
    }

    @Test
    public void testDescribeWithAliases() throws Exception {
        DbConfigurationTest.setPropertiesIfNotInitialized("postgresql");

        String sql = "CREATE TABLE " + escapeTableName("country") + "(" +
                "id integer NOT NULL," +
                "name character varying(255)," +
                "description character varying(1024)," +
                "CONSTRAINT country_id_pk PRIMARY KEY (id)" +
                ");";

        String aliasName = "COMMENT ON COLUMN " + escapeTableName("country") + ".name IS '{{ManyWhoName:The Name}}';";
        //ignored at the moment
        String aliasTable = "COMMENT ON TABLE " + escapeTableName("country") + " IS '{{ManyWhoName:The Country}}';";

        try (Connection connection = getSql2o().open()) {
            connection.createQuery(sql).executeUpdate();
            connection.createQuery(aliasName).executeUpdate();
            connection.createQuery(aliasTable).executeUpdate();
        }

        DefaultApiRequest.describeServiceRequestAndAssertion("/metadata",
                "suites/common/describe/with-alias-types/metadata1-request.json",
                configurationParameters(),
                "suites/common/describe/with-alias-types/metadata1-response.json",
                dispatcher
        );
    }

    @Test
    public void testTableWithNoColumns() throws Exception {
        DbConfigurationTest.setPropertiesIfNotInitialized("postgresql");
        try (Connection connection = getSql2o().open()) {
            String sql = "CREATE TABLE " + escapeTableName("emptytable") + "(id integer PRIMARY KEY);";
            connection.createQuery(sql).executeUpdate();
            String sql2 = "ALTER TABLE " + escapeTableName("emptytable") + " DROP COLUMN id;";
            connection.createQuery(sql2).executeUpdate();
            String sql3 = "CREATE TABLE " + escapeTableName("notemptytable") + "(id integer PRIMARY KEY, data text);";
            connection.createQuery(sql3).executeUpdate();
        }

        DefaultApiRequest.describeServiceRequestAndAssertion("/metadata",
                "suites/postgresql/describe/with-types/metadata-empty-table-request.json",
                configurationParameters(),
                "suites/postgresql/describe/with-types/metadata-empty-table-response.json",
                dispatcher
        );
    }

    @After
    public void cleanDatabaseAfterEachTest() {
        try (Connection connection = getSql2o().open()) {
            deleteTableIfExist("country", connection);
            deleteTableIfExist("country2", connection);
            deleteTableIfExist("emptytable", connection);
            deleteTableIfExist("notemptytable", connection);
            deleteTableIfExist("timetest", connection);
        } catch (ClassNotFoundException e) {
        }
    }
}
