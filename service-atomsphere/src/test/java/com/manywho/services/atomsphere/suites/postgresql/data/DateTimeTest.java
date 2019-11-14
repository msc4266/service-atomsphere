package com.manywho.services.sql.suites.postgresql.data;
import com.manywho.services.sql.ServiceFunctionalTest;
import com.manywho.services.sql.DbConfigurationTest;
import com.manywho.services.sql.utilities.DefaultApiRequest;
import org.json.JSONException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sql2o.Connection;

import java.io.IOException;
import java.net.URISyntaxException;

public class DateTimeTest extends ServiceFunctionalTest {
    @Before
    public void setupDatabase() throws Exception {
        DbConfigurationTest.setPropertiesIfNotInitialized("postgresql");
    }

    @Test
    public void testLoadDatesAndTimes() throws ClassNotFoundException, JSONException, IOException, URISyntaxException {
        try (Connection connection = getSql2o().open()) {
            String sqlCreate = "CREATE TABLE "+ escapeTableName("timetest") +
                    "(" +
                    "id integer NOT NULL," +
                    "time_with_timezone time," +
                    "time_without_timezone time," +
                    "timestamp_with_timezone timestamp with time zone," +
                    "timestamp_without_timezone timestamp without time zone," +
                    "CONSTRAINT timetest_id_pk PRIMARY KEY (id)" +
                    ");";
            connection.createQuery(sqlCreate).executeUpdate();

            String sql = "INSERT INTO " + escapeTableName("timetest") + "(id, time_with_timezone, time_without_timezone, timestamp_with_timezone, timestamp_without_timezone) VALUES " +
                                                     "('1', '2012-05-24 14:09:08 +02:00', '2013-06-25 15:10:09 +02:00', '2014-07-26 14:00:00 +02:00', '2014-07-26 14:00:00');";

            connection.createQuery(sql).executeUpdate();
        }

        DefaultApiRequest.loadDataRequestAndAssertion("/data",
                "suites/postgresql/dates/load/request-dates.json",
                configurationParameters(),
                "suites/postgresql/dates/load/response-dates.json",
                dispatcher
        );
    }

    @Test
    public void testUpdateDatesAndTimes() throws ClassNotFoundException, JSONException, IOException, URISyntaxException {
        try (Connection connection = getSql2o().open()) {
            String sqlCreate = "CREATE TABLE "+ escapeTableName("timetest") +
                    "(" +
                    "id integer NOT NULL," +
                    "time_with_timezone time with time zone," +
                    "time_without_timezone time without time zone," +
                    "timestamp_with_timezone timestamp with time zone," +
                    "timestamp_without_timezone timestamp without time zone," +
                    "CONSTRAINT timetest_id_pk PRIMARY KEY (id)" +
                    ");";
            connection.createQuery(sqlCreate).executeUpdate();

            String sql = "INSERT INTO " + escapeTableName("timetest") +"(id, time_with_timezone, time_without_timezone, timestamp_with_timezone, timestamp_without_timezone) VALUES " +
                    "('1', '2012-05-24 14:09:08 +02:00', '2013-06-25 15:10:09 +02:00', '2014-07-26 14:00:00 +02:00', '2014-07-26 14:00:00');";

            connection.createQuery(sql).executeUpdate();
        }

        DefaultApiRequest.saveDataRequestAndAssertion("/data",
                "suites/postgresql/dates/save/request-dates.json",
                configurationParameters(),
                "suites/postgresql/dates/save/response-dates.json",
                dispatcher);
    }

    @After
    public void cleanDatabaseAfterEachTest() {
        try (Connection connection = getSql2o().open()) {
            deleteTableIfExist("timetest", connection);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
