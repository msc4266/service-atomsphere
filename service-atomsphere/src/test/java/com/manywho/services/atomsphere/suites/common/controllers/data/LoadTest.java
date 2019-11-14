package com.manywho.services.sql.suites.common.controllers.data;

import com.manywho.services.sql.DbConfigurationTest;
import com.manywho.services.sql.ServiceFunctionalTest;
import com.manywho.services.sql.utilities.DefaultApiRequest;
import org.junit.After;
import org.junit.Test;
import org.sql2o.Connection;

public class LoadTest extends ServiceFunctionalTest {

    private void setupTableCountryTable() throws Exception {
        DbConfigurationTest.setPropertiesIfNotInitialized("sqlserver");
        try (Connection connection = getSql2o().open()) {
            String sqlCreateTable = "CREATE TABLE " + escapeTableName("country") + "("+
                            "id integer NOT NULL,"+
                            "name character varying(255)," +
                            "description character varying(1024) null," +
                            "CONSTRAINT country_id_pk PRIMARY KEY (id)" +
                        ");";

            connection.createQuery(sqlCreateTable).executeUpdate();
        }
    }

    @Test
    public void testLoadIsEmptyData() throws Exception {
        setupTableCountryTable();

        try (Connection connection = getSql2o().open()) {
            String sql = "INSERT INTO "+ escapeTableName("country")+"(id, name, description) VALUES ('1', 'Uruguay', null);";
            connection.createQuery(sql).executeUpdate();

            String sql2 = "INSERT INTO "+ escapeTableName("country")+"(id, name, description) VALUES ('2', 'UK', 'UK description');";
            connection.createQuery(sql2).executeUpdate();
        }

        DefaultApiRequest.loadDataRequestAndAssertion("/data",
                "suites/common/data/load/is-empty/load-request.json",
                configurationParameters(),
                "suites/common/data/load/is-empty/load-response.json",
                dispatcher
        );
    }

    @Test
    public void testLoadIsNotEmptyData() throws Exception {
        setupTableCountryTable();

        try (Connection connection = getSql2o().open()) {
            String sql = "INSERT INTO "+ escapeTableName("country")+"(id, name, description) VALUES ('1', 'Uruguay', 'Uruguay description');";
            connection.createQuery(sql).executeUpdate();

            String sql2 = "INSERT INTO "+ escapeTableName("country")+"(id, name, description) VALUES ('2', 'UK', null);";
            connection.createQuery(sql2).executeUpdate();
        }

        DefaultApiRequest.loadDataRequestAndAssertion("/data",
                "suites/common/data/load/is-not-empty/load-request.json",
                configurationParameters(),
                "suites/common/data/load/is-not-empty/load-response.json",
                dispatcher
        );
    }


    @Test
    public void testLoadDataByExternalId() throws Exception {
        setupTableCountryTable();

        try (Connection connection = getSql2o().open()) {
            String sql = "INSERT INTO "+ escapeTableName("country")+"(id, name, description) VALUES ('1', 'Uruguay', 'It is a nice country');";
            connection.createQuery(sql).executeUpdate();
        }

        DefaultApiRequest.loadDataRequestAndAssertion("/data",
                "suites/common/data/load/by-external-id/load-request.json",
                configurationParameters(),
                "suites/common/data/load/by-external-id/load-response.json",
                dispatcher
        );
    }

    @Test
    public void testLoadDataByEqualAndLikeFilter() throws Exception {
        setupTableCountryTable();

        try (Connection connection = getSql2o().open()) {
            String sql = "INSERT INTO "+ escapeTableName("country")+"(id, name, description) VALUES " +
                    "('1', 'Uruguay', 'It is a nice country')," +
                    "('2', 'England', 'It is a beautiful country');";

            connection.createQuery(sql).executeUpdate();
        }

        DefaultApiRequest.loadDataRequestAndAssertion("/data",
                "suites/common/data/load/by-filter/equal-and-like/load-request.json",
                configurationParameters(),
                "suites/common/data/load/by-filter/equal-and-like/load-response.json",
                dispatcher
        );
    }

    @Test
    public void testLoadDataByEqualOrLikeFilter() throws Exception {
        setupTableCountryTable();

        try (Connection connection = getSql2o().open()) {
            String sql = "INSERT INTO " + escapeTableName("country") + "(id, name, description) VALUES " +
                    "('1', 'Uruguay', 'It is a nice country')," +
                    "('2', 'England', 'It is a beautiful country');";

            connection.createQuery(sql).executeUpdate();
        }

        DefaultApiRequest.loadDataRequestAndAssertion("/data",
                "suites/common/data/load/by-filter/equal-or-like/load-request.json",
                configurationParameters(),
                "suites/common/data/load/by-filter/equal-or-like/load-response.json",
                dispatcher
        );
    }

    @Test
    public void testLoadDataByFilterWithOffsetAndLimit() throws Exception {
        setupTableCountryTable();

        try (Connection connection = getSql2o().open()) {
            String sql = "INSERT INTO " + escapeTableName("country") + "(id, name, description) VALUES " +
                    "('1', 'Uruguay', 'Uruguay description')," +
                    "('2', 'England', 'England description')," +
                    "('3', 'Spain', 'Spain description')," +
                    "('4', 'Italy', 'Italy description');";

            connection.createQuery(sql).executeUpdate();
        }

        DefaultApiRequest.loadDataRequestAndAssertion("/data",
                "suites/common/data/load/by-filter/offset-and-limit/load-offset-request.json",
                configurationParameters(),
                "suites/common/data/load/by-filter/offset-and-limit/load-offset-response.json",
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
