package com.manywho.services.sql.suites.postgresql.data;

import com.manywho.services.sql.DbConfigurationTest;
import com.manywho.services.sql.ServiceFunctionalTest;
import com.manywho.services.sql.utilities.DefaultApiRequest;
import org.junit.After;
import org.junit.Test;
import org.sql2o.Connection;

public class LoadTest extends ServiceFunctionalTest {
    @Test
    public void testLoadDataByEqualOrLikeFilterWithAlias() throws Exception {
        DbConfigurationTest.setPropertiesIfNotInitialized("postgresql");
        try (Connection connection = getSql2o().open()) {
            String sqlCreateTable = "CREATE TABLE " + escapeTableName("country") + "("+
                    "id integer NOT NULL,"+
                    "name character varying(255)," +
                    "description character varying(1024)," +
                    "CONSTRAINT country_id_pk PRIMARY KEY (id)" +
                    ");";

            connection.createQuery(sqlCreateTable).executeUpdate();

            String aliasName = "COMMENT ON COLUMN " + escapeTableName("country") + ".name IS '{{ManyWhoName:The Name}}';";
            connection.createQuery(aliasName).executeUpdate();

            String aliasDescription = "COMMENT ON COLUMN " + escapeTableName("country") + ".description IS '{{ManyWhoName:The Description}}';";
            connection.createQuery(aliasDescription).executeUpdate();

            String sql = "INSERT INTO " + escapeTableName("country") + "(id, name, description) VALUES " +
                    "('1', 'Uruguay', 'It is a nice country')," +
                    "('2', 'England', 'It is a beautiful country');";

            connection.createQuery(sql).executeUpdate();
        }

        DefaultApiRequest.loadDataRequestAndAssertion("/data",
                "suites/common/data/load/by-filter/equal-or-like-with-alias/load-request.json",
                configurationParameters(),
                "suites/common/data/load/by-filter/equal-or-like-with-alias/load-response.json",
                dispatcher
        );
    }

    @Test
    public void testLoadDataByExternalIdWithFilters() throws Exception {
        DbConfigurationTest.setPropertiesIfNotInitialized("postgresql");
        try (Connection connection = getSql2o().open()) {
            String sqlCreateTable = "CREATE TABLE " + escapeTableName("country") + "("+
                    "id integer NOT NULL,"+
                    "name character varying(255)," +
                    "description character varying(1024)," +
                    "CONSTRAINT country_id_pk PRIMARY KEY (id)" +
                    ");";
            connection.createQuery(sqlCreateTable).executeUpdate();

            String sql = "INSERT INTO " + escapeTableName("country")+"(id, name, description) VALUES ('1', 'Uruguay', 'It is a nice country');";
            connection.createQuery(sql).executeUpdate();

            String aliasName = "COMMENT ON COLUMN " + escapeTableName("country") + ".id IS '{{ManyWhoName:The ID}}';";
            connection.createQuery(aliasName).executeUpdate();
        }

        DefaultApiRequest.loadDataRequestAndAssertion("/data",
                "suites/common/data/load/by-external-id-with-alias/load-request.json",
                configurationParameters(),
                "suites/common/data/load/by-external-id-with-alias/load-response.json",
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
