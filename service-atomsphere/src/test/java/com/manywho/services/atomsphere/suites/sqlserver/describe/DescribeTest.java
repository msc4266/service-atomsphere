package com.manywho.services.sql.suites.sqlserver.describe;

import com.manywho.services.sql.DbConfigurationTest;
import com.manywho.services.sql.ServiceFunctionalTest;
import com.manywho.services.sql.utilities.DefaultApiRequest;
import org.junit.After;
import org.junit.Test;
import org.sql2o.Connection;

public class DescribeTest extends ServiceFunctionalTest {

    @Test
    public void testDescribeWithTypes() throws Exception {
        DbConfigurationTest.setPropertiesIfNotInitialized("sqlserver");
        String sql = "CREATE TABLE " + escapeTableName("country") + "(" +
                "id integer NOT NULL," +
                "name character varying(255)," +
                "description character varying(1024)," +
                "available bit," +
                "CONSTRAINT country_id_pk PRIMARY KEY (id)" +
                ");";

        try (Connection connection = getSql2o().open()) {
            connection.createQuery(sql).executeUpdate();
        }

        DefaultApiRequest.describeServiceRequestAndAssertion("/metadata",
                "suites/sqlserver/describe/with-types/metadata1-request.json",
                configurationParameters(),
                "suites/sqlserver/describe/with-types/metadata1-response.json",
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
