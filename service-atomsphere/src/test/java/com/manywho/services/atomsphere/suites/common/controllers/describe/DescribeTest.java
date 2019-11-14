package com.manywho.services.sql.suites.common.controllers.describe;

import com.manywho.services.sql.DbConfigurationTest;
import com.manywho.services.sql.ServiceFunctionalTest;
import com.manywho.services.sql.utilities.DefaultApiRequest;
import org.junit.After;
import org.junit.Test;
import org.sql2o.Connection;

public class DescribeTest extends ServiceFunctionalTest {

    @Test
    public void testDescribe() throws Exception {
        DbConfigurationTest.setPropertiesIfNotInitialized("postgresql");
        String sql = "CREATE TABLE " + escapeTableName("country") + "(" +
                        "id integer NOT NULL," +
                        "name character varying(255)," +
                        "description character varying(1024)," +
                        "CONSTRAINT country_id_pk PRIMARY KEY (id)" +
                    ");";

        try (Connection connection = getSql2o().open()) {
            connection.createQuery(sql).executeUpdate();
        }

        DefaultApiRequest.describeServiceRequestAndAssertion("/metadata",
                "suites/common/describe/without-types/metadata1-request.json",
                configurationParameters(),
                "suites/common/describe/without-types/metadata1-response.json",
                dispatcher
        );
    }

    @Test
    public void testDescribeWithTypes() throws Exception {
        DbConfigurationTest.setPropertiesIfNotInitialized("postgresql");
        String sql = "CREATE TABLE " + escapeTableName("country2") + "(" +
                "id integer NOT NULL," +
                "name character varying(255)," +
                "description character varying(1024)," +
                "CONSTRAINT country_id_pk PRIMARY KEY (id)" +
                ");";

        try (Connection connection = getSql2o().open()) {
            connection.createQuery(sql).executeUpdate();
        }

        DefaultApiRequest.describeServiceRequestAndAssertion("/metadata",
                "suites/common/describe/with-types/metadata1-request.json",
                configurationParameters(),
                "suites/common/describe/with-types/metadata1-response.json",
                dispatcher
        );
    }

    @Test(expected = Exception.class)
    public void testDescribeWithTypesButNotPort() throws Exception {
        DbConfigurationTest.setPropertiesIfNotInitialized("postgresql");
        String sql = "CREATE TABLE " + escapeTableName("country") + "(" +
                "id integer NOT NULL," +
                "name character varying(255)," +
                "description character varying(1024)," +
                "CONSTRAINT country_id_pk PRIMARY KEY (id)" +
                ");";

        try (Connection connection = getSql2o().open()) {
            connection.createQuery(sql).executeUpdate();
        }

        DefaultApiRequest.describeServiceRequestAndAssertion("/metadata",
                "suites/common/describe/with-types-no-password/metadata1-request.json",
                configurationParameters(),
                "suites/common/describe/with-types-no-password/metadata1-response.json",
                dispatcher
        );
    }

    @Test
    public void testIgnoringTimeBecauseIsNotSupportedType() throws Exception {
        DbConfigurationTest.setPropertiesIfNotInitialized("postgresql");
        String sql = "CREATE TABLE " + escapeTableName("timetest") + "(" +
                "id integer NOT NULL," +
                "time time," +
                "description character varying(255)," +
                "CONSTRAINT timetest_id_pk PRIMARY KEY (id)" +
                ");";

        try (Connection connection = getSql2o().open()) {
            connection.createQuery(sql).executeUpdate();

            String sql1 = "INSERT INTO "+ escapeTableName("timetest")+"(id, time, description) VALUES " +
                    "('1', '14:09:08', 'time description');";

            connection.createQuery(sql1).executeUpdate();
        }

        DefaultApiRequest.describeServiceRequestAndAssertion("/metadata",
                "suites/common/describe/not-supported-types/metadata1-request.json",
                configurationParameters(),
                "suites/common/describe/not-supported-types/metadata1-response.json",
                dispatcher
        );
    }

    @After
    public void cleanDatabaseAfterEachTest() {
        try (Connection connection = getSql2o().open()) {
            deleteTableIfExist("country", connection);
        } catch (ClassNotFoundException e) {
        }

        try (Connection connection = getSql2o().open()) {
            deleteTableIfExist("country2", connection);
        } catch (ClassNotFoundException e) {
        }

        try (Connection connection = getSql2o().open()) {
            deleteTableIfExist("timetest", connection);
        } catch (ClassNotFoundException e) {
        }
    }
}
